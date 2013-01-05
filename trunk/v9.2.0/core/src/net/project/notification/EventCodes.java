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

 package net.project.notification;

/**
 * This class contains the codes for Event
 *
 * @author deepak
 */
public class EventCodes {

    /**
     * Constant for new news items created
     */
    public static final String CREATE_NEWS = "news_create";
    /**
     * Constant for news items modified
     */
    public static final String MODIFY_NEWS = "news_modify";
    /**
     * Constant for news items deleted
     */
    public static final String REMOVE_NEWS = "news_remove";

    /**
     * Constant for new form created
     */
    public static final String CREATE_FORM = "form_create";
    /**
     * Constant for form being modified
     */
    public static final String MODIFY_FORM = "form_modify";
    /**
     * Constant for form being deleted
     */
    public static final String REMOVE_FORM = "form_remove";
    /**
     * Constant for new form data created
     */
    public static final String CREATE_FORM_DATA = "form_data_create";
    /**
     * Constant for form data being modified
     */
    public static final String MODIFY_FORM_DATA = "form_data_modify";
    /**
     * Constant for form data being deleted
     */
    public static final String REMOVE_FORM_DATA = "form_data_remove";

    /**
     * Constant for new discussion group being created
     */
    public static final String CREATE_DISCUSSION_GROUP = "discussion_group_create";
    /**
     * Constant for new discussion group being modified
     */
    public static final String MODIFY_DISCUSSION_GROUP = "discussion_group_modify";
    /**
     * Constant for new discussion group being deleted
     */
    public static final String REMOVE_DISCUSSION_GROUP = "discussion_group_remove";
    /**
     * Constant for removing a post from a discussion.
     */
    public static final String REMOVE_DISCUSSION_POST = "discussion_remove_post";
    /**
     * Constant for creating a post in a discussion.
     */
    public static final String CREATE_DISCUSSION_POST = "discussion_create_post";
    /**
     * Constant for new post being created
     */
    public static final String CREATE_POST = "post_create";
    /**
     * Constant for new post being modified
     */
    private static final String MODIFY_POST = "post_modify";
    /**
     * Constant for post being deleted
     */
    public static final String REMOVE_POST = "post_remove";
    /**
     * Constant for new reply being posted
     */
    public static final String CREATE_REPLY = "reply_create";

    /**
     * Constant for new task being created
     */
    public static final String CREATE_TASK = "task_create";
    /**
     * Constant for  existing task being modified
     */
    public static final String MODIFY_TASK = "task_modify";
    /**
     * Constant for  existing task being deleted
     */
    public static final String REMOVE_TASK = "task_remove";
    /**
     * Constant for status for existing task being modified
     */
    public static final String CHANGE_TASK_STATUS = "task_status_change";
    
    /**
     * Constant for new blog entry created
     */
    public static final String CREATE_BLOG_ENTRY = "blog_entry_create";
    
    /**
     * Constant for existing blog entry being modified
     */
    public static final String MODIFY_BLOG_ENTRY = "blog_entry_modified";
    
    /**
     * Constant for existing blog entry being deleted
     */
    public static final String DELETE_BLOG_ENTRY = "blog_entry_deleted";
    
    /**
     * Constant for new comment added on a blog entry
     */
    public static final String CREATE_BLOG_COMMENT = "blog_comment_added";
    
    /**
     * Constant for new wiki page created
     */
    public static final String CREATE_WIKI_PAGE = "wiki_page_create";
    
    /**
     * Constant for exisitng wiki page being modified
     */
    public static final String MODIFY_WIKI_PAGE = "wiki_page_modify";
    
    /**
     * Constant for exisitng wiki page being deleted
     */
    public static final String DELETE_WIKI_PAGE = "wiki_page_deleted";

    /**
     * Constant for exisitng wiki image uploaded
     */
    public static final String WIKI_UPLOAD_IMAGE = "wiki_upload_image";
    

    /**
     * Constant for existing project edited
     */
    public static final String MODIFY_PROJECT = "project_modified";

    /**
     * Constant for existing project deleted
     */
    public static final String REMOVE_PROJECT = "project_deleted";
    
    /**
     * Constant for project participant invited
     */
    public static final String INVITE_PROJECT_PARTICIPANT = "participant_invited";

    /**
     * Constant for project participant edited
     */
    public static final String MODIFY_PROJECT_PARTICIPANT = "participant_modified";
    
    /**
     * Constant for project participant deleted
     */
    public static final String REMOVE_PROJECT_PARTICIPANT = "participant_delete";

    /**
     * Returns the name of the Event based on the parameter or constant being passed
     *
     * @param eventCode
     * @return the name of the Event based on the parameter or constant being passed
     */
    public static String getName(String eventCode) {

        String name = null;

        if (eventCode.equals(CREATE_NEWS))
            name = "Create News Item";
        else if (eventCode.equals(MODIFY_NEWS))
            name = "Modify News";
        else if (eventCode.equals(REMOVE_NEWS))
            name = "Remove News";
        else if (eventCode.equals(CREATE_FORM))
            name = "Create Form";
        else if (eventCode.equals(MODIFY_FORM))
            name = "Modify Form";
        else if (eventCode.equals(REMOVE_FORM))
            name = "Remove Form";
        else if (eventCode.equals(CREATE_FORM_DATA))
            name = "Create Form Data";
        else if (eventCode.equals(MODIFY_FORM_DATA))
            name = "Modify Form Data";
        else if (eventCode.equals(REMOVE_FORM_DATA))
            name = "Remove Form Data";
        else if (eventCode.equals(CREATE_DISCUSSION_GROUP))
            name = "Create Discussion Group";
        else if (eventCode.equals(MODIFY_DISCUSSION_GROUP))
            name = "Modify Discussion Group";
        else if (eventCode.equals(REMOVE_DISCUSSION_GROUP))
            name = "Remove Discussion Group";
        else if (eventCode.equals(REMOVE_DISCUSSION_POST))
            name = "Remove Post from Discussion";
        else if (eventCode.equals(CREATE_DISCUSSION_POST))
            name = "Create Post in Discussion";
        else if (eventCode.equals(CREATE_POST))
            name = "Create Post";
        else if (eventCode.equals(MODIFY_POST))
            name = "Modify Post";
        else if (eventCode.equals(REMOVE_POST))
            name = "Remove Post";
        else if (eventCode.equals(CREATE_REPLY))
            name = "Reply Posted";
        else if (eventCode.equals(CREATE_TASK))
            name = "Create Task";
        else if (eventCode.equals(MODIFY_TASK))
            name = "Modify Task";
        else if (eventCode.equals(REMOVE_TASK))
            name = "Remove Task";
        else if (eventCode.equals(CHANGE_TASK_STATUS))
            name = "Change Task Status";
        else if (eventCode.equals(CREATE_BLOG_ENTRY))
            name = "Create Blog Entry";
        else if (eventCode.equals(MODIFY_BLOG_ENTRY))
            name = "Modify Blog Entry";
        else if (eventCode.equals(DELETE_BLOG_ENTRY))
            name = "Delete Blog Entry";
        else if (eventCode.equals(CREATE_BLOG_COMMENT))
            name = "Blog Entry Comment";
        else if (eventCode.equals(CREATE_WIKI_PAGE))
            name = "Create Wiki Page";
        else if (eventCode.equals(MODIFY_WIKI_PAGE))
            name = "Modify Wiki Page";
        else if (eventCode.equals(DELETE_WIKI_PAGE))
            name = "Remove Wiki Page";
        else if (eventCode.equals(WIKI_UPLOAD_IMAGE))
            name = "Upload Wiki Page Image ";
        else if (eventCode.equals(MODIFY_PROJECT))
            name = "Project Edited";
        else if (eventCode.equals(REMOVE_PROJECT))
            name = "Project Deleted";
        else if (eventCode.equals(INVITE_PROJECT_PARTICIPANT))
            name = "Project Participant Invited";
        else if (eventCode.equals(MODIFY_PROJECT_PARTICIPANT))
            name = "Project Participant Edited";
        else if (eventCode.equals(REMOVE_PROJECT_PARTICIPANT))
            name = "Project Participant Deleted";

        return name;
    }

} // end class EventCodes
