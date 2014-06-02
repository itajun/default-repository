package br.ivj.sandbox.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.BookService;

@WebService(serviceName = "bookWS")
public class BookWS extends SpringBeanAutowiringSupport {
	@Autowired
	private BookService bookService;

	@WebMethod(operationName = "createBook")
	public Book createBook(Book book) {

		return bookService.createBook(book);

	}
}
