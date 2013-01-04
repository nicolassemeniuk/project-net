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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.form;

/**
 * FormListResult represents the results of loading the data for a specific
 * FormList.  It provides functionality for manipulating the form data
 * returned for the form list.
 * @since emu
 * @author Tim
 */
public class FormListResult 
extends FormDataList
implements java.io.Serializable
{

    /** Form List from which form data was loaded */
    private FormList formList = null;

    /**
     * Creates empty form list results for the specified form list.
     * @param formList the form list to which these results apply
     */
    FormListResult(FormList formList) {
        this.formList = formList;
    }

    /**
     * Creates empty form list results for specified form list.
     * @param formList the form list to which these results apply
     * @param initialCapacity the initial capacity of the list
     * @see FormDataList#FormDataList(int)
     */
    FormListResult(FormList formList, int initialCapacity) {
        super(initialCapacity);
        this.formList = formList;
    }
    /**
      * Returns the list as XML (including the XML version tag).
      * This method returns the object as XML text.
      * @return XML representation of the object including XML version tag
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
            getXMLBody();
    }

    /**
     * Returns the list as XML (including the XML version tag).
     * This method returns the object as XML text.  The XML includes the
     * form list XML and the form data xml.  For example:
     * <pre>
     * &lt;FormListResult&gt;
     *     &lt;FormList&gt; ... &lt;/FormList&gt;
     *     &lt;FormDataList&gt; ... &lt;/FormDataList&gt;
     * &lt;/FormListResult&gt; </pre>
     * @return XML node representation
     * @see FormList#getXMLBody
     * @see FormDataList#getXMLBody
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        FormData formData = null;

        xml.append("<FormListResult>");

        // Include the <FormList /> xml
        xml.append(this.formList.getXMLBody());

        // Include the <FormDataList /> xml
        xml.append(super.getXMLBody());

        xml.append("</FormListResult>");

        return xml.toString();
    }

}
