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

 /*----------------------------------------------------------------------+
|                                                                       
|    $RCSfile$
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.gui.error;


/**
 * IErrorProvider defines methods used for returning error messages and
 * flagging field labels to indicate that an error has occurred.
 * It is typically implemented by a Bean that is updated upon submission of
 * an HTML form.  Its validation routines are called prior to storage.  If
 * the Bean {@link #hasErrors} then the store would not be performed.
 *
 * <p>
 * Please read <a href="doc-files/IErrorProvider-usage.html">Usage Instructions</a>
 * for details on implementing this interface.
 * </p>
 */
public interface IErrorProvider {


    /**
     * Clears all errors.
     */
    public void clearErrors();


   /**
    * Indicates whether there are any errors.
    * @return true if there are errors; false otherwise
    */
    public boolean hasErrors();


    /**
     * Gets the Error Flag for the Field.  This method is used for
     * flagging a field label as having an error.  If an error is present
     * for the field with the specified id, the specified label is returned
     * but formatted to indicate there is an error.  Currently this uses
     * a &lt;span&gt;&lt;/span&gt; tag to specify a CSS class.  If there is no error
     * for the field with the specified id, the label is returned untouched.
     * @param fieldID the id of the field which may have the error
     * @param label the label to modify to indicate there is an error
     * @return the HTML formatted label
     */
    public String getFlagError(String fieldID, String label);


    /**
     * Gets the Error Message for the Field.
     * @param fieldID  the id of the field for which to get the error message
     * @return the HTML formatted error message
     */
    public String getErrorMessage(String fieldID);


    /**
     * Gets the Error Message for all fields.
     * @return HTML formatted error messages
     */
    public String getAllErrorMessages();


}
