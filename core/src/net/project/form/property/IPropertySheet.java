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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form.property;

import net.project.form.Form;
import net.project.form.FormElement;
import net.project.persistence.IJDBCPersistence;

/**
 * IPropertySheet interface defines the methods that a property sheet must
 * support.
 */
public interface IPropertySheet extends IJDBCPersistence {

    /**
     * Sets the current space context.
     * @param space the current space context
     */     
    public void setSpace(net.project.space.Space space);
                                                                                                                   
    /**
     * Sets the current user context
     * @param user the current user context
     */
    public void setUser(net.project.security.User user);

    /**
     * Sets the form that the property sheet is for.
     * @param form the form
     */
    public void setForm(net.project.form.Form form);

    /**
     * Sets the field that the property sheet is for.
     * @param field the field
     */
    public void setManagedField(net.project.form.FormField field);

    /**
     * Writes the property sheet as HTML.
     * This should return the property sheet that is suitable for insertion
     * in a table division.<br>
     * There are a number of special HTML form fields that may be present
     * in the property sheet.  These are described here:<br>
     * <ul>
     * <li><code>classID</code> - The form class id of the form that owns the
     * field to which this property sheet belongs.  This is mandatory.</li>
     * <li><code>fieldID</code> - The field id of the field to which this
     * property sheet belongs</li>
     * <li><code>clientTypeID</code> - The type of client to which the property
     * values belong.  See {@link Form} for client type constants</li>
     * <li><code>elementID</code> - The id of the element type of the field
     * to which this property sheet belongs. See {@link FormElement} for the
     * selection of element ids.</li>
     * <li><code>elementName</code> - The element name</li>
     * <li><code>elementLabel</code> - The element label</li>
     * <li><code>field_label</code> - The field label</li>
     * <li><code>row_num</code> - The row number of the field when displayed</li>
     * <li><code>row_span</code> - The row span of the field when displayed</li>
     * <li><code>column_id</code> - The column in which the field should be
     * rendered.  One of {@link Form#LEFT_COLUMN}, {@link Form#RIGHT_COLUMN},
     * {@link Form#BOTH_COLUMNS}</li>
     * <li><code>field_group</code> - </li>
     * <li><code>domain_id</code> - The id of the domain of values for this form</li>
     * <li><code>data_column_size</code> - The storage column size (or precision)</li>
     * <li><code>data_column_scale</code> - The storage column scale</li>
     * <li><code>instructions</code> - The instructions for this field</li>
     * <li><code>is_multi_select</code> - Whether this field permits multi-select</li>
     * <li><code>use_default</code> - ?</li>
     * <li><code>datatype</code> - The storage datatype of the field</li>
     * </ul>
     * Some parameters should be specifically ignored:<br>
     * <code>theAction, ElementID, id, module, action</code>
     * The remaining parameters are properties specific for the field being modified.
     * @param out the PrintWriter to write to
     * @throws java.io.IOException if there is a problem writing
     */
    public void writeHtml(java.io.PrintWriter out)
            throws java.io.IOException;

    /**
     * Indicates whether this Property Sheet has a domain list of values.
     * If <code>true</code> is returned, then the managed form field
     * should return the domain via its {@link net.project.form.FormField#getDomain} method.
     * @return true if this property sheet has a domain list of values; false
     * otherwise
     */
    public boolean hasDomain();

    /**
     * Process the request containing parameters for setting this property 
     * sheet's values before storing.
     * See {@link #writeHTML} for details on the special parameters that
     * may be specified in the request.
     * @param request the request containing parameters.
     * @throws net.project.form.FormException if there is a problem processing the request
     */
    public void processHttpPost(javax.servlet.ServletRequest request)
            throws net.project.form.FormException;

    /**
     * Returns the id of this Property Sheet.
     * @return the id
     */
    public String getID();

    int getDesignerFieldCount();
}
