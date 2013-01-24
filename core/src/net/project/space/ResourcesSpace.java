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
package net.project.space;

import java.io.Serializable;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
/**
 * This class encapsulates Resource Manager functionality.
 * @author Alejandro
 * @author Carlos
 * @version %I%, %G%
 * @since 8.3.0
 */
public class ResourcesSpace extends Space implements Serializable {
	
	/** The default Resource space object id in <code>PN_OBJECT</code> table. */
	public static final String SPACE_ID = "10";
	
	public ResourcesSpace() {
		super(SPACE_ID);
        setType(ISpaceTypes.RESOURCES_SPACE);
        this.spaceType = SpaceTypes.RESOURCES;        
    }

	@Override
	public String getXML() {
		return null;
	}

	@Override
	public String getXMLBody() {
		return null;
	}
	
    /**
     * Get the Space's name.
     * @since 8.3.0
     */
    public String getName() {
        return PropertyProvider.get("prm.space.spacetypes.resources.name");
    }

	@Override
	public void load() throws PersistenceException {
		
	}

	@Override
	public void remove() throws PersistenceException {
	}

	@Override
	public void store() throws PersistenceException {
	}

}
