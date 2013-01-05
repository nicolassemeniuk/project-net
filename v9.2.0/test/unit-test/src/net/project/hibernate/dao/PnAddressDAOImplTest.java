package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnAddressDAO;
import net.project.hibernate.model.PnAddress;

public class PnAddressDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnAddressDAO pnAddressDAO;
	
	public PnAddressDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnAddressDAO.findByPimaryKey(Integer)
	 */
	public void testFindByPimaryKey(){
		Integer addressId = 566214; 
		try {
			PnAddress pnAddress = pnAddressDAO.findByPimaryKey(addressId);
			assertNotNull(pnAddress);
			assertTrue(StringUtils.isNotEmpty(pnAddress.getCity()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
