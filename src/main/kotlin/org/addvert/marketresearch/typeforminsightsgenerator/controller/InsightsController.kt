package org.addvert.marketresearch.typeforminsightsgenerator.controller


import com.beust.klaxon.Klaxon
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import org.addvert.marketresearch.typeforminsightsgenerator.client.IFormClient
import org.addvert.marketresearch.typeforminsightsgenerator.client.IResponsesClient
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import org.apache.logging.log4j.kotlin.Logging
import com.github.michaelbull.result.*
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.ErrorMessage
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.UnableToParseForm
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.UnableToParseResponses


@Controller("/insights")
class InsightsController(
    private val responsesClient: IResponsesClient,
    private val formClient: IFormClient,
    private val formHandler: IFormHandler,
    private val configuration: TypeformConfiguration
) : Logging {
    //TODO: Definitely not the most performant implementation: See https://medium.com/neo4j/5-tips-tricks-for-fast-batched-updates-of-graph-structures-with-neo4j-and-cypher-73c7f693c8cc

    companion object {
        const val emptyJsonString = """{}"""
        const val pageSize = 1000

        fun buildErrorResponse(message: ErrorMessage): HttpResponse<Any> {
            return when (message) {
                UnableToParseForm, UnableToParseResponses -> HttpResponse.badRequest()
            }
        }
    }

    //    @Delete("/{formId}")
//    fun deleteFormData(@Parameter formId: String): HttpResponse<String>{
//        //TODO: until persistance checks have been implemented,
//        // deletes need to occur prior to every put
//        return HttpResponse.ok()
//    }
    //TODO: Should potentially be in a different controller, not technically insights
    @Put("/{formId}")
    fun putFormData(@Parameter formId: String): HttpResponse<Any> {

        return try {
            //TODO: find out how to return form == null from Klaxon
            getFormByFormId(formId).mapBoth(
                { form -> formHandler.persistForm(form) },
                { errorMessage -> return buildErrorResponse(errorMessage) }
            )
            var before: String? = null
            var pageCount: Int = Int.MAX_VALUE

            //TODO: Check if form has previously been persisted
            //TODO: Check if response has been previously been persisted
            while (pageCount > 1) {
                getResonsesByFormId(formId, pageSize, before).mapBoth(
                    { responses ->
                        formHandler.persistFormResponses(formId, responses)
                        before = responses.items.last().responseId
                        pageCount = responses.pageCount
                    },
                    { errorMessage -> return buildErrorResponse(errorMessage) }
                )
            }

            HttpResponse.created("Form data items for [$formId] has been successfully persisted to database.")

        } catch (e: Exception) {
            logger.error("some error", e)
            HttpResponse.serverError()
        }
    }

    private fun getResonsesByFormId(formId: String, pageSize: Int?, before: String?): Result<Responses, ErrorMessage> {
        val responses = Klaxon().parse<Responses>(
            responsesClient.fetchResponses(
                "Bearer ${configuration.token}",
                formId,
                pageSize,
                before
            ) ?: emptyJsonString
        )

        return if (responses != null) {
            Ok(responses)
        } else {
            Err(UnableToParseResponses)
        }
    }

    private fun getFormByFormId(formId: String): Result<Form, ErrorMessage> {
        val form = Klaxon().parse<Form>(
            formClient.fetchForm(
                "Bearer ${configuration.token}",
                formId
            ) ?: emptyJsonString
        )
        return if (form != null) {
            Ok(form)
        } else {
            Err(UnableToParseForm)
        }
    }
//
//    @Get("/{formId}")
//    fun getAllFormInsights(@Parameter formId: String): HttpResponse<String>{
//
//    }

}