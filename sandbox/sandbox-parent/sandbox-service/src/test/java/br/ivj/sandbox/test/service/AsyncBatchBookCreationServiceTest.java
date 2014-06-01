package br.ivj.sandbox.test.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.AsyncBatchBookCreationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service-test.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles = { "development" })
public class AsyncBatchBookCreationServiceTest {
	@Autowired
	private AsyncBatchBookCreationService asyncBatchBookCreationService;

	@Test
	public void expectedBookCreateTest() throws InterruptedException {
		Future<Integer> result = asyncBatchBookCreationService.createBatchBooks(get1001Books());
		int iterations = 0;
		while (!result.isDone()) {
			iterations++;
			Thread.sleep(50);
		}
		assertTrue(iterations > 0);
	}

	private List<Book> get1001Books() {
		List<Book> result = new ArrayList<Book>();
		for (int i = 0; i < 1000; i++) {
			Book book = new Book();
			book.setTitle("Book " + book.getId());
			book.setAuthor("Most Published Author Ever");
			result.add(book);
		}
		// Adding an invalid one
		Book book = new Book();
		book.setTitle("FORCE_ERROR");
		result.add(book);
		return result;
	}
}
