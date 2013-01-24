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

 package net.project.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyManager;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.security.domain.UserDomain;


public class ConfigurationSpaceManager extends ConfigurationSpace implements java.io.Serializable {

    /**
     * Copies the configuration space for specified id and returns the new
     * configuration id.  The new configuration has been stored.  Configuration
     * space is NOT added to any portfolios.
     * @param the current user
     * @param configurationID the id of the configuraiton space to copy
     * @return the id of the new configuration space
     * @throws PersistenceException if there is a problem copying
     */
    public static String copyPersistent(User user, String configurationID) throws PersistenceException {
        return copyPersistent(user, configurationID, null, null);
    }

    /**
     * Copies the configuration space for specified id and returns the new
     * configuration id.  The new configuration has been stored.
     *
     * @param user the current user
     * @param configurationID the id of the configuraiton space to copy
     * @param name the name of the new configuration.  If name is null, the
     * new configuration name is same as source configuration name, prefixed
     * with "Copy of ".
     * @param portfolioMembershipIDList a list of portoflio IDs that new configuration space should belong to
     * @return the id of the new configuration space
     * @throws PersistenceException if there is a problem copying
     */
    public static String copyPersistent(User user, String configurationID, String name, ArrayList portfolioMembershipIDList)
            throws PersistenceException {
        
        ConfigurationSpace fromSpace = null;
        ConfigurationSpace toSpace = null;

        // Load configuration for specified id
        fromSpace = new ConfigurationSpace();
        fromSpace.setUser(user);
        fromSpace.setID(configurationID);
        fromSpace.load();

        // Duplicate the configuration without copying portfolio membership
        // then set the specific portfolio membership
        // Also do not cause the new space to have default forms copied from
        // the application space; these will be copied from the fromSpace
        toSpace = fromSpace.copy(false, false);
        toSpace.setPortfolioMembership(portfolioMembershipIDList);
        
        // Update new configurations name
        if (name == null) {
            name = "Copy of " + fromSpace.getName();
        }
        toSpace.setName(name);
        
        toSpace.store();
	    toSpace.copyDefaultForms(fromSpace.getID(), toSpace.getID());

        // Now copy the tokens
        PropertyManager.copyProperties(fromSpace.getBrand().getID(), toSpace.getBrand().getID());
        
        return toSpace.getID();
    }


    /**
     * Returns and HTML option list of all configuration spaces in the application
     * 
     * @return HTML Option List (string)
     * @since Gecko
     */
    public static String getConfigurationProviderOptionList(UserDomain userDomain) {

        net.project.database.DBBean db = new net.project.database.DBBean();
        StringBuffer optionList = new StringBuffer();
        String qstrGetConfigurationList = "select configuration_id, configuration_name from pn_configuration_space where record_status = 'A'";

        try {

            db.executeQuery (qstrGetConfigurationList);

            while (db.result.next()) {
                Iterator itr = userDomain.getConfigurationCollection().iterator();

                optionList.append ("<option value=\"" + db.result.getString("configuration_id")+ "\" ");

                while(itr.hasNext()){
                       ConfigurationSpace cspace = (ConfigurationSpace) itr.next();
                    
                       if(cspace !=null && cspace.getID() != null && cspace.getID().equals(db.result.getString("configuration_id"))) {
                           optionList.append(" SELECTED ");
                       }
                }
                optionList.append (" >" +db.result.getString ("configuration_name") + "</option>");
            }
        }

        catch (java.sql.SQLException sqle) {
            // do nothing
        
        } finally {
            db.release();
        
        }

        return optionList.toString();
    }

    /**
     * Returns a ArrayList of Configurations not supported by the Domain
     * 
     * @param userDomain <code>UserDomain</code>
     * @return List of Configurations not supported by the Domain 
     * @since Gecko
     */
    public static List getNotSupportedConfigurationProviderList(UserDomain userDomain) {

        net.project.database.DBBean dbean = new net.project.database.DBBean();
        
        List notSupportedList = new ArrayList();

        String query = "select configuration_id, configuration_name from pn_configuration_space where record_status = 'A'";

        try {

            dbean.prepareStatement (query);
            dbean.executePrepared();

            while (dbean.result.next()) {

                Iterator itr = userDomain.getConfigurationCollection().iterator();
                boolean isSupported = false ;
                ConfigurationSpace cspace = new ConfigurationSpace(); 
                cspace.setID(dbean.result.getString("configuration_id"));
                cspace.setName(dbean.result.getString("configuration_name"));

                while(itr.hasNext()) {
                   ConfigurationSpace supportedSpace = (ConfigurationSpace) itr.next();
                    
                    if(supportedSpace != null && supportedSpace.getID() != null && supportedSpace.getID().equals(dbean.result.getString("configuration_id"))) {
                        isSupported = true;         
                    }
                }
                
                if (!isSupported)
                    notSupportedList.add(cspace);
            }

        } catch (java.sql.SQLException sqle) {
            // do nothing
        
        } finally {
            dbean.release();
        }

        return notSupportedList;
    }

}
