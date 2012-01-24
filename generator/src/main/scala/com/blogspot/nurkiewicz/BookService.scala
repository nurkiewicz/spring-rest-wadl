package com.blogspot.nurkiewicz

import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import javax.annotation.PostConstruct
import org.springframework.transaction.annotation.{Propagation, Transactional}
import net._01001111.text.LoremIpsum

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 18:42
 */
@Service
@Transactional
class BookService @Autowired() (bookDao: BookDao, reviewDao: ReviewDao, loremIpsum: LoremIpsum) {

	@Deprecated
	def this() {this(null, null, null)}

	@PostConstruct
	def addBestSellers() {
		if(bookDao.count() == 0)
			insertBestSellers()
	}

	def insertBestSellers() {
		saveBook(new Book("A Tale of Two Cities", "Charles Dickens", 1859))
		saveBook(new Book("The Lord of the Rings", "J. R. R. Tolkien", 1954))
		saveBook(new Book("The Hobbit", "J. R. R. Tolkien", 1937))
		saveBook(new Book("红楼梦 (Dream of the Red Chamber)", "Cao Xueqin", 1759))
		saveBook(new Book("And Then There Were None", "Agatha Christie", 1939))
		saveBook(new Book("The Lion, the Witch and the Wardrobe", "C. S. Lewis", 1950))
		saveBook(new Book("She", "H. Rider Haggard", 1887))
		saveBook(new Book("Le Petit Prince (The Little Prince)", "Antoine de Saint-Exupéry", 1943))
		saveBook(new Book("The Da Vinci Code", "Dan Brown", 2003))
		saveBook(new Book("Think and Grow Rich", "Napoleon Hill", 1937))
		saveBook(new Book("The Catcher in the Rye", "J. D. Salinger", 1951))
		saveBook(new Book("O Alquimista (The Alchemist)", "Paulo Coelho", 1988))
		saveBook(new Book("Steps to Christ", "Ellen G. White", 1892))
		saveBook(new Book("Lolita", "Vladimir Nabokov", 1955))
		saveBook(new Book("Heidis Lehr- und Wanderjahre (Heidi's Years of Wandering and Learning)", "Johanna Spyri", 1880))
		saveBook(new Book("The Common Sense Book of Baby and Child Care", "Dr. Benjamin Spock", 1946))
		saveBook(new Book("Anne of Green Gables", "Lucy Maud Montgomery", 1908))
		saveBook(new Book("Black Beauty: His Grooms and Companions: The autobiography of a horse", "Anna Sewell", 1877))
		saveBook(new Book("Il Nome della Rosa (The Name of the Rose)", "Umberto Eco", 1980))
		saveBook(new Book("The Hite Report", "Shere Hite", 1976))
		saveBook(new Book("Charlotte's Web", "E.B. White; illustrated by Garth Williams", 1952))
		saveBook(new Book("The Tale of Peter Rabbit", "Beatrix Potter", 1902))
		saveBook(new Book("Harry Potter and the Deathly Hallows", "J. K. Rowling", 2007))
		saveBook(new Book("Jonathan Livingston Seagull", "Richard Bach", 1970))
		saveBook(new Book("A Message to Garcia", "Elbert Hubbard", 1899))
		saveBook(new Book("Angels and Demons", "Dan Brown", 2000))
		saveBook(new Book("Как закалялась сталь (Kak zakalyalas' stal'; How the Steel Was Tempered)", "Nikolai Ostrovsky", 1932))
		saveBook(new Book("Война и мир (Voyna i mir; War and Peace)", "Leo Tolstoy", 1869))
		saveBook(new Book("Le avventure di Pinocchio. Storia di un burattino (The Adventures of Pinocchio)", "Carlo Collodi", 1881))
		saveBook(new Book("You Can Heal Your Life", "Louise Hay", 1984))
		saveBook(new Book("Kane and Abel", "Jeffrey Archer", 1979))
		saveBook(new Book("Het Achterhuis (The Diary of a Young Girl, The Diary of Anne Frank)", "Anne Frank", 1947))
		saveBook(new Book("In His Steps: What Would Jesus Do?", "Charles M. Sheldon", 1896))
		saveBook(new Book("To Kill a Mockingbird", "Harper Lee", 1960))
		saveBook(new Book("Valley of the Dolls", "Jacqueline Susann", 1966))
		saveBook(new Book("Gone with the Wind", "Margaret Mitchell", 1936))
		saveBook(new Book("Cien Años de Soledad (One Hundred Years of Solitude)", "Gabriel García Márquez", 1967))
		saveBook(new Book("The Purpose Driven Life", "Rick Warren", 2002))
		saveBook(new Book("The Thorn Birds", "Colleen McCullough", 1977))
		saveBook(new Book("The Revolt of Mamie Stover", "William Bradford Huie", 1951))
		saveBook(new Book("The Girl with the Dragon Tattoo (original title: Män som hatar kvinnor)", "Stieg Larsson", 2005))
		saveBook(new Book("The Very Hungry Caterpillar", "Eric Carle", 1969))
		saveBook(new Book("Sophie's World", "Jostein Gaarder", 1991))
	}

	private def saveBook(book: Book) {
		val savedBook = bookDao save book
		for(i <- 1 to (1 + math.random * 4).toInt)
			reviewDao save new Review(loremIpsum.randomWord(), loremIpsum.paragraph(), savedBook)
	}

	def deleteBy(id: Int) {bookDao delete id}

	def update(book: Book) = bookDao save book

	def save(book: Book) = bookDao save book

	def listBooks(page: PageRequest) = bookDao findAll page

	def findBy(id: Int) = Option(bookDao.findOne(id))

}
