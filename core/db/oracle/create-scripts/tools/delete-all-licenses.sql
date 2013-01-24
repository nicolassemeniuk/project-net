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
/* 
 * deletes all project.net licensing files, including master properties and user license assignments.
 * After running this script, you must install a new master properties file and new license(s).
 */

delete from pn_ledger;
delete from pn_invoice;
delete from pn_invoice_lob;
delete from pn_bill;
delete from pn_license_purchaser;
delete from pn_license_person_history;
delete from pn_license;
delete from pn_license_certificate;
delete from pn_license_certificate_lob;
delete from pn_license_master_prop_clob;
delete from pn_person_has_license;
commit;
