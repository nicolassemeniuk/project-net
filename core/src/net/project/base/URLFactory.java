/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 package net.project.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.project.persistence.PersistenceException;
import net.project.resource.Timesheet;
import net.project.resource.TimesheetFinder;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.space.ISpaceTypes;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


public class URLFactory {

    /** main url for the Project.net website */
    public static final String PROJECT_NET_URL = SessionManager.getAppURL();

    /** root URL path for the document module */
    public static final String DOCUMENT_ROOT_PATH = "document";
    
    /** root URL path for the blog module */
    public static final String BLOG_ROOT_PATH = "blog";
    
    /** root URL path for the wiki module */
    public static final String WIKI_ROOT_PATH = "wiki";

    /** root URL path for the calendar module */
    public static final String CALENDAR_ROOT_PATH = "calendar";

    /** root URL path for the post module */
    public static final String POST_ROOT_PATH = "discussion";

    /** root URL path for the process module */
    public static final String PROCESS_ROOT_PATH = "process";

    /** root URL path for the event module */
    public static final String EVENT_ROOT_PATH = "calendar";

    /** path used for various things */
    public static final String PERSON_ROOT_PATH = "project";

    /** path used for various things */
    public static final String ROSTER_ROOT_PATH = "roster";

    /** root URL path for the business module */
    public static final String BUSINESS_ROOT_PATH = "business";

    /** root URL path for the form module */
    public static final String FORM_ROOT_PATH = "form";

    /** root URL path for the Application Space module */
    public static final String APPLICATION_SPACE_ROOT_PATH = "admin";

    /** root URL path for the Configuration Space module */
    public static final String CONFIGURATION_SPACE_ROOT_PATH = "configuration";

    /** JSP page for view deliverable */
    public static final String VIEW_DELIVERABLE_JSP = "ViewDeliverable.jsp";

    /** JSP page for document properties */
    public static final String DOCUMENT_PROPERTIES_JSP = "GoToDocument.jsp";
    
    /** Tapestry page for blog entry */
    public static final String BLOG_ENTRY_PAGE = "Show_Entry";
    
    /** Tapestry page for wiki entry */
    public static final String WIKI_ENTRY_PAGE = "page";

    /** JSP page for bookmark properties */
    public static final String BOOKMARK_PROPERTIES_JSP = "BookmarkProperties.jsp";

    /** JSP page for document properties */
    public static final String CONTAINER_VIEW_JSP = "Main.jsp";

    /** JSP page for meeting manager */
    public static final String MEETING_MANAGER_JSP = "MeetingManager.jsp";

    /** JSP page for post threadList */
    public static final String POST_PROPERTIES_JSP = "ThreadList.jsp";

    /** JSP page for schedule taskList */
    public static final String VIEW_TASK_JSP = "TaskView.jsp";

    /** JSP page for schedule taskList */
    public static final String VIEW_EVENT_JSP = "EventView.jsp";

    /** JSP page for roster member view */
    public static final String VIEW_PERSON_JSP = "MemberView.jsp";

    /** JSP page for form data properties */
    public static final String FORM_DATA_PROPERTIES_JSP = "FormEdit.jsp";

    /** JSP page for form list */
    public static final String FORM_LIST_JSP = "FormListLoad.jsp";

    /** query parameter for the id of a post */
    public static final String POST_ID_QUERY_PARAMETER = "postid";
    
    /** query parameter for the id of a post group name */
    public static final String POST_GROUP_NAME_QUERY_PARAMETER = "name";
    
    /** standardized query parameter for the id of an object */
    public static final String ID_QUERY_PARAMETER = "id";

    /** standardized query parameter for the action of an object */
    public static final String IDTYPE_QUERY_PARAMETER = "idtype";

    /** standardized query parameter for the action of an object */
    public static final String ACTION_QUERY_PARAMETER = "action";

    /** standardized query parameter for the action of an object */
    public static final String MODULE_QUERY_PARAMETER = "module";
    
    /** root URL path for the news module */
    public static final String NEWS_ROOT_PATH = "news";
    
    /** JSP page for news properties */
    public static final String NEWS_VIEW_JSP = "NewsView.jsp";
    
    /** JSP page for container properties */
    public static final String TRAVERSE_CONTAINER_JSP = "TraverseFolderProcessing.jsp";
    
    /** constant for forward slash */
    public static final String SLASH = "/";

    /** constant for equals */
    public static final String EQUALS = "=";

    /** constant for question mark */
    public static final String QUESTION_MARK = "?";

    /** constant for ampersand */
    public static final String AMP = "&amp;";
    public static final String URLAMP = "&";




    /*****************************************************************************************************************
    *****                                     CONSTRUCTORS                                                                        *****
    *****************************************************************************************************************/

    public URLFactory() {
        // do nothing
    }


    /*****************************************************************************************************************
    *****                                         public Factory Methods                                                             *****
    *****************************************************************************************************************/

    public static String makeURL (String objectID, String objectType, boolean escapeHtml) {

        String URL = null;

        if(objectType.equals (ObjectType.DOCUMENT))
            URL = makeDocumentURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.BOOKMARK))
            URL = makeBookmarkURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.MEETING))
            URL = makeMeetingURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.POST))
            URL = makePostURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.CONTAINER))
            URL = makeContainerURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.DELIVERABLE))
            URL = makeDeliverableURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.TASK))
            URL = makeTaskURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.ACTIVITY))
            URL = makeActivityURL(objectID);
        else if(objectType.equals (ObjectType.EVENT))
            URL = makeEventURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.PERSON))
            URL = makePersonURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.PROJECT))
            URL = makeProjectURL(objectID, escapeHtml);
        else if(objectType.equals (ObjectType.BUSINESS))
            URL = makeBusinessURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.FORM))
            URL = makeFormURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.FORM_DATA))
            URL = makeFormDataURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.FORM_LIST))
            URL = makeFormListURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.APPLICATION))
            URL = makeApplicationURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.CONFIGURATION))
            URL = makeConfigurationURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.BLOG))
            URL = makeBlogURL(objectID, escapeHtml);
        else if(objectType.equals(ObjectType.WIKI))
            URL = makeWikiURL(objectID);
        else if(objectType.equals(ObjectType.NEWS))
            URL = makeNewsURL(objectID, escapeHtml);
        else
            URL = PROJECT_NET_URL;

        return URL;

    } // end makeURL()

    public static String makeURL (String objectID, String objectType) {        
        return makeURL(objectID,objectType,false);        
    }
    
	private static String makeBlogURL(String objectID, boolean escapeHtml) {
		
		StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( BLOG_ROOT_PATH );
        url.append ( SLASH );
        url.append ( "view" );
        url.append ( SLASH );
        url.append ( BLOG_ENTRY_PAGE );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        
        String spaceType = SessionManager.getUser().getCurrentSpace().getSpaceType().getName().toLowerCase();
        spaceType = spaceType.equalsIgnoreCase(ISpaceTypes.APPLICATION_SPACE)
        			|| spaceType.equalsIgnoreCase(ISpaceTypes.BUSINESS_SPACE)
        			|| spaceType.equalsIgnoreCase(ISpaceTypes.CONFIGURATION_SPACE)
        			|| spaceType.equalsIgnoreCase(ISpaceTypes.METHODOLOGY_SPACE)
        			|| spaceType.equalsIgnoreCase(ISpaceTypes.RESOURCES_SPACE)
        			|| spaceType.equalsIgnoreCase("personal")
        			? ISpaceTypes.PERSONAL_SPACE : spaceType;
        url.append(net.project.base.Module.getModuleForSpaceType(spaceType));

        return url.toString();
		
	}

    
	private static String makeWikiURL(String objectID) {
        
        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( WIKI_ROOT_PATH );
        url.append ( SLASH );
        url.append ( WIKI_ENTRY_PAGE );
        url.append ( SLASH );
        url.append ( objectID );
        url.append ( QUESTION_MARK );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.PROJECT_SPACE );
        return url.toString();
    }

	public static String makeURL(String postID, String objectType, String postGroupID, String postGroupName) {

        String URL = null;

        if(objectType.equals (ObjectType.POST)) {
        	URL = makePostURL(postID, postGroupID, postGroupName);        	
        }
        

        return URL;

    } // end makeURL()
    
    
    /*****************************************************************************************************************
    *****                                         private Worker Methods                                                            *****
    *****************************************************************************************************************/

    private static String makeDocumentURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( DOCUMENT_ROOT_PATH );
        url.append ( SLASH );
        url.append ( DOCUMENT_PROPERTIES_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP);
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.DOCUMENT );


        return url.toString();

    } // end makeDocumentURL

    private static String makeContainerURL (String containerID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( DOCUMENT_ROOT_PATH );
        url.append ( SLASH );
        url.append ( TRAVERSE_CONTAINER_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( containerID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.DOCUMENT );


        return url.toString();

    } // end makeDocumentURL


    private static String makeBookmarkURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( DOCUMENT_ROOT_PATH );
        url.append ( SLASH );
        url.append ( BOOKMARK_PROPERTIES_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.DOCUMENT );


        return url.toString();

    } // end makeDocumentURL


    private static String makeMeetingURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( CALENDAR_ROOT_PATH );
        url.append ( SLASH );
        url.append ( MEETING_MANAGER_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.CALENDAR );

        return url.toString();

    } // end makeMeetingURL

    public static String makeExternalMeetingURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getAppURL() );
        url.append ( SLASH );
        url.append ( CALENDAR_ROOT_PATH );
        url.append ( SLASH );
        url.append ( MEETING_MANAGER_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.CALENDAR );

        return url.toString();

    } // end makeMeetingURL

    private static String makePostURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();
        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( POST_ROOT_PATH );
        url.append ( SLASH );
        url.append ( POST_PROPERTIES_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.DISCUSSION );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( IDTYPE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( "post" );

        return url.toString();

    } // end makePostURL
    
    private static String makePostURL (String postID, String postGroupID, String postGroupName) {

        final StringBuffer url = new StringBuffer();
        
        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( POST_ROOT_PATH );
        url.append ( SLASH );
        url.append ( POST_PROPERTIES_JSP );
        url.append ( QUESTION_MARK );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.DISCUSSION );
        url.append ( AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );
        url.append ( AMP );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( postGroupID );
        url.append ( AMP );
        url.append ( POST_ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( postID );
        /*url.append ( AMP );
        url.append ( POST_GROUP_NAME_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( postGroupName );*/

        return url.toString();

    } // end makePostURL

    private static String makeDeliverableURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( PROCESS_ROOT_PATH );
        url.append ( SLASH );
        url.append ( VIEW_DELIVERABLE_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.PROCESS );
        return url.toString();

    } // end makeDeliverableURL

    private static String makeTaskURL (String objectID, boolean escapeHtml) {
    	if(escapeHtml){
    		return SessionManager.getJSPRootURL() + "/servlet/ScheduleController/TaskView?"+
                "id="+objectID+"&action="+Action.VIEW+"&module="+Module.SCHEDULE;
    	} else {
	        return XMLUtils.escape(
	            SessionManager.getJSPRootURL() + "/servlet/ScheduleController/TaskView?"+
	            "id="+objectID+"&action="+Action.VIEW+"&module="+Module.SCHEDULE
	        );
    	}
    }
    
    private static String makeActivityURL (String objectID) {
        TimesheetFinder timesheetFinder = new TimesheetFinder();

        Timesheet timesheet = new Timesheet();;
        try {
            timesheetFinder.findByActivityID(objectID, timesheet);
        } catch (PersistenceException e) {
            Logger.getLogger(TimesheetFinder.class).debug("An unexpected SQL Exception has occurred: " + e);
            return PROJECT_NET_URL;
        }
        
        String url = 
            SessionManager.getJSPRootURL() + 
                "/servlet/AssignmentController/CurrentAssignments/CreateTimesheet?module=" + Module.RESOURCE +
                "&action=" + Action.MODIFY + "&timesheetId=" + timesheet.getID() +
                "&personID=" + SessionManager.getUser().getID() + "&startDate=" + timesheet.getStartDate().getTime();
        try {
            url += "&returnTo="+URLEncoder.encode("/servlet/AssignmentController/PersonalAssignments?&module=" + Module.PERSONAL_SPACE + "&action="+Action.VIEW, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            url += "&returnTo=/servlet/AssignmentController/PersonalAssignments";
        }
        return XMLUtils.escape(url);

    } // end makeActivityURL

    private static String makeEventURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();
        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( EVENT_ROOT_PATH );
        url.append ( SLASH );
        url.append ( VIEW_EVENT_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.CALENDAR );


        return url.toString();

    } // end makeEventURL

    private static String makePersonURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();
        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( ROSTER_ROOT_PATH );
        url.append ( SLASH );
        url.append ( VIEW_PERSON_JSP );
        url.append ( QUESTION_MARK );
        url.append ( "memberid" );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.DIRECTORY );


        return url.toString();

    } // end makeEventURL

    private static String makeProjectURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();
        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( PERSON_ROOT_PATH );
        url.append ( SLASH );
        url.append ( "ProjectRegister.jsp" );
        url.append ( QUESTION_MARK );
        url.append ( "projectid" );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.PERSONAL_SPACE );

        return url.toString();

    } // end makeProjectURL

    private static String makeBusinessURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();
        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( BUSINESS_ROOT_PATH );
        url.append ( SLASH );
        url.append ( "BusinessRegister.jsp" );
        url.append ( QUESTION_MARK );
        url.append ( "businessid" );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.PERSONAL_SPACE );

        return url.toString();

    } // end makeProjectURL

    private static String makeFormDataURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( FORM_ROOT_PATH );
        url.append ( SLASH );
        url.append ( FORM_DATA_PROPERTIES_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.FORM );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );

        return url.toString();

    } // end makeFormDataURL

    private static String makeFormListURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( FORM_ROOT_PATH );
        url.append ( SLASH );
        url.append ( FORM_LIST_JSP );
        url.append ( QUESTION_MARK );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.FORM );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ACTION_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.security.Action.VIEW );

        return url.toString();

    } // end makeFormListURL

    private static String makeApplicationURL(String objectID, boolean escapeHtml) {
        StringBuffer url = new StringBuffer();

        url.append(SessionManager.getJSPRootURL());
        url.append(SLASH);
        url.append(APPLICATION_SPACE_ROOT_PATH);
        url.append(SLASH);
        url.append("ApplicationRegister.jsp");
        url.append(QUESTION_MARK);
        url.append(ID_QUERY_PARAMETER);
        url.append(EQUALS);
        url.append(objectID);
        url.append(escapeHtml ? URLAMP : AMP);
        url.append(ACTION_QUERY_PARAMETER);
        url.append(EQUALS);
        url.append(net.project.security.Action.VIEW);
        url.append(escapeHtml ? URLAMP : AMP);
        url.append(MODULE_QUERY_PARAMETER);
        url.append(EQUALS);
        url.append(net.project.base.Module.APPLICATION_SPACE);

        return url.toString();
    } // end makeApplicationURL

    private static String makeConfigurationURL(String objectID, boolean escapeHtml) {
         StringBuffer url = new StringBuffer();

         url.append(SessionManager.getJSPRootURL());
         url.append(SLASH);
         url.append(CONFIGURATION_SPACE_ROOT_PATH);
         url.append(SLASH);
         url.append("ConfigurationRegister.jsp");
         url.append(QUESTION_MARK);
         url.append(ID_QUERY_PARAMETER);
         url.append(EQUALS);
         url.append(objectID);
         url.append(escapeHtml ? URLAMP : AMP);
         url.append(ACTION_QUERY_PARAMETER);
         url.append(EQUALS);
         url.append(net.project.security.Action.VIEW);
         url.append(escapeHtml ? URLAMP : AMP);
         url.append(MODULE_QUERY_PARAMETER);
         url.append(EQUALS);
         url.append(net.project.base.Module.CONFIGURATION_SPACE);

         return url.toString();
     } // end makeApplicationURL
    
    private static String makeFormURL(String objectID, boolean escapeHtml) {
        StringBuffer url = new StringBuffer();

        url.append(SessionManager.getJSPRootURL());
        url.append(SLASH);
        url.append(FORM_ROOT_PATH);
        url.append(SLASH);
        url.append("FormQuickAccess.jsp");
        url.append(QUESTION_MARK);
        url.append(ID_QUERY_PARAMETER);
        url.append(EQUALS);
        url.append(objectID);
        url.append(escapeHtml ? URLAMP : AMP);
        url.append(MODULE_QUERY_PARAMETER);
        url.append(EQUALS);
        url.append(net.project.base.Module.FORM);

        return url.toString();
    } // end makeFormURL
    
    private static String makeNewsURL (String objectID, boolean escapeHtml) {

        StringBuffer url = new StringBuffer();

        url.append ( SessionManager.getJSPRootURL() );
        url.append ( SLASH );
        url.append ( NEWS_ROOT_PATH );
        url.append ( SLASH );
        url.append ( NEWS_VIEW_JSP );
        url.append ( QUESTION_MARK );
        url.append ( MODULE_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( net.project.base.Module.NEWS );
        url.append ( escapeHtml ? URLAMP : AMP );
        url.append ( ID_QUERY_PARAMETER );
        url.append ( EQUALS );
        url.append ( objectID );
        
        return url.toString();

    }
}
