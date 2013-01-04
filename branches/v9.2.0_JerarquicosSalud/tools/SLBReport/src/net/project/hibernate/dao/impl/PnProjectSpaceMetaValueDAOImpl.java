package net.project.hibernate.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO;
import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.model.PnProjectSpaceMetaValuePK;

import org.hibernate.Query;
import org.hibernate.Session;


public class PnProjectSpaceMetaValueDAOImpl extends AbstractHibernateDAO<PnProjectSpaceMetaValue, PnProjectSpaceMetaValuePK> implements IPnProjectSpaceMetaValueDAO {
    public PnProjectSpaceMetaValueDAOImpl() {
        super(PnProjectSpaceMetaValue.class);
    }

    @SuppressWarnings("unchecked")
    public List<PnProjectSpaceMetaValue> getMetaValuesByProjectId(final BigDecimal projectId) {
        try {
        	String hql = " from PnProjectSpaceMetaValue where comp_id.projectId = :projectId ";
        	Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
            Query query = session.createQuery(hql);
            query.setBigDecimal("projectId", projectId);            
            List<PnProjectSpaceMetaValue> metaValues = query.list();
            return metaValues;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
