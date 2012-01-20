package com.blogspot.nurkiewicz.web

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.servlet.mvc.condition._
import org.springframework.web.method.HandlerMethod
import collection.JavaConversions._
import net.java.dev.wadl.{WadlMethod, WadlResource}

/**
 * @author Tomasz Nurkiewicz
 * @since 16.01.12, 23:15
 */

class WadlGeneratorTest extends FunSuite with ShouldMatchers {

	def mappingInfo(pattern: String, method: RequestMethod): RequestMappingInfo = mappingInfo(List(pattern), List(method))
	
	def mappingInfo(patterns: Seq[String], methods: Seq[RequestMethod]): RequestMappingInfo = {
		new RequestMappingInfo(
			new PatternsRequestCondition(patterns: _*),
			new RequestMethodsRequestCondition(methods: _*),
			new ParamsRequestCondition(),
			new HeadersRequestCondition(),
			new ConsumesRequestCondition(),
			new ProducesRequestCondition(),
			new RequestConditionHolder(null)
		)
	}

	test("should generate empty document when no mappings found") {
		//given
		val generator = new WadlGenerator(Map())

		//when
		val resources = wadlResources(generator)

		//then
		resources should have size (0)
	}

	def wadlResources(generator: WadlGenerator) = {
		generator.generate().getResources.get(0).getResource.toSeq
	}

	test("should generate WADL document with single resource and method") {
		//given
		val generator = new WadlGenerator(Map(
			mappingInfo("/books", GET) -> handlerMethod("substring")
		))

		//when
		val resources = wadlResources(generator)

		//then
		resources should have size (1)
		val booksResource = resources(0).asInstanceOf[WadlResource]
		val getBooks = booksResource.getMethodOrResource.get(0).asInstanceOf[WadlMethod]
		getBooks.getName should equal ("GET")
		getBooks.getId should equal ("java.lang.String.substring/GET")
	}
	
	test("should generate WADL with two resources, each having two methods, all pointing to a single method") {
		
	}
	
	test("should generate WADL with single resource and two methods pointing to two methods") {
		
	}
	
	test("should generate WADL with two resources, each exposing single method") {
		
	}
	
	test("should generate WADL with nested resource") {
		
	}
	
	test("should generate WADL with sibling and child resources") {
		
	}

	test("should add top-level method when mapping to root / directory") {
		
	}

	test("should create intermediate resources if not existing") {

	}

	def handlerMethod(name: String): HandlerMethod = new HandlerMethod("", method(name))

	def method(name: String) = classOf[String].getMethods.find(_.getName == name).get

}