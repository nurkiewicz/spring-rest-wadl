package com.blogspot.nurkiewicz

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest

/**
 * @author Tomasz Nurkiewicz
 * @since 10.01.12, 21:56
 */
@Service
@Transactional
class BorrowedBooksService @Autowired() (borrowedBookDao: BorrowedBookDao) {

	@deprecated def this() {this(null)}

	def findBy(id: Int) = Option(borrowedBookDao findOne id)

	def listBorrowedBooks(page: PageRequest) = borrowedBookDao findAll page

	def deleteBy(id: Int) {
		borrowedBookDao delete id
	}

	def save(borrowedBook: BorrowedBook) = borrowedBookDao save borrowedBook

	def update(borrowedBook: BorrowedBook) = borrowedBookDao save borrowedBook

}