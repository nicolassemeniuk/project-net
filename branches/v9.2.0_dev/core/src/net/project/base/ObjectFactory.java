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

 package net.project.base;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.link.ILinkableObject;
import net.project.persistence.PersistenceException;
import net.project.schedule.ScheduleFinder;
import net.project.schedule.TaskFinder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * <code>ObjectFactory</code> is used to create an object when you only have an
 * object type or ID.  <code>ObjectFactory</code> is only designed to create
 * "Tools", i.e. objects that are created inside of a space.
 *
 * @author Brian Conneen
 * @since 03/01/2000
 */
public class ObjectFactory {
    /**
     * Get an empty object of the proper type
     *
     * @param objectID a <code>String</code> value containing an ID that will be
     * assigned to the object after it is created.
     * @return the proper type of object implementing ILinkableObject
     */
    public static Object make(String objectID) {
        return make(null, objectID);
    }

    /**
     * This is a slightly more advanced version of the {@link #make(String)}
     * method which doesn't do a very good job of constructing an object.
     *
     * @param objectID a <code>String</code> containing the object we want to
     * load.
     * @return a <code>Object</code> which is fully constructed.
     */
    public static Object load(String objectID) throws PersistenceException {
        //First, figure out what type of object we are trying to load.
        String type = getObjectType(objectID);

        Object loadedObject = null;
        if (type == ObjectType.SCHEDULE) {
            loadedObject = new ScheduleFinder().findByPlanID(objectID);
        } else if (type == ObjectType.SCHEDULE_ENTRY) {
            loadedObject = new TaskFinder().findByID(objectID);
        } else {
            throw new ObjectTypeNotSupportedException();
        }

        return loadedObject;
    }

    public static String getObjectType(String objectID) {
        PnObject object = new PnObject();
        object.setID(objectID);
        object.load();

        return object.getType();
    }

    public static boolean isSpaceType(String objectID){
    	if(StringUtils.isEmpty(objectID))
    		return false;
    	String objectType = getObjectType(objectID);
    	if(StringUtils.isEmpty(objectType))
    		return false;
    	
        if (objectType.equalsIgnoreCase(ObjectType.PERSON))
            return true;
        else if (objectType.equalsIgnoreCase(ObjectType.PROJECT))
            return true;
        else if (objectType.equalsIgnoreCase(ObjectType.BUSINESS))
            return true;    	
        else if (objectType.equalsIgnoreCase(ObjectType.FINANCIAL))
            return true;    	        
        else if (objectType.equalsIgnoreCase(ObjectType.APPLICATION))
            return true;
        else if (objectType.equalsIgnoreCase(ObjectType.CONFIGURATION))
            return true;
        else if (objectType.equalsIgnoreCase(ObjectType.METHODOLOGY))
            return true;        
        else if (objectType.equalsIgnoreCase(ObjectType.ENTERPRISE_SPACE))
            return true;
 	
        return false;
    }
    
    /**
     * Get an empty object of the proper type
     *
     * @param type the object's type
     * @return the proper type of object implementing ILinkableObject
     */
    public static Object make (String type, String objectID) {
        // get the type of the object if needed.
        if (type == null) {
            PnObject object = new PnObject();
            object.setID(objectID);
            object.load();

            // can't figure out the type.
            if (( type = object.getType()) == null)
                return null;
        }

        // Document Module
        if (type.equals(ObjectType.DOCUMENT))
            return new net.project.document.Document();

        else if (type.equals(ObjectType.CONTAINER))
            return new net.project.document.Container();

        else if (type.equals(ObjectType.BOOKMARK))
            return new net.project.document.Bookmark();

        // Calendar Module
        else if (type.equals(ObjectType.CALENDAR))
            return new net.project.calendar.PnCalendar();

        else if (type.equals(ObjectType.MEETING))
            return new net.project.calendar.Meeting();

        else if (type.equals(ObjectType.EVENT))
            return new net.project.calendar.CalendarEvent();

        // Discussion Module
        else if (type.equals(ObjectType.POST))
            return new net.project.discussion.Post();

        // Form Module
        else if (type.equals(ObjectType.FORM))
            return new net.project.form.Form();

        else if (type.equals(ObjectType.FORM_FIELD))
            return new net.project.form.FormFieldDesigner();

        else if (type.equals(ObjectType.FORM_LIST))
            return new net.project.form.FormList();

        else if (type.equals(ObjectType.FORM_DATA))
            return new net.project.form.FormData();

        // Schedule Module
        else if (type.equals(ObjectType.SCHEDULE))
            return new net.project.schedule.Schedule();

        else if (type.equals(ObjectType.PLAN))
            return new net.project.schedule.Schedule();

        else if (type.equals(ObjectType.TASK))
            return new net.project.schedule.Task();

        // Process Module
        else if (type.equals(ObjectType.PHASE))
            return new net.project.process.Phase();
        
        else if (type.equals(ObjectType.DELIVERABLE))
            return new net.project.process.Deliverable();

        // MethodologySpace
        else if (type.equals(ObjectType.METHODOLOGY))
            return new net.project.methodology.MethodologySpace();

        // Workflow Module
        else if (type.equals(ObjectType.WORKFLOW))
            return new net.project.workflow.Workflow();

        else if (type.equals(ObjectType.WORKFLOW_STEP))
            return new net.project.workflow.Step();

        else if (type.equals(ObjectType.WORKFLOW_TRANSITION))
            return new net.project.workflow.Transition();

        else if (type.equals(ObjectType.WORKFLOW_RULE)) {
            /* Must create Rule type of correct sub-class */
            try {
                return net.project.workflow.RuleFactory.createEmptyRule(objectID);
            } catch (net.project.workflow.RuleException re) {
                return null;
            }
        }

        // News Module
        else if (type.equals(ObjectType.NEWS))
            return new net.project.news.News();

        // No matching object type found
        else
            return null;
    }



    /**
     * Returns a loaded ILinkable object of the specified type.
     *
     * @param type the object's type
     * @param objectID the object's ID
     * @return the loaded object of appropriate type or <code>null</code>
     * if there is a persistence exception while loading the object
     */
    public static ILinkableObject makeLinkableObject (String type, String objectID) {

        //Add more here as we support it
        ILinkableObject linkable = null;

        if (type.equals(ObjectType.DOCUMENT)) {
            linkable = new net.project.document.Document();
            if(trashcanFlag) {
               ((net.project.document.Document) linkable ).setListDeleted();
            }
        } else if (type.equals(ObjectType.POST)) {
            linkable = new net.project.discussion.Post();

        } else if (type.equals(ObjectType.BOOKMARK)) {
            linkable = new net.project.document.Bookmark();

        } else if (type.equals(ObjectType.DELIVERABLE)) {
            linkable = new net.project.process.Deliverable();

        } else if (type.equals(ObjectType.MEETING)) {
            linkable = new net.project.calendar.Meeting();

        } else if (type.equals(ObjectType.TASK)) {
            linkable = new net.project.schedule.Task();

        } else if (type.equals(ObjectType.EVENT)) {
            linkable = new net.project.calendar.CalendarEvent();

        } else if (type.equals(ObjectType.FORM_DATA)) {
            linkable = new net.project.form.FormData();

        } else if (type.equals(ObjectType.FORM_LIST)) {
            linkable = new net.project.form.FormList();

        }

        linkable.setID(objectID);

        try {
            linkable.load();
            return linkable;
        } catch (PersistenceException pe) {
            // do nothing right now
        }
        return null;
    }
    
    private static boolean trashcanFlag = false;

    /**
     * Get an empty object based on the object_ID and the module from which its called.
     * Note the status of object may be different in different modules like in a trashcan
     * the status of document would be 'D' and in document module it would be 'A'
     *
     * @param module in which the object is called
     * @param objectID the object's ID
     * @return the proper type of object implementing ILinkableObject
     */
    public static ILinkableObject makeLinkableObject( int module, String objectID ) throws PersistenceException {
        if(module == Module.TRASHCAN) {
            trashcanFlag = true;
        } else {
            trashcanFlag = false;
        }
        return makeLinkableObject(objectID);
    }

    /**
     * Get an empty object based on the object_ID
     *
     * @param objectID the object's ID
     * @return the proper type of object implementing ILinkableObject
     */
    public static ILinkableObject makeLinkableObject( String objectID ) throws PersistenceException {
        if ((objectID == null) || (objectID.equals("")))
            throw new PersistenceException("ObjectFactory.makeLinkableObject(objectID) was called with "+
                                           " a blank or null string.  This value is required for creating "+
                                           " a linkable object");

        String type = null;

        DBBean db = new DBBean();
        try {
            db.prepareStatement("SELECT object_type FROM pn_object WHERE object_id = ?");
            db.pstmt.setString(1, objectID);

            db.executePrepared();

            if ( db.result.next() )
                type = db.result.getString("object_type");
            else
                throw new PersistenceException("Unable to find an object type for objectid=("+objectID+")");
        } catch (SQLException sqle) {
        	Logger.getLogger(ObjectFactory.class).error("ObjectFactory.makeLinkableObject(String) could not find "+
                                       "an object type for id (" + objectID + ").  SQLException:"+ sqle);
            throw new PersistenceException("Unable to create a linkable object.", sqle);
        } finally {
            db.release();
        }

        return makeLinkableObject(type, objectID);
    }
}
