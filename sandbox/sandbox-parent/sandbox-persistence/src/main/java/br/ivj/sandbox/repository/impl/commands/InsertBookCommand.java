package br.ivj.sandbox.repository.impl.commands;

import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;

public class InsertBookCommand extends SqlUpdate {
	private static final String SQL_INSERT_BOOK = "INSERT INTO BOOK(TITLE, AUTHOR) VALUES (:TITLE, :AUTHOR)";

	public InsertBookCommand(DataSource dataSource) {
		super(dataSource, SQL_INSERT_BOOK);
		super.declareParameter(new SqlParameter("TITLE", Types.VARCHAR));
		super.declareParameter(new SqlParameter("AUTHOR", Types.VARCHAR));
		super.setGeneratedKeysColumnNames(new String[] { "BOOK_ID" });
		super.setReturnGeneratedKeys(true);
	}
}