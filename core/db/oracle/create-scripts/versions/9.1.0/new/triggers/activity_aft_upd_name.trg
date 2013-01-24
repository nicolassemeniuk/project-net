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
CREATE OR REPLACE TRIGGER activity_aft_upd_name
AFTER INSERT OR UPDATE OF name ON PNET.PN_ACTIVITY
FOR EACH ROW
when (NEW.name != OLD.name OR OLD.name IS NULL)
BEGIN
  IF INSERTING THEN
    INSERT INTO PN_OBJECT_NAME
      (object_id, name)
    VALUES
      (:NEW.object_id, :NEW.name);
  ELSE
    UPDATE PN_OBJECT_NAME
    SET name = :NEW.name
    WHERE object_id = :NEW.object_id;
  END IF;
END;
/

