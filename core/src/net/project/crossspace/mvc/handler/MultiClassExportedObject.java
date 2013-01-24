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

 package net.project.crossspace.mvc.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.project.crossspace.AllowableActionCollection;
import net.project.crossspace.ExportedObject;
import net.project.crossspace.TradeAgreement;
import net.project.util.NumberUtils;

import org.apache.commons.collections.CollectionUtils;

public class MultiClassExportedObject {
    public MultiClassValue permissionType = new MultiClassValue();
    public MultiClassValue permittedSpaces = new MultiClassValue();
    public MultiClassValue permittedUsers = new MultiClassValue();
    public MultiClassValue propagateToChildren = new MultiClassValue();
    public MultiClassValue allowableActions = new MultiClassValue();

    public boolean allConsistent() {
        return permissionType.consistent && permittedSpaces.consistent &&
            permittedUsers.consistent && propagateToChildren.consistent &&
            allowableActions.consistent;
    }

    /**
     * This class attempts to combine the information from several
     * <code>ExportedObject</code> objects into one for the purpose of trying to
     * figure out what to show on the modify page.  Think of it this way -- if
     * you have only one textbox to show a name, what do you show if you have
     * two objects with different names.  The resulting class --
     * MultiClassExportedObject has the information that answers the question.
     * (Or at least, it tries to.)
     *
     * @param exportedObjects a <code>Collection</code> containing zero or more
     * <code>ExportObject</code>s
     * @return a <code>MultiClassExportedObject</code> which has either combined
     * the fields appropriately, or has created good defaults if no objects have
     * been exported.
     */
    public static MultiClassExportedObject processExportedObjects(Collection exportedObjects) {
        boolean first = true;

        //Initial settings -- these are the same even if there are no exported
        //objects found.
        MultiClassExportedObject mcObject = new MultiClassExportedObject();
        mcObject.permissionType.consistent = true;
        mcObject.permittedSpaces.consistent = true;
        mcObject.permittedUsers.consistent = true;
        mcObject.permissionType.value = TradeAgreement.NO_ACCESS.getID();
        mcObject.permittedSpaces.value = Collections.EMPTY_LIST;
        mcObject.permittedUsers.value = Collections.EMPTY_LIST;
        mcObject.propagateToChildren.value = Boolean.FALSE;
        mcObject.allowableActions.value = AllowableActionCollection.DEFAULT;


        for (Iterator it = exportedObjects.iterator(); it.hasNext();) {
            ExportedObject export = (ExportedObject) it.next();

            if (first) {
                mcObject.permissionType.value = export.getPermissionType();
                mcObject.permittedSpaces.value = export.getPermittedSpaces();
                mcObject.permittedUsers.value = export.getPermittedUsers();
                mcObject.propagateToChildren.value = new Boolean(export.isPropagateToChildren());
                mcObject.allowableActions.value = export.getAllowableActions();
                first = false;
            } else {
                if (!export.getPermissionType().equals(mcObject.permissionType.value)) {
                    mcObject.permissionType.consistent = false;
                    mcObject.permissionType.value = NumberUtils.max(((TradeAgreement)mcObject.permissionType.value), export.getPermissionType());
                }
                if (!export.getPermittedUsers().equals(mcObject.permittedUsers.value)) {
                    mcObject.permittedUsers.consistent = false;
                    mcObject.permittedUsers.value = CollectionUtils.union(
                        ((Collection)mcObject.permittedUsers.value),
                        export.getPermittedUsers()
                    );
                }
                if (!export.getPermittedSpaces().equals(mcObject.permittedSpaces.value)) {
                    mcObject.permittedSpaces.consistent = false;
                    mcObject.permittedSpaces.value = CollectionUtils.union(
                        ((Collection)mcObject.permittedSpaces.value),
                        export.getPermittedSpaces()
                    );
                }
                if (!new Boolean(export.isPropagateToChildren()).equals(mcObject.propagateToChildren.value)) {
                    mcObject.propagateToChildren.consistent = false;
                    mcObject.propagateToChildren.value = Boolean.TRUE;
                }
                ((AllowableActionCollection)mcObject.allowableActions.value).addAll(export.getAllowableActions().getAll());
            }
        }

        return mcObject;
    }
}
