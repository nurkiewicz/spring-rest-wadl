package com.blogspot.nurkiewicz.web

import net.java.dev.wadl.{WadlDoc, WadlMethod}


/**
 * @author Tomasz Nurkiewicz
 * @since 21.01.12, 14:17
 */

object WadlMethodPostProcessors {

	def addHttpMethod(wadlMethod: WadlMethod, wrapper: MethodWrapper) =
		wadlMethod.withName(wrapper.httpMethod.toString)

	def classNameDoc(wadlMethod: WadlMethod, wrapper: MethodWrapper) =
		wadlMethod.
			withDoc(
				new WadlDoc().
					withTitle("class")
					withContent(wrapper.handlerMethod.getMethod.getDeclaringClass.getName)
			)

	def methodNameDoc(wadlMethod: WadlMethod, wrapper: MethodWrapper) =
		wadlMethod.
			withDoc(
				new WadlDoc().
					withTitle("method")
					withContent(wrapper.handlerMethod.getMethod.getName)
			)

	val All = List(addHttpMethod _, classNameDoc _, methodNameDoc _)

}