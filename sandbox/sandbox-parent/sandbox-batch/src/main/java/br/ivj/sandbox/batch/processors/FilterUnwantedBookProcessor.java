package br.ivj.sandbox.batch.processors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import br.ivj.sandbox.batch.external.entity.ExternalAppBook;

@Component("filterUnwantedBookProcessor")
public class FilterUnwantedBookProcessor implements
		ItemProcessor<ExternalAppBook, ExternalAppBook> {
	@Override
	public ExternalAppBook process(ExternalAppBook item) throws Exception {
		if (item.getTitle() != null
				&& item.getTitle().toUpperCase().contains("UNWANTED")) {
			return null;
		}

		return item;
	}

}