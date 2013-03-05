/**
 * 
 */
package net.project.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpaceBean;
import net.project.resource.PersonProperty;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.InvalidDateException;

import org.apache.log4j.Logger;

/**
 * A list of column of schedule's tasks.
 * @author 
 *
 */
public class ScheduleColumn {
	
	/**
	 * Column property context, all column settings related values will be stored by using this context.
	 */
	public static final String COLOUMN_PROPERTY_CONTEXT = "prm.schedule.main.column";
	
	/**
	 * The list of all Column that are available to show.
	 */
	public static LinkedList <ScheduleColumn> scheduleColumnList = new LinkedList <ScheduleColumn>(); 
	
	
	/**
	 * Schedule's viewable columns  with default value
	 */
	public static ScheduleColumn SEQUENCE =  new ScheduleColumn(0, "sequence", true, "prm.schedule.list.sequence.column", true, "seq", false, true, 25, false, "", "none");
	public static ScheduleColumn NAME =  new ScheduleColumn(1, "name", true, "prm.schedule.list.task.column", true, "tn_td", true, false, 200, false, "", "general");
	public static ScheduleColumn WORK =  new ScheduleColumn(2, "work", true, "prm.schedule.list.work.column", true, "w", true, true, 100, true, "numberColumn", "status");
	public static ScheduleColumn DURATION =  new ScheduleColumn(3, "duration", true, "prm.schedule.list.duration.column", true, "d", true, true, 100, true, "numberColumn", "status");
	public static ScheduleColumn START_DATE =  new ScheduleColumn(4, "startDate", true, "prm.schedule.list.startdate.column", true, "sd", true, true, 100, true, "", "status");
	public static ScheduleColumn END_DATE =  new ScheduleColumn(5, "endDate", true, "prm.schedule.list.enddate.column", true, "ed", true, true, 100, true, "", "status");
	public static ScheduleColumn WORK_COMPLETE =  new ScheduleColumn(6, "workComplete", true, "prm.schedule.list.workcomplete.column", false, "wc", true, true, 100, true, "numberColumn", "status");
	public static ScheduleColumn PHASE =  new ScheduleColumn(7, "phase", true, "prm.schedule.list.phase.column", false, "p", true, true, 100, true, "", "general");
	public static ScheduleColumn PRIORITY =  new ScheduleColumn(8, "priority", true, "prm.schedule.list.priority.column", false, "pr", true, true, 100, true, "", "general");
	public static ScheduleColumn CALCULATION_TYPE =  new ScheduleColumn(9, "calculationType", true, "prm.schedule.list.calculationtype.column", false, "ct", false, true, 100, true, "", "general");
	public static ScheduleColumn ACTUAL_START_DATE =  new ScheduleColumn(10, "actualStartDate", true, "prm.schedule.list.actualstartdate.column", false, "asd", false, true, 100, true, "", "status");
	public static ScheduleColumn BASELINE_START_DATE =  new ScheduleColumn(11, "baselineStartDate", true, "prm.schedule.list.baselinestartdate.column", false, "bsd", false, true, 100, true, "", "calculated");
	public static ScheduleColumn START_VARIANCE =  new ScheduleColumn(12, "startVariance", true, "prm.schedule.list.startvariance.column", false, "sv", false, true, 100, true, "numberColumn", "calculated");
	public static ScheduleColumn ACTUAL_ENDDATE =  new ScheduleColumn(13, "actualEndDate", true, "prm.schedule.list.actualenddate.column", false, "ae", false, true, 100, true, "", "status");
	public static ScheduleColumn BASELINE_END_DATE =  new ScheduleColumn(14, "baselineEndDate", true, "prm.schedule.list.baselineenddate.column", false, "bed", false, true, 100, true, "", "calculated");
	public static ScheduleColumn END_VARIANCE =  new ScheduleColumn(15, "endVariance", true, "prm.schedule.list.endvariance.column", false, "ev", false, true, 100, true, "numberColumn", "calculated");
	public static ScheduleColumn BASELINE_WORK =  new ScheduleColumn(16, "baselineWork", true, "prm.schedule.list.baselinework.column", false, "bw", false, true, 100, true, "numberColumn", "calculated");
	public static ScheduleColumn WORK_VARIANCE =  new ScheduleColumn(17, "workVariance", true, "prm.schedule.list.workvariance.column", false, "wv", false, true, 100, true, "numberColumn", "calculated");
	public static ScheduleColumn BASELINE_DURATION =  new ScheduleColumn(18, "baselineDuration", true, "prm.schedule.list.baselineduration.column", false, "bd", false, true, 100, true, "numberColumn", "calculated");
	public static ScheduleColumn DURATION_VARIANCE =  new ScheduleColumn(19, "durationVariance", true, "prm.schedule.list.durationvariance.column", false, "dv", false, true, 100, true, "numberColumn", "calculated");
	public static ScheduleColumn WORK_PERCENT_COMPLETE =  new ScheduleColumn(20, "workPercentComplete", true, "prm.schedule.list.complete.column", true, "wpc", true, true, 100, true, "numberColumn", "status");
	public static ScheduleColumn STATUS_NOTIFIERS =  new ScheduleColumn(21, "statusNotifiers", false, "prm.schedule.list.statusnotifiers.column", true, "sn", false, true, 100, true, "", "status");
	public static ScheduleColumn RESOURCES =  new ScheduleColumn(22, "resources", true, "prm.schedule.list.resources.column", false, "r", false, true, 100, true, "", "status");
	public static ScheduleColumn DEPENDENCIES =  new ScheduleColumn(23, "dependencies", true, "prm.schedule.list.dependencies.column", false, "dp", false, true, 100, true, "", "status");
	public static ScheduleColumn WBS =  new ScheduleColumn(24, "wbs", true, "prm.schedule.list.wbs.column", false, "wbs", false, true, 100, true, "", "calculated");
	public static ScheduleColumn CHARGECODE =  new ScheduleColumn(25, "chargecode", false, "prm.schedule.list.chargecode.column", false, "wbs", false, true, 100, true, "", "status");
	
	
	/**
	 * column properties
	 */
	private String columnAbbr;
	private String id;
	private int sequence;
	private int defaultSequence;
	private boolean visible;
	private boolean defaultVisibility;
	private boolean hidable;
	private String columnId;
	private boolean sortable;
	private String header;
	private String value;
	private boolean editable;
	private String onClick;
	private ScheduleDecorator scheduleDecorator;
	private String hiddenField;
	private int width;
	private int defaultWidth;
	private boolean dragable;
	private String style;
	private boolean flatView;
	private String category;

	/**
	 *Default constructor 
	 */
	public ScheduleColumn() {
    }
	
	/**
	 *Parametrized  constructor
	 *  @param columnId
	 */
	public ScheduleColumn(String columnId) {
		this.columnId = columnId;
    }
	
	
	/**
	 * Parametrized  constructor
	 * @param sc
	 * @param id
	 * @param value
	 * @param onClick
	 */
	public ScheduleColumn(ScheduleColumn sc, String id, String value, String hiddenField, String onClick) {
		this.sequence = sc.getSequence();
		this.columnId = sc.getColumnId();
		this.header = sc.getHeader();
		this.visible = sc.isVisible();
		this.columnAbbr = sc.getColumnAbbr();
		this.editable = sc.isEditable();
		this.style = sc.getStyle();
		this.id = id;
		this.value = value;
		this.onClick = onClick;
		this.hiddenField = hiddenField;
    }
	
	/**Parametrized  constructor
	 * @param defaultSeq
	 * @param columnId
	 * @param sortable
	 * @param headerToken
	 * @param defaultVisiblity
	 */
	private ScheduleColumn(int defaultSequence, String columnId, boolean sortable, String headerToken, boolean defaultVisibility, String columnAbbr, boolean editable, boolean hidable, int defaultWidth, boolean dragable, String style, String category) {
		this.defaultSequence = defaultSequence;
		this.columnId = columnId;
		this.sortable = sortable;
		this.header = headerToken;
		this.defaultVisibility = defaultVisibility;
		this.columnAbbr = columnAbbr;
		this.editable = editable;
		this.hidable = hidable;
		this.defaultWidth = defaultWidth;
		this.dragable = dragable;
		this.style = style;
		this.category = category;
		//assertUniqueSequence(scheduleColumnList, this); //To be implement.
		if("chargecode".equals(columnId) && PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) {
			this.scheduleColumnList.add(getSequence(), this);			
		} else if(!"chargecode".equals(columnId)) {
			this.scheduleColumnList.add(getSequence(), this);
		}
    }
	
	/**
	 * New instance of ScheduleColumn by initializing all required property which will be use during rendering task in page.
	 * @param sc
	 * @param value
	 * @return
	 */
	public ScheduleColumn getNewInstance(ScheduleColumn sc, ScheduleEntry se, String displayValue){
		return new ScheduleColumn(sc, se.getID(), displayValue, getHiddenFiledHTML(sc, se) ,getOnClickMethod(sc, se));
	}
	
	
	/**
	 * Getting appropriate instance of ScheduleColumn by column id 
	 * @param id
	 * @return
	 */
	public ScheduleColumn getInstanceByColumnId(String id){
		ScheduleColumn scheduleColumn = new ScheduleColumn(id);
		for(ScheduleColumn sc : this.scheduleColumnList){
			if(sc.equals(scheduleColumn))
				return sc;
		}
		return null;
	}
	
	/**Re Sequenced Row as per persisted column sequence. And also render dispaly value for specific table data.
	 * @param ScheduleEntry se
	 * @return
	 */
	public List getSequencedRow(ScheduleEntry se){
		LinkedList <ScheduleColumn> col = new LinkedList<ScheduleColumn>(this.scheduleColumnList);
		col.set(ScheduleColumn.SEQUENCE.getSequence(), getNewInstance(ScheduleColumn.SEQUENCE, se,  ""+se.getSequenceNumber()));
		col.set(ScheduleColumn.NAME.getSequence(), getNewInstance(ScheduleColumn.NAME, se, getTaskNameColumnHTML(se)));
		col.set(ScheduleColumn.WORK.getSequence(), getNewInstance(ScheduleColumn.WORK, se, se.getWorkTQ().toShortString(0, 2)));
		col.set(ScheduleColumn.DURATION.getSequence(), getNewInstance(ScheduleColumn.DURATION, se,  se.getDurationFormatted()));
		col.set(ScheduleColumn.START_DATE.getSequence(), getNewInstance(ScheduleColumn.START_DATE, se,  se.getStartTimeStringFormatted()));
		col.set(ScheduleColumn.END_DATE.getSequence(), getNewInstance(ScheduleColumn.END_DATE, se,  se.getEndTimeStringFormatted()));
		col.set(ScheduleColumn.WORK_COMPLETE.getSequence(), getNewInstance(ScheduleColumn.WORK_COMPLETE, se,  se.getWorkCompleteTQ().toShortString(0, 2)));
		col.set(ScheduleColumn.PHASE.getSequence(), getNewInstance(ScheduleColumn.PHASE, se,  se.getPhaseName()));
		col.set(ScheduleColumn.PRIORITY.getSequence(), getNewInstance(ScheduleColumn.PRIORITY, se,  se.getPriorityString()));
		col.set(ScheduleColumn.CALCULATION_TYPE.getSequence(), getNewInstance(ScheduleColumn.CALCULATION_TYPE, se,  se.getTaskCalculationTypeString()));
		col.set(ScheduleColumn.ACTUAL_START_DATE.getSequence(), getNewInstance(ScheduleColumn.ACTUAL_START_DATE, se,  se.getActualStartDateStringMedium()));
		col.set(ScheduleColumn.BASELINE_START_DATE.getSequence(), getNewInstance(ScheduleColumn.BASELINE_START_DATE, se,  se.getBaselineStartDateStringMedium()));
		col.set(ScheduleColumn.START_VARIANCE.getSequence(), getNewInstance(ScheduleColumn.START_VARIANCE, se,  se.getStartVarianceString()));
		col.set(ScheduleColumn.ACTUAL_ENDDATE.getSequence(), getNewInstance(ScheduleColumn.ACTUAL_ENDDATE, se, se.getActualEndDateStringMedium()));
		col.set(ScheduleColumn.BASELINE_END_DATE.getSequence(), getNewInstance(ScheduleColumn.BASELINE_END_DATE, se,  se.getBaselineEndDateStringMedium()));
		col.set(ScheduleColumn.END_VARIANCE.getSequence(), getNewInstance(ScheduleColumn.END_VARIANCE, se,  se.getEndVarianceString()));
		col.set(ScheduleColumn.BASELINE_WORK.getSequence(), getNewInstance(ScheduleColumn.BASELINE_WORK, se,  se.getFormattedBaselineWork()));
		col.set(ScheduleColumn.WORK_VARIANCE.getSequence(), getNewInstance(ScheduleColumn.WORK_VARIANCE, se,  se.getWorkVarianceString()));
		col.set(ScheduleColumn.BASELINE_DURATION.getSequence(), getNewInstance(ScheduleColumn.BASELINE_DURATION, se,  se.getBaselineDurationString()));
		col.set(ScheduleColumn.DURATION_VARIANCE.getSequence(), getNewInstance(ScheduleColumn.DURATION_VARIANCE, se,  se.getDurationVarianceString()));
		col.set(ScheduleColumn.WORK_PERCENT_COMPLETE.getSequence(), getNewInstance(ScheduleColumn.WORK_PERCENT_COMPLETE, se,  se.getWorkPercentComplete()));
		col.set(ScheduleColumn.STATUS_NOTIFIERS.getSequence(), getNewInstance(ScheduleColumn.STATUS_NOTIFIERS, se,  getStatusNotifiersColumnHTML(se))); 
		col.set(ScheduleColumn.RESOURCES.getSequence(), getNewInstance(ScheduleColumn.RESOURCES, se,  se.getResourceListString()));
		col.set(ScheduleColumn.DEPENDENCIES.getSequence(), getNewInstance(ScheduleColumn.DEPENDENCIES, se,  se.getDependenciesString()));
		col.set(ScheduleColumn.WBS.getSequence(), getNewInstance(ScheduleColumn.WBS, se,  se.getWBS()));

		if(PropertyProvider.getBoolean("prm.global.business.managechargecode.isenabled")) {
			col.set(ScheduleColumn.CHARGECODE.getSequence(), getNewInstance(ScheduleColumn.CHARGECODE, se,  se.getChargeCodeName()));			
		}

		return col;
	}
	
	/**constructing Visiblility of culumn list as per persisted person porperty
	 * @param personProperty
	 */
	public void constructVisiblility(PersonProperty personProperty){
		for(ScheduleColumn sc : this.scheduleColumnList){
			sc.setVisible(Boolean.valueOf(getProperty(personProperty, sc.getColumnId(), "" + sc.isDefaultVisibility())));
		}
	}
	
	/**constructing Sequence of culumn list as per persisted person porperty
	 * @param personProperty
	 */
	public void constructSequence(PersonProperty personProperty){
		for(ScheduleColumn sc : this.scheduleColumnList){
			sc.setSequence(Integer.valueOf(getProperty(personProperty, "column" + sc.getColumnId() + "sequence", "" + sc.getDefaultSequence())));
		}
		//Assert unique and Re-arrange columns in sequence.
		assertUniqueSequence(personProperty);
	}
	
	/**
	 * constructing Width of column list as per persisted person porperty
	 * @param personProperty
	 */
	public void constructWidth(PersonProperty personProperty){
		for(ScheduleColumn sc : this.scheduleColumnList){
			sc.setWidth(Integer.valueOf(getProperty(personProperty, "prm.schedule.main", "thd_"+ sc.getColumnId()+"_width", "" + sc.getDefaultWidth())));
		}
	}
	
	/**
	 * constructing all column settngs as persisted person porperties.
	 * Currently three property(sequence, visibilty and width) of scheule columns is persitable.
	 * this method constructs person's property for column sequence, column visibilty and column width.
	 * @param personProperty
	 */
	public void constructColumnSettings(PersonProperty personProperty){
		//First set all person properties in columns.
		for (ScheduleColumn sc : this.scheduleColumnList) {
			sc.setSequence(Integer.valueOf(getProperty(personProperty, "column"+sc.getColumnId()+"sequence", ""+sc.getDefaultSequence())));
			sc.setVisible(Boolean.valueOf(getProperty(personProperty, sc.getColumnId(), "" + sc.isDefaultVisibility())));
			sc.setWidth(Integer.valueOf(getProperty(personProperty, "prm.schedule.main", "thd_"+ sc.getColumnId()+"_width", ""+sc.getDefaultWidth())));
		}
		//Assert unique and Re-arrange columns in sequence.
		assertUniqueSequence(personProperty);
	}
	
	
	/**
	 * Get person property value.
	 * @param prop
	 * @param context
	 * @param property
	 * @param ifNull
	 * @return propertyValue
	 */
	private String getProperty(PersonProperty prop, String context, String property, String ifNull) {
		String[] sequenceProps = prop.get(context, property, true);
		if (sequenceProps != null && sequenceProps.length > 0)
			return sequenceProps[0];
		else
			return ifNull;
	}
	
	/**
	 * getting person property value using defalut column context.
	 * @param prop
	 * @param property
	 * @param ifNull
	 * @return propertyValue
	 */
	private String getProperty(PersonProperty prop, String property, String ifNull) {
		return getProperty(prop, COLOUMN_PROPERTY_CONTEXT, property, ifNull);
	}
	
	/**
	 * Handling dragging and dropping by rearranging columns sequence
	 * this rearrangement will be saved too.
	 * @param property
	 * @param draggedColumn
	 * @param droppedColumn
	 */
	public void handleColumnDragAndDrop(PersonProperty property, ScheduleColumn draggedColumn, ScheduleColumn droppedColumn) {
		if (property == null || draggedColumn == null || droppedColumn == null) {
			throw new NullPointerException("required parameter null");
		}
		//first replace dragged column sequnce by dropped column sequence.
		saveChanges(property, "column"+draggedColumn.getColumnId()+"sequence", droppedColumn.getSequence());
		
		//And then	
		//shift coumns up from dropzone to dragged column if dargged column sequnce is less than dropped zone sequuence.
		if (draggedColumn.getSequence() < droppedColumn.getSequence()) {
			for (ScheduleColumn sc : this.scheduleColumnList) {
				if(sc.getSequence() > draggedColumn.getSequence() && sc.getSequence() <= droppedColumn.getSequence()){
					saveChanges(property, "column"+sc.getColumnId()+"sequence", sc.getSequence()-1);
				}
			}
		}else{//shift coumns down from dropzone to dragged column, if dargged column sequnce is more than dropped zone sequuence.
			for (ScheduleColumn sc : this.scheduleColumnList) {
				if(sc.getSequence() >= droppedColumn.getSequence() && sc.getSequence() < draggedColumn.getSequence() ){
					saveChanges(property, "column"+sc.getColumnId()+"sequence", sc.getSequence()+1);
				}
			}
		}
		//Re-construct sequence of ScheduleColumn.
		constructSequence(property);
	}
	
	/**
	 * Assert Unique sequence 
	 * Current schedule column list <code>this.scheduleColumnList</code>  in this method is the list of colulmn 
	 *   with user persisted sequences in database.
	 * If all sequences are not unique, Revert user persisted sequnce and update it using default sequecnces. 
	 * This mehtod contains one parameter <code>PersonProperty</code> which can be null, If it is not null then mehtod will
	 *  remove improper persisted column sequnce value from databse too.
	 * 
	 * Method also arranges all column in sequnence in the currnet schedule column list. 
	 */
	private void assertUniqueSequence(PersonProperty property) {
		boolean uniqueSequence = true;
		//first check all columns sequences are unique
		for (ScheduleColumn col1 : this.scheduleColumnList) {
			for (ScheduleColumn col2 : this.scheduleColumnList) {
				if (!col1.getColumnId().equals(col2.getColumnId()) && col1.getSequence() == col2.getSequence()) {
					uniqueSequence = false;
					break;
				}
			}
		}

		//Now re-arrange all column in sequence.
		//If  column sequnces are not unique remove it and use default column sequcence, also remove persisted value from databse. 
		LinkedList<ScheduleColumn> sequencedColumnList = new LinkedList<ScheduleColumn>(this.scheduleColumnList);
		for (ScheduleColumn col : this.scheduleColumnList) {
			if (uniqueSequence) {
				sequencedColumnList.set(col.getSequence(), col);
			} else {
				col.setSequence(col.getDefaultSequence());
				sequencedColumnList.set(col.getDefaultSequence(), col);
				if (property != null)
					removeProperty(property, "column" + col.getColumnId() + "sequence");
			}
		}
		this.scheduleColumnList = sequencedColumnList;
	}
	
	/**
	 * @param property
	 * @param attribute
	 * @param value
	 */
	private void saveChanges(PersonProperty property, String attribute, int value){
		try {
	    	property.replace(COLOUMN_PROPERTY_CONTEXT, attribute, ""+value);
	    } catch (PersistenceException e) {
	    	Logger.getLogger(ScheduleColumn.class).error("Error occured while saving column settings" + e.getMessage());
	    }
	}
	
	/**
	 * @param property
	 * @param attribute
	 * @param value
	 */
	private void removeProperty(PersonProperty prop, String property){
		try {
	    	prop.removeAllValues(COLOUMN_PROPERTY_CONTEXT, property);
	    } catch (PersistenceException e) {
	    	Logger.getLogger(ScheduleColumn.class).error("Error occured while removing column settings" + e.getMessage());
	    }
	}
	
	/**
	 * For generating tree view. 
	 * @param se
	 * @return
	 */
	private String getTaskNameColumnHTML(ScheduleEntry se){
		String taskNameTD  = "";
		if(!this.isFlatView() && se.isNumSpacers()){
			taskNameTD += "<img src=\""+SessionManager.getJSPRootURL()+"/s.gif\" height=\"1\" width=\""+se.getSpaceWidth()+"\"/>";
		}
		if(se.isMilestone()){
			taskNameTD += "<img src=\""+SessionManager.getJSPRootURL()+"/images/milestone.gif\" height=\"10\" width=\"10\" border=\"0\"/>";
		}
		if(!this.isFlatView() && se.isSummaryTask()){
			if(se.isExpanded())
				taskNameTD += "<img src=\""+SessionManager.getJSPRootURL()+"/u.gif\" align=\"absmiddle\" id=\"toggler"+se.getID()+"\" onclick=\"javascript:toggleTree(" +se.getID()+ ");\" ondblclick=\"stopEventPropagationFor(event);\"/>";
			else
				taskNameTD += "<img src=\""+SessionManager.getJSPRootURL()+"/e.gif\" align=\"absmiddle\" id=\"toggler"+se.getID()+"\" onclick=\"javascript:toggleTree(" +se.getID()+ ");\" ondblclick=\"stopEventPropagationFor(event);\"/>";
		}
		taskNameTD += "&nbsp;<span id=\"tn_"+ se.getID()+"\"  onclick=\"sfc('tn','"+  se.getID() +"', false, false);\">"+ HTMLUtils.escape(se.getName())+"</span>";
		return taskNameTD;
	}
	
	/**
	 * Rendering ScheduleColumn STATUS_NOTIFIERS as per user settings. 
	 * There are many conditon to render this column.
	 * @param se
	 * @return
	 */
	private String getStatusNotifiersColumnHTML(ScheduleEntry se){
		if(scheduleDecorator == null){
			scheduleDecorator = new ScheduleDecorator();
		}
		
		String jSPRootURL = SessionManager.getJSPRootURL();
		String StatusNotifiersTD  = "";
		
		if(se.isFromShare()){
			boolean isActionAllowed = checkAccessForExternalProject(se.getSharingSpaceID());
			if(isActionAllowed) {
				StatusNotifiersTD  += "<a href=\""+jSPRootURL+ "/project/Main.jsp?module=150&amp;id="+se.getSharingSpaceID()+"&amp;page="+ jSPRootURL + "%2Fworkplan%2Ftaskview%3Fmodule%3D60\" title=\""+scheduleDecorator.getSharingProjectTitle()+"\">";
			}
				StatusNotifiersTD  += "<img hspace=\"2\" border=\"0\" src=\""+scheduleDecorator.getExternalTaskImage()+"\" title=\""+ se.getSharingSpaceName()+"\"/>";
			if(isActionAllowed) {
				StatusNotifiersTD  +=  "</a>";
			}
				StatusNotifiersTD  += "<input type=\"hidden\" id=\"ex_"+se.getID()+"\" value=\"" + se.getID()+"\"/>";
			
		}
		if(se.isHasDependencies()){
			StatusNotifiersTD += "<a href=\""+ jSPRootURL+ "/servlet/ScheduleController/TaskView/Dependencies?action=1&amp;module=60&amp;id="+se.getID()+"\" onmouseover=\"hiLite("+se.getDependentIds()+");dPopup('"+se.getDependentTaskInfo()+"');\" onmouseout=\"unLite("+se.getDependentIds()+");dClose();\">";
			StatusNotifiersTD += "<img src=\""+scheduleDecorator.getTaskDependenciesExistImage()+"\" hspace=\"2\" border=\"0\"/></a>";

		}
		if(se.getConstraintType().isDateConstrained()){
			StatusNotifiersTD += "<a href=\""+jSPRootURL+"/servlet/ScheduleController/TaskView/Advanced?action=1&amp;module=60&amp;id="+se.getID()+"\">";
			StatusNotifiersTD += "<img src=\""+scheduleDecorator.getIsDateConstrainedImage()+"\" hspace=\"2\" title=\""+se.getDateConstrainedTooltip()+"\" border=\"0\"/></a>";
		}
		if(se.isHasAssignments()){
			StatusNotifiersTD += "<a href=\""+jSPRootURL+"/servlet/ScheduleController/TaskView/Assignments?action=1&amp;module=60&amp;id="+se.getID()+"\">";
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getHasAssignmentImage()+"\" hspace=\"2\" border=\"0\" onmouseover=\"aPopup('" + se.getAssignmentsTooltip() +"');\" onmouseout=\"aClose();\"/></a>";
		} else if(scheduleDecorator.getUnassignedTasksImage() != null){
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getUnassignedTasksImage() +"\" hspace=\"2\" border=\"0\" title=\""+scheduleDecorator.getNoresourcesTitle()+"\"/>";
		}
		if(se.isCriticalPath()){
			StatusNotifiersTD += "<img src=\""+scheduleDecorator.getIsCriticalPathImage()+"\" hspace=\"2\" border=\"0\" title=\""+scheduleDecorator.getCriticalpathTitle()+"\"/>";
		}
		if(se.isPastDeadline()){
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getAfterDeadlineImage() +"\" hspace=\"2\" border=\"0\" title=\""+se.getAfterdeadlineToolTip()+"\"/>";
		}
		if(se.isHasMaterialAssignments()){
			StatusNotifiersTD += "<a href=\""+jSPRootURL+"/servlet/ScheduleController/TaskView/Material?action=1&amp;module=60&amp;id="+se.getID()+"\">";
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getHasMaterialAssignmentImage()+"\" hspace=\"2\" border=\"0\" onmouseover=\"mPopup('" + se.getMaterialAssignmentsTooltip() +"');\" onmouseout=\"mClose();\"/></a>";			
		}		
		
		Date todaysDate = new Date();
		try {
			todaysDate = DateFormat.getInstance().parseDateString(DateFormat.getInstance().formatDate(new Date()));
		} catch (InvalidDateException pnetEx) {
			Logger.getLogger(ScheduleColumn.class).error("Error occurred while parsing todays date : "+pnetEx.getMessage());
		}
		Calendar todayPlus7 = Calendar.getInstance();
		todayPlus7.add(Calendar.DATE, 8);
		
		if(se.getEndTime().compareTo(todayPlus7.getTime()) <= 0 && se.getEndTime().compareTo(todaysDate) >= 0 && scheduleDecorator.getTasksComingDueImage() != null){
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getTasksComingDueImage() +"\" hspace=\"2\" border=\"0\" title=\""+scheduleDecorator.getTaskcomingdueTitle()+"\"/>";
		}
		if(se.isComplete() && scheduleDecorator.getCompletedTasksImage() != null){
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getCompletedTasksImage() +"\" hspace=\"2\" border=\"0\" title=\""+scheduleDecorator.getCompletetaskTitle()+"\"/>";
		}
		if (se.getEndTime().before(todaysDate) && se.getPercentComplete() < 1 && scheduleDecorator.getLateTasksImage() != null) {
			StatusNotifiersTD += "<img src=\""+ scheduleDecorator.getLateTasksImage() +"\" hspace=\"2\" border=\"0\" title=\""+scheduleDecorator.getLatetaskTitle()+"\"/>";
		}
		return StatusNotifiersTD;
	}
	
	/**Generate HTML of hidden field with provided vaue and id. 
	 * @param se
	 * @param hiddenAbbr
	 * @return  HTML String
	 */
	private String getHiddenHTML(String value, String id) {
		return "<input type=\"hidden\" id=\"" + id + "\" value=\"" + value + "\" />";
	}
	
	/**
	 * Table data on click JS method. Will be used for inline editing. 
	 * @param ScheduleColumn sc
	 * @param ScheduleEntry se
	 * @return JS method call
	 */
	private String getOnClickMethod(ScheduleColumn sc, ScheduleEntry se) {
		if (sc.isEditable() && !sc.equals(ScheduleColumn.NAME)) {
			return "javascript:sfc('" + sc.getColumnAbbr() + "','" + se.getID() + "'," + se.isSummaryTask() + ","+ se.isFromShare() + ");";
		} else {
			return "";
		}
	}
	
	/**
	 * Table header on click JS method. Will be used for sorting. 
	 * @param ScheduleColumn sc
	 * @return JS method call
	 */
	public String getSortJSCall() {
		return this.isSortable() ? "javascript:sort('" + this.getDefaultSequence() + "');" : "";
	}
	
	
	/**Some column needs one hidden field while rendering, Which will be used during editing.
	 * return HTML of hidden field if it is needed otherwise return null 
	 * @param sc
	 * @param se
	 * @return HTML of hedden filed for specified column
	 */
	private String getHiddenFiledHTML(ScheduleColumn sc, ScheduleEntry se) {
		if (sc.equals(ScheduleColumn.START_DATE)) {
			return getHiddenHTML(se.getStartTimeString(), "h" + sc.getColumnAbbr() + "_" + se.getID());
		} else if (sc.equals(ScheduleColumn.END_DATE)) {
			return getHiddenHTML(se.getEndTimeString(), "h" + sc.getColumnAbbr() + "_" + se.getID());
		} else if (sc.equals(ScheduleColumn.PHASE)) {
			return getHiddenHTML(se.getPhaseID(), "h" + sc.getColumnAbbr() + "_" + se.getID());
		} else if (sc.equals(ScheduleColumn.PRIORITY)) {
			return getHiddenHTML(se.getPriority().getID(), "h" + sc.getColumnAbbr() + "_" + se.getID());
		} else if (sc.equals(ScheduleColumn.WORK_COMPLETE)|| sc.equals(ScheduleColumn.WORK_PERCENT_COMPLETE)) {
			return getHiddenHTML(se.isHasAssignments()+"", "has_assignment_"+se.getID());
		} else {
			return null;
		}
	}
	
	/**
	 * Returns access for external task project
	 * 
	 * @param id Sharing space ID
	 * @return access for sharing project
	 */
	private boolean checkAccessForExternalProject(String id) {
		SecurityProvider checkSecurityProvider = new SecurityProvider();
		try {
			Space checkSpace = new ProjectSpaceBean();
			checkSpace.setID(id);
	        checkSecurityProvider.setUser(SessionManager.getUser());
	        checkSecurityProvider.setSpace(checkSpace);
		}catch (Exception e) {
			Logger.getLogger(ScheduleColumn.class).error("Error occured while checking external project access" + e.getMessage());
		}
		return checkSecurityProvider.isActionAllowed(null, Module.PROJECT_SPACE, Action.VIEW);
	}
	
	/**
	 * @return the columnId
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @param columnId the columnId to set
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return PropertyProvider.get(this.header);
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
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
	 * @return the sortable
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * @param sortable the sortable to set
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * @return the scheduleColumnList
	 */
	public LinkedList<ScheduleColumn> getScheduleColumnList() {
		return this.scheduleColumnList;
	}

	/**
	 * @param scheduleColumnList the scheduleColumnList to set
	 */
	public static void setScheduleColumnList(LinkedList<ScheduleColumn> scheduleColumnList) {
		ScheduleColumn.scheduleColumnList = scheduleColumnList;
	}
	
	/**
	 * sum of all visible columns default width.
	 * 
	 * @return int totalWidth.
	 */
	public static int getVisibleColumnsWidth(){
		int totalWidth = 0;
		for (ScheduleColumn col : ScheduleColumn.scheduleColumnList) {
			if (col.isVisible()) {
				totalWidth += col.getDefaultWidth();
			}
		}
		return totalWidth;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the columnAbbr
	 */
	public String getColumnAbbr() {
		return columnAbbr;
	}

	/**
	 * @param columnAbbr the columnAbbr to set
	 */
	public void setColumnAbbr(String columnAbbr) {
		this.columnAbbr = columnAbbr;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * @return the onClick
	 */
	public String getOnClick() {
		return onClick;
	}

	/**
	 * @param onClick the onClick to set
	 */
	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}
	
	/**
	 * @return the hiddenField
	 */
	public String getHiddenField() {
		return hiddenField;
	}

	/**
	 * @param hiddenField the hiddenField to set
	 */
	public void setHiddenField(String hiddenField) {
		this.hiddenField = hiddenField;
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
		final ScheduleColumn other = (ScheduleColumn) obj;
		if (columnId == null) {
			if (other.columnId != null)
				return false;
		} else if (!columnId.equals(other.columnId))
			return false;
		return true;
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
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the defaultSequence
	 */
	public int getDefaultSequence() {
		return defaultSequence;
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
	 * @return the defaultWidth
	 */
	public int getDefaultWidth() {
		return defaultWidth;
	}

	/**
	 * @param defaultWidth the defaultWidth to set
	 */
	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
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

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @return the flatView
	 */
	public boolean isFlatView() {
		return flatView;
	}

	/**
	 * @param flatView the flatView to set
	 */
	public void setFlatView(boolean flatView) {
		this.flatView = flatView;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * Method to check whether category is status
	 * 
	 * @return true / false
	 */
	public boolean isStatus(){
		return this.category.equals("status");
	}
	
	/**
	 * Method to check whether category is general
	 * 
	 * @return true / false
	 */
	public boolean isGeneral(){
		return this.category.equals("general");
	}
	
	/**
	 * Method to check whether category is calculated
	 * 
	 * @return true / false
	 */
	public boolean isCalculated(){
		return this.category.equals("calculated");
	}
	
}
