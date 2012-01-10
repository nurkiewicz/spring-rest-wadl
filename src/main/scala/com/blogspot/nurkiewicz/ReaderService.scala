package com.blogspot.nurkiewicz

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct
import collection.JavaConversions._
import net._01001111.text.LoremIpsum
import java.util.Date
import org.springframework.data.domain.PageRequest

/**
 * @author Tomasz Nurkiewicz
 * @since 10.01.12, 21:56
 */
@Service
@Transactional
class ReaderService @Autowired() (readerDao: ReaderDao, borrowedBookDao: BorrowedBookDao, bookService: BookService, loremIpsum: LoremIpsum) {

	@deprecated def this() {this(null, null, null, null)}

	@PostConstruct
	def init() {
		val books = bookService.listBooks(new PageRequest(0, 100)).getContent
		for(_ <- 1 to 30) {
			val reader = readerDao save new Reader(loremIpsum.randomWord() + " " + loremIpsum.randomWord())
			for(_ <- 1 to (math.random * 6).toInt) {
				val randomBook = books((math.random * books.size).toInt)
				borrowedBookDao save new BorrowedBook(reader, randomBook, new Date())
			}
		}
	}

}