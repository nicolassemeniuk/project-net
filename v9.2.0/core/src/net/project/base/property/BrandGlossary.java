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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.RecordStatus;
import net.project.brand.Brand;
import net.project.brand.BrandManager;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Provides a glossary of brand-level tokens.
 *
 * @author Unascribed
 * @since Version 2 
 */
public class BrandGlossary extends Glossary {

    private static final String BRAND_GLOSSARY_OPEN_XML_ELEMENT = "<brand_glossary>";
    private static final String BRAND_GLOSSARY_CLOSE_XML_ELEMENT = "</brand_glossary>";

    private BrandManager brand = null;
    // Is BrandGlossary is expected to represent all tokens? - Yes : tokenPresentationSize =  -1
    // Otherwise tokenPresentationSize would be a possitive value specifying the number to represent.
    private String tokenPresentationSize = "-1"; 

    public BrandGlossary(BrandManager brand) {
        super();
        this.brand = brand;
        setIsDirty();
    }

    public BrandGlossary(String brandID, String language) throws PersistenceException {
        super();
        this.brand = new BrandManager();

        this.brand.setID(brandID);
        this.brand.setRequestedLanguage(language);

        this.brand.load();
        setIsDirty();
    }
    
    public BrandGlossary(String brandID, String language, String tokenPresentationSize) throws PersistenceException {
        super();
        this.tokenPresentationSize = tokenPresentationSize;
        this.brand = new BrandManager();

        this.brand.setID(brandID);
        this.brand.setRequestedLanguage(language);

        this.brand.load();
        setIsDirty();
    }
    
    /** ------------------------------- Abstract Glossary Method Implementation  -------------------------------*/

    /**
     * Loads a glossary without changing the global token cache.
     * This is useful for displaying and updating values from the database but
     * should not be used for general token fetching.
     * The loaded search map is modifiable.
     * @throws PersistenceException
     */
    public void load() throws PersistenceException {
        List contextOrder = null;
        Map tokenCollectionMap = null;

        try {

            // Build the list of Contexts in order
            contextOrder = buildContextPrecedenceOrder(this.brand);

            // Load a map of contexts to tokens
            tokenCollectionMap = TokenCollectionManager.loadTokensForContexts(contextOrder);
            setIsLoaded();
            setIsDirty(false);

        } catch (PropertyException pe) {
            setIsLoaded(false);
            setIsDirty();
            Logger.getLogger(BrandGlossary.class).debug("BrandGlossary.load() thew a PropertyException: " + pe);
        }

        // Now construct the search map which is based on the context order
        // and the tokens for each context
        this.tokenSearchMap = new TokenSearchMap(contextOrder, tokenCollectionMap, true);
    }

    /**
     * Loads this brand glossary.
     * It first builds a glossary precedence order which is based on the current brand in session.
     * The glossary precedence order is an ordered list of objects of type Context. We first try to
     * retrieve the respective glossary from the Application Scope else we load it from the database
     * based on the context.
     * <p>
     * The loaded search map is NOT modifiable.  Any calls {@link #set} or
     * {@link #getFilteredProperties} will result in an exception.
     * This is because the search map maintains handles to token collections
     * that are in the application-scoped token cache. These _cannot_ be
     * updated.
     * </p>
     * @param application the application scope required adding any loaded
     * tokens to the token cache
     * @throws PersistenceException if there is a problem loading
     */
    public void load(javax.servlet.ServletContext application) throws PersistenceException {
        List contextOrder = null;
        Map tokenCollectionMap = null;

        try {

            contextOrder = buildContextPrecedenceOrder(this.brand);

            // Build a map of Context to TokenCollection for each of the
            // contexts; note that the map keys are not in order
            // This will load any tokens as necessary
            tokenCollectionMap = TokenCollectionManager.getTokensForContexts(application, contextOrder);

            setIsLoaded();
            setIsDirty(false);

        } catch (PropertyException pe) {
            setIsLoaded(false);
            setIsDirty();
            Logger.getLogger(BrandGlossary.class).debug("BrandGlossary.load(application) thew a PropertyException: " + pe);
        }

        this.tokenSearchMap = new TokenSearchMap(contextOrder, tokenCollectionMap);
    }

    /**
     * Stores glossary properties to the database.
     * <p>
     * Important note: BrandGlossary does not support system properties (what does this mean?)
     * This method iterates over every token in the search map looking for
     * tokens who's language matches the current brand's active language.
     * If a token has a value or the token belongs to the system default brand
     * it is stored.  If a token doesn't have a value then it is deleted (except
     * if it is the system default brand).
     * </p>
     * <p>
     * Tokens are stored individually, resulting in one SQL statement per token.
     * </p>
     */
    public void store()
            throws PersistenceException, PropertyException {

        List deleteList = new ArrayList();

        for (Iterator it = getTokenSearchMap().iterator(); it.hasNext(); ) {
            Token token = (Token) it.next();

            if (token.isDirty() && token.getLanguage().equals(this.brand.getActiveLanguage())) {

                //Since we do not want to delete a token of the system's default brand
                if (!token.isValueNull() || token.getContextID().equals(this.brand.getSystemDefaultBrandID())) {
                    token.store();

                } else {
                    deleteList.add(token);
                }

            }

        }

        removeTokens(deleteList);
    }


    /**
     * Removes all tokens in the sepcified collection from the database.
     * Each token is removed individually.
     * @param tokens the collection of tokens to remove
     * @throws PersistenceException if there is a problem removing
     */
    private void removeTokens(Collection tokens) throws PersistenceException {
        if (tokens.size() > 0) {

            Iterator entries = tokens.iterator();

            while (entries.hasNext()) {

                Token token = (Token) entries.next();

                if (token.getContextID() != this.brand.getSystemDefaultBrandID() && token.isValueNull())
                    token.remove();

            }
        }
    }

    /* ------------------------------- Glossary Manipulation Methods  ------------------------------- */

    /**
     * Adds a new token with the specified default brand value.
     * It adds the token into the system default brand, then adds the token
     * into the token's brand.
     * @param token the token to add
     * @param defaultBrandValue the value for the default brand
     * @throws PropertyException if this glossary is not loaded or the token has no
     * contextID (that is, no brand ID) or there is a problem storing
     */
    public void addToken(Token token, String defaultBrandValue) throws PropertyException {

        if (!isLoaded() || token.getContextID() == null || token.getContextID().equals("")) {
            throw new PropertyException("BrandGlossary.addToken() failed.");
        }

        // now store the default entry
        Token defaultToken = new Token(this.brand.getSystemDefaultBrandID(),
                token.getName(), defaultBrandValue, token.getType(),
                BrandManager.SYSTEM_DEFAULT_LANGUAGE, RecordStatus.ACTIVE,
                token.isSystemProperty(), token.isTranslatableProperty());

        // Ensure token is inserted
        defaultToken.setIsNewToken();

        try {
            defaultToken.store();

            // if this isn't the default brand we are editing
            if (!token.getContextID().equals(this.brand.getSystemDefaultBrandID()) && !token.isValueNull()) {
                token.setIsNewToken();
                super.addToken(token);
            }

        } catch (PersistenceException e) {
            throw new PropertyException("Error adding token: " + e, e);

        }

    }


    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());
        return xml.toString();

    }

    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();
        TokenSearchMap map = (this.applyFilter) ? getFilteredProperties() : getTokenSearchMap();
        xml.append(BRAND_GLOSSARY_OPEN_XML_ELEMENT);
        xml.append(this.brand.getXMLBody());
        xml.append("<token_list>");
        if("-1".equals(tokenPresentationSize))
        	xml.append(map.getXMLBody());
        else
        	xml.append(map.getXMLBody(tokenPresentationSize));
        xml.append("</token_list>");
        xml.append(BRAND_GLOSSARY_CLOSE_XML_ELEMENT);
        return xml.toString();
    }




    /* ------------------------------- Manager Wrapper Methods  ------------------------------- */

    public String getSupportedLanguageOptionList() {
        return this.brand.getSupportedLanguageOptionList();
    }


    public String getBrandName() {
        return this.brand.getName();
    }

    public String getBrandID() {
        return this.brand.getID();
    }

    public String getLanguage() {
        return this.brand.getActiveLanguage();
    }

    /* ------------------------------- Utility Methods  ------------------------------- */

    /**
     * Returns a list of Contexts in correct precedence order.
     * The ordering is determined as follows:
     * <ul>
     * <li>If current brand is not the default brand, <i>Current brand, current active language</i>
     * <li>If current brand is not the default brand AND if current brand's active language is different from current brand's
     * default language AND the current brand's active language is different
     * from the default brand's default language: <i>Default brand, current brand's active language</i>
     * <li>If current brand is not the default brand: <i>Current brand, current brand's default language</i>
     * <li>If current active language is different from the default brand's default language:
     * <i>Default brand, current active language</i>
     * <li><i>Default brand, Default brand's default language</i>
     * </ul>
     * For example, consider the default brand "pnet" with default language "en"
     * that also supports language "de" and current brand "custom" with default language
     * "en" that also supports "de" and current language "de"
     * <ul>
     * <li>custom, de  (current brand, current lang)
     * <li>pnet, de    (default brand, current lang)
     * <li>custom, en  (current brand, default lang)
     * <li>pnet, de    (default brand, current lang)
     * <li>pnet, en    (default brand, default lang)
     * </ul>
     * For example, default brand "pnet" with default language "en" and
     * current brand "custom" with default language "en" and current language "en"
     * <ul>
     * <li>custom, en  (current brand, current lang)
     * <li>custom, en  (current brand, default lang)
     * <li>pnet, en    (default brand, default lang)
     * </ul>
     * For example, default brand "pnet" with default language "en" and
     * current brand "pnet" with default language "en" and current language "en"
     * <ul>
     * <li>pnet, en
     * </ul>
     * <p>
     * Clearly, there is room for improvement in this algorithm.  In general,
     * the intent is to provide the user's <i>language</i> first; starting
     * with their brand then the default brand.  Then we attempt to provide
     * their brand's <i>default language</i>, starting with their brand then
     * the default brand.  Finally we provide the default brand's default language
     * as a final position.
     * </p>
     * @param brand should have been retrieved by the constructor
     * @return a list where each element is a <code>Context</code>
     */
    private List buildContextPrecedenceOrder(BrandManager brand) throws PropertyException {

        List contextOrder = new ArrayList();
        int glossaryOrder = 0;
        Brand defaultBrand = brand.getSystemDefaultBrand();

        if (!brand.isLoaded())
            throw new PropertyException("BrandGlossary.buildContextPrecedenceOrder failed because brand is not loaded");

        // Build the contextOrder BACKWARDS

        // first set system default brand with system default language
        if (defaultBrand != null) {
            // default brand, default lang
            contextOrder.add(glossaryOrder, new Context(defaultBrand.getID(), defaultBrand.getDefaultLanguage()));
            glossaryOrder++;

            // If the brand's active language is different from the
            // default brand default language
            // then load the default brand for that active language
            if (!brand.getActiveLanguage().equals(defaultBrand.getDefaultLanguage())) {
                // default brand, current lang
                contextOrder.add(glossaryOrder, new Context(defaultBrand.getID(), brand.getActiveLanguage()));
                glossaryOrder++;
            }
        }

        // load the requested brand with the default language
        // if the requested brand is not the default brand
        if (!brand.isDefaultBrand()) {
            // current brand, current brand's default lang
            contextOrder.add(glossaryOrder, new Context(brand.getID(), brand.getDefaultLanguage()));
            glossaryOrder++;

            // if the active language is different from the default language
            // getActiveLanguage() guarantees that the brand supports the language.
            if (!brand.getActiveLanguage().equals(brand.getDefaultLanguage())) {

                // if the system default brand supports the active language
                // and the system default language is different from the active language (sys default loaded above)
                // load the active language for the system default brand
                if (defaultBrand != null &&
                        !defaultBrand.getDefaultLanguage().equals(brand.getActiveLanguage())) {

                    // default brand, current lang
                    contextOrder.add(glossaryOrder, new Context(brand.getSystemDefaultBrandID(), brand.getActiveLanguage()));
                    glossaryOrder++;
                }

                // finally add the brand glossary for the active language
                // note, no check is needed here because active language is guaranteed to be supported
                // by the brand
                if (!brand.isDefaultBrand()) {
                    // current brand, current lang
                    contextOrder.add(glossaryOrder, new Context(brand.getID(), brand.getActiveLanguage()));
                    glossaryOrder++;
                }
            }

        }


        // Now reverse the elements so that the current brand's language is
        // at the bottom
        Collections.reverse(contextOrder);
        return contextOrder;
    }

}

