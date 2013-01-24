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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.discussion.postlist;

import net.project.discussion.Post;

/**
 * Interface for post list maintainers used by a DiscussionGroup object
 *
 * @author AdamKlatzkin
 * @since 01/00
 */
public interface IPostListMaintainer {
    public static final String NBSP = "&nbsp;";

    public static final String COLOR_bgHighlight = "#FFFFCC";
    public static final String COLOR_fgHighlight = "red";

    static final int IMAGE_WIDTH = 16;

    static final int SUBJECT_WIDTH = 135;
    static final int PERSON_WIDTH = 100;
    static final int VIEW_WIDTH = 60;
    static final int DATE_WIDTH = 150;

    static final int SUBJECT_MAX_CHARS = 40;

    /**
     * add a post to the thread list
     *
     * @param post   the post
     */
    public void addPostToList(Post post);

    /**
     * Remove a post from the list.
     *
     * @param postID the primary key of the post we are to remove from the list.
     */
    public void removePostFromList(String postID);

    /**
     * Get a post in the thread list without populating the body
     * @param    postid      the post's id number
     * @return   Post        the post
     */
    public Post getPostHeader(String postid);

    /**
     * get a post that is a member of the list
     *
     * @param postid     the id of the post to retrieve
     */
    public Post getPost(String postid);

    /**
     * Get the first post in the list.
     *
     * @return Post  the first post in the current list.
     */
    public Post getFirstPost();

    /**
     * Get the post following the previously read post.
     *
     * @return Post  the next post
     */
    public Post getNextPost();

    /**
     * Get the post the post before the current post.
     *
     * @return Post  the previous post
     */
    public Post getPreviousPost();

    /**
     * get the id of the current active post
     *
     * @return Post  the current post
     */
    public String getCurrentPostID();

    /**
     * Set the ID of the current active post.
     *
     * @param postID the primary key of the post we want to set as current.
     */
    public void setCurrentPostID(String postID);

    /**
     * get a HTML representation of the post list
     *
     * @return String    the html representation
     */
    public String toHTML();

    /**
     * This method determines which node would be current if the post id param
     * were deleted and selects that post.
     *
     * @param postID a <code>String</code> value which indicates which post id
     * we are going to consider the "deleted" node.
     */
    void selectPostDeleteNode(String postID);

}   // IPostListMainainer
