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
import java.util.Date

/**
 * @author Tomasz Nurkiewicz
 * @since 16.01.12, 23:15
 */
@RunWith(classOf[JUnitRunner])
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooksComplex"/>
				</method>
				<method name="GET">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooksComplex"/>
				</method>
			</resource>
			<resource path="bookz">
				<method name="DELETE">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooksComplex"/>
				</method>
				<method name="GET">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooksComplex"/>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
				</method>
				<method name="POST">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.createBook"/>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
				</method>
			</resource>
			<resource path="readers">
				<method name="GET">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listReaders"/>
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
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listAllReviews"/>
					</method>
				</resource>
				<method name="GET">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
				</method>
				<resource path="reviews">
					<method name="GET">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listAllReviews"/>
					</method>
				</resource>
			</resource>
			<resource path="readers">
				<method name="GET">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listReaders"/>
				</method>
				<resource path="active">
					<method name="GET">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listActiveReviews"/>
					</method>
				</resource>
				<resource path="passive">
					<method name="DELETE">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.deletePassiveReviews"/>
					</method>
					<method name="GET">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listPassiveReviews"/>
					</method>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should create intermediate resources if not existing") {
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
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listAllReviews"/>
					</method>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should create several intermediate resources if not existing") {
		given("")
		val mapping = Map(
			mappingInfo("/books/{bookId}/reviews/{reviewId}/verify", PUT) -> handlerMethod("listAllReviews")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<resource path="{bookId}">
					<param name="bookId" style="template" required="true" />
					<resource path="reviews">
						<resource path="{reviewId}">
							<param name="reviewId" style="template" required="true" />
							<resource path="verify">
								<method name="PUT">
									<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listAllReviews"/>
								</method>
							</resource>
						</resource>
					</resource>
				</resource>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL for several resources without ancestors") {
		given("")
		val mapping = Map(
			mappingInfo("/books/reviews/verify", PUT) -> handlerMethod("createBook"),
			mappingInfo("/users", GET) -> handlerMethod("listAllReviews"),
			mappingInfo("/readers/votes/authorized", GET) -> handlerMethod("readReviewComment")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<resource path="reviews">
					<resource path="verify">
						<method name="PUT">
							<doc title="com.blogspot.nurkiewicz.springwadl.TestController.createBook"/>
						</method>
					</resource>
				</resource>
			</resource>
			<resource path="readers">
				<resource path="votes">
					<resource path="authorized">
						<method name="GET">
							<doc title="com.blogspot.nurkiewicz.springwadl.TestController.readReviewComment"/>
						</method>
					</resource>
				</resource>
			</resource>
			<resource path="users">
				<method name="GET">
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listAllReviews"/>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

	test("should generate WADL for several resources without ancestors but with common parent") {
		given("")
		val mapping = Map(
			mappingInfo("/books/reviews/verify", PUT) -> handlerMethod("createBook"),
			mappingInfo("/books/reviews/reject", PUT) -> handlerMethod("deleteBook")
		)

		when("")
		val wadl = generate(mapping)

		then("")
		assertXMLEqual(wadlHeader + """
			<resource path="books">
				<resource path="reviews">
					<resource path="reject">
						<method name="PUT">
							<doc title="com.blogspot.nurkiewicz.springwadl.TestController.deleteBook"/>
						</method>
					</resource>
					<resource path="verify">
						<method name="PUT">
							<doc title="com.blogspot.nurkiewicz.springwadl.TestController.createBook"/>
						</method>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
				</method>
				<resource path="{bookId}">
					<param name="bookId" style="template" required="true" />
					<method name="GET">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.readBook"/>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooks"/>
				</method>
				<resource path="{bookId}">
					<param name="bookId" style="template" required="true" />
					<method name="DELETE">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.deleteBook"/>
					</method>
					<method name="GET">
						<doc title="com.blogspot.nurkiewicz.springwadl.TestController.readBook"/>
					</method>
					<resource path="reviews">
						<method name="GET">
							<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBookReviews"/>
						</method>
						<resource path="{reviewId}">
							<param name="reviewId" style="template" required="true" />
							<method name="DELETE">
								<doc title="com.blogspot.nurkiewicz.springwadl.TestController.deleteBookReview"/>
							</method>
							<method name="GET">
								<doc title="com.blogspot.nurkiewicz.springwadl.TestController.readBookReview"/>
							</method>
							<resource path="comments">
								<method name="GET">
									<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBookReviewComments"/>
								</method>
								<resource path="{commentId}">
									<param name="commentId" style="template" required="true" />
									<method name="DELETE">
										<doc title="com.blogspot.nurkiewicz.springwadl.TestController.deleteReviewComment"/>
									</method>
									<method name="GET">
										<doc title="com.blogspot.nurkiewicz.springwadl.TestController.readReviewComment"/>
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
					<doc title="com.blogspot.nurkiewicz.springwadl.TestController.listBooksWithPaging"/>
					<request>
						<param name="page" style="query" required="false" default="1" />
						<param name="size" style="query" required="false" default="20" />
					</request>
				</method>
			</resource>
		""" + wadlFooter, wadl)
	}

}
