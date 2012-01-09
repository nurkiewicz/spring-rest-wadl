package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import java.net.URI
import org.springframework.web.util.UriTemplate
import org.springframework.beans.factory.annotation.Autowired
import com.blogspot.nurkiewicz.{BookService, Book}
import org.springframework.web.bind.annotation._
import javax.servlet.http.HttpServletRequest
import org.springframework.http.{ResponseEntity, HttpHeaders, HttpStatus}
import org.springframework.data.domain.PageRequest
import scalaj.collection.Implicits._

/**
 * @author Tomasz Nurkiewicz
 * @since 24.09.11, 23:39
 */
@Controller
@RequestMapping(value = Array("/book"))
class BookController @Autowired()(bookService: BookService) {

	@RequestMapping(value = Array("/{id}"), method = Array(GET))
	@ResponseBody def read(@PathVariable("id") id: Int) = bookService.findBy(id).getOrElse(throw new NotFoundException)

	@RequestMapping(method = Array(GET))
	@ResponseBody def listBooks(
			                           @RequestParam(value = "page", required = false, defaultValue = "1") page: Int,
			                           @RequestParam(value = "max", required = false, defaultValue = "20") max: Int) =
		new ResultPage(bookService.listBooks(new PageRequest(page - 1, max)))

	@RequestMapping(value = Array("/{id}"), method = Array(PUT))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def updateBook(@PathVariable("id") id: Int, @RequestBody book: Book) {
		book.id = id
		bookService update book
	}

	@RequestMapping(method = Array(POST)) def createBook(request: HttpServletRequest, @RequestBody book: Book) = {
		bookService save book
		val uri: URI = new UriTemplate("{requestUrl}/{username}").expand(request.getRequestURL.toString, book.id.toString)
		val headers = new HttpHeaders
		headers.put("Location", List(uri.toASCIIString).asJava)
		new ResponseEntity[String](headers, HttpStatus.CREATED)
	}

	@RequestMapping(value = Array("/{id}"), method = Array(DELETE))
	@ResponseStatus(HttpStatus.NO_CONTENT) def deleteBook(@PathVariable("id") id: Int) {
		bookService.deleteBy(id)
	}
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException