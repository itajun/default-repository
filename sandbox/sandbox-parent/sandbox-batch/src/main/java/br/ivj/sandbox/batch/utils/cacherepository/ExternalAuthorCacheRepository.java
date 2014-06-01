package br.ivj.sandbox.batch.utils.cacherepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.ivj.sandbox.batch.external.entity.ExternalAppAuthor;

@Component("externalAuthorCacheRepository")
public class ExternalAuthorCacheRepository {
	// TODO: Experiment with ehCache
	private Map<Integer, ExternalAppAuthor> cachedAuthors = new HashMap<Integer, ExternalAppAuthor>();
	
	public ExternalAppAuthor getCachedAuthor(Integer authorId) {
		return cachedAuthors.get(authorId);
	}
	
	public void addCachedAuthor(ExternalAppAuthor author) {
		cachedAuthors.put(author.getId(), author);
	}
}
