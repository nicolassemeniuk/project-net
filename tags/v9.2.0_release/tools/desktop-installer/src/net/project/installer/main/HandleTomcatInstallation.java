package net.project.installer.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class HandleTomcatInstallation {
	
	public static void main(String[] args){
		try {
			updateExistingTomcatInstallation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void startTomcatInstallation(){
		try {
			boolean contextFileAdded = addContextFileToWAR();
			boolean extractTomcat = extractTomcatToDefaultLocation();
			boolean copyApplication = copyWARToWebapps();
			boolean setEvnVariavles = setEvnVariables();
			boolean startTomcat = startTomcat();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updateExistingTomcatInstallation(){
		try {
			System.out.println("Adding context file to WAR!");
			boolean contextFileAdded = addContextFileToWAR();
			System.out.println("Added:"+contextFileAdded);
			System.out.println("Validating Tomcat location!");
			boolean validateLocation = validateTomcatLocation();
			System.out.println("Validated:"+validateLocation);
			System.out.println("Deploying application on Tomcat!");
			boolean copyApplication = copyWARToWebapps();
			System.out.println("Deployed:"+copyApplication);
			System.out.println("Creating Tomcat Service!");
			boolean createTomcatService = createTomcatService();
			System.out.println("Service created:"+createTomcatService);
			System.out.println("Starting Tomcat!");
			boolean startTomcat = startTomcat();
			System.out.println("Started:"+startTomcat);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean validateTomcatLocation() {
		try {
			File bin = new File(WizardModel.getInstance().getTomcatHomeLocation() + File.separator + "bin");
			File conf = new File(WizardModel.getInstance().getTomcatHomeLocation() + File.separator + "conf");
			File webapps = new File(WizardModel.getInstance().getTomcatHomeLocation() + File.separator + "webapps");
			if (!(bin.isDirectory() && conf.isDirectory() && webapps.isDirectory())) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean createTomcatService() {
		try {
			List<String> command = new ArrayList<String>();
			if (System.getProperty("os.name").startsWith("Windows")) {

				File f = new File(System.getProperty("user.dir") + File.separator + "createService.bat");
				StringBuffer sb = new StringBuffer();
				sb.append("SET JAVA_HOME=" + System.getProperty("java.home") + "\n");
				sb.append("SET CATALINA_HOME=" + WizardModel.getInstance().getTomcatHomeLocation() + "\n");
				sb.append("call " + WizardModel.getInstance().getTomcatHomeLocation() + File.separator + "bin" + File.separator + "service.bat install\n");
				sb.append("exit");

				setContents(f, sb.toString());

				command.add("cmd.exe");
				command.add("/C");
				command.add("createService.bat");

				ProcessBuilder builderPnet = new ProcessBuilder(command);
				builderPnet.directory(new File(System.getProperty("user.dir")));

				final Process godotPnet = builderPnet.start();
				godotPnet.waitFor();

				InputStream isPnet = godotPnet.getInputStream();
				InputStreamReader isrPnet = new InputStreamReader(isPnet);
				BufferedReader brPnet = new BufferedReader(isrPnet);
				String linePnet;
				while ((linePnet = brPnet.readLine()) != null) {
					System.out.println(linePnet);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static boolean startTomcat() {
		try {
			List<String> command = new ArrayList<String>();
			if(System.getProperty("os.name").startsWith("Windows")){
				command.add("cmd.exe");
				command.add("/C");
				command.add("net");
				command.add("start");
				command.add("\"Tomcat6\"");
			}else{
				command.add("/bin/sh");
				command.add( "-c");
				command.add(WizardModel.getInstance().getTomcatHomeLocation() + File.separator + "bin" + File.separator +"./catalina.sh run");
			}
				
			ProcessBuilder builder = new ProcessBuilder(command);
			Map<String, String> environ = builder.environment();
			environ.put("CATALINA_HOME", WizardModel.getInstance().getTomcatHomeLocation());
			environ.put("JAVA_HOME", System.getProperty("java.home"));
			builder.directory(new File(System.getProperty("user.dir")));

		    final Process process = builder.start();
		    InputStream is = process.getInputStream();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader br = new BufferedReader(isr);
		    String line;
		    while ((line = br.readLine()) != null) {
		      System.out.println(line);
		    }
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void setContents(File aFile, String aContents) throws FileNotFoundException, IOException {
		if (aFile == null) {
			throw new IllegalArgumentException("File should not be null.");
		}
		// if (!aFile.exists()) {
		// throw new FileNotFoundException("File does not exist: " + aFile);
		// }
		// if (!aFile.isFile()) {
		// throw new IllegalArgumentException("Should not be a directory: " +
		// aFile);
		// }
		// if (!aFile.canWrite()) {
		// throw new IllegalArgumentException("File cannot be written: " +
		// aFile);
		// }

		// use buffering
		Writer output = new BufferedWriter(new FileWriter(aFile));
		try {
			// FileWriter always assumes default encoding is OK!
			output.write(aContents);
		} finally {
			output.close();
		}
	}
	
	private static boolean setEvnVariables() {
		try {
		
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static boolean copyWARToWebapps() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "ROOT.war"));
			fos = new FileOutputStream(new File(WizardModel.getInstance().getTomcatHomeLocation() + File.separator + "webapps" + File.separator + "ROOT.war"));

			byte[] buf = new byte[1024];
			int len;
			while ((len = fis.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
				}
			}
		}
		return true;
	}
	
	private static boolean extractTomcatToDefaultLocation() {
		try {
		
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean addContextFileToWAR() {
		try {
			File f = new File(System.getProperty("user.dir") + File.separator + "context.xml");

			String mailServerCredentials = "			 mail.user=\"" + WizardModel.getInstance().getMailServerUsername() + "\"\n" + 
										   "			 mail.password=\"" + WizardModel.getInstance().getMailServerPassword() + "\"\n" ;
			if(WizardModel.getInstance().getMailServerUsername() == null){
				mailServerCredentials = "";
			}
			String context = "<Context debug=\"5\" reloadable=\"true\" crossContext=\"true\">\n\n" + 
							"<Resource name=\"jdbc/PnetDB\" auth=\"Container\"\n" + 
							"           type=\"javax.sql.DataSource\" username=\"pnet_user\" password=\"pnet_user\"\n" + 
							"           driverClassName=\"oracle.jdbc.OracleDriver\" url=\"jdbc:oracle:thin:@localhost:"+ WizardModel.getInstance().getOraclePort() +":" + WizardModel.getInstance().getDatabaseName() + "\"\n" + 
							"           maxActive=\"8\" maxIdle=\"4\"/>\n\n" + 
							"<Resource name=\"mail/PnetSession\" auth=\"Container\"\n" + 
							"           type=\"javax.mail.Session\"\n" + 
							mailServerCredentials +
							"			 mail.smtp.host=\"" + WizardModel.getInstance().getMailServerHostName() + "\"/>\n\n" + 
							"</Context>";

			Writer output = new BufferedWriter(new FileWriter(f));
			try {
				// FileWriter always assumes default encoding is OK!
				output.write(context);
			} finally {
				output.close();
			}

			File files[] = new File[1];
			files[0] = f;
			addFilesToExistingZip(new File(System.getProperty("user.dir") + File.separator + "ROOT.war"), files);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void addFilesToExistingZip(File zipFile, File[] files) throws IOException {
		// get a temp file
		File tempFile = File.createTempFile(zipFile.getName(), null);
		// delete it, otherwise you cannot rename your existing zip to it.
		tempFile.delete();

		boolean renameOk = zipFile.renameTo(tempFile);
		if (!renameOk) {
			throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			boolean notInFiles = true;
			for (File f : files) {
				if (f.getName().equals(name)) {
					notInFiles = false;
					break;
				}
			}
			if (notInFiles) {
				// Add ZIP entry to output stream.
				// if the file is named context.xml, skip it
				if (!("META-INF/context.xml").equals(name)) {
					out.putNextEntry(new ZipEntry(name));
					// Transfer bytes from the ZIP file to the output file
					int len;
					while ((len = zin.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				}
			}
			entry = zin.getNextEntry();
		}
		// Close the streams
		zin.close();
		// Compress the files
		for (int i = 0; i < files.length; i++) {
			InputStream in = new FileInputStream(files[i]);
			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry("META-INF/" + files[i].getName()));
			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			// Complete the entry
			out.closeEntry();
			in.close();
		}
		// Complete the ZIP file
		out.close();
		tempFile.delete();
	}
	
	public static boolean extractTomcat(){
		try {
			int BUFFER = 2048;
				BufferedOutputStream dest = null;
				FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + File.separator + "apache-tomcat-6.0.20.zip");
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					System.out.println("Extracting: " + entry);
					int count;
					byte data[] = new byte[BUFFER];
					// write the files to the disk
					FileOutputStream fos = new FileOutputStream(new File(WizardModel.getInstance().getTomcatHomeLocation() + File.separator + entry.getName()));
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
				zis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
