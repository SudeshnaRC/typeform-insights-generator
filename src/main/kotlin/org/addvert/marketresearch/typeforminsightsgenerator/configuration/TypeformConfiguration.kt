package org.addvert.marketresearch.typeforminsightsgenerator.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Requires

@ConfigurationProperties(TypeformConfiguration.PREFIX)
@Requires(property = TypeformConfiguration.PREFIX)
class TypeformConfiguration {
    var token: String? = null

    companion object{
        const val PREFIX = "typeform"
        const val TYPEFORM_API_URL = "https://api.typeform.com"
    }
}