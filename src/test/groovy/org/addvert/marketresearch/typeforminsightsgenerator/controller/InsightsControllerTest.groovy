package org.addvert.marketresearch.typeforminsightsgenerator.controller

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlin.Unit
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.*
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.PartialForm
import spock.lang.Specification

class InsightsControllerTest extends Specification {

    def formHandler = Mock(IFormHandler)
    def formId = "formId"
    def questionId = "questionId"
    def questionTitle = "questionTitle"
    def questionType = "questionType"
    def instance = new TypeformController(formHandler)
    def formEntities = [new FormEntity(new FormQuestion(formId, questionId), new PartialForm(formId), questionTitle, questionType)]

    def "Should return 200 response when form exists and is persisted for a given form ID"() {
        given:
        formHandler.persistFormIfFound(formId) >> new Ok(formEntities)
        when:
        def result = instance.putFormData(formId)
        then:
        result.code() == 200
    }

    def "Should return 400 response when form json string for given form ID cannot be parsed"() {
        given:
        formHandler.persistFormIfFound(formId) >> new Err(UnableToParseForm.INSTANCE)
        when:
        def result = instance.putFormData(formId)
        then:
        result.code() == 400

    }

    def "Should return 404 response when form for given form ID cannot be not found"() {
        given:
        formHandler.persistFormIfFound(formId) >> new Err(FormNotFound.INSTANCE)
        when:
        def result = instance.putFormData(formId)
        then:
        result.code() == 404

    }

    def "Should return 500 response when exception is thrown while attempting to persist form by given ID"() {
        given:
        formHandler.persistFormIfFound(formId) >> {throw new Exception()}
        when:
        def result = instance.putFormData(formId)
        then:
        result.code() == 500

    }

    def "Should return 200 response when responses exist and is persisted for a given form ID"() {
        given:
        formHandler.persistResponsesIfFound(formId) >> new Ok(Unit.INSTANCE)
        when:
        def result = instance.putResponsesData(formId)
        then:
        result.code() == 200
    }


    def "Should return 400 response when responses json string for given form ID cannot be parsed"() {
        given:
        formHandler.persistResponsesIfFound(formId) >> new Err(UnableToParseResponses.INSTANCE)
        when:
        def result = instance.putResponsesData(formId)
        then:
        result.code() == 400

    }

    def "Should return 404 response when responses for given form ID cannot be not found"() {
        given:
        formHandler.persistResponsesIfFound(formId) >> new Err(ResponsesNotFound.INSTANCE)
        when:
        def result = instance.putResponsesData(formId)
        then:
        result.code() == 404

    }

    def "Should return 500 response when responses for given form ID cannot be not found"() {
        given:
        formHandler.persistResponsesIfFound(formId) >> new Err(UnmatchedAnswerAttribute.INSTANCE)
        when:
        def result = instance.putResponsesData(formId)
        then:
        result.code() == 500

    }

    def "Should return 500 response when exception is thrown while attempting to persist responses by given form ID"() {
        given:
        formHandler.persistResponsesIfFound(formId) >> {throw new Exception()}
        when:
        def result = instance.putResponsesData(formId)
        then:
        result.code() == 500

    }

}