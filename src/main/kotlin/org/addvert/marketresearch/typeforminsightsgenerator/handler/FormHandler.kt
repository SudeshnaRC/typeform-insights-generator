package org.addvert.marketresearch.typeforminsightsgenerator.handler


import jakarta.inject.Singleton
import org.addvert.marketresearch.typeforminsightsgenerator.handler.exceptions.InvalidFormFieldRefException
import org.addvert.marketresearch.typeforminsightsgenerator.handler.exceptions.InvalidResponseIDException
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.PartialForm
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.AnswerType
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses.Item.Answer
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses.Item.Answer.Choice
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IFormRepository
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IResponsesRepository
import org.apache.logging.log4j.kotlin.Logging

@Singleton
class FormHandler(
    private val responsesRepository: IResponsesRepository,
    private val formRepository: IFormRepository
) : IFormHandler, Logging {

    override fun persistFormResponses(formId: String, responses: Responses) {
        responses.items?.forEach { item ->
            val respondentNode =
                addRespondentNodeToInMemoryCypherQuery(item.responseId ?: throw InvalidResponseIDException(""))

            item.answers?.forEach { answer ->

                //TODO: Replace all generic exceptions in this class with custom exceptions
                val questionId = answer.field?.ref ?: throw InvalidFormFieldRefException("")

                if (answer.type != AnswerType.CHOICES.name.lowercase()) {
                    val answerNode = addAnswerNodeToInMemoryCypherQuery(answer)
                    val relationship = addRelationshipToInMemoryCypherQuery(formId, questionId)
                    val fullStatement = respondentNode + answerNode + relationship
                    responsesRepository.createPropertyGraph(fullStatement)

                } else {
                    answer.choices?.let { choices ->
                        choices.ids?.indices?.forEach {
                            val simpleAnswer = Answer(
                                type = AnswerType.CHOICE.name.lowercase(),
                                choice = Choice(
                                    label = choices.labels?.get(it)
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

    override fun persistForm(form: Form) {
        form.fields?.forEach { question ->
            formRepository.update(
                FormEntity(
                    formQuestion = FormQuestion(
                        form.id ?: throw Exception(),
                        question.ref ?: throw InvalidFormFieldRefException(""),
                    ),
                    form = PartialForm(form.id),
                    questionTitle = question.title ?: throw Exception(),
                    questionType = question.type ?: throw Exception()
                )
            )
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
                    it.label?.let { label -> responsesRepository.nodeStatement(AnswerNode(label)) }
                        ?: responsesRepository.nodeStatement(AnswerNode("other"))
                }
            }
        } ?: throw Exception()
    }


//    fun retrieveAllInsightsByFormId(formId: String){
//        val formEntityList = formRepository.findAllByForm(PartialForm(formId))
//
//
//    }
}