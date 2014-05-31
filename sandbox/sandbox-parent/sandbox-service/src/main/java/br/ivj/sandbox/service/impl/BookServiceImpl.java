package br.ivj.sandbox.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;
import br.ivj.sandbox.service.BookService;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	private BookRepository repository;

	public Book createBook(Book book) {
		if ("FORCE_ERROR".equals(book.getTitle())) {
			// TODO BusinessException and internationalization
			throw new RuntimeException("Chega de testes");
		}
		
		return repository.createBook(book);
	}

	public Book findBookById(Integer id) {
		return repository.findBookById(id);
	}
}
