package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.bind.annotation.{ResponseBody, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import scala.collection.JavaConversions._
import net.java.dev.wadl._2009._02._
import javax.servlet.http.HttpServletRequest

/**
 * @author Tomasz Nurkiewicz
 * @since 09.01.12, 22:39
 */
@Controller
class WadlController @Autowired()(mapping: RequestMappingHandlerMapping) {

	@RequestMapping(value = Array("/"), method = Array(GET))
	@ResponseBody def generate(request: HttpServletRequest) = {

		val methods = for (mappingInfo <- mapping.getHandlerMethods.keys;
		                   method <- mappingInfo.getMethodsCondition.getMethods;
		                   pattern <- mappingInfo.getPatternsCondition.getPatterns)
			yield (pattern, method)
		methods.groupBy(_._1).collect({case (k, v) => (k, v.map(_._2))}).toSeq.sortBy(_._1.size) foreach println

		val application = new WadlApplication
		val wadlResources = new WadlResources
		wadlResources.setBase(request.getRequestURL.toString)
		val wadlResource = new WadlResource
		val wadlMethod = new WadlMethod
		wadlMethod.setName("GET")
		val wadlRequest = new WadlRequest
		val wadlParam = new WadlParam()
		wadlParam.setName("page")
		wadlRequest.getParam += wadlParam
		wadlMethod.setRequest(wadlRequest)
		wadlResource.getMethodOrResource += wadlMethod
		wadlResource.setPath("/book")
		wadlResources.getResource += wadlResource
		application.getResources += wadlResources
		application
	}


}