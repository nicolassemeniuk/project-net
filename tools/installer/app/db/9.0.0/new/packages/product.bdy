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
CREATE OR REPLACE PACKAGE BODY product IS
--
-- MODIFICATION HISTORY
-- Person      Date    Comments
-- ---------   ------  ------------------------------------------
-- Phil        1/15/01 created

Procedure GET_VERSION
    (i_product IN VARCHAR2,
     o_current_version OUT VARCHAR2)

IS

BEGIN

    -- select string of major.minor.sub-minor.build
    SELECT lpad(major_version,2,0)||'.'||lpad(minor_version,2,0)||'.'||lpad(sub_minor_version,2,0)||'.'||lpad(build_version,2,0) INTO o_current_version
      FROM product_version
      WHERE product = i_product
      AND timestamp = (select max(timestamp) from product_version where product = i_product);

EXCEPTION
    WHEN OTHERS THEN
        o_current_version := NULL;

END; -- Procedure GET_VERSION

/*
 Returns the database version
 */
Procedure GET_DATABASE_VERSION (
    o_current_version OUT VARCHAR2,
    o_client_version OUT VARCHAR2,
    o_client_name OUT VARCHAR2)
IS

BEGIN
    -- select string of major.minor.sub-minor.x
    BEGIN
        select
  major_version||'.'||minor_version||'.'||sub_minor_version||'.x' into o_current_version
from
  database_version dv,
  (select max(major_version*10000 + minor_version*100 + sub_minor_version) as version_id
     from database_version) maxdv
where
  (major_version*10000 + minor_version*100 + sub_minor_version) = maxdv.version_id;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            o_current_version := null;
    END;

        BEGIN
        select
  major_version||'.'||minor_version||'.'||sub_minor_version||'.x' into o_client_version
from
  client_database_version dv,
  (select max(major_version*10000 + minor_version*100 + sub_minor_version) as version_id
     from client_database_version) maxdv
where
  (major_version*10000 + minor_version*100 + sub_minor_version) = maxdv.version_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            o_client_version := null;
    END;

    BEGIN
        SELECT client_name INTO o_client_name
        FROM client_database_version
        WHERE timestamp = (select max(timestamp) from client_database_version);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            o_client_name := null;
    END;
END; -- Procedure GET_DATABASE_VERSION

END;
/

