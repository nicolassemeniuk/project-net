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

 package net.project.resource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.space.Space;
import net.project.util.DateUtils;
import net.project.util.NumberFormat;
import net.project.util.TimeQuantity;
import net.project.util.Validator;
import net.project.xml.XMLConstructionException;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * This class is a kind of an "augmented" roster that presents much less
 * information about a person, but has a few extra fields, such as the maximum
 * amount of time that a person is allocated during a time period.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class AssignmentRoster implements IXMLPersistence {
    private Date dateStart;
    private Date dateEnd;
    private Space space;
    private LinkedHashMap personMap = new LinkedHashMap();
    private List personList = new ArrayList();
    private String id;
    private String object_type;
    private boolean loaded = false;


    public void setObjectID(String id, String object_type) {
        this.id = id;
        this.object_type = object_type;
    }

    public static class Person implements IXMLPersistence {
        private String id;
        private String displayName;
        private BigDecimal maxAllocated;
        private TimeQuantity workComplete;
        private Date dateStart;
        private Date dateEnd;
        private TimeZone timeZone;

        /**
         * Get the ID of the person being allocated.
         *
         * @return a <code>String</code> containing the person_id of the person
         * record that corresponds to the person being allocated.
         */
        public String getID() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        /**
         * This is the maximum amount that a user was allocated during the period.
         *
         * @return a percentage with a scale of 2, where <code>1.00</code> = 100%
         */
        public BigDecimal getMaxAllocatedDecimal() {
            return maxAllocated;
        }

        public TimeQuantity getWorkComplete() {
            return workComplete;
        }

        public void setWorkComplete(TimeQuantity workComplete) {
            this.workComplete = workComplete;
        }
        
        public TimeZone getTimeZone() {
            return this.timeZone;
        }
        
        public Date getDateEnd() {
            return dateEnd;
        }

        public Date getDateStart() {
            return dateStart;
        }

        public String getXML() {
            return (IXMLPersistence.XML_VERSION + getXMLBody());
        }

        public String getXMLBody() {
            StringBuffer xml = new StringBuffer();
            NumberFormat numberFormtter = NumberFormat.getInstance();
            
            xml.append("<person>\n");
            
            xml.append("<id>");
            xml.append(id);
            xml.append("</id>\n");
            
            xml.append("<name>");
            xml.append(XMLUtils.escape(displayName));
            xml.append("</name>\n");
            
            xml.append("<max_allocated>");
            xml.append(numberFormtter.formatNumber(maxAllocated.doubleValue()));
            xml.append("</max_allocated>\n");
            
            xml.append("<start_date>");
            xml.append(XMLUtils.formatISODateTime(dateStart));
            xml.append("</start_date>\n");
            xml.append("<end_date>");
            xml.append(XMLUtils.formatISODateTime(dateEnd));
            xml.append("</end_date>\n");
            
            xml.append("<work_complete>");
            xml.append(numberFormtter.formatNumber(workComplete.getAmount().doubleValue()));
            xml.append("</work_complete>\n");
            xml.append("<work_complete_units>");
            xml.append(workComplete.getUnits().getPluralName());
            xml.append("</work_complete_units>\n");     
            xml.append("<work_complete_string>");
            xml.append(workComplete.toShortString(0, 2));
            xml.append("</work_complete_string>\n");     

            xml.append("<time_zone>");
            xml.append(timeZone.getID());
            xml.append("</time_zone>\n");

            xml.append("</person>\n");

            return xml.toString();
        }
        
        
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    
    /**
     * Converts the object to XML representation
     * This method returns the object as XML text.
     * @return XML representation of the object
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Converts the object to XML node representation without the xml header tag.
     * This method returns the object as XML text.
     * <p/>
     * Loads the Roster if not already loaded.
     * @return XML node representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        try {
            if (!loaded)
                this.load();
        } catch (PersistenceException pe) {
            Logger.getLogger(AssignmentRoster.class).error("Un unexpected exception occurred while attempting load person: " + pe);
            throw new XMLConstructionException("Un unexpected exception occurred while attempting to load person: " + pe, pe);
        }

        xml.append("<AssignmentRoster>\n");

        xml.append("<id>");
        xml.append(id);
        xml.append("</id>\n");
        
        xml.append("<space_id>");
        xml.append(space.getID());
        xml.append("</space_id>\n");
        xml.append("<space_name>");
        xml.append(XMLUtils.escape(space.getName()));
        xml.append("</space_name>\n");
        
        xml.append("<start_date>");
        xml.append(XMLUtils.formatISODateTime(dateStart));
        xml.append("</start_date>\n");
        xml.append("<end_date>");
        xml.append(XMLUtils.formatISODateTime(dateEnd));
        xml.append("</end_date>\n");
        
        for (int i = 0; i < personList.size(); i++) {

            Person person = (Person) personList.get(i);
            xml.append(person.getXMLBody());
        }

        xml.append("</AssignmentRoster>\n");

        return xml.toString();
    }

    public void load() throws PersistenceException {
        List assignmentEntries = null;
        if (id != null) {
            assignmentEntries = new AssignmentWorkLogFinder().findByObjectID(id);
        }

        DBBean db = new DBBean();
        try {
            db.prepareStatement(
                "select "+
                "  p.person_id, "+
                "  p.display_name, "+
                "  pp.timezone_code "+
                "from "+
                "  pn_person p," +
                "  pn_person_profile pp, "+
                "  pn_space_has_person shp "+
                "where " +
                "  shp.space_id = ? " +
                "  and shp.person_id = p.person_id " +
                "  and pp.person_id(+) = p.person_id " +
                "order by" +
                "  lower(p.display_name) "
            );
            db.pstmt.setString(1, space.getID());
            db.executePrepared();

            while (db.result.next()) {
                AssignmentRoster.Person person = new AssignmentRoster.Person();
                person.id = db.result.getString("person_id");
                person.displayName = db.result.getString("display_name");
                String zone = db.result.getString("timezone_code");
                if (!Validator.isBlankOrNull(zone)) {
                    person.timeZone = TimeZone.getTimeZone(zone);
                }
                
                if(AssignmentType.TASK.getObjectType().equals(object_type)) {
                    ResourceAllocationList ral = new ResourceAllocationList();
                    ral.loadResourceAllocationsForPerson(person.id);
                    person.maxAllocated = new BigDecimal(ral.getMaximumAllocation(dateStart, dateEnd)).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
                }
                
                if (assignmentEntries != null) {
                    person.setWorkComplete(AssignmentWorkLogUtils.getWorkLoggedForAssignee(assignmentEntries, person.id));
                    
                    if(AssignmentType.FORM.getObjectType().equals(object_type)) {
                        for(int i = 0; i < assignmentEntries.size(); i++ ) {
                            AssignmentWorkLogEntry entry = (AssignmentWorkLogEntry) assignmentEntries.get(i);
                            person.dateStart = DateUtils.min(person.dateStart, entry.getLogDate());
                            person.dateEnd = DateUtils.max(person.dateEnd, entry.getLogDate());
                        }
                    }
                }

                personMap.put(person.id, person);
                personList.add(person);
            }
            loaded = true;
        } catch (SQLException sqle) {
            throw new PersistenceException("Unable to load AssignmentRoster: "+sqle,sqle);
        } finally {
            db.release();
        }
    }

    public Iterator iterator() {
        return personList.iterator();
    }

    public Map getPersonMap() {
        return personMap;
    }
}
