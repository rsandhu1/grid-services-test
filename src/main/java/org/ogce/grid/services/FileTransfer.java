package org.ogce.grid.services;

import java.net.URI;

import org.apache.log4j.BasicConfigurator;
import org.ietf.jgss.GSSCredential;
import org.ogce.grid.security.ApplicationContext;
import org.ogce.grid.utils.GridFtp;

public class FileTransfer {

	public void makeDir(GSSCredential gssCred, URI destURI) throws Exception {
		GridFtp ftp = new GridFtp();
		ftp.makeDir(destURI, gssCred);
	}

	public void transferData(GSSCredential gssCred, URI srcURI, URI destURI) throws Exception {
		GridFtp ftp = new GridFtp();
		ftp.transfer(srcURI, destURI, gssCred, true);
	}

	public void transferData(GSSCredential gssCred, String sourceERP, String remoteSrcFile, String targetERP,
			String remoteDestFile) throws Exception {
		GridFtp ftp = new GridFtp();
		URI srcURI = GridFtp.createGsiftpURI(sourceERP, remoteSrcFile);
		URI destURI = GridFtp.createGsiftpURI(targetERP, remoteDestFile);
		ftp.transfer(srcURI, destURI, gssCred, true);
	}

	public static void main(String[] args) {
		try {
			BasicConfigurator.configure();
			ApplicationContext context = new ApplicationContext();
			context.login();
			FileTransfer fileTransfer = new FileTransfer();

			String sourceERP = "";
			String remoteSrcFile = "";
			String targeterp = "";
			String remoteDestFile = "";
			URI srcURI = GridFtp.createGsiftpURI(sourceERP, remoteSrcFile);
			URI destURI = GridFtp.createGsiftpURI(targeterp, remoteDestFile);
			URI dirLocation = GridFtp.createGsiftpURI(targeterp,
					remoteDestFile.substring(0, remoteDestFile.lastIndexOf("/")));
			GSSCredential gssCredential = context.getGssCredential();
			fileTransfer.makeDir(gssCredential, dirLocation);
			fileTransfer.transferData(gssCredential, srcURI, destURI);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}