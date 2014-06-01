package br.ivj.sandbox.test.service;


import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.BookService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service-test.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles = { "development" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection="sandboxJdbcDataSource")
public class BookServiceDBUnitTest {
	@Autowired
	private BookService bookService;

	@Test
	@DatabaseSetup("/datasets/books-sample.xml")
	@ExpectedDatabase(value = "/datasets/books-expected-after-creation.xml", table = "BOOK", query = "SELECT TITLE, AUTHOR FROM BOOK WHERE TITLE LIKE '[TEST]%' ORDER BY TITLE")
	public void expectedBookCreatedTest() {
		Book book = new Book();
		book.setTitle("[TEST] BOOK 6");
		book.setAuthor("[TEST] AUTHOR 6");
		book = bookService.createBook(book);
		assertNotNull(book.getId());
		book = bookService.findBookById(book.getId());
		assertEquals("[TEST] BOOK 6", book.getTitle());
	}

	@Test(expected = RuntimeException.class)
	@DatabaseSetup("/datasets/books-sample.xml")
	@ExpectedDatabase(value = "/datasets/books-expected-after-failed-creation.xml", table = "BOOK", query = "SELECT TITLE, AUTHOR FROM BOOK WHERE TITLE LIKE '[TEST]%' ORDER BY TITLE")
	public void expectedBookCreatedFailTest() {
		Book book = new Book();
		book.setTitle("FORCE_ERROR");
		book = bookService.createBook(book);
		assertNotNull(book.getId());
	}
}
