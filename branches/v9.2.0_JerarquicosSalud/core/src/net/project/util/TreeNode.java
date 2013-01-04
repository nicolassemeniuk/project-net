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

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.util;

import java.util.Iterator;

/**
 * Tree node provides a common interface for a node in a tree structure.  This 
 * structure is provided to allow hierarchical and ordered arrangement of data.
 *
 * @author Matthew Flower (11/09/2001)
 * @since Gecko
 */
public interface TreeNode
{

    /**
     * Returns the value for this node.
     * @return the value
     */
    public Object getValue();

    /**
     * Returns an iterator that contains all of the child nodes underneath this
     * node.  
     *
     * @return an <code>Iterator</code> containing 0 or more TreeNodes
     */
    public Iterator children();

    /**
     * Get the TreeNode above this node in the hierarchy of TreeNodes.  This
     * data structure only supports a single parent.
     *
     * @return a <code>TreeNode</code> value that is this node's parent, or
     * null if there is no parent.
     */
    public TreeNode getParent();

    /**
     * Returns whether or not this TreeNode has any children. 
     *
     * @return true if there are no children, false if there are children.
     */
    public boolean isLeaf();

    /**
     * Add a new child to this list of children for this tree node.  This
     * tree has no default implementation, so at this level, there is no
     * assumption that the tree will be balanced, nor is there any enforcement
     * of structure (such as max of 2 children.)
     *
     * @param newChild a <code>TreeNode</code> value that is to become this
     * class's child.
     */
    public void addChild(TreeNode newChild);
}
