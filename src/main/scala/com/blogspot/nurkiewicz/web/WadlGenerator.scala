package com.blogspot.nurkiewicz.web

import net.java.dev.wadl._
import net.java.dev.wadl.WadlParamStyle._
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.method.HandlerMethod
import collection.JavaConversions._

/**
 * @author Tomasz Nurkiewicz
 * @since 15.01.12, 18:34
 */

class WadlGenerator(mapping: Map[RequestMappingInfo, HandlerMethod]) {

	def param(name: String, style: WadlParamStyle) =
		new WadlParam().
				withName(name).
				withStyle(style)

	def generate() = {

		val methods = for (mappingInfo <- mapping.keys;
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
					withBase("http://todo").
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