package br.ivj.sandbox.webservice.impl;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.service.BookService;
import br.ivj.sandbox.webservice.BookWS;

@WebService(endpointInterface = "br.ivj.sandbox.webservice.BookWS", serviceName="BookWS", targetNamespace="http://webservice.sandbox.ivj.br")
public class BookWSImpl extends SpringBeanAutowiringSupport implements BookWS {
	@Autowired
	private BookService bookService;

	@Override
	@WebMethod(operationName = "createBook")
	public Book createBook(Book book) {

		return bookService.createBook(book);

	}
}
