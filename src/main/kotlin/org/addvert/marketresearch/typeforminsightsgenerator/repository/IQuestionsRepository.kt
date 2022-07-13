package org.addvert.marketresearch.typeforminsightsgenerator.repository

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormEntity
import org.addvert.marketresearch.typeforminsightsgenerator.model.psql.FormQuestion

@Repository
interface IQuestionsRepository : CrudRepository<FormEntity, FormQuestion>