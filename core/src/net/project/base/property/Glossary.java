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
| Singleton PropertyProvider
+----------------------------------------------------------------------*/

package net.project.base.property;

import java.util.Iterator;

import net.project.base.RecordStatus;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLFormatter;

/**
 * A glossary provides persistence mechanisms for loading and storing
 * properties.
 * <p>
 * It is used both for loading properties at runtime and for loading properties
 * during for modification purposes.
 * </p>
 * @author Unascribed
 * @since Version 2
 */
public abstract class Glossary implements java.io.Serializable {

    /* ------------------------------- Class Members  ------------------------------- */

    /**
     * The token search map of this glossary.
     * This provides the tokens for this glossary.
     * */
    TokenSearchMap tokenSearchMap = null;

    private PropertiesFilter filter = null;

    /** is loaded */
    private boolean isLoaded = false;

    boolean applyFilter = false;

    private String stylesheet = null;

    /* -------------------------------   Constructors  ------------------------------- */

    public Glossary() {
        this.filter = new PropertiesFilter();
    }


    /* -------------------------------   public Interface Methods  ------------------------------- */

    public abstract void load() throws PersistenceException;

    protected abstract String getXML();

    public abstract String getXMLBody();




    /* ------------------------------- Glossary Manipulation Methods  ------------------------------- */

    public void set(String name, String value, String type, String contextID, String language, boolean isSystemProperty, boolean isTranslatableProperty) throws PropertyException {

        if (!this.isLoaded)
            throw new PropertyException("Glossary.set() failed:  Glossary is not loaded");

        Token token = new Token(contextID, name, value, type, language,
                RecordStatus.ACTIVE, isSystemProperty, isTranslatableProperty);

        if (hasTokenChanged(token))
            token.setIsDirty();

        //this.glossary.setTokenProperty (token);
        this.tokenSearchMap.setTokenForContext(token, new Context(contextID, language));
    }

    /**
     * Gets the token search map of this glossary.
     * @return the token search map of this glossary.
     */
    public TokenSearchMap getTokenSearchMap() {
        return (this.tokenSearchMap);
    }

    TokenSearchMap getFilteredProperties() {

        // Filter the search map
        for (Iterator it = this.tokenSearchMap.iterator(); it.hasNext(); ) {
            Token nextToken = (Token) it.next();

            if (!this.filter.matchesFilter(nextToken)) {
                it.remove();
            }
        }

        return this.tokenSearchMap;
    }

    private boolean hasTokenChanged(Token token) {

        boolean hasChanged = false;

        if (this.tokenSearchMap != null) {

            Token temp = tokenSearchMap.getTokenNoDefaults(token.getName());

            if (temp == null && token != null && !token.isValueNull()) {
                token.setIsNewToken();
                hasChanged = true;
            } else if (temp == null && token != null && token.isValueNull()) {
                hasChanged = false;
            } else
                hasChanged = (token.equalsValue(temp)) ? false : true;
        }

        return hasChanged;
    }


    /* ------------------------------- XML/Stylesheet Support Methods  ------------------------------- */

    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName) {
        this.stylesheet = styleSheetFileName;
    }

    /**
     * Gets the presentation of the component
     * This method will apply the stylesheet to the XML representation of the component and
     * return the resulting text
     *
     * @return presetation of the component
     */
    public String getPresentation() {
        // return the XML representation of the container's contents
        XMLFormatter xmlFormatter = new XMLFormatter();
        xmlFormatter.setStylesheet(stylesheet);
        return xmlFormatter.getPresentation(getXML());
    }



    /* -------------------------------   Glossary Manipulation Methods  ------------------------------- */

    /**
     * Adds a token without specifying a value for the default brand.
     * This means that this method should only be called when it has already
     * been added to the default brand.
     * @param token the token to add
     * @throws PropertyException if this glossary is not loaded or the token
     * has no context ID (that is, brand ID) or the token has no value or
     * there is a problem inserting into the database
     */
    void addToken(Token token) throws PropertyException {

        if (!isLoaded() || token.getContextID() == null || token.getContextID().equals("") || token.isValueNull()) {
            throw new PropertyException("Glossary.addToken() failed: context or value is null, or glossary not loaded");
        }

        try {
            token.store();

        } catch (PersistenceException e) {
            throw new PropertyException("Error storing token: " + e, e);

        }

    }


    /**------------------------------ The Setters ------------------------------------------*/
    public void setPropertiesFilter(PropertiesFilter filter) {
        this.filter = filter;
    }

    void setIsDirty(boolean flag) {
    }

    void setIsDirty() {
        setIsDirty(true);
    }

    void setIsLoaded(boolean flag) {
        this.isLoaded = flag;
    }

    public void applyFilter(boolean flag) {
        this.applyFilter = flag;
    }

    void setIsLoaded() {
        setIsLoaded(true);
    }

    boolean isLoaded() {
        return this.isLoaded;
    }


} // end class Glossary
