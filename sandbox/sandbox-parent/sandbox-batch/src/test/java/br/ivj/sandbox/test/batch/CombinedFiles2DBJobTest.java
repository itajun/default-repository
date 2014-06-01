package br.ivj.sandbox.test.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import br.ivj.sandbox.service.BookService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence-test.xml", "/app-context-batch-test.xml",
		"/jobs/combined-files2db-job.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		StepScopeTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@ActiveProfiles(profiles = { "development" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class CombinedFiles2DBJobTest {
	@Autowired
	private JobLauncherTestUtils jobLauncher;

	@Autowired
	private BookService bookService;

	@Autowired
	private Job job;

	@Test
	public void expectedSuccessfulImport() throws Exception {
		assertNull(bookService.findBookById(6));

		jobLauncher.setJob(job);
		JobExecution jobResult = jobLauncher.launchJob(new JobParameters());
		assertEquals(jobResult.getExitStatus(), ExitStatus.COMPLETED);
	}

}
