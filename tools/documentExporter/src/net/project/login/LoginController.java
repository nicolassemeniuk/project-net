package net.project.login;


public class LoginController {

//	private final Logger logger = Logger.getLogger(getClass());
//
//	@Override
//	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
//		Map<String, String> model = new HashMap<String, String>();
//		try {
//			Login loginCommand = (Login) command;
//			boolean emptyFields = false;
//			if ("".equals(loginCommand.getUsername())) {
//				model.put("emptyUsername", "true");
//				emptyFields = true;
//			}
//			System.out.println(" a1. ");
//			// if password is empty report error
//			if ("".equals(loginCommand.getPassword())) {
//				model.put("emptyPassword", "true");
//				emptyFields = true;
//			}
//			System.out.println(" a2. ");
//			if (emptyFields) {
//				System.out.println(" a3. ");
//				return new ModelAndView("/tiles_login", "model", model);
//			}
//			System.out.println(" a4. ");
//			if (!"appadmin".equals(loginCommand.getUsername())) {
//				model.put("notAdmin", "true");
//				System.out.println(" a5. ");
//				return new ModelAndView("/tiles_login", "model", model);
//			}
//			System.out.println(" a6. ");
//			boolean login = checkPassword(loginCommand.getUsername(), loginCommand.getPassword());
//			System.out.println(" a7. login:" + login);
//			if (!login) {
//				System.out.println(" a8. ");
//				model.put("loginFailed", "true");
//				return new ModelAndView("/tiles_login", "model", model);
//			} else {
//				System.out.println(" a9. ");
//				request.getSession().setAttribute("user", loginCommand.getUsername());
//			}
//			System.out.println(" a10. ");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		System.out.println(" a11. ");
//		return new ModelAndView("/tiles_index", "model", model);
//	}
//
//	/**
//	 * checkPassword checks user details in login page
//	 * 
//	 * @param userName
//	 *            is username user provided in login page
//	 * @param password
//	 *            is password user provided in login page
//	 * @return <tt>true<tt> if user has provided correct username and password
//	 */
//	public boolean checkPassword(String userName, String password) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("checkPassword(" + userName + "," + password + ") starts...");
//		}
//		try {
//			ILoginUserService service = ServiceFactory.getInstance().getLoginUserService();
//			String databasePassword = service.getPasswordByUserName(userName);
//			// String databasePassword = getPasswordByUserName(userName);
//			if (databasePassword != null) {
//				if (databasePassword.equalsIgnoreCase(net.project.security.EncryptionManager.pbeEncrypt(password))) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("checkPassword(" + userName + "," + password + ")");
//					}
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (logger.isDebugEnabled()) {
//				logger.debug("checkPassword(" + userName + ", ******) has failed..." + e.getMessage());
//			}
//		}
//		if (logger.isDebugEnabled()) {
//			logger.debug("checkPassword(" + userName + ", ******) ends...");
//		}
//		return false;
//	}

}
