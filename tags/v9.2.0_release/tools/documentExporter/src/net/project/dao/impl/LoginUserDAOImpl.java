package net.project.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.project.dao.ILoginUserDAO;
import net.project.model.PnUser;

@Transactional
@Repository
public class LoginUserDAOImpl extends AbstractHibernateDAO<PnUser, Integer> implements ILoginUserDAO {

	public LoginUserDAOImpl() {
		super(PnUser.class);
	}

	@SuppressWarnings("unchecked")
	public Map getPasswordByUserName(String userName) {
		Map result = new HashMap();
		try {
			String sql = "select udc.password as password, u.userId from PnUser u, PnUserDefaultCredential udc "
					+ "where u.userId = udc.userId and u.domainId = udc.domainId and u.username = :userName ";
			List results = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql).setString("userName", userName).list();
			if (results != null && results.size() > 0) {
				Object[] obj = (Object[])results.get(0);
				result.put("password", (String)obj[0]);
				result.put("userId", (Integer)obj[1]);
			} else {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
