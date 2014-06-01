package br.ivj.sandbox.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;
import br.ivj.sandbox.service.BookService;

@Service
@Transactional
public class BookServiceImpl implements BookService {
	@Autowired
	@Qualifier("sandboxBookRepository")
	private BookRepository sandboxBookRepository;

	@Autowired
	@Qualifier("legacyBookRepository")
	private BookRepository legacyBookRepository;

	private Book legacyBook;

	public Book createBook(Book book) {
		if ("FORCE_ERROR".equals(book.getTitle())) {
			// TODO BusinessException and internationalization
			throw new RuntimeException("Forced error.");
		}
		
		return sandboxBookRepository.createBook(book);
	}

	public Book migrateBook(Integer id) {
		legacyBook = legacyBookRepository.findBookById(id);
		legacyBookRepository.deleteBook(id);
		// Might throw UniqueConstraintException
		return sandboxBookRepository.createBook(legacyBook);
	}

	@Transactional(readOnly=true)
	public Book findBookById(Integer id) {
		return sandboxBookRepository.findBookById(id);
	}

	@Transactional(readOnly=true)
	public Book findBookByLegacyId(Integer id) {
		return legacyBookRepository.findBookById(id);
	}
}
