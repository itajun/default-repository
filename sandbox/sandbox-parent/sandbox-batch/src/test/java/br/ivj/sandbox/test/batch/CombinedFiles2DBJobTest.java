package br.ivj.sandbox.test.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/app-context-service.xml",
		"/app-context-persistence-test.xml", "/app-context-batch-test.xml",
		"/jobs/combined-files2db-job.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		StepScopeTestExecutionListener.class })
@ActiveProfiles(profiles = { "development" })
public class CombinedFiles2DBJobTest {
	/*
	 * TODO: Why am I getting duplicated bean errors when I run all tests in this package?
	 * javax.naming.NamingException: Another resource already exists with name sandboxDatabase - pick a different name
	 */
	
	@Autowired
	private JobLauncherTestUtils jobLauncher;

	@Autowired
	private Job job;

	@Test
	public void expectedSuccessfulImport() throws Exception {
		jobLauncher.setJob(job);
		jobLauncher.launchJob(new JobParameters());
	}
}
