package com.blogspot.nurkiewicz

import reflect.BeanProperty
import org.springframework.data.repository.PagingAndSortingRepository
import java.{util => ju}
import java.{lang => jl}
import javax.xml.bind.annotation.XmlRootElement
import javax.persistence._

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 18:40
 */
@Entity
@XmlRootElement
class Book(@BeanProperty var title: String, @BeanProperty var author: String, @BeanProperty var publishedYear: Int) {

	@Deprecated
	def this() {this("", "", 0)}

	@Id
	@BeanProperty
	@GeneratedValue
	var id = 0

}

trait BookDao extends PagingAndSortingRepository[Book, jl.Integer]