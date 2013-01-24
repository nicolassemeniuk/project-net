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

 package net.project.workflow;

/**
 * Workflow ErrorCodes
 */
public interface ErrorCodes {

    /**
     * The Code class simply defines an error code
     */
    public class Code {
        /** code number */
        private int code;

        /**
         * Create new Code.  Only ErrorCodes can do this.
         */
        private Code(int code) {
            this.code = code;
        }

        /**
         * Return the internal code as a string
         * @return the internal code
         */
        public String toString() {
            return "" + this.code;
        }

    }

    /*
        0 - 999 : Workflow Designer errors
     */

    /* 100 - 199 : Workflow errors */

    /** workflow record modified in database and update failed as a result */
    public static final Code WORKFLOW_RECORD_MODIFIED = new Code(100);

    /** workflow record locked in database and update failed as a result */
    public static final Code WORKFLOW_RECORD_LOCKED = new Code(101);


    /* 200 - 299 : Step errors */

    /** step record modified in database and update failed as a result */
    public static final Code STEP_RECORD_MODIFIED = new Code(200);

    /** step record locked in database and update failed as a result */
    public static final Code STEP_RECORD_LOCKED = new Code(201);

    /** step has group record modified in database and update failed as a result */
    public static final Code STEP_GROUP_RECORD_MODIFIED = new Code(250);


    /* 300 - 399 : Transition errors */

    /** transition record modified in database and update failed as a result */
    public static final Code TRANSITION_RECORD_MODIFIED = new Code(300);

    /** transition record locked in database and update failed as a result */
    public static final Code TRANSITION_RECORD_LOCKED = new Code(301);


    /* 400 - 499 : Rule errors */

    /** step record modified in database and update failed as a result */
    public static final Code RULE_RECORD_MODIFIED = new Code(400);

    /** rule record locked in database and update failed as a result */
    public static final Code RULE_RECORD_LOCKED = new Code(401);

    /** rule type could not be loaded */
    public static final Code RULE_TYPE_LOAD_ERROR = new Code(450);

    /** rule status could not be loaded */
    public static final Code RULE_STATUS_LOAD_ERROR = new Code(451);


    /*
        1000 - 1999 : Envelope manipulation errors
     */

    /** error performing transition: transition not available */
    public static final Code TRANSITION_NOT_AVAILABLE = new Code(1000);

    /** error determining available transitions for envelope */
    public static final Code TRANSITION_UNKNOWN = new Code(1001);

    /** error storing envelope when performing transition */
    public static final Code TRANSITION_ENVELOPE_STORE_ERROR = new Code(1002);

    /** error storing history when performing transition */
    public static final Code TRANSITION_HISTORY_STORE_ERROR = new Code(1003);

    /** error gettting rules for transition */
    public static final Code TRANSITION_RULE_ERROR = new Code(1004);

    /** error performing notification for transition */
    public static final Code TRANSITION_NOTIFICATION_ERROR = new Code(1005);

    /** error loading envelope during transition */
    public static final Code TRANSITION_ENVELOPE_LOAD_ERROR = new Code(1006);

    /** object is not workflowable; it has not implemented the IWorkflowable interface */
    public static final Code OBJECT_NOT_WORKFLOWABLE = new Code(1500);

}
