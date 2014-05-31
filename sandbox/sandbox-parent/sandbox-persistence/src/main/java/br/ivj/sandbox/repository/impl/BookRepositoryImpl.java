package br.ivj.sandbox.repository.impl;

import org.springframework.stereotype.Repository;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;

@Repository
public class BookRepositoryImpl implements BookRepository {

	public Book createBook(Book book) {
		System.out.println(String.format("Creating book: %s", book.getTitle()));
		book.setId(0);
		return book;
	}

}
