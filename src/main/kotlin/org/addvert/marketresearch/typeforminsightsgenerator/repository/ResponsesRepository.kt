package org.addvert.marketresearch.typeforminsightsgenerator.repository

import io.micronaut.context.annotation.Primary
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.AnswerNode
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.QuestionRelationship
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.RespondentNode
import org.apache.logging.log4j.kotlin.Logging
import org.neo4j.driver.Driver
import java.net.URLDecoder
import java.net.URLEncoder


@Primary
@Singleton
class ResponsesRepository : IResponsesRepository, Logging {

    @Inject
    private var driver: Driver? = null

    private var inMemoryQueryList = mutableListOf<String>()


    override fun nodeStatement(respondentNode: RespondentNode): String {
        return "MERGE (r:Respondent { id: \"${respondentNode.id}\" }) "

    }

    override fun nodeStatement(node: AnswerNode): String {

        return when (node.label) {
            is String -> "MERGE (a:Answer { label: ${
                URLDecoder.decode(
                    URLEncoder.encode(
                        node.label.lowercase(),
                        "utf-8"
                    ), "utf-8"
                )
            } })"
            else -> "MERGE (a:Answer { label: ${node.label} })"
        }

    }

    override fun relationshipStatement(questionRelationship: QuestionRelationship): String {
        val relationshipString = questionRelationship.question
            .replace("\\p{P}".toRegex(), "")
            .replace(" ", "_")
            .uppercase()
        return "MERGE (r)-[:$relationshipString { id: ${questionRelationship.id}," +
                " type: ${questionRelationship.type} } ]->(a) "

    }

    override fun createPropertyGraph(fullStatement: String) {
        inMemoryQueryList.add(fullStatement)
    }

    private fun runCypherQuery(statement: String) {
        driver?.session().use { s ->
            s?.writeTransaction { tx -> tx.run(statement) }
        }
    }

    override fun commitBatchInMemoryCypherQuery() {
        driver?.session()?.beginTransaction().use { tx ->
            inMemoryQueryList.forEach { tx?.run(it) }
            tx?.commit()
        }
        inMemoryQueryList.clear()

    }


}