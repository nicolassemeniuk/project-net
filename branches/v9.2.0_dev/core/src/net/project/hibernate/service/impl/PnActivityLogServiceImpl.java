/**
 * 
 */
package net.project.hibernate.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import net.project.events.ApplicationEvent;
import net.project.hibernate.dao.IPnActivityLogDAO;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.service.IPnActivityLogService;
import net.project.util.StringUtils;

/**
 *
 */
@Service(value="pnActivityLogService")
public class PnActivityLogServiceImpl implements IPnActivityLogService {
	
	@Autowired
	private IPnActivityLogDAO pnActivityLogDAO;

	/**
	 * @param pnActivityLogDAO the pnActivityLogDAO to set
	 */
	public void setPnActivityLogDAO(IPnActivityLogDAO pnActivityLogDAO) {
		this.pnActivityLogDAO = pnActivityLogDAO;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#get(java.lang.Integer)
	 */
	public PnActivityLog get(Integer key) {
		return pnActivityLogDAO.findByPimaryKey(key);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#load(java.lang.Integer)
	 */
	public PnActivityLog load(Integer key) {
		return pnActivityLogDAO.findByPimaryKey(key);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#findAll()
	 */
	public List<PnActivityLog> findAll() {
		return pnActivityLogDAO.findAll();
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#save(net.project.hibernate.model.PnActivityLog)
	 */
	public Integer save(PnActivityLog pnActivityLog) {
		return pnActivityLogDAO.create(pnActivityLog);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#update(net.project.hibernate.model.PnActivityLog)
	 */
	public void update(PnActivityLog pnActivityLog) {
		pnActivityLogDAO.update(pnActivityLog);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#delete(net.project.hibernate.model.PnActivityLog)
	 */
	public void delete(PnActivityLog pnActivityLog) {
		pnActivityLogDAO.delete(pnActivityLog);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#saveApplicationEvent(net.project.notification.ApplicationEvent)
	 */
	public Integer saveApplicationEvent(ApplicationEvent applicationEvent) {
		PnActivityLog activityLog = getParsedActivityLog(applicationEvent);
		return save(activityLog);
	}

	/**
	 * @param applicationEvent
	 * @return PnActivityLog
	 */
	private PnActivityLog getParsedActivityLog(ApplicationEvent applicationEvent) {
		PnActivityLog activityLog = new PnActivityLog();
		activityLog.setActivityBy(Integer.valueOf(applicationEvent.getInitiatorID()));
		activityLog.setActivityType(applicationEvent.getType().getText());
		activityLog.setTargetObjectId(Integer.valueOf(applicationEvent.getObjectID()));
		activityLog.setTargetObjectType(applicationEvent.getObjectType());
		activityLog.setActivityOnDate(new Timestamp(applicationEvent.getEventDate().getTime()));
		activityLog.setDescription(applicationEvent.getDescription());
		activityLog.setSpaceId(Integer.valueOf(applicationEvent.getSpaceID()));
		if(StringUtils.isNotEmpty(applicationEvent.getParentObjectId()))
			activityLog.setParentObjectId(Integer.valueOf(applicationEvent.getParentObjectId()));
		if(applicationEvent.getIsImportant() != null)
			activityLog.setIsImportant(applicationEvent.getIsImportant());
		return activityLog;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#getActivityLogBySpaceIdAndDate(java.lang.Integer, java.util.Date, java.util.Date, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	public List<PnActivityLog> getActivityLogBySpaceIdAndDate(Integer spaceId, Date startDate, Date endDate, String criteria, Integer personId, Integer offSet, Integer range, Integer currentUserId) {
		return pnActivityLogDAO.getActivityLogBySpaceIdAndDate(spaceId, startDate, endDate, criteria, personId, offSet, range, currentUserId);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogService#getActivityIdsOfBlogHavingComment(java.util.List)
	 */
	public String getActivityIdsOfBlogHavingComment(List activityIds){
		return pnActivityLogDAO.getActivityIdsOfBlogHavingComment(activityIds);
	}
}
