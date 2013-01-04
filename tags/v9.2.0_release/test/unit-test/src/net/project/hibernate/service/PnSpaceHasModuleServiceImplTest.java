package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnSpaceHasModuleDAO;
import net.project.hibernate.model.PnModule;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;
import net.project.hibernate.service.impl.PnSpaceHasModuleServiceImpl;
import net.project.space.ISpaceTypes;
import junit.framework.TestCase;

public class PnSpaceHasModuleServiceImplTest extends TestCase{
	
	private PnSpaceHasModuleServiceImpl spaceHasModuleService;
	
	private IPnSpaceHasModuleDAO mockSpaceHasModuleDAO;
	
	public PnSpaceHasModuleServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		mockSpaceHasModuleDAO = createStrictMock(IPnSpaceHasModuleDAO.class);
		spaceHasModuleService = new PnSpaceHasModuleServiceImpl();
		spaceHasModuleService.setPnSpaceHasModuleDAO(mockSpaceHasModuleDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasModuleServiceImpl#findAll()
     */
    public final void testFindAll() {
    	List<PnSpaceHasModule> list = new ArrayList<PnSpaceHasModule>();
    	
    	PnSpaceHasModule pnSpaceHasModule = new PnSpaceHasModule();
    	pnSpaceHasModule.setIsActive(1);
    	PnModule pnModule = new PnModule();
    	pnModule.setModuleId(150);
    	pnModule.setName(ISpaceTypes.PROJECT_SPACE);
    	pnSpaceHasModule.setPnModule(pnModule);
    	
    	list.add(pnSpaceHasModule);
    	
    	PnSpaceHasModule pnSpaceHasModule2 = new PnSpaceHasModule();
    	pnSpaceHasModule2.setIsActive(1);
    	PnModule pnModule2 = new PnModule();
    	pnModule2.setModuleId(160);
    	pnModule2.setName(ISpaceTypes.PERSONAL_SPACE);
    	pnSpaceHasModule.setPnModule(pnModule2);
    	
    	list.add(pnSpaceHasModule2);
    	
    	expect(mockSpaceHasModuleDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasModuleDAO);
    	List<PnSpaceHasModule> spaceHasModules = spaceHasModuleService.findAll();
    	
    	assertEquals(2, spaceHasModules.size());
    	verify(mockSpaceHasModuleDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasModuleServiceImpl#findAll()
     */
    public final void testFindAllWithEmptyList() {
    	List<PnSpaceHasModule> list = new ArrayList<PnSpaceHasModule>();
    	
    	expect(mockSpaceHasModuleDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasModuleDAO);
    	List<PnSpaceHasModule> spaceHasModules = spaceHasModuleService.findAll();
    	
    	assertEquals(0, spaceHasModules.size());
    	verify(mockSpaceHasModuleDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasModuleServiceImpl#findBySpaceId()
     */
    public final void testFindBySpaceId() {
    	Integer spaceId = 1;
    	List<PnSpaceHasModule> list = new ArrayList<PnSpaceHasModule>();
    	
    	PnSpaceHasModule pnSpaceHasModule = new PnSpaceHasModule();
    	pnSpaceHasModule.setIsActive(1);
    	PnSpaceHasModulePK comp_id = new PnSpaceHasModulePK();
    	comp_id.setSpaceId(1);
    	comp_id.setModuleId(150);
    	pnSpaceHasModule.setComp_id(comp_id);
    	PnModule pnModule = new PnModule();
    	pnModule.setModuleId(150);
    	pnModule.setName(ISpaceTypes.PROJECT_SPACE);
    	pnSpaceHasModule.setPnModule(pnModule);
    	
    	list.add(pnSpaceHasModule);
    	
    	PnSpaceHasModule pnSpaceHasModule2 = new PnSpaceHasModule();
    	pnSpaceHasModule2.setIsActive(0);
    	PnSpaceHasModulePK comp_id2 = new PnSpaceHasModulePK();
    	comp_id2.setSpaceId(2);
    	comp_id2.setModuleId(160);
    	pnSpaceHasModule2.setComp_id(comp_id2);
    	PnModule pnModule2 = new PnModule();
    	pnModule2.setModuleId(150);
    	pnModule2.setName(ISpaceTypes.PERSONAL_SPACE);
    	pnSpaceHasModule2.setPnModule(pnModule2);
    	
    	list.add(pnSpaceHasModule2);
    	
    	expect(mockSpaceHasModuleDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasModuleDAO);
    	List<PnSpaceHasModule> spaceHasModules = spaceHasModuleService.findBySpaceId(spaceId);
    	assertEquals(1, spaceHasModules.size());
    	verify(mockSpaceHasModuleDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnSpaceHasModuleServiceImpl#findBySpaceId()
     */
    public final void testFindBySpaceIdWithEmptyList() {
    	Integer spaceId = 1;
    	List<PnSpaceHasModule> list = new ArrayList<PnSpaceHasModule>();
    	
    	expect(mockSpaceHasModuleDAO.findAll()).andReturn(list);
    	replay(mockSpaceHasModuleDAO);
    	List<PnSpaceHasModule> spaceHasModules = spaceHasModuleService.findBySpaceId(spaceId);
    	assertEquals(0, spaceHasModules.size());
    	verify(mockSpaceHasModuleDAO);
    }
}
