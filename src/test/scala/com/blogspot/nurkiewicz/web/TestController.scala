package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.bind.annotation.{ResponseBody, RequestMapping}

/**
 * @author Tomasz Nurkiewicz
 * @since 21.01.12, 19:11
 */
@Controller
class TestController {

	@RequestMapping(value = Array("/books"), method = Array(GET))
	@ResponseBody def listBooks() = ""

	@RequestMapping(value = Array("/books", "/bookz"), method = Array(GET, DELETE))
	@ResponseBody def listBooksComplex() = ""

}