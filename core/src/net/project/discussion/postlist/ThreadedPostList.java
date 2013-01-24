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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.discussion.postlist;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.discussion.DiscussionGroup;
import net.project.discussion.Post;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.security.User;

/**
 * Maintains a threaded list of the posts within a discussion group.
 * Tracks the users position within the list and provides a navigation API.
 *
 * @author AdamKlatzkin
 * @since 01/00
 */
public class ThreadedPostList implements IPostListMaintainer, java.io.Serializable {
    static final int NAMED_COLUMNS_IN_TABLE = 3;
    static final int NAMED_COLUMNS_OTHER_THAN_SUBJECT = 2;

    private User user = null;
    private String groupID = null;
    private static Post dummyPost = new Post();

    /**
     * A collection of the top level links in the discussion group.  This
     * collection contains zero or more PostNode objects.
     */
    private LinkedList topLevelPostNodes = new LinkedList();
    /**
     * Hashmap which maps the post id to a "post node".  These post nodes are
     * used to create the tree structure of a discussion group.  This hashtable
     * allows quick and easy lookup of nodes.
     */
    private Hashtable postNodeLookup = new Hashtable();
    private String lastReadPostID = null;
    private int maxDepth = 0;

    /**
     * Constructor
     *
     * @param user   the User context for this post list
     */
    public ThreadedPostList(User user, String groupid) {
        this.user = user;
        groupID = groupid;
        dummyPost.setID("-1");
    }

    /**
     * add a post to the thread list
     *
     * @param post   the post
     */
    public void addPostToList(Post post) {
        // add the post to the thread list
        PostNode newPostNode = new PostNode();
        newPostNode.children = new LinkedList();
        newPostNode.post = post;

        PostNode sibling = null;
        LinkedList siblingList = null;

        //See if we need to remove the post from the linked list of siblings of
        //this post's parent.
        if (post.getParentPostID() == null) {
            //Make all of the top level tasks the siblings of this post
            siblingList = topLevelPostNodes;
            newPostNode.depth = 0;
        } else {
            //The post has a parent, we need to find the siblings
            PostNode parent = (PostNode)postNodeLookup.get(post.getParentPostID());
            siblingList = parent.children;
            newPostNode.parent = parent;
            newPostNode.depth = parent.depth + 1;
            if (newPostNode.depth > maxDepth)
                maxDepth = newPostNode.depth;
        }

        //There are other siblings, link this new post to the last sibling
        if (siblingList.size() > 0) {
            sibling = (PostNode)(siblingList.getLast());
            newPostNode.prevSibling = sibling;
            sibling.nextSibling = newPostNode;
        }

        siblingList.add(newPostNode);
        postNodeLookup.put(post.getID(), newPostNode);
    }

    /**
     * Remove a post from the list.
     *
     * @param postID the primary key of the post we are to remove from the list.
     */
    public void removePostFromList(String postID) {
        //Remove the post from the hash table and linked list
        PostNode removedPostNode = (PostNode)postNodeLookup.remove(postID);
        topLevelPostNodes.remove(removedPostNode);

        //Link this removedPostNode's children to this nodes parent, or set them to have
        //no parent if this removedPostNode doesn't have one.
        String parentPostID = removedPostNode.post.getParentPostID();
        if (parentPostID != null) {
            PostNode parentPostNode = (PostNode)postNodeLookup.get(removedPostNode.post.getParentPostID());

            //Remove the child from its parents list of children
            parentPostNode.children.remove(removedPostNode);

            List children = removedPostNode.children;
            for (Iterator it = children.iterator(); it.hasNext();) {
                PostNode child = (PostNode)it.next();
                //Relink the post to the deleted post's parent.
                child.parent = parentPostNode;

                //It probably isn't really good practice to go around changing
                //the fields of post like this, but if we are going to do local
                //caching, there isn't much choice without implementing
                //something fairly complex.  Seems how it is just one field,
                //we'll just set it.  If we were using EJB, we wouldn't have
                //this problem.  (Although, undoubtedly we'd have another dozen
                //problems to replace it.)
                child.post.setParentPostID(parentPostID);

                //Put the post in the parent child list
                parentPostNode.children.add(child);
                child.nextSibling = null;
                try {
                    child.prevSibling = (PostNode)parentPostNode.children.getLast();
                } catch (NoSuchElementException e) {
                    child.prevSibling = null;
                }
            }

        } else {
            //If a post doesn't have a parent, it means that it is at the top level.
            List children = removedPostNode.children;
            PostNode prevNode = removedPostNode.prevSibling;
            for (Iterator it = children.iterator(); it.hasNext();) {
                PostNode child = (PostNode)it.next();
                //Reset the node's parent
                child.parent = null;
                child.post.setParentPostID(null);

                //Add the child to the list of top level nodes
                topLevelPostNodes.add(child);

                //Fix the previous and next siblings
                child.prevSibling = prevNode;

                if (prevNode != null) {
                    prevNode.nextSibling = child;
                }

                //If this is the last child, link it to the deleted node's next sibling
                if (!it.hasNext()) {
                    child.nextSibling = removedPostNode.nextSibling;
                }
            }
        }

        //Reduce the depth of all children (no matter how distantly related.)
        for (Iterator it = removedPostNode.children.iterator(); it.hasNext();) {
            PostNode child = (PostNode)it.next();
            reduceDepthOfNodeAndChildren(child);
        }

        //Unlink this post from its siblings.
        if (removedPostNode.nextSibling != null) {
            removedPostNode.nextSibling.prevSibling = removedPostNode.prevSibling;
        }
        if (removedPostNode.prevSibling != null) {
            removedPostNode.prevSibling.nextSibling = removedPostNode.nextSibling;
        }

        selectPostDeleteNode(removedPostNode);
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
        if (node != null) {
            selectPostDeleteNode(node);
        }
    }

    private void selectPostDeleteNode(PostNode removedPostNode) {
        //Find the correct post to be the new "current" post
        if (removedPostNode.nextSibling != null) {
            setCurrentPostID(removedPostNode.nextSibling.post.getID());
        } else if (removedPostNode.prevSibling != null) {
            setCurrentPostID(removedPostNode.prevSibling.post.getID());
        } else if (removedPostNode.parent != null) {
            setCurrentPostID(removedPostNode.parent.post.getID());
        } else {
            setCurrentPostID(null);
        }
    }

    /**
     * Reduce the depth of a currentNode and all of its children.
     *
     * @param currentNode the node for which you are going to reduce
     * depth.
     */
    private void reduceDepthOfNodeAndChildren(PostNode currentNode) {
        currentNode.depth = Math.max(currentNode.depth-1, 0);
        List children = currentNode.children;

        for (Iterator it = children.iterator(); it.hasNext();) {
          PostNode child = (PostNode)it.next();
          reduceDepthOfNodeAndChildren(child);
        }
    }

    /**
     * Invert the UI state (collapsed/expanded) of a post maintained by this
     * list.
     *
     * @param postID the id of the post for which we are going to invert the
     * state.
     */
    public void invertThreadViewState(String postID) {
        //Find the post that we are going to invert.
        PostNode post = (PostNode)postNodeLookup.get(postID);

        //As long as we found a post, invert it.
        if (post != null) {
            //Invert the expanded state
            post.isExpanded = !post.isExpanded;
        }
    }

    /**
     * Get a post in the thread list without populating the body
     *
     * @param postID the post's id number
     * @return a <code>Post</code> object.
     */
    public Post getPostHeader(String postID) {
        //Find the post in the lookup list.
        PostNode post = (PostNode)postNodeLookup.get(postID);

        Post postToReturn = null;
        if (post != null) {
            postToReturn = post.post;
        }

        return postToReturn;
    }

    /**
     * Get a post that is a member of the list.
     *
     * @param postID the id of the post to retrieve
     */
    public Post getPost(String postID) {
        PostNode node = (PostNode)postNodeLookup.get(postID);

        if (node == null)
            return dummyPost;

        Post post = node.post;
        lastReadPostID = post.getID();
        expandToNode(node);
        return post;
    }

    /**
     * Get the first post in the list.
     *
     * @return Post  the first post in the current list.
     */
    public Post getFirstPost() {
        if (topLevelPostNodes.size() == 0) {
            return dummyPost;
        }

        Post post = ((PostNode)topLevelPostNodes.getFirst()).post;
        lastReadPostID = post.getID();
        return post;
    }

    /**
     * Get the post following the previously read post.
     *
     * @return Post  the next post
     */
    public Post getNextPost() {
        if (lastReadPostID == null) {
            return getFirstPost();
        }
        PostNode node = (PostNode)postNodeLookup.get(lastReadPostID);
        PostNode nextNode = node;
        // does this post have any children
        if (node.children.size() > 0) {
            nextNode = (PostNode)node.children.getFirst();
        }
        // does the node have a sibling
        else if (node.nextSibling != null) {
            nextNode = node.nextSibling;
        }
        // does the nodes parent have a sibling
        else {
            PostNode tempNode = node;
            do {
                tempNode = tempNode.parent;
                if ((tempNode != null) && (tempNode.nextSibling != null)) {
                    nextNode = tempNode.nextSibling;
                    break;
                }
            } while (tempNode != null);
        }

        lastReadPostID = nextNode.post.getID();
        expandToNode(nextNode);

        return nextNode.post;
    }

    /**
     * Get the post before the current post.
     *
     * @return Post  the previous post
     */
    public Post getPreviousPost() {
        PostNode prevNode;
        if (lastReadPostID == null) {
            return getFirstPost();
        }
        PostNode node = (PostNode)postNodeLookup.get(lastReadPostID);
        // does this post have a previous sibling
        if (node.prevSibling != null) {
            // go to the end of the previous siblings thread
            PostNode tempNode = node.prevSibling;
            while (tempNode.children.size() > 0) {
                if (tempNode.children.size() > 0) {
                    tempNode = (PostNode)(tempNode.children.getLast());
                }
            }
            prevNode = tempNode;
        }
        // does the node have a parent
        else if (node.parent != null) {
            prevNode = node.parent;
        }
        // this is the last thread
        else {
            prevNode = node;
        }
        lastReadPostID = prevNode.post.getID();
        expandToNode(prevNode);

        return prevNode.post;
    }

    /**
     * get the id of the current active post
     *
     * @return Post  the current post
     */
    public String getCurrentPostID() {
        if (lastReadPostID == null)
            return dummyPost.getID();
        else
            return lastReadPostID;
    }

    /**
     * Set the ID of the current active post.
     *
     * @param postID the primary key of the post we want to set as current.
     */
    public void setCurrentPostID(String postID) {
        lastReadPostID = postID;
        if (postID != null) {
            expandToNode((PostNode)postNodeLookup.get(postID));
        }
    }

    /**
     * get a HTML representation of the post list
     *
     * @return String the html representation
     */
    public String toHTML() {
        if ((topLevelPostNodes == null) || (topLevelPostNodes.size() == 0)) {
            return ("<B>" + PropertyProvider.get("prm.discussion.group.noposts.message") + "</b>");
        }
        StringBuffer sb = new StringBuffer();
        // prints out the parents (the first post in the thread) and then calls findTheChildren()
        // to print out the children in a depth first style

        sb.append("<TABLE border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">\n");

        //getDummyRow(sb);
        getHeadingRow(sb);
        getListRow(sb, topLevelPostNodes);

        sb.append("</TABLE>\n");

        return sb.toString();

    } // method  printPostTable

    /**
     * append the rows of a linked list to a string buffer
     *
     * @param sb the string buffer to append to
     * @param list the linked list of PostNode objects
     */
    protected void getListRow(StringBuffer sb, LinkedList list) {
        if (list.size() == 0)
            return;

        Object[] nodeArray = list.toArray();

        for (int i = 0; i < nodeArray.length; i++) {
            PostNode node = (PostNode)nodeArray[i];

            printRow(sb, node);
            if (node.isExpanded) {
                getListRow(sb, node.children);
            }

            if (node.parent == null)
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
        sb.append("<TD class=\"tableHeader\" nowrap><b><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&view=" + DiscussionGroup.VIEW_FLAT + "&sort=" + DiscussionGroup.SORT_SUBJECT + "\">" + PropertyProvider.get("prm.discussion.group.subject.label") + "</a></b></TD>\n");
        sb.append("<TD class=\"tableHeader\" width = " + PERSON_WIDTH + " nowrap><b><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&view=" + DiscussionGroup.VIEW_FLAT + "&sort=" + DiscussionGroup.SORT_NAME + "\">" + PropertyProvider.get("prm.discussion.group.from.label") + "</a></b></TD>\n");
        sb.append("<TD class=\"tableHeader\" width = " + VIEW_WIDTH + " nowrap><b>" + PropertyProvider.get("prm.discussion.group.views.label") + "</b></TD>\n");
        sb.append("<TD class=\"tableHeader\"width = " + DATE_WIDTH + " nowrap><b><A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&view=" + DiscussionGroup.VIEW_FLAT + "&sort=" + DiscussionGroup.SORT_DATE + "\">" + PropertyProvider.get("prm.discussion.group.date.label") + "</a></b></TD>\n");
        sb.append("</TR>\n");
    }

    /**
     * append a dummy row containing info about a post to a string buffer
     *
     * @param sb     the string buffer to append to
     * @param node   the post node
     */
    protected void printRow(StringBuffer sb, PostNode node) {

        Post post = node.post;
        if (post.getID().equals(lastReadPostID))
            sb.append("<TR ID=ROW_" + post.getID() + " bgcolor=\"" + COLOR_bgHighlight + "\">\n");
        else
            sb.append("<TR ID=ROW_" + post.getID() + " bgcolor=\"#FFFFFF\">\n");

        sb.append("<TD>\n<TABLE border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=100%><tr>");

        printIndentationRow(sb, node);
        printOutBeginningOfHREFTag(sb, post, node.depth);
        printOutEndOfHREFTag(sb, post);

        sb.append("</tr></TABLE></TD>\n");
        sb.append("<TD class=\"tableContentNoBGC\" width=" + PERSON_WIDTH + " nowrap>");
        sb.append(post.getAuthorDisplayName());
        sb.append("</TD>");

        sb.append("<TD class=\"tableContentNoBGC\" width=" + VIEW_WIDTH + " nowrap align=middle>");
        sb.append(Long.valueOf(post.getNumberOfViews()).longValue());
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
    protected void printIndentationRow(StringBuffer sb, PostNode node) {
        Post post = node.post;

        String imageURL = SessionManager.getJSPRootURL() +post.getImageURL();
        if (node.isExpanded)
            imageURL = SessionManager.getJSPRootURL() +"/images/post_minus.gif";
        else if (node.children.size() > 0)
            imageURL = SessionManager.getJSPRootURL() +"/images/post_plus.gif";

        if (node.depth > 0) {
            for (int i = 0; i < node.depth - 1; i++)
                // print out the indentation for this row
                sb.append("<TD class=\"tableContentNoBGC\" WIDTH=" + IMAGE_WIDTH + " nowrap>" + NBSP + "</td> ");

            if (node.prevSibling == null)
                sb.append("<TD class=\"tableContentNoBGC\"  WIDTH=" + IMAGE_WIDTH + " nowrap><img src='"+ SessionManager.getJSPRootURL() +"/images/ReplyArrow.gif'></td> ");
            else
                sb.append("<TD class=\"tableContentNoBGC\" WIDTH=" + IMAGE_WIDTH + " nowrap>" + NBSP + "</td> ");
        }
        sb.append("<TD class=\"tableHeader\" nowrap><input type=\"radio\" ")
          .append("name=\"selected\" value=\'").append(post.getID())
          .append("'").append((post.getID().equals(getCurrentPostID()) ? " checked" : ""))
          .append(" onclick=\"selectPost(").append(post.getID()).append(")\">");
        // let javascript functions know what whether this post has replies
        sb.append("<input type=\"hidden\" name=\"post").append(post.getID()).append("replies");
        sb.append("\" value=\"").append((post.hasReplies()?"1":"0")).append("\"></TD>\n");
        // print out the image for this row
        sb.append("<TD class=\"tableContentNoBGC\" WIDTH=" + IMAGE_WIDTH + " nowrap>");
        if (node.children.size() > 0)
            sb.append("<A HREF=\"ThreadList.jsp?module=" + Module.DISCUSSION + "&action=" + Action.VIEW + "&id=" + groupID + "&iconId=" + post.getID() + "\">");
        sb.append("<img src = '" + imageURL + "' border=0>");
        if (node.children.size() > 0)
            sb.append("</A>");
        sb.append("</td>");
    }

    /**
     * append the beginning of an HREF tag to a string buffer
     *
     * @param sb     the string buffer to append to
     * @param post   the post
     * @param depth  the depth of the post
     */
    protected void printOutBeginningOfHREFTag(StringBuffer sb, Post post, int depth) {

        sb.append("<TD width=100% nowrap> ");
        // print out the begining <TD> for this column.
        // sb.append("<TD class=\"tableHeader\" nowrap><input type=\"radio\" name=\"selected\" value=\'"+post.m_post_id+"'></TD>\n");
        sb.append("<a NAME=");
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

        if (post.getID().equals(lastReadPostID)) {
            sb.append("<FONT color=" + COLOR_fgHighlight + " class=\"tableContentFontOnly\">\n");
        } else {
            sb.append("<FONT color=" + post.getLinkColor() + " class=\"tableContentFontOnly\">\n");
        }

        // prints the  " > part of the <a href="blah  " >
    }

    /**
     * Append the end of an HREF tag to a string buffer.
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

        sb.append("</FONT>");
        sb.append("</a>");
        // prints out the </a> part of the anchor tag

        sb.append("</TD>\n");
    }

    /**
     * expand the thread UI to the specified node.  This is done
     * by traversing through the nodes ancestors and setting their state to
     * expanded
     *
     * @param node   the node in the list containing the post to expand to
     */
    protected void expandToNode(PostNode node) {
        if ((node != null) && (node.parent != null)) {
            node.parent.isExpanded = true;
            expandToNode(node.parent);
        }
    }

    /**
     * a node utilized by the ThreadedPostList to hold a post within the tree
     *
     * @author AdamKlatzkin
     * @since 01/00
     */
    private class PostNode implements java.io.Serializable {
        /** The post that this post node contains. */
        public Post post = null;
        /** A list of this node's children. */
        public LinkedList children = null;
        /** The depth of the current node. */
        public int depth = 0;
        /** Whether or not the current node is expanded. */
        public boolean isExpanded = false;
        /** The next post at the same depth. */
        public PostNode nextSibling = null;
        /** The previous post at the same depth. */
        public PostNode prevSibling = null;
        /** The parent post of this post. */
        public PostNode parent = null;

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PostNode)) return false;

            final PostNode postNode = (PostNode)o;

            if (!post.equals(postNode.post)) return false;

            return true;
        }

        public int hashCode() {
            return post.hashCode();
        }
    }

} // PostNode
