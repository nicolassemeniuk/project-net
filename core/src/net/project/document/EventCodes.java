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

 
package net.project.document;


public class EventCodes {

    public static final String CREATE_DOCUMENT = "doc_create";
    public static final String MODIFY_PROPERTIES = "doc_modify_properties";
    public static final String VIEW_DOCUMENT = "doc_view";
    public static final String VIEW_DOCUMENT_VERSION = "doc_view_version";
    public static final String VIEW_PROPERTIES = "doc_view_properties";
    public static final String CHECK_OUT_DOCUMENT = "doc_check_out";
    public static final String CHECK_IN_DOCUMENT = "doc_check_in";
    public static final String UNDO_CHECK_OUT_DOCUMENT = "doc_undo_check_out";
    public static final String CREATE_CONTAINER = "container_create";
    public static final String REMOVE_DOCUMENT = "doc_remove_object";
    public static final String UNDO_REMOVE_DOCUMENT = "undo_doc_remove_object";
    public static final String REMOVE_CONTAINER = "container_remove";
    public static final String UNDO_REMOVE_CONTAINER = "undo_container_remove";
    public static final String MOVE_OBJECT = "doc_move_object";
    public static final String COPY_OBJECT = "doc_copy_object";


    public static String getName (String eventCode) {

	String name = null;

	if (eventCode.equals(CREATE_DOCUMENT))
	    name = "Create";
	else if (eventCode.equals(MODIFY_PROPERTIES))
	    name = "Modify Properties";
	else if (eventCode.equals(VIEW_DOCUMENT_VERSION))
	    name = "View Version";
	else if (eventCode.equals(VIEW_DOCUMENT))
	    name = "View Document";
	else if (eventCode.equals(VIEW_PROPERTIES))
	    name = "View Properties";
	else if (eventCode.equals(CHECK_OUT_DOCUMENT))
	    name = "Check Out";
	else if (eventCode.equals(CHECK_IN_DOCUMENT))
	    name = "Check In";
	else if (eventCode.equals(UNDO_CHECK_OUT_DOCUMENT))
	    name = "Undo Check Out";
	else if (eventCode.equals(CREATE_CONTAINER))
	    name = "Creae Container";
	else if (eventCode.equals(REMOVE_DOCUMENT))
	    name = "Remove Document";
	else if (eventCode.equals(UNDO_REMOVE_DOCUMENT))
	    name = "Undo Remove Document";
	else if (eventCode.equals(REMOVE_CONTAINER))
	    name = "Remove Container";
	else if (eventCode.equals(UNDO_REMOVE_CONTAINER))
	    name = "Undo Remove Container";
	else if (eventCode.equals(MOVE_OBJECT))
	    name = "Move Object";

	return name;
    }

} // end class EventCodes
