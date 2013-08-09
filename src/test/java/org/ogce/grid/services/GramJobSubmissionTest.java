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

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.ogce.grid.security.ApplicationContext;

public class GramJobSubmissionTest extends AbstractJavaSamplerClient implements JavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult result = new SampleResult();
        try {
            ApplicationContext context = new ApplicationContext();
            context.login();
            String resource = "stampede";
            ExectionContext execontext = new ExectionContext(resource);
            
            GramJobSubmission gramJobSubmittion = new GramJobSubmission();

            gramJobSubmittion.executeJob(context.getGssCredential(), execontext);
            result.setSuccessful(true);
            result.setResponseCodeOK();
            result.setResponseMessageOK(); 
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccessful(false);
        }
        return result;
    }

}
