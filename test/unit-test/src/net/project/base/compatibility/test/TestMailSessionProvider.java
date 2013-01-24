package net.project.base.compatibility.test;

import java.util.Properties;
import javax.mail.Session;

import net.project.base.compatibility.IMailSessionProvider;
import net.project.base.compatibility.Compatibility;

/**
 * Provides a mail session configured from Bluestone properties file.
 * <p>
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class TestMailSessionProvider implements IMailSessionProvider {

    /** Property name for specifying SMTP host. */
    private static final String HOST_PROPERTY_NAME = "mail.smtp.host";

    /**
     * Returns a mail session configured from the properties file.
     * @return the mail session
     * @see net.project.security.SessionManager#getSmtpHost()
     */
    public Session getSession() {
        String smtpHost = Compatibility.getConfigurationProvider().getSetting(HOST_PROPERTY_NAME);

        Properties properties = new Properties();
        properties.put(HOST_PROPERTY_NAME, smtpHost);
        return Session.getDefaultInstance(properties, null);
    }

}
