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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.process;

import java.io.Serializable;

import net.project.base.property.PropertyProvider;
import net.project.code.TableCodeDomain;

/**
 * This Bean wraps Deliverable.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class DeliverableBean extends Deliverable implements Serializable {

    /**
     * Construct an empty DeliverableBean.
     */
    public DeliverableBean() {
    }

    /**
     * Get an HTML option list of status options with the tasks current status
     * selected.
     *
     * @return String     HTML option list
     */
    public String getStatusOptionList() {
        TableCodeDomain domain = new TableCodeDomain();

        domain.setTableName("pn_deliverable");
        domain.setColumnName("status_id");

        domain.load();

        return domain.getOptionList(m_status_id);
    }

    /**
     * Get an HTML option list for the is_optional field with the current option
     * selected.
     *
     * @return String     HTML option list
     */
    public String getOptionalOptionList() {
        StringBuffer sb = new StringBuffer();
        if (m_is_optional != null) {
            if (m_is_optional.equals("0")) {
                sb.append("<OPTION VALUE=\"0\" SELECTED>" + PropertyProvider.get("prm.project.process.deliverablebean.option.no.name"));
                sb.append("<OPTION VALUE=\"1\">" + PropertyProvider.get("prm.project.process.deliverablebean.option.yes.name"));
            } else {
                sb.append("<OPTION VALUE=\"0\">" + PropertyProvider.get("prm.project.process.deliverablebean.option.no.name"));
                sb.append("<OPTION VALUE=\"1\" SELECTED>" + PropertyProvider.get("prm.project.process.deliverablebean.option.yes.name"));
            }
        } else {
            sb.append("<OPTION VALUE=\"0\" SELECTED>" + PropertyProvider.get("prm.project.process.deliverablebean.option.no.name"));
            sb.append("<OPTION VALUE=\"1\">" + PropertyProvider.get("prm.project.process.deliverablebean.option.yes.name"));
        }
        return sb.toString();
    }

}



