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
package net.project.resource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * Language
 */
public class Language
implements java.io.Serializable, IXMLPersistence {

    private String languageCode = null;
    private String languageName = null;
    private String characerSet = null;

    private DBBean db = null;


    /**
     * Returns all defined languages
     */
    public static LanguageList getLanguages() {
        LanguageList languages = new LanguageList();
        try {
            languages.load();
        } catch (PersistenceException pe) {
            // Simply return empty language list
        }
        return languages;
    }

    Language() {
        this.db = new DBBean();
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return PropertyProvider.get(this.languageName);
    }

    void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getCharacterSet() {
        return this.characerSet;
    }

    void setCharacterSet(String characterSet) {
        this.characerSet = characterSet;
    }

    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<Language>");
        xml.append(getXMLElements());
        xml.append("</Language>");
        return xml.toString();
    }

    public String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<LanguageCode>" + XMLUtils.escape(getLanguageCode()) + "</LanguageCode>");
        xml.append("<LanguageName>" + XMLUtils.escape(getLanguageName()) + "</LanguageName>");
        xml.append("<CharacterSet>" + XMLUtils.escape(getCharacterSet()) + "</CharacterSet>");
        return xml.toString();
    }


    /**
     * LanguageList is a list of all languages. 
     */
    public static class LanguageList extends ArrayList implements java.io.Serializable {

        private DBBean db = null;

        private LanguageList() {
            db = new DBBean();
        }

        /**
         * Returns the language which has the specified code
         * @return language which has specified language code or null if
         * no language has specified language code
         */
        public Language getLanguageForCode(String languageCode) {
            Language language = null;
            Language foundLanguage = null;

            Iterator it = iterator();
            while (it.hasNext()) {
                language = (Language) it.next();
                if (language.getLanguageCode().equals(languageCode)) {
                    foundLanguage = language;
                    break;
                }
            }

            return foundLanguage;
        }


        /** 
         *  load the object from database persistence.
         */
        private void load() throws PersistenceException {
            Language language = null;
            StringBuffer query = new StringBuffer();

            query.append("select l.language_code, l.language_name, l.character_set ");
            query.append("from pn_language l ");

            try {
                db.executeQuery(query.toString());

                while (db.result.next()) {
                    language = new Language();

                    language.setLanguageCode(db.result.getString("language_code"));
                    language.setLanguageName(db.result.getString("language_name"));
                    language.setCharacterSet(db.result.getString("character_set"));

                    add(language);
                }

            } catch (SQLException sqle) {
            	Logger.getLogger(Language.class).error("LanguageList.load() threw an SQLException: " + sqle);
                throw new PersistenceException("Language list load operation failed.", sqle);

            } finally {
                db.release();
            }
        }

    }

}

