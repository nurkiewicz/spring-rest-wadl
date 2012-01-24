package com.blogspot.nurkiewicz

import javax.xml.bind.annotation.XmlRootElement
import reflect.BeanProperty
import org.springframework.data.repository.PagingAndSortingRepository
import java.{lang => jl}
import java.util.Date
import javax.persistence._

/**
 * @author Tomasz Nurkiewicz
 * @since 10.01.12, 21:51
 */
@XmlRootElement
@Entity
class Reader(@BeanProperty var name: String) {

	@deprecated def this(){this("")}

	@Id
	@GeneratedValue
	@BeanProperty
	var id = 0

	@Temporal(TemporalType.TIMESTAMP)
	@BeanProperty
	var created = new Date()

}

trait ReaderDao extends PagingAndSortingRepository[Reader, jl.Integer]