package com.blogspot.nurkiewicz.web

import org.scalatest.matchers.ShouldMatchers
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.servlet.mvc.condition._
import org.springframework.web.method.HandlerMethod
import org.custommonkey.xmlunit.XMLAssert._
import java.io.StringWriter
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.custommonkey.xmlunit.XMLUnit
import javax.xml.bind.{Marshaller, JAXBContext}

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

	def method(name: String) = classOf[TestController].getMethods.find(_.getName == name).get

	def handlerMethod(name: String): HandlerMethod = new HandlerMethod(new Object(), method(name))

	private def wadlResources(generator: WadlGenerator) = {
		val output = new StringWriter
		val marshaller = JAXBContext.
				newInstance("net.java.dev.wadl").
				createMarshaller()
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(generator.generate(), output)
		println(output)
		output.toString
	}

	private def generate(mapping: Map[RequestMappingInfo, HandlerMethod]) =
		wadlResources(
			new WadlGenerator(mapping, "http://example.com/rest")
		)

	test("should generate empty document when no mappings found") {
		//given
		val mapping = Map[RequestMappingInfo, HandlerMethod]()

		//when
		val wadl = generate(mapping)

		//then
		assertXMLEqual("""
			<application xmlns="http://wadl.dev.java.net/2009/02">
				<doc title="Spring MVC REST appllication"/>
				<resources base="http://example.com/rest"/>
			</application>
		""", wadl)

	}

	test("should generate WADL document with single resource and method") {
		//given
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks")
		)

		//when
		val wadl = generate(mapping)

		//then
		assertXMLEqual("""
			<application xmlns="http://wadl.dev.java.net/2009/02">
				<doc title="Spring MVC REST appllication"/>
				<resources base="http://example.com/rest">
					<resource path="books">
						<method name="GET">
							<doc title="class">com.blogspot.nurkiewicz.web.TestController</doc>
							<doc title="method">listBooks</doc>
						</method>
					</resource>
				</resources>
			</application>
		""", wadl)
	}
	
	test("should generate WADL with two resources, each having two methods, all pointing to a single method") {
		val mapping = Map(
			mappingInfo(List("/books", "/bookz"), List(GET, DELETE)) -> handlerMethod("listBooksComplex")
		)

		//when
		val wadl = generate(mapping)

		//then
		assertXMLEqual("""
			<application xmlns="http://wadl.dev.java.net/2009/02">
				<doc title="Spring MVC REST appllication"/>
				<resources base="http://example.com/rest">
					<resource path="books">
						<method name="GET">
							<doc title="class">com.blogspot.nurkiewicz.web.TestController</doc>
							<doc title="method">listBooksComplex</doc>
						</method>
						<method name="DELETE">
							<doc title="class">com.blogspot.nurkiewicz.web.TestController</doc>
							<doc title="method">listBooksComplex</doc>
						</method>
					</resource>
					<resource path="bookz">
						<method name="GET">
							<doc title="class">com.blogspot.nurkiewicz.web.TestController</doc>
							<doc title="method">listBooksComplex</doc>
						</method>
						<method name="DELETE">
							<doc title="class">com.blogspot.nurkiewicz.web.TestController</doc>
							<doc title="method">listBooksComplex</doc>
						</method>
					</resource>
				</resources>
			</application>
		""", wadl)
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

}