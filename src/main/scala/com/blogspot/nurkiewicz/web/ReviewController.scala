package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation._
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.data.domain.PageRequest
import com.blogspot.nurkiewicz.{Review, ReviewService}

/**
 * @author Tomasz Nurkiewicz
 * @since 24.09.11, 23:39
 */
@Controller
@RequestMapping(value = Array("/book/{bookId}/review"))
class ReviewController @Autowired()(reviewService: ReviewService) {

	@RequestMapping(value = Array("/{id}"), method = Array(GET))
	@ResponseBody def read(@PathVariable("id") id: Int) = reviewService.findBy(id).getOrElse(throw new NotFoundException)

	@RequestMapping(method = Array(GET))
	@ResponseBody def listReviews(
			                           @RequestParam(value = "page", required = false, defaultValue = "1") page: Int,
			                           @RequestParam(value = "max", required = false, defaultValue = "20") max: Int) =
		new ResultPage(reviewService.listReviews(new PageRequest(page - 1, max)))

	@RequestMapping(value = Array("/{id}"), method = Array(PUT))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def updateReview(@PathVariable("id") id: Int, @RequestBody review: Review) {
		review.id = id
		reviewService update review
	}

	@RequestMapping(method = Array(POST))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def createReview(request: HttpServletRequest, @RequestBody review: Review) = {
		reviewService save review
	}

	@RequestMapping(value = Array("/{id}"), method = Array(DELETE))
	@ResponseStatus(HttpStatus.NO_CONTENT)
	def deleteReview(@PathVariable("id") id: Int) {
		reviewService deleteBy id
	}
}

