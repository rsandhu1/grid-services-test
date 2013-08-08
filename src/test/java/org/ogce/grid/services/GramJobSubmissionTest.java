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
