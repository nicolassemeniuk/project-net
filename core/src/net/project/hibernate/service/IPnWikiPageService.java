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
package net.project.hibernate.service;

import java.util.Date;
import java.util.List;
import net.project.hibernate.model.PnWikiPage;


public interface IPnWikiPageService {

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param pnWikiPage a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(net.project.hibernate.model.PnWikiPage pnWikiPage);	

	/**
	 * For saving assignment objects wiki page. 
	 * @param pnWikiPage wiki page to save
	 * @param ownerSpaceId the ID of object that owns assignment (e.g project)
	 * @return
	 */
	public Integer saveAssignment(PnWikiPage pnWikiPage, Integer ownerSpaceId);
	
	/**
	 * Get the record from pn_wiki_page table with given parameters, and status A or D (doesn't matter)
	 * @param pageName
	 * @param ownerObjectId
	 * @return
	 */
	public PnWikiPage getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(String pageName, Integer ownerObjectId);
	
	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param pnWeblog a transient instance containing updated state
	 */
	public void update(net.project.hibernate.model.PnWikiPage pnWikiPage);
	
	public net.project.hibernate.model.PnWikiPage get(java.lang.Integer key);
	
	public java.util.List<net.project.hibernate.model.PnWikiPage> findAll ();
	
	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param pnWeblog the instance to be removed
	 */
	public void delete(net.project.hibernate.model.PnWikiPage pnWikiPage);

	/**
	 * Returns the wiki page from db with specific page name with restriction that it belongs to specified object.
	 * @param pageName the name of the page to search for
	 * @param ownerObjectId the id of the object this page belogns to (e.g. ProjectA has page ProjectObjective, and ProjectB has page with same name)
	 * @return
	 */
	public PnWikiPage getWikiPageWithName(String pageName, Integer ownerObjectId);
	
	/**
	 * Method for deleting wiki page (along with all of its subpages and its subpages...).<br>
	 * <b>It also deletes all attachmnets related to those deleted pages.</b><br>
	 * 
	 * @param pnWikiPage wiki page to delete
	 */
	public void deletePageWithSubpages(PnWikiPage pnWikiPage);
	
	/**
	 * Returns list of wiki page from db with appropriate status.
	 * 
	 * @param ownerObjectId
	 * @param status
	 * @return
	 */
	public List<PnWikiPage> getWikiPagesByOwnerAndRecordStatus(Integer ownerObjectId, String status);

	/**
	 * Checks for existence of wiki page with specified name for given project id (in specified project space).
	 * Doesn't matter which status is "D" or "A".
	 * 
	 * @param pnWikiPage
	 * @param projectSpaceId
	 * @return true if it exists, false otherwise.
	 */
	public boolean doesWikiPageWithGivenNameExist(String wikiPageName, Integer projectSpaceId);
	
	//added for retrieving list of images available for attaching to wiki pages of one wiki
	/**
	 * Method that retrieves images-detail wiki pages for all images uploaded to wiki for specified object.
	 * <b>Use method <i>getAllImageDetailPagesForWiki(Integer ownerObjectId, String recordStatus)</i> instead.</b>
	 * @param ownerObjectId id of object which wiki we are currently using
	 * @param objectName name of object which wiki we are currently using
	 * @param recordStatus
	 */
	public List<PnWikiPage> getAllImageDetailPagesForWiki(Integer ownerObjectId, String objectName, String recordStatus);
	
	/**
	 * Method that retrieves images-detail wiki pages for all images uploaded to wiki for specified object.
	 * @param ownerObjectId id of object which wiki we are currently using
	 * @param recordStatus
	 */
	public List<PnWikiPage> getAllImageDetailPagesForWiki(Integer ownerObjectId, String recordStatus);
	
	/**
	 * Method that returns list of all subpages (child pages) of wiki page passed as argument.
	 */
	public List<PnWikiPage> getSubPages(PnWikiPage pnWikiPage);
	
	/**
	 * Method for getting list of <b>Recent Changes</b> for one wiki space.
	 * @param spaceId wiki space we are searching in
	 * @param rangeNumber number of pages to return
	 * @param namespace the namespace of pages to return (eg Image...)
	 * @return list of recent changed pages
	 */
	public List<PnWikiPage> getRecentChangesForWiki(Integer spaceId, int rangeNumber, String namespace);
	
	/**
	 * Method for assigning selected wiki page (selectedWikiPage) to object with ID objectId.
	 * @param selectedWikiPage
	 * @param objectId
	 */
	public void assignWikiPageToObject(PnWikiPage selectedWikiPage, Integer objectId);
	
	/**
	 * Method for retreiving root wiki page for object specified with objectId.
	 * @param objectId
	 * @return
	 */
	public PnWikiPage getRootPageForObject(Integer objectId);
    
    /**
     * Method for retreiving wiki page by wiki page id.
     * @param wikiPageId
     * @return
     */
    public PnWikiPage getWikiPage(Integer wikiPageId);
    
    /**
     * Method for retrieving wiki page by wiki page id and owner object id with no regards to recordStatus field.
     * @param wikiPageId
     * @param ownerObjectId
     * @return
     */
    public PnWikiPage getWikiPage(String wikiPageName, Integer ownerObjectId);

    /**
     * Method to retrive wiki page count for project within start date and snd date
     * @param projectId
     * @param startDate
     * @param endDate
     * @return Integer
     */
    public Integer getWikiPageCountForProject(Integer projectId, Date startDate, Date endDate);
    
    
    /**
     * @param spaceId
     * @return PnWikiPage
     */
    public PnWikiPage getRootPageOfSpace(Integer spaceId);
    
    /**
     * @param pageName
     * @return
     */
    public PnWikiPage getRootPageByPageName(String pageName);
    
    public List<PnWikiPage> getRootWikiPagesByName(String pageName);
    /**
     * @param pageName
     * @param parentPageId
     * @return
     */
    public PnWikiPage getChildWikiPageByName(String pageName, Integer parentPageId);
    
    /**
     * @param objectId
     * @return
     */
    public PnWikiPage getWikiPageByObjectId(Integer objectId);
    
    /**
     * To get all documents pages for wiki space
     * @param ownerObjectId space identifier
     * @param recordStatus
     * @return List of PnWikiPage instances
     */
    public List<PnWikiPage> getAllDocumentsPagesForWiki(Integer ownerObjectId, String recordStatus);
    
    /**
     * To delete the wiki page with name and owner object id
     * @param pageName wiki page name
     * @param ownerObjectId
     */
    public void deleteWikiPageByName(String pageName, Integer ownerObjectId);

    /**
     * Method to rename wiki page name. MediaWiki pattern for renaming is used.
     * @param newName - new wiki page
     * @param wikiPageToRename - wiki page to rename
     */
 	public void renameWikiPage(PnWikiPage newPage, PnWikiPage wikiPageToRename);

 	/**
     * To get wiki page with name , parent page name and record status
     * @param wikiPageId
     * @return PnWikiPage Object 
     */
 	public PnWikiPage getWikiPageWithPageNameAndRecordStatusWithParent(Integer wikiPageId);
 	
 	/**
     * To get wiki page with name , parent page name and record status
     * @param wikiPageId
     * @return PnWikiPage Object 
     */
    public PnWikiPage getWikiPageWithPageNameAndRecordStatusWithoutParent(Integer wikiPageId);
    
    /**
     * To get wiki page with name , content and edited date and edited by
     * @param wikiPageId
     * @return PnWikiPage Object 
     */
    public PnWikiPage getWikiPageWithPageNameAndContent(Integer wikiPageId);
    
    /**
     * To delete wiki page by id
     * @param wikiPageId wiki page identifier
     */
    public void deleteWikiPageById(Integer wikiPageId);
}
