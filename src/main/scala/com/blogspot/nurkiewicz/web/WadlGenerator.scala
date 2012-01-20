package com.blogspot.nurkiewicz.web

import net.java.dev.wadl._
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.method.HandlerMethod
import collection.JavaConversions._
import org.springframework.web.bind.annotation.RequestMethod

/**
 * @author Tomasz Nurkiewicz
 * @since 15.01.12, 18:34
 */

class WadlGenerator(mapping: Map[RequestMappingInfo, HandlerMethod]) {

	def generate() = {
		val methods = for ((mappingInfo, handlerMethod) <- mapping;
		                    pattern <- mappingInfo.getPatternsCondition.getPatterns;
		                    if (!splitUri(pattern).isEmpty);
		                    httpMethod <- mappingInfo.getMethodsCondition.getMethods)
		yield new MethodWrapper(pattern, httpMethod, handlerMethod)

		val resources = methods.groupBy(mw => cleanUri(mw.uri)).map {
			case (uri, handlers) => (uri, buildResource(uri, handlers).get)
		}

		new WadlApplication().
				withDoc(new WadlDoc().withTitle("Spring MVC REST appllication")).
				withResources(buildHierarchy(resources))
	}

	private def buildHierarchy(resources: Map[String, WadlResource]) = {
		val root = new WadlResources()
		resources foreach {case(uri, resource) =>
			resources.get(parentUri(uri)) match {
				case Some(parent) => parent.getMethodOrResource += resource
				case None => root.getResource += resource
			}
		}
		root
	}


	def splitUri(uri: String) = uri.split("/").filterNot(_.isEmpty)
	def cleanUri(uri: String) = splitUri(uri).mkString("/")
	def parentUri(uri: String) = uri.init.mkString("/")

	private def buildResource(uri: String, methods: scala.collection.Iterable[MethodWrapper]) = splitUri(uri).lastOption map {sUri =>
		new WadlResource().
				withPath(sUri).
				withMethodOrResource((methods map buildMethod).toSeq: _*)
	}


	private def buildMethod(method: MethodWrapper) = {
		val javaMethod = method.handlerMethod.getMethod
		new WadlMethod().
				withName(method.httpMethod.toString).
				withId(javaMethod.getDeclaringClass.getName + "." + javaMethod.getName + "/" + method.httpMethod.toString)
	}

}

class MethodWrapper(val uri: String, val httpMethod: RequestMethod, val handlerMethod: HandlerMethod)