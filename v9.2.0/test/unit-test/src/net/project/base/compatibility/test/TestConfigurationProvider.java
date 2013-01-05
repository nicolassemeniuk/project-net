/*----------------------------------------------------------------------+
|                                                                       
|     $RCSfile$
|    $Revision: 17090 $
|        $Date: 2008-03-21 22:30:31 +0530 (Fri, 21 Mar 2008) $
|      $Author: sjmittal $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.base.compatibility.test;

import java.util.HashMap;
import java.util.Map;
import net.project.base.compatibility.IConfigurationProvider;
import net.project.test.util.TestProperties;

/**
 * A overridden test that does not read sajava.ini.
 *
 * @author Tim Morrow
 * @since Version 7.7
 */
public class TestConfigurationProvider implements IConfigurationProvider {

    /**
     * Hardcoded settings (for now).
     * The settings with null values will always return the default value.
     */
    private static final Map settings = new HashMap();
    static {
        settings.put("jspRootURL", TestProperties.getInstance().getProperty("JSPRootURL"));
        settings.put("repositoryPath", TestProperties.getInstance().getProperty("filePath"));
        settings.put("smtpHost", "llama.project.net");
        settings.put("siteHost", TestProperties.getInstance().getProperty("SiteHost"));
        settings.put("siteScheme", "http://");
        settings.put("supportEmail", "support@project.net");
        settings.put("webexSite", "");
        settings.put("siteProtocol", "http://");
        settings.put("characterEncoding", "UTF-8");
        settings.put("msAccessDriver", "sun.jdbc.odbc.JdbcOdbcDriver");
        settings.put("msAccessDriverConnection", "jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=@@@filename@@@");
        settings.put("verisignServerName", "test-payflow.verisign.com");
        settings.put("verisignPortNumber", "443");
        settings.put("sslSupported", null);
        settings.put("ccTestMode", "true");
        settings.put("creditCardMerchantAccountClass", null);
        settings.put("mpdImportTempDirectory", System.getProperty("java.io.tmpdir"));
        settings.put("emailCharacterEncoding", null);
        settings.put("startupPage", null);
        settings.put("customXSLRootPath", null);
        settings.put("webDocumentRoot", "");
        settings.put("defaultLayoutTemplate", "");
        settings.put("postDataTempDir", System.getProperty("java.io.tmpdir"));
        settings.put("jdbcConnPoolSize", "20");
        settings.put("username", TestProperties.getInstance().getProperty("username"));
        settings.put("password", TestProperties.getInstance().getProperty("password"));
        settings.put("connectString", TestProperties.getInstance().getProperty("connectString"));
    }

    public String getSetting(String name) {

        if (!settings.containsKey(name)) {
            throw new IllegalArgumentException("Unknown setting with name: " + name);
        }

        return (String) settings.get(name);
    }

    public boolean isModern() {
	return false;
    }

    /**
     * Returns the default location for uploads, currently determined
     * by the system property <code>java.io.tmpdir</code>.
     * @return the default upload directory
     */
    public String getDefaultUploadTempDirectory() {
        return getSetting("postDataTempDir");
    }

    /**
     * Returns the value of <code>jdbcConnPoolSize</code> from
     * the <code>ubs</code> section of the configuration file.
     * @return the maximum connection pool size
     */
    public String getJDBCConnectionPoolMaxSize() {
        return getSetting("jdbcConnPoolSize");
    }

    /**
     * Returns the path to the "etc" directory of the installation.
     * @return
     */
    public String getFilePath() {
        return getSetting("repositoryPath");
    }
}