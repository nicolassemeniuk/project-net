package net.project.test.acceptance.engine;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class TestPropertiesAcceptance {

	private static TestPropertiesAcceptance testPropertiesAcceptanceInstance = 
		new TestPropertiesAcceptance();
	
	private static final Properties propertyFile;
	
	static {
       try {
           propertyFile = new Properties();
           ResourceBundle prop = ResourceBundle.getBundle("testacceptance");
           Enumeration<String> keys = prop.getKeys();
           while (keys.hasMoreElements()) {
               String key = keys.nextElement();
               String value = prop.getString(key);
               propertyFile.put(key, value);
           }
       } catch (Exception e) {
           throw new RuntimeException("Unable to initialize properties file: " + e);
       }
   }

	/**
    * Returns a handle to the TestPropertiesAcceptance.
    * 
    * @return the TestPropertiesAcceptance
    */
	public static final TestPropertiesAcceptance getInstance() {
		return testPropertiesAcceptanceInstance;
	}

	/**
	 * Creates an empty TestPropertiesAcceptance.
	 */
	private TestPropertiesAcceptance() {
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
