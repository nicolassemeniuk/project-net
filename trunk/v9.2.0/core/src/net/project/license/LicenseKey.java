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
package net.project.license;

import java.sql.SQLException;
import java.util.Iterator;

import net.project.database.DBBean;

import org.jdom.Element;

/**
 * A LicenseKey is a human readable key that points to an existing license.
 *
 * @author Tim Morrow
 * @since Gecko Update 2
 */
public final class LicenseKey implements java.io.Serializable {

    //
    // Static members
    //

    /** 
     * The maximum number of tries to generate a unique license key,
     * currently <code>50</code>.
     */
    private static final int LICENSE_KEY_GENERATE_UNIQUE_MAX_TRIES = 50;

    /**
     * The current license key version, prefixed to license keys.
     * May help in the future to identify different meaning license keys.
     * Note that the only characters that should be used come from the
     * array of presentable characters in net.project.security.Crypto.
     */
    // Previous versions
    // private static char currentVersionCharacter = <none yet>;
    private static char currentVersionCharacter = '2';

    /**
     * Creates a license key based on the specified display license key.
     * No check is made to see if it exists.
     * @return the license key
     * @throws InvalidLicenseKeyException if the specified key is not of the
     * correct format
     */
    public static LicenseKey createLicenseKey(String displayKey)
            throws InvalidLicenseKeyException {
        
        return createLicenseKeyForValue(parseFromDisplay(displayKey));
    }


    /**
     * Creates a new, unique license key.
     * @return the unique license key
     * @throws LicenseKeyUniquenessException if an error occurred generating
     * a unique key
     * @throws LicenseKeyException if there is any other problem creating the license
     * key
     */
    public static LicenseKey createLicenseKey() 
            throws LicenseKeyUniquenessException, LicenseKeyException {
        
        return createLicenseKeyForValue(generateUniqueValue());
    }


    /**
     * Creates a license key based on the specified value.
     * No check is made to see if it exists or is appropriate.
     * @return the license key
     */
    static LicenseKey createLicenseKeyForValue(String keyValue) {
        LicenseKey key = new LicenseKey();
        key.setValue(keyValue);
        return key;
    }

    /**
     * Creates a license key from the specified license key element.
     * @param licenseKeyElement the xml element from which to create the
     * license key
     * @return the license key
     */
    static LicenseKey create(org.jdom.Element licenseKeyElement) {
        LicenseKey key = new LicenseKey();
        key.populate(licenseKeyElement);
        return key;
    }

    /**
     * Parses a display key and returns an internal value.
     * @param displayKey the display key to parse
     * @return the internal value for the display key
     */
    private static String parseFromDisplay(String displayKey) {
        char[] source = displayKey.toCharArray();
        StringBuffer result = new StringBuffer();
        
        // Iterate over source and ignore '-' characters
        for (int i = 0; i < source.length; i++) {
            if (source[i] != '-') {
                result.append(source[i]);
            }
        }

        return result.toString();
    }


    /**
     * Formats the specified internal value for display purposes.
     * This is formatted as <code>X-XXXXX-XXXXX-XXXXX-XXXXX</code>.
     * @return the formatted value
     */
    private static String formatForDisplay(String value) {
        char[] source = value.toCharArray();
        StringBuffer result = new StringBuffer();

        int length = source.length;
        int sourceIndex = 0;
        int groupCounter = 0;
        final int groupSize = 5;

        // License key version number
        result.append(source[sourceIndex++]).append('-');
        
        // Iterate over remaining source characters, adding to result
        // Breaking groups up with '-'
        while (sourceIndex < length) {
            // Add the next character to the result, incrementing pointer
            result.append(source[sourceIndex++]);

            // If loop condition still holds (that is, we're not done yet)
            // AND we've counted up 5 positions
            // Add '-'
            if ((sourceIndex < length) && (++groupCounter == groupSize)) {
                result.append('-');
                groupCounter = 0;
            }
        }
        
        return result.toString();
    }


    /**
     * Generates a unique value.
     * @return the unique key value with no special formatting.
     * @throws LicenseKeyUniquenessException if there is a problem generating
     * a unique license key; that is the attempt was aborted after a number
     * of tries given by <code>{@link #LICENSE_KEY_GENERATE_UNIQUE_MAX_TRIES}</code>
     * @throws LicenseKeyException if there is some other problem generating
     * a value
     */
    private static String generateUniqueValue()
            throws LicenseKeyUniquenessException, LicenseKeyException {

        StringBuffer uniqueValue = null;
        boolean isUnique = false;

        // Construct query to look for license key with same value
        StringBuffer query = new StringBuffer();
        query.append("select 1 ");
        query.append("from pn_license ");
        query.append("where license_key_value = ? ");

        // Generate unique value
       DBBean db = new DBBean();
       try {
           db.prepareStatement(query.toString());

           // We abort the loop after a certain number of tries
           // to avoid any potential for infinite looping
           // A well defined exception is thrown in this case
           // Note that tests have shown that when generating 100,000
           // 20 character strings (in a tight loop) using generatePresentableRandomCharacterString
           // ZERO duplicates were returned.  This is due to the effectiveness of
           // the SecureRandom class

           // Loop, constructing a new value and checking it is unique
           for (int count = 0; count < LICENSE_KEY_GENERATE_UNIQUE_MAX_TRIES && !isUnique; count++) {
               // Make next try
               uniqueValue = new StringBuffer();
               uniqueValue.append(LicenseKey.currentVersionCharacter);
               uniqueValue.append(net.project.security.Crypto.generatePresentableRandomCharacterString(20));

               // Try it
               db.pstmt.setString(1, uniqueValue.toString());
               db.executePrepared();

               if (db.result.next()) {
                   // Found it.  Not unique.
                   // Simply continue in loop
               } else {
                   // Not found.  It is unique.
                   isUnique = true;
               }

           }

       } catch (SQLException sqle) {
           throw new LicenseKeyException("License key generate operation failed: " + sqle, sqle);

       } finally {
           db.release();

       }

       if (!isUnique) {
           // We exited without finding a unique license key
           // A serious problem occurred
           throw new LicenseKeyUniquenessException("Failed to generate unique license key after " + LICENSE_KEY_GENERATE_UNIQUE_MAX_TRIES + " tries");
       }

       return uniqueValue.toString();
    }


    //
    // Instance attributes and methods
    //

    /** The value of this license key. */
    private String value = null;

    /**
     * Creates an empty LicenseKey.
     */
    LicenseKey() {
        // Do Nothing
    }

    /**
     * Creates a  LicenseKey with given value.
     */
    LicenseKey(String licenseKey) {
        this.setValue(licenseKey);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(super.toString()).append("\n");
        result.append("value: ").append(getValue()).append("\n");
        return result.toString();
    }

    /**
     * Indicates whether specified object is equal to this license key.
     * @param obj the license key to compare
     * @return true if the specified license key's value is equal to this
     * license key's value
     */
    public boolean equals(Object obj) {
        if (obj instanceof LicenseKey &&
            obj != null &&
            ((LicenseKey) obj).getValue().equals(getValue())) {

            return true;
        }

        return false;
    }


    /**
     * Sets this license key's value.
     * @param value the internal value for this license key
     * @see #getValue
     */
    private void setValue(String value) {
        this.value = value;
    }


    /**
     * Returns this license key's internal value.
     * @return the value
     * @see #setValue
     */
    String getValue() {
        return this.value;
    }


    /**
     * Returns the display representation of this key.
     * This is formatted as <code>X-XXXXX-XXXXX-XXXXX-XXXXX</code>.
     * @return the license key formatted for display
    */
    public String toDisplayString() {
        return LicenseKey.formatForDisplay(getValue());
    }


    /**
     * Returns the XML format of this license key suitable for storage.
     * @return the storage XML
     */
    String serializeXMLBody() {
        String xml = null;

        try {
            net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
            doc.startElement("LicenseKey");
            doc.addElement("Value", getValue());
            doc.addElement("DisplayString", toDisplayString());
            doc.endElement();
            xml = doc.getXMLBodyString();
        
        } catch (net.project.xml.document.XMLDocumentException xde) {
            // Suck it up
            xml = "";
        
        }

        return xml;
    }

   /**
     * Returns the xml Element for this LicenseKey.
     * @return the element
     */
    public Element getXMLElement() {
        Element rootElement = new Element("LicenseKey");
        rootElement.addContent(new Element("Value").addContent(getValue()));
        rootElement.addContent(new Element("DisplayString").addContent(toDisplayString()));;
        return rootElement;
    }

    /**
     * Populates this license model from the xml element.
     * The element can be assumed to be of the correct type for the license model.
     * @param element the xml element from which to populate this license model
     */
    protected void populate(Element element) {
        
        // Iterate over each child element of this LicenseKey element
        // and handle each one
        Element childElement = null;
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            childElement = (Element) it.next();

            if (childElement.getName().equals("Value")) {
                // Value contains a string
                setValue(childElement.getTextTrim());
            
            } else if (childElement.getName().equals("DisplayString")) {
                // Ignored, this is read-only and is always
                // constructed from the value

            }
        }

    }

    /**
     * Returns this license key as xml.
     * @return the xml for this license key
     */
    net.project.xml.document.XMLDocument getXMLDocument() {
        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();
        
        try {
            doc.startElement("LicenseKey");
            doc.addElement("Value", getValue());
            doc.addElement("DisplayString", toDisplayString());
            doc.endElement();
        } catch (net.project.xml.document.XMLDocumentException e) {
            // Nothing much can be done about this
            // Simply return the empty document
        }

        return doc;
    }

    
//     //
//     // Unit Test
//     //
//
//     public static void main(String[] args) {
//         try {
//             long start;
//
//             start = System.currentTimeMillis();
//             LicenseKey key = createLicenseKey();
//             System.out.println("Elapsed time for first license key: " + (System.currentTimeMillis() - start));
//             System.out.println("New license key:");
//             System.out.println("\tInternal value: " + key.getValue());
//             System.out.println("\tDisplay value: " + key.toDisplayString());
//
//             for (int i = 0; i < 20; i++) {
//                 start = System.currentTimeMillis();
//                 key = createLicenseKey();
//                 System.out.println("Elapsed time for key: " + (System.currentTimeMillis() - start));
//                 System.out.println("New license key:");
//                 System.out.println("\tInternal value: " + key.getValue());
//                 System.out.println("\tDisplay value: " + key.toDisplayString());
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
}
