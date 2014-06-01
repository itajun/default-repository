package br.ivj.sandbox.repository;

import br.ivj.sandbox.entity.Book;

public interface BookRepository {
	public Book createBook(Book book);
	public Book findBookById(Integer id);
	public void deleteBook(Integer id);
}
