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
package net.project.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnProjectSpaceMetaProp;
import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;
import net.project.hibernate.service.IPnProjectSpaceMetaPropService;
import net.project.hibernate.service.IPnProjectSpaceMetaValueService;
import net.project.hibernate.service.ServiceFactory;


public class MetaData implements Serializable {
    public static final int STRING_PROPERTY = 1;
    public static final int NUMBER_PROPERTY = 2;

    private String projectSpaceID;
    private Hashtable<String, PnProjectSpaceMetaValue> properties = new Hashtable<String, PnProjectSpaceMetaValue>();
    private static Hashtable<String, PnProjectSpaceMetaProp> metaDataProp = new Hashtable<String, PnProjectSpaceMetaProp>();
    private static Hashtable<String, MetaData> projectsMetaData = new Hashtable<String, MetaData>();
    
    private Vector<PnProjectSpaceMetaValue> addedProperties = new Vector<PnProjectSpaceMetaValue>();

    protected MetaData(String projectSpaceID) {
        this.projectSpaceID = projectSpaceID;
        initialize();
    }

    public static MetaData getMetaData(String projectId){
    	if(projectId == null){
    		return new MetaData(projectId);
    	}
    	if(projectsMetaData.get(projectId) == null){
    		projectsMetaData.put(projectId, new MetaData(projectId));
    	}
    	return projectsMetaData.get(projectId);
    }
    
    public static void initializeMetaData() {
		try {
			if (projectsMetaData == null) {
				projectsMetaData = new Hashtable<String, MetaData>();
			}
			
			List<PnProjectSpace> projectIds = ServiceFactory.getInstance().getPnProjectSpaceService().getActiveProjectIds();
			for (int i = 0; i < projectIds.size(); i++) {
				projectsMetaData.put(projectIds.get(i).getProjectId().toString(), new MetaData(projectIds.get(i).getProjectId().toString()));
			}
	        IPnProjectSpaceMetaPropService service = ServiceFactory.getInstance().getPnProjectSpaceMetaPropService();
	        List<PnProjectSpaceMetaProp> metaProp=  service.getAllProjectSpaceMetaProperties();
	        
			for (int i = 0; i < metaProp.size(); i++) {				
				metaDataProp.put(metaProp.get(i).getPropertyName(), metaProp.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private void initialize() {
		try {
			// if (projectSpaceID == null) {
			// properties = new Hashtable<String, PnProjectSpaceMetaValue>();
			// return;
			//			}
			IPnProjectSpaceMetaValueService service = ServiceFactory.getInstance().getPnProjectSpaceMetaValueService();
			
			if(service == null){
				System.out.println("\n\n\n service is null !!! \n\n\n");
			}
			
			List<PnProjectSpaceMetaValue> metaValues = null;
			if (projectSpaceID != null){
				metaValues = service.getMetaValuesByProjectId(new Integer(projectSpaceID));
			}
			properties = new Hashtable<String, PnProjectSpaceMetaValue>();
			if (metaValues != null) {
				for (PnProjectSpaceMetaValue metaValue : metaValues) {
					properties.put(metaValue.getPnProjectSpaceMetaProp().getPropertyName(), metaValue);
					metaDataProp.put(metaValue.getPnProjectSpaceMetaProp().getPropertyName(), metaValue.getPnProjectSpaceMetaProp());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
	 * Returns property value by the property name
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @return the value of the property
	 * @throws NoSuchPropertyException
	 *             if the specified propertyName does not exist
	 */
    public String getProperty(String propertyName) throws NoSuchPropertyException {
        //if (properties == null) initialize();
        if (getMetaPropByName(propertyName) == null)
            throw new NoSuchPropertyException("The property with the name '" + propertyName + "' does not exist.");
        PnProjectSpaceMetaValue metaValue = null;
        if (projectSpaceID != null){
        	metaValue = projectsMetaData.get(projectSpaceID).properties.get(propertyName);
        }else{
        	metaValue = properties.get(propertyName);
        }
        if (metaValue == null)
            return "";
        else
            return metaValue.getPropertyValue();
    }

    /**
     * Returns property type by the property name
     *
     * @param propertyName the name of the property
     * @return the type of the property, possible values are STRING_PROPERTY and NUMBER_PROPERTY
     * @throws NoSuchPropertyException if the specified propertyName does not exist
     */
    public int getPropertyType(String propertyName) throws NoSuchPropertyException {
        if (properties == null) initialize();
        PnProjectSpaceMetaProp metaProp = getMetaPropByName(propertyName);
        if (metaProp == null)
            throw new NoSuchPropertyException("The property with the name '" + propertyName + "' does not exist.");

        if (("" + NUMBER_PROPERTY).equals(metaProp.getPropertyType()))
            return NUMBER_PROPERTY;
        else
            return STRING_PROPERTY;
    }

    /**
     * Sets new property value; if propertyValue is null then the property will be removed
     *
     * @param propertyName  the name of the property
     * @param propertyValue the value of the property
     * @throws NoSuchPropertyException if the specified propertyName does not exist
     */
    public void setProperty(String propertyName, String propertyValue) throws NoSuchPropertyException {
        if (properties == null) initialize();
        PnProjectSpaceMetaProp metaProp = getMetaPropByName(propertyName);
        if (metaProp == null)
            throw new NoSuchPropertyException("The property with the name '" + propertyName + "' does not exist.");
        PnProjectSpaceMetaValue metaValue = null;
        if (projectSpaceID != null){
        	metaValue = projectsMetaData.get(projectSpaceID) != null ? projectsMetaData.get(projectSpaceID).properties.get(propertyName) : null;
        }else{
        	metaValue = properties.get(propertyName);
        }
        if (metaValue == null) {
            metaValue = new PnProjectSpaceMetaValue(new PnProjectSpaceMetaValuePK(new Integer(projectSpaceID != null ? projectSpaceID : "0"), metaProp.getPropertyId()));
            if(projectSpaceID != null){
            	projectsMetaData.get(projectSpaceID).properties.put(propertyName, metaValue);
            }else{
            	properties.put(propertyName, metaValue);
            }
            addedProperties.add(metaValue);
        }
        metaValue.setPropertyValue(propertyValue);
    }

    /**
     * Saves properties to the database.
     *
     * @param projectSpaceID the ID is passed because while creating the new project we will have null id until calling store()
     */
    public void store(String projectSpaceID) {
		this.projectSpaceID = projectSpaceID;
		IPnProjectSpaceMetaValueService service = ServiceFactory.getInstance().getPnProjectSpaceMetaValueService();
		if ((properties == null) || (properties.values() == null))
			return;
		for (PnProjectSpaceMetaValue metaValue : properties.values()) {
			metaValue.getComp_id().setProjectId(new Integer(projectSpaceID));
			if (addedProperties.contains(metaValue)) {
				addedProperties.remove(metaValue);
				service.saveProjectSpaceMetaValue(metaValue);
			} else {
				service.updateProjectSpaceMetaValue(metaValue);
			}
		}
		projectsMetaData.put(projectSpaceID, this);
	}

    /**
	 * Removes properties from the database.
	 */
    public void delete() {
        if (projectsMetaData.get(projectSpaceID).properties == null) initialize();
        IPnProjectSpaceMetaValueService service = ServiceFactory.getInstance().getPnProjectSpaceMetaValueService();
        for (PnProjectSpaceMetaValue metaValue : projectsMetaData.get(projectSpaceID).properties.values()) {
            if (addedProperties.contains(metaValue)) {
                addedProperties.remove(metaValue);
            } else {
                service.deleteProjectSpaceMetaValue(metaValue);
            }
        }
    }

    /**
     * Returns the list of all available properties
     *
     * @return the full list of the possible property names
     */
    public static List<String> getPropertyNames() {
        IPnProjectSpaceMetaPropService service = ServiceFactory.getInstance().getPnProjectSpaceMetaPropService();
        List<PnProjectSpaceMetaProp> props = service.getAllProjectSpaceMetaProperties();
        List<String> result = new ArrayList<String>();
        for (PnProjectSpaceMetaProp prop : props) {
            result.add(prop.getPropertyName());
        }
        return result;
    }

    private static PnProjectSpaceMetaProp getMetaPropByName(String propertyName) {
    	return metaDataProp.get(propertyName);
    }
}
