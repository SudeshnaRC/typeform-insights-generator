package org.addvert.marketresearch.typeforminsightsgenerator.handler

import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses

interface IFormHandler {
    fun persistFormResponses(formId: String, responses: Responses)
    fun persistForm(form: Form)

}