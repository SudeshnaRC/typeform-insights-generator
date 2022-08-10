package org.addvert.marketresearch.typeforminsightsgenerator.controller

import org.addvert.marketresearch.typeforminsightsgenerator.client.IFormClient
import org.addvert.marketresearch.typeforminsightsgenerator.client.IResponsesClient
import org.addvert.marketresearch.typeforminsightsgenerator.configuration.TypeformConfiguration
import org.addvert.marketresearch.typeforminsightsgenerator.handler.IFormHandler
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Form
import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
import spock.lang.Specification

class InsightsControllerTest extends Specification {
    def responsesClient = Mock(IResponsesClient)
    def formClient = Mock(IFormClient)
    def formHandler = Mock(IFormHandler)
    def configuration = new TypeformConfiguration()

    def questionsString = """{
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

    def responsesString = """{
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
    def formChoice = new Form.Field.Properties.Choice("47SBZZ1xJbGW", "2848f62e-a0db-4a02-b804-c18f04d76136", "18-24")
    def properties = new Form.Field.Properties([formChoice])
    def formField = new Form.Field("S3pWLf4ffpde", "How old are you?", "9aa98a25-81b2-4cf4-9624-ee576673b302", properties, "multiple_choice")
    def form = new Form(formId, "Byline Survey", [formField])

    def instance = new InsightsController(responsesClient, formClient, formHandler, configuration)

    def responsesChoice = new Responses.Item.Answer.Choice("q0idYhWH2HP5", "6fefdf72-51d0-4f7c-aefe-aef32fa71951", "55-64")
    def responsesField = new Responses.Item.Answer.Field("S3pWLf4ffpde", "multiple_choice", "9aa98a25-81b2-4cf4-9624-ee576673b302")
    def answer = new Responses.Item.Answer(responsesField, "choice", null, responsesChoice, null, null, null, null, null, null, null, null)
    def item = new Responses.Item("52ihzjrpd81vni6gdt6n52ihzjrkk0mf", [answer])
    def responses = new Responses(1945, 78, [item])

    def "Should return 201 response when both questions and responses exist for a given form ID"() {
        given:
        formClient.fetchForm("Bearer null", "axX8qmAr") >> questionsString
        responsesClient.fetchResponses("Bearer null", "axX8qmAr") >> responsesString
        when:
        def result = instance.putFormData(formId)
        then:
        1 * formHandler.persistForm(form)
        1 * formHandler.persistFormResponses(formId, responses)
        result.code() == 201
        result.body() == "Form data for [$formId] has been successfully persisted to database."

    }

    def "Should return 400 response when questions do not exist for a given form ID"() {
        given:
        formClient.fetchForm("Bearer null", "axX8qmAr") >> null
        responsesClient.fetchResponses("Bearer null", "axX8qmAr") >> null

        when:
        def result = instance.putFormData(formId)

        then:
        result.code() == 400
        result.body() == "Form [$formId] does not contain necessary data."

    }

    def "Should return 500 response exception is thrown by handler"() {
        given:
        formClient.fetchForm("Bearer null", "axX8qmAr") >> questionsString
        responsesClient.fetchResponses("Bearer null", "axX8qmAr") >> responsesString
        when:
        def result = instance.putFormData(formId)
        then:
        1 * formHandler.persistForm(form) >> { throw new Exception() }
        result.code() == 500

    }


}