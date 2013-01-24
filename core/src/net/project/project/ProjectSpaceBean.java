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

 package net.project.project;

import java.io.Serializable;

import net.project.persistence.PersistenceException;

/**
 * This Bean provides properties and methods for a Project Space.
 */
public class ProjectSpaceBean extends ProjectSpace implements Serializable {

    private String originalParentBusinessID = null;
    private String originalParentProjectID = null;
    
    public static final String SPACE_ID = "20";

    /**
     * Creates an empty ProjectSpaceBean.
     */
    public ProjectSpaceBean() {
        super(SPACE_ID);
    }

    /**
     * Loads this project space and records the original parent business and projects
     * to aid when modifying the project space.
     * <p>
     * It is possible that a user may modify this project that has a parent business or
     * parent project where they ordinarily don't have permission to make this project
     * a child of either.
     * We permit this relationship to continue.  However, they still should not be able
     * to change the parent business or project to one which they don't have permission for,
     * although that behavior is not implemented here.
     * </p>
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        super.load();
        this.originalParentBusinessID = getParentBusinessID();
        this.originalParentProjectID = getParentProjectID();
    }

    public void clear() {
        super.clear();
        this.originalParentBusinessID = null;
        this.originalParentProjectID = null;
    }

    public String getOriginalParentBusinessID() {
        return this.originalParentBusinessID;
    }

    public String getOriginalParentProjectID() {
        return this.originalParentProjectID;
    }

}
