/**
 * 
 */
package net.project.view.pages.portfolio;

import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.FinderSorter;
import net.project.base.property.PropertyProvider;
import net.project.portfolio.view.MetaColumn;
import net.project.portfolio.view.MetaColumnList;
import net.project.util.HTMLUtils;
import net.project.util.JSONUtils;
import net.project.view.pages.base.BasePage;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * To provide functionality for customizing columns in project portfolio
 */
public class CustomizePortfolio extends BasePage{
	
	private static Logger log = Logger.getLogger(CustomizePortfolio.class);
	
	@Property
	@InjectPage
	private ProjectListPage projectListPage;

	@Property
	private List<MetaColumn> projectColumn;

	@Property
	private List<MetaColumn> allColumnList;
	
	@Property
	private MetaColumn metaColumn;
	
	@Property
	private FinderSorter sorter;
	
	@Property
	private List<FinderSorter> sorterList;
	
	@Property
	private final String ASCENDING = PropertyProvider.get("prm.global.finder.findersorter.ascending");
	
	@Property
	private final String DESCENDING = PropertyProvider.get("prm.global.finder.findersorter.descending");

	void onActivate(){
		allColumnList = projectListPage.getAllColumnList().getAllColumns();
		sorterList = projectListPage.getSorterList();
	}
	
	Object onActivate(String param){
        if(getParameter("parameterString") != null){
           	List<MetaColumn> projectColumn = projectListPage.getAllColumnList().getAllColumns();
        	MetaColumnList updatedMetaColumnList = new MetaColumnList();
        	try {
        	JSONArray jsArray = new JSONArray(getParameter("parameterString"));
        	int columnOrder = 1;
			for(MetaColumn column : projectColumn){
	        	for (int arrayIndex = 0; arrayIndex < jsArray.length(); arrayIndex++) {
					JSONObject object = jsArray.getJSONObject(arrayIndex);
					if(column.getPropertyName().equalsIgnoreCase(object.getString("columnName"))){
						if (Boolean.parseBoolean(object.getString("columnValue"))){
							column.setInclude(true);
							column.setColumnOrder(columnOrder);
							columnOrder++;
						} else {
							column.setInclude(false);
							column.setColumnOrder(0);
						}
					}
	        	}
				updatedMetaColumnList.addMetaColumn(column);
			}
    		} catch (JSONException pnetEx) {
    			log.error("Error occured while jsonToObjectLibertal : " + pnetEx.getMessage());
    		}
			projectListPage.setProjectColumnList(updatedMetaColumnList, "");
			setSessionAttribute("MetaColumnList", updatedMetaColumnList);
	    }
        return new TextStreamResponse("text/plain", "success");
	}
	
	/**
	 * @return
	 */
	public String getSorterIdJSONString(){
		JSONObject jSONObject = new JSONObject();
		int index = 0;
		try {
			for(FinderSorter sorter : sorterList) {
				index++;
				jSONObject.put("sorter_"+index, sorter.getID());
			}
				return JSONUtils.jsonToObjectLibertal(jSONObject, null);
			} catch (JSONException pnetEx) {
				log.error("Error occured while jsonToObjectLibertal : " + pnetEx.getMessage());
			}
		return null;
	}
	
	
	/**
	 * To get html representation of sorting criteria
	 * @return htmlString
	 */
	public String getSorterPresentation(){
		String htmlString = "";
		for(FinderSorter sorter : sorterList){
			htmlString += "<tr>";
			if(sorter.isSelected())
				htmlString += "<td ><input type=\"checkbox\" id=\"" + sorter.getID()+"_selected" + "\" checked=\"checked\"/></td>";
			else
				htmlString += "<td ><input type=\"checkbox\" id=\"" + sorter.getID()+"_selected" + "\"/></td>";
			
			htmlString += "<td style=\"padding-left:10px;\">";
		    htmlString += "<select class=\"cp-sorter-dd-list\" id=\"" + sorter.getID()+"_list" + "\">";
	        //Iterate through all of the available columns and add them to the
	        //select list.
			for(Object columnObj : sorter.getColumns()){
				ColumnDefinition column = (ColumnDefinition) columnObj;
			    htmlString += "<option value=\"" + column.getColumnName() +"\" "; 
			    if (sorter.isSelected() && column.equals(sorter.getSelectedColumn()))
			    	htmlString += "selected";
			    htmlString += ">" + HTMLUtils.escape(column.getName()).replaceAll("'", "&acute;");
			}
			htmlString += "</select>";
			if(sorter.isSelected() && sorter.isDescending()){
				htmlString += "&nbsp;<input type=\"radio\" name=\"" + sorter.getID()+"_order" + "\" value=\"false\"/>&nbsp;" + ASCENDING ;
				htmlString += "&nbsp;<input type=\"radio\" name=\"" + sorter.getID()+"_order" + "\" value=\"true\"/ checked>&nbsp;" + DESCENDING; 
			} else {
				htmlString += "&nbsp;<input type=\"radio\" name=\"" + sorter.getID()+"_order" + "\" value=\"false\"/ checked>&nbsp;" + ASCENDING ;
				htmlString += "&nbsp;<input type=\"radio\" name=\"" + sorter.getID()+"_order" + "\" value=\"true\"/>&nbsp;" + DESCENDING; 
			}
			htmlString += "</tr>";
			
		}
		
		return htmlString;
	}
}
