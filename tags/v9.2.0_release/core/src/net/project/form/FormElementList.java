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
package net.project.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.util.HTMLUtils;

/**  
    A list of elements (type of form field) which are available for a particular class type.      
*/
public class FormElementList 
extends ArrayList
{
    private static final String DEFAULT_CLASS_TYPE_ID = "1";

   protected String m_classTypeID = DEFAULT_CLASS_TYPE_ID;

    // database bean
    private DBBean db = new DBBean();
    private boolean m_isLoaded = false;

    // query used to load list.
    private static final String LOAD_QUERY = "select e.element_id, e.element_type, e.element_name, e.element_desc, e.element_label, e.db_field_datatype, edc.class_id " + 
                                             "from pn_class_type_element cte, pn_element e, pn_element_display_class edc " +
                                             "where  cte.class_type_id= ? and e.element_id = cte.element_id and edc.element_id = cte.element_id and e.record_status = 'A' ";



    /** 
        Set the class type of this element list.  
        Only elements for that class type will be loaded 
    */
    public void setClassTypeID(String classTypeID)
    {
        m_classTypeID = classTypeID;
    }



    /** 
       Returns an HTML select-option list.     
       Option values are the element's m_displayClassID, option Names are the m_elementLabel.
       @param selectedElementID the elment in the list that should be marked selected.
       @return the html for the option list.
   */
    public String getHtmlOptionList(String selectedElementID)
    {
        StringBuffer sb = new StringBuffer();
        FormElement element = null;

        for (int i=0; i<this.size(); i++)
        {
            element = (FormElement) this.get(i);
            sb.append("<option value=\"" + element.m_displayClassID + "\"");

            if (element.getDisplayClassID().equals(selectedElementID))
                sb.append(" selected>");
            else
                sb.append(">");

            sb.append(HTMLUtils.escape(element.m_elementLabel));
            sb.append("</option>\n");
        }

        sb.append("</select>\n");
        return(sb.toString());
    }



    /** 
        has the list been loaded from the database.
    */
    public boolean isLoaded()
    {
        return m_isLoaded;
    }



    /** 
     * Load the element list from the database and include custom elements.
     * Custom elements are not loaded from the database; they are constructed
     * on the fly.
     */
    public void load()
    {
        FormElement element = null;

        if (m_classTypeID == null)
            throw new NullPointerException("m_classTypeID is null.  The classTypeID must be set before calling load()");

        try
        {
            db.prepareStatement (LOAD_QUERY);
            db.pstmt.setString (1, m_classTypeID);
            db.executePrepared();

            while (db.result.next())
            {
                element = (FormElement) new FormElement();
                element.m_elementID = db.result.getString("element_id");
                element.m_elementType = db.result.getString("element_type");
                element.m_elementName = db.result.getString("element_name");
                element.m_elementDescription = db.result.getString("element_desc");
                element.m_elementLabel = db.result.getString("element_label");
                element.m_db_fieldDatatype = db.result.getString("db_field_datatype");
                element.m_displayClassID = db.result.getString("class_id");

                // Resolve tokens for those fields which may contain tokens in
                // the database.
                if (PropertyProvider.isToken(element.m_elementLabel)) {
                    element.m_elementLabel = PropertyProvider.get(element.m_elementLabel);
                }

                this.add(element);
            }
            m_isLoaded = true;
        }
        catch (SQLException sqle)
        {
            System.out.println(sqle);
            m_isLoaded = false;
        }
        finally
        {
            db.release();
        }

        // Now add in the custom form elements
        // Note that all custom form elements are returned, irrespective
        // of the class type ID (that is, all class types get all custom elements)
        // Only one class type has ever been defined

        this.addAll(FormElement.getCustomFormElements());
    }

    /**
     * Returns a FormElement with the matching display class.
     * This list is loaded if necessary.
     * @param displayClassID the display class id of the element to get
     * @return the element
     */
    public FormElement getElementForDisplayClass(String displayClassID) {
        FormElement element = null;

        if (!isLoaded()) {
            load();
        }
        
        // Locate element with matching display class id
        Iterator it = iterator();
        while (it.hasNext()) {
            element = (FormElement) it.next();
            if (element.getDisplayClassID().equals(displayClassID)) {
                break;
            }
        }
        
        return element;
    }


}

