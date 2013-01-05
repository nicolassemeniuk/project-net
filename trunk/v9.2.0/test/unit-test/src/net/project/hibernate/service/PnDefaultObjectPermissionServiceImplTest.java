package net.project.hibernate.service;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import net.project.hibernate.dao.IPnDefaultObjectPermissionDAO;
import net.project.hibernate.model.PnDefaultObjectPermission;
import net.project.hibernate.model.PnDefaultObjectPermissionPK;
import net.project.hibernate.service.filters.IPnDefaultObjectPermissionFilter;
import net.project.hibernate.service.impl.PnDefaultObjectPermissionServiceImpl;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnDefaultObjectPermissionServiceImplTest extends TestCase{

    private PnDefaultObjectPermissionServiceImpl service;
    
    private IPnDefaultObjectPermissionDAO mockDao;
    
    /*public static final Integer GROUP_TYPE_USERDEFINED = new Integer(100);
    public static final Integer GROUP_TYPE_EVERYONE = new Integer(600);*/
    
	public PnDefaultObjectPermissionServiceImplTest() {
		super();
	}

	@BeforeMethod
	protected void setUp() throws Exception {
		mockDao = createStrictMock(IPnDefaultObjectPermissionDAO.class);
		service = new PnDefaultObjectPermissionServiceImpl();
		service.setPnDefaultObjectPermissionDAO(mockDao);
	}

	@AfterMethod
	protected void tearDown() throws Exception {

	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDefaultObjectPermissionServiceImpl#getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(Integer, String)
	 */
	public void testGetDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup() {
		 Integer spaceId = 477997;
		 String objectType = "activity";
		 
		 List<PnDefaultObjectPermission> permissionList = new ArrayList<PnDefaultObjectPermission>();
		 permissionList.add(new PnDefaultObjectPermission(new PnDefaultObjectPermissionPK(477997, "activity", 478000), 65535));
		 permissionList.add(new PnDefaultObjectPermission(new PnDefaultObjectPermissionPK(477997, "activity", 478001), 135));
		 
		 expect(mockDao.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType)).andReturn(permissionList);
		 replay(mockDao);
		 
		 List<PnDefaultObjectPermission> permissionList2 = service.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType);
		 assertEquals(477997, permissionList2.get(0).getComp_id().getSpaceId().intValue());
		 assertEquals(478001, permissionList2.get(1).getComp_id().getGroupId().intValue());
		 verify(mockDao);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDefaultObjectPermissionServiceImpl#getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(Integer, String)
	 */
	public void testGetDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroupWithEmptyList() {
		 Integer spaceId = 477997;
		 String objectType = "activity";
		 
		 List<PnDefaultObjectPermission> permissionList = new ArrayList<PnDefaultObjectPermission>();
		 
		 expect(mockDao.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType)).andReturn(permissionList);
		 replay(mockDao);
		 
		 List<PnDefaultObjectPermission> permissionList2 = service.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType);
		 assertNotNull(permissionList2);
		 verify(mockDao);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDefaultObjectPermissionServiceImpl#getAll()
	 */
	public void testGetAll() {
		 List<PnDefaultObjectPermission> permissionList = new ArrayList<PnDefaultObjectPermission>();
		 permissionList.add(new PnDefaultObjectPermission(new PnDefaultObjectPermissionPK(477997, "activity", 478000), 65535));
		 permissionList.add(new PnDefaultObjectPermission(new PnDefaultObjectPermissionPK(477997, "activity", 478001), 135));
		 
		 expect(mockDao.findAll()).andReturn(permissionList);
		 replay(mockDao);
		 
		 List<PnDefaultObjectPermission> permissionList2 = service.getAll();
		 assertEquals(477997, permissionList2.get(0).getComp_id().getSpaceId().intValue());
		 assertEquals(478001, permissionList2.get(1).getComp_id().getGroupId().intValue());
		 verify(mockDao);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnDefaultObjectPermissionServiceImpl#getAll()
	 */
	public void testGetAllWithEmptyList() {
		 List<PnDefaultObjectPermission> permissionList = new ArrayList<PnDefaultObjectPermission>();
		 
		 expect(mockDao.findAll()).andReturn(permissionList);
		 replay(mockDao);
		 
		 List<PnDefaultObjectPermission> permissionList2 = service.getAll();
		 assertNotNull(permissionList2);
		 verify(mockDao);
	}
}