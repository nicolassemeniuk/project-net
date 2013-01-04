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
CREATE OR REPLACE PACKAGE user_domain IS


  FUNCTION getCountUsersForDomain
     ( i_domain_id IN number)
     RETURN  number;

     PROCEDURE removeUser
    (
        i_domain_id in number,
        i_user_id in varchar2
    );

    PROCEDURE CREATE_MIGRATION_RECORD
    (
        i_domain_migration_id in number,
        i_source_domain_id in number ,
        i_migration_status_id in number
    );

END; -- Package spec
/

