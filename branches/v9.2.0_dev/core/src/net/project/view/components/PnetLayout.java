/**
 * 
 */
package net.project.view.components;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

/**
 *
 */
//@IncludeStylesheet("context:styles/global.css, context:styles/fonts.css, context:styles/noframes.css, context:src/extjs/resources/css/ext-all.css")
//@IncludeJavaScriptLibrary("context:scripts/global.js")
public class PnetLayout {
	
	@Property
	@Parameter(required = false)
	private String title;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.MESSAGE)
	private String heading;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.MESSAGE)
	private String menu;

	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String bodyId;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String callOnLoad;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private boolean isViewPage;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String activityServletUrl;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private Boolean showNavigationBar;
	
	@Property
	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String space;

	@SetupRender
	void setValues(){
		if(title == null) {
			title = PropertyProvider.get("prm.global.application.title");
		}
		if(showNavigationBar == null){
			showNavigationBar = true;
		}
	}
	
	/**
	 * @return jSPRootURL
	 */
	public String getJSPRootURL() {
		return SessionManager.getJSPRootURL();
	}
	
	/**
	 * @return versionNumber
	 */
	public String getVersionNumber() {
		return StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());
	}
	
	public User getUser() {
		return SessionManager.getUser();
	}

}
