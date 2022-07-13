package org.addvert.marketresearch.typeforminsightsgenerator.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form


@Client(TypeformConfiguration.TYPEFORM_API_URL)
interface IQuestionsClient {

    @Get("/forms/{formId}")
    fun fetchQuestions(
        @Header(name = "Authorization") authorization: String?,
        @PathVariable formId: String
    ): Form

}