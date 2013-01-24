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
+----------------------------------------------------------------------*/
package net.project.document;

import net.project.persistence.PersistenceException;

/**
 * An ICopier provides copy functionality for certain object types.
 */
public interface ICopier {

    /**
     * Copies the object with the specified id into the target container.
     * After copying, a new copy of the object will exist with <code>targetContainerID</code>
     * being its parent container id.<br>
     * <b>Note: No commits or rollbacks should be performed in this method.</b><br>
     * @param db the DBBean in which to perform the transaction
     * @param sourceObjectID the id of the object to copy
     * @param targetContainerID the id of the container that will be the parent
     * container of the new copy
     * @param currentUser the current user performing the copy
     * @return the new object ID of the copied object
     * @throws PersistenceException if there is a persistence error while copying;
     * these kinds of errors will normally imply a serious problem
     * @throws CopyException if there is a problem copying; these errors may
     * be recoverable
     */
    public String copy(net.project.database.DBBean db, String sourceObjectID, 
        String targetContainerID, net.project.security.User currentUser) 
            throws net.project.persistence.PersistenceException, CopyException;

}
