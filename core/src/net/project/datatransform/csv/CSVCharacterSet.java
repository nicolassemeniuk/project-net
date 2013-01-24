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
package net.project.datatransform.csv;

import java.util.ArrayList;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * This class represents the character sets in which a CSV file can be produced.
 * It is mostly used to generate a dropdown option list of character sets in
 * which you'd like to download the CSV file.
 *
 * @author Matthew Flower
 * @since 7.5.1 patch 1
 */
public class CSVCharacterSet implements IHTMLOption {
    /**
     * Gets a list of <code>CSVCharacterSet</code> items that indicate the
     * available character sets.
     *
     * @return a <code>List</code> object containing zero or more
     * <code>CSVCharacterSet</code> files.
     */
    public static List getSupportedCharsets() {
        List toReturn = new ArrayList();

        for (int i = 1; PropertyProvider.isDefined("prm.global.form.list.export.charset."+i); i++) {
            CSVCharacterSet charSet = new CSVCharacterSet(
                PropertyProvider.get("prm.global.form.list.export.charset."+i+".name"),
                PropertyProvider.get("prm.global.form.list.export.charset."+i)
            );
            toReturn.add(charSet);
        }

        return toReturn;
    }

    /** This is the human-readable version of the CharacterSet. */
    private String displayName;
    /**
     * This is the code that the {@link java.nio.charset.Charset} object uses
     * to figure out which character set to load.
     */
    private String characterCode;

    public CSVCharacterSet(String displayName, String characterCode) {
        this.displayName = displayName;
        this.characterCode = characterCode;
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return characterCode;
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return displayName;
    }
}