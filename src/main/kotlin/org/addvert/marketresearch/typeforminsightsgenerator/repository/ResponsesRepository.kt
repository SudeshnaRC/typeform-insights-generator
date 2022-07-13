package org.addvert.marketresearch.typeforminsightsgenerator.repository

import io.micronaut.context.annotation.Primary
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.*
import org.neo4j.driver.Driver
import javax.inject.Inject
import javax.inject.Singleton

@Primary
@Singleton
class ResponsesRepository : IResponsesRepository {
    @Inject
    var driver: Driver? = null

    override fun nodeStatement(respondentNode: RespondentNode) : String {
       return "CREATE (respondentNode:RespondentNode { id: '${respondentNode.id}' })"
    }

    override fun nodeStatement(textNode: TextNode): String {
        return "CREATE (textNode: TextNode { value: '${textNode.value}' })"
    }
    override fun nodeStatement(booleanNode: BooleanNode): String {
        return "CREATE (booleanNode: BooleanNode { value: '${booleanNode.value}' })"
    }

    override fun nodeStatement(choiceNode: ChoiceNode): String {
        return "CREATE (choiceNode: ChoiceNode { id: '${choiceNode.id}'," +
                " ref: '${choiceNode.ref}', label: '${choiceNode.label}'})"
    }

    override fun relationshipStatement(relationship: Relationship): String {
        val relationshipString = relationship.question
            .replace("\\p{Punct}".toRegex(), "")
            .replace(" ", "_")
            .toUpperCase()
        return "-[:$relationshipString {id: ['${relationship.id}']}]->"
    }

    override fun createPropertyGraph(fullStatement: String){
        driver?.session().use { s ->
            s?.writeTransaction { tx -> tx.run(fullStatement) }
        }
    }
}