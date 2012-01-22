package com.blogspot.nurkiewicz.web

import net.java.dev.wadl._
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.method.HandlerMethod
import collection.JavaConversions._
import org.springframework.web.bind.annotation.RequestMethod
import collection.immutable.SortedMap

/**
 * @author Tomasz Nurkiewicz
 * @since 15.01.12, 18:34
 */

class WadlGenerator(
		                   mapping: Map[RequestMappingInfo, HandlerMethod],
		                   baseUrl: String,
		                   methodPostProcessors: Seq[(WadlMethod, MethodWrapper) => WadlMethod] = WadlMethodPostProcessors.All,
		                   resourcePostProcessors: Seq[WadlResource => WadlResource] = WadlResourcePostProcessors.All
		                   ) {

	def generate() = {
		val methods = for ((mappingInfo, handlerMethod) <- mapping;
		                    pattern <- mappingInfo.getPatternsCondition.getPatterns;
		                    if (!splitUri(pattern).isEmpty);
		                    httpMethod <- mappingInfo.getMethodsCondition.getMethods)
		yield new MethodWrapper(pattern, httpMethod, handlerMethod)

		val resources = SortedMap(methods.groupBy(mw => cleanUri(mw.uri)).map {
			case (uri, handlers) => (uri, buildResource(uri, handlers))
		}.toSeq: _*)

		new WadlApplication().
				withDoc(new WadlDoc().withTitle("Spring MVC REST appllication")).
				withResources(buildHierarchy(resources))
	}

	private def buildHierarchy(resources: Map[String, WadlResource]) = {
		val root = new WadlResources().withBase(baseUrl)
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
	def parentUri(uri: String) = splitUri(uri).init.mkString("/")

	private def buildResource(uri: String, methods: scala.collection.Iterable[MethodWrapper]) = {
		val resource = new WadlResource().
				withPath(splitUri(uri).last).
				withMethodOrResource(methods.map(buildMethod).toSeq.sortBy(_.getName): _*)
		resourcePostProcessors.foldLeft(resource) {
			(curWadlResource, postProcessorFun) =>
				postProcessorFun(curWadlResource)
		}
		}

	private def buildMethod(method: MethodWrapper) =
		methodPostProcessors.foldLeft(new WadlMethod()) {
			(curWadlMethod, postProcessorFun) =>
				postProcessorFun(curWadlMethod, method)
		}

}

class MethodWrapper(val uri: String, val httpMethod: RequestMethod, val handlerMethod: HandlerMethod)