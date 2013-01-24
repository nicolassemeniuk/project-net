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
package net.project.hibernate.util;

import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnModulePermissionPK;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;
import net.project.hibernate.service.ServiceFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 19.05.2007
 * Time: 10:57:39
 * To change this template use File | Settings | File Templates.
 */
public class InsertObjectUtils {
    public static void insertPnObjectPermission(Integer object_id, Integer group_id, long actions) {
        PnObjectPermission objectPermission = new PnObjectPermission();
        PnObjectPermissionPK objectPermissionPk = new PnObjectPermissionPK();
        objectPermission.setComp_id(objectPermissionPk);
        objectPermissionPk.setGroupId(group_id);
        objectPermissionPk.setObjectId(object_id);
        objectPermission.setActions(actions);
        ServiceFactory.getInstance().getPnObjectPermissionService().saveObjectPermission(objectPermission);
    }

    public static void savePnModulePermission(Integer module_id, Integer space_id, Integer group_id, long actions) {
        PnModulePermission newPnModulePermission = new PnModulePermission();
        PnModulePermissionPK pkPnModulePermissionPK = new PnModulePermissionPK();
        newPnModulePermission.setComp_id(pkPnModulePermissionPK);
        pkPnModulePermissionPK.setSpaceId(space_id);
        pkPnModulePermissionPK.setModuleId(module_id);
        pkPnModulePermissionPK.setGroupId(group_id);
        newPnModulePermission.setActions(actions);
        ServiceFactory.getInstance().getPnModulePermissionService().saveModulePermission(newPnModulePermission);
    }

    public static void savePnDefaultObjectPermission(Integer space_id, String object_type, Integer group_id, long actions) {
        PnDefaultObjectPermission newDefaultObjectPermission = new PnDefaultObjectPermission();
        PnDefaultObjectPermissionPK pk = new PnDefaultObjectPermissionPK();
        newDefaultObjectPermission.setComp_id(pk);
        pk.setSpaceId(space_id);
        pk.setObjectType(object_type);
        pk.setGroupId(group_id);
        newDefaultObjectPermission.setActions(actions);
        ServiceFactory.getInstance().getPnDefaultObjectPermissionService().saveDefaultObjectPermission(newDefaultObjectPermission);
    }

}
