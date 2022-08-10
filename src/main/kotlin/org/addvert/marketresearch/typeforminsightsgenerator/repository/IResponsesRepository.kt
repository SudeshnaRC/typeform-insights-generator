package org.addvert.marketresearch.typeforminsightsgenerator.repository

import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*

interface IResponsesRepository {
    fun nodeStatement(respondentNode: RespondentNode): String
    fun nodeStatement(node: AnswerNode): String
    fun relationshipStatement(questionRelationship: QuestionRelationship): String
    fun createPropertyGraph(fullStatement: String)
    fun commitBatchInMemoryCypherQuery()


}