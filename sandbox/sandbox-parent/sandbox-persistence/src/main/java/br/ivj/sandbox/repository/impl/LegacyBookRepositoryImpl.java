package br.ivj.sandbox.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;

@Repository("legacyBookRepository")
@Transactional
public class LegacyBookRepositoryImpl implements BookRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(LegacyBookRepositoryImpl.class);

	@Autowired
	@Qualifier("legacyDataSource")
	private DataSource dataSource;

	public Book createBook(Book book) {
		// TODO: Create BusinessException and internationalize
		logger.warn("Attempt to create a book in the legacy database.");
		throw new RuntimeException("Can't create books in the legacy system.");
	}

	@Transactional(readOnly=true)
	public Book findBookById(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query("SELECT BOOK_ID, TITLE, NAME FROM BOOK B, AUTHOR A WHERE B.AUTHOR_ID = A.AUTHOR_ID AND BOOK_ID = ?", new Object[] {id}, new ResultSetExtractor<Book>() {
			public Book extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				if (!rs.next()) {
					return null;
				}
				
				Book result = new Book();
				result.setId(rs.getInt(1));
				result.setTitle(rs.getString(2));
				result.setAuthor(rs.getString(3));
				return result;
			}
			
		});
	}

	public void deleteBook(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM AUTHOR A, BOOK B WHERE A.AUTHOR_ID = B.AUTHOR_ID AND B.BOOK_ID = ?", Integer.class, id) == 0) {
			logger.debug("Deleting author since were are deleting his last book.");
			jdbcTemplate.update("DELETE FROM AUTHOR A WHERE A.AUTHOR_ID IN (SELECT AUTHOR_ID FROM BOOK B WHERE B.BOOK_ID = ?)", id);
		}
		if (jdbcTemplate.update("DELETE FROM BOOK WHERE BOOK_ID = ?", id) == 0) {
			// TODO BusinessException and internationalization
			throw new RuntimeException(String.format("Coundn't find book with id [%d] to delete.", id));
		}
	}
}
