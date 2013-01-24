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

 package net.project.gui.toolbar;

import java.io.Serializable;

import net.project.persistence.IXMLPersistence;

public class ButtonType implements Serializable, IXMLPersistence {

    /*
        All possible button types
     */
    public static final ButtonType CUSTOM   = new ButtonType("custom");

    public static final ButtonType BACK     = new ButtonType("back");
    public static final ButtonType RESET    = new ButtonType("reset");
    public static final ButtonType JUMP     = new ButtonType("jump");
    public static final ButtonType SUBMIT   = new ButtonType("submit");
    public static final ButtonType ACCEPT   = new ButtonType("accept");
    public static final ButtonType NEW_POST = new ButtonType("new_post");
    public static final ButtonType ADD      = new ButtonType("add");
    public static final ButtonType DECLINE  = new ButtonType("decline");
    public static final ButtonType CANCEL   = new ButtonType("cancel");
    public static final ButtonType NEXT     = new ButtonType("next");
    public static final ButtonType FINISH   = new ButtonType("finish");
    public static final ButtonType SEARCH   = new ButtonType("search");
    public static final ButtonType INFO     = new ButtonType("info");
    public static final ButtonType UP       = new ButtonType("up");
    public static final ButtonType DOWN     = new ButtonType("down");
    public static final ButtonType DELETE   = new ButtonType("delete");
    public static final ButtonType UPDATE   = new ButtonType("update");

    public static final ButtonType LIST_DELETED	= new ButtonType("list_deleted");
    public static final ButtonType UNDO_DELETE = new ButtonType("undo_delete");
    public static final ButtonType CREATE	= new ButtonType("create");
    public static final ButtonType MODIFY	= new ButtonType("modify");
    public static final ButtonType REMOVE	= new ButtonType("remove");
    public static final ButtonType REFRESH	= new ButtonType("refresh");
    public static final ButtonType PROPERTIES   = new ButtonType("properties");
    public static final ButtonType LINK	        = new ButtonType("link");
    public static final ButtonType WORKFLOW = new ButtonType("workflow");
    public static final ButtonType CAPTURE_WORK = new ButtonType("capture_work");
    public static final ButtonType NOTIFY	= new ButtonType("notify");
    public static final ButtonType HELP	        = new ButtonType("help");
    public static final ButtonType ADD_EXTERNAL   = new ButtonType("add_external");
    public static final ButtonType SHARE = new ButtonType("share");
    public static final ButtonType SECURITY	= new ButtonType("security");
    public static final ButtonType EXPORT_PDF   = new ButtonType("export_pdf");

    public static final ButtonType CHECK_OUT         = new ButtonType("check_out");
    public static final ButtonType CHECK_IN          = new ButtonType("check_in");
    public static final ButtonType VIEW	             = new ButtonType("view");
    public static final ButtonType UNDO_CHECK_OUT    = new ButtonType("undo_check_out");
    public static final ButtonType CREATE_NEW_FOLDER = new ButtonType("new_folder");
    public static final ButtonType MOVE              = new ButtonType("move");

    public static final ButtonType REPLY             = new ButtonType("reply");
    public static final ButtonType INFO_LINKS        = new ButtonType("info_links");
    public static final ButtonType PREVIOUS_POST     = new ButtonType("previous_post");
    public static final ButtonType NEXT_POST         = new ButtonType("next_post");

    public static final ButtonType COPY	    = new ButtonType("copy");
    public static final ButtonType ALPHABETIZE = new ButtonType("alphabetize");
    public static final ButtonType DISCUSS = new ButtonType("discuss");
    public static final ButtonType DOCUMENTS = new ButtonType("documents");
    public static final ButtonType EXPAND_ALL = new ButtonType("expand_all");
    public static final ButtonType COLLAPSE_ALL = new ButtonType("collapse_all");

    public static final ButtonType LINK_TASKS = new ButtonType("link_tasks");
    public static final ButtonType UNLINK_TASKS = new ButtonType("unlink_tasks");
    public static final ButtonType RECALCULATE = new ButtonType("recalculate");
    public static final ButtonType TASK_UP = new ButtonType("task_up");
    public static final ButtonType TASK_DOWN = new ButtonType("task_down");
    public static final ButtonType TASK_LEFT = new ButtonType("task_left");
    public static final ButtonType TASK_RIGHT = new ButtonType("task_right");
    public static final ButtonType PERCENTAGE = new ButtonType("percentage");
    public static final ButtonType CHOOSE_PHASE = new ButtonType("phase");
    public static final ButtonType RESOURCES = new ButtonType("resources");
    public static final ButtonType CALENDAR = new ButtonType("calendar");
    
    // Button Type for Blog it image
    public static final ButtonType BLOGIT = new ButtonType("blogit");

    // Button Type for import/export image
    public static final ButtonType IMPORT = new ButtonType("import");
    public static final ButtonType EXPORT = new ButtonType("export");
    public static final ButtonType EXPORT_CUSTOM = new ButtonType("export_tasks");
    public static final ButtonType WIKI = new ButtonType("wiki");
    public static final ButtonType BULKINVITATION = new ButtonType("bulkinvitation");

    public static final ButtonType PERSONALIZE_PAGE = new ButtonType("personalize_page");
    public static final ButtonType EXPORT_CSV = new ButtonType("export_csv");
    public static final ButtonType EXPORT_EXCEL = new ButtonType("export_excel");
    public static final ButtonType SAVE_CURRENT_SETTINGS = new ButtonType("save_current_settings");
    public static final ButtonType DELETE_SAVED_VIEWS = new ButtonType("delete_saved_views");

    /** type name */
    private final String name;

    private ButtonType(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
    
    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ButtonType
            && this.name.equals( ((ButtonType) obj).name)) {
            return true;
        }
        return false;
    }
    
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<button_type>");
        xml.append("<name>" + this.name + "</name>");
        xml.append("</button_type>");
        return xml.toString();
    }

}
