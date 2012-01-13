package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.bind.annotation.{ResponseBody, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import scala.collection.JavaConversions._
import net.java.dev.wadl._2009._02._
import net.java.dev.wadl._2009._02.WadlParamStyle._
import javax.servlet.http.HttpServletRequest

/**
 * @author Tomasz Nurkiewicz
 * @since 09.01.12, 22:39
 */
@Controller
class WadlController @Autowired()(mapping: RequestMappingHandlerMapping) {

	def param(name: String, style: WadlParamStyle) =
		new WadlParam().
				withName(name).
				withStyle(style)

	@RequestMapping(value = Array("/"), method = Array(GET))
	@ResponseBody def generate(request: HttpServletRequest) = {

		val methods = for (mappingInfo <- mapping.getHandlerMethods.keys;
		                   method <- mappingInfo.getMethodsCondition.getMethods;
		                   pattern <- mappingInfo.getPatternsCondition.getPatterns)
		yield (pattern, method)
		methods.groupBy(_._1).collect({
			case (k, v) => (k, v.map(_._2))
		}).toSeq.sortBy(_._1.size) foreach println

		new WadlApplication().
				withDoc(
			new WadlDoc().
					withTitle("Spring MVC REST appllication")).
				withResources(
			new WadlResources().
					withBase(request.getRequestURL.toString).
					withResource(
				new WadlResource().
						withPath("book")
						withMethodOrResource(
						new WadlMethod().
								withName("GET").
								withRequest(
							new WadlRequest().
									withParam(
								param("page", QUERY), param("max", QUERY)
							)
						),
						new WadlMethod().
								withName("POST"),
						new WadlResource().
								withPath("{bookId}").
								withParam(param("bookId", TEMPLATE)).
								withMethodOrResource(
							new WadlMethod().withName("GET"),
							new WadlMethod().withName("PUT"),
							new WadlMethod().withName("DELETE"),
							new WadlResource().
									withPath("review")
									withMethodOrResource(
									new WadlMethod().
											withName("GET").
											withRequest(
										new WadlRequest().
												withParam(
											param("page", QUERY), param("max", QUERY)
										)
									),
									new WadlMethod().
											withName("POST"),
									new WadlResource().
											withPath("{reviewId}").
											withParam(param("reviewId", TEMPLATE))
											withMethodOrResource(
											new WadlMethod().withName("GET"),
											new WadlMethod().withName("PUT"),
											new WadlMethod().withName("DELETE")
											)
									)
						)
						),
				new WadlResource().
						withPath("reader")
						withMethodOrResource(
						new WadlMethod().
								withName("GET").
								withRequest(
							new WadlRequest().
									withParam(param("page", QUERY), param("max", QUERY)
							)
						),
						new WadlMethod().
								withName("POST")
						)

			)
		)
	}

}