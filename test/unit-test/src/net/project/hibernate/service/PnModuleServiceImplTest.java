package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;
import net.project.hibernate.service.impl.PnModuleServiceImpl;
import net.project.space.ISpaceTypes;
import junit.framework.TestCase;

public class PnModuleServiceImplTest extends TestCase{
	
	private PnModuleServiceImpl moduleService;
	
	private IPnModuleDAO mockModuleDAO;
	
	public PnModuleServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockModuleDAO = createMock(IPnModuleDAO.class);
		moduleService = new PnModuleServiceImpl();
		moduleService.setPnModuleDAO(mockModuleDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnModuleServiceImpl#getModuleIds(PnPlan)
     */
    public final void testGetModuleIds(){
    	List<PnModule> list = new ArrayList<PnModule>();
    	
    	PnModule pnModule = new PnModule();
    	pnModule.setModuleId(1);
    	pnModule.setName(ISpaceTypes.PERSONAL_SPACE);
    	
    	list.add(pnModule);
    	
    	PnModule pnModule2 = new PnModule();
    	pnModule2.setModuleId(2);
    	pnModule2.setName(ISpaceTypes.PROJECT_SPACE);
    	
    	list.add(pnModule2);
    	
    	expect(mockModuleDAO.getModuleIds()).andReturn(list);
    	replay(mockModuleDAO);
    	List<PnModule> modules = moduleService.getModuleIds();
    	assertEquals(2, modules.size());
    	verify(mockModuleDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnModuleServiceImpl#getModuleIds(PnPlan)
     */
    public final void testGetModuleIdsWithNoModules(){
    	List<PnModule> list = new ArrayList<PnModule>();
    	expect(mockModuleDAO.getModuleIds()).andReturn(list);
    	replay(mockModuleDAO);
    	List<PnModule> modules = moduleService.getModuleIds();
    	assertEquals(0, modules.size());
    	verify(mockModuleDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnModuleServiceImpl#getModuleDefaultPermissions(Integer)
     */
    public final void testGetModuleDefaultPermissions(){
    	Integer spaceId = Integer.valueOf(1);
    	List<PnModule> list = new ArrayList<PnModule>();
    	
    	PnModule pnModule = new PnModule();
    	pnModule.setModuleId(1);
    	pnModule.setName(ISpaceTypes.PERSONAL_SPACE);
    	PnSpaceHasModule spaceHasModule = new PnSpaceHasModule();
    	PnSpaceHasModulePK comp_id = new PnSpaceHasModulePK();
    	comp_id.setSpaceId(1);
    	spaceHasModule.setComp_id(comp_id);
    	Set<PnSpaceHasModule> pnSpaceHasModules = new HashSet<PnSpaceHasModule>();
    	pnSpaceHasModules.add(spaceHasModule);
    	pnModule.setPnSpaceHasModules(pnSpaceHasModules);
    	
    	list.add(pnModule);
    	
    	expect(mockModuleDAO.getModuleDefaultPermissions(spaceId)).andReturn(list);
    	replay(mockModuleDAO);
    	List<PnModule> modules = moduleService.getModuleDefaultPermissions(spaceId);
    	assertEquals(1, modules.size());
    	verify(mockModuleDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnModuleServiceImpl#getModuleDefaultPermissions(Integer)
     */
    public final void testGetModuleDefaultPermissionsWithNoModules(){
    	Integer spaceId = Integer.valueOf(1);
    	List<PnModule> list = new ArrayList<PnModule>();
    	expect(mockModuleDAO.getModuleDefaultPermissions(spaceId)).andReturn(list);
    	replay(mockModuleDAO);
    	List<PnModule> modules = moduleService.getModuleDefaultPermissions(spaceId);
    	assertEquals(0, modules.size());
    	verify(mockModuleDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnModuleServiceImpl#getModulesForSpace(Integer)
     */
    public final void testGetModulesForSpace(){
    	Integer spaceId = Integer.valueOf(1);
    	List<PnModule> list = new ArrayList<PnModule>();
    	
    	PnModule pnModule = new PnModule();
    	pnModule.setModuleId(1);
    	pnModule.setName(ISpaceTypes.PERSONAL_SPACE);
    	PnSpaceHasModule spaceHasModule = new PnSpaceHasModule();
    	PnSpaceHasModulePK comp_id = new PnSpaceHasModulePK();
    	comp_id.setSpaceId(1);
    	spaceHasModule.setComp_id(comp_id);
    	Set<PnSpaceHasModule> pnSpaceHasModules = new HashSet<PnSpaceHasModule>();
    	pnSpaceHasModules.add(spaceHasModule);
    	pnModule.setPnSpaceHasModules(pnSpaceHasModules);
    	
    	list.add(pnModule);
    	
    	expect(mockModuleDAO.getModulesForSpace(spaceId)).andReturn(list);
    	replay(mockModuleDAO);
    	List<PnModule> modules = moduleService.getModulesForSpace(spaceId);
    	assertEquals(1, modules.size());
    	verify(mockModuleDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnModuleServiceImpl#getModulesForSpace(Integer)
     */
    public final void testGetModulesForSpaceWithNoModules(){
    	Integer spaceId = Integer.valueOf(1);
    	List<PnModule> list = new ArrayList<PnModule>();
    	expect(mockModuleDAO.getModulesForSpace(spaceId)).andReturn(list);
    	replay(mockModuleDAO);
    	List<PnModule> modules = moduleService.getModulesForSpace(spaceId);
    	assertEquals(0, modules.size());
    	verify(mockModuleDAO);
    }
}
