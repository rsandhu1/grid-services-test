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

package org.ogce.grid.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.DataSourceStream;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.HostPort;
import org.globus.ftp.Marker;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.ietf.jgss.GSSCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * GridFTP tools
 */
public class GridFtp {

      public static final String GSIFTP_SCHEME = "gsiftp";
      private static final Logger log = LoggerFactory.getLogger(GridFtp.class);

    /**
     * Make directory at remote location
     * 
     * @param destURI
     * @param gssCred
     * @throws ServerException
     * @throws IOException
     */
    public void makeDir(URI destURI, GSSCredential gssCred) throws Exception {
        GridFTPClient destClient = null;
        GridFTPContactInfo destHost = new GridFTPContactInfo(destURI.getHost(), destURI.getPort());
        try {

            String destPath = destURI.getPath();
            log.info(("Creating Directory = " + destHost + "=" + destPath));

            destClient = new GridFTPClient(destHost.hostName, destHost.port);

            int tryCount = 0;
            while (true) {
                try {
                    destClient.setAuthorization(new HostAuthorization("host"));
                    destClient.authenticate(gssCred);
                    destClient.setDataChannelAuthentication(DataChannelAuthentication.SELF);

                    if (!destClient.exists(destPath)) {
                        destClient.makeDir(destPath);
                    }
                    break;
                } catch (ServerException e) {
                    tryCount++;
                    if (tryCount >= 3) {
                        throw new Exception(e.getMessage(), e);
                    }
                    Thread.sleep(10000);
                } catch (IOException e) {
                    tryCount++;
                    if (tryCount >= 3) {
                        throw new Exception(e.getMessage(), e);
                    }
                    Thread.sleep(10000);
                }
            }
        } catch (ServerException e) {
            throw new Exception("Cannot Create GridFTP Client to:" + destHost.toString(), e);
        } catch (IOException e) {
            throw new Exception("Cannot Create GridFTP Client to:" + destHost.toString(), e);
        } catch (InterruptedException e) {
            throw new Exception("Internal Error cannot sleep", e);
        } finally {
            if (destClient != null) {
                try {
                    destClient.close();
                } catch (Exception e) {
                    log.info("Cannot close GridFTP client connection");
                }
            }
        }
    }

    /**
     * Upload file from stream
     * 
     * @param destURI
     * @param gsCredential
     * @param localFile
     * @throws GfacException
     */
    public void updateFile(URI destURI, GSSCredential gsCredential, InputStream io) throws Exception {
        GridFTPClient ftpClient = null;
        GridFTPContactInfo contactInfo = new GridFTPContactInfo(destURI.getHost(), destURI.getPort());

        try {

            String remoteFile = destURI.getPath();
            log.info("The remote file is " + remoteFile);

            log.info("Setup GridFTP Client");

            ftpClient = new GridFTPClient(contactInfo.hostName, contactInfo.port);
            ftpClient.setAuthorization(new HostAuthorization("host"));
            ftpClient.authenticate(gsCredential);
            ftpClient.setDataChannelAuthentication(DataChannelAuthentication.SELF);

            log.info("Uploading file");

            ftpClient.put(remoteFile, new DataSourceStream(io), new MarkerListener() {
                public void markerArrived(Marker marker) {
                }
            });

            log.info("Upload file to:" + remoteFile + " is done");

        } catch (ServerException e) {
            throw new Exception("Cannot upload file to GridFTP:" + contactInfo.toString(), e);
        } catch (IOException e) {
            throw new Exception("Cannot upload file to GridFTP:" + contactInfo.toString(), e);
        } catch (ClientException e) {
            throw new Exception("Cannot upload file to GridFTP:" + contactInfo.toString(), e);
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.close();
                } catch (Exception e) {
                	log.info("Cannot close GridFTP client connection");
                }
            }
        }
    }

    /**
     * Upload file to remote location
     * 
     * @param destURI
     * @param gsCredential
     * @param localFile
     * @throws GfacException
     */
    public void updateFile(URI destURI, GSSCredential gsCredential, File localFile) throws Exception {
        GridFTPClient ftpClient = null;
        GridFTPContactInfo contactInfo = new GridFTPContactInfo(destURI.getHost(), destURI.getPort());
        try {

            String remoteFile = destURI.getPath();

            log.info("The local temp file is " + localFile);
            log.info("the remote file is " + remoteFile);

            log.info("Setup GridFTP Client");

            ftpClient = new GridFTPClient(contactInfo.hostName, contactInfo.port);
            ftpClient.setAuthorization(new HostAuthorization("host"));
            ftpClient.authenticate(gsCredential);
            ftpClient.setDataChannelAuthentication(DataChannelAuthentication.SELF);

            log.info("Uploading file");
            long startTime = System.currentTimeMillis();

            ftpClient.put(localFile, remoteFile, false);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.info("Upload file to:" + remoteFile + " is done in duration in ms: " + duration);

        } catch (ServerException e) {
            throw new Exception("Cannot upload file to GridFTP:" + contactInfo.toString(), e);
        } catch (IOException e) {
            throw new Exception("Cannot upload file to GridFTP:" + contactInfo.toString(), e);
        } catch (ClientException e) {
            throw new Exception("Cannot upload file to GridFTP:" + contactInfo.toString(), e);
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.close();
                } catch (Exception e) {
                	log.info("Cannot close GridFTP client connection");
                }
            }
        }
    }

    /**
     * Download File from remote location
     * 
     * @param destURI
     * @param gsCredential
     * @param localFile
     * @throws GfacException
     */
    public void downloadFile(URI destURI, GSSCredential gsCredential, File localFile) throws Exception {
        GridFTPClient ftpClient = null;
        GridFTPContactInfo contactInfo = new GridFTPContactInfo(destURI.getHost(), destURI.getPort());
        try {
            String remoteFile = destURI.getPath();

            log.info("The local temp file is " + localFile);
            log.info("the remote file is " + remoteFile);

            log.info("Setup GridFTP Client");

            ftpClient = new GridFTPClient(contactInfo.hostName, contactInfo.port);
            ftpClient.setAuthorization(new HostAuthorization("host"));
            ftpClient.authenticate(gsCredential);
            ftpClient.setDataChannelAuthentication(DataChannelAuthentication.SELF);

            log.info("Downloading file");

            ftpClient.get(remoteFile, localFile);

            log.info("Download file to:" + remoteFile + " is done");

        } catch (ServerException e) {
            throw new Exception("Cannot download file from GridFTP:" + contactInfo.toString(), e);
        } catch (IOException e) {
            throw new Exception("Cannot download file from GridFTP:" + contactInfo.toString(), e);
        } catch (ClientException e) {
            throw new Exception("Cannot download file from GridFTP:" + contactInfo.toString(), e);
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.close();
                } catch (Exception e) {
                    log.info("Cannot close GridFTP client connection");
                }
            }
        }
    }

    /**
     * Stream remote file
     * 
     * @param destURI
     * @param gsCredential
     * @param localFile
     * @return
     * @throws GfacException
     */
    public String readRemoteFile(URI destURI, GSSCredential gsCredential, File localFile) throws Exception {
        BufferedReader instream = null;
        File localTempfile = null;
        try {

            if (localFile == null) {
                localTempfile = File.createTempFile("stderr", "err");
            } else {
                localTempfile = localFile;
            }

            log.info("Loca temporary file:" + localTempfile);

            downloadFile(destURI, gsCredential, localTempfile);

            instream = new BufferedReader(new FileReader(localTempfile));
            StringBuffer buff = new StringBuffer();
            String temp = null;
            while ((temp = instream.readLine()) != null) {
                buff.append(temp);
                buff.append(System.getProperty("line.separator"));
            }

            log.info("finish read file:" + localTempfile);

            return buff.toString();
        } catch (FileNotFoundException e) {
            throw new Exception("Cannot read localfile file:" + localTempfile, e);
        } catch (IOException e) {
            throw new Exception("Cannot read localfile file:" + localTempfile, e);
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (Exception e) {
                    log.info("Cannot close GridFTP client connection");
                }
            }
        }
    }

    /**
     * Transfer data from one GridFTp Endpoint to another GridFTP Endpoint
     * 
     * @param srchost
     * @param desthost
     * @param gssCred
     * @param srcActive
     * @throws ServerException
     * @throws ClientException
     * @throws IOException
     */
    public void transfer(URI srchost, URI desthost, GSSCredential gssCred, boolean srcActive) throws Exception {
        GridFTPClient destClient = null;
        GridFTPClient srcClient = null;

        try {
            destClient = new GridFTPClient(desthost.getHost(), desthost.getPort());
            destClient.setAuthorization(new HostAuthorization("host"));
            destClient.authenticate(gssCred);
            destClient.setType(Session.TYPE_IMAGE);

            srcClient = new GridFTPClient(srchost.getHost(), srchost.getPort());
            srcClient.setAuthorization(new HostAuthorization("host"));
            srcClient.authenticate(gssCred);
            srcClient.setType(Session.TYPE_IMAGE);

            if (srcActive) {
                log.info("Set src active");
                HostPort hp = destClient.setPassive();
                srcClient.setActive(hp);
            } else {
                log.info("Set dst active");
                HostPort hp = srcClient.setPassive();
                destClient.setActive(hp);
            }

            log.info("Start transfer file from GridFTP:" + srchost.toString() + " to " + desthost.toString());

            /**
             * Transfer a file. The transfer() function blocks until the transfer is complete.
             */
            srcClient.transfer(srchost.getPath(), destClient, desthost.getPath(), false, null);
            if (srcClient.getSize(srchost.getPath()) == destClient.getSize(desthost.getPath())) {
                log.info("CHECK SUM OK");
            } else {
                log.info("****CHECK SUM FAILED****");
            }

        } catch (ServerException e) {
            throw new Exception("Cannot transfer file from GridFTP:" + srchost.toString() + " to "
                    + desthost.toString(), e);
        } catch (IOException e) {
            throw new Exception("Cannot transfer file from GridFTP:" + srchost.toString() + " to "
                    + desthost.toString(), e);
        } catch (ClientException e) {
            throw new Exception("Cannot transfer file from GridFTP:" + srchost.toString() + " to "
                    + desthost.toString(), e);
        } finally {
            if (destClient != null) {
                try {
                    destClient.close();
                } catch (Exception e) {
                    log.info("Cannot close GridFTP client connection at Desitnation:" + desthost.toString());
                }
            }
            if (srcClient != null) {
                try {
                    srcClient.close();
                } catch (Exception e) {
                    log.info("Cannot close GridFTP client connection at Source:" + srchost.toString());
                }
            }
        }
    }
    public static URI createGsiftpURI(String host, String localPath) throws URISyntaxException {
        StringBuffer buf = new StringBuffer();
        if (!host.startsWith("gsiftp://"))
            buf.append("gsiftp://");
        buf.append(host);
        if (!host.endsWith("/"))
            buf.append("/");
        buf.append(localPath);
        return new URI(buf.toString());
    }
    /**
     * converts time (in milliseconds) to human-readable format
     *  "<dd:>hh:mm:ss"
     */
    public static String millisToShortDHMS(long duration) {
      String res = "";
      long days  = TimeUnit.MILLISECONDS.toDays(duration);
      long hours = TimeUnit.MILLISECONDS.toHours(duration)
                     - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
      long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                       - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
      long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                     - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
      if (days == 0) {
        res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
      }
      else {
        res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
      }
      return res;
    }
}
