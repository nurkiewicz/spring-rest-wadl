package com.blogspot.nurkiewicz.springwadl

import org.scalatest.matchers.ShouldMatchers
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.servlet.mvc.condition._
import org.springframework.web.method.HandlerMethod
import org.custommonkey.xmlunit.XMLAssert._
import java.io.StringWriter
import org.custommonkey.xmlunit.XMLUnit
import javax.xml.bind.{Marshaller, JAXBContext}
import org.scalatest.{GivenWhenThen, BeforeAndAfterAll, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Tomasz Nurkiewicz
 * @since 16.01.12, 23:15
 */
class WadlGeneratorTest extends FunSuite with ShouldMatchers with BeforeAndAfterAll with GivenWhenThen {


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

	private def method(name: String) = classOf[TestController].getMethods.find(_.getName == name).getOrElse{
		throw new NoSuchElementException(name)
	}

	private def handlerMethod(name: String): HandlerMethod = new HandlerMethod(new Object(), method(name))

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

	private val wadlHeader = """
		<application xmlns="http://wadl.dev.java.net/2009/02">
			<doc title="Spring MVC REST appllication"/>
			<resources base="http://example.com/rest">
				"""

	private val wadlFooter = """
			</resources>
		</application>
	"""

	private def generate(mapping: Map[RequestMappingInfo, HandlerMethod]) =
		wadlResources(
			new WadlGenerator(mapping, "http://example.com/rest")
		)

	test("should generate empty document when no mappings found") {
		given("")
		val mapping = Map[RequestMappingInfo, HandlerMethod]()

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + wadlFooter, wadl)

	}

	test("should generate WADL document with single resource and method") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL with two resources, each having two methods, all pointing to a single method") {
		given("")
		val mapping = Map(
			mappingInfo(List("/books", "/bookz"), List(GET, DELETE)) -> handlerMethod("listBooksComplex")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="DELETE">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooksComplex</doc>
				</method>
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooksComplex</doc>
				</method>
			</resource>
			<resource path="bookz">
				<method name="DELETE">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooksComplex</doc>
				</method>
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooksComplex</doc>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL with single resource and two HTTP methods pointing to two request handling methods") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks"),
			mappingInfo("/books", POST) -> handlerMethod("createBook")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
				<method name="POST">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">createBook</doc>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL with two resources, each exposing single method") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks"),
			mappingInfo("/readers", GET) -> handlerMethod("listReaders")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
			</resource>
			<resource path="readers">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listReaders</doc>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL with nested resource") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks"),
			mappingInfo("/books/reviews", GET) -> handlerMethod("listAllReviews")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<resource path="reviews">
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">listAllReviews</doc>
					</method>
				</resource>
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL with sibling and child resources") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks"),
			mappingInfo("/books/reviews", GET) -> handlerMethod("listAllReviews"),
			mappingInfo("/readers", GET) -> handlerMethod("listReaders"),
			mappingInfo("/readers/active", GET) -> handlerMethod("listActiveReviews"),
			mappingInfo("/readers/passive", GET) -> handlerMethod("listPassiveReviews"),
			mappingInfo("/readers/passive", DELETE) -> handlerMethod("deletePassiveReviews")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
				<resource path="reviews">
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">listAllReviews</doc>
					</method>
				</resource>
			</resource>
			<resource path="readers">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listReaders</doc>
				</method>
				<resource path="active">
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">listActiveReviews</doc>
					</method>
				</resource>
				<resource path="passive">
					<method name="DELETE">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">deletePassiveReviews</doc>
					</method>
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">listPassiveReviews</doc>
					</method>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	ignore("should create intermediate resources if not existing") {
		given("")
		val mapping = Map(
			mappingInfo("/books/reviews", GET) -> handlerMethod("listAllReviews")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<resource path="reviews">
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">listAllReviews</doc>
					</method>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	ignore("should create several intermediate resources if not existing") {
		given("")
		val mapping = Map(
			mappingInfo("/books/reviews/verify/{reviewId}", PUT) -> handlerMethod("listAllReviews")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<resource path="reviews">
					<resource path="verify">
						<resource path="{reviewId}">
							<method name="PUT">
								<param name="reviewId" style="template" required="true" />
								<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
								<doc title="method">listAllReviews</doc>
							</method>
						</resource>
					</resource>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}


	test("should add parameter info for template parameter in URL") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks"),
			mappingInfo("/books/{bookId}", GET) -> handlerMethod("readBook")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
				<resource path="{bookId}">
					<param name="bookId" style="template" required="true" />
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">readBook</doc>
					</method>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should add parameters description for every template variable found in nested URL") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooks"),
			mappingInfo("/books/{bookId}", GET) -> handlerMethod("readBook"),
			mappingInfo("/books/{bookId}", DELETE) -> handlerMethod("deleteBook"),
			mappingInfo("/books/{bookId}/reviews", GET) -> handlerMethod("listBookReviews"),
			mappingInfo("/books/{bookId}/reviews/{reviewId}", GET) -> handlerMethod("readBookReview"),
			mappingInfo("/books/{bookId}/reviews/{reviewId}", DELETE) -> handlerMethod("deleteBookReview"),
			mappingInfo("/books/{bookId}/reviews/{reviewId}/comments", GET) -> handlerMethod("listBookReviewComments"),
			mappingInfo("/books/{bookId}/reviews/{reviewId}/comments/{commentId}", GET) -> handlerMethod("readReviewComment"),
			mappingInfo("/books/{bookId}/reviews/{reviewId}/comments/{commentId}", DELETE) -> handlerMethod("deleteReviewComment")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooks</doc>
				</method>
				<resource path="{bookId}">
					<param name="bookId" style="template" required="true" />
					<method name="DELETE">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">deleteBook</doc>
					</method>
					<method name="GET">
						<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
						<doc title="method">readBook</doc>
					</method>
					<resource path="reviews">
						<method name="GET">
							<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
							<doc title="method">listBookReviews</doc>
						</method>
						<resource path="{reviewId}">
							<param name="reviewId" style="template" required="true" />
							<method name="DELETE">
								<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
								<doc title="method">deleteBookReview</doc>
							</method>
							<method name="GET">
								<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
								<doc title="method">readBookReview</doc>
							</method>
							<resource path="comments">
								<method name="GET">
									<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
									<doc title="method">listBookReviewComments</doc>
								</method>
								<resource path="{commentId}">
									<param name="commentId" style="template" required="true" />
									<method name="DELETE">
										<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
										<doc title="method">deleteReviewComment</doc>
									</method>
									<method name="GET">
										<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
										<doc title="method">readReviewComment</doc>
									</method>
								</resource>
							</resource>
						</resource>
					</resource>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should add controller method parameters as WADL method parameters") {
		given("")
		val mapping = Map(
			mappingInfo("/books", GET) -> handlerMethod("listBooksWithPaging")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<method name="GET">
					<doc title="class">com.blogspot.nurkiewicz.springwadl.TestController</doc>
					<doc title="method">listBooksWithPaging</doc>
					<request>
						<param name="page" style="query" required="false" default="1" />
						<param name="size" style="query" required="false" default="20" />
					</request>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

}