package org.addvert.marketresearch.typeforminsightsgenerator.repository

import io.micronaut.context.annotation.Primary
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*
import org.neo4j.driver.Driver


@Primary
@Singleton
class ResponsesRepository : IResponsesRepository {
    companion object {
        const val merge = " MERGE "
        const val respondent = "(respondent)"
        const val answer = "(answer)"
    }

    @Inject
    var driver: Driver? = null

    override fun nodeStatement(respondentNode: RespondentNode): String {
        return merge + "(respondent:RespondentNode { id: \"${respondentNode.id}\" })"
    }

    override fun nodeStatement(textNode: TextNode): String {
        return merge + "(answer:TextNode { value: \"${textNode.value}\" })"
    }

    override fun nodeStatement(booleanNode: BooleanNode): String {
        return merge + "(answer:BooleanNode { value: ${booleanNode.value} })"

    }

    override fun nodeStatement(numberNode: NumberNode): String {
        return merge + "(answer:NumberNode { value: ${numberNode.value} })"
    }

    override fun nodeStatement(choiceNode: ChoiceNode): String {
        return merge + "(answer:ChoiceNode { id: \"${choiceNode.id}\"," +
                " ref: \"${choiceNode.ref}\", label: \"${choiceNode.label}\"})"
    }

    override fun relationshipStatement(relationship: Relationship): String {
        val relationshipString = relationship.question
            .replace("\\p{P}".toRegex(), "")
            .replace(" ", "_")
            .uppercase()
        return "-[:$relationshipString {id:'${relationship.id}'}]->"
    }

    override fun createPropertyGraph(mergeNodes: String, relationship: String) {
        val fullStatement = mergeNodes + merge + respondent + relationship + answer
        commitCypher(fullStatement)
    }

    private fun commitCypher(statement: String) {
        driver?.session().use { s ->
            s?.writeTransaction { tx -> tx.run(statement) }
        }
    }
}