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

 package net.project.business;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.portfolio.ProjectPortfolioBean;
import net.project.project.ProjectSpace;
import net.project.space.Space;

import org.apache.log4j.Logger;


/**
 * Provides methods used in BusinessDeleteWizard . Hold the information regarding 
 * the projects being deleted or moved , etc . Finally , resorts to a "soft" delete 
 * of business  
 * @author deepak
 */

public class BusinessDeleteWizard{

    private static Logger logger = Logger.getLogger(BusinessDeleteWizard.class);

    /**
     * Holds the ID of the Business on which the Deletion is being performed
     */
    private String businessID=null;
    /**
     * Pertains to the ID of the new owner (or Business ) for the projects 
     */
    private String newOwnerID=null;
    /**
     * List of projects being deleted
     */
    private ArrayList projectsForDeletion=new ArrayList();
    /**
     * List of projects being moved from one business to other
     */
    private ArrayList projectsForMoving =new ArrayList();
    /**
     * Collection of all projects under the Business
     */
    private ProjectPortfolioBean projectPortfolio=null;
    /**
     * Holds the information about the errors occurring during deletion
     */
    private ValidationErrors errors = new ValidationErrors();

    /**
     *  Sets the BusinessID
     * param String BusinessID
     * @param businessID String
     */
    public void setBusinessID(String businessID){
        this.businessID=businessID;
    }
    
    /**
     * Resets the object
     */
    public void clear(){
        projectsForDeletion.clear();
        projectsForMoving.clear();
        projectPortfolio=null;
        errors.clearErrors();
    }

     
    /**
     *  Gets the BusinessID
     * return String BusinessID
     * @return String
     */
    public String getBusinessID(){
        return this.businessID;
    }

    /**
     *  Sets the BusinessID for the new owner of the deleted projects under this business
     * param String BusinessID
     * @param businessID String
     */

    public void setNewOwnerID(String businessID){
        this.newOwnerID=businessID;
    }

    /**
     *  Gets the New Business Owner ID . This is of use if the business is to be deleted
     *  & the projects under the business is to be transferred to some other business .
     * return String NewBusinessID
     * @return String
     */
    public String getNewOwnerID(){
        return this.newOwnerID;
    }

    /**
     * Sets the ProjectPortfolio for the current Business Wizard Object
     * @param projectPortfolio the portfolio of projects owned by the
     * current business
     */
    public void setProjectPortfolio(ProjectPortfolioBean projectPortfolio){
        this.projectPortfolio=projectPortfolio;
    }

    /**
     * Adds the Project to Arraylist meant for deleted projects
     * @param space
     */
    public void addProjectForDeletion(Space space){
        this.projectsForDeletion.add(space);                       
    }

    /**
     * Changes the OwnerID  for the undeleted projects under the existing business 
     * @param businessID
     */

    public void changeOwnerForProjects(String businessID) 
        throws PersistenceException {

         Iterator itr=this.projectPortfolio.iterator();
         while(itr.hasNext ()){
            boolean check=false;
            IPortfolioEntry ppe=(IPortfolioEntry)itr.next();
            Iterator itrForDeletion=this.projectsForDeletion.iterator();
	        while(itrForDeletion.hasNext()){
                    Space space=(Space)itrForDeletion.next();
                    if(space.getID().equals(ppe.getID())){
                        check=true;
                    }
                }
                if((check==false || projectsForDeletion.size()==0) && !(this.projectPortfolio.size()==0)){
                   ProjectSpace space =new ProjectSpace(ppe.getID());
                    try{
                        space.load();
                        space.setParentBusinessID(businessID);
                        space.store();      
                    }
                    catch(PersistenceException pe){
                        throw new PersistenceException("Owner for the projects " +
                            "could not be changed " +pe, pe);
                    }
                }
                       
	    }   
            removeBusiness();
     }

    /**
     * Removes the errors stored in Validation Errors HashMap
     */
    public void clearErrors() {
        errors.clearErrors();
    }

   /**
    * Indicate whether there are any errors
    * @return true if there are errors, false otherwise
    */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    /**
     * Gets the Error Flag for the Field
     * @return String
     * @param fieldID
     * @param label
     */
    public String getFlagError(String fieldID, String label) {
        return errors.getFlagErrorHTML(fieldID, label);
    }

    /**
     * Gets the Error Message for the Field
     *@return String 
     *@param fieldID
     */
    public String getErrorMessage(String fieldID) {
        return errors.getErrorMessageHTML(fieldID);
    }

    /**
     * Gets the Error Message for the Field
     * @return String
     */
    public String getAllErrorMessages() {
        return errors.getAllErrorMessagesHTML();
    }

    /**
     *Invalidates the BusinessDeleteWizard
     */
    public void invalidate() {
        // something is invalid
        errors.put("error1", PropertyProvider.get("prm.business.delete.wizard.invalidate.message"));
    }

    /**
     * Checks whether all projects have been deleted or not
     *@return boolean
     */

    public boolean checkForAllDeleted() 
        throws PersistenceException {

        Iterator itr=this.projectPortfolio.iterator();
        while(itr.hasNext ()){

            boolean check =false ; // Flag to be check project is deleted or not
            IPortfolioEntry ppe=(IPortfolioEntry)itr.next();
            Iterator itrForDeletion=this.projectsForDeletion.iterator();
	    while(itrForDeletion.hasNext()){
                Space space=(Space)itrForDeletion.next();
                if(space.getID().equals(ppe.getID())){
                    check=true;
                    break;
                 }
            }
            if((check==false || projectsForDeletion.size()==0) && !(this.projectPortfolio.size()==0)){
                 return false;
            }
         }
        removeBusiness();
        return true;
    }

    /**
     * Finally removes the Business
     * @throws PersistenceException
     */
    private void removeBusiness() 
        throws PersistenceException {

        try{

            BusinessSpace space = new BusinessSpace(getBusinessID());

            // Encapsulate the load in its own try...catch
            // so that we do not prevent the removal of a business that
            // fails to load
            try {
                // We load the space so that the notification email
                // contains the business name
                space.load();
            } catch (PersistenceException e) {
                // We'll log this as a problem, but suck up the exception
                logger.warn("Unable to load a business with ID " + getBusinessID() + " while attempting to disable. " +
                        "Continuing to disable anyway.", e);

            }

            // Now remove the business; we don't suck up exceptions here
            // since this is the core functionality of this method
            space.remove();

        } catch(PersistenceException pe){
            throw new PersistenceException ("Business could not be deleted " + pe, pe);
        }
     }

    


}
