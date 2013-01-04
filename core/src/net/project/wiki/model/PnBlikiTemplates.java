package net.project.wiki.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.StringUtils;
import net.project.wiki.ExtWikiModel;

/**
 * Class encapsulating custom templates implemented in pnet's wiki
 * @author uroslates
 *
 */
public class PnBlikiTemplates {

	// Template parameters holder
	private Map<String, String> templateParams;
	private Integer ownerObjectId;
	private String objectType;
	
	// Template names that are set as system tokens (non-translatable).
	private static String Pn_Current_Space_Name = PropertyProvider.get("prm.project.wiki.configuration.template.currentspacename");
	private static String Pn_Project_Start_Date = PropertyProvider.get("prm.project.wiki.configuration.template.projectstartdate");
	private static String Pn_Project_Schedule_Late_Tasks = PropertyProvider.get("prm.project.wiki.configuration.template.projectschedulelatetasks");
	private static String Pn_Project_Schedule_Unassigned_Tasks = PropertyProvider.get("prm.project.wiki.configuration.template.projectscheduleunassignedtasks");
	private static String Pn_Project_Schedule_Tasks_Due_This_Week = PropertyProvider.get("prm.project.wiki.configuration.template.projectscheduletasksduethisweek");
	private static String Pn_Project_Schedule_Completed_Tasks = PropertyProvider.get("prm.project.wiki.configuration.template.projectschedulecompletedtasks");
	
	// Templates List
	private String REFLIST_TEXT = "<div class=\"references-small\" {{#if: {{{colwidth|}}}| style=\"-moz-column-width:{{{colwidth}}}; -webkit-column-width:{{{colwidth}}}; column-width:{{{colwidth}}};\" | {{#if: {{{1|}}}| style=\"-moz-column-count:{{{1}}}; -webkit-column-count:{{{1}}}; column-count:{{{1}}} }};\" |}}>\n" +
										 "<references /></div><noinclude>{{pp-template|small=yes}}{{template doc}}</noinclude>\n";
	private String CITE_WEB_TEXT = "[{{{url}}} {{{title}}}]";
	private String CITE_BOOK_TEXT =  "<table cellpadding=\"2\" cellspacinsg=\"0\" width=\"100%\"> \n" +
											" <tr> <th>Author</th> <th>Publisher</th> <th>Book Title</th> </tr> \n" +
											" <tr> <td>{{{author}}}</td> <td>{{{publisher}}}</td> <td>[{{{url}}} {{{title}}}]</td> </tr> \n" +
											"</table> \n";
	private String ALERT_TEXT = "<div style=\"border: 1px solid rgb(170, 170, 170); padding: 5px; background-color: rgb(249, 249, 249); clear: both; margin-top: 1em;\">{{{content}}}</div>";
	private String PORTAL_TEXT = "";
	/** Displays current wiki object name */
	private String CURRENT_SPACE_NAME = getSpaceName();
	private String ALL_TEMPLATES = getAllSupportedTemplates();
		
	// No argument constructor
	public PnBlikiTemplates() {}

	// Method for parsing wiki templates
	public String parseTemplate(String templateName, Map<String, String> templateParameters, Integer ownerObjectId) {
		this.templateParams = templateParameters;
		this.ownerObjectId = ownerObjectId;
		setObjectType(ownerObjectId);
		
		if (templateName.equals("Reflist")) {
			return REFLIST_TEXT;
		} else if (templateName.equals("Cite_web")) {
			return CITE_WEB_TEXT;
		} else if (templateName.equals("Cite_book")) {
			return CITE_BOOK_TEXT;
		} else if (templateName.equals("Portal")) {
			return PORTAL_TEXT;
		}  else if (templateName.equals("Alert")) {
			return ALERT_TEXT;
		}  else if (templateName.equals(Pn_Current_Space_Name)) {
			return CURRENT_SPACE_NAME;
		} else if (templateName.equals(Pn_Project_Start_Date)) {
			return getProjectSpaceStartDate(ownerObjectId, objectType);
		} else if (templateName.equals(Pn_Project_Schedule_Late_Tasks)) {
			return getLateTasksCount(ownerObjectId, objectType);
		} else if (templateName.equals(Pn_Project_Schedule_Unassigned_Tasks)) {
			return getUnassignedTaksCount(ownerObjectId, objectType);
		} else if (templateName.equals(Pn_Project_Schedule_Tasks_Due_This_Week)) {
			return getTasksDueThisWeekCount(ownerObjectId, objectType);
		} else if (templateName.equals(Pn_Project_Schedule_Completed_Tasks)) {
			return getTasksCompletedCount(ownerObjectId, objectType);
		} else if (templateName.equals("Infobox")) {
			return getInfoboxTemplate(templateParameters);
		} else if (templateName.equals("All_Templates")) {
			return ALL_TEMPLATES;
		} else {
			return "";
		}
	}
	
	private String getSpaceName() {
		String spaceName = null;
		User user = SessionManager.getUser();
		String spaceId;
		if( user != null && user.getCurrentSpace() != null ) {
			spaceId = user.getCurrentSpace().getID();
			if( spaceId != null ){
		    	spaceName = user.getCurrentSpace().getName();
			}
		}
		return spaceName;
	}

	/** Displays current project start date */
	private String getProjectSpaceStartDate(Integer ownerObjectId, String objectType) {
		String result = "'''This template is available within Project Space only.'''";
		User user = SessionManager.getUser();
		if( user != null && objectType != null ) {
			DateFormat userDateFormatter = SessionManager.getUser().getDateFormatter();
			if( Space.PROJECT_SPACE.equalsIgnoreCase(objectType) ) {
				IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
				ProjectSchedule projectSchedule = projectSpaceService.getProjectSchedule(ownerObjectId);
				result = ("".equals(userDateFormatter.formatDate(projectSchedule.getActualStart()).trim())  
							? "'''no start date specified'''"
							: userDateFormatter.formatDate(projectSchedule.getActualStart()));
			}
		}		
		return result;
	}
	
	/** Displays number of current projects late tasks (as link) */
	private String getLateTasksCount(Integer ownerObjectId, String objectType) {
		String result = "'''This template is available within Project Space only.'''";
		User user = SessionManager.getUser();
		if( user != null && this.objectType != null ) {
			if( Space.PROJECT_SPACE.equalsIgnoreCase(this.objectType) ) {
				IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
				ProjectSchedule projectSchedule = projectSpaceService.getProjectSchedule(ownerObjectId);
				result = projectSchedule.getNumberOfLateTasks().toString() + " Late Task"
							+ (projectSchedule.getNumberOfLateTasks() != 1 ? "s" : "");
			}
		}
		return result;
	}

	/** Displays number of current projects completed tasks count (as link) */	
	private String getTasksCompletedCount(Integer ownerObjectId, String objectType) {
		String result = "'''This template is available within Project Space only.'''";
		User user = SessionManager.getUser();
		if( user != null && objectType != null ) {
			if( Space.PROJECT_SPACE.equalsIgnoreCase(objectType) ) {
				IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
				ProjectSchedule projectSchedule = projectSpaceService.getProjectSchedule(ownerObjectId);
//				result = "<a href=\"javascript:showReport('ltr');\" style=\"color: green;\">" + projectSchedule.getNumberOfCompletedTasks().toString() + " Late Task</a>"
				result = projectSchedule.getNumberOfCompletedTasks().toString() + " Task"
							+ (projectSchedule.getNumberOfCompletedTasks() != 1 ? "s" : "") + " Completed";
			}
		}
		return result;
	}

	/** Displays number of current projects tasks that due this week (as link) */
	private String getTasksDueThisWeekCount(Integer ownerObjectId, String objectType) {
		String result = "'''This template is available within Project Space only.'''";
		User user = SessionManager.getUser();
		if( user != null && objectType != null ) {
			if( Space.PROJECT_SPACE.equalsIgnoreCase(objectType) ) {
				IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
				ProjectSchedule projectSchedule = projectSpaceService.getProjectSchedule(ownerObjectId);
//				result = "<a href=\"javascript:showReport('tcdr');\" style=\"color: yellow;\">" + projectSchedule.getNumberOfTaskComingDue().toString() + " Task</a>"
				result = projectSchedule.getNumberOfTaskComingDue().toString() + " Task"
							+ (projectSchedule.getNumberOfTaskComingDue() != 1 ? "s" : "") + " due this week";
			}
		}
		return result;
	}

	/** Displays number of current projects unassigned tasks (as link) */
	private String getUnassignedTaksCount(Integer ownerObjectId, String objectType) {
		String result = "'''This template is available within Project Space only.'''";;
		User user = SessionManager.getUser();
		if( user != null && objectType != null ) {
			if( Space.PROJECT_SPACE.equalsIgnoreCase(objectType) ) {
				IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
				ProjectSchedule projectSchedule = projectSpaceService.getProjectSchedule(ownerObjectId);
//				result = "<a href=\"javascript:showReport('unassigned')\" style=\"color: blue;\">" + projectSchedule.getNumberOfUnassignedTasks().toString() + " Unassigned Task</a>"
				result = projectSchedule.getNumberOfUnassignedTasks().toString() + " Unassigned Task"
							+ (projectSchedule.getNumberOfUnassignedTasks() != 1 ? "s" : "");
			}
		}
		return result;
	}
	
	private String getInfoboxTemplate(Map<String, String> templateParameters) {
		String result = null;
		if( templateParameters != null ) {
			result = buildInfoboxTemplate(templateParameters);
		}
		
		return result;
	}
	
	/** Method to set objectType from passed objectId */
	private void setObjectType(Integer objectId) {
		if( objectId != null ) {
			PnObjectType objType = ServiceFactory.getInstance().getPnObjectTypeService().getObjectTypeByObjectId(objectId);
			if( objType != null ) {
				this.objectType = objType.getObjectType();
			} 
		}
	}
	
	// TODO: handle css margin (escape overlapping)
	private String buildInfoboxTemplate(Map<String, String> params) {
		// Infobox template parameters
		String NAME = "name";
		String CAPTION = "caption";
		String PROGRAMMING_LANGUAGE = "programming language";
		String WEB_SITE = "website";
		String OPERATING_SYSTEM = "operating_system";
		String GENRE = "genre";
		String LATEST_RELEASE_VERSION = "latest_release_version";
		String LATEST_RELEASE_DATE = "latest_release_date";
		String DEVELOPER = "developer";
		String LICENSE = "license";
		String LOGO = "logo";
		String SCREEN_SHOOT = "screenshot";
		
		StringBuffer str = new StringBuffer();
		str.append("<table id=\"infoboxTbl\" class=\"infobox\" style=\"width: 22em; text-align: left; font-size: 88%; line-height: 1.5em;\">");
			if( StringUtils.isNotEmpty(params.get(NAME)) ) {
				str.append("<caption class=\"summary\">").append( params.get(NAME) ).append("</caption>");
			}
		
			if( StringUtils.isNotEmpty(params.get(LOGO)) ) {
				str.append("<tr><td colspan=\"2\" style=\"text-align: center; font-size: 125%; font-weight: bold;\" align=\"center\">")
				.append("		").append( params.get(LOGO) )
				.append("</td></tr>");
			}
				
			if( StringUtils.isNotEmpty(params.get(SCREEN_SHOOT)) ) {
				str.append("<tr><td colspan=\"2\" style=\"text-align: center;\" align=\"center\">")
				.append( params.get(SCREEN_SHOOT) ).append("<br>");
				if( StringUtils.isNotEmpty(params.get(SCREEN_SHOOT)) ) {
					str.append("<span>").append( params.get(CAPTION) ).append("</span>");
				}
				str.append("</td></tr>");
			}
			
			if( StringUtils.isNotEmpty(params.get(DEVELOPER)) ) {
				str.append("<tr><th>Developed by:</th>")
				.append("<td>").append( params.get(DEVELOPER) ).append("</td>")
				.append("</tr>");
			}

			if( StringUtils.isNotEmpty(params.get(LATEST_RELEASE_DATE)) && StringUtils.isNotEmpty(params.get(LATEST_RELEASE_VERSION)) ) {
				str.append("<tr><th>Latest release:</th>")
				.append("<td><span class=\"bday\">").append( params.get(LATEST_RELEASE_VERSION) ).append("</span><span class=\"noprint\">; ").append( params.get(LATEST_RELEASE_DATE) ).append("<br></td>")
				.append("</tr>");
			}
			
			if( StringUtils.isNotEmpty(params.get(PROGRAMMING_LANGUAGE)) ) {
				str.append("<tr><th>Written in:</th>")
				.append("<td>").append( params.get(PROGRAMMING_LANGUAGE) ).append("</td>")
				.append("</tr>");
			}
			
			if( StringUtils.isNotEmpty(params.get(OPERATING_SYSTEM)) ) {
				str.append("<tr><th>OS:</th>")
				.append("<td>").append( params.get(OPERATING_SYSTEM) ).append("</td>")
				.append("</tr>");
			}
			
			if( StringUtils.isNotEmpty(params.get(OPERATING_SYSTEM)) ) {
				str.append("<tr><th>Type:</th>")
				.append("<td>").append( params.get(GENRE) ).append("</td>")
				.append("</tr>");
			}
			
			if( StringUtils.isNotEmpty(params.get(LICENSE)) ) {
				str.append("<tr><th>License:</th>")
				.append("<td>").append( params.get(LICENSE) ).append("</td>")
				.append("</tr>");
			}
			
			if( StringUtils.isNotEmpty(params.get(WEB_SITE)) ) {
				str.append("<tr><th>Website:</th>")
				.append("<td>").append( params.get(WEB_SITE) ).append("</td>")
				.append("</tr>");
			}
			str.append("</table>");
			
		return str.toString(); 
	}
	
	/** Displays all supported wiki templates */
	@SuppressWarnings("unchecked")
	private String getAllSupportedTemplates() {
		StringBuffer result = new StringBuffer();
		ExtWikiModel wikiModel = new ExtWikiModel("","");
		Set<String> templatesList = wikiModel.getTemplates() != null ? wikiModel.getTemplates() : new TreeSet<String>();
		
		// Array as register of all custom pnet wiki templates
		String[] PNET_CUSTOM_TEMPLATES = {
				Pn_Current_Space_Name, Pn_Project_Start_Date, Pn_Project_Schedule_Late_Tasks,
				Pn_Project_Schedule_Unassigned_Tasks, Pn_Project_Schedule_Tasks_Due_This_Week,
				Pn_Project_Schedule_Completed_Tasks, "Reflist", "Cite_web", "Cite_book", "Portail",
				"Alert", "Infobox", "All_Templates"
			};		 
				
		for( int i = 0; i < PNET_CUSTOM_TEMPLATES.length; i++) {
			// Add custom pnet templates
			templatesList.add(PNET_CUSTOM_TEMPLATES[i]);
		}
		
		if( templatesList != null && templatesList.size() > 0) {
			result.append("Templates: \n");
			Iterator it = templatesList.iterator();
			while( it.hasNext() ) {
				result.append(" - ").append(it.next()).append("\n");
			}
		} else {
			result.append("No templates implemented!");
		}
		
		return result.toString();
	}
}
