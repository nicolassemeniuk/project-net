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
-- Update table data

UPDATE PN_NOTIFICATION_TYPE SET IS_CREATE_TYPE = 1 WHERE NOTIFICATION_TYPE_ID IN (2360, 2330, 2340, 2320, 2350, 2310)
/

INSERT INTO PNET.PN_NOTIFICATION_TYPE("NOTIFICATION_TYPE_ID", "NAME", "DESCRIPTION", "DEFAULT_MESSAGE", "OBJECT_TYPE", "CREATE_DATE", "CREATED_BY_ID", "MODIFIED_DATE", "MODIFIED_BY_ID", "RECORD_STATUS", "CRC", "IS_CREATE_TYPE")
VALUES (2364, 'post_create', '@prm.notification.type.createpost.description', '@prm.notification.type.createpost.default.message', 'post', NULL, 1, NULL, 1, 'A', NULL, '1')
/

INSERT INTO PNET.PN_EVENT_TYPE("EVENT_TYPE_ID", "NAME", "DESCRIPTION", "OBJECT_TYPE", "RECORD_STATUS", "CRC")
VALUES (2364, 'post_create', '@prm.discussion.post.event.create.description', 'post', 'A', NULL)
/

INSERT INTO PNET.PN_EVENT_HAS_NOTIFICATION("NOTIFICATION_TYPE_ID", "EVENT_TYPE_ID")
VALUES (2364, 2364)
/

UPDATE PN_NOTIFICATION_TYPE SET RECORD_STATUS = 'D' WHERE NOTIFICATION_TYPE_ID = 2360
/