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
+----------------------------------------------------------------------*/
package net.project.portfolio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.project.util.TreeNode;

/**
 * Provides a tree structure backed by HashMap.
 * Nodes are indexed to allow insertion of Node / Parent Node relationships
 * in any order.
 * Each item in the tree has a key and a value.
 */
public class HashTree {

    /**
     * The index into the tree.
     * Maintains nodes by keys for easy lookup and rearranging.
     */
    private Map index = new HashMap();

    /**
     * The root of the entire tree.
     * Has no value itself.
     */
    private HashNode rootNode = new HashNode();

    /**
     * Adds an object without a parent.
     * If the value is already in the tree (determined by the specified key),
     * its parent is not changed.  If the value is not in the tree, it
     * becomes a root value.
     * @param key the key for looking up the value
     * @param value the value to add
     * @return the added node
     */
    public TreeNode add(Object key, Object value) {
        // Fetch or create a node
        HashNode node = fetchNode(key, value);

        // If the node is new, add it to the root node
        // If it already has a parent, leave it
        if (node.getParent() == null) {
            this.rootNode.addChild(node);
        }

        return node;
    }

    /**
     * Adds a value with the specified parent.
     * Regardless of whether the value is already in the tree, its parent
     * becomes the specified parent value.
     * If the parent value is not yet in the tree, it is added as a root value;
     * if the parent value is already in the tree, its parent is not changed.
     * @param key the key for looking up the value
     * @param value the value to add
     * @param parentKey the key for looking up the parent value
     * @param parentValue the parent value to add and to become the parent
     * of the specified value
     * @return the added node
     */
    public TreeNode add(Object key, Object value, Object parentKey, Object parentValue) {

        // Fetch or create parent node
        HashNode parentNode = fetchNode(parentKey, parentValue);
        if (parentNode.getParent() == null) {
            this.rootNode.addChild(parentNode);
        }

        // Fetch or create child node
        HashNode node = fetchNode(key, value);

        // Add child node to parent node
        parentNode.addChild(node);

        return node;
    }

    /**
     * Fetches or creates a node for the specified key and value.
     * @param key the key for looking up the value
     * @param value the value to add
     * @return the existing node, or a new node
     */
    private HashNode fetchNode(Object key, Object value) {

        HashNode node = (HashNode) this.index.get(key);
        if (node == null) {
            // We do not have a node for this object
            node = new HashNode(key, value);
            this.index.put(key, node);
        }

        return node;
    }

    /**
     * Returns an iterator over the root nodes.
     * @return an iterator where each element is a <code>TreeNode</code>
     */
    public Iterator getRoots() {
        return this.rootNode.children();
    }

    /**
     * Sorts this tree based on the specified comparator.
     * Elements are sorted by depth.  That is, all root elements are sorted.
     * A child elements of the same parent are sorted.
     * @param c the comparator to use for sorting; the comparator should be
     * based on comparing <code>TreeNode</code> objects.
     * @see java.util.Collections#sort
     */
    public void sort(Comparator c) {
        traverseSort(this.rootNode, c);
    }

    private void traverseSort(HashNode node, Comparator c) {
        // Sort children
        node.sortChildren(c);
        traverseSort(node.children(), c);
    }

    private void traverseSort(Iterator it, Comparator c) {
        while (it.hasNext()) {
            traverseSort((HashNode) it.next(), c);
        }
    }

    /**
     * A Tree HashNode.
     * This maintains the key and value.
     * Child Nodes are stored in the order that they are added.
     */
    private static class HashNode implements net.project.util.TreeNode {

        /**
         * A key for identifying this node.
         */
        private Object key = null;

        /**
         * The value at this node.
         */
        private Object value = null;

        /**
         * The child nodes of this node.
         * A LinkedHashMap is used to perserve ordering.
         */
        private Map childNodes = new LinkedHashMap();

        /**
         * The parent node of this node.
         */
        private TreeNode parentNode = null;

        /**
         * The current depth of this node.
         */
        private int depth = 0;

        /**
         * Creates an empty node.
         */
        private HashNode() {
            // Do nothing
        }

        /**
         * Creates a node with the specified value.
         * @param key the key for this node, used for maintaining the node
         * in a list of child nodes.
         * @param value the value of the node
         */
        private HashNode(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns this node's key.
         * @return the key
         */
        private Object getKey() {
            return this.key;
        }

        /**
         * Returns this node's depth.
         * @return the depth
         */
        private int getDepth() {
            return this.depth;
        }

        /**
         * Sets the parent of this node to be the specified node.
         * Also updates this node's depth to be one more than its parent's
         * depth.
         * @param parentNode the parent node
         */
        private void setParent(HashNode parentNode) {
            this.parentNode = parentNode;
            this.depth = parentNode.getDepth() + 1;
        }

        /**
         * Removes the specified child node from this node.
         * @param childNode the node to remove from this node
         */
        private void removeChild(HashNode childNode) {
            this.childNodes.remove(childNode.getKey());
        }

        /**
         * Sorts the child nodes based on the specified comparator.
         * @param c the comparator to use for sorting
         * @see Collections#sort
         */
        private void sortChildren(Comparator c) {

            // Sort the children
            List children = new ArrayList(this.childNodes.size());
            children.addAll(this.childNodes.values());
            Collections.sort(children, c);

            // Rebuild the map
            Map sortedChildren = new LinkedHashMap(children.size());
            for (Iterator it = children.iterator(); it.hasNext(); ) {
                HashNode nextNode = (HashNode) it.next();
                sortedChildren.put(nextNode.getKey(), nextNode);
            }

            this.childNodes = sortedChildren;
        }


        /**
         * Returns the value for this node.
         * @return the value.
         */
        public Object getValue() {
            return this.value;
        }

        /**
         * Returns an iterator over the child nodes.
         * @return an iterator where each element is a <code>TreeNode</code>
         */
        public Iterator children() {
            return this.childNodes.values().iterator();
        }

        /**
         * Returns the parent node of this node.
         * @return the parent node
         */
        public TreeNode getParent() {
            return this.parentNode;
        }

        /**
         * Indicates whether this node is a leaf.
         * A leaf is defined as a node with no children.
         * @return true if this is a leaf node, false otherwise
         */
        public boolean isLeaf() {
            return this.childNodes.isEmpty();
        }

        /**
         * Adds a node as a child of this node.
         * This node becomes the parent of the child node. If the specified
         * child node already had a parent, its parent is changed.
         * @param newChild the node which is to become the child of this node.
         */
        public void addChild(TreeNode newChild) {

            HashNode childNode = (HashNode) newChild;

            // If we're given the child a new parent, remove from existing
            // parent
            if (childNode.getParent() != null) {
                ((HashNode) childNode.getParent()).removeChild(childNode);
            }

            // Add as a child of this node
            this.childNodes.put(childNode.getKey(), childNode);

            // Set the new parent
            childNode.setParent(this);

        }

    }

}