/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnDocContainerListViewDAO;
import net.project.hibernate.model.PnDocContainerListView;
import net.project.hibernate.service.IPnDocContainerListViewService;

@Service(value="docContainerListViewService")
public class PnDocContainerListViewServiceImpl implements IPnDocContainerListViewService{
	
	@Autowired
	private IPnDocContainerListViewDAO docContainerListViewDAO;

	public IPnDocContainerListViewDAO getDocContainerListViewDAO() {
		return docContainerListViewDAO;
	}

	public void setDocContainerListViewDAO(IPnDocContainerListViewDAO docContainerListViewDAO) {
		this.docContainerListViewDAO = docContainerListViewDAO;
	}

	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnDocContainerListViewDAO#getAllContainersDocument(java.lang.String)
	 */
	public List<PnDocContainerListView> getAllContainersDocument(String spaceID){
		return docContainerListViewDAO.getAllContainersDocument(spaceID);
	}
}
