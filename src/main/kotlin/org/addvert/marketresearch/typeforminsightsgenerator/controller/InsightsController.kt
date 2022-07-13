package org.addvert.marketresearch.typeforminsightsgenerator.controller


import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import org.addvert.marketresearch.typeforminsightsgenerator.client.IQuestionsClient

import org.addvert.marketresearch.typeforminsightsgenerator.client.IResponsesClient
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler


@Controller("/insights")
class InsightsController(
    private val responsesClient: IResponsesClient,
    private val questionClients: IQuestionsClient,
    private val formHandler: IFormHandler,
    private val configuration: TypeformConfiguration
)  {
    @Put("/{formId}")
    fun putFormData(@Parameter formId: String): HttpResponse<String> {
        return try {
            val questions = questionClients.fetchQuestions("Bearer ${configuration.token}", formId)
            val responses = responsesClient.fetchResponses("Bearer ${configuration.token}", formId)
            formHandler.persistFormQuestions(questions)
            formHandler.persistFormResponses(responses)
            HttpResponse.created("Form data for [$formId] have been successfully persisted to database.")

        } catch (e: Exception) {

            HttpResponse.serverError()
        }
    }

}