package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnProjectSpaceMetaPropDAO;
import net.project.hibernate.model.PnProjectSpaceMetaProp;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;


public class PnProjectSpaceMetaPropDAOImpl extends AbstractHibernateDAO<PnProjectSpaceMetaProp, BigDecimal> implements IPnProjectSpaceMetaPropDAO {
    public PnProjectSpaceMetaPropDAOImpl() {
        super(PnProjectSpaceMetaProp.class);
    }

    @SuppressWarnings("unchecked")
    public PnProjectSpaceMetaProp getProjectSpaceMetaPropByName(final String propertyName) {
        try {
            List metaProps = (List) getHibernateTemplate().execute(new HibernateCallback() {
                public List doInHibernate(Session session) throws HibernateException, SQLException {
                    String hql = "from PnProjectSpaceMetaProp where propertyName=:propertyName";
                    try {
                        Query query = session.createQuery(hql);
                        query.setString("propertyName", propertyName);
                        return query.list();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });

            if (metaProps.size() > 0)
                return (PnProjectSpaceMetaProp) metaProps.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
