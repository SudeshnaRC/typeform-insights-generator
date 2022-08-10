package org.addvert.marketresearch.typeforminsightsgenerator.handler

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.addvert.marketresearch.typeforminsightsgenerator.client.IFormClient
import org.addvert.marketresearch.typeforminsightsgenerator.client.IResponsesClient
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.FormNotFound
import org.addvert.marketresearch.typeforminsightsgenerator.handler.error.UnableToParseForm
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.AnswerNode
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.QuestionRelationship
import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.RespondentNode
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.PartialForm
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IFormRepository
import org.addvert.marketresearch.typeforminsightsgenerator.repository.IResponsesRepository
import spock.lang.Specification

class FormHandlerTest extends Specification {
    def formClient = Mock(IFormClient)
    def responsesClient = Mock(IResponsesClient)
    def responsesRepository = Mock(IResponsesRepository)
    def formRepository = Mock(IFormRepository)
    def accessToken = "access-token"

    def instance = new FormHandler(responsesClient, formClient, responsesRepository, formRepository, accessToken)
    def formJsonString = """{
                          "id": "axX8qmAr",
                          "type": "quiz",
                          "title": "Byline Survey",
                          "workspace": {
                            "href": "https://api.typeform.com/workspaces/3xKBfu"
                          },
                          "theme": {
                            "href": "https://api.typeform.com/themes/xDWbYOyz"
                          },
                          "fields": [
                            {
                              "id": "S3pWLf4ffpde",
                              "title": "How old are you?",
                              "ref": "9aa98a25-81b2-4cf4-9624-ee576673b302",
                              "properties": {
                                "randomize": false,
                                "allow_multiple_selection": false,
                                "allow_other_choice": false,
                                "vertical_alignment": true,
                                "choices": [
                                  {
                                    "id": "47SBZZ1xJbGW",
                                    "ref": "2848f62e-a0db-4a02-b804-c18f04d76136",
                                    "label": "18-24"
                                  }
                                ]
                              },
                              "validations": {
                                "required": true
                              },
                              "type": "multiple_choice"
                            }
                          ]
                        }""".trim().replaceAll("[\n\r]", "").replaceAll(/\s\s+/, "")

    def responsesJsonString = """{
                                  "total_items": 1945,
                                  "page_count": 78,
                                  "items": [
                                    {
                                      "landing_id": "52ihzjrpd81vni6gdt6n52ihzjrkk0mf",
                                      "token": "52ihzjrpd81vni6gdt6n52ihzjrkk0mf",
                                      "response_id": "52ihzjrpd81vni6gdt6n52ihzjrkk0mf",
                                      "landed_at": "2022-07-12T22:27:31Z",
                                      "submitted_at": "2022-07-12T22:33:47Z",
                                      "hidden": {},
                                      "calculated": {
                                        "score": 0
                                      },
                                      "answers": [
                                        {
                                          "field": {
                                            "id": "S3pWLf4ffpde",
                                            "ref": "9aa98a25-81b2-4cf4-9624-ee576673b302",
                                            "type": "multiple_choice"
                                          },
                                          "type": "choice",
                                          "choice": {
                                            "id": "q0idYhWH2HP5",
                                            "ref": "6fefdf72-51d0-4f7c-aefe-aef32fa71951",
                                            "label": "55-64"
                                          }
                                        }
                                      ]
                                    }
                                  ]
                                }""".trim().replaceAll("[\n\r]", "").replaceAll(/\s\s+/, "")
    def formId = "axX8qmAr"
    def formField = new Form.Field("S3pWLf4ffpde", "How old are you?", "9aa98a25-81b2-4cf4-9624-ee576673b302", "multiple_choice")
    def form = new Form(formId, "Byline Survey", [formField])

    def responsesChoice = new Responses.Item.Answer.Choice("55-64")
    def responsesField = new Responses.Item.Answer.Field("9aa98a25-81b2-4cf4-9624-ee576673b302")
    def answer = new Responses.Item.Answer(responsesField, "choice", null, responsesChoice, null, null, null, null, null, null, null, null)
    def item = new Responses.Item("52ihzjrpd81vni6gdt6n52ihzjrkk0mf", [answer])
    def responses = new Responses(1945, 78, [item])


    /*********************************
     * persistFormIfFound
     */

    def "Typeform successfully persisted if valid Form ID is passed into method constructor"(){
        given:
        def formEntity = new FormEntity(new FormQuestion(formId, responsesField.ref), new PartialForm(formId), formField.title, formField.type)
        formClient.fetchForm("Bearer access-token", formId) >> formJsonString
        formRepository.update(formEntity) >> formEntity
        def success  = new Ok([formEntity])

        when:
        def result = instance.persistFormIfFound(formId)

        then:
        result == success

    }

//    def "Typeform not persisted if invalid Form ID is passed into method constructor"(){
//        given:
//        formClient.fetchForm("Bearer access-token", formId) >> ""
//        def failure  = new Err(FormNotFound.INSTANCE)
//
//        when:
//        def result = instance.persistFormIfFound(formId)
//
//        then:
//        result == failure
//
//    }

    def "Typeform not persisted if exception is thrown while parsing form JSON string"(){
        given:
        formClient.fetchForm("Bearer access-token", formId) >> "form JSON string"
        def failure  = new Err(UnableToParseForm.INSTANCE)

        when:
        def result = instance.persistFormIfFound(formId)

        then:
        result == failure

    }

    /*********************************
     * persistResponsesIfFound
     */

    def "Typeform responses successfully persisted if valid Form ID is passed into method constructor"(){
        given:
        responsesClient.fetchResponses("Bearer access-token", formId, 1000,null) >> responsesJsonString
        def respondent = responsesRepository.nodeStatement(new RespondentNode(item.responseId))
        def answer = responsesRepository.nodeStatement(new AnswerNode(answer))
        def question = responsesRepository.relationshipStatement(new QuestionRelationship(formField.title, formField.ref, formField.type))
        def fullStatement = respondent + answer + question
        responsesRepository.createPropertyGraph(fullStatement)
        def success  = new Ok(responsesRepository.commitBatchInMemoryCypherQuery())

        when:
        def result = instance.persistResponsesIfFound(formId)

        then:
        result == success

    }

}