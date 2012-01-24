package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.data.domain.PageRequest
import com.blogspot.nurkiewicz.{BorrowedBook, BorrowedBooksService}

/**
 * @author Tomasz Nurkiewicz
 * @since 24.09.11, 23:39
 */
@Controller
@RequestMapping(value = Array("/readers/{readerId}/borrowed"))
class BorrowedBooksController @Autowired()(borrowedBooksService: BorrowedBooksService) {

	@RequestMapping(value = Array("/{borrowedId}"), method = Array(GET))
	@ResponseBody def read(@PathVariable("borrowedId") id: Int) = borrowedBooksService.findBy(id).getOrElse(throw new NotFoundException)

	@RequestMapping(method = Array(GET))
	@ResponseBody def listBorrowedBooks(
			                           @RequestParam(value = "page", required = false, defaultValue = "1") page: Int,
			                           @RequestParam(value = "max", required = false, defaultValue = "20") max: Int) =
		new ResultPage(borrowedBooksService.listBorrowedBooks(new PageRequest(page - 1, max)))

	@RequestMapping(value = Array("/{borrowedId}"), method = Array(PUT))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def updateBorrowedBook(@PathVariable("borrowedId") id: Int, @RequestBody borrowedBook: BorrowedBook) {
		borrowedBook.id = id
		borrowedBooksService update borrowedBook
	}

	@RequestMapping(method = Array(POST))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def createBorrowedBook(request: HttpServletRequest, @RequestBody borrowedBook: BorrowedBook) = {
		borrowedBooksService save borrowedBook
	}

	@RequestMapping(value = Array("/{borrowedId}"), method = Array(DELETE))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def deleteBorrowedBook(@PathVariable("borrowedId") id: Int) {
		borrowedBooksService deleteBy id
	}
}

