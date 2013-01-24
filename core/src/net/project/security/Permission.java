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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.security;

import net.project.security.group.Group;

/**
 * Provides a set of Actions that a Role may perform on an object. An int member
 * variable is used for storing and managing actions instead of the Action
 * class. This is enhances performance of checking permissions on on object each
 * click.
 *
 * @author Roger Bly
 */
public abstract class Permission {
    public static final String EXIST = "Exist";
    public static final String NEW = "New";
    public static final String DELETED = "Delete";

    /** The Group that this Permission applies to. */
    private Group group;
    /** the 32 bit array for actions allowed by this permission */
    private int actions = 0;
    /** The Space context for this permission */
    private String status = null;


    /**
     * Set the Group context.
     *
     * @param group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Get the group that this permission refers to.
     *
     * @return a <code>Group</code> object identifying the group that this
     * permission refers to.
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Set the action bits for this permission.
     *
     * @param actions an int representing the action bit array.
     */
    public void setActionBits(int actions) {
        this.actions = actions;
    }


    /**
     * Get action bits for this permission.
     *
     * @return an int representing the bit array of actions for this
     *         permission.
     */
    public int getActionsBits() {
        return actions;
    }

    /**
     * Set the status of the Permission.
     *
     * @param value the status, one of <code>{@link #EXIST}</code>, <code>{@link
     * #NEW}</code>, <code>{@link #DELETED}</code>,
     */
    public void setStatus(String value) {
        status = value;
    }

    /**
     * Returns the status of the Permission
     *
     * @return a <code>String</code> value containing the status of the
     *         Permission.
     */
    public String getStatus() {
        return status;
    }


    /**
     * Clears all the action for this permission.
     */
    public void clearAll() {
        actions = 0;
    }


    /**
     * Returns true if any action specified in the actionBitMask is granted for
     * this permission.
     *
     * @param actionBitMask
     * @return a <code>boolean</code> value which will be true if any action
     *         specified in the <code>actionBitMask</code> parameter is
     *         granted.
     */
    public boolean actionAllowed(int actionBitMask) {
        return ((actions & actionBitMask) > 0);
    }

    /**
     * Grant an action for this permission. Usage:   Permission
     * permission.grant(Permission.MODIFY);
     *
     * @param action the action to grant.
     */
    public void grant(Action action) {
        actions = actions | action.getBitMask();
    }


    // I think this is bogus:
    // XOR is not correct for revoking
    // If you try and revoke a bit that is not set, it actually grants it?
    // e.g.  110 ^ 001 = 111 = WRONG
    // if bit is set, it works:  011 ^ 001 = 010
    //
    // better to use (action & ~bitset), then it works regardless
    // 110 & ~001 = 110
    // 011 & ~001 = 010
    // 
    /**
     Revoke an action for this permission.
     The bitwise exclusive or operator is used to clear one or more requested actions.
     @param action the bit mask for the action to revoke.
     * /
     public void revoke(Action action)
     {
     // TODO -- dangerous:  will  grant if called twice.
     m_actions = m_actions ^ action.getBitMask();
     } */

}
