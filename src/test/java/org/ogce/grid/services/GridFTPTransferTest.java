package org.ogce.grid.services;

import java.io.InputStream;
import java.util.Properties;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.ogce.grid.utils.ServiceConstants;

public class GridFTPTransferTest  extends AbstractJavaSamplerClient implements JavaSamplerClient {

    @Override
    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult result = new SampleResult();
        String source = "lonestar";
        String target = "stampede";
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream propertyStream = classLoader.getResourceAsStream(ServiceConstants.GRIDCLIENT_PROPERTY);
            Properties properties = new Properties();
            if (propertyStream != null) {
                properties.load(propertyStream);
                String sourceEPR = properties.getProperty(source+".gridftp.epr");
                String sourceFile = properties.getProperty(source+".gridftp.file");
                String targetEPR = properties.getProperty(target+".gridftp.epr");
                String targetFile = properties.getProperty(target+".gridftp.file");
                if(sourceEPR == null  || sourceFile == null || targetEPR == null || targetFile  == null){
                    throw new Exception("Property for source: " + source + " or target: "+ target +" is not set in" + ServiceConstants.GRIDCLIENT_PROPERTY);
                }
                if(sourceEPR.isEmpty() || sourceFile.isEmpty() || targetEPR.isEmpty() || targetFile.isEmpty()){
                    throw new Exception("Values for source or target endpoints or filelocation are not set");
                }
                GridFTPTransfer ftpTransfer = new GridFTPTransfer();
                ftpTransfer.gridFTPTransfer(sourceEPR, sourceFile, targetEPR, targetFile);
            }   
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
