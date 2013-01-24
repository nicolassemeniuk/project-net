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
package net.project.view.pages.assignments;

import java.util.ArrayList;
import java.util.List;

import net.project.channel.ScopeType;
import net.project.persistence.PersistenceException;
import net.project.resource.AssignmentColumn;
import net.project.resource.PersonProperty;
import net.project.schedule.ScheduleColumn;
import net.project.security.SessionManager;
import net.project.view.pages.base.BasePage;
import net.project.view.pages.workplan.CustomizeWorkplan;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

public class CustomizeAssignments extends BasePage {

	@Property
	private AssignmentColumn column;

	@Property
	private List<AssignmentColumn> rows;

	@InjectPage
	private MyAssignmentListPage assignmentList;

	Object onActivate() {
		try {
			PersonProperty property = PersonProperty.getFromSession(getHttpServletRequest().getSession());
			property.setScope(ScopeType.SPACE.makeScope(SessionManager.getUser()));
			if (getParameter("parameterString") != null) {
				String columnPropertyContext = new AssignmentColumn().getColumnPropertyContext();
				JSONArray jsArray = new JSONArray(getParameter("parameterString"));
				for (int arrayIndex = 0; arrayIndex < jsArray.length(); arrayIndex++) {
					JSONObject object = jsArray.getJSONObject(arrayIndex);

					property.replace(columnPropertyContext, object.getString("columnName"), object.getString("columnValue"));
				}
				assignmentList.setRearrangeSequence(true);
				assignmentList.setAssignmentColumn(new AssignmentColumn());
				return assignmentList;
			}else if(getParameter("draggedColumn") != null && getParameter("droppedColumn") != null){
				AssignmentColumn assignmentColumn = new AssignmentColumn();
				assignmentColumn.handleColumnDragAndDrop(property, assignmentColumn.getInstanceByColumnId(getParameter("draggedColumn")),assignmentColumn.getInstanceByColumnId(getParameter("droppedColumn")));
				assignmentList.setRearrangeSequence(true);
				assignmentList.setAssignmentColumn(assignmentColumn);
	        }
		} catch (PersistenceException e) { 
			Logger.getLogger(CustomizeAssignments.class).error("Error occured while saving column settings" + e.getMessage());
			return new TextStreamResponse("text", "false");
		}
		return null;
	}

	/**
	 * Populating all columns of assignments
	 * for arranging assignments column list in 3 column table(* X 3).  
	 * @return list of assignmentColumn row list.
	 */
	public List<List<AssignmentColumn>> getAssignmentsColumnsRows() {
		List<List<AssignmentColumn>> row = new ArrayList<List<AssignmentColumn>>();
		List<AssignmentColumn> scl = new AssignmentColumn().getAssignmentColumnList();
		for (int columnIndex = 0; columnIndex < scl.size();) {
			List<AssignmentColumn> col = new ArrayList<AssignmentColumn>();
			col.add(scl.get(columnIndex++));
			if (columnIndex < scl.size())
				col.add(scl.get(columnIndex++));
			if (columnIndex < scl.size())
				col.add(scl.get(columnIndex++));
			row.add(col);
		}
		return row;
	}
}
