package org.ogce.grid.services;

import org.globus.gram.GramJob;
import org.ietf.jgss.GSSCredential;
import org.ogce.grid.services.listener.JobSubmissionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GramJobSubmission {

	private static final Logger log = LoggerFactory.getLogger(GramJobSubmission.class);

	public void executeJob(GSSCredential gssCred, ExectionContext appExecContext) throws Exception {

			String contact = appExecContext.getHost();
			String rsl = appExecContext.getRSL();

			GramJob job = new GramJob(rsl);
			
			log.info("RSL = " + rsl);
			JobSubmissionListener listener = new JobSubmissionListener(job);
			job.setCredentials(gssCred);
			job.addListener(listener);
			log.info("Request to contact:" + contact);			
			job.request(contact);

			log.info("JobID = " + job.getIDAsString());
			
			listener.waitFor();
			job.removeListener(listener);
	}
}
