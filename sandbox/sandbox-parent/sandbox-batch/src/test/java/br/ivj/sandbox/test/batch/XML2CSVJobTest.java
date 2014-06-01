package br.ivj.sandbox.test.batch;

import static org.junit.Assert.assertEquals;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence-test.xml", "/app-context-batch-test.xml",
		"/jobs/xml2csv-job.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
	StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@ActiveProfiles(profiles = { "development" })
@DirtiesContext(classMode=ClassMode.AFTER_CLASS)
public class XML2CSVJobTest {
	@Autowired
	private JobLauncherTestUtils jobLauncher;

	@Autowired
	private Job job;


	@Test
	public void expectedSuccessfulImport() throws Exception {
		jobLauncher.setJob(job);
		JobExecution jobExecution = jobLauncher.launchJob(new JobParameters());
		assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
	}
}
