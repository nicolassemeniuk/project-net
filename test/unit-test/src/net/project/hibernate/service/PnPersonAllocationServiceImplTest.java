package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import net.project.hibernate.dao.IPnPersonAllocationDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPersonAllocation;
import net.project.hibernate.model.PnPersonAllocationPK;
import net.project.hibernate.service.impl.PnPersonAllocationServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PnPersonAllocationServiceImplTest extends TestCase{
	
	private PnPersonAllocationServiceImpl service;
	
	private IPnPersonAllocationDAO mockPersonAllocationDAO;	
	
	private IUtilService mockUtilService;
	
	private IPnObjectService mockObjectService;
	
	private List<PnPersonAllocation> allocations = new ArrayList<PnPersonAllocation>();

	@Before
	public void setUp() throws Exception {
		super.setUp();
		System.out.println("setUpDao for PnPersonAllocationServiceImplTest");
		mockPersonAllocationDAO = createMock(IPnPersonAllocationDAO.class);
		mockObjectService = createStrictMock(IPnObjectService.class);
		mockUtilService = createStrictMock(IUtilService.class);
		service = new PnPersonAllocationServiceImpl();
		service.setPnPersonAllocationDAO(mockPersonAllocationDAO);
		service.setUtilService(mockUtilService);
		service.setPnObjectService(mockObjectService);
		
		// creating mock objects
		PnPersonAllocation allocation = getAllocation(11001, 1, 22001, new BigDecimal(8), new Date());
		allocations.add(allocation);
		allocation = getAllocation(11002, 1, 22002, new BigDecimal(48), new Date());
		allocations.add(allocation);
		allocation = getAllocation(11003, 1, 22003, new BigDecimal(80), new Date());
		allocations.add(allocation);
	}
	
	private PnPersonAllocation getAllocation(Integer projectId, Integer personId, Integer allocationId, BigDecimal hoursAllocated, Date allocationDate){
		PnPerson person = new PnPerson(personId);
		PnPersonAllocationPK allocationPK = new PnPersonAllocationPK();
		allocationPK.setSpaceId(projectId);
		allocationPK.setPersonId(personId);
		allocationPK.setAllocationId(allocationId);
		PnPersonAllocation allocation = new PnPersonAllocation();
		allocation.setComp_id(allocationPK);
		allocation.setPnPerson(person);
		allocation.setHoursAllocated(hoursAllocated);
		allocation.setAllocationDate(allocationDate);
		return allocation;
	}
	
	@Test
	public void testGetResourceAllocationEntryByProject(){
		System.out.println("Executing testGetResourceAllocationEntryByProject()");		
		Integer businesId  = 101;
		Integer projectId = 11001;
		Date startDate = new Date();
		Date endDate = new Date();
		List list = new ArrayList();
		
		// set expected behaviour on dao
		expect(mockPersonAllocationDAO.getResourceAllocationEntryByProject(businesId, projectId, startDate, endDate)).andReturn(list);
		replay(mockPersonAllocationDAO);
		
		list = service.getResourceAllocationEntryByProject(businesId, projectId, startDate, endDate);
		verify(mockPersonAllocationDAO);
	}
	
	@Test
	public void testGetResourceAllocationEntryByPerson(){
		System.out.println("Testing getResourceAllocationEntryByPerson()");
		List list = new ArrayList();
		Integer businesId  = 101;
		Integer personId = 1;
		Date startDate = new Date();
		Date endDate = new Date();
		
		// set expected behaviour on dao
		expect(mockPersonAllocationDAO.getResourceAllocationEntryByPerson(businesId, personId, startDate, endDate)).andReturn(list);
		replay(mockPersonAllocationDAO);
		
		list = service.getResourceAllocationEntryByPerson(businesId, personId, startDate, endDate);
		verify(mockPersonAllocationDAO);
	}
	
	public void testSaveResourceAllocations() {
		System.out.println("Testing saveResourceAllocations()");
		List<PnPersonAllocation> newAllocations = new ArrayList<PnPersonAllocation>();
		newAllocations.add(getAllocation(11004, 1, 22004, new BigDecimal(120), new Date()));
		newAllocations.add(getAllocation(11005, 1, 22005, new BigDecimal(136), new Date()));
		
		// set expected behaviour on dao
		mockPersonAllocationDAO.saveResourceAllocations(allocations);
		replay(mockPersonAllocationDAO);
		
		allocations.addAll(newAllocations);
		
		service.saveResourceAllocations(allocations);
		assertEquals(5, allocations.size());		
		verify(mockPersonAllocationDAO);
		reset(mockPersonAllocationDAO);		
	}
	
	@Test
	public void testGetResourceAllocationDetails(){
		System.out.println("Testing getResourceAllocationDetails()");
		PnPersonAllocation pnPersonAllocation = new PnPersonAllocation();
		Integer resourceId  = 1;
		Integer projectId = 11001;
		Date startDate = new Date();
		Date endDate = new Date();
		
		// set expected behaviour on dao
		expect(mockPersonAllocationDAO.getResourceAllocationDetails(resourceId, projectId, startDate, endDate)).andReturn(allocations.get(0));
		replay(mockPersonAllocationDAO);
		
		pnPersonAllocation = service.getResourceAllocationDetails(resourceId, projectId, startDate, endDate);
		assertEquals(pnPersonAllocation.getComp_id().getPersonId(), resourceId);
		verify(mockPersonAllocationDAO);		
	}
	
	@Test
	public void testGetResourceAllocationSumary(){
		System.out.println("Testing getResourceAllocationSumary()");
		List resourceAllocationsummary = new ArrayList();
		Integer resourceId  = 1;
		Integer businessId = 201;
		Date startDate = new Date();
		Date endDate = new Date();
		
		// set expected behaviour on dao
		expect(mockPersonAllocationDAO.getResourceAllocationSumary(resourceId, businessId, startDate, endDate)).andReturn(resourceAllocationsummary);
		replay(mockPersonAllocationDAO);
		
		resourceAllocationsummary = service.getResourceAllocationSumary(resourceId, businessId, startDate, endDate);
		verify(mockPersonAllocationDAO);
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
