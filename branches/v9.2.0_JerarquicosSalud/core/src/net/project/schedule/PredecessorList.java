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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.util.Conversion;
import net.project.util.NumberFormat;
import net.project.xml.XMLUtils;
import oracle.sql.ArrayDescriptor;

import org.apache.log4j.Logger;

/**
 * This class is a collection of a task predecessors.
 *
 * @author Matthew Flower
 * @since 7.4
 */
public class PredecessorList implements IXMLPersistence, Cloneable, Serializable {

    /**
     * The interal list of <code>TaskDependency</code> objects which represents
     * the predecessors of a task.
     */
    private final List predecessors = new LinkedList();

    /**
     * A <code>String</code> containing the primary key of the task for which
     * we are show predecessors.
     */
    private String taskID;
    /** Indicates if the predecessor list has already been loaded. */
    private boolean isLoaded = false;
    /**
     * This PredecessorList object contains the state the predecessor list was
     * in the last time it was stored to the database.
     */
    private PredecessorList lastSavedState;
    /** Contains the array descriptor needed to send the task dependencies to the database. */
    private ArrayDescriptor taskDependencyListDescriptor;

    /**
     * Set the id of the task for which we are collecting predecessors.
     *
     * @param taskID a <code>String</code> value containing the primary key of
     * the task for which we are collecting predecessors.
     */
    public void setTaskID(String taskID) {
        if ((this.taskID != null) && (this.taskID.equals(taskID))) {
            //The id hasn't changed, so don't change the load status.
        } else {
            isLoaded = false;
        }
        this.taskID = taskID;
    }

    /**
     * Load all dependent tasks for the task id identified by {@link #setTaskID}.
     * @throws PersistenceException
     */
    public void load() throws PersistenceException {
        if ((taskID == null) || taskID.trim().equals("")) {
            return;
        }

        TaskDependencyFinder finder = new TaskDependencyFinder();
        List tasks = finder.findByTaskID(taskID);
        this.predecessors.addAll(tasks);
        isLoaded = true;
        lastSavedState = (PredecessorList)clone();
    }


    /**
     * Insert all of this predecessor list into the database.  Be forewarned
     * that this method does no checks to make sure that the PredecessorList
     * hasn't already been stored in the database.  If you want that kind of
     * protection, use the {@link #store} method instead.
     *
     * @param db a <code>DBBean</code> object which may already be in a
     * transaction.
     * @throws SQLException if any error occurs storing any of the task
     * constraints.
     */
    public void insert(DBBean db) throws SQLException {
        db.setAutoCommit(false);

        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency)it.next();
            td.setTaskID(taskID);
            td.store(db);
        }

        db.commit();
        lastSavedState = (PredecessorList)clone();
    }

    /**
     * Store all of the predecessors in this list using a transaction.
     *
     * @throws PersistenceException if a database error occurs while trying to
     * save the TaskDependency objects to disk.
     */
    public void store() throws PersistenceException {
        //If there haven't been any changes, don't store the object
        if (this.equals(lastSavedState)) {
            return;
        }

        DBBean db = new DBBean();

        try {
            store(db);
        } catch (SQLException sqle) {
        	Logger.getLogger(PredecessorList.class).error("Unexpected SQL Exception: " + sqle);
            throw new PersistenceException("Unexpected SQL Exception thrown " +
                "while storing task dependency list.", sqle);
        } finally {
            db.release();
        }

        lastSavedState = (PredecessorList)clone();
    }

    public void store(DBBean db) throws SQLException {
        //If there haven't been any changes, don't store the object
        if (this.equals(lastSavedState)) {
            return;
        }

        db.setAutoCommit(false);

        //Delete any existing predecessors related to this task id
        db.prepareStatement("delete from pn_task_dependency where task_id = ?");
        db.pstmt.setString(1, taskID);
        db.executePrepared();

        //Grab all of the predecessors and save them
        TaskDependency.prepareStoreStatement(db);
        
        Iterator it = iterator();
        while (it.hasNext()) {
            TaskDependency td = (TaskDependency)it.next();
            //Make sure that the task dependency has the proper task id set.
            //If there wasn't a task ID when the dependency was created, it
            //might be blank.
            td.setTaskID(taskID);
            td.setStoreParameters(db.cstmt, true);
            db.cstmt.addBatch();
        }

        //Commit all of the pending task dependency store transactions
        db.executeCallableBatch();
        db.commit();

        lastSavedState = (PredecessorList)clone();
    }

    /**
     * Converts the object to XML representation. This method returns the
     * object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }

    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        StringBuffer xml2 = new StringBuffer();
        StringBuffer idList = new StringBuffer();
        NumberFormat nf = NumberFormat.getInstance();

        xml.append("<TaskDependencyList count=\"").append(predecessors.size()).append("\">\n");
        xml.append("  <PopupInfo>");
        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency) it.next();

            //Field 1, Sequence Number
            xml.append(fixForDHTMLPopup(XMLUtils.escape(nf.formatNumber(td.getSequenceNumber()))));
            xml.append("@");

            //Field 2, Task Name
            if(td.getTaskName()!=null && td.getTaskName().length() > 40){
            	xml.append(fixForDHTMLPopup(XMLUtils.escape(td.getTaskNameMaxLength40())));
            }else{
            	xml.append(fixForDHTMLPopup(XMLUtils.escape(td.getTaskName())));
            }
            xml.append("@");

            //Field 3, CyclicDependencyDetector Name
            xml.append(fixForDHTMLPopup(XMLUtils.escape(td.getDependencyType().getName())));
            xml.append("@");

            //Field 4, Lag
            xml.append(fixForDHTMLPopup(XMLUtils.escape(td.getLag().toShortString(0,100))));

            if (it.hasNext()) {
                xml.append("|");
            }
        }

        Iterator it = predecessors.iterator();
        while (it.hasNext()) {
            TaskDependency td = ((TaskDependency)it.next());
            idList.append(idList.length() > 0 ? ", " : "").append(td.getDependencyID());
        }

        xml.append("  </PopupInfo>\n");
        xml.append("<idlist>").append(idList).append("</idlist>");
        xml.append(xml2);
        xml.append("</TaskDependencyList>\n");

        return xml.toString();
    }

    private String fixForDHTMLPopup(String stringToFix) {
        stringToFix = stringToFix.replaceAll("\\|", "&#166;");
        stringToFix = stringToFix.replaceAll("'", "\\\\'");
        return stringToFix.replaceAll("@", "&#40;");
    }

    /**
     * Determine if the Predecessor has already been loaded from the database.
     *
     * @return a <code>boolean</code> value indicating whether the list has
     * already been loaded.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Sets the internal state of the object to indicate if it has already been
     * populated with data.
     *
     * @param loaded a <code>boolean</code> value indicating whether the list
     * has already been populated with data.
     */
    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    //Forwaring methods

    /** @see java.util.Collection#size */
    public int size() {
        return predecessors.size();
    }

    /** @see java.util.Collection#clear */
    public void clear() {
        taskID = null;
        predecessors.clear();
        isLoaded = false;
    }

    /** @see java.util.Collection#add */
    public void add(TaskDependency td) {
        predecessors.add(td);
    }

    /** @see java.util.Collection#iterator */
    public Iterator iterator() {
        return predecessors.iterator();
    }

    /** @see java.util.Collection#addAll */
    public void addAll(Collection dependencies) {
        this.predecessors.addAll(dependencies);
    }

    /** @see java.util.Collection#remove */
    public boolean remove(TaskDependency td) {
        return this.predecessors.remove(td);
    }

    /** @see java.util.Collection#removeAll */
    public boolean removeAll(Collection c) {
        return this.predecessors.removeAll(c);
    }

    /** @see java.util.Collection#isEmpty */
    public boolean isEmpty() {
        return this.predecessors.isEmpty();
    }

    /**
     * Returns an unmodifiable collection of the predecessors in this collection.
     * @return a collection where each element is a <code>TaskDependency</code>
     */
    Collection getInternalList() {
        return Collections.unmodifiableCollection(predecessors);
    }

    /**
     * Return a copy or "clone" of this object.  This object implements a "deep"
     * copy in which all of the contained TaskDependency objects are cloned as
     * well.
     *
     * @return a clone of this instance.
     * @see Cloneable
     */
    public Object clone() {
        PredecessorList pl = new PredecessorList();

        //We need to make a "deep copy" that copies all of the individual
        //elements.
        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency taskDependency = (TaskDependency)it.next();
            pl.add((TaskDependency)taskDependency.clone());
        }

        pl.taskID = taskID;
        pl.isLoaded = isLoaded;

        return pl;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PredecessorList)) return false;

        final PredecessorList predecessorList = (PredecessorList)o;

        if (isLoaded != predecessorList.isLoaded) return false;
        if (predecessors != null ? !predecessors.equals(predecessorList.predecessors) : predecessorList.predecessors != null) return false;
        if (taskID != null ? !taskID.equals(predecessorList.taskID) : predecessorList.taskID != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (predecessors != null ? predecessors.hashCode() : 0);
        result = 29 * result + (taskID != null ? taskID.hashCode() : 0);
        result = 29 * result + (isLoaded ? 1 : 0);
        return result;
    }

    /**
     * This method indicates that the current state that the object is in is the
     * state that is currently saved in the database for this object.  The
     * object should only be saved if it changes from this state.
     */
    protected void setLastSaveState() {
        lastSavedState = (PredecessorList)this.clone();
    }

    /**
     * Return the task dependency that corresponds to the indicated ID.
     *
     * @param dependencyID a <code>String</code> containing the task uid of the
     * dependency for which we are looking.
     * @return a <code>TaskDependency</code> object which has the given
     * dependency id.  This value will be null of there isn't one.
     */
    public TaskDependency findByDependencyID(String dependencyID) {
        TaskDependency toReturn = null;

        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency taskDependency = (TaskDependency) it.next();
            if (taskDependency.getDependencyID().equals(dependencyID)) {
                toReturn = taskDependency;
                break;
            }
        }

        return toReturn;
    }

    /**
     * Get a string which has a comma-separated list of the sequence numbers of
     * the tasks in this list.
     *
     * @return a <code>String</code> containing a comma-separated list of the
     * sequence numbers of the predecessors in this list.
     */
    public String getSeqCSVString() {
        List seqNumbers = new ArrayList();
        NumberFormat nf = NumberFormat.getInstance();

        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency taskDependency = (TaskDependency) it.next();
            seqNumbers.add(nf.formatNumber(taskDependency.getSequenceNumber()));
        }

        return Conversion.toCommaSeparatedString(seqNumbers);
    }
    
    public TaskDependency get(int index) {
        return (TaskDependency)predecessors.get(index);
    }
    
    public String getDependentTaskInfo() {
        StringBuffer popupInfoString = new StringBuffer();
        NumberFormat nf = NumberFormat.getInstance();

        
        for (Iterator it = predecessors.iterator(); it.hasNext();) {
            TaskDependency td = (TaskDependency) it.next();

            //Field 1, Sequence Number
            popupInfoString.append(fixForDHTMLPopup(XMLUtils.escape(nf.formatNumber(td.getSequenceNumber()))));
            popupInfoString.append("@");

            //Field 2, Task Name
            if(td.getTaskName()!=null && td.getTaskName().length() > 40){
            	popupInfoString.append(fixForDHTMLPopup(XMLUtils.escape(td.getTaskNameMaxLength40())));
            }else{
            	popupInfoString.append(fixForDHTMLPopup(XMLUtils.escape(td.getTaskName())));
            }
            popupInfoString.append("@");

            //Field 3, CyclicDependencyDetector Name
            popupInfoString.append(fixForDHTMLPopup(XMLUtils.escape(td.getDependencyType().getName())));
            popupInfoString.append("@");

            //Field 4, Lag
            popupInfoString.append(fixForDHTMLPopup(XMLUtils.escape(td.getLag().toShortString(0,100))));

            if (it.hasNext()) {
            	popupInfoString.append("|");
            }
        }
        return popupInfoString.toString();
    }
    
    public String getDependentCSVIds(){
    	StringBuffer idList = new StringBuffer();
    	Iterator it = predecessors.iterator();
        while (it.hasNext()) {
            TaskDependency td = ((TaskDependency)it.next());
            idList.append(idList.length() > 0 ? ", " : "").append(td.getDependencyID());
        }
        return idList.toString();
    }
}
