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

 package net.project.base;

public class PnetExceptionTypes {

    public static final int USER_IS_NULL = 100;

    public static final int SQL_EXCEPTION = 101;

    public static final int DB_EXCEPTION = 102;

    public static final int OBJECT_ID_IS_NULL = 103;

    public static final int OBJECT_IS_NOT_LOADED = 104;

    public static final int SESSION_IS_NOT_ACTIVE = 200;

    public static final int DOCUMENT_MANAGER_COULD_NOT_BE_LOADED = 1000;

    public static final int CHECK_IN_FAILED_NOT_CHECKED_OUT = 1001;

    public static final int CHECK_IN_FAILED_NOT_CKO_BY_USER = 1002;

    public static final int CHECK_IN_FAILED_UNKNOWN_REASON = 1003;

    public static final int CHECK_OUT_FAILED_ALREADY_CHECKED_OUT = 1004;

    public static final int CHECK_OUT_FAILED_UNKNOWN_REASON = 1005;

    public static final int UNDO_CHECK_OUT_FAILED_NOT_CHECKED_OUT = 1006;

    public static final int UNDO_CHECK_OUT_FAILED_NOT_CKO_BY_USER = 1007;

    public static final int UNDO_CHECK_OUT_FAILED_UNKNOWN_REASON = 1008;

    public static final int UPDATE_FAILED_DB_OUT_OF_SYNC = 1009;

    public static final int UPDATE_FAILED_DOCUMENT_IS_CKO = 1010;

    public static final int UNIQUE_NAME_CONSTRAINT = 1011;

    public static final int FILE_UPLOAD_FAILED_SPACE_ID_NULL = 1012;

    public static final int FILE_UPLOAD_FAILED_DOC_SPACE_ID_NULL = 1013;

    public static final int FILE_UPLOAD_FAILED_OBJECT_ID_NULL = 1014;

    public static final int DOCUMENT_REMOVE_FAILED_CKO_BY_ANOTHER_USER = 1015;

    public static final int DOCUMENT_MOVE_FAILED_TRIED_MOVING_OBJECT_INTO_ITSELF = 1016;

    public static final int DOCUMENT_MOVE_FAILED_TRIED_MOVING_OBJECT_INTO_ITS_CHILD = 1017;

    public static final int FILE_MANAGER_FAILED_TO_GET_REPOSITORY_BASE = 1018;



} // end class PnetExceptionTypes
