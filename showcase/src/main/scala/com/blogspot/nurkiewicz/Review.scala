package com.blogspot.nurkiewicz

import org.springframework.data.repository.PagingAndSortingRepository
import java.{lang => jl}
import javax.persistence._
import reflect.BeanProperty
import javax.xml.bind.annotation.{XmlTransient, XmlRootElement}

/**
 * @author Tomasz Nurkiewicz
 * @since 08.11.11, 22:08
 */
@Entity
@XmlRootElement
class Review(@BeanProperty var author: String, _contents: String, _book: Book) {

	@deprecated
	def this() {this("", "", new Book("", "", 0))}

	@Column(length = 4000)
	@BeanProperty
	var contents = _contents

	@Id
	@GeneratedValue
	@BeanProperty
	var id = 0

	@ManyToOne
	@BeanProperty
	@XmlTransient
	var book = _book

}

trait ReviewDao extends PagingAndSortingRepository[Review, jl.Integer]