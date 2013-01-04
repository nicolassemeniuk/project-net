package net.project.installer.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandleOracleInstallation {

	public static void main(String[] args) {
		try {


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void startOracleInstallation(){
		try {
			System.out.println("Oracle installation started.");
			boolean oracleInstalled = installOracle();
			System.out.println("Oracle installation ends:"+oracleInstalled);
			System.out.println("Tablespaces creation started.");
			boolean tablespacesCreated = createTablespaces();
			System.out.println("Tablespaces created:"+tablespacesCreated);
			System.out.println("Creating users.");
			boolean usersCreated = createUsers();
			System.out.println("Users created:"+usersCreated);
			System.out.println("Importing users data.");
			boolean dataImported = importData();
			System.out.println("Data imported:"+dataImported);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean installOracle() {
		try {
			File f = new File(System.getProperty("user.dir") + File.separator + "OracleXE-install.iss");
			
			StringBuffer content = new StringBuffer();
			content.append("[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-DlgOrder]\n" + 
					"Dlg0={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdWelcome-0\n" + 
					"Count=9\n" + 
					"Dlg1={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdLicense2Rtf-0\n" + 
					"Dlg2={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdComponentDialog-0\n" + 
					"Dlg3={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskTNSPort-13013\n" + 
					"Dlg4={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskMTSPort-13012\n" + 
					"Dlg5={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskHTTPPort-13014\n" + 
					"Dlg3={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskSYSPassword-13011\n" + 
					"Dlg4={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdStartCopy-0\n" + 
					"Dlg5={F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdFinish-0\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdWelcome-0]\n" + 
					"Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdLicense2Rtf-0]\n" + 
					"Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdComponentDialog-0]\n");
			content.append("szDir=").append(WizardModel.getInstance().getOracleTargetLocation()).append("\n");
			content.append("Component-type=string\n" + 
					"Component-count=1\n" + 
					"Component-0=DefaultFeature\n" + 
					"Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskTNSPort-13013]\n");
			content.append("TNSPort=").append(WizardModel.getInstance().getOraclePort()).append("\n");
			content.append("Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskMTSPort-13012]\n" + 
					"MTSPort=2030\n" + 
					"Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskHTTPPort-13014]\n");
			content.append("HTTPPort=").append("8081").append("\n");
			content.append("Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-AskSYSPassword-13011]\n");
			content.append("SYSPassword=").append(WizardModel.getInstance().getOracleSystemPassword()).append("\n");
			content.append("Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdStartCopy-0]\n" + 
					"Result=1\n" + 
					"[{F0BC0F9E-C4A8-485C-93ED-424DB9EA3F75}-SdFinish-0]\n" + 
					"Result=1\n" + 
					"bOpt1=0\n" + 
					"bOpt2=0");
	
			setContents(f, content.toString());
			
			 List<String> command = new ArrayList<String>();
			 command.add(System.getProperty("user.dir") + File.separator + "OracleXEUniv.exe");
			 command.add("/s");
			 command.add("/f1\""+System.getProperty("user.dir") + File.separator +"OracleXE-Install.iss\"");
			 command.add("/f2\""+System.getProperty("user.dir") + File.separator + "OracleSetup.log\"");
			 ProcessBuilder builder = new ProcessBuilder(command);
			 // Map<String, String> environ = builder.environment();
			 // environ.put("PATH","D:/oracle;C:/windows;C:/windows/system32;d:/jdk1.6.0_05/bin;C:/WINDOWS/system32;C:/WINDOWS;"
			 // );
			 builder.directory(new File(System.getProperty("user.dir")));
			
			 final Process godot = builder.start();
			 godot.waitFor();
			 System.out.println("Oracle installed.");
		
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
	
	public static boolean importData() {
		try {
			File f = new File(System.getProperty("user.dir") + File.separator + "import.bat");
			StringBuffer sb = new StringBuffer();
			String oracleLocation = WizardModel.getInstance().getOracleTargetLocation() + File.separator + "app" + File.separator + "oracle" + File.separator + "product"
					+ File.separator + "10.2.0" + File.separator + "server" + File.separator + "BIN";
			sb.append("SET PATH=" + oracleLocation + ";%PATH%");
			sb.append("\n");
			sb.append("SET Path=" + oracleLocation + ";%Path%");
			sb.append("\n");
			sb.append("call");
			sb.append(" imp.exe");
			sb.append(" userid=\"system/" + WizardModel.getInstance().getOracleSystemPassword() + "@" + WizardModel.getInstance().getDatabaseName() + "\"");
			sb.append(" file=\"" + System.getProperty("user.dir") + File.separator + "pnet.dmp\"");
			sb.append(" fromuser=\"pnet\"");
			sb.append(" touser=\"pnet\"");
			sb.append(" ignore=\"y\"");
			sb.append("\n");
			sb.append("call");
			sb.append(" imp.exe");
			sb.append(" userid=\"system/" + WizardModel.getInstance().getOracleSystemPassword() + "@" + WizardModel.getInstance().getDatabaseName() + "\"");
			sb.append(" file=\"" + System.getProperty("user.dir") + File.separator + "pnet_user.dmp\"");
			sb.append(" fromuser=\"pnet_user\"");
			sb.append(" touser=\"pnet_user\"");
			sb.append(" ignore=\"y\"");
			sb.append("\n");
			sb.append("exit");

			setContents(f, sb.toString());
//			try {
//				Runtime.getRuntime().exec("cmd.exe /c start " + System.getProperty("user.dir") + File.separator + "import.bat");
//			} catch (Exception e) {
//				System.out.println("prvi puca!");
//				e.printStackTrace();
//			}

			try {
				List<String> command = new ArrayList<String>();
				command.add("cmd.exe");
				command.add("/C");
				command.add("start");
				command.add("import.bat");

				ProcessBuilder builderPnet = new ProcessBuilder(command);
				Map<String, String> environPnet = builderPnet.environment();
				environPnet.put("PATH", oracleLocation);
				environPnet.put("Path", oracleLocation);
				builderPnet.directory(new File(System.getProperty("user.dir") ));

				final Process godotPnet = builderPnet.start();
				godotPnet.waitFor();

				InputStream isPnet = godotPnet.getInputStream();
				InputStreamReader isrPnet = new InputStreamReader(isPnet);
				BufferedReader brPnet = new BufferedReader(isrPnet);
				String linePnet;
				while ((linePnet = brPnet.readLine()) != null) {
					System.out.println(linePnet);
				}

			} catch (Exception e) {
				System.out.println("drugi puca!");
				e.printStackTrace();
			}
//			
//			try {
//				List<String> command = new ArrayList<String>();
//				command.add("cmd.exe");
//				command.add("/C");
//				command.add("start");
//				command.add(System.getProperty("user.dir") + File.separator + "import.bat");
//
//				ProcessBuilder builderPnet = new ProcessBuilder(command);
//				Map<String, String> environPnet = builderPnet.environment();
//				environPnet.put("PATH", oracleLocation);
//				environPnet.put("Path", oracleLocation);
//				builderPnet.directory(new File(oracleLocation));
//
//				final Process godotPnet = builderPnet.start();
//				godotPnet.waitFor();
//
//				InputStream isPnet = godotPnet.getInputStream();
//				InputStreamReader isrPnet = new InputStreamReader(isPnet);
//				BufferedReader brPnet = new BufferedReader(isrPnet);
//				String linePnet;
//				while ((linePnet = brPnet.readLine()) != null) {
//					System.out.println(linePnet);
//				}
//
//			} catch (Exception e) {
//				System.out.println("treci puca!");
//				e.printStackTrace();
//			}
			
//			 List<String> commandPnet = new ArrayList<String>();
//			 String oracleLocation = WizardModel.getInstance().getOracleTargetLocation() + File.separator + "app" + File.separator + "oracle" + File.separator + "product" + File.separator + "10.2.0" + File.separator + "server" + File.separator + "BIN";
//			 commandPnet.add("imp.exe");
//			 commandPnet.add("userid=\"system/"+WizardModel.getInstance().getOracleSystemPassword()+"@"+WizardModel.getInstance().getDatabaseName()+"\"");
//			 commandPnet.add("file=\"" + System.getProperty("user.dir") + File.separator + "pnet.dmp\"");
//			 commandPnet.add("fromuser=\"pnet\"");
//			 commandPnet.add("touser=\"pnet\"");
//			 commandPnet.add("ignore=\"y\"");
//			 ProcessBuilder builderPnet = new ProcessBuilder(commandPnet);
//			 Map<String, String> environPnet = builderPnet.environment();
//			 environPnet.put("PATH", oracleLocation);
//			 environPnet.put("Path", oracleLocation);
//			 builderPnet.directory(new File(oracleLocation));
//			
//			 final Process godotPnet = builderPnet.start();
//			 godotPnet.waitFor();
//			 
//			 InputStream isPnet = godotPnet.getInputStream();
//			InputStreamReader isrPnet = new InputStreamReader(isPnet);
//			BufferedReader brPnet = new BufferedReader(isrPnet);
//			String linePnet;
//			while ((linePnet = brPnet.readLine()) != null) {
//				System.out.println(linePnet);
//			}
//			    
//			 System.out.println("Pnet data imported.");
//			 
//			 List<String> command = new ArrayList<String>();
//			 command.add("imp.exe");
//			 command.add("userid=\"system/"+WizardModel.getInstance().getOracleSystemPassword()+"@"+WizardModel.getInstance().getDatabaseName()+"\"");
//			 command.add("file=\"pnet_user.dmp\"");
//			 command.add("fromuser=\"" + System.getProperty("user.dir") + File.separator + "pnet_user\"");
//			 command.add("touser=\"pnet_user\"");
//			 command.add("ignore=\"y\"");
//			 ProcessBuilder builder = new ProcessBuilder(command);
//			 Map<String, String> environ = builder.environment();
//			 environ.put("PATH", oracleLocation);
//			 environ.put("Path", oracleLocation);
//			 builder.directory(new File(oracleLocation));
//			
//			 final Process godot = builder.start();
//			 godot.waitFor();
//			
//			 InputStream is = godot.getInputStream();
//				InputStreamReader isr = new InputStreamReader(is);
//				BufferedReader br = new BufferedReader(isr);
//				String line;
//				while ((line = br.readLine()) != null) {
//					System.out.println(line);
//				}
//				
			 
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Connection getConnection(){
		Connection connection = null;
		try {
			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);

			// Create a connection to the database
			String serverName = "localhost";
			String portNumber = WizardModel.getInstance().getOraclePort();
			String sid = WizardModel.getInstance().getDatabaseName();
			String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
			String username = "SYSTEM";
			String password = WizardModel.getInstance().getOracleSystemPassword();
			connection = DriverManager.getConnection(url, username, password);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static boolean createTablespaces() {
		Connection connection = null;
		try {
			connection = getConnection();

			// select path from data dictionary, where tablespaces will be
			// created
			String tablespacePath = "";
			String selectTablespacePath = " select substr(name, 1, instr(name,'" + File.separator + "', -1, 1)) as tablespacePath from v$datafile ";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(selectTablespacePath);
			if (rs != null) {
				rs.next();
				tablespacePath = rs.getString("tablespacePath");
			}
			stmt.close();
			rs.close();

			String dataTablespace = "CREATE TABLESPACE data01 DATAFILE '" + tablespacePath
					+ "data01.dbf'  SIZE 300M AUTOEXTEND ON MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL AUTOALLOCATE ";

			String indexTablespace = "CREATE TABLESPACE index01 DATAFILE '" + tablespacePath
					+ "index01.dbf'  SIZE 300M AUTOEXTEND ON MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL AUTOALLOCATE ";

			String formsTablespace = "CREATE TABLESPACE forms_data DATAFILE '" + tablespacePath
					+ "forms_data_01.dbf'  SIZE 100M AUTOEXTEND ON MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL AUTOALLOCATE ";

			String formsIndexTablespace = "CREATE TABLESPACE forms_index DATAFILE '" + tablespacePath
					+ "forms_index_01.dbf'  SIZE 100M AUTOEXTEND ON MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL AUTOALLOCATE ";

			Statement dataTablespaceStmt = connection.createStatement();
			dataTablespaceStmt.executeUpdate(dataTablespace);
			dataTablespaceStmt.close();
			
			Statement indexTablespaceStmt = connection.createStatement();
			indexTablespaceStmt.executeUpdate(indexTablespace);
			indexTablespaceStmt.close();
			
			Statement formsTablespaceStmt = connection.createStatement();
			formsTablespaceStmt.executeUpdate(formsTablespace);
			formsTablespaceStmt.close();
			
			Statement formsIndexTablespaceStmt = connection.createStatement();
			formsIndexTablespaceStmt.executeUpdate(formsIndexTablespace);
			formsIndexTablespaceStmt.close();
			
			connection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static boolean createUsers() {
		Connection connection = null;
		try {
			connection = getConnection();
			String createUsersSQL = "begin EXECUTE IMMEDIATE 'create role pnet_role'; " +
									"EXECUTE IMMEDIATE 'create user pnet identified by \"pnet\" default tablespace data01 temporary tablespace temp';" +
									"EXECUTE IMMEDIATE 'grant create session to pnet';" +
									"EXECUTE IMMEDIATE 'GRANT ALTER session TO pnet';" +
									"EXECUTE IMMEDIATE 'grant create table to pnet';" +
									"EXECUTE IMMEDIATE 'grant create view to pnet';" +
									"EXECUTE IMMEDIATE 'grant create sequence to pnet';" +
									"EXECUTE IMMEDIATE 'grant create procedure to pnet';" +
									"EXECUTE IMMEDIATE 'grant create public synonym to pnet';" +
									"EXECUTE IMMEDIATE 'grant select_catalog_role to pnet';" +
									"EXECUTE IMMEDIATE 'grant create snapshot to pnet';" +
									"EXECUTE IMMEDIATE 'grant create database link to pnet';" +
									"EXECUTE IMMEDIATE 'grant create trigger to pnet';" +
									"EXECUTE IMMEDIATE 'grant create type to pnet';" +
									"EXECUTE IMMEDIATE 'grant drop public synonym to pnet';" +
									"EXECUTE IMMEDIATE 'ALTER USER pnet QUOTA UNLIMITED ON data01 QUOTA UNLIMITED ON index01';" +
									"EXECUTE IMMEDIATE 'ALTER USER pnet QUOTA UNLIMITED ON FORMS_DATA QUOTA UNLIMITED ON FORMS_INDEX';" +
									"EXECUTE IMMEDIATE 'create user pnet_user identified by \"pnet_user\" default tablespace data01 temporary tablespace temp';" +
									"EXECUTE IMMEDIATE 'grant create session to pnet_user';" +
									"EXECUTE IMMEDIATE 'grant pnet_role to pnet_user';" +
									"EXECUTE IMMEDIATE 'ALTER USER pnet_user QUOTA UNLIMITED ON FORMS_DATA QUOTA UNLIMITED ON FORMS_INDEX';" +
									"EXECUTE IMMEDIATE 'grant create table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant create any synonym to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant select any table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant insert any table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant delete any table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant update any table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant create any table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant execute any procedure to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant select any sequence to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant select_catalog_role to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant alter any table to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant create trigger to pnet_role';" +
									"EXECUTE IMMEDIATE 'grant execute any type TO pnet_role';" +
									"EXECUTE IMMEDIATE 'grant pnet_role to pnet'; end;";

			Statement createUsersStmt = connection.createStatement();
			createUsersStmt.executeUpdate(createUsersSQL);
			createUsersStmt.close();

			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
