package br.ivj.sandbox.test.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;
import br.ivj.sandbox.service.BookService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles={"development"})
/* As this test injects a mocked Repository, we must mark the context as dirty, otherwise other tests 
 * will fail as the real repository will never be injected again. 
 */
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class BookServiceMockedTest {
	@Mock
	private BookRepository bookRepository;
	
	@Autowired
	@InjectMocks
	private BookService bookService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Book notABook = new Book();
		notABook.setId(1);
		when(bookRepository.createBook((Book) anyObject())).thenReturn(notABook);
	}
	
	@Test
	public void expectedBookCreateTest() {
		Book book = new Book();
		book.setTitle("NO TEST");
		book = bookService.createBook(book);
		assertEquals(new Integer(1), book.getId());;
	}

}
