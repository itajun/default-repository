package br.ivj.sandbox.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import br.ivj.sandbox.entity.Book;

@WebService(name="BookWS")
@SOAPBinding(style = Style.RPC)
public interface BookWS {

	@WebMethod
	public Book createBook(Book book);

}