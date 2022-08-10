package org.addvert.marketresearch.typeforminsightsgenerator.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration


@Client(TypeformConfiguration.TYPEFORM_API_URL)
interface IFormClient {

    @Get("/forms/{formId}")
    fun fetchForm(
        @Header(name = "Authorization") authorization: String?,
        @PathVariable formId: String
    ): String?

}