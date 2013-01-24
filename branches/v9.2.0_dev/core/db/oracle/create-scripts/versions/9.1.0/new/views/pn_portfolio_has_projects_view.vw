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
CREATE OR REPLACE VIEW PN_PORTFOLIO_HAS_PROJECTS_VIEW AS
SELECT
    port.portfolio_id,
    port.portfolio_name,
    p.project_id,
    p.project_name,
    p.project_desc,
    p.status_code_id,
    p.percent_complete,
    d1.code_name as status,
    p.color_code_id,
    d2.code_name as color,
    d2.code_url as color_image_url,
    p.is_subproject,
    p.record_status
FROM
    pn_portfolio_has_space phs,
    pn_portfolio port,
    pn_project_space p,
    pn_global_domain d1,
    pn_global_domain d2
WHERE
    d1.table_name = 'pn_project_space' and
    d1.column_name = 'status_code_id' and
    d1.code = p.status_code_id and
    d2.table_name = 'pn_project_space' and
    d2.column_name = 'color_code_id' and
    d2.code = p.color_code_id and
    phs.portfolio_id = port.portfolio_id and
    phs.space_id = p.project_id
/

