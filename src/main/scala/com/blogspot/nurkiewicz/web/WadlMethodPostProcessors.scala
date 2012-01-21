package com.blogspot.nurkiewicz.web

import net.java.dev.wadl.{WadlDoc, WadlMethod}


/**
 * @author Tomasz Nurkiewicz
 * @since 21.01.12, 14:17
 */

object WadlMethodPostProcessors {

	def addHttpMethod(wadlMethod: WadlMethod, wrapper: MethodWrapper) =
		wadlMethod.withName(wrapper.httpMethod.toString)

	def addClassAndMethodToDoc(wadlMethod: WadlMethod, wrapper: MethodWrapper) = {
		wadlMethod.withDoc(
			new WadlDoc().
				withContent(wrapper.handlerMethod.getMethod.toString)
		)
	}

	val All = List(addHttpMethod _, addClassAndMethodToDoc _)

}