/**
 * 
 */
package net.project.view.components;


import java.util.Date;

import net.project.base.Module;
import net.project.base.URLFactory;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;

import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 *
 */
public class ShowActivityText {
	
	 @Parameter(required = true)
	 @Property
	 private PnActivityLog activityLog;
	 
	 @Parameter
	 @Property
	 private String unMarkedToken;
	 
	 @Property
	 private String personLink;
	 
	 @Property
     private String activityTime;
	 
	 @Property
	 private String objectLink;
	 
	 @Property
	 private String personName;
	 
	 @Property
	 private String personImg;
	 
	 private String jspRootURL;
	 
	 private IPnPersonService pnPersonService;
	 
	 private PnPerson pnPerson;
	 
	 private Integer imageId;
	 
	 @Inject
	 private Block document;
	 
	 @Inject
	 private Block task;
	 
	 @Inject
	 private Block form;
	 
	 @Inject
	 private Block formData;
	 
	 @Inject
	 private Block blogEntry;
	 
	 @Inject
	 private Block blogComment;
	 
	 @Inject
	 private Block wiki;
	 
	 @Inject
	 private Block news;
	 
	 @Inject
	 private Block project;
	 
	 @Inject
	 private Block activityNotPresent;
	 
	 private enum ActivityObject {
		    FORM, FORM_DATA, DOCUMENT, 
		    TASK, BLOG_ENTRY, BLOG_COMMENT, 
		    WIKI, NEWS, PROJECT, DOC_CONTAINER;
		    
			public static ActivityObject get( String v ) {
	            try {
	                return ActivityObject.valueOf( v.toUpperCase() );
	            } catch( Exception ex ) { }
	            return null;
	        }
	}
	 
	 public Object getObjectCase() {
		 if (activityLog.getTargetObjectType() != null) {
			switch (ActivityObject.get(activityLog.getTargetObjectType())) {
			case FORM:
				return form;
			case FORM_DATA:
				return formData;
			case DOCUMENT:
				return document;
			case DOC_CONTAINER:
				return document;
			case TASK:
				return task;
			case BLOG_ENTRY:
				return blogEntry;
			case BLOG_COMMENT:
				return blogComment;
			case WIKI:
				return wiki;
			case NEWS:
				return news;
			case PROJECT:
				return project;
			default:
				return activityNotPresent;
			}
		} else {
			return activityNotPresent;
		}
	 }	 
		 
	 @SetupRender
	 void setValues() {
		 try {
			 jspRootURL = SessionManager.getJSPRootURL();
			 pnPersonService = ServiceFactory.getInstance().getPnPersonService();
			 pnPerson = activityLog.getActivityBy() != null ? pnPersonService.getPesronNameAndImageIdByPersonId(activityLog.getActivityBy()) : null;
			 String objectType = (activityLog.getTargetObjectType() != null && activityLog.getTargetObjectType().equalsIgnoreCase(net.project.base.ObjectType.BLOG_ENTRY)) 
			 ? net.project.base.ObjectType.BLOG
					 : activityLog.getTargetObjectType();
			 if(activityLog.getTargetObjectId() != null && objectType != null){
				 objectLink = URLFactory.makeURL(activityLog.getTargetObjectId().toString(), objectType).replaceAll(URLFactory.AMP, URLFactory.URLAMP);
			 }
			 personLink = pnPerson != null ? jspRootURL+"/blog/view/"+pnPerson.getPersonId()+"/"+pnPerson.getPersonId()+"/person/"+Module.PERSONAL_SPACE+"?module="+Module.PERSONAL_SPACE : "";
			 activityTime = activityLog.getActivityOnDate() != null ? DateFormat.getInstance().formatDate(new Date(activityLog.getActivityOnDate().getTime()), "h:mm a").toLowerCase() : ""; 
			 personName = pnPerson != null ? pnPerson.getDisplayName() : "";
			 personImg = getPersonImage();
		 } catch (Exception e) {
			 Logger.getLogger(ShowActivityText.class).error("Error occurred while getting person details: "+e.getMessage());
		 }
	 }
	 
	 private String getPersonImage(){
		 if(pnPerson != null && pnPerson.getImageId()!=null)
			 return jspRootURL+"/servlet/photo?id="+pnPerson.getPersonId()+"&amp;size=thumbnail&module="+ Module.PROJECT_SPACE;
		 else
			 return jspRootURL+"/images/NoPicture.gif";
	 }
}
