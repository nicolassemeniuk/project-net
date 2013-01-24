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
CREATE OR REPLACE PACKAGE project IS

-- MODIFICATION HISTORY
-- Person      Date       Comments
-- ---------   ------     ------------------------------------------
-- Robin       27-Apr-00  Creation.
-- Robin       11-May-00  Changed error codes to coincide with new table.
-- Brian       12-Jun-00  Added business space logic.

-- error logging
err_num NUMBER;
err_msg VARCHAR2(120);

success CONSTANT NUMBER:=0;
generic_error CONSTANT NUMBER:=101;
no_data CONSTANT NUMBER:=102;
dupe_key CONSTANT NUMBER:=103;
null_field CONSTANT NUMBER:=104;
no_parent_key CONSTANT NUMBER:=105;
check_violated CONSTANT NUMBER:=106;

e_null_constraint EXCEPTION;
e_unique_constraint EXCEPTION;
e_check_constraint EXCEPTION;
e_no_parent_key EXCEPTION;
e_no_data EXCEPTION;

PRAGMA EXCEPTION_INIT (e_unique_constraint, -00001);
PRAGMA EXCEPTION_INIT (e_null_constraint, -01400);
PRAGMA EXCEPTION_INIT (e_no_parent_key, -02291);
PRAGMA EXCEPTION_INIT (e_check_constraint, -02290);

TYPE ReferenceCursor            IS REF CURSOR;


Procedure invite_person_to_project
(
    i_project_id IN NUMBER,
    i_email IN VARCHAR2,
    i_firstname IN VARCHAR2,
    i_lastname IN VARCHAR2,
    i_responsibilities IN VARCHAR2,
    i_invitor_id IN NUMBER,
    o_invitation_code OUT NUMBER,
    o_status OUT NUMBER
);


Procedure respond_to_project_invite
(
    i_invitation_code IN NUMBER,
    i_project_id IN NUMBER,
    i_person_id IN NUMBER,
    i_response IN VARCHAR2,
    o_status OUT NUMBER
);

PROCEDURE get_property_group_info
(
    project_id IN varchar2,
    property_group_id OUT NUMBER,
    property_sheet_type OUT varchar2,
    property_table_name OUT varchar2
);

procedure add_logo_to_project
(
    project_id IN varchar2,
    logo_id IN varchar2
);

PROCEDURE create_project
(
    p_proj_creator IN VARCHAR2,
    p_subproject_of IN VARCHAR2,
    p_business_space_id IN VARCHAR2,
    p_project_visibility IN VARCHAR2,
    p_proj_name IN VARCHAR2,
    p_proj_desc IN VARCHAR2,
    p_proj_status IN VARCHAR2,
    p_proj_color_code IN VARCHAR2,
    p_calculation_method in varchar2,
    p_percent_complete IN varchar2,
    p_start_date IN date,
    p_end_date IN date,
    p_serial IN varchar2,
    p_project_logo_id IN varchar2,
    p_default_currency_code in varchar2,
    p_sponsor in varchar2,
    p_improvement_code_id in number,
    p_current_status_description in varchar2,
    p_financial_stat_color_code_id in number,
    p_financial_stat_imp_code_id in number,
    p_budgeted_total_cost_value in number,
    p_budgeted_total_cost_cc in varchar2,
    p_current_est_total_cost_value in number,
    p_current_est_total_cost_cc in varchar2,
    p_actual_to_date_cost_value in number,
    p_actual_to_date_cost_cc in varchar2,
    p_estimated_roi_cost_value in number,
    p_estimated_roi_cost_cc in varchar2,
    p_cost_center in varchar2,
    p_schedule_stat_color_code_id in number,
    p_schedule_stat_imp_code_id in number,
    p_resource_stat_color_code_id in number,
    p_resource_stat_imp_code_id in number,
    p_priority_code_id in number,
    p_risk_rating_code_id in number,
    p_visibility_id in number,
    p_autocalc_schedule in number,
    p_plan_name in varchar2,
    p_create_share in number,
    o_project_id OUT NUMBER,
    o_workplan_id OUT NUMBER,
    o_space_admin_role_id OUT NUMBER
);

PROCEDURE update_properties
(
    p_project_id IN VARCHAR2,
    whoami IN varchar2,
    p_business_space_id IN VARCHAR2,
    p_project_visibility IN VARCHAR2,
    p_proj_name IN VARCHAR2,
    p_proj_desc IN VARCHAR2,
    p_proj_status IN VARCHAR2,
    p_proj_color_code IN VARCHAR2,
     p_calculation_method in varchar2,
    p_percent_complete IN varchar2,
    p_start_date IN date,
    p_end_date IN date,
    p_project_logo_id IN varchar2,
    p_default_currency_code in varchar2,
    p_sponsor in varchar2,
    p_improvement_code_id in number,
    p_current_status_description in varchar2,
    p_financial_stat_color_code_id in number,
    p_financial_stat_imp_code_id in number,
    p_budgeted_total_cost_value in number,
    p_budgeted_total_cost_cc in varchar2,
    p_current_est_total_cost_value in number,
    p_current_est_total_cost_cc in varchar2,
    p_actual_to_date_cost_value in number,
    p_actual_to_date_cost_cc in varchar2,
    p_estimated_roi_cost_value in number,
    p_estimated_roi_cost_cc in varchar2,
    p_cost_center in varchar2,
    p_schedule_stat_color_code_id in number,
    p_schedule_stat_imp_code_id in number,
    p_resource_stat_color_code_id in number,
    p_resource_stat_imp_code_id in number,
    p_priority_code_id in number,
    p_risk_rating_code_id in number,
    p_visibility_id in number,
    i_serial_number IN varchar2
)
;

PROCEDURE remove
(
    project_id IN VARCHAR2,
    whoami IN VARCHAR2
);

FUNCTION get_heartbeat_metrics
(
    project_id in pn_project_space.project_id%type
) RETURN ReferenceCursor;

FUNCTION ENCODE_SERIAL_NUMBER
(
    i_serial_number in varchar2
) return varchar2;

PROCEDURE activate
(
    project_id IN VARCHAR2,
    whoami IN VARCHAR2
);


END; -- Package Specification PROJECT
/

