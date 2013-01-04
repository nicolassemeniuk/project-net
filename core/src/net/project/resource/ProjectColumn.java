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
package net.project.resource;

import java.util.LinkedList;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * To render headers on project list page page.
 *
 * @author Ritesh S
 */
public class ProjectColumn {
	
	private static LinkedList<ProjectColumn> projectColumnList = new LinkedList<ProjectColumn>();

	private String id;

	private String header;

	private int width;

	private int defaultWidth;
	
	private boolean visible;
	
	private boolean hidable;
	
	private boolean defaultVisibility;
	
	private int sequence;
	
	private int defaultSequence;
	
	private boolean dragable;
	
	private final String COLUMN_PROPERTY_CONTEXT = "net.project.column.ProjectPorfolio";

	// General Columns
	public static ProjectColumn projectName = new ProjectColumn("projectName", PropertyProvider.get("prm.project.portfoliio.gridheader.projectname"), 250, true, false, 0, false);


	// Status Colunms
	public static ProjectColumn startDate = new ProjectColumn("startDate", PropertyProvider.get("prm.project.portfoliio.gridheader.startdate"), 80, true, true, 0, true);
	public static ProjectColumn endDate = new ProjectColumn("endDate", PropertyProvider.get("prm.project.portfoliio.gridheader.finishdate"), 80, true, true, 0, true);
	public static ProjectColumn status = new ProjectColumn("status", PropertyProvider.get("prm.project.portfoliio.gridheader.status"), 100, true, true, 0, true);
	public static ProjectColumn percentComplete = new ProjectColumn("percentComplete", PropertyProvider.get("prm.project.portfoliio.gridheader.percentcomplete"), 150, true, true, 0, true);
	public static ProjectColumn overallStatusCode = new ProjectColumn("overallStatusCode", PropertyProvider.get("prm.project.portfoliio.gridheader.overallstatuscode"), 15, true, false, 0, true);
	public static ProjectColumn financialStatusCode = new ProjectColumn("financialStatusCode", PropertyProvider.get("prm.project.portfoliio.gridheader.financialstatuscode"), 15, true, true, 0, true);
	public static ProjectColumn scheduleStatusCode = new ProjectColumn("scheduleStatusCode", PropertyProvider.get("prm.project.portfoliio.gridheader.schedulestatuscode"), 15, true, true, 0, true);
	public static ProjectColumn resourceStatusCode = new ProjectColumn("resourceStatusCode", PropertyProvider.get("prm.project.portfoliio.gridheader.resourcestatuscode"), 15, true, true, 0, true);
	

	
	public ProjectColumn() {
	}
	
	/**
	 *Parametrized  constructor
	 *  @param columnId
	 */
	public ProjectColumn(String columnId) {
		this.id = columnId;
    }

	public ProjectColumn(String columnId, String columnHeader, int columnWidth, boolean visible, boolean hidable, int defaultSequence, boolean dragable) {
		this.id = columnId;
		this.header = columnHeader;
		this.width = columnWidth;
		this.visible = visible;
		this.hidable = hidable;
		this.defaultSequence = defaultSequence;
		this.dragable = dragable;
		this.projectColumnList.add(this);
	}
	
	/**
	 * Constructing all column settngs as persisted person porperties.
	 * Currently three property(sequence, visibilty and width) of scheule columns is persitable.
	 * this method constructs person's property for column sequence, column visibilty and column width.
	 * @param personProperty
	 */
//	public void constructColumnSettings(PersonProperty personProperty){
//		//First set all person properties in columns.
//		for (ProjectColumn assignmentColumn : this.projectColumnList) {
//			assignmentColumn.setVisible(Boolean.valueOf(getProperty(personProperty, assignmentColumn.getId(), "" + assignmentColumn.isDefaultVisibility())));
//			assignmentColumn.setSequence(Integer.valueOf(getProperty(personProperty, "column"+assignmentColumn.getId()+"sequence", ""+assignmentColumn.getDefaultSequence())));
//			assignmentColumn.setWidth(Integer.valueOf(getProperty(personProperty, COLUMN_PROPERTY_CONTEXT, "thd_"+ assignmentColumn.getId()+"_width", ""+assignmentColumn.getDefaultWidth())));
//		}
//		assertUniqueSequence(personProperty);
//	}

	/**
	 * Get person property value.
	 * @param prop
	 * @param context
	 * @param property
	 * @param ifNull
	 * @return propertyValue
	 */
	public String getProperty(PersonProperty prop, String context, String property, String ifNull) {
		String[] sequenceProps = prop.get(context, property, true);
		if (sequenceProps != null && sequenceProps.length > 0)
			return sequenceProps[0];
		else
			return ifNull;
	}
	
	/**
	 * Getting person property value using defalut column context.
	 * @param prop
	 * @param property
	 * @param ifNull
	 * @return propertyValue
	 */
	private String getProperty(PersonProperty prop, String property, String ifNull) {
		return getProperty(prop, COLUMN_PROPERTY_CONTEXT, property, ifNull);
	}
	
	/**
	 * Getting appropriate instance of projectColumn by column id 
	 * @param id
	 * @return
	 */
	public ProjectColumn getInstanceByColumnId(String id){
		ProjectColumn assignmentColumn = new ProjectColumn(id);
		for(ProjectColumn assignmentCol : this.projectColumnList){
			if(assignmentCol.equals(assignmentColumn))
				return assignmentCol;
		}
		return null;
	}
	
	/**
	 * Handling dragging and dropping by rearranging columns sequence
	 * this rearrangement will be saved too.
	 * @param property
	 * @param draggedColumn
	 * @param droppedColumn  
	 */
//	public void handleColumnDragAndDrop(PersonProperty property, ProjectColumn draggedColumn, ProjectColumn droppedColumn) {
//		if (property == null || draggedColumn == null || droppedColumn == null) {
//			throw new NullPointerException("required parameter null");
//		}
//		//first replace dragged column sequnce by dropped column sequence.
//		saveChanges(property, "column"+draggedColumn.getId()+"sequence", droppedColumn.getSequence());
//		
//		//And then shift coumns up from dropzone to dragged column if dargged column sequnce is less than dropped zone sequuence.
//		if (draggedColumn.getSequence() < droppedColumn.getSequence()) {
//			for (ProjectColumn assignmentColumn : this.projectColumnList) {
//				if(assignmentColumn.getSequence() > draggedColumn.getSequence() && assignmentColumn.getSequence() <= droppedColumn.getSequence()){
//					saveChanges(property, "column"+assignmentColumn.getId()+"sequence", assignmentColumn.getSequence()-1);
//				}
//			}
//		}else{//shift coumns down from dropzone to dragged column, if dargged column sequnce is more than dropped zone sequuence.
//			for (ProjectColumn assignmentColumn : this.projectColumnList) {
//				if(assignmentColumn.getSequence() >= droppedColumn.getSequence() && assignmentColumn.getSequence() < draggedColumn.getSequence() ){
//					saveChanges(property, "column"+assignmentColumn.getId()+"sequence", assignmentColumn.getSequence()+1);
//				}
//			}
//		}
//		//Re-construct sequence of assignmentColumn.
//		constructSequence(property);
//	}
	
	/**constructing Sequence of column list as per persisted person porperty
	 * @param personProperty
	 */
//	public void constructSequence(PersonProperty personProperty){
//		for(ProjectColumn sc : this.projectColumnList){
//			sc.setSequence(Integer.valueOf(getProperty(personProperty, "column" + sc.getId() + "sequence", "" + sc.getDefaultSequence())));
//		}
//		//Assert unique and Re-arrange columns in sequence.
//		assertUniqueSequence(personProperty);
//	}
	
	/**
	 * Assert Unique sequence 
	 * Current assignment column list <code>this.projectColumnList</code>  in this method is the list of colulmn 
	 *   with user persisted sequences in database.
	 * If all sequences are not unique, Revert user persisted sequnce and update it using default sequecnces. 
	 * This mehtod contains one parameter <code>PersonProperty</code> which can be null, If it is not null then mehtod will
	 *  remove improper persisted column sequnce value from databse too.
	 * 
	 * Method also arranges all column in sequnence in the currnet assignment column list. 
	 */
	private void assertUniqueSequence(PersonProperty property) {
		boolean uniqueSequence = true;
		//first check all columns sequences are unique
		for (ProjectColumn col1 : this.projectColumnList) {
			for (ProjectColumn col2 : this.projectColumnList) {
				if (!col1.getId().equals(col2.getId()) && col1.getSequence() == col2.getSequence()) {
					uniqueSequence = false;
					break;
				}
			}
		}

		//Now re-arrange all column in sequence.
		//If  column sequnces are not unique remove it and use default column sequcence, also remove persisted value form databse. 
		LinkedList<ProjectColumn> sequencedColumnList = new LinkedList<ProjectColumn>(this.projectColumnList);
		for (ProjectColumn col : this.projectColumnList) {
			if (uniqueSequence) {
				sequencedColumnList.set(col.getSequence(), col);
			} else {
				col.setSequence(col.getDefaultSequence());
				sequencedColumnList.set(col.getDefaultSequence(), col);
				if (property != null)
					removeProperty(property, "column" + col.getId() + "sequence");
			}
		}
		this.projectColumnList = sequencedColumnList;
	}
	
	/**
	 * @param property
	 * @param attribute
	 * @param value
	 */
	private void removeProperty(PersonProperty prop, String property){
		try {
	    	prop.removeAllValues(COLUMN_PROPERTY_CONTEXT, property);
	    } catch (PersistenceException e) {
	    	Logger.getLogger(ProjectColumn.class).error("Error occured while removing column settings" + e.getMessage());
	    }
	}
	
	
	/**
	 * @param property
	 * @param attribute
	 * @param value
	 */
	private void saveChanges(PersonProperty property, String attribute, int value){
		try {
	    	property.replace(COLUMN_PROPERTY_CONTEXT, attribute, ""+value);
	    } catch (PersistenceException e) {
	    	Logger.getLogger(ProjectColumn.class).error("Error occured while saving column settings" + e.getMessage());
	    }
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ProjectColumn other = (ProjectColumn) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	/**
	 * sum of all visible columns default width.
	 * 
	 * @return int totalWidth.
	 */
	public int getVisibleColumnsWidth(){
		int totalWidth = 0;
		for (ProjectColumn col : projectColumnList) {
			if (col.getVisible()) {
				totalWidth += col.getDefaultWidth();
			}
		}
		return totalWidth;
	}
	
	/**
	 * @return the defaultWidth
	 */
	public int getDefaultWidth() {
		return defaultWidth;
	}

	/**
	 * @param defaultWidth
	 *            the defaultWidth to set
	 */
	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return this.header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the projectColumnList
	 */
	public LinkedList<ProjectColumn> getProjectColumnList() {
		return projectColumnList;
	}

	/**
	 * @param projectColumnList
	 *            the projectColumnList to set
	 */
	public void setProjectColumnList(LinkedList<ProjectColumn> projectColumnList) {
		ProjectColumn.projectColumnList = projectColumnList;
	}

	/**
	 * @return the hidable
	 */
	public boolean isHidable() {
		return hidable;
	}

	/**
	 * @param hidable the hidable to set
	 */
	public void setHidable(boolean hidable) {
		this.hidable = hidable;
	}

	/**
	 * @return the visible
	 */
	public boolean getVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the cOLUMN_PROPERTY_CONTEXT
	 */
	public String getColumnPropertyContext() {
		return COLUMN_PROPERTY_CONTEXT;
	}

	/**
	 * @return the defaultVisibility
	 */
	public boolean isDefaultVisibility() {
		return defaultVisibility;
	}

	/**
	 * @param defaultVisibility the defaultVisibility to set
	 */
	public void setDefaultVisibility(boolean defaultVisibility) {
		this.defaultVisibility = defaultVisibility;
	}
	
	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the defaultSequence
	 */
	public int getDefaultSequence() {
		return defaultSequence;
	}

	/**
	 * @param defaultSequence the defaultSequence to set
	 */
	public void setDefaultSequence(int defaultSequence) {
		this.defaultSequence = defaultSequence;
	}

	/**
	 * @return the projectName
	 */
	public ProjectColumn getprojectName() {
		return projectName;
	}

	/**
	 * @return the dragable
	 */
	public boolean isDragable() {
		return dragable;
	}

	/**
	 * @param dragable the dragable to set
	 */
	public void setDragable(boolean dragable) {
		this.dragable = dragable;
	}

}
