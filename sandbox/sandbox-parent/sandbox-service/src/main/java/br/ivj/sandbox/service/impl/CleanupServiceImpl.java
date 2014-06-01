package br.ivj.sandbox.service.impl;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import br.ivj.sandbox.service.CleanupService;

@Service("cleanupService")
public class CleanupServiceImpl implements CleanupService {
	private Logger logger = LoggerFactory.getLogger(CleanupServiceImpl.class);
	
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("legacyDataSource")
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Guess what? Got to uncomment it to work... Takes too much test resources if I leave it here.
	 * @Scheduled(fixedRate=1000)
	 */
	@Override
	public void performCleanup() {
		int updateQty = jdbcTemplate.update("DELETE FROM AUTHOR A WHERE A.AUTHOR_ID NOT IN (SELECT B.AUTHOR_ID FROM BOOK B)");
		if (updateQty > 0) {
			logger.info(String.format("Removed %d orphan authors.", updateQty));
		} else {
			logger.debug(String.format("Removed %d orphan authors.", updateQty));
		}
	}

}
