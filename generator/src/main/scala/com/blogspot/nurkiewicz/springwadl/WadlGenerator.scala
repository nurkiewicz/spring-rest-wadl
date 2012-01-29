package com.blogspot.nurkiewicz.springwadl

import net.java.dev.wadl._
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.method.HandlerMethod
import collection.JavaConversions._
import org.springframework.web.bind.annotation.RequestMethod
import collection.immutable.SortedMap
import collection.immutable
import java.{util => ju}

/**
 * @author Tomasz Nurkiewicz
 * @since 15.01.12, 18:34
 */

class WadlGenerator(
		                   mapping: Map[RequestMappingInfo, HandlerMethod],
		                   baseUrl: String,
		                   methodPostProcessors: immutable.Seq[(WadlMethod, MethodWrapper) => WadlMethod] = WadlMethodPostProcessors.All,
		                   resourcePostProcessors: immutable.Seq[WadlResource => WadlResource] = WadlResourcePostProcessors.All
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
		def addResourceInCorrectPlace(uri: Seq[String], resource: WadlResource, childResources: ju.List[AnyRef]) {
			childResources.collect{case r: WadlResource => r}.find(_.getPath == uri.head) match {
				case Some(child) => 
					addResourceInCorrectPlace(uri.tail, resource, child.getMethodOrResource)
				case None =>
					uri.tail match {
						case Nil =>
							childResources += resource
						case tail =>
							val newChildResource = postProcessResource(new WadlResource().withPath(uri.head))
							childResources += newChildResource
							addResourceInCorrectPlace(tail, resource, newChildResource.getMethodOrResource)
					}
			}
		}

		val root = new WadlResources().withBase(baseUrl)
		resources foreach {case(uri, resource) =>
			addResourceInCorrectPlace(splitUri(uri), resource, root.getResource.asInstanceOf[ju.List[AnyRef]])
		}
		root
	}

	private def splitUri(uri: String) = uri.split("/").filterNot(_.isEmpty)
	private def cleanUri(uri: String) = splitUri(uri).mkString("/")
	private def parentUri(uri: String) = splitUri(uri).init.mkString("/")

	def postProcessResource(resource: WadlResource): WadlResource = {
		resourcePostProcessors.foldLeft(resource) {
			(curWadlResource, postProcessorFun) =>
				postProcessorFun(curWadlResource)
		}
	}

	private def buildResource(uri: String, methods: scala.collection.Iterable[MethodWrapper]) = {
		val wadlMethods = methods.map(buildMethod)
		val resource = new WadlResource().
				withPath(splitUri(uri).last).
				withMethodOrResource(wadlMethods.toSeq.sortBy(_.getName): _*)
		postProcessResource(resource)
	}

	private def buildMethod(method: MethodWrapper) =
		methodPostProcessors.foldLeft(new WadlMethod()) {
			(curWadlMethod, postProcessorFun) =>
				postProcessorFun(curWadlMethod, method)
		}

}

class MethodWrapper(val uri: String, val httpMethod: RequestMethod, val handlerMethod: HandlerMethod)