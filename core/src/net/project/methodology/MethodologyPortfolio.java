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
|    $Revision: 20299 $
|    $Date: 2010-01-19 08:38:29 -0300 (mar, 19 ene 2010) $
|    $Author: uroslates $
|
+----------------------------------------------------------------------*/
package net.project.methodology;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.space.ISpaceTypes;
import net.project.xml.IXMLTags;

import org.apache.log4j.Logger;

/**
 * A Portfolio of <code>MethodologySpace</code>s.
 */
public class MethodologyPortfolio extends net.project.portfolio.Portfolio {
    /**
     * Creates an empty MethodologyPortfolio.
     */
    public MethodologyPortfolio() {
        super();
        setContentType(ISpaceTypes.METHODOLOGY_SPACE);
    }

    /**
     * Loads the <code>MethodologySpace</code>s for the current parent space id.
     *
     * @throws net.project.persistence.PersistenceException if there is a problem
     * loading
     * @throws IllegalStateException if no parent space if is available by
     * {@link #getParentSpaceID}
     */
    public void load() throws net.project.persistence.PersistenceException {
        if (getParentSpaceID() == null || getParentSpaceID().trim().length() == 0) {
            throw new IllegalStateException("Parent space id required to load a Methodology Portfolio");
        }

        String qstrGetMethodologies = "select methodology_id, parent_space_id, methodology_name, " +
            "methodology_desc, use_scenario_clob, created_by_id, created_by, modified_by_id, modified_by, " +
            "created_date, modified_date, record_status, crc, based_on_space_id " +
            "from pn_methodology_view " +
            "where parent_space_id = " + getParentSpaceID() + " " +
            "and record_status = 'A' ";
        DBBean db = new DBBean();

        try {
            db.executeQuery(qstrGetMethodologies);
            while (db.result.next()) {
                MethodologySpace methodology = new MethodologySpace();

                methodology.setID(db.result.getString("methodology_id"));
                methodology.setParentSpaceID(db.result.getString("parent_space_id"));
                methodology.setName(db.result.getString("methodology_name"));
                methodology.setDescription(db.result.getString("methodology_desc"));
                methodology.setUseScenario(ClobHelper.read(db.result.getClob("use_scenario_clob")));
                methodology.setCreatedBy(db.result.getString("created_by"));
                methodology.setCreatedByID(db.result.getString("created_by_id"));
                methodology.setCreatedDate(db.result.getTimestamp("created_date"));
                methodology.setModifiedBy(db.result.getString("modified_by"));
                methodology.setModifiedByID(db.result.getString("modified_by_id"));
                methodology.setBasedOnSpaceID(db.result.getString("based_on_space_id"));

// load methodology modules
methodology.setModules( ServiceFactory.getInstance().getPnMethodologyModulesService()
		.findMethodologyModules(Integer.valueOf(methodology.getID())) );

                this.add(methodology);
            }

            setLoaded(true);
        } catch (SQLException sqle) {
        	Logger.getLogger(MethodologyPortfolio.class).debug("MethodologyPortfolio.load(): unable to execute query: " + sqle);
            throw new PersistenceException("MethodologyPortfolio load operation failed: " + sqle, sqle);
        } finally {
            db.release();
        }
        
    }

    /**
     * Removes a Portfolio - not supported by MethodologyPortfolio.
     * @throws net.project.persistence.PersistenceException never
     * @throws UnsupportedOperationException always
     */
    public void remove() throws net.project.persistence.PersistenceException {
        throw new UnsupportedOperationException("Methodology Portfolio remove operation not supported");
    }

    /**
     * Returns the XML for this MethodologyPortfolio, including the XML version
     * tag
     * @return the XML
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();

        xml.append(IXMLTags.XML_VERSION_STRING);
        xml.append(getXMLBody());

        return xml.toString();
    }

    /**
     * Returns the XML for this MethodologyPortfolio, excluding the XML version
     * tag
     * @return the XML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        Iterator i = this.iterator();
        MethodologySpace methodology = null;

        xml.append("<methodology_portfolio>");

        while (i.hasNext()) {

            methodology = (MethodologySpace)i.next();
            xml.append(methodology.getXMLBody());

        }
        xml.append("</methodology_portfolio>");

        return xml.toString();
    }
    
    /**
     * Returns the JSON for this MethodologyPortfolio.
     * @return the JSON
     */
    public String getJSONBody() {
        Map<String, List> dataMap = getDataAsMap();
        
        return convertMapToJson(dataMap);
    }
    
    /**
     * Convert map of data into properly parsed JSON array object. 
     * @param dataMap - map of methodologies
     * @return String representation of JSON array
     */
    private String convertMapToJson(Map<String, List> dataMap) {
    	StringBuffer jsonArr = new StringBuffer("[");
    	
    	Iterator keysIt = dataMap.keySet().iterator(); 
    	while(keysIt.hasNext()) {
    		String key = (String) keysIt.next();
    		Iterator values = dataMap.get(key).listIterator();
    		
    		StringBuffer methodologyTypeJsonObj = new StringBuffer("{ \n");
    		methodologyTypeJsonObj.append("	category	:	'" + key + "', \n");
    		methodologyTypeJsonObj.append("	items		:	[ \n");

    		// constructing JSON items Array
    		while(values.hasNext()) {
    			String value = (String) values.next();
    			methodologyTypeJsonObj.append(value);
    			if( values.hasNext() ) {
    				methodologyTypeJsonObj.append(", \n");
    			} 
    		}
    		methodologyTypeJsonObj.append("	] \n");			// items array end
    		methodologyTypeJsonObj.append("} \n");
    		
    		jsonArr.append(methodologyTypeJsonObj);
    		if(keysIt.hasNext()) {
    			jsonArr.append(", \n");
    		}
    	}
    	jsonArr.append("]");
    	
//    	return jsonArr.toString();
    	String json = 	"{\n" +
    					"	totalItems: " + this.size() + ", \n " +
    					"	templateItems: " + jsonArr.toString() + " \n " +
    					"}";
    	return json;
    }
    
    /**
     * Construct the TreeMap out of the list of methodologies.
     * @return Map of retrieved methodologies
     */
    private Map<String, List> getDataAsMap() {
        Iterator i = this.iterator();
        MethodologySpace methodology = null;
        Map<String, List> dataMap = new TreeMap<String, List>();
        List<String> values = null;

        while (i.hasNext()) {
            methodology = (MethodologySpace)i.next();
            String basedOnSpaceType = ( methodology.getBasedOnSpaceID() == null ?
            		"business" : DatabaseUtils.getTypeForObjectID(methodology.getBasedOnSpaceID()) );
            
            if( !dataMap.containsKey(basedOnSpaceType) ) {
            	values = new ArrayList<String>();
            } else {
            	values = dataMap.get( basedOnSpaceType );
            }
            values.add(methodology.getAsJSON());
            
            dataMap.put(basedOnSpaceType, values);

        }
        
        return dataMap;
    }
    
}