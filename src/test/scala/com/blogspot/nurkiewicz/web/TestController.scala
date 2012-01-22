package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.bind.annotation.{PathVariable, ResponseBody, RequestMapping}

/**
 * @author Tomasz Nurkiewicz
 * @since 21.01.12, 19:11
 */
@Controller
class TestController {

	@RequestMapping(value = Array("/books"), method = Array(GET))
	@ResponseBody def listBooks() = ""

	@RequestMapping(value = Array("/books/{bookId}"), method = Array(GET))
	@ResponseBody def readBook(@PathVariable("bookId") bookId: Int) = ""

	@RequestMapping(value = Array("/books/reviews"), method = Array(GET))
	@ResponseBody def listReviews() = ""

	@RequestMapping(value = Array("/books"), method = Array(POST))
	@ResponseBody def createBook() = ""

	@RequestMapping(value = Array("/books", "/bookz"), method = Array(GET, DELETE))
	@ResponseBody def listBooksComplex() = ""

	@RequestMapping(value = Array("/readers"), method = Array(GET))
	@ResponseBody def listReaders() = ""

	@RequestMapping(value = Array("/active"), method = Array(GET))
	@ResponseBody def listActiveReviews() = ""

	@RequestMapping(value = Array("/passive"), method = Array(GET))
	@ResponseBody def listPassiveReviews() = ""

	@RequestMapping(value = Array("/passive"), method = Array(DELETE))
	@ResponseBody def deletePassiveReviews() = ""

}