package br.ivj.sandbox.service;

import br.ivj.sandbox.entity.Book;

public interface BookService {
	public Book createBook(Book book);
	public Book findBookById(Integer id);
}
