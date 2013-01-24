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

 package net.project.base.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.security.SessionManager;
import net.project.util.ErrorDescription;
import net.project.util.ErrorReporter;
import net.project.util.StringUtils;

/**
 * Provides a view that returns Javascript.
 * <p>
 * The response content type is set to <code>text/javascript</code>.
 * Implementing classes must generate the actual Javascript.
 * </p>
 * @author Tim Morrow
 * @since Version 7.7
 */
public abstract class AbstractJavaScriptView implements IView {

    //
    // Static Members
    //

    /**
     * A helper method to format errors into Javascript function calls.
     * <p>
     * Errors are of the form: <pre><code>
     *   flagError('Error message');</code></pre>
     * </p>
     * @param errorReporter the error reporter containing the errors
     * @return
     */
    protected static String getJavaScriptErrors(ErrorReporter errorReporter) {
        StringBuffer result = new StringBuffer();

        buildJavascriptError(result, errorReporter.getOverallError());

        for (Iterator iterator = errorReporter.getErrorDescriptions().iterator(); iterator.hasNext();) {
            ErrorDescription nextError = (ErrorDescription) iterator.next();
            if(StringUtils.isNotEmpty(nextError.getFieldName())){
            	buildJavascriptError(result, nextError.getErrorText(), nextError.getFieldName());
            } else {
            	buildJavascriptError(result, nextError.getErrorText());
            }            
        }

        return result.toString();
    }

    /**
     * Constructs a Javascript error call for the specified text, handling
     * null cases.
     * @param result the string buffer to append the error call to
     * @param text the text to produce a Javascript error call for; when null, no
     * error call is produced
     */
    private static void buildJavascriptError(StringBuffer result, String text) {
        if (text != null) {
            result.append("flagError(" + formatJavascriptString(text) + ");");
        }
    }
    
    /**
     * Constructs a Javascript error call for the specified text, handling
     * null cases.
     * @param result the string buffer to append the error call to
     * @param text the text to produce a Javascript error call for; when null, no
     * @param fieldName the fieldName to set the focus
     * error call is produced
     */
    private static void buildJavascriptError(StringBuffer result, String text, String fieldName) {
        if (text != null) {
            result.append("flagError("+ formatJavascriptString(text) +"," + formatJavascriptString(fieldName) + ");");
        }
    }
    /**
     * Formats the specified text as a javascript string.
     * <p>
     * The text is surrounded by single quotes and single-quotes are replaced
     * with double-quotes.
     * </p>
     * @param text the text to format
     * @return the text formatted as a Javascript string
     */
    protected static String formatJavascriptString(String text) {
        return "'" + text.replace('\'', '"') + "'";
    }

    /**
     * Returns the model attribute for the specified name, throwing an exception if it is not found.
     * @param attributeName the name of the attribute to get
     * @param model the model from which to get the attribute
     * @return the attribute
     * @throws ViewException if no attribute is found for that name in the model
     */
    protected static Object getRequiredModelAttribute(String attributeName, Map model) throws ViewException {
        Object modelAttribute = model.get(attributeName);
        if (modelAttribute == null) {
            throw new ViewException("Missing model attribute '" + attributeName + "'");
        }
        return modelAttribute;
    }
   

    //
    // Instance Members
    //

    /**
     * Render the model as Javascript.
     * @param model a <code>Map</code> containing name to object mappings for
     * the objects required to render the view.
     * @param request a <code>HttpServletRequest</code> object which contains
     * the parameters passed to the controller servlet.
     * @param response a <code>HttpServletResponse</code> object which allows us
     * to render the view.
     * @throws Exception if an error occurs while rendering the view.
     */
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        acceptModel(model);

        // Ensure browsers (mostly IE) that the response is not cached; this is required
        // when browsers have an "Automatically" setting for caching responses
        // It is particularly vital for round-trip logic where identical requests often
        // result in the same response, which seems to encourage IE to ignore future responses,
        // even though they may have changed
        
        
        response.setCharacterEncoding(SessionManager.getCharacterEncoding());
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader ("Expires", 0);

        response.setContentType("text/javascript");
        //response.setContentLength(getContentLength());
        PrintWriter writer = response.getWriter();
        writeJavascript(writer);
        writer.close();
    }

    /**
     * Provides the model during rendering.
     * <p>
     * This is provided at the beginning of the render process to allow implementing
     * classes to process the model in order to provide content length before actually
     * writing the view.
     * </p>
     * <p>
     * It guaranteed to be called before {@link #getContentLength()} or {@link #writeJavascript(java.io.Writer)}.
     * </p>
     * @param model the model from which to render the view
     * @throws ViewException if there is a problem rendering the view, for example
     * missing attributes
     */
    protected abstract void acceptModel(Map model) throws ViewException;

    /**
     * Returns the length of the Javascript content to be written.
     * <p>
     * This is used to set the response content length.
     * </p>
     * @return the length of the content or <code>-1</code> if the length is unknown
     */
    protected abstract int getContentLength();

    /**
     * Writes the javascript to the specified writer.
     * <p/>
     * Implementing classes should write the Javascript for this view.
     * The writer should not be closed; it will be closed by the
     * <code>render</code> method.
     * <p/>
     * It is recommend that in the event of errors, {@link #getJavaScriptErrors} is
     * called to produce an error view.
     * @param writer the writer to which to write the Javascript for the
     * @throws IOException if there is a problem writing
     * @see #formatJavascriptString(java.lang.String)
     */
    protected abstract void writeJavascript(Writer writer) throws IOException;

}
