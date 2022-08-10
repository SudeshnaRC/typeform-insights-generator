package org.addvert.marketresearch.typeforminsightsgenerator.handler


import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.github.michaelbull.result.*
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import org.addvert.marketresearch.typeforminsightsgenerator.client.IFormClient
import org.addvert.marketresearch.typeforminsightsgenerator.client.IResponsesClient
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration
import org.addvert.marketresearch.typeforminsightsgenerator.controller.TypeformController
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.*
import org.addvert.marketresearch.typeforminsightsgenerator.handler.exceptions.InvalidResponseIDException
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.PartialForm
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.AnswerType
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses.Item.Answer
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses.Item.Answer.Field
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses.Item.Answer.Choice
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IFormRepository
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IResponsesRepository
import org.apache.logging.log4j.kotlin.Logging

@Singleton
class FormHandler(
    private val responsesClient: IResponsesClient,
    private val formClient: IFormClient,
    private val responsesRepository: IResponsesRepository,
    private val formRepository: IFormRepository,
    @Value("\${typeform.token}") private var accessToken: String
) : IFormHandler, Logging {

    override fun persistFormIfFound(formId: String): Result<List<FormEntity>, ErrorMessage> {
        return getFormByFormId(formId)
            .andThen(::persistForm)
    }

    override fun persistResponsesIfFound(formId: String): Result<Unit, ErrorMessage> {
        var before: String? = null
        var pageCount: Int = Int.MAX_VALUE

        return binding {
            while (pageCount > 1) {
                getResonsesByFormId(formId, before).andThen { responses ->
                    before = responses.items.last().responseId
                    pageCount = responses.pageCount
                    persistFormResponses(formId, responses)
                }
            }
        }
    }

    private fun getFormByFormId(formId: String): Result<Form, ErrorMessage> {
        return try {
            val form = Klaxon().parse<Form>(
                formClient.fetchForm(
                    "Bearer $accessToken",
                    formId
                ) ?: TypeformController.emptyJsonString
            )
            if (form != null) {
                Ok(form)
            } else {
                Err(FormNotFound)
            }
        } catch (ex: KlaxonException) {
            Err(UnableToParseForm)
        }
    }

    private fun getResonsesByFormId(formId: String, before: String?): Result<Responses, ErrorMessage> {
        return try {
            val responses = Klaxon().parse<Responses>(
                responsesClient.fetchResponses(
                    "Bearer $accessToken",
                    formId,
                    TypeformController.pageSize,
                    before
                ) ?: TypeformController.emptyJsonString
            )

            if (responses != null) {
                Ok(responses)
            } else {
                Err(UnableToParseResponses)
            }
        } catch (ex: KlaxonException) {
            Err(ResponsesNotFound)
        }
    }

    private fun persistFormResponses(formId: String, responses: Responses): Result<Unit, ErrorMessage> {
        return binding {
            responses.items.forEach { item ->
                val respondentNode =
                    addRespondentNodeToInMemoryCypherQuery(item.responseId)

                item.answers.forEach { answer ->

                    //TODO: Replace all generic exceptions in this class with custom exceptions
                    val questionId = answer.field.ref

                    if (answer.type != AnswerType.CHOICES.name.lowercase()) {
                        val answerNode = addAnswerNodeToInMemoryCypherQuery(answer)
                        val relationship = addRelationshipToInMemoryCypherQuery(formId, questionId)
                        val fullStatement = respondentNode + answerNode + relationship
                        responsesRepository.createPropertyGraph(fullStatement)

                    } else {
                        answer.choices?.let { choices ->
                            choices.ids.indices.forEach {
                                val simpleAnswer = Answer(
                                    field = Field(
                                        ref = questionId
                                    ),
                                    type = AnswerType.CHOICE.name.lowercase(),
                                    choice = Choice(
                                        label = choices.labels[it]
                                    )
                                )

                                val answerNode = addAnswerNodeToInMemoryCypherQuery(simpleAnswer)
                                val relationship = addRelationshipToInMemoryCypherQuery(formId, questionId)
                                val fullStatement = respondentNode + answerNode + relationship
                                responsesRepository.createPropertyGraph(fullStatement)
                            }

                        }

                    }
                }
            }
            responsesRepository.commitBatchInMemoryCypherQuery()
        }
    }

    private fun persistForm(form: Form): Result<List<FormEntity>, ErrorMessage> {
        return binding {
            val savedFormEntities = mutableListOf<FormEntity>()
            form.fields.forEach { question ->
                savedFormEntities.add(formRepository.update(
                    FormEntity(
                        formQuestion = FormQuestion(
                            form.id,
                            question.ref
                        ),
                        form = PartialForm(form.id),
                        questionTitle = question.title,
                        questionType = question.type
                    )
                ))
            }
            savedFormEntities
        }
    }


    private fun addRespondentNodeToInMemoryCypherQuery(responseId: String): String {
        return responsesRepository
            .nodeStatement(RespondentNode(responseId))
    }

    private fun addRelationshipToInMemoryCypherQuery(formId: String, questionId: String): String {
        val formEntity = formRepository
            .findByFormQuestion(FormQuestion(formId, questionId))
            .get()
        val questionString = formEntity.questionTitle
        val questionType = formEntity.questionType

        return responsesRepository
            .relationshipStatement(QuestionRelationship(questionString, questionId, questionType))

    }

    private fun addAnswerNodeToInMemoryCypherQuery(answer: Answer): String {

        return when (answer.type) {
            AnswerType.TEXT.name.lowercase() -> {
                answer.text?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.EMAIL.name.lowercase() -> {
                answer.email?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.URL.name.lowercase() -> {
                answer.url?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.FILEURL.name.lowercase() -> {
                answer.fileUrl?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.BOOLEAN.name.lowercase() -> {
                answer.boolean?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.NUMBER.name.lowercase() -> {
                answer.number?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.DATE.name.lowercase() -> {
                answer.date?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            AnswerType.PAYMENT.name.lowercase() -> {
                answer.payment?.let { responsesRepository.nodeStatement(AnswerNode(it)) }
            }
            else -> {
                answer.choice?.let {
                    it.label.let { label -> responsesRepository.nodeStatement(AnswerNode(label)) }
                }
            }
        } ?: throw Exception()
    }

}