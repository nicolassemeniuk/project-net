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
|
+----------------------------------------------------------------------*/
package net.project.security.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.persistence.PersistenceException;

/**
 * Class to produce the list of domain options for the login page.
 *
 * @author Matthew Flower
 * @since Gecko
 */
public class DomainOptionList extends ArrayList {

    private String configurationID = null;

    /**
     * Gets the value of configurationID.
     *
     * @return the value of configurationID
     */
    public String getConfigurationID() {
        return this.configurationID;
    }

    /**
     * Sets the value of configurationID.
     *
     * @param argConfigurationID Value to assign to this.configurationID
     */
    public void setConfigurationID(String argConfigurationID){
        this.configurationID = argConfigurationID;
    }


    /**
     * Returns this domain option list as an HTML option list.
     * Each value is the domain's ID, this display is the domain's name.
     * No options are selected.
     * @return the HTML option list
     */
    public String getDomainOptionList() {
        return getDomainOptionList(null);
    }
    
    /**
     * Returns this domain option list as an HTML option list with
     * the specified values selected.
     * Each value is the domain's ID, the display string is the domain's
     * name.  Each option value that matches an element in the specified
     * array as the attribute <code>SELECTED</code<.
     * @param defaultOptions the options to select; if null, no options
     * are selected
     * @return the HTML option list
     */
    public String getDomainOptionList (String[] defaultOptions) {
        StringBuffer optionList = new StringBuffer();

        Collection defaultOptionsCollection;
        if (defaultOptions == null) {
            defaultOptionsCollection = Collections.EMPTY_SET;
        } else {
            defaultOptionsCollection = Arrays.asList(defaultOptions);
        }

        for (Iterator it = iterator(); it.hasNext(); ) {
            DomainOption nextOption = (DomainOption) it.next();

            if (defaultOptionsCollection.contains(nextOption.getID())) {
                nextOption.setSelected(true);
            }

            // Convert option to string
            optionList.append(nextOption.getHTMLOption()).append("\n");
        }
        
        return optionList.toString();
    }

    /**
     * Returns this domain option list as an HTML option list except those
     * the specified as parameter values
     * @param defaultOptions the options to select
     * @return the HTML option list
     */
    public String getDomainOptionListExceptSpecified (String[] defaultOptions) {
        StringBuffer optionList = new StringBuffer();

        Collection defaultOptionsCollection;
        if (defaultOptions == null) {
            defaultOptionsCollection = Collections.EMPTY_SET;
        } else {
            defaultOptionsCollection = Arrays.asList(defaultOptions);
        }

        for (Iterator it = iterator(); it.hasNext(); ) {
            DomainOption nextOption = (DomainOption) it.next();

            if (!defaultOptionsCollection.contains(nextOption.getID())) {
                optionList.append(nextOption.getHTMLOption()).append("\n");
            }
            
        }
        
        return optionList.toString();
    }


    /**
     * Returns this domain option list as an HTML option list with
     * the specified values selected.
     * Each value is the domain's ID, the display string is the domain's
     * name.  Each option value that matches an element in the specified
     * array as the attribute <code>SELECTED</code<.
     * @param defaultOptions the options to select; if null, no options
     * are selected
     * @return the HTML option list
     */
    public String getSelectedDomainOptionList (String[] defaultOptions) {
        StringBuffer optionList = new StringBuffer();

        Collection defaultOptionsCollection;
        if (defaultOptions == null) {
            defaultOptionsCollection = Collections.EMPTY_SET;
        } else {
            defaultOptionsCollection = Arrays.asList(defaultOptions);
        }


        for (Iterator it = iterator(); it.hasNext(); ) {
            DomainOption nextOption = (DomainOption) it.next();

            if (defaultOptionsCollection.contains(nextOption.getID())) {
                nextOption.setSelected(true);
                optionList.append(nextOption.getHTMLOption()).append("\n");
            }

        }
        
        return optionList.toString();
    }

    /**
     * Loads all the domains for the configuration space with the
     * current configuration id.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        
        UserDomainCollection domainCollection = new UserDomainCollection();
        domainCollection.loadForConfigurationID(getConfigurationID());

        for (Iterator it = domainCollection.iterator(); it.hasNext(); ) {
            UserDomain nextDomain = (UserDomain) it.next();

            DomainOption newOption = new DomainOption();
            newOption.setID(nextDomain.getID());
            newOption.setName(nextDomain.getName());
            this.add(newOption);
        }
    
    }

   /**
    * Loads all domains, ignoring current configuration space.
    * @throws PersistenceException if there is a problem loading
    */
   public void loadAll() throws PersistenceException {
       
       UserDomainCollection domainCollection = new UserDomainCollection();
       domainCollection.load();

       for (Iterator it = domainCollection.iterator(); it.hasNext(); ) {
           UserDomain nextDomain = (UserDomain) it.next();

           DomainOption newOption = new DomainOption();
           newOption.setID(nextDomain.getID());
           newOption.setName(nextDomain.getName());
           this.add(newOption);
       }
   
   }


}
