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
import net.project.util.TimeQuantity;
import net.project.util.TimeQuantityUnit;

/**
 * Provides a Javascript view for rendering the results after percent complete
 * changed.
 * <p>
 * Currently, this involves updating the work and work remaining values through calls
 * to Javascript functions <code>setWork(scheduleEntryID, workDisplay, workAmount);</code>
 * and <code>setWorkRemaining(scheduleEntryID, workRemainingDisplay);</code>.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public class PercentCompleteChangedView extends AbstractJavaScriptView {

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
     * @return the javascript produced from the model
     * @throws ViewException if missing model attributes <code>objectID</code>, <code>work</code> or <code>workRemaining</code>
     */
    private String getJavaScript(Map model) throws ViewException {
        String javaScript = "";
        ErrorReporter errors = (ErrorReporter)getRequiredModelAttribute("errors", model);
        String objectID = (String) getRequiredModelAttribute("objectID", model);

        if (errors.errorsFound()) {
            javaScript += getJavaScriptErrors(errors);
            javaScript += "setPercentComplete('"+objectID+"','"+model.get("percentComplete")+"');";
        } else {
            TimeQuantity work = (TimeQuantity) getRequiredModelAttribute("work", model);
            TimeQuantity workRemaining = (TimeQuantity) getRequiredModelAttribute("workRemaining", model);

            javaScript += "setWork('" + objectID + "','"+ work.toShortString(0,2) + "','" + work.convertTo(TimeQuantityUnit.HOUR,2).getAmount().toString() + "');";
            javaScript += "setWorkRemaining('" + objectID + "','" + workRemaining.toShortString(0,2) + "');";
        }

        return javaScript;
    }

}