package net.project.portfolio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.project.financial.FinancialSpace;
import net.project.financial.PnFinancialSpaceList;
import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.space.SpaceManager;
import net.project.space.SpaceRelationship;

public class PersonalFinancialTreeView extends PortfolioTreeView {
	
	PersonalFinancialTreeView(Portfolio portfolio) throws PersistenceException {
        super(portfolio);
    }
	
    /**
     * Returns the collection of financial spaces related to the spaces with IDs in the
     * specified collection.
     * This may include spaces with IDs in the collection as well as other spaces
     * @param membershipSpaceIDCollection the IDs for which to get related project
     * spaces
     * @return the collection of <code>{@link SpaceManager.RelatedSpace}</code>s
     * @throws PersistenceException if there is a problem getting the related
     * spaces
     */

	@Override
	protected Collection getRelatedSpaceCollection(Set membershipSpaceIDCollection) throws PersistenceException {
        return SpaceManager.getHierarchicalRelatedSpaces(membershipSpaceIDCollection, SpaceRelationship.SUBSPACE, "financial");
	}

	@Override
	protected Map loadAllSpacesMap(Collection additionalSpaceIDCollection) throws PersistenceException {

        // Now load the spaces spaces for set of ids
        // And add in the spaces in this portfolio
        List allSpacesList = new ArrayList();

        if (!additionalSpaceIDCollection.isEmpty()) {
        	
        	ArrayList<Integer> ids = new ArrayList<Integer>();
        
        	for(Object id : additionalSpaceIDCollection){
        		ids.add(Integer.valueOf(id.toString()));
        	}
        		
        	
        	PnFinancialSpaceList spaces = ServiceFactory.getInstance().getPnFinancialSpaceService().getFinancialSpacesByIds(ids);
        	
        	for(PnFinancialSpace space : spaces){
        		FinancialSpace financialSpace = new FinancialSpace(space);
        		allSpacesList.add(financialSpace);
        	}       	
            
        }
        allSpacesList.addAll(getPortfolio());

        // Construct a map from space id to space
        Map allSpacesMap = new HashMap();
        for (Iterator it = allSpacesList.iterator(); it.hasNext();) {
            FinancialSpace nextFinancialSpace = (FinancialSpace) it.next();
            allSpacesMap.put(nextFinancialSpace.getID(), nextFinancialSpace);
        }

        return allSpacesMap;
	}

	@Override
	protected void constructTree(Map allSpacesMap, Collection membershipSpaceIDCollection, Map childParentMap) {		
        // Iterate over all spaces building the tree structure
        // Add FinancialSpace objects to tree for each id
        // Add flag indicating whether member of space or not
        for (Iterator it = allSpacesMap.keySet().iterator(); it.hasNext();) {

            FinancialSpace nextFinancialSpace = (FinancialSpace) allSpacesMap.get(it.next());

            TreeEntry entry = makeTreeEntry(nextFinancialSpace, membershipSpaceIDCollection);

            // Add the entry to the tree with the appropriate parent

            // Lookup the parent space
            String parentSpaceID = (String) childParentMap.get(nextFinancialSpace.getID());
            // This might return null if the parent was not loaded because
            // it was disabled
            FinancialSpace parentFinancialSpace = null;
            if (parentSpaceID != null) {
                parentFinancialSpace = (FinancialSpace) allSpacesMap.get(parentSpaceID);
            }

            if (parentFinancialSpace != null) {
                // We got a parent space, so we add the parent
                // to the tree also
                TreeEntry parentEntry = makeTreeEntry(parentFinancialSpace, membershipSpaceIDCollection);
                this.tree.add(nextFinancialSpace.getID(), entry, parentFinancialSpace.getID(), parentEntry);

            } else {
                // No parent space, add the entry with no parent
                this.tree.add(nextFinancialSpace.getID(), entry);

            }

        }

        // Now sort the tree in Space Name order
        this.tree.sort(new NameComparator());

	}

}
