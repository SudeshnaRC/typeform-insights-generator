package org.addvert.marketresearch.typeforminsightsgenerator.repository

import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*

interface IResponsesRepository {
    fun nodeStatement(respondentNode: RespondentNode) : String
    fun nodeStatement(textNode: TextNode): String
    fun nodeStatement(booleanNode: BooleanNode): String
    fun nodeStatement(choiceNode: ChoiceNode): String
    fun relationshipStatement(relationship: Relationship): String
    fun createPropertyGraph(fullStatement: String)
}