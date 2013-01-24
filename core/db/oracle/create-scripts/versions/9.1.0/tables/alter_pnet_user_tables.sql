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
declare cursor c is
select table_name from ALL_TABLES a
where a.OWNER = 'PNET_USER'
minus
select a.table_name
from ALL_TABLES a ,ALL_TAB_COLUMNS atc
where a.owner = atc.OWNER
and a.table_name = atc.table_name
and atc.column_name = 'CREATOR_EMAIL'
and a.OWNER = 'PNET_USER';

c_rec c%ROWTYPE;
sss ALL_TABLES.table_name%type;
com VARCHAR2(100);
BEGIN

FOR c_rec IN c
LOOP
DBMS_OUTPUT.PUT_LINE('Altering table '||c_rec.table_name);
sss := c_rec.table_name;
com := 'ALTER TABLE  PNET_USER.'||sss||' ADD (CREATOR_EMAIL VARCHAR2(100))';
execute immediate com;
END LOOP;
EXCEPTION
WHEN OTHERS THEN
DBMS_OUTPUT.PUT_LINE('error:'||sqlcode||' '||sqlerrm);
END;
/