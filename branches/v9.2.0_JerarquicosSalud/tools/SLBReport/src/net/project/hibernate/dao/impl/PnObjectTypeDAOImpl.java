package net.project.hibernate.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IPnObjectTypeDAO;
import net.project.hibernate.model.PnObjectType;

public class PnObjectTypeDAOImpl extends AbstractHibernateDAO<PnObjectType, String> implements IPnObjectTypeDAO {

	public PnObjectTypeDAOImpl() {
		super(PnObjectType.class);
	}

	@SuppressWarnings("unchecked")
	public List<PnObjectType> findObjectTypes() {
		List<PnObjectType> pnObjectTypeList = new ArrayList();
		try {
			Iterator pnObjectTypeIterator = getHibernateTemplate().find(" select objectType, defaultPermissionActions from PnObjectType ").iterator();
			while(pnObjectTypeIterator.hasNext()){
				Object[] obj = (Object[])pnObjectTypeIterator.next();
				PnObjectType pnObjectType = new PnObjectType((String)obj[0], ((Long)obj[1]).longValue());
				pnObjectTypeList.add(pnObjectType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pnObjectTypeList;
	}
	
	

}
