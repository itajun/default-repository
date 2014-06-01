package br.ivj.sandbox.batch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ivj.sandbox.batch.external.entity.ExternalAppAuthor;
import br.ivj.sandbox.batch.external.entity.ExternalAppBook;
import br.ivj.sandbox.batch.utils.cacherepository.ExternalAuthorCacheRepository;
import br.ivj.sandbox.entity.Book;

@Component("combineBookAuthorProcessor")
public class CombineBookAuthorProcessor implements ItemProcessor<ExternalAppBook, Book> {
	@Autowired
	private ExternalAuthorCacheRepository externalAuthorCacheRepository;
	
	@Override
	public Book process(ExternalAppBook externalAppBook) throws Exception {
		ExternalAppAuthor externalAppAuthor = externalAuthorCacheRepository.getCachedAuthor(externalAppBook.getAuthor());
		if (externalAppAuthor == null) {
			// TODO: We can probably play around here
		}
		Book result = new Book();
		result.setTitle(externalAppBook.getTitle());
		result.setAuthor(externalAppAuthor.getName());
		return result;
	}
}
