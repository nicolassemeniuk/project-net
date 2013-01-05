package net.project.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.dao.ILoginUserDAO;
import net.project.service.ILoginService;

@Service(value = "loginService")
public class LoginServiceImpl implements ILoginService {

	@Autowired
	private ILoginUserDAO loginUserDAO;

	private final Logger logger = Logger.getLogger(getClass());

	public Integer loginUser(String username, String password) {
		return checkPassword(username, password);
	}

	private Integer checkPassword(String userName, String password) {
		if (logger.isDebugEnabled()) {
			logger.debug("checkPassword(" + userName + "," + password + ") starts...");
		}
		try {
			Map result = loginUserDAO.getPasswordByUserName(userName);
			String databasePassword = (String)result.get("password");
			if (databasePassword != null) {
				if (databasePassword.equalsIgnoreCase(net.project.security.EncryptionManager.pbeEncrypt(password))) {
					if (logger.isDebugEnabled()) {
						logger.debug("checkPassword(" + userName + "," + password + ")");
					}
					return (Integer)result.get("userId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isDebugEnabled()) {
				logger.debug("checkPassword(" + userName + ", ******) has failed..." + e.getMessage());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("checkPassword(" + userName + ", ******) ends...");
		}
		return 0;
	}

}
