package com.blogspot.nurkiewicz.springwadl

import org.springframework.web.bind.annotation.RequestParam
import net.java.dev.wadl._
import net.java.dev.wadl.WadlParamStyle._


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

	def methodParamDesc(wadlMethod: WadlMethod, wrapper: MethodWrapper) = {
		val javaMethod = wrapper.handlerMethod.getMethod
		val wadlMethodRequest = Option(wadlMethod.getRequest).getOrElse(new WadlRequest)
		val paramTypesWithAnnotation = javaMethod.getParameterTypes zip javaMethod.getParameterAnnotations
		val requestParams = paramTypesWithAnnotation flatMap {
			case (clazz, annotations) => annotations.map((clazz, _))} collect {case(clazz, reqParAnnot: RequestParam) => (clazz, reqParAnnot)
		}
		requestParams foreach {case (clazz, rp) =>
			wadlMethodRequest.withParam(
				new WadlParam().
					withName(rp.value()).
					withStyle(QUERY).
					withRequired(rp.required()).
					withDefault(rp.defaultValue())
			)
		}
		if(!wadlMethodRequest.getParam.isEmpty)
			wadlMethod.withAny(wadlMethodRequest)
		wadlMethod
	}

	val All = List(addHttpMethod _, classNameDoc _, methodNameDoc _, methodParamDesc _)

}