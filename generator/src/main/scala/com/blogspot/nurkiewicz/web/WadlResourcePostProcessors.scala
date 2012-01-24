package com.blogspot.nurkiewicz.web

import net.java.dev.wadl._
import net.java.dev.wadl.WadlParamStyle._


/**
 * @author Tomasz Nurkiewicz
 * @since 22.01.12, 18:40
 */

object WadlResourcePostProcessors {

	def pathVariableDescription(wadlResource: WadlResource) =
		if(wadlResource.getPath.head == '{' && wadlResource.getPath.last == '}') {
			wadlResource.withParam(
				new WadlParam().
					withName(wadlResource.getPath.tail.init).
					withRequired(true).
					withStyle(TEMPLATE)
			)
		} else {
			wadlResource
		}

	val All = List(pathVariableDescription _)

}