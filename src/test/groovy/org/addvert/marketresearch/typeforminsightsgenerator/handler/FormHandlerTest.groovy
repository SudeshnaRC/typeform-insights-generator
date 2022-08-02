//package org.addvert.marketresearch.typeforminsightsgenerator.handler
//
//import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.ChoiceNode
//import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.QuestionRelationship
//import org.addvert.marketresearch.typeforminsightsgenerator.model.graph.RespondentNode
//import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
//import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
//import org.addvert.marketresearch.typeforminsightsgenerator.model.typeform.Responses
//import org.addvert.marketresearch.typeforminsightsgenerator.repository.IFormRepository
//import org.addvert.marketresearch.typeforminsightsgenerator.repository.IResponsesRepository
//import spock.lang.Specification
//
//class FormHandlerTest extends Specification {
//
//    def responsesRepository = Mock(IResponsesRepository)
//    def formRepository = Mock(IFormRepository)
//
//    def instance = new FormHandler(responsesRepository, formRepository)
//
//    def responsesChoice = new Responses.Item.Answer.Choice("q0idYhWH2HP5", "6fefdf72-51d0-4f7c-aefe-aef32fa71951", "55-64")
//    def responsesField = new Responses.Item.Answer.Field("S3pWLf4ffpde", "multiple_choice", "9aa98a25-81b2-4cf4-9624-ee576673b302")
//    def answer = new Responses.Item.Answer(responsesField, "choice", null, responsesChoice, null, null, null, null, null, null, null, null)
//    def item = new Responses.Item("52ihzjrpd81vni6gdt6n52ihzjrkk0mf", [answer])
//    def responses = new Responses(1945, 78, [item])
//
//    def formId =  "axX8qmAr"
//
//    def formQuestion = new FormQuestion(formId, "9aa98a25-81b2-4cf4-9624-ee576673b302")
//    def formEntity = new FormEntity(formQuestion,"How old are you?")
//
//    /***************************************
//     * persistFormResponses()
//     */
//
//    def "Should successfully persist correct property graph to database for passed response object"(){
//        given:
//        formRepository.findById(formQuestion) >> Optional.of(formEntity)
//        responsesRepository.relationshipStatement(_ as QuestionRelationship) >>  "-[:HOW_OLD_ARE_YOU_? {id: '9aa98a25-81b2-4cf4-9624-ee576673b302'}]->"
//        responsesRepository.nodeStatement(_ as RespondentNode) >> "CREATE (respondentNode:RespondentNode { id: '52ihzjrpd81vni6gdt6n52ihzjrkk0mf'})"
//        responsesRepository.nodeStatement(_ as ChoiceNode) >> "CREATE (choiceNode: ChoiceNode { id: 'q0idYhWH2HP5', ref: '6fefdf72-51d0-4f7c-aefe-aef32fa71951', label: '55-64'})"
//
//        when:
//        instance.persistFormResponses(formId, responses)
//
//        then:
//        1 * responsesRepository.createPropertyGraph("CREATE (respondentNode:RespondentNode { id: '52ihzjrpd81vni6gdt6n52ihzjrkk0mf'})-[:HOW_OLD_ARE_YOU_? {id: '9aa98a25-81b2-4cf4-9624-ee576673b302'}]->CREATE (choiceNode: ChoiceNode { id: 'q0idYhWH2HP5', ref: '6fefdf72-51d0-4f7c-aefe-aef32fa71951', label: '55-64'})")
//    }
//
//    def "Should successfully persist correct property graph to database for passed response object with type CHOICES"(){
//        given:
//        def answer = new Responses.Item.Answer(responsesField, "choices", null, null, [responsesChoice, responsesChoice], null, null, null, null, null, null, null)
//        def item = new Responses.Item("52ihzjrpd81vni6gdt6n52ihzjrkk0mf", [answer])
//        def responses = new Responses(1945, 78, [item])
//        formRepository.findById(formQuestion) >> Optional.of(formEntity)
//        responsesRepository.relationshipStatement(_ as QuestionRelationship) >>  "-[:HOW_OLD_ARE_YOU_? {id: '9aa98a25-81b2-4cf4-9624-ee576673b302'}]->"
//        responsesRepository.nodeStatement(_ as RespondentNode) >> "CREATE (respondentNode:RespondentNode { id: '52ihzjrpd81vni6gdt6n52ihzjrkk0mf'})"
//        responsesRepository.nodeStatement(_ as ChoiceNode) >> "CREATE (choiceNode: ChoiceNode { id: 'q0idYhWH2HP5', ref: '6fefdf72-51d0-4f7c-aefe-aef32fa71951', label: '55-64'})"
//
//        when:
//        instance.persistFormResponses(formId, responses)
//
//        then:
//        2 * responsesRepository.createPropertyGraph("CREATE (respondentNode:RespondentNode { id: '52ihzjrpd81vni6gdt6n52ihzjrkk0mf'})-[:HOW_OLD_ARE_YOU_? {id: '9aa98a25-81b2-4cf4-9624-ee576673b302'}]->CREATE (choiceNode: ChoiceNode { id: 'q0idYhWH2HP5', ref: '6fefdf72-51d0-4f7c-aefe-aef32fa71951', label: '55-64'})")
//    }
//}