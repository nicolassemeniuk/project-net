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

 package net.project.crossspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * These are actions allowed by a sharing space.
 */
public class AllowableAction {
    private static Map allowableActions = new HashMap();
    public static final AllowableAction SHARE = new AllowableAction(1, "prm.sharing.action.share.name");
    public static final AllowableAction SHARE_READ_ONLY = new AllowableAction(2, "prm.sharing.action.sharereadonly.name");
    public static final AllowableAction COPY = new AllowableAction(4, "prm.sharing.action.copy.name");
    public static final AllowableAction MOVE = new AllowableAction(8, "prm.sharing.action.move.name");

    private final int id;
    private final String nameToken;

    public static AllowableAction getForID(String id) {
        return (AllowableAction)allowableActions.get(id);
    }

    public static List getAll() {
        return new ArrayList(allowableActions.values());
    }

    protected AllowableAction(int id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
        allowableActions.put(String.valueOf(id), this);
    }

    public int getID() {
        return id;
    }


}
