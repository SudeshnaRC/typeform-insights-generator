package org.addvert.marketresearch.typeforminsightsgenerator.handler

import com.github.michaelbull.result.Result
import io.micronaut.http.HttpResponse
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.*
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity

interface IFormHandler {
    companion object {
        fun buildErrorResponse(message: ErrorMessage): HttpResponse<Any> {
            return when (message) {
                UnableToParseForm, UnableToParseResponses -> HttpResponse.badRequest()
                FormNotFound, ResponsesNotFound -> HttpResponse.notFound()
                UnmatchedAnswerAttribute -> HttpResponse.serverError()
            }
        }
    }

    fun persistFormIfFound(formId: String): Result<List<FormEntity>, ErrorMessage>
    fun persistResponsesIfFound(formId: String): Result<Unit, ErrorMessage>

}