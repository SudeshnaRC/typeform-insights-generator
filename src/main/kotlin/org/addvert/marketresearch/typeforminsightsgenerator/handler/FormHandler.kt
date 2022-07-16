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

            item.answers?.forEach { answer ->
                //Lots of duplication in this, I don't like it at all.
                logger.info(answer)
                //TODO: Replace all generic exceptions in this class with custom exceptions
                val questionId = answer.field?.ref ?: throw InvalidFormFieldRefException("")

                val questionString = formRepository
                    .findByFormQuestion(FormQuestion(formId, questionId))
                    .get()
                    .questionTitle

                val questionRelationshipStatement = responsesRepository
                    .relationshipStatement(Relationship(questionString, questionId))

                val respondentNodeStatement = responsesRepository
                    .nodeStatement(RespondentNode(item.responseId ?: throw InvalidResponseIDException("")))

                if (answer.type != AnswerType.CHOICES.name.lowercase()) {
                    val answerNodeStatement = mapToAnswerStatement(answer)
                    val mergeNodes = respondentNodeStatement + answerNodeStatement
                    responsesRepository.createPropertyGraph(
                        mergeNodes,
                        questionRelationshipStatement
                    )

                } else {
                    answer.choices?.let { choices ->
                        val lastIndex = choices.ids?.lastIndex ?: throw Exception()
                        (0..lastIndex).forEach {
                            val simpleAnswer = Answer(
                                type = AnswerType.CHOICE.name.lowercase(),
                                choice = Choice(
                                    choices.ids[it],
                                    choices.refs?.get(it),
                                    choices.labels?.get(it)
                                )
                            )

                            val answerNodeStatement = mapToAnswerStatement(simpleAnswer)
                            val mergeNodes = respondentNodeStatement + answerNodeStatement
                            responsesRepository.createPropertyGraph(
                                mergeNodes,
                                questionRelationshipStatement
                            )
                        }

                    }

                }

            }
        }
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

    private fun mapToAnswerStatement(answer: Answer): String {

        return when (answer.type) {
            AnswerType.TEXT.name.lowercase() -> {
                answer.text?.let { textStatement(TextNode(it)) }
            }
            AnswerType.EMAIL.name.lowercase() -> {
                answer.email?.let { textStatement(TextNode(it)) }
            }
            AnswerType.URL.name.lowercase() -> {
                answer.url?.let { textStatement(TextNode(it)) }
            }
            AnswerType.FILEURL.name.lowercase() -> {
                answer.fileUrl?.let { textStatement(TextNode(it)) }
            }
            AnswerType.BOOLEAN.name.lowercase() -> {
                answer.boolean?.let { booleanStatement(BooleanNode(it)) }
            }
            AnswerType.NUMBER.name.lowercase() -> {
                answer.number?.let { numberStatement(NumberNode(it)) }
            }
            AnswerType.DATE.name.lowercase() -> {
                answer.date?.let { textStatement(TextNode(it)) }
            }
            AnswerType.PAYMENT.name.lowercase() -> {
                answer.payment?.let { textStatement(TextNode(it)) }
            }
            else -> {
                answer.choice?.let {
                    choiceStatement(
                        ChoiceNode(
                            it.id ?: throw Exception(),
                            it.ref,
                            it.label
                        )
                    )
                }
            }
        } ?: throw Exception()

    }

    private fun textStatement(textNode: TextNode): String {
        return responsesRepository.nodeStatement(textNode)
    }

    private fun choiceStatement(choiceNode: ChoiceNode): String {
        return responsesRepository.nodeStatement(choiceNode)
    }

    private fun booleanStatement(booleanNode: BooleanNode): String {
        return responsesRepository.nodeStatement(booleanNode)
    }

    private fun numberStatement(numberNode: NumberNode): String {
        return responsesRepository.nodeStatement(numberNode)
    }

    fun retrieveAllInsightsByFormId(formId: String){
        val formEntityList = formRepository.findAllByForm(PartialForm(formId))


    }
}