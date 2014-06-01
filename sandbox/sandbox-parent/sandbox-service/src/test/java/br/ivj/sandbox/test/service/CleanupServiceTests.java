package br.ivj.sandbox.test.service;

import static org.junit.Assert.assertEquals;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import br.ivj.sandbox.service.CleanupService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service-test.xml",
		"/app-context-persistence-test.xml" })
@ActiveProfiles(profiles = { "development" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection = "legacyJdbcDataSource")
public class CleanupServiceTests {
	@Spy
	private JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("cleanupService")
	@InjectMocks
	private CleanupService cleanupService;

	@Autowired
	@Qualifier("legacyDataSource")
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = Mockito.spy(new JdbcTemplate(dataSource));
	}

	@Test
	@DatabaseSetup("/datasets/authors-orphan.xml")
	public void hasJobRunTwiceIn2500msAndAuthorsDeleted()
			throws InterruptedException {
		MockitoAnnotations.initMocks(this);
		Thread.sleep(2500);
		Mockito.verify(jdbcTemplate, Mockito.atLeast(1)).update(
				Mockito.anyString());
		Mockito.verify(jdbcTemplate, Mockito.atMost(2)).update(
				Mockito.anyString());
		assertEquals(
				"Orphan authors weren't deleted",
				jdbcTemplate
						.queryForObject(
								"SELECT COUNT(*) FROM AUTHOR WHERE AUTHOR_ID NOT IN (SELECT AUTHOR_ID FROM BOOK)",
								Integer.class), new Integer(0));
	}

}
