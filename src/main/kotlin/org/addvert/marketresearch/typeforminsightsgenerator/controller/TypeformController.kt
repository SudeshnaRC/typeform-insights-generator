package org.addvert.marketresearch.typeforminsightsgenerator.controller

import com.github.michaelbull.result.mapBoth
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler.Companion.buildErrorResponse
import org.apache.logging.log4j.kotlin.Logging


@Controller("/typeform")
class TypeformController(
    private val formHandler: IFormHandler
) : Logging {
    //TODO: Definitely not the most performant implementation: See https://medium.com/neo4j/5-tips-tricks-for-fast-batched-updates-of-graph-structures-with-neo4j-and-cypher-73c7f693c8cc

    companion object {
        const val emptyJsonString = """{}"""
        const val pageSize = 1000
    }

    //TODO: Should potentially be in a different controller, not technically insights
    @Put("/{formId}")
    fun putFormData(formId: String): HttpResponse<Any> {

        return try {
            //TODO: Check if form has previously been persisted
            formHandler.persistFormIfFound(formId).mapBoth({
                HttpResponse.ok()
            }, { errorMessage ->
                buildErrorResponse(errorMessage)
            })

        } catch (e: Exception) {
            logger.error("Exception thrown while attempting to persist Typeform [$formId] to database", e)
            HttpResponse.serverError()
        }
    }

    @Put("/{formId}/responses")
    fun putResponsesData(formId: String): HttpResponse<Any> {

        return try {
            //TODO: Check if response has been previously been persisted
            formHandler.persistResponsesIfFound(formId).mapBoth({
                HttpResponse.ok()
            }, { errorMessage
                ->
                buildErrorResponse(errorMessage)
            })

        } catch (e: Exception) {
            logger.error("Exception thrown while attempting to persist responses for Typeform [$formId] to database", e)
            HttpResponse.serverError()
        }
    }

}