/**
 * 
 */
package net.project.view.pages.timesheet;

import java.net.URL;
import java.util.Date;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.channel.ScopeType;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;
import net.project.resource.PersonPropertyGlobalScope;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.InvalidDateException;
import net.project.view.pages.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * 
 *
 */
public class PersonalizeTimesheet extends BasePage{
	
	private static Logger log;
	
	@Property
	private String errorAlertTitle;
	
	@Property
	private boolean isWeeklytotal = false;
	
	@Property
	private boolean isProjectTotal = false;
	
	@Property
	private String isBlogOnRight ="BlogOnRight";
	
	@Property
	private String isBlogInBottom ="BlogInBottom";
	
	@Property
	private String position;
	
	private PersonProperty property;
	
	@Property
	private boolean checkedWeeklyTotal;  
	
	@Property
	private boolean isActualWork;
	
	@Property
	private boolean isPercentComplete;
	
	private Long startDate;
	
	@Persist
	private String userId;
	
	@Persist
	private String assignmentStatus;
	
	Object onActivate(){
    	Object isValidAccess;
    	isValidAccess = checkForUser();
    	if(isValidAccess != null){
    		return isValidAccess;
    	}
		property = new PersonProperty();
		log = Logger.getLogger(PersonalizeTimesheet.class);
		property.setScope(new PersonPropertyGlobalScope(getUser()));
		String[] properties = property.get("prm.resource.timesheet.weeklytotalcolumn.isenabled", "weeklyTotal");
		isWeeklytotal = properties.length == 0 ? false : Integer.parseInt(properties[0]) == 1;
		
		String[] projectTotalProperty = property.get("prm.resource.timesheet.projecttotalcolumn.isenabled", "projectTotal");
		isProjectTotal = projectTotalProperty.length == 0 ? false : Integer.parseInt(projectTotalProperty[0]) == 1;
		
		String[] blogPositionProperty = property.get("prm.resource.timesheet.blogpostion.isenabled", "blogPosition");
		if(blogPositionProperty.length == 0){
			position = isBlogOnRight; 
		}else{
			position = Integer.parseInt(blogPositionProperty[0]) == 1 ? isBlogOnRight : isBlogInBottom ;
		}
		if(StringUtils.isNotEmpty(getRequest().getParameter("isActualWork"))) {
			if(getRequest().getParameter("isActualWork").length() >= 4){
				isActualWork = Boolean.valueOf(getRequest().getParameter("isActualWork").substring(0,4)).booleanValue();	
			}
		}
		if(StringUtils.isNotEmpty(getRequest().getParameter("isPercentComplete"))) {
			if(getRequest().getParameter("isPercentComplete").length() >= 4){
				isPercentComplete = Boolean.valueOf(getRequest().getParameter("isPercentComplete").substring(0,4)).booleanValue();	
			}
		}
		if(StringUtils.isNotEmpty(getRequest().getParameter("personId"))) {
			userId = getRequest().getParameter("personId").substring(0,getRequest().getParameter("personId").indexOf("?"));
		}
		if(StringUtils.isNotEmpty(getRequest().getParameter("assignmentStatus"))) {
			assignmentStatus = getRequest().getParameter("assignmentStatus");
		}
		if(getRequest().getSession(true).getAttribute("startOfWeek") != null) {
			try {
				startDate = DateFormat.getInstance().parseDateString(DateFormat.getInstance().formatDate((Date)getRequest().getSession(true).getAttribute("startOfWeek"))).getTime();
			} catch (InvalidDateException pnetEx) {
				log.error("Error occured while parsing the date.."+pnetEx.getMessage());
			}
		}
		return null;
	}
	
	
	/**
	 *save user settings 
	 */
	Object onActivate(String prm) {
		PersonProperty property = PersonProperty.getFromSession(getSession());
		property.setScope(ScopeType.GLOBAL.makeScope(SessionManager.getUser()));
		try {
			property.replace("prm.resource.timesheet", getParameter("property"), getParameter("value"));
		} catch (PersistenceException e) {
			log.getLogger(PersonalizeTimesheet.class).error("Error occured while saving user settings" + e.getMessage());
		}
		return new TextStreamResponse("text/plain", "success"); 
	}
	
	
	/**
	 * called on submit button
	 * 
	 * @return weekly assignment page
	 */
	Object onAction(){
		URL url = null;
		
		//set weekly total column
		setWeeklyTotal();
		
		//set project total column
		setProjectTotal();
		
		//set blog position (right/bottom)
		setBlogPosition();
		
		//return url for parent page
		try {
			url = new URL(SessionManager.getAppURL()
					+ "/servlet/AssignmentController/CurrentAssignments/Update?module=" + Module.PERSONAL_SPACE
					+ "&action=" + Action.MODIFY + "&isFromTimeSheet=true&startDate="
					+ startDate + "&isEditMode="
					+ PropertyProvider.getBoolean("prm.resource.timesheet.editmode.isenabled") + "&isActualWork="
					+ isActualWork + "&isPercentComplete=" + isPercentComplete + "&personId="+ userId + "&assignmentStatus="+assignmentStatus);
		} catch (Exception e) {
			log.error("Error occured while returning to weekly assignment.."+e.getMessage());
		}
		
		return url;
	}
	
	/**
	 * set blog position on right or bottom depending on property value
	 */
	public void setBlogPosition(){
		try{
			if(StringUtils.isNotEmpty(position)) {
				if(position.equalsIgnoreCase("BlogOnRight")){
					saveAndReplaceTokenValue("prm.resource.timesheet.blogpostion.isenabled", "blogPosition", "1");
				} else {
					saveAndReplaceTokenValue("prm.resource.timesheet.blogpostion.isenabled", "blogPosition", "0");
				}
			}
		}catch(Exception e){
			log.error("Error occured while setting blog property values : "+e.getMessage());
		}
	}
	
	/**
	 * display weekly total column on token value
	 */
	public void setWeeklyTotal(){
		try{
			if(isWeeklytotal){
				saveAndReplaceTokenValue("prm.resource.timesheet.weeklytotalcolumn.isenabled", "weeklyTotal", "1");	
			} else {
				saveAndReplaceTokenValue("prm.resource.timesheet.weeklytotalcolumn.isenabled", "weeklyTotal", "0");
			}
		}catch(Exception e){
			log.error("Error occured while setting weekly total property values : "+e.getMessage());
		}
	}
	
	/**
	 * display project total column on token value
	 */
	public void setProjectTotal(){
		try{
			if(isProjectTotal){
				saveAndReplaceTokenValue("prm.resource.timesheet.projecttotalcolumn.isenabled", "projectTotal", "1");
			}else{
				saveAndReplaceTokenValue("prm.resource.timesheet.projecttotalcolumn.isenabled", "projectTotal", "0");
			}
		}catch(Exception e){
			log.error("Error occured while setting project total property values : "+e.getMessage());
		}
	}
	
	
	
	/**
	 * save token value in personProperty if not saved otherwise replace
	 * @param context
	 * @param tokenProperty
	 * @param value
	 */
	public void saveAndReplaceTokenValue(String context, String tokenProperty, String value){
		String[] properties = property.get(context,tokenProperty);
		try{
			if (properties.length == 0) {
				property.put(context, tokenProperty, value);
			}else{
				property.replace(context, tokenProperty, value);
			}
		}catch(Exception e){
			log.error("Error occured while setting property values : "+e.getMessage());
		}
	}
		
}
