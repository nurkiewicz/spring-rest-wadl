package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import java.net.URI
import org.springframework.web.util.UriTemplate
import org.springframework.beans.factory.annotation.Autowired
import com.blogspot.nurkiewicz.{ReaderService, Reader}
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
@RequestMapping(value = Array("/readers"))
class ReaderController @Autowired()(readerService: ReaderService) {

	@RequestMapping(value = Array("/{readerId}"), method = Array(GET))
	@ResponseBody def read(@PathVariable("readerId") id: Int) = readerService.findBy(id).getOrElse(throw new NotFoundException)

	@RequestMapping(method = Array(GET))
	@ResponseBody def listReaders(
			                           @RequestParam(value = "page", required = false, defaultValue = "1") page: Int,
			                           @RequestParam(value = "max", required = false, defaultValue = "20") max: Int) =
		new ResultPage(readerService.listReaders(new PageRequest(page - 1, max)))

	@RequestMapping(value = Array("/{readerId}"), method = Array(PUT))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def updateReader(@PathVariable("readerId") id: Int, @RequestBody reader: Reader) {
		reader.id = id
		readerService update reader
	}

	@RequestMapping(method = Array(POST)) def createReader(request: HttpServletRequest, @RequestBody reader: Reader) = {
		readerService save reader
		val uri: URI = new UriTemplate("{requestUrl}/{username}").expand(request.getRequestURL.toString, reader.id.toString)
		val headers = new HttpHeaders
		headers.put("Location", List(uri.toASCIIString).asJava)
		new ResponseEntity[String](headers, HttpStatus.CREATED)
	}

	@RequestMapping(value = Array("/{readerId}"), method = Array(DELETE))
	@ResponseStatus(HttpStatus.NO_CONTENT) def deleteReader(@PathVariable("readerId") id: Int) {
		readerService deleteBy id
	}
}
