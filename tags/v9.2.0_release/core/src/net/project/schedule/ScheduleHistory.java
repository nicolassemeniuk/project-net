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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.schedule;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.FinderSorter;
import net.project.base.finder.FinderSorterList;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * This class provides access to a list of Schedule version objects that
 * represent different times in a Schedules history.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class ScheduleHistory implements IXMLPersistence {
    private String planID;
    private List scheduleVersions = Collections.EMPTY_LIST;

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public List getScheduleVersions() {
        return scheduleVersions;
    }

    public void setScheduleVersions(List scheduleVersions) {
        this.scheduleVersions = scheduleVersions;
    }

    public void load() throws PersistenceException {
        //Load schedule versions
        ScheduleFinder finder = new ScheduleFinder();

        FinderSorterList fsl = new FinderSorterList();
        fsl.add(new FinderSorter(ScheduleFinder.BASELINE_ID_COLDEF, true));
        fsl.add(new FinderSorter(ScheduleFinder.MODIFIED_DATE_COLDEF, true));
        finder.addFinderSorterList(fsl);

        scheduleVersions = finder.findVersions(planID);
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     *
     * @return XML representation of this object
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        Baseline currentBaseline = null;
        boolean lastWasPreBaseline = true;

        xml.append("<scheduleHistory>");
        for (Iterator it = scheduleVersions.iterator(); it.hasNext();) {
            ScheduleVersion s = (ScheduleVersion) it.next();

            if (s.getBaseline() != null && (!(s.getBaseline().equals(currentBaseline)))) {
                currentBaseline = s.getBaseline();
                lastWasPreBaseline = false;
                xml.append(s.getBaseline().getXMLBody());
            } else if (s.getBaseline() == null && !lastWasPreBaseline) {
                currentBaseline = null;
                lastWasPreBaseline = true;
                xml.append("<pre-baseline/>");
            }

            xml.append(s.getXMLBody());
        }
        xml.append("</scheduleHistory>");

        return xml.toString();
    }


}
