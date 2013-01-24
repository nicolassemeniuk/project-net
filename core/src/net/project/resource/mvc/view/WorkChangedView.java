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

 package net.project.resource.mvc.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.project.base.mvc.AbstractJavaScriptView;
import net.project.base.mvc.ViewException;
import net.project.util.ErrorReporter;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a Javascript view for rendering the results after percent complete
 * changed.
 * <p>
 * Currently, this involves updating the work and work remaining values through calls
 * to Javascript functions
 * <code>setPercentComplete();</code>
 * <code>setWorkReported();</code>
 * <code>setWorkRemaining();</code>
 * <code>setEstimatedFinish();</code>
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class WorkChangedView extends AbstractJavaScriptView {

    /** The Javascript text produced. */
    private String javascriptText;

    protected void acceptModel(Map model) throws ViewException {
        this.javascriptText = getJavaScript(model);
    }

    protected int getContentLength() {
        return javascriptText.length();
    }

    protected void writeJavascript(Writer writer) throws IOException {
        writer.write(this.javascriptText);
    }

    /**
     * Provides the actual Javascript function calls.
     * @param model the model providing the work and work remaining values.
     * @return the javascript
     * @throws ViewException is missing model attributes <code>objectID</code>, <code>percentComplet</code>
     */
    private String getJavaScript(Map model) throws ViewException {
        ErrorReporter errorReporter = (ErrorReporter) getRequiredModelAttribute("errorReporter", model);
        
        NumberFormat numberFormat = NumberFormat.getInstance();
        String js = "";
       
            js+= AbstractJavaScriptView.getJavaScriptErrors(errorReporter);
	        String objectID = (String) getRequiredModelAttribute("objectID", model);
	        String dateLongName = (String)model.get("dateLongName");
	        TimeQuantity dateWork = (TimeQuantity)model.get("dateLongNameWork");
	        String spaceID =(String)model.get("spaceId");
	        String percentComplete = (String) getRequiredModelAttribute("percentComplete", model);
	        js += "setPercentComplete('"+objectID+"','"+percentComplete+"');";
	
	        TimeQuantity workComplete = (TimeQuantity)model.get("workComplete");
	        if (workComplete != null) {
	            js += "setWorkReported('"+objectID+"','"+numberFormat.formatNumber(workComplete.getAmount().doubleValue(), 0, 2)+"','"+dateLongName+"','"+spaceID+"');";
	        }
	
	        TimeQuantity workRemaining = (TimeQuantity)model.get("workRemaining");
	        if (workRemaining != null) {
	            js += "setWorkRemaining('"+objectID+"','"+workRemaining.toShortString(0,2)+"');";
	        }
	
	//        Date estimatedFinish = (Date)model.get("estimatedFinish");
	//        if (estimatedFinish != null) {
	//            DateFormat df = DateFormat.getInstance();
	//            js += "setEstimatedFinish('"+objectID+"','"+df.formatDate(estimatedFinish)+"');";
	//        }
	
	        TimeQuantity work = (TimeQuantity)model.get("work");
	        if (work != null) {
	            js += "setWork('"+objectID+"','"+numberFormat.formatNumber(work.getAmount().doubleValue(), 0, 2)+"','"+work.convertTo(TimeQuantityUnit.HOUR,2).getAmount().toString()+"');";
	        }
	        
	        String fromTimeSheet = (String)model.get("fromTimesheet");
	        
	        
	        if (Boolean.parseBoolean(fromTimeSheet) && dateWork != null) {
	            js += "setDaySummaryWork('"+dateLongName+"','"+numberFormat.formatNumber(dateWork.getAmount().doubleValue(), 0, 2)+"','"+dateWork.convertTo(TimeQuantityUnit.HOUR,2).getAmount().toString()+"');";
	        }
	        js +="setNonWorkingDayFlag('"+model.get("isNonWorkingDayWorkCapture")+"', '"+dateLongName+"');";
	        js +="setWork24DayFlag('"+model.get("isWork24DayWorkCapture")+"', '"+model.get("dateLongName")+"');";

        return js;
    }

}
