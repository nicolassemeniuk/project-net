package net.project.util.installer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import net.project.util.FileUtils;
import scriptella.execution.EtlExecutor;

public class WebInstallerHelper {

    static private String mailFrom = "testpnet@project.net"; // To be decided yet

    static private String installPropertiesFile = "install/install.properties";

    static private String databaseConfigPropertiesFile = "install/db/dbinit.properties";

    static private String installLogFile = "install/installLog.log";

    static private String tomcatFolderName = "apache-tomcat-5.5.23";

    static private String jdkFolderName = "jdk1.5.0_09_";

    private String realTomcatPath = "";

    private boolean installerStatus;

    private boolean jdbcJarStatus;

    private boolean endorsedDirStatus;

    private boolean activationJarStatus;

    private boolean mailJarStatus;

    private boolean jcejdkDirStatus;

    private boolean jcejreDirStatus;

    private boolean contextXmlStatus;

    private boolean databaseConnectionStatus;

    private boolean webXmlStatus;

    private boolean sendEmailStatus;

    private boolean databaseSetupStatus;

    private boolean smtpSetupStatus;

    private boolean restartStatus;

    private boolean dbScriptExeStatus;

    private boolean restartAgainStatus;

    private String databaseHost;

    private String databaseName;

    private String systemUser;

    private String systemPass;

    private String oraclePath;

    private String smtpHost;

    private String smtpPort;

    private String smtpUsername;

    private String smtpPassword;

    private String errorMessage;

    private Properties prop;

    private String operatingSystem;

    public WebInstallerHelper(String realTomcatPath) {
        this.realTomcatPath = realTomcatPath;
        loadProperties();
    }

    public WebInstallerHelper() {
    }

    private void loadProperties() {
        prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(realTomcatPath + installPropertiesFile);
            prop.load(fis);
            fis.close();

            jdbcJarStatus = getBooleanProperty("step.copy.jdbc.jar");
            endorsedDirStatus = getBooleanProperty("step.copy.endorsed.dir");
            activationJarStatus = getBooleanProperty("step.copy.activation.jar");
            mailJarStatus = getBooleanProperty("step.copy.mail.jar");
            jcejdkDirStatus = getBooleanProperty("step.copy.jce.jdk.dir");
            jcejreDirStatus = getBooleanProperty("step.copy.jce.jre.dir");
            contextXmlStatus = getBooleanProperty("step.change.context.file");
            databaseConnectionStatus = getBooleanProperty("step.check.db.connection");
            sendEmailStatus = getBooleanProperty("step.send.email.test");
            webXmlStatus = getBooleanProperty("step.xml.apply.changes");
            databaseSetupStatus = getBooleanProperty("step.check.db.setup");
            dbScriptExeStatus = getBooleanProperty("step.execute.db.script");
            restartAgainStatus = getBooleanProperty("step.restart.again");
            smtpSetupStatus = getBooleanProperty("step.check.smtp.setup");
            restartStatus = getBooleanProperty("step.restart");

            databaseHost = getProperty("db.databasehost", "");
            databaseName = getProperty("db.databaseName", "");
            systemUser = getProperty("db.sysUsername", "");
            systemPass = getProperty("db.sysPassword", "");
            oraclePath = getProperty("db.oraclePath", "");

            smtpHost = getProperty("smtp.smtpHost", "");
            smtpPort = getProperty("smtp.smtpPort", "");
            smtpUsername = getProperty("smtp.smtpUsername", "");
            smtpPassword = getProperty("smtp.smtpPassword", "");

            if (jdbcJarStatus && endorsedDirStatus && activationJarStatus && mailJarStatus && jcejdkDirStatus
                    && jcejreDirStatus && contextXmlStatus && databaseConnectionStatus && webXmlStatus
                    && sendEmailStatus && databaseSetupStatus && smtpSetupStatus) {
                // if all steps are done then mark it finished
                installerStatus = true;
                
            }
            if ("/".equals(System.getProperty("file.seperator")))
                operatingSystem = "linux";
            else
                operatingSystem = "win";
        } catch (IOException e) {
            log("Unable to load properties from " + installPropertiesFile + " file", e);
        }
    }

    private boolean getBooleanProperty(String str) {
        return "1".equals(prop.getProperty(str));
    }

    private String getProperty(String str, String strReturnDefault) {
        return prop.getProperty(str, strReturnDefault);
    }

    public boolean writePropertyFile(String string, String strVal) {
        try {
            loadProperties();
            if( strVal == null)
                strVal = "";
            prop.setProperty(string, strVal);
            FileOutputStream fos = new FileOutputStream(realTomcatPath + installPropertiesFile);
            prop.store(fos, null);
            fos.close();
        } catch (IOException e) {
            log("Unable to write properties file ", e);
            return false;
        }
        return true;
    }

    public boolean changeWebXml() {
        try {
            BufferedReader rdr = new BufferedReader(new FileReader(realTomcatPath + "install/xml/web.xml"));
            BufferedWriter webWrt = new BufferedWriter(new FileWriter(realTomcatPath + "WEB-INF/web.xml"));
            while (rdr.ready()) {
                webWrt.write(rdr.readLine() + "\n");
            }
            rdr.close();
            webWrt.close();
            writePropertyFile("step.xml.apply.changes", "1");
            writePropertyFile("step.restart.again", "0");
        } catch (IOException e) {
            log("Unable to replace web.xml file : ", e);
            return false;
        }
        return true;
    }

    private String getTomcatHome() {
        if (realTomcatPath.indexOf("webapps") < 0)
            return null;

        return realTomcatPath.substring(0, realTomcatPath.indexOf("webapps"));
    }

    public boolean changeContextXml() {
        String contextContent;
        if (getTomcatHome() == null)
            return false;

        try {
            contextContent = "<Context reloadable=\"true\" > \n";
            contextContent += "<WatchedResource>WEB-INF/web.xml</WatchedResource>\n";
            contextContent += "<Resource name=\"jdbc/PnetDB\" auth=\"Container\" type=\"javax.sql.DataSource\""
                    + " username=\"pnet_user\" password=\"pnet_user\"  driverClassName=\"oracle.jdbc.OracleDriver\""
                    + " url=\"jdbc:oracle:thin:@" + databaseHost + ":1521:" + databaseName
                    + "\" maxActive=\"8\" maxIdle=\"4\"/>\n";

            contextContent += "<Resource name=\"mail/PnetSession\"  auth=\"Container\"   type=\"javax.mail.Session\""
                    + " mail.smtp.host=\"" + smtpHost + "\" mail.smtp.port=\"" + smtpPort + "\" username=\""
                    + smtpUsername + "\" password=\"" + smtpPassword + "\" />\n";

            contextContent += "</Context>";

            BufferedWriter contxtWrt = new BufferedWriter(new FileWriter(getTomcatHome() + "conf/context.xml"));
            contxtWrt.write(contextContent);
            contxtWrt.close();

            writePropertyFile("step.change.context.file", "1");
            writePropertyFile("step.restart", "0");
        } catch (IOException e) {
            log("Unable to update context.xml file ", e);
            return false;
        }
        return true;
    }

    public boolean copyJDBCjars() {
        String targetPath;
        String sourcePath;
        // step to copy jdbc jar file
        sourcePath = realTomcatPath + "install/lib/jdbc/ojdbc14.jar";
        if (getTomcatHome() == null)
            return false;
        targetPath = getTomcatHome();
        targetPath = targetPath + "common/lib/ojdbc14.jar";
        try {
            if (!new File(targetPath).exists())
                FileUtils.copy(sourcePath, targetPath);
            writePropertyFile("step.copy.jdbc.jar", "1");
        } catch (Exception e) {
            log("Unable to copy jbdc files ", e);
            return false;
        }
        return true;
    }

    public boolean copyEndorsedjars() {
        String targetPath;
        String sourcePath;
        // step to copy endorsed jar files
        sourcePath = realTomcatPath + "install/lib/endorsed";
        if (getTomcatHome() == null)
            return false;
        targetPath = getTomcatHome();
        targetPath += "common/endorsed";
        // Check if endorsed folder already exists
        File dir = new File(targetPath);
        if (!dir.exists()) {
            new File(targetPath).mkdir();
        }

        File fl = new File(sourcePath);
        String[] fls = fl.list();
        String strFl;
        if (dir.exists()) {
            try {
                for (int i = 0; i < fls.length; i++) {
                    strFl = fls[i];
                    if (strFl.indexOf(".jar") > -1) {
                        File targetfile = new File(targetPath + "/" + strFl);
                        if (!targetfile.exists()) {
                            FileUtils.copy(sourcePath + "/" + strFl, targetPath + "/" + strFl);
                        }
                    }
                }
                writePropertyFile("step.copy.endorsed.dir", "1");
            } catch (Exception e) {
                log("Unable to copy endoresed jar files  ", e);
                return false;
            }
        }
        return true;
    }

    public boolean copyMailjars() {
        boolean activationSuccess = false;
        boolean mailSuccess = false;
        String targetPath;
        String sourcePath;
        // step to copy activation.jar and mail.jar

        // copying activation jar
        sourcePath = realTomcatPath + "install/lib/activation.jar";
        if (getTomcatHome() == null)
            return false;
        targetPath = getTomcatHome() + "common/lib/activation.jar";
        try {
            if (!new File(targetPath).exists()) {
                FileUtils.copy(sourcePath, targetPath);
            }
            writePropertyFile("step.copy.activation.jar", "1");
            activationSuccess = true;
        } catch (Exception e) {
            log("Unable to copy activation.jar ", e);
        }

        // copying mail jar
        sourcePath = realTomcatPath + "install/lib/mail.jar";
        targetPath = getTomcatHome() + "common/lib/mail.jar";
        try {
            if (!new File(targetPath).exists()) {
                FileUtils.copy(sourcePath, targetPath);
            }
            writePropertyFile("step.copy.mail.jar", "1");
            mailSuccess = true;
        } catch (Exception e) {
            log("Unable to copy mail.jar  ", e);
        }
        return (activationSuccess && mailSuccess);
    }

    private String getInstallerHome() {
        if (realTomcatPath.indexOf(tomcatFolderName) < 0)
            return null;
        return realTomcatPath.substring(0, realTomcatPath.indexOf(tomcatFolderName));
    }

    public boolean copyJDKjars() {
        boolean jdkSuccess = false;
        boolean jreSuccess = false;
        String targetPath;
        String sourcePath;
        // step to configure JDK
        String installerHome = getInstallerHome();
        String jdkPath = installerHome + jdkFolderName + operatingSystem;

        if (!new File(jdkPath).exists())
            jdkPath = System.getenv("JAVA_HOME");

        String jrePath = jdkPath.replace("jdk", "jre");

        System.out.println("JAVA Path :" + jdkPath);
        System.out.println("JRE Path :" + jrePath);

        sourcePath = realTomcatPath + "install/lib/jce";
        targetPath = jdkPath + "/jre/lib/security";
        File fl = new File(sourcePath);
        String[] fls = fl.list();
        String strFl;
        try {
            for (int i = 0; i < fls.length; i++) {
                strFl = fls[i];
                if (strFl.indexOf(".jar") > -1 || strFl.indexOf(".html") > -1 || strFl.indexOf(".txt") > -1) {
                    File targetfile = new File(targetPath + "/" + strFl);
                    if (!targetfile.exists()) {
                        FileUtils.copy(sourcePath + "/" + strFl, targetPath + "/" + strFl);
                    }
                }
            }
            // on successful JDK configuration
            writePropertyFile("step.copy.jce.jdk.dir", "1");
            jdkSuccess = true;
        } catch (Exception e) {
            log("Unable to configure JDK for higher encryption ", e);
        }

        targetPath = jrePath + "/lib/security";
        try {
            writePropertyFile("step.copy.jce.jre.dir", "1");
            jreSuccess = true;
        } catch (Exception e) {
            log("Unable to configure JRE for higher encryption   ", e);
        }

        return (jdkSuccess && jreSuccess);
    }

    public boolean testMailConfiguration(String mailTo) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHost);
        Session msg_session = Session.getDefaultInstance(props, null);
        msg_session.setDebug(true);
        log("started sending email ... ");
        try {
            MimeMessage msg = new MimeMessage(msg_session);
            msg.setFrom(new InternetAddress(mailFrom));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo, false));
            msg.setSubject("Project.net test email");
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText("This is test email to check Project.net applications SMTP server is configured correctly.");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);
            msg.setContent(mp);
            msg.setSentDate(new Date());
            Transport.send(msg);
            log("Email sent successfully ...");
            writePropertyFile("step.send.email.test", "1");
        } catch (Exception e) {
            log("Unable to send test email ", e);
            return false;
        }
        return true;
    }

    public boolean testDatabaseConfiguration() {
        Connection connection = null;
        DataSource dataSrc = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            InitialContext ic = new InitialContext();
            dataSrc = (DataSource) ic.lookup("java:comp/env/jdbc/PnetDB");
        } catch (Exception e) {
            log("Unable to perform datasource lookup ", e);
        }
        try {
            connection = dataSrc.getConnection();
        } catch (Exception e) {
            log("Unable to get connection ", e);
        }
        try {
            stmt = connection.createStatement();
            String strSql = "select count(*) as languages from pn_language";
            rs = stmt.executeQuery(strSql);
            if (rs.next()) {
                writePropertyFile("step.check.db.connection", "1");
                return true;
            }

        } catch (Exception e) {
            log("Unable to verify database connection  ", e);
        } finally {
            try {
                rs.close();
                stmt.close();
                connection.close();
            } catch (Exception ex) {
            }
        }
        return false;
    }

    public void stopTomcat() {
        Runtime r = Runtime.getRuntime();
 
        String installerHome = getInstallerHome();

        String tomcat_home = getTomcatHome();
        if (!new File(tomcat_home).exists())
            tomcat_home = System.getenv("CATALINA_HOME");

        String java_home = installerHome + jdkFolderName + operatingSystem;
        if (!new File(java_home).exists())
            java_home = System.getenv("JAVA_HOME");
        java_home = java_home.replace('\\', '/');
        tomcat_home = tomcat_home.replace('\\', '/');
        
        String prog = "\"" + java_home + "/bin/" +  "java" + "\"";
        String endorsed = " -Djava.endorsed.dirs=\"" + tomcat_home + "/common/endorsed\"";
        String classpath = " -classpath \"" + java_home + "/lib/tools.jar;" + tomcat_home + "/bin/bootstrap.jar\"";
        String cat_base = " -Dcatalina.base=\"" + tomcat_home + "\"";
        String cat_home = " -Dcatalina.home=\"" + tomcat_home + "\"";
        String temp = " -Djava.io.tmpdir=\"" + tomcat_home + "/temp" + "\" ";
        String bootclass = "org.apache.catalina.startup.Bootstrap";

        String commandStop = " stop";

        String exeStop = prog + endorsed + classpath + cat_base + cat_home + temp + bootclass + commandStop;

        log("exeStop " + exeStop);
        try {
            r.exec(exeStop);
            log("Stopping tomcat");
        } catch (IOException e) {
            log("Unable to stop tomcat ", e);
        }
    }

    private void writeDBinitProperty(String string, String strVal) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(realTomcatPath + databaseConfigPropertiesFile);
        prop.load(fis);
        fis.close();

        prop.setProperty(string, strVal);
        FileOutputStream fos = new FileOutputStream(realTomcatPath + databaseConfigPropertiesFile);
        prop.store(fos, null);
        fos.close();
    }

    public boolean initDbProperty() {
        try {
            loadProperties();
            writeDBinitProperty("url", "jdbc:oracle:thin:@" + getDatabaseHost() + ":1521:" + getDatabaseName());
            writeDBinitProperty("user", getSystemUser());
            writeDBinitProperty("password", getSystemPass());
            writeDBinitProperty("PNET_BUILD_SYSTEM_ACCOUNT", getSystemUser());
            writeDBinitProperty("PNET_BUILD_SYSTEM_PASSWORD", getSystemPass());
            writeDBinitProperty("PNET_BUILD_DB_DATABASE_NAME", getDatabaseName());
            writeDBinitProperty("PNET_BUILD_DB_DATAFILE_PATH", getOraclePath() + "/" + getDatabaseName() + "/");
            writeDBinitProperty("PNET_BUILD_DB_LOG_PATH", realTomcatPath + "install/pnet_" + getDatabaseName()
                    + "_db_build.log");
            writeDBinitProperty("ADMINPATH", realTomcatPath + "install/db/9.0.0/new/");
            writeDBinitProperty("SCRIPTPATH", realTomcatPath + "install/db/9.0.0/");
            writeDBinitProperty("VERSIONPATH", realTomcatPath + "install/db/9.0.0/");
        } catch (IOException ioe) {
            log("Unable to initialise database properties file ", ioe);
            return false;
        }
        return true;
    }

    public boolean initDatabase() {
        try {
            log("Started executing database scripts");
            long startTime = new java.util.Date().getTime();
            RandomAccessFile wrt = new RandomAccessFile(realTomcatPath + "install/pnet_" + getDatabaseName()
                    + "_db_build.log", "rw");
            wrt.writeBytes("1. Creating Table Space ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/cr_tablespaces.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n2. Creating Users ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/cr_users.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n3. Loading Tables ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/tables.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n4. Creating Sequences ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/sequences.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n5. Creating Packages ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/packages.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n6. Creating Views  ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/views.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n7. Creating Triggers  ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/triggers.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n8. Compiling Objects ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/recompile_objects.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n9. Creating Synonyms  ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/synonyms.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\n10. Applying Version Changes 9.0.0 ");
            EtlExecutor.newExecutor(new File(realTomcatPath + "install/db/prm_db_patch_9.0.0.etl.xml")).execute();
            wrt.writeBytes("- Done.");

            wrt.writeBytes("\nTotal time elapsed : " + (new java.util.Date().getTime() - startTime) / 60000
                    + " minutes.");
            wrt.close();
            writePropertyFile("step.execute.db.script", "1");
            log("Finished executing database scripts ");
        } catch (Exception e) {
            errorMessage = "Error while executing data base script \n" + e.getMessage();
            log("Unable to execute all database scripts", e);
            return false;
        }
        return true;
    }

    public boolean isCompletedRestartAgain() {
        return restartAgainStatus;
    }

    public boolean isCompletedWebXML() {
        return webXmlStatus;
    }

    public boolean isCompletedDatabaseSetup() {
        return databaseSetupStatus && dbScriptExeStatus;
    }

    public boolean isCompletedSmtpSetup() {
        return smtpSetupStatus;
    }

    public boolean isCompletedContext() {
        return contextXmlStatus;
    }

    public boolean isCompletedRestart() {
        return restartStatus;
    }

    public boolean isCompletedConfigurationTest() {
        //return sendEmailStatus && databaseConnectionStatus;
        // only database connection test is mendatory and smtp can fail without the smtp server!
        return databaseConnectionStatus;
    }

    public boolean isCompletedDatabaseConnectionTest() {
        return databaseConnectionStatus;
    }

    public boolean isCompletedMailConnectionTest() {
        return sendEmailStatus;
    }

    public boolean isCompletedInstaller() {
        return installerStatus;
    }

    public boolean isCompletedJceSecurityCopy() {
        return jcejdkDirStatus && jcejreDirStatus;
    }

    public boolean isCompletedMailJarCopy() {
        return activationJarStatus && mailJarStatus;
    }

    public boolean isCompletedJdbcJarCopy() {
        return jdbcJarStatus;
    }

    public boolean isCompletedEndorsedJarCopy() {
        return endorsedDirStatus;
    }

    public boolean isCompletedJarCopyStatus() {
        return (jdbcJarStatus && endorsedDirStatus && activationJarStatus && mailJarStatus && jcejdkDirStatus && jcejreDirStatus);
    }

    public String getIndexForward() {
        if (restartAgainStatus) {
            return "finish.jsp";
        }
        if (sendEmailStatus || databaseConnectionStatus || webXmlStatus) {
            return "testconf.jsp";
        }
        if (restartStatus) {
            return "restart.jsp";
        }
        if (contextXmlStatus) {
            return "tomcatsetup.jsp";
        }
        if (smtpSetupStatus) {
            return "smtpsetup.jsp";
        }
        if (databaseSetupStatus || dbScriptExeStatus) {
            return "dbsetup.jsp";
        }
        return null;
    }

    public String getDbsetupForward() {
        if (contextXmlStatus) {
            return "tomcatsetup.jsp";
        }
        if (smtpSetupStatus) {
            return "smtpsetup.jsp";
        }
        return null;
    }

    public String getSMTPsetupForward() {
        if (contextXmlStatus) {
            return "tomcatsetup.jsp";
        }
        return null;
    }

    public boolean getActivationJarStatus() {
        return activationJarStatus;
    }

    public boolean getContextXmlStatus() {
        return contextXmlStatus;
    }

    public boolean getDatabaseConnectionStatus() {
        return databaseConnectionStatus;
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean getDatabaseSetupStatus() {
        return databaseSetupStatus;
    }

    public boolean getDbScriptExeStatus() {
        return dbScriptExeStatus;
    }

    public boolean getEndorsedDirStatus() {
        return endorsedDirStatus;
    }

    public boolean getInstallerStatus() {
        return installerStatus;
    }

    public boolean getJcejdkDirStatus() {
        return jcejdkDirStatus;
    }

    public boolean getJcejreDirStatus() {
        return jcejreDirStatus;
    }

    public boolean getJdbcJarStatus() {
        return jdbcJarStatus;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public boolean getMailJarStatus() {
        return mailJarStatus;
    }

    public String getOraclePath() {
        return oraclePath;
    }

    public Properties getProp() {
        return prop;
    }

    public String getRealTomcatPath() {
        return realTomcatPath;
    }

    public void setRealTomcatPath(String path) {
        realTomcatPath = path;
        loadProperties();
    }

    public boolean getRestartAgainStatus() {
        return restartAgainStatus;
    }

    public boolean getRestartStatus() {
        return restartStatus;
    }

    public boolean getSendEmailStatus() {
        return sendEmailStatus;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public boolean getSmtpSetupStatus() {
        return smtpSetupStatus;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getSystemPass() {
        return systemPass;
    }

    public String getSystemUser() {
        return systemUser;
    }

    public boolean getWebXmlStatus() {
        return webXmlStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void log(String error) {
        log(error, null);
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public void log(String error, Exception ex) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(realTomcatPath + installLogFile, true));
            if (ex != null) {
                System.out.println(error + " : " + ex.getMessage());
                out.write(error + " : " + ex + "\n");
                ex.printStackTrace(new PrintWriter(out, true));
            } else {
                System.out.println(error);
                out.write(error + "\n");
            }
            out.close();
        } catch (Exception ignore) {
        }
    }

}