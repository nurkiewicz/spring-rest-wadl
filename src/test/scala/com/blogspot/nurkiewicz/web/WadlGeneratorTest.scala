package com.blogspot.nurkiewicz.web

import org.scalatest.matchers.ShouldMatchers
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.servlet.mvc.condition._
import org.springframework.web.method.HandlerMethod
import org.custommonkey.xmlunit.XMLAssert._
import javax.xml.bind.JAXBContext
import java.io.StringWriter
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.custommonkey.xmlunit.XMLUnit

/**
 * @author Tomasz Nurkiewicz
 * @since 16.01.12, 23:15
 */

class WadlGeneratorTest extends FunSuite with ShouldMatchers with BeforeAndAfterAll {


	override protected def beforeAll() {
		super.beforeAll()
		XMLUnit.setIgnoreWhitespace(true)
	}

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

	def buildGenerator(mapping: Map[RequestMappingInfo, HandlerMethod]) = new WadlGenerator(mapping, "http://example.com/rest")

	test("should generate empty document when no mappings found") {
		//given
		val generator = buildGenerator(Map())

		//when
		val wadl = wadlResources(generator)

		//then
		assertXMLEqual("""
			<application xmlns="http://wadl.dev.java.net/2009/02">
				<doc title="Spring MVC REST appllication"/>
				<resources base="http://example.com/rest"/>
			</application>
		""", wadl)

	}

	def wadlResources(generator: WadlGenerator) = {
		val output = new StringWriter
		JAXBContext.
				newInstance("net.java.dev.wadl").
				createMarshaller().
				marshal(generator.generate(), output)
		println(output)
		output.toString
	}

	test("should generate WADL document with single resource and method") {
		//given
		val generator = buildGenerator(Map(
			mappingInfo("/books", GET) -> handlerMethod("substring")
		))

		//when
		val wadl = wadlResources(generator)

		//then
		/*resources should have size (1)
		val booksResource = resources(0).asInstanceOf[WadlResource]
		val getBooks = booksResource.getMethodOrResource.get(0).asInstanceOf[WadlMethod]
		getBooks.getName should equal ("GET")
		getBooks.getId should equal ("java.lang.String.substring/GET")*/
	}
	
	test("should generate WADL with two resources, each having two methods, all pointing to a single method") {
		val generator = buildGenerator(Map(
			mappingInfo(List("/books"), List(GET, POST)) -> handlerMethod("substring"),
			mappingInfo(List("/toys"), List(GET, POST)) -> handlerMethod("indexOf"),
			mappingInfo("/toys/new", GET) -> handlerMethod("concat")
		))

		//when
		val resources = wadlResources(generator)
		println(resources)
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