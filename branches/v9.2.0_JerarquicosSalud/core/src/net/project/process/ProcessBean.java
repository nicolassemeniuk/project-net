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

 package net.project.process;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.persistence.PersistenceException;
import net.project.util.TextFormatter;

/**
 * This Bean wraps Process.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class ProcessBean extends Process implements Serializable {
    private DBBean db = new DBBean();

    /**
     * Construct an empty ProcessBean.
     */
    public ProcessBean() {
    }

    public boolean loadProcess(String space_id) throws PersistenceException {
        boolean failed = true;

        String qStrGetProcess = "select process_id from pn_space_has_process " +
            "where space_id = " + space_id + " ";
        try {
            db.executeQuery(qStrGetProcess);
            if (db.result.next()) {
                setID(db.result.getString("process_id"));
                load();
                loadPhases();
                failed = false;
            }
        } catch (SQLException sqle) {

        } finally {
            db.release();
        }

        return failed;
    }


    /**
     * Get an option list of phases with none selected.
     *
     * @return HTML option list or empty string if process not loaded or there
     * are no phases.
     */
    public String getPhaseOptionList() {
        return getPhaseOptionList(Collections.EMPTY_LIST);
    }

    /**
     * Get an option list of phases with some selected.
     *
     * @param phaseSelectCollection a collection of strings containing phase ids
     * which are used to select some entries in option list.
     * @return HTML option list or empty string if process not loaded or there
     * are no phases.
     */
    public String getPhaseOptionList(Collection phaseSelectCollection) {
        return HTMLOptionList.makeHtmlOptionList(getPhaseOptions(true), phaseSelectCollection);
    }

    /**
     * Returns a collection of phases.
     * @return a collection where each element is an <code>IHTMLOption</code>;
     * the collection is empty if there are no phases
     */
    public Collection getPhaseOptions(boolean includeAllAndUnassignedOptions) {
        List phaseOptions = new ArrayList();

        PhaseList phaseList = getPhaseList();

        if (includeAllAndUnassignedOptions) {
        // first add the ALL option
        phaseOptions.add(new HTMLOption("", PropertyProvider.get("prm.schedule.main.phase.option.all.name")));
        }
        // if there are any phases in the list, add the none option and any other phases that might exist
        if (phaseList != null) {

            Iterator it = phaseList.iterator();
            if (it.hasNext() && includeAllAndUnassignedOptions) {
                phaseOptions.add(new HTMLOption("null", PropertyProvider.get("prm.schedule.main.phase.option.none.name")));
            }
            
            while (it.hasNext()) {
                Phase nextPhase = (Phase) it.next();
                phaseOptions.add(new HTMLOption(nextPhase.getID(), TextFormatter.truncateString(nextPhase.getName(),20)));
            }
        }

        return phaseOptions;
    }

}
