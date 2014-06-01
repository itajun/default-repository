package br.ivj.sandbox.service;

import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import br.ivj.sandbox.entity.Book;

@Service("asyncBatchBookCreationService")
public class AsyncBatchBookCreationService {
	private Logger logger = LoggerFactory.getLogger(AsyncBatchBookCreationService.class);
	
	@Autowired
	private BookService bookService;
	
	@Async
	public Future<Integer> createBatchBooks(List<Book> books) {
		logger.debug("Start execution of async book creation task");
		int result = books.size();
		for (Book book : books) {
			try {
				bookService.createBook(book);
			} catch (RuntimeException e) {
				result--;
				logger.warn(String.format("Failed to create book %s.", book));
			}
		}
		logger.info("Complete execution of async book creation task");
		return new AsyncResult<Integer>(result);
	}
}
