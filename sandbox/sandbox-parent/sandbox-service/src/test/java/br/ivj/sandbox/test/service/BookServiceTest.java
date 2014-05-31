package br.ivj.sandbox.test.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.BookService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence.xml" })
public class BookServiceTest {
	@Autowired
	private BookService bookService;

	@Test(expected = RuntimeException.class)
	public void expectedInvalidTitleExceptionTest() {
		Book book = new Book();
		book.setTitle("TEST");
		bookService.createBook(book);
	}

	@Test
	public void expectedBookCreateTest() {
		Book book = new Book();
		book.setTitle("NO TEST");
		book = bookService.createBook(book);
		assertNotNull(book.getId());
	}

}
