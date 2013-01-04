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
CREATE OR REPLACE VIEW PN_PORTFOLIO_VIEW AS
SELECT
    php.portfolio_id,
    php.portfolio_name,
    php.project_id,
    php.project_name,
    php.project_desc,
    php.percent_complete,
    -- status
    php.status_code_id,
    php.status,
    -- color
    php.color_code_id,
    php.color,
    php.color_image_url,
    php.is_subproject,
    shs.parent_space_id as project_owner
FROM
    pn_portfolio_has_projects_view php,
    (select parent_space_id, child_space_id from pn_space_has_space
    where relationship_parent_to_child = 'owns')shs
WHERE
    php.record_status = 'A' and
    shs.child_space_id = php.project_id
/

