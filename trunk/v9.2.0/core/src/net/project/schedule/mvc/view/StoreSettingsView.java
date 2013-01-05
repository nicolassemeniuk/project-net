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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule.mvc.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.project.base.mvc.AbstractJavaScriptView;
import net.project.base.mvc.ViewException;

public class StoreSettingsView extends AbstractJavaScriptView {
    private String javaScript = "ok";

    protected void acceptModel(Map model) throws ViewException {
        // Currently expects no model items
    }

    /**
     * Returns the length of the Javascript content to be written. <p> This is
     * used to set the response content length. </p>
     *
     * @return the length of the content or <code>-1</code> if the length is
     *         unknown
     */
    protected int getContentLength() {
        return javaScript.length();
    }

    /**
     * Writes the javascript to the specified writer. <p> Implementing classes
     * should write the Javascript for this view. The writer should not be
     * closed; it will be closed by the <code>render</code> method. </p>
     *
     * @param writer the writer to which to write the Javascript for the
     * @throws java.io.IOException if there is a problem writing
     */
    protected void writeJavascript(Writer writer) throws IOException {
        writer.write(javaScript);
    }
}
