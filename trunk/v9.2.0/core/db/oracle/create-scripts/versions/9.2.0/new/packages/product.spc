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
CREATE OR REPLACE PACKAGE product IS
--
-- General product support (version #'s, etc)

--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Phil        1/15/01 created
-- Tim         04/09/01 Added GET_DATABASE_VERSION since we use a different
--                      table for database version
-- Matt        09/24/01 Add o_client_version and o_client_name parameters to
--                      getDatabaseVersion().

PROCEDURE get_version
 ( i_product IN VARCHAR2,
   o_current_version OUT varchar2);

/*
 Returns the database version
 */
procedure GET_DATABASE_VERSION
    (o_current_version OUT VARCHAR2,
     o_client_version OUT VARCHAR2,
     o_client_name OUT VARCHAR2);

END; -- Package spec
/

