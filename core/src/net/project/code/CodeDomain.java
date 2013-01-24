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
package net.project.code;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.gui.html.HTMLOptionList;
import net.project.persistence.IXMLPersistence;


/**
 * An abstract group of codes.
 */
public abstract class CodeDomain implements ICodeDomain, IXMLPersistence {

    /**
     * All the codes in this Domain.
     */
    protected ArrayList codes = null;


    /*************************************************************************************************
     * Implementing ICodeDomain.
     *************************************************************************************************

    /**
     * Get all the codes including the default code ordered by the presentation sequence.
     * @return a list where each element is a <code>Code</code>
     */
    public ArrayList getCodes() {
        return this.codes;
    }

    /**
     * Returns the display name of the code with the specified internal code.
     * @return the display name of the default code; returns null if the
     * specified code is null or the current domain has no codes or has no
     * code with the matching value
     */
    public String getCodeName(String code) {
        String name = null;

        if (codes != null && code != null) {

            // Find the code with matching code
            Code foundCode = findByCode(code);
            if (foundCode != null) {
                name = foundCode.getName();
            }
        }

        return name;
    }


    /**
     * Returns all the codes EXCEPT the default code ordered by the presentation sequence.
     * @return a List of <code>Code</code>s or null if the current codes are
     * null
     */
    public ArrayList getNonDefaultCodes() {

        ArrayList nonDefaultCodes = null;

        if (getCodes() != null) {
            nonDefaultCodes = new ArrayList();

            for (Iterator it = getCodes().iterator(); it.hasNext(); ) {
                Code nextCode = (Code) it.next();
                if (!nextCode.isDefault) {
                    nonDefaultCodes.add(nextCode);
                }
            }

        }

        return nonDefaultCodes;
    }


    /**
     * Returns the default code.
     */
    public Code getDefaultCode() {
        if (codes == null)
            return null;

        for (int i = 0; i < codes.size(); i++) {
            if (((Code) codes.get(i)).isDefault)
                return (Code) codes.get(i);
        }
        return null;
    }

    //
    // Utility Methods
    //


    /**
     * Sets the code with the specified ID to be selected by default.
     * @param defaultCodeID the id of the code to selected by default
     */
    public void setDefaultCodeID(String defaultCodeID) {

        // First reset the current default code
        getDefaultCode().isDefault = false;

        // Now set the new default code
        Code newDefaultCode = findByID(defaultCodeID);
        if (newDefaultCode != null) {
            newDefaultCode.isDefault = true;
        }
    }

    /**
     * Returns the code with matching object id.
     * @param objectID the id of the code to get
     * @return the code or null if none has matching id
     */
    private Code findByID(String objectID) {
        Code foundCode = null;

        for (Iterator it = getCodes().iterator(); it.hasNext(); ) {
            Code nextCode = (Code) it.next();
            if (nextCode.getObjectID().equals(objectID)) {
                foundCode = nextCode;
                break;
            }
        }

        return foundCode;
    }

    /**
     * Returns the code with matching code value.
     * @param code the code value of the code to get
     * @return the code or null if none has matching value
     */
    private Code findByCode(String code) {
        Code foundCode = null;

        for (Iterator it = getCodes().iterator(); it.hasNext(); ) {
            Code nextCode = (Code) it.next();
            if (nextCode.getCode().equals(code)) {
                foundCode = nextCode;
                break;
            }
        }

        return foundCode;
    }

    /**
     * Returns an HTML option list of the codes in this domain.
     * The default code is selected.
     * @return the HTML options
     */
    public String getOptionList() {
        return getOptionList(null);
    }

    /**
     * Returns an HTML option list of the codes in this domain.
     * The default code is set to selected unless overrideDefault is
     * specified and is not null; in that case it is selected.
     * If no code is found with that id, no code is selected
     * @param overrideDefault the object id of the code to select instead
     * of the default
     * @return the HTML options
     */
    public String getOptionList(String overrideDefault) {

        String selectCodeID = null;
        if (overrideDefault != null) {
            selectCodeID = overrideDefault;
        } else {
            Code defaultCode = getDefaultCode();
            if (defaultCode != null) {
                selectCodeID = defaultCode.getObjectID();
            }
        }

        return HTMLOptionList.makeHtmlOptionList(getCodes(), selectCodeID);
    }


    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        String tab = null;
        Iterator iterator = codes.iterator();

        Code code = null;

        xml.append("<code_domain>\n");

        // now get the rest of the codes

        while (iterator.hasNext()) {

            code = (Code) iterator.next();

            tab = "\t";
            xml.append(tab + "<code>\n");
            xml.append(code.getXML());
            xml.append(tab + "</code>\n\n");

        } // end while

        // close the xml statement
        xml.append("</code_domain>\n");

        return xml.toString();
    }

}