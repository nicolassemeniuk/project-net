/**
 * 
 */
package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnDocContainerListViewDAO;
import net.project.hibernate.model.PnDocContainerListView;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional 
@Repository 
public class PnDocContainerListViewDAOImpl extends AbstractHibernateAnnotatedDAO<PnDocContainerListView, Integer> implements IPnDocContainerListViewDAO {

	public PnDocContainerListViewDAOImpl() {
		super(PnDocContainerListView.class);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.dao.IPnDocContainerListViewDAO#getAllContainersDocument(java.lang.String)
	 */
	public List<PnDocContainerListView> getAllContainersDocument(String spaceID){
		List<PnDocContainerListView> list = new ArrayList<PnDocContainerListView>();
		
		String allContainersAndDocuments = "SELECT DISTINCT objectId, objectType, name, format, appIconUrl," +
				"url, version, isCheckedOut, checkedOutBy,status,checkedOutById, author, dateModified,fileSize, " +
				"docContainerId, shortFileName,hasLinks,hasWorkflows,hasDiscussions, description, comments, isHidden " +
				"FROM PnDocContainerListView  WHERE docContainerId IN (SELECT pnDocContainer.docContainerId  " +
				"FROM PnDocSpaceHasContainer WHERE pnDocSpace.docSpaceId = (SELECT pnDocSpace.docSpaceId " +
				"FROM PnSpaceHasDocSpace WHERE comp_id.spaceId = :spaceID)) AND is_hidden = 0";
		try{
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(allContainersAndDocuments);
			query.setString("spaceID", spaceID);
			Iterator results = query.list().iterator();
			PnDocContainerListView containerListView;
			while(results.hasNext()){
				
				Object[] row = (Object[]) results.next();
				containerListView = new PnDocContainerListView();
				
				containerListView.setObjectId((BigDecimal)row[0]);
				containerListView.setObjectType((String)row[1]);
				containerListView.setName((String)row[2]);
				containerListView.setFormat((String)row[3]);
				containerListView.setAppIconUrl((String)row[4]);
				containerListView.setUrl((String)row[5]);
				containerListView.setVersion((BigDecimal)row[6]);
				containerListView.setIsCheckedOut((BigDecimal)row[7]);
				containerListView.setCheckedOutBy((String)row[8]);
				containerListView.setStatus((String)row[9]);
				containerListView.setCheckedOutById((BigDecimal)row[10]);
				containerListView.setAuthor((String)row[11]);
				containerListView.setDateModified((Date)row[12]);
				containerListView.setFileSize((BigDecimal)row[13]);
				containerListView.setDocContainerId((BigDecimal)row[14]);
				containerListView.setShortFileName((String)row[15]);
				containerListView.setHasLinks((BigDecimal)row[16]);
				containerListView.setHasWorkflows((BigDecimal)row[17]);
				containerListView.setHasDiscussions((BigDecimal)row[18]);
				containerListView.setDescription((String)row[19]);
				containerListView.setComments((String)row[20]);
				containerListView.setIsHidden((BigDecimal)row[21]);
				
				list.add(containerListView);
			}
			
		}catch (Exception e) {
			Logger.getLogger(PnDocContainerListViewDAOImpl.class).error("Error Occured while getting all Documents.."+e.getMessage());	
		}
		return list;
	}
}
