package org.addvert.marketresearch.typeforminsightsgenerator.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.PartialForm
import java.util.Optional

@Repository
interface IFormRepository : CrudRepository<FormEntity, FormQuestion>{

    fun findByFormQuestion(formQuestion: FormQuestion): Optional<FormEntity>
    fun findAllByForm(form : PartialForm): List<FormEntity>
}