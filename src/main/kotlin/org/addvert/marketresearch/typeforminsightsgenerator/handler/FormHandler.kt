package org.addvert.marketresearch.typeforminsightsgenerator.handler


import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.AnswerType
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses.Item.Answer
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IQuestionsRepository
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IResponsesRepository

class FormHandler(
    private val responsesRepository: IResponsesRepository,
    private val questionsRepository: IQuestionsRepository
) : IFormHandler {
    override fun persistFormResponses(formId: String, responses: Responses) {
        responses.items?.forEach { item ->
            item.answers?.forEach { answer ->


                //Lots of duplication in this, I don't like it at all.

                    val questionId = answer.field.ref
                    val questionString = questionsRepository
                        .findById(FormQuestion(formId, questionId))
                        .get()
                        .questionTitle
                    val questionRelationshipStatement = responsesRepository
                        .relationshipStatement(Relationship(questionString, questionId))
                    val respondentNodeStatement = responsesRepository
                        .nodeStatement(RespondentNode(item.responseId))
                    var answerNodeStatement: String

                if (answer.type != AnswerType.CHOICES.name) {
                    answerNodeStatement = mapToAnswerStatement(answer)

                } else{
                    answer.choices?.let { choices ->
                        choices.forEach {
                            val simpleAnswer = SimpleAnswer(type = AnswerType.CHOICE.name, choice = it)
                            choiceStatement(ChoiceNode(
                                it.id,
                                it.ref,
                                it.label
                            ))
                        }
                        answerNodeStatement = mapToSimpleAnswerStatement(simpleAnswer)
                    }

                }
                val fullStatement = respondentNodeStatement + questionRelationshipStatement + answerNodeStatement
                responsesRepository.createPropertyGraph(fullStatement)
            }
        }
    }

    override fun persistFormQuestions(form: Form) {
        form.fields.forEach { question ->
            questionsRepository.update(
                FormEntity(
                    FormQuestion(
                        form.id, question.id
                    ), question.title
                )
            )
        }
    }

    private fun mapToAnswerStatement(answer: Answer): String? {

        return when (answer.type) {
            AnswerType.TEXT.name -> answer.text?.let { textStatement(TextNode(it)) }
            AnswerType.CHOICE.name -> answer.choice?.let { choiceStatement(ChoiceNode(it.id, it.ref, it.label)) }
            AnswerType.BOOLEAN.name -> answer.boolean?.let { booleanStatement(BooleanNode(it)) }
            else -> null
        }

    }

    private fun textStatement(textNode: TextNode): String{
        return responsesRepository.nodeStatement(textNode)
    }
    private fun choiceStatement(choiceNode: ChoiceNode): String{
        return responsesRepository.nodeStatement(choiceNode)
    }
    private fun booleanStatement(booleanNode: BooleanNode): String{
        return responsesRepository.nodeStatement(booleanNode)
    }
}