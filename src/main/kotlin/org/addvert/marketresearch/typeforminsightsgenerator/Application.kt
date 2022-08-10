package org.addvert.marketresearch.typeforminsightsgenerator

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("org.addvert.marketresearch.typeforminsightsgenerator")
		.start()
}