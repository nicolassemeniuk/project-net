/*----------------------------------------------------------------------+
 |                                                                       
 |     $RCSfile$
 |    $Revision: 17087 $
 |        $Date: 2008-03-21 21:17:04 +0530 (Fri, 21 Mar 2008) $
 |      $Author: sjmittal $
 |                                                                       
 +----------------------------------------------------------------------*/
package net.project.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Provides a mechanism for reading the mockobjects properties file (which is really a Test properties file).
 * 
 * @author Tim Morrow
 * @since Version 7.6.3
 */
public class TestProperties {

	private static final Properties propertyFile;
	static {
        try {
            propertyFile = new Properties();
            ResourceBundle prop = ResourceBundle.getBundle("test");
            Enumeration keys = prop.getKeys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = prop.getString(key);
                propertyFile.put(key, value);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize properties file: " + e);
        }
    }

	/**
     * Returns a handle to the TestProperties.
     * 
     * @return the TestProperties
     */
	public static TestProperties getInstance() {
		return new TestProperties();
	}

	/**
	 * Creates an empty TestProperties.
	 */
	private TestProperties() {
		// Do nothing
	}

	/**
	 * Returns the property for the specified name.
	 * 
	 * @param name
	 *            the name of the property to get
	 * @return the value or null if no property with that value is found
	 */
	public String getProperty(String name) {
		return propertyFile.getProperty(name);
	}
}
