package br.ivj.sandbox.test.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.crsh.shell.impl.command.system.repl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.BookService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles = { "development" })
public class BookServiceJTATest {
	private static final int ID_SUCCESS_TO_MIGRATE = 2;
	private static final int ID_FAIL_TO_MIGRATE = 1;
	
	@Autowired
	private BookService bookService;

	@Test
	public void expectedBookCreateTest() {
		// Let's manually migrate a book
		Book legacyBook = bookService.findBookByLegacyId(ID_FAIL_TO_MIGRATE);
		assertNotNull("Should have found one.", legacyBook);
		Book createdBook = bookService.createBook(legacyBook);
		Book invalidBook = new Book();
		invalidBook.setTitle("FORCE_ERROR");
		try {
			bookService.createBook(invalidBook);
		} catch (RuntimeException invalidBookException) {
			assertTrue("Should have thrown an exception due to the title.", true);
			createdBook = bookService.findBookById(createdBook.getId());
			assertNotNull("Should have commited.", createdBook);
		}

		// Let's try to migrate the same book and hope the transaction is rolled back
		try {
			bookService.migrateBook(ID_FAIL_TO_MIGRATE);
		} catch (RuntimeException duplicatedBookException) {
			assertTrue("Should have thrown an exception due to unique constraint.", true);
			legacyBook = bookService.findBookByLegacyId(ID_FAIL_TO_MIGRATE);
			assertNotNull("Should have rolled back.", legacyBook);
		}
		
		// Let's try to successfully migrate another book
		Book migratedBook = bookService.migrateBook(ID_SUCCESS_TO_MIGRATE);
		assertNotNull("Should have returned the newly create book.", migratedBook);
		legacyBook = bookService.findBookByLegacyId(ID_SUCCESS_TO_MIGRATE);
		assertTrue("Shouldn't have found one.", legacyBook == null);
	}

}
