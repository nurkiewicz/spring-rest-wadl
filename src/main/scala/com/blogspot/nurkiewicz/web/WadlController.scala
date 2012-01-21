package com.blogspot.nurkiewicz.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMethod._
import org.springframework.web.bind.annotation.{ResponseBody, RequestMapping}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import scala.collection.JavaConversions._
import javax.servlet.http.HttpServletRequest

/**
 * @author Tomasz Nurkiewicz
 * @since 09.01.12, 22:39
 */
@Controller
class WadlController @Autowired()(mapping: RequestMappingHandlerMapping) {

	@RequestMapping(method = Array(GET))
	@ResponseBody def generate(request: HttpServletRequest) = {
		new WadlGenerator(mapping.getHandlerMethods.toMap, request.getRequestURL.toString).generate()
	}

}