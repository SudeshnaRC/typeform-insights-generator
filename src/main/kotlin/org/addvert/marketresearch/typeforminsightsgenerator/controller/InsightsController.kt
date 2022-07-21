package org.addvert.marketresearch.typeforminsightsgenerator.controller


import com.beust.klaxon.Klaxon
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import org.addvert.marketresearch.typeforminsightsgenerator.client.IFormClient
import org.addvert.marketresearch.typeforminsightsgenerator.client.IResponsesClient
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import org.apache.logging.log4j.kotlin.Logging


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
    }

    //    @Delete("/{formId}")
//    fun deleteFormData(@Parameter formId: String): HttpResponse<String>{
//        //TODO: until persistance checks have been implemented,
//        // deletes need to occur prior to every put
//        return HttpResponse.ok()
//    }
    //TODO: Should potentially be in a different controller, not technically insights
    @Put("/{formId}")
    fun putFormData(@Parameter formId: String): HttpResponse<String> {

        return try {
            //TODO: find out how to return form == null from Klaxon
            val form = getFormByFormId(formId)
            var responses = getResonsesByFormId(formId, pageSize, null)

            //TODO: Check if form has previously been persisted
            //TODO: Check if response has been previously been persisted

            if (form?.id != null && responses?.items != null) {
                formHandler.persistForm(form)
                while (responses?.pageCount!! >= 1) {
                    formHandler.persistFormResponses(formId, responses)
                    val before = responses.items!!.last().responseId
                    responses = getResonsesByFormId(formId, pageSize, before)
                }

            } else {
                return HttpResponse.badRequest("Form [$formId] does not contain necessary data.")
            }
            HttpResponse.created("Form data items for [$formId] has been successfully persisted to database.")

        } catch (e: Exception) {
            logger.error("some error", e)
            HttpResponse.serverError()
        }
    }

    private fun getResonsesByFormId(formId: String, pageSize: Int?, before: String?): Responses? {
        return Klaxon().parse<Responses>(
            responsesClient.fetchResponses(
                "Bearer ${configuration.token}",
                formId,
                pageSize,
                before
            ) ?: emptyJsonString
        )
    }

    private fun getFormByFormId(formId: String): Form? {
        return Klaxon().parse<Form>(
            formClient.fetchForm(
                "Bearer ${configuration.token}",
                formId
            ) ?: emptyJsonString
        )
    }
//
//    @Get("/{formId}")
//    fun getAllFormInsights(@Parameter formId: String): HttpResponse<String>{
//
//    }

}