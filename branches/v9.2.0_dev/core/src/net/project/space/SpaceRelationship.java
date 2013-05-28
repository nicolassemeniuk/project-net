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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|    
| Tim Morrow, Roger Bly 7/5/01                                                                   
+----------------------------------------------------------------------*/
package net.project.space;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Defines allowed relationships between Spaces.
 * Provides an enumeration of allowed relationships.
 * SpaceRelationships are constructed using the static enumerations such as:
 * <code><pre>
 * SpaceRelationship.OWNERSHIP
 * SpaceRelationship.SUBSPACE
 * SpaceRelationship.MASTER
 * SpaceRelationship.FINANCIAL
 * </code></pre>
 * 
 * And methods are called as follows (for example):
 * <code><pre>
 * String s = SpaceRelationship.SUBSPACE.getNameParentToChild();
 * </code></pre>
 */
public class SpaceRelationship {

    /** All the defined relationships, used for easy traversal of all relationships. */
    private static ArrayList allRelationships = new ArrayList();

    /**
     * Returns the relationship with the given names.
     * @param parentRelationshipName the parent name of the relationship to get
     * @param childRelationshipName the child name of the relationship to get
     * @return the relationship with those names; or null if none matches
     */
    public static SpaceRelationship forNames(String parentRelationshipName, String childRelationshipName) {
        SpaceRelationship rel = null;
        
        Iterator it = SpaceRelationship.allRelationships.iterator();
        while (it.hasNext()) {
            rel = (SpaceRelationship) it.next();

            if (rel.parentRelationshipName.equals(parentRelationshipName) &&
                    rel.childRelationshipName.equals(childRelationshipName)) {
                break;
            }

        }

        return rel;
    }

    /** The name of the relationship from parent to child. */
    private String parentRelationshipName = null;
    
    /** The name of the relationship from child to parent. */
    private String childRelationshipName = null;


    /**
     * Construct a SpaceRelationship with the specified names.
     * @param parentRelationshipName the name of the relationship from parent
     * to child
     * @param childRelationshipName the name of the relationship from child
     * to parent
     */
    private SpaceRelationship(String parentRelationshipName, String childRelationshipName) {
        this.parentRelationshipName = parentRelationshipName;
        this.childRelationshipName = childRelationshipName;

        SpaceRelationship.allRelationships.add(this);
    }

    /**
     * Get the name of the relationship from parent to child
     * @return the name of the relationship
     */
    public String getNameParentToChild() {
        return this.parentRelationshipName;
    }

    /**
     * Get the name of the relationship from child to parent
     * @return the name of the relationship
     */
    public String getNameChildToParent() {
        return this.childRelationshipName;
    }

    /**
     * Test to see if two relationships are equal.
     * This is defined as having identical parent-to-child names and
     * child-to-parent names.
     * @param obj the relationship to compare with this one
     * @return true if the relationships are equal; false otherwise
     */
    public boolean equals(Object obj) {
        if (obj instanceof SpaceRelationship &&
            obj != null &&
            ((SpaceRelationship)obj).getNameParentToChild().equals(this.getNameParentToChild()) &&
            ((SpaceRelationship)obj).getNameChildToParent().equals(this.getNameChildToParent())) {

            return true;
        }
        return false;
    }


    //
    // Relationship constants
    // These are defined at the bottom to ensure any other static code
    // as completely initialized prior to constructing the objects
    // (Very important - Tim)
    //
    
    /**
     * Defines a Space that owns another Space
     */
    public static final SpaceRelationship OWNERSHIP = new SpaceRelationship("owns", "owned_by");

    /**
     * Defines a Space that is a sub-space of another Space
     */
    public static final SpaceRelationship SUBSPACE = new SpaceRelationship("superspace", "subspace");

    /**
     * Defines a Space that is a master of another Space, typically a business space that is used for top-level billing
     */
    public static final SpaceRelationship MASTER = new SpaceRelationship("is_master", "has_master");

    /**
     * Define a Space that receives information from other spaces by providing them with a tool to enter this
     * information.  An example of this is form sharing.  An information_consumer is provided information by
     * an information_provider.  The information_provider is the space that created the form.  The
     * information_consumers have access to the form and can enter data.
     */
    public static final SpaceRelationship INFORMATION_PROVIDER = new SpaceRelationship("information_consumer", "information_provider");
    
    public static final SpaceRelationship FINANCIAL = new SpaceRelationship("financial_business", "financial_financial");
}
