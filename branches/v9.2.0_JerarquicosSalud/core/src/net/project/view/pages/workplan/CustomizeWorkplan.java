/**
 * 
 */
package net.project.view.pages.workplan;

import java.util.ArrayList;
import java.util.List;

import net.project.base.Module;
import net.project.channel.ScopeType;
import net.project.persistence.PersistenceException;
import net.project.resource.PersonProperty;
import net.project.schedule.ScheduleColumn;
import net.project.security.SessionManager;
import net.project.view.pages.base.BasePage;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;
/**
 *
 */
public class CustomizeWorkplan extends BasePage {
	
	@Property
	private ScheduleColumn column;
	
	@Property
	private List<ScheduleColumn> rows;
	
	Object onActivate(String param) {
		PersonProperty property = PersonProperty.getFromSession(getHttpServletRequest().getSession());
        property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
        if(getParameter("parameterString") != null){
			JSONArray jsArray = new JSONArray(getParameter("parameterString"));
			for (int arrayIndex = 0; arrayIndex < jsArray.length(); arrayIndex++) {
				JSONObject object = jsArray.getJSONObject(arrayIndex);
				try {
					property.replace(ScheduleColumn.COLOUMN_PROPERTY_CONTEXT, object.getString("columnName"), object.getString("columnValue"));
				} catch (PersistenceException e) {
					Logger.getLogger(CustomizeWorkplan.class).error("Error occured while saving column settings" + e.getMessage());
				}
			}
        }else if(getParameter("draggedColumn") != null && getParameter("droppedColumn") != null){
        	ScheduleColumn sc = new ScheduleColumn();
        	sc.handleColumnDragAndDrop(property, sc.getInstanceByColumnId(getParameter("draggedColumn")),sc.getInstanceByColumnId(getParameter("droppedColumn")));
        }
		return new TextStreamResponse("text/plain", "success");
	}
	
	/**
	 * Here populating all columns of schedule
	 * For arranging workplan column list in 3 column table(* X 3).  
	 * @return list of workplanColumn row list.
	 */
	public List<ScheduleColumn> getWorkPlanColumnsRows() {
		return ScheduleColumn.scheduleColumnList;
	}
	
	public int getModuleId() {
		return Module.SCHEDULE;
	}
	
	public String getSpaceId() {
		return SessionManager.getUser().getCurrentSpace().getID();
	}
}
