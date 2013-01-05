/**
 * 
 */
package net.project.view.components;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.util.StringUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * This component is used to used to generate a drop down list
 * To use this following parameters should be specified -
 * @param list - collection of object containing options to be displayed in drop down list.
 * @param id - id of this drop down list
 * @param displayField - name of field used to get display value in option
 * @param valueField - name of field used to get value in option
 * 
 * @author Ritesh S
 *
 */
public class ComboBox {
	
	@Inject
    private Logger log;

	@Parameter(required = true)
	private List<Object> list;

	@Parameter(required = true, defaultPrefix = "literal")
	private String id;
	
	@Parameter(required = true, defaultPrefix = "literal")
	private String displayField;
	
	@Parameter(required = true, defaultPrefix = "literal")
	private String valueField;

	@Parameter(required = false, defaultPrefix = "literal")
	private String cssClass;

	@Parameter(required = false, defaultPrefix = "literal")
	private String value;

	@Parameter(required = false, defaultPrefix = "literal")
	private Boolean defaultOption;

	@Parameter(required = false, defaultPrefix = "literal")
	private String defaultOptionText;

	@Parameter(required = false, defaultPrefix = "literal")
	private String events;

	@Parameter(required = false, defaultPrefix = "literal")
	private String action;

	private String htmlString;

	public String getPresentationString() {
		htmlString = "<select id="+id+"";
		if(StringUtils.isNotEmpty(cssClass))
			htmlString += " class="+cssClass+"";

		if(StringUtils.isNotEmpty(events) && StringUtils.isNotEmpty(events)){
			String eventsArray[] = events.split(",");
			String actionArray[] = action.split(",");
			if(eventsArray.length == actionArray.length)
				for (int index = 0; index < eventsArray.length; index++)
					htmlString += "  " + eventsArray[index] +"=" + actionArray[index] + "";
		}
		htmlString += ">";
		if(defaultOption && StringUtils.isNotEmpty(defaultOptionText))
			htmlString += " <option value=\"\">"+PropertyProvider.get(defaultOptionText)+"</option>";
		else if(defaultOption)
			htmlString += " <option value=\"\"></option>";

		if(StringUtils.isNotEmpty(value))
			htmlString += HTMLOptionList.makeHtmlOptionList(gethtmlOptionCollection(), value);
		else
			htmlString += HTMLOptionList.makeHtmlOptionList(gethtmlOptionCollection());
		
		htmlString += "</select>";	
		return htmlString;
	}

	public Collection gethtmlOptionCollection(){
		List htmlOptions = new ArrayList();
		
		if (CollectionUtils.isNotEmpty(list))
        	for(Object option : list){
        		try{
            		htmlOptions.add(new HTMLOption(BeanUtils.getProperty(option, valueField),BeanUtils.getProperty(option, displayField)));
        		} catch (InvocationTargetException pnetEx) {
        	    	log.error("Error occurred while generating combo box :" + pnetEx.getMessage()); 
				} catch (IllegalAccessException pnetEx) {
        	    	log.error("Error occurred while generating combo box :" + pnetEx.getMessage()); 
				} catch (NoSuchMethodException pnetEx) {
        	    	log.error("Error occurred while generating combo box :" + pnetEx.getMessage()); 
				}
        		
	        }
	   return htmlOptions;
	}
	
}
