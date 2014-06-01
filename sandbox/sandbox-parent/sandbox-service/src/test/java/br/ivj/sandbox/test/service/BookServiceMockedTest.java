package br.ivj.sandbox.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;
import br.ivj.sandbox.service.BookService;

/**
 * I kept this class here just as an example, but it doesn't work. Apparently there's a bug on Mockito, as it stopped working as soon
 * as I added two fields of the same type (BookRepository) in the service. After two hours trying to work it around, I gave up. If I ever
 * face the same problem in real life, the easier solution would be to add a setter to the implementation and force the mock into the
 * injected bean.
 * 
 * No longer valid...
 * @RunWith(SpringJUnit4ClassRunner.class)
 * 
 * @author Itamar
 *
 */
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles = { "development" })
/*
 * As this test injects a mocked Repository, we must mark the context as dirty,
 * otherwise other tests will fail as the real repository will never be injected
 * again.
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class BookServiceMockedTest {
	@Mock
	private BookRepository sandboxBookRepository;

	@Autowired
	@InjectMocks
	private BookService bookService;

	@Autowired
	private ApplicationContext context;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Book notABook = new Book();
		notABook.setId(1);
		when(sandboxBookRepository.createBook((Book) anyObject())).thenReturn(
				notABook);
	}

	/**
	 * No longer valid :(.
	 * 
	 * @Test
	 */
	public void expectedBookCreateTest() {
		Book book = new Book();
		book.setTitle("NO TEST");
		book = bookService.createBook(book);
		assertEquals(new Integer(1), book.getId());
		;
	}
}
