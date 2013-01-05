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
package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import net.project.hibernate.model.PnWikiPage;

/**
 * Interface for accessing wiki page database object.
 */
public interface IPnWikiPageDAO extends IDAO<PnWikiPage, Integer> {
	
	public PnWikiPage getWikiPageWithName(String pageName, Integer ownerObjectId);
	
	public PnWikiPage getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(String pageName, Integer ownerObjectId);
	
	/**
	 * Method which returns list of <i>all subpages</i> of one wiki page.<br><br>
	 * 
	 * So <b>image-details</b> pages are also included in returned list of pages.
	 * @param pnWikiPage
	 * @return
	 */
	public List<PnWikiPage> getSubPages(PnWikiPage pnWikiPage);
	
	/**
	 * Method which returns list of <i>actual subpages</i> of one wiki page.<br>
	 * <b>Actual subpages</b> are wiki pages that are subpages of the given wiki page, 
	 * but all image-details wiki pages that are children of this page are not considred to be 
	 * acutal subpages.<br>
	 * So 'image-details' pages are excluded from returned list of pages.
	 * @param pnWikiPage
	 * @return
	 */
	public List<PnWikiPage> getActualSubPages(PnWikiPage pnWikiPage);
	
	public boolean hasSubPages(PnWikiPage pnWikiPage);

	public List<PnWikiPage> getWikiPagesByOwnerAndRecordStatus(Integer ownerObjectId, String status);
	
	public boolean doesWikiPageWithGivenNameExist(String wikiPageName, Integer projectSpaceId);
	
	//added for retrieving list of images available for attaching to wiki pages of one wiki
	/**
	 * Method that retrieves images-detail wiki pages for all images uploaded to wiki for specified object. <br>
	 * TODO: remove this method.
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
	 * Method for getting specified number of wiki pages from specified wiki space ordered by date (in desc order).
	 * @param spaceId
	 * @param numberOfPages
	 * @return
	 */
	public List<PnWikiPage> getWikiPagesByDate(Integer spaceId, int numberOfPages, String namespace);
	
	/**
	 * Method for retreiving root wiki page for object specified with objectId. For regular wiki objects only.
	 * (Not for assignments type objects)
	 * @param objectId
	 * @return
	 */
	public PnWikiPage getRootPageForObject(Integer objectId);
    
    /**
     * Method for retreiving wiki page by wiki page id.
     * @param pageId
     * @return PnWikiPage
     */
    public PnWikiPage  getWikiPageByPageId(Integer pageId);
    
    /**
     * Method to retrive wiki page count for project within start date and snd date
     * @param projectId
     * @param startDate
     * @param endDate
     * @return Integer
     */
    public Integer getWikiPageCountForProject(Integer projectId, Date startDate, Date endDate);
    
    /**
     * @param pageName
     * @return
     */
    public PnWikiPage getRootWikiPageByName(String pageName);
    
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
    
    /**
     * Method for retrieving wiki page by wiki page id and owner object id with no regards to recordStatus field.
     * @param wikiPageId
     * @param ownerObjectId
     * @return
     */
    public PnWikiPage getWikiPage(String wikiPageName, Integer ownerObjectId);

}