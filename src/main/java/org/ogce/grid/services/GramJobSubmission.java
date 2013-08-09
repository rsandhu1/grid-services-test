/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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
