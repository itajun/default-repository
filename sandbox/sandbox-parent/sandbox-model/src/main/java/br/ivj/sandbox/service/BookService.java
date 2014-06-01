package br.ivj.sandbox.service;

import br.ivj.sandbox.entity.Book;

public interface BookService {
	public Book createBook(Book book);
	public Book findBookById(Integer id);
	public Book findBookByLegacyId(Integer id);
	public Book migrateBook(Integer id);
}
