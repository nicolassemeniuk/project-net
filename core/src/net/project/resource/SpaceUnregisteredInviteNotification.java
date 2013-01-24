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
package net.project.resource;

/**
 * Notification sent to unregistered users inviting them to a space.
 * This includes information about registering in the application.
 */
public class SpaceUnregisteredInviteNotification extends SpaceInviteNotification {

    /** The stylsheet for transforming notification, currently <code>/roster/xsl/unregistered-space-invite.xsl</code>. */
    protected static final String UNREG_NOTIFICATION_STYLE_SHEET = "/roster/xsl/unregistered-space-invite.xsl";

    /**
     * Returns the stylesheet to use for unregistered-style notification.
     * @return the stylesheet path given by {@link #UNREG_NOTIFICATION_STYLE_SHEET}.
     */
    protected String getStylesheet() {
        return UNREG_NOTIFICATION_STYLE_SHEET;
    }

}
