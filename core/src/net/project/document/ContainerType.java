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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.document;

/**
 * ContainerType identifies the different types of containers that are special
 * "system containers" in the document vault.  Any of these containers are hidden
 * from normal browsing from within the space.
 *
 * @author Matthew Flower
 * @since Gecko Update 2 (ProductionLink)
 */
public class ContainerType {
    public static ContainerType FORM_DATA_DOCUMENT_CONTAINER = new ContainerType("Form Data");
    public static ContainerType SYSTEM_CONTAINER = new ContainerType("");
    public static ContainerType METRICS_CONTAINER = new ContainerType("Metrics");
    public static ContainerType BUDGET_CONTAINER = new ContainerType("Budget");
    public static ContainerType ORGANIZATION_CHART_CONTAINER = new ContainerType("Organization Chart");

    /**
     * This prefix is used to identify a container in the document vault.  For
     * example, to find the budget container for a project, one would find
     * {spaceID}::BUDGET_CONTAINER.getContainerPrefix();.
     *
     * @author Matthew Flower
     * @since Gecko Update 2
     * @returns an identifier used to find this container in the document vault.
     */
    private String containerPrefix = null;

    /**
     * Get an identifier used to find this container in the document vault.  To find
     * this container, you would combine:
     *    {space_id}::{containerPrefix}
     *
     * @return a <code>String</code> value containing the containerPrefix in the
     * document vault.
     */
    public String getContainerPrefix() {
        return this.containerPrefix;
    }

    public String getFolderName(String spaceID) {
        return spaceID + "::" + getContainerPrefix();
    }
    
    /**
     * This is a private constructor used to construct instances.  It cannot be
     * called outside of this class or in subclasses.  (This is on purpose.)
     *
     * @param containerPrefix a <code>String</code> value
     */
    private ContainerType(String containerPrefix) {
        this.containerPrefix = containerPrefix;
    }
}


