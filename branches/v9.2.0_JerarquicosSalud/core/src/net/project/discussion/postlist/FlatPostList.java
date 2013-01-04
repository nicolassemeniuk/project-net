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
|   $Revision: 20738 $
|       $Date: 2010-04-22 09:43:07 -0300 (jue, 22 abr 2010) $
|     $Author: uroslates $
|
+-----------------------------------------------------------------------------*/
package net.project.discussion.postlist;

import java.util.ArrayList;
import java.util.Hashtable;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.discussion.DiscussionGroup;
import net.project.discussion.Post;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.NumberFormat;

/**
 * A post maintainer that does not keep a hierarchical relationship between
 * posts.
 *
 * @author AdamKlatzkin
 * @since 02/00
 */
public class FlatPostList implements IPostListMaintainer, java.io.Serializable {
    private User user = null;
    /** The discussion group ID that this post list represents. */
    private String groupID = null;
    /** Post to be shown when there is no "current post". */
    private static Post dummyPost = new Post();
    /** A lookup table to find nodes without having to look through the postList. */
    private Hashtable postNodeLookup = new Hashtable();
    /** A flat list of posts. */
    private ArrayList postList = new ArrayList();
    /** The post marked as "current." */
    private String lastPostRead = null;

    /**
     * Constructor.
     *
     * @param user the User context for this post list
     */
    public FlatPostList(User user, String groupid) {
        this.user = user;
        groupID = groupid;
        dummyPost.setID("-1");
    }

    /**
     * Add a post to the thread list.
     *
     * @param post the post to be added to the list.
     */
    public void addPostToList(Post post) {
        PostNode node = new PostNode();
        node.post = post;
        node.index = postList.size();
        postNodeLookup.put(post.getID(), node);
        postList.add(post);
    }

    /**
     * Remove a post from the list.
     *
     * @param postID the primary key of the post we are to remove from the list.
     */
    public void removePostFromList(String postID) {
        //Find the node we are going to delete
        PostNode node = (PostNode)postNodeLookup.remove(postID);

        //Select the correct node to be selected after the deletion
        selectPostDeleteNode(node);

        //Do the actual deletion.
        postList.remove(node.index);
    }

    /**
     * Select the correct node to be selected after having deleted the node
     * passed as the parameter to this method.
     *
     * @param node a <code>PostNode</code> object which is about to be deleted.
     */
    private void selectPostDeleteNode(PostNode node) {
        if (node != null) {
            //Make no change
        } else if (postList.size() == 1) {
            //There won't be a list after we're done.  Select nothing
            setCurrentPostID(null);
        } else if (node.index < postList.size() - 1) {
            //Node wasn't the last in the list.  Set to the next in the list
            setCurrentPostID(((Post)postList.get(node.index + 1)).getID());
        } else if (node.index == postList.size() - 1) {
            //Was the last in the list.  Set it to the previous item.
            setCurrentPostID(((Post)postList.get(node.index - 1)).getID());
        } else {
            //Shouldn't ever be selected.
            setCurrentPostID(null);
        }
    }

    /**
     * This method determines which node would be current if the post id param
     * were deleted and selects that post.
     *
     * @param postID a <code>String</code> value which indicates which post id
     * we are going to consider the "deleted" node.
     */
    public void selectPostDeleteNode(String postID) {
        PostNode node = (PostNode)postNodeLookup.get(postID);
        selectPostDeleteNode(node);
    }


    /**
     * Get a post in the thread list without populating the body
     * @param postID the post's id number
     * @return Post the post
     */
    public Post getPostHeader(String postID) {
        PostNode node = (PostNode)postNodeLookup.get(postID);
        if (node == null)
            return null;
        else
            return node.post;
    }

    /**
     * get a post that is a member of the list
     *
     * @param postid     the id of the post to retrieve
     */
    public Post getPost(String postid) {
        PostNode node = (PostNode)postNodeLookup.get(postid);

        if (node == null)
            return dummyPost;

        lastPostRead = node.post.getID();
        return node.post;
    }

    /**
     * Get the first post in the list.
     *
     * @return Post  the first post in the current list.
     */
    public Post getFirstPost() {
        if (postList.size() == 0) {
            return dummyPost;
        }

        Post post = (Post)postList.get(0);
        lastPostRead = post.getID();
        return post;
    }

    /**
     * Get the post following the previously read post.
     *
     * @return Post  the next post
     */
    public Post getNextPost() {
        if (lastPostRead == null)
            return getFirstPost();

        int lastIndex = ((PostNode)postNodeLookup.get(lastPostRead)).index;
        if (lastIndex + 1 == postList.size()) {
            return (Post)postList.get(lastIndex);
        } else {
            Post post = (Post)postList.get(lastIndex + 1);
            lastPostRead = post.getID();
            return post;
        }
    }

    /**
     * Get the post the post before the current post.
     *
     * @return Post  the previous post
     */
    public Post getPreviousPost() {
        if (lastPostRead == null)
            return getFirstPost();

        int lastIndex = ((PostNode)postNodeLookup.get(lastPostRead)).index;
        if (lastIndex == 0) {
            return (Post)postList.get(lastIndex);
        } else {
            Post post = (Post)postList.get(lastIndex - 1);
            lastPostRead = post.getID();
            return post;
        }
    }

    /**
     * get the id of the current active post
     *
     * @return Post  the current post
     */
    public String getCurrentPostID() {
        if (lastPostRead == null)
            return dummyPost.getID();
        else
            return lastPostRead;
    }

    /**
     * Set the ID of the current active post.
     *
     * @param postID the primary key of the post we want to set as current.
     */
    public void setCurrentPostID(String postID) {
        if (postNodeLookup.get(postID) != null) {
            lastPostRead = postID;
        } else {
            lastPostRead = null;
        }
    }

    /**
     * Get a HTML representation of the post list.
     *
     * @return String the html representation of the posts.
     */
    public String toHTML() {
        if (postList.size() == 0) {
            return ("<B>" + PropertyProvider.get("prm.discussion.group.noposts.message") + "</b>");
        }
        StringBuffer sb = new StringBuffer();

        sb.append("<TABLE border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=100%>\n");

        //getDummyRow(sb);
        getHeadingRow(sb);
        getListRow(sb);

        sb.append("</TABLE >\n");

        return sb.toString();

    } // method  printPostTable

    /**
     * Append the rows of a linked list to a string buffer.
     *
     * @param sb     the string buffer to append to
     */
    protected void getListRow(StringBuffer sb) {
        if (postList.size() == 0)
            return;

        for (int i = 0; i < postList.size(); i++) {
            printRow(sb, (Post)postList.get(i));
            sb.append("<tr class=\"tableLine\"><td  colspan=\"5\" class=\"tableLine\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/spacers/trans.gif\" width=\"1\" height=\"1\" border=\"0\" alt=\"\"/></td></tr>");

        }
    }

    /**
     * append a dummy row to a string buffer
     *
     * @param sb     the string buffer to append to
     */
    protected void getDummyRow(StringBuffer sb) {
        sb.append("<TR><TD>&nbsp;\n");
        sb.append("</TD></TR>\n");
    }

    /**
     * append a heading row to a string buffer
     *
     * @param sb     the string buffer to append to
     */
    protected void getHeadingRow(StringBuffer sb) {
        sb.append("<TR>\n");
        sb.append("<TD class=\"tableHeader\" width = " + IMAGE_WIDTH + " nowrap>" + NBSP + "</TD>\n");

        sb.append("<TD class=\"tableHeader\" width=100% nowrap><b><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&sort=" + DiscussionGroup.SORT_SUBJECT + "\">" + PropertyProvider.get("prm.discussion.group.subject.label") + "</a></b></TD>\n");
        sb.append("<TD class=\"tableHeader\" width = " + PERSON_WIDTH + " nowrap><b><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&sort=" + DiscussionGroup.SORT_NAME + "\">" + PropertyProvider.get("prm.discussion.group.from.label") + "</a></b></TD>\n");
        sb.append("<TD class=\"tableHeader\" width = " + VIEW_WIDTH + " nowrap><b>" + PropertyProvider.get("prm.discussion.group.views.label") + "</b></TD>\n");
        sb.append("<TD class=\"tableHeader\" width = " + DATE_WIDTH + " nowrap><b><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&sort=" + DiscussionGroup.SORT_DATE + "\">" + PropertyProvider.get("prm.discussion.group.date.label") + "</a></b></TD>\n");
        sb.append("</TR>\n");
    }

    /**
     * append a dummy row containing info about a post to a string buffer
     *
     * @param sb     the string buffer to append to
     * @param post   the post
     */
    protected void printRow(StringBuffer sb, Post post) {
        printIndentationRow(sb, post);

        printOutBeginningOfHREFTag(sb, post);
        printOutEndOfHREFTag(sb, post);

        sb.append("<TD class=\"tableContentNoBGC\" width=" + PERSON_WIDTH + " nowrap>");
        //Add hidden fields for "selectedid" and "postidreplies".  This will let the
        //notify and remove buttons work on the flat view.
        if (post.getID().equals(getCurrentPostID())) {
            sb.append("<input type=\"hidden\" name=\"selectedID\" value=\"" + post.getID() + "\">");
            sb.append("<input type=\"hidden\" name=\"post").append(post.getID()).append("replies");
            sb.append("\" value=\"").append((post.hasReplies() ? "1" : "0")).append("\">");
        }
        sb.append(post.getAuthorDisplayName());
        sb.append("</TD>");

        sb.append("<TD class=\"tableContentNoBGC\" width=" + VIEW_WIDTH + " nowrap align=middle>");
        sb.append(NumberFormat.getInstance().formatNumber(Long.valueOf(post.getNumberOfViews()).longValue()));
        sb.append("</TD>");

        sb.append("<TD class=\"tableContentNoBGC\" width=" + DATE_WIDTH + " nowrap>");
        sb.append(post.getPostdate());
        sb.append("</TD>");

        sb.append("</TR>\n");
    }

    /**
     * append a row indentation to a string buffer
     *
     * @param sb     the string buffer to append to
     */
    protected void printIndentationRow(StringBuffer sb, Post post) {
        if (post.getID().equals(lastPostRead))
            sb.append("<TR ID=ROW_" + post.getID() + " bgcolor=\"" + COLOR_bgHighlight + "\"" + " class=\"selectable\" data=\"" + post.getID() + "\" " + ">\n");
        else
            sb.append("<TR ID=ROW_" + post.getID() + " class=\"selectable\" data=\"" + post.getID() + "\" " + ">\n");

        // print out the image for this row
        sb.append("<TD WIDTH=" + IMAGE_WIDTH + " nowrap><img src = '" + SessionManager.getJSPRootURL() + post.getImageURL() + " '></td>");
    }

    /**
     * append the beginning of an HREF tag to a string buffer
     *
     * @param sb     the string buffer to append to
     * @param post   the post
     */
    protected void printOutBeginningOfHREFTag(StringBuffer sb, Post post) {
        sb.append("<TD width=100% nowrap> ");
        // print out the begining <TD> for this column.

        sb.append("<a ID=");
        sb.append(post.getID());
        sb.append(" HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&postid=");
        sb.append(post.getID());

        // the anchor href to the post has been printed
        // note the spaces in this string at the begining and end
        // are very important.
        sb.append("\" target=\"_self\" title=\"" + post.getSubject() + "\"");
        // the style section of the AHREF tag has been printed with
        // the user's choice of color

        // this is where all font properties will go also.

        sb.append(">");
        // prints the  " > part of the <a href="blah  " >

        if (post.getID().equals(lastPostRead))
            sb.append("<FONT color=" + COLOR_fgHighlight + " class=\"tableContentFontOnly\">\n");
        else
            sb.append("<FONT color=" + post.getLinkColor() + " class=\"tableContentFontOnly\">\n");
    }

    /**
     * append the end of an HREF tag to a string buffer
     *
     * @param sb     the string buffer to append to
     * @param post   the post
     */
    protected void printOutEndOfHREFTag(StringBuffer sb, Post post) {
        // this case finishes printing out the <ahref... tag and the <td></td> tags up to:  someString </a></td>
        if (post.getSubject() != null) {
            String subject = post.getSubject();
            if (subject.length() > SUBJECT_MAX_CHARS) {
                subject = subject.substring(0, SUBJECT_MAX_CHARS - 4) + "...";
            }
            sb.append(subject);
        }

        sb.append("</a>");
        // prints out the </a> part of the anchor tag

        sb.append("</TD>\n");
    }

    /**
     * A node utilized by the ThreadedPostList to hold a post within the tree.
     *
     * @author AdamKlatzkin
     * @since 02/00
     */
    private class PostNode implements java.io.Serializable {
        public Post post = null;
        public int index = 0;
    }

} // FlatPostList

