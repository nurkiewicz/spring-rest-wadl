package com.blogspot.nurkiewicz

import java.{lang => jl}
import java.{io => ji}
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.Date
import javax.persistence._

/**
 * @author Tomasz Nurkiewicz
 * @since 10.01.12, 21:53
 */
@Entity
class BorrowedBook(_reader: Reader, _book: Book, var borrowedTime: Date) {

	@deprecated def this() {this(null, null, null)}

	@Id
	@GeneratedValue
	var id: jl.Integer = _

	@ManyToOne
	var reader = _reader

	@ManyToOne
	var book = _book

}

trait BorrowedBookDao extends PagingAndSortingRepository[BorrowedBook, jl.Integer]