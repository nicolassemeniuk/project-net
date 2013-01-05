package net.project.test.acceptance.engine;

import net.project.test.acceptance.framework.PnetAcceptanceTestFramework;
import net.sourceforge.jwebunit.junit.WebTestCase;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

// Vladimir Malykhin

public class PnetTestEngine extends WebTestCase {
	//public static final String PNET_URL = "http://localhost:8080"; 
	public static final String PNET_URL = 
		TestPropertiesAcceptance.getInstance().getProperty("pnetUrl");
	
	//existing users username
	//protected static final String LOGIN_NAME = "appadmin";
	protected static final String LOGIN_NAME = 
		TestPropertiesAcceptance.getInstance().getProperty("loginName");
	//existing users password
	//protected static final String PASSWORD = "appadmin";
	protected static final String PASSWORD = 
		TestPropertiesAcceptance.getInstance().getProperty("password");
	
	private static final String CONTEXT_PATH = "/";
	
	public PnetAcceptanceTestFramework _framework;
	
 	@Override
	protected void tearDown() throws Exception {
		//_framework.getDataCleaner().removeActualObjects();
		_framework.logout();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {	
		//if (_framework == null) {
			jWebUnitSetup();
			_framework = new PnetAcceptanceTestFramework(this.getTester());
			_framework.login(LOGIN_NAME, PASSWORD);			
		//}
	}

	protected void jWebUnitSetup() {
		setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		getTestContext().setBaseUrl(PNET_URL);
					
		//getTestContext().setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");
		//getTestContext().setUserAgent("Mozilla/4.0 (Windows NT 5.1; YPC 3.0.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)");
		getTestContext().setUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; FDM; .NET CLR 2.0.50727; .NET CLR 1.1.4322)");		
		//getTestContext().setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
		//beginAt(CONTEXT_PATH);
	}
	
	public void assertImageNotPresent(String imageSrc, String imageAlt) {	
		String xpath = "//img[@src=\"" + imageSrc + "\"";
        if (imageAlt!= null) {
            xpath += " and @alt=\"" + imageAlt + "\"";
        }
        xpath += "]";
        assertElementNotPresentByXPath(xpath);
	}
	
	public void checkCheckboxWithId(String checkboxId) {
		clickElementByXPath("//input[@id=\"" + checkboxId + "\"]");
	}
	
	public String createUniqueValue(String valuePrefix) {
		return valuePrefix + System.currentTimeMillis();
		
	}

	public void clickRadioOptionWithId(String radioId) {
		clickElementByXPath("//input[@type=\"radio\" and @id=\"" + radioId + "\"]");
	}
	
	public void clickRadioOptionWithName(String radioName) {
		clickElementByXPath("//input[@type=\"radio\" and @name=\"" + radioName + "\"]");
	}	
}

/*package net.project.test.acceptance.engine;

import net.sourceforge.jwebunit.junit.WebTestCase;
import net.sourceforge.jwebunit.junit.WebTester;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;

// Vladimir Malykhin

public abstract class PnetTestEngine extends WebTestCase {
	public static final String PROTOCOL = "http://";
	public static final String HOST_NAME = "localhost";
	public static final String PORT = ":8080";
	
	public PnetTestEngine() {
		if (tester==null) {
		tester = new WebTester();
		
		setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
		getTestContext().setBaseUrl(PROTOCOL + HOST_NAME + PORT);
		
		getTestContext().setUserAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; FDM; .NET CLR 2.0.50727; .NET CLR 1.1.4322)");
		//getTestContext().setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
		
		beginAt("/");
		}
	}
	
}
*/
