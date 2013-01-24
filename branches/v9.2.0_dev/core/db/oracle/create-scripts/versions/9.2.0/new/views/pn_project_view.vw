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
CREATE OR REPLACE VIEW PN_PROJECT_VIEW AS
SELECT
      p.project_id,
      p.project_name,
      p.project_desc,
      p.percent_calculation_method,
      p.percent_complete,
      p.status_code_id,
      gd2.code_name as status,
      p.color_code_id,
      gd3.code_name as color,
      gd3.code_url as color_image_url,
      p.is_subproject,
      p.record_status,
      p.project_logo_id,
      p.start_date,
      p.end_date,
      p.date_modified,
      p.default_currency_code,
      p.sponsor_desc,
      p.improvement_code_id,
      p.current_status_description,
      p.financial_status_color_code_id,
      p.financial_status_imp_code_id,
      p.budgeted_total_cost_value,
      p.budgeted_total_cost_cc,
      p.current_est_total_cost_value,
      p.current_est_total_cost_cc,
      p.actual_to_date_cost_value,
      p.actual_to_date_cost_cc,
      p.estimated_roi_cost_value,
      p.estimated_roi_cost_cc,
      p.cost_center,
      p.schedule_status_color_code_id,
      p.schedule_status_imp_code_id,
      p.resource_status_color_code_id,
      p.resource_status_imp_code_id,
      p.priority_code_id,
      p.risk_rating_code_id,
      p.visibility_id
  FROM
      pn_project_space p,
      pn_global_domain gd2,
      pn_global_domain gd3
  WHERE
      gd2.table_name = 'pn_project_space' and
      gd2.column_name = 'status_code_id' and
      gd2.code = p.status_code_id and
      gd3.table_name = 'pn_project_space' and
      gd3.column_name = 'color_code_id' and
      gd3.code = p.color_code_id
/

