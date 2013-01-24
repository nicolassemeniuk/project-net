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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceFinder;
import net.project.persistence.PersistenceException;
import net.project.space.SpaceManager;
import net.project.space.SpaceRelationship;

/**
 * Provides a Tree View of Businesses from a portfolio.
 * This includes other businesses that are not in the portfolio but that are
 * parents of those businesses, required to build the tree structure.
 * The exact algorithm is as follows:
 * <p>
 * <li>All leaf-nodes in the tree are businesses from the portfolio;
 * that is, no sub-businesses of those businesses are included.
 * <li>All ancestor businesses of their membership businesses are included;
 * that is, all parent businesses, and their parents, and their parents etc.
 * <li>No siblings of their membership businesses are included (unless, of course,
 * the user is a member of that also).
 * </p>
 */
public class PersonalBusinessTreeView extends PortfolioTreeView {

    /**
     * Creates a new Tree View for the specified portfolio.
     * The businesses in the portfolio are used as the basis of the tree view.
     * The tree view includes all of those businesses and any additional
     * ancestor businesses necessary to build the tree structure.
     * @param portfolio the portfolio on which to base the tree structure
     * @throws PersistenceException if there is a problem building the
     * tree
     */
    PersonalBusinessTreeView(Portfolio portfolio) throws PersistenceException {
        super(portfolio);
    }

    /**
     * Returns the collection of project spaces related to the spaces with IDs in the
     * specified collection.
     * This may include spaces with IDs in the collection as well as other spaces
     * @param membershipSpaceIDCollection the IDs for which to get related project
     * spaces
     * @return the collection of <code>{@link SpaceManager.RelatedSpace}</code>s
     * @throws PersistenceException if there is a problem getting the related
     * spaces
     */
    protected Collection getRelatedSpaceCollection(Set membershipSpaceIDCollection) throws PersistenceException {
        // Get the parent space relationship information for those
        // portfolio entries
        // This may include space ids that are not in the membership list
        return SpaceManager.getHierarchicalRelatedSpaces(membershipSpaceIDCollection, SpaceRelationship.SUBSPACE, "business");
    }

    /**
     * Returns a map of loaded spaces for the ids in the specified
     * collection and the spaces in the portfolio.
     * @param additionalSpaceIDCollection the additional space ids to load;
     * only spaces with matching IDs and an <i>Active</i> record status
     * are loaded; if any spaces are not found, no exception is thrown.  Instead,
     * that ID is silently ignored
     * @return a map where each key is a String space id and each value
     * is a <code>BusinessSpace</code> for that id
     * @throws PersistenceException if there is a problem loading
     */
    protected Map loadAllSpacesMap(Collection additionalSpaceIDCollection) throws PersistenceException {

        // Now load the spaces spaces for set of ids
        // And add in the spaces in this portfolio
        List allSpacesList = new ArrayList();

        if (!additionalSpaceIDCollection.isEmpty()) {
            allSpacesList.addAll(new BusinessSpaceFinder().findByIDs(additionalSpaceIDCollection, "A"));
        }
        allSpacesList.addAll(getPortfolio());

        // Construct a map from space id to space
        Map allSpacesMap = new HashMap();
        for (Iterator it = allSpacesList.iterator(); it.hasNext();) {
            BusinessSpace nextBusinessSpace = (BusinessSpace) it.next();
            allSpacesMap.put(nextBusinessSpace.getID(), nextBusinessSpace);
        }

        return allSpacesMap;
    }

    /**
     * Builds the tree and sorts it in name order.
     * @param allSpacesMap the spaces to add to the tree
     * @param membershipSpaceIDCollection the ids of the spaces that the
     * user is a member of
     * @param childParentMap a map of child id to parent id for determine
     * parent child relationships
     */
    protected void constructTree(Map allSpacesMap, Collection membershipSpaceIDCollection, Map childParentMap) {

        // Iterate over all spaces building the tree structure
        // Add BusinessSpace objects to tree for each id
        // Add flag indicating whether member of space or not
        for (Iterator it = allSpacesMap.keySet().iterator(); it.hasNext();) {

            BusinessSpace nextBusinessSpace = (BusinessSpace) allSpacesMap.get(it.next());

            TreeEntry entry = makeTreeEntry(nextBusinessSpace, membershipSpaceIDCollection);

            // Add the entry to the tree with the appropriate parent

            // Lookup the parent space
            String parentSpaceID = (String) childParentMap.get(nextBusinessSpace.getID());
            // This might return null if the parent was not loaded because
            // it was disabled
            BusinessSpace parentBusinessSpace = null;
            if (parentSpaceID != null) {
                parentBusinessSpace = (BusinessSpace) allSpacesMap.get(parentSpaceID);
            }

            if (parentBusinessSpace != null) {
                // We got a parent space, so we add the parent
                // to the tree also
                TreeEntry parentEntry = makeTreeEntry(parentBusinessSpace, membershipSpaceIDCollection);
                this.tree.add(nextBusinessSpace.getID(), entry, parentBusinessSpace.getID(), parentEntry);

            } else {
                // No parent space, add the entry with no parent
                this.tree.add(nextBusinessSpace.getID(), entry);

            }

        }

        // Now sort the tree in Space Name order
        this.tree.sort(new NameComparator());
    }

}
