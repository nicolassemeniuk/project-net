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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.project.persistence.PersistenceException;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.util.TreeNode;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides a Tree View of Spaces from a portfolio.
 * This includes other spaces that are not in the portfolio but that are
 * parents of those spaces, required to build the tree structure.
 * The exact algorithm is as follows:
 * <p>
 * <li>All leaf-nodes in the tree are spaces from the portfolio;
 * that is, no sub-spaces of those projects are included.
 * <li>All ancestor spaces of their membership spaces are included;
 * that is, all parent spaces, and their parents, and their parents etc.
 * <li>No siblings of their membership spaces are included (unless, of course,
 * the user is a member of that also).
 * </p>
 */
abstract class PortfolioTreeView implements IPortfolioView {

    /**
     * The tree structure.
     */
    protected final HashTree tree = new HashTree();

    /**
     * The portfolio from which to construct the tree.
     */
    private Portfolio portfolio = null;

    /**
     * The maximum depth of the tree, required for display purposes.
     */
    private int maxDepth = 0;

    /**
     * Creates a new Tree View for the specified portfolio.
     * The spaces in the portfolio are used as the basis of the tree view.
     * The tree view includes all of those spaces and any additional
     * ancestor spaces necessary to build the tree structure.
     * @param portfolio the portfolio on which to base the tree structure
     * @throws PersistenceException if there is a problem building the
     * tree
     */
    protected PortfolioTreeView(Portfolio portfolio) throws PersistenceException {
        this.portfolio = portfolio;
        constructTree();
    }

    /**
     * Returns the current portfolio on which this tree view is based.
     * @return the portfolio
     */
    protected Portfolio getPortfolio() {
        return this.portfolio;
    }

    /**
     * Returns the XML for this tree view, including the version tag.
     * @return the XML
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML for this tree view, excluding the version tag.
     * @return the XML
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Constructs an XMLDocument for the tree structure.
     * @return the XMLDocument structure.
     */
    private XMLDocument getXMLDocument() {
        XMLDocument doc = new XMLDocument();

        try {
            // Reset the max depth
            this.maxDepth = 0;
            int level = 0;

            // Root node
            doc.startElement("PortfolioTree");

            // Build the XML document
            traverse(level, this.tree.getRoots(), doc);

            // Add the updated max depth
            doc.addElement("MaxDepth", new Integer(this.maxDepth));
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new net.project.base.PnetRuntimeException("Error building XML document: " + e, e){};

        }

        return doc;
    }

    /**
     * Traverse over the <code>TreeNode</code>s for the specified iterator.
     * @param level the level at which the nodes in the iterator occur
     * @param it the iterator where each element is a <code>TreeNode</code>
     * @param doc the XML document to add to
     * @throws net.project.xml.document.XMLDocumentException if there is a problem constructing the XML
     */
    private void traverse(int level, Iterator it, XMLDocument doc) throws XMLDocumentException {
        while (it.hasNext()) {
            traverse(level, (TreeNode) it.next(), doc);
        }
    }

    /**
     * Traverse over the specified TreeNode and its children.
     * @param level the level at which the node is occurring
     * @param node the TreeNode to visit and traverse over children
     * @param doc the XML document to add to
     * @throws net.project.xml.document.XMLDocumentException if there is a problem constructing the XML
     */
    private void traverse(int level, TreeNode node, XMLDocument doc) throws XMLDocumentException {

        // Visit the node
        doc.startElement("Node");

        visit(node, doc);

        // Ensure max depth is increased, if necessary
        this.maxDepth = Math.max(this.maxDepth, level);

        // Traverse over the node's children
        doc.startElement("Children");
        traverse((level + 1), node.children(), doc);
        doc.endElement();

        doc.endElement();



    }

    /**
     * Visit the specified node, updating the XML.
     * @param node the node to visit
     * @param doc the XML document to add to
     * @throws net.project.xml.document.XMLDocumentException if there is a problem constructing the XML
     */
    private void visit(TreeNode node, XMLDocument doc) throws XMLDocumentException {
        TreeEntry entry = (TreeEntry) node.getValue();

        doc.startElement("Value");
        doc.addElement("IsMember", new Boolean(entry.isMember()));
        doc.addXMLString(entry.getSpace().getXMLBody());
        doc.endElement();

    }

    /**
     * Construct the tree structure based on the current portfolio.
     * @throws PersistenceException if there is a problem loading information
     * from the database
     */
    protected void constructTree() throws PersistenceException {

        // Get the ids of the spaces in this portfolio
        // This is used to determine membership of each Space
        Set membershipSpaceIDCollection = new HashSet();
        membershipSpaceIDCollection.addAll(getMembershipSpaceIDCollection());

        if (!membershipSpaceIDCollection.isEmpty()) {

            // Get the parent space relationship information for those
            // portfolio entries
            // This may include space ids that are not in the membership list
            Collection relatedSpaceCollection = getRelatedSpaceCollection(membershipSpaceIDCollection);

            // Build a set of additional space IDs
            // This includes spaces that are parents of the spaces
            // that are in the portfolio
            // Also build a map of spaces and parent spaces
            Set additionalSpaceIDSet = new HashSet();
            Map childParentMap = new HashMap();
            for (Iterator it = relatedSpaceCollection.iterator(); it.hasNext();) {
                SpaceManager.RelatedSpace nextRelatedSpace = (SpaceManager.RelatedSpace) it.next();
                // Add id and parent id to collection of all ids
                additionalSpaceIDSet.add(nextRelatedSpace.getSpaceID());
                additionalSpaceIDSet.add(nextRelatedSpace.getParentSpaceID());
                // Add id and parent id to lookup map
                childParentMap.put(nextRelatedSpace.getSpaceID(), nextRelatedSpace.getParentSpaceID());
            }

            // Remove any ids that the user is already a member of
            // This ensures that we only load additional spaces and we
            // do not reload spaces already in the portfolio
            additionalSpaceIDSet.removeAll(membershipSpaceIDCollection);

            // Build a Map of all spaces based on the ids in the specified set
            // and the spaces in the portfolio
            Map allSpacesMap = loadAllSpacesMap(additionalSpaceIDSet);

            // Actually build the tree
            constructTree(allSpacesMap, membershipSpaceIDCollection, childParentMap);

        }

    }

    /**
     * Returns a collection of space ids from this portfolio.
     * @return the ids of proejcts contained within this portfolio
     */
    protected Collection getMembershipSpaceIDCollection() {
        Set membershipSpaceIDList = new HashSet();

        for (Iterator it = getPortfolio().iterator(); it.hasNext();) {
            membershipSpaceIDList.add(((IPortfolioEntry) it.next()).getID());
        }

        return membershipSpaceIDList;
    }


    /**
     * Returns a collection of spaces related to the spaces with IDs in the
     * specified collection of IDs.
     * @param membershipSpaceIDCollection the IDs for which to get related
     * spaces; the spaces are ancestors of the spaces with the specified IDs
     * @return the collection of <code>{@link SpaceManager.RelatedSpace}</code>s
     * @throws PersistenceException if there is a problem getting the related spaces
     */
    protected abstract Collection getRelatedSpaceCollection(Set membershipSpaceIDCollection) throws PersistenceException;

    /**
     * Loads a map of space ID to space based, aggregating the spaces in this
     * portfolio and the spaces for the additional IDs specified.
     * @param additionalSpaceIDCollection the IDs of additional spaces to load
     * and put in the map
     * @return the map of space ID to space containing the spaces in the
     * current porfolio and the spaces for the specified IDs
     * @throws PersistenceException if there is a problem loading the spaces
     */
    protected abstract Map loadAllSpacesMap(Collection additionalSpaceIDCollection) throws PersistenceException;

    /**
     * Constructs the tree based on the spaces in the specified map, adding
     * membership information based on the specified membership collection.
     * @param allSpacesMap the spaces to build the tree from
     * @param membershipSpaceIDCollection the IDs of spaces that the user is
     * a member of
     * @param childParentMap a map of child space IDs to parent space IDs
     * used to build the hierarchies
     */
    protected abstract void constructTree(Map allSpacesMap, Collection membershipSpaceIDCollection, Map childParentMap);

    /**
     * Makes a tree entry for the specified project space.
     * @param space the project space to make a tree entry for
     * @param membershipProjectIDCollection the collection of project IDs
     * from which to determine membership
     * @return the tree entry
     */
    protected TreeEntry makeTreeEntry(Space space, Collection membershipProjectIDCollection) {
        return new TreeEntry(space, membershipProjectIDCollection.contains(space.getID()));
    }

    /**
     * Provides the value for the TreeNodes.
     * Includes the ProjectSpace and a flag indicating membership.
     */
    static class TreeEntry {

        /**
         * The Space.
         */
        private Space space = null;

        /**
         * The membership flag.
         */
        private boolean isMember = false;

        /**
         * Creates a new TreeEntry for the specified project space and
         * membership.
         * @param space the project space for this tree entry
         * @param isMember true if the project space is defined
         * in the current portfolio; false otherwise
         */
        private TreeEntry(Space space, boolean isMember) {
            this.space = space;
            this.isMember = isMember;
        }

        /**
         * Returns this entry's project space.
         * @return the project space
         */
        private Space getSpace() {
            return this.space;
        }

        /**
         * Indicates whether the owner of the current portfolio is a member
         * of this entry's project space.
         * @return true if the project space is in the portfolio; false otherwise
         */
        private boolean isMember() {
            return this.isMember;
        }

    }

    /**
     * Comparator for comparing <code>TreeNode</code> objects based
     * on the assumption that their values are ProjectSpace objects.
     */
    protected static class NameComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            TreeEntry entry1 = (TreeEntry) ((TreeNode) o1).getValue();
            TreeEntry entry2 = (TreeEntry) ((TreeNode) o2).getValue();
            return entry1.getSpace().getName().compareToIgnoreCase(entry2.getSpace().getName());
        }

    }
}

