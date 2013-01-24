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

 package net.project.schedule;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.space.Space;

import org.apache.log4j.Logger;

/**
 * A collection of TaskComment objects.
 * Usage:<code><pre>
 *     TaskComments comments = new TaskComments();
 *     comments.setSpace(<some space>);
 *     comments.load();
 *     comments.getXML();
 * </pre></code>
 */
public class TaskComments extends ArrayList implements Serializable, IXMLPersistence {

    /** The task to which these comments belong. */
    private ScheduleEntry task = null;
    private Space space = null;
    private Roster roster = null;

    /**
     * Creates a new, empty TaskComments object.
     */
    public TaskComments() {
        this.roster = new Roster();
    }

    /**
     * Sets the current space.
     * @param space the current space
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Sets the task to which these comments belong.
     * @param task the task
     */
    public void setTask(ScheduleEntry task) {
        this.task = task;
    }

    /**
     * Returns the task to which these comments belong.
     * @return the task
     */
    private ScheduleEntry getTask() {
        return this.task;
    }

    /**
     * Adds a new TaskComment to the collection.
     * If specified Object is not of type TaskComment a ClassCastException
     * will be thrown.
     * @param o the TaskComment to add
     */
    public boolean add(Object o) {
        return super.add(o);
    }

    /**
     * Loads the comments for the task.
     * Assumes {@link #setTask} has been called.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        StringBuffer query = new StringBuffer();
        int index = 0;
        TaskComment taskComment = null;

        query.append("select tc.task_id, tc.created_by_id, tc.created_datetime, tc.text_clob ");
        query.append("from pn_task_comment tc ");
        query.append("where tc.task_id = ? ");
        query.append("order by tc.baseline_id desc, tc.seq desc ");

        DBBean db = new DBBean();
        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(++index, getTask().getID());
            db.executePrepared();

            while (db.result.next()) {
                taskComment = new TaskComment();
                taskComment.setTaskID(db.result.getString("task_id"));
                taskComment.setCreatedByID(db.result.getString("created_by_id"));
                taskComment.setCreatedByDisplayName(getDisplayName(taskComment.getCreatedByID()));
                taskComment.setCreatedDatetime(db.result.getTimestamp("created_datetime"));
                taskComment.setText(ClobHelper.read(db.result.getClob("text_clob")));
                taskComment.setLoaded(true);
                add(taskComment);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(TaskComments.class).error("TaskComments.load() threw an SQL exception: " + sqle);
            throw new PersistenceException("Task Comments load operation failed.", sqle);

        } finally {
            db.release();

        }

    } //end load()

    /**
     * Returns the roster and loads if necessary.
     */
    private Roster getRoster() {
        if (!this.roster.isLoaded()) {
            this.roster.setSpace(this.space);
            this.roster.load();
        }
        return this.roster;
    }

    /**
     * Returns the display name for the specified personID.
     * @return the display name or null if the person is not in the roster
     * for the current space.
     */
    private String getDisplayName(String personID) {
        String displayName = null;
        Person person = getRoster().getPerson(personID);
        if (person != null) {
            displayName = person.getDisplayName();
        }

        return displayName;
    }

    //
    // Implementing IXMLPersistence
    //

    /**
     * Returns the TaskComments as XML, including the XML version tag.
     * @return the XML
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns the TaskComments as XML.
     * @return the XML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<TaskComments>");

        // Add each TaskComment's XML
        Iterator it = iterator();
        while (it.hasNext()) {
            xml.append(((TaskComment)it.next()).getXMLBody());
        }

        xml.append("</TaskComments>");

        return xml.toString();
    }

    //
    // End IXMLPersistence
    //

}
