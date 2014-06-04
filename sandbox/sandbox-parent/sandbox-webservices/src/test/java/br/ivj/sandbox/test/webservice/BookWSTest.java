package br.ivj.sandbox.test.webservice;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.BookService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-webservices-clients-test.xml" })
@ActiveProfiles(profiles = { "development" })
public class BookWSTest {
	@Autowired
	private BookService bookService;

	@Test
	public void createBook() {
		Book book = new Book();
		book.setTitle("FORCE_ERROR");
		book = bookService.createBook(book);
		assertNotNull(book.getId());
	}

}
