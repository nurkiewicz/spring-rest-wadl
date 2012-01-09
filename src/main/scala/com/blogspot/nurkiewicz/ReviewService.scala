package com.blogspot.nurkiewicz

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import javax.annotation.PostConstruct
import org.springframework.transaction.annotation.{Propagation, Transactional}

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 18:42
 */
@Service
@Transactional
class ReviewService @Autowired() (reviewDao: ReviewDao) {

	@Deprecated
	def this() {this(null)}

	def deleteBy(id: Int) {reviewDao delete id}

	def update(review: Review) = reviewDao save review

	def save(review: Review) = reviewDao save review

	def listReviews(page: PageRequest) = reviewDao findAll page

	def findBy(id: Int) = Option(reviewDao.findOne(id))

}
