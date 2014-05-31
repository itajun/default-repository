package br.ivj.sandbox.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import br.ivj.sandbox.entity.Book;
import br.ivj.sandbox.repository.BookRepository;
import br.ivj.sandbox.repository.impl.commands.InsertBookCommand;

@Repository
public class BookRepositoryImpl implements BookRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(BookRepositoryImpl.class);

	@Autowired
	private DataSource dataSource;

	public Book createBook(Book book) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TITLE", book.getTitle());
		paramMap.put("AUTHOR", book.getAuthor());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		new InsertBookCommand(dataSource).updateByNamedParam(paramMap,
				keyHolder);
		book.setId(keyHolder.getKey().intValue());

		logger.debug(String.format("Created book: %s", book.toString()));

		return book;
	}

	public Book findBookById(Integer id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.query("SELECT BOOK_ID, TITLE, AUTHOR FROM BOOK WHERE BOOK_ID = ?", new Object[] {id}, new ResultSetExtractor<Book>() {
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
}
