package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO;
import net.project.hibernate.model.PnProjectSpaceMetaCombo;
import net.project.hibernate.model.PnProjectSpaceMetaComboPK;
import net.project.hibernate.model.PnProjectSpaceMetaProp;
import net.project.hibernate.model.PnProjectSpaceMetaValue;
import net.project.hibernate.service.impl.PnProjectSpaceMetaValueServiceImpl;
import junit.framework.TestCase;

public class PnProjectSpaceMetaValueServiceImplTest extends TestCase{
	
	private PnProjectSpaceMetaValueServiceImpl spaceMetaValueService;
	
	private IPnProjectSpaceMetaValueDAO mockProjectSpaceMetaValueDAO;
	
	public PnProjectSpaceMetaValueServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		mockProjectSpaceMetaValueDAO = createStrictMock(IPnProjectSpaceMetaValueDAO.class);
		spaceMetaValueService = new PnProjectSpaceMetaValueServiceImpl();
		spaceMetaValueService.setPnProjectSpaceMetaValueDAO(mockProjectSpaceMetaValueDAO);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	

	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaValueServiceImpl#getMetaValuesByProjectId(Integer)
     */
    public final void testGetMetaValuesByProjectId() {
    	Integer projectId = 1;
    	List<PnProjectSpaceMetaValue> list = new ArrayList<PnProjectSpaceMetaValue>();
    	
    	PnProjectSpaceMetaValue pnProjectSpaceMetaValue = new PnProjectSpaceMetaValue();
    	PnProjectSpaceMetaProp pnProjectSpaceMetaProp = new PnProjectSpaceMetaProp();
    	pnProjectSpaceMetaProp.setPropertyId(1);
    	pnProjectSpaceMetaProp.setPropertyName("Test Property");
    	pnProjectSpaceMetaValue.setPnProjectSpaceMetaProp(pnProjectSpaceMetaProp);
    	
    	list.add(pnProjectSpaceMetaValue);
    	
    	expect(mockProjectSpaceMetaValueDAO.getMetaValuesByProjectId(projectId)).andReturn(list);
    	replay(mockProjectSpaceMetaValueDAO);
    	List<PnProjectSpaceMetaValue> metaValues = spaceMetaValueService.getMetaValuesByProjectId(projectId);
    	assertEquals(1, metaValues.size());
    	verify(mockProjectSpaceMetaValueDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaValueServiceImpl#getMetaValuesByProjectId(Integer)
     */
    public final void testGetMetaValuesByProjectIdWithEmptyList() {
    	Integer projectId = 1;
    	List<PnProjectSpaceMetaValue> list = new ArrayList<PnProjectSpaceMetaValue>();
    	expect(mockProjectSpaceMetaValueDAO.getMetaValuesByProjectId(projectId)).andReturn(list);
    	replay(mockProjectSpaceMetaValueDAO);
    	List<PnProjectSpaceMetaValue> metaValues = spaceMetaValueService.getMetaValuesByProjectId(projectId);
    	assertEquals(0, metaValues.size());
    	verify(mockProjectSpaceMetaValueDAO);
    }
    
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaValueServiceImpl#getMetaValueByProjectAndPropertyId(Integer, Integer)
     */
    public final void testGetMetaValueByProjectAndPropertyId() {
    	Integer projectId = 11;
    	Integer propertyId = 1;
    	PnProjectSpaceMetaValue pnProjectSpaceMetaValue = new PnProjectSpaceMetaValue();
    	
    	PnProjectSpaceMetaProp pnProjectSpaceMetaProp = new PnProjectSpaceMetaProp();
    	pnProjectSpaceMetaProp.setPropertyId(1);
    	pnProjectSpaceMetaProp.setPropertyName("Test Property");
    	pnProjectSpaceMetaValue.setPnProjectSpaceMetaProp(pnProjectSpaceMetaProp);
    	
    	expect(mockProjectSpaceMetaValueDAO.getMetaValueByProjectAndPropertyId(projectId, propertyId)).andReturn(pnProjectSpaceMetaValue);
    	replay(mockProjectSpaceMetaValueDAO);
    	PnProjectSpaceMetaValue projectSpaceMetaValue = spaceMetaValueService.getMetaValueByProjectAndPropertyId(projectId, propertyId);
    	// Test for the meta property values
    	assertEquals(1, projectSpaceMetaValue.getPnProjectSpaceMetaProp().getPropertyId().intValue());
    	assertEquals("Test Property", projectSpaceMetaValue.getPnProjectSpaceMetaProp().getPropertyName());
    	
    	verify(mockProjectSpaceMetaValueDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaValueServiceImpl#getMetaValueByProjectAndPropertyId(Integer, Integer)
     */
    public final void testGetMetaValueByProjectAndPropertyIdWithEmptyPropery() {
    	Integer projectId = 11;
    	Integer propertyId = 1;
    	PnProjectSpaceMetaValue pnProjectSpaceMetaValue = new PnProjectSpaceMetaValue();
    	
    	expect(mockProjectSpaceMetaValueDAO.getMetaValueByProjectAndPropertyId(projectId, propertyId)).andReturn(pnProjectSpaceMetaValue);
    	replay(mockProjectSpaceMetaValueDAO);
    	PnProjectSpaceMetaValue projectSpaceMetaValue = spaceMetaValueService.getMetaValueByProjectAndPropertyId(projectId, propertyId);
    	// Test for the meta property values
    	assertEquals(null, projectSpaceMetaValue.getPnProjectSpaceMetaProp());
    	
    	verify(mockProjectSpaceMetaValueDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaValueServiceImpl#getValuesOptionListForProperty(Integer)
     */
    public final void testGetValuesOptionListForProperty() {
    	List<PnProjectSpaceMetaCombo> list = new ArrayList<PnProjectSpaceMetaCombo>();
    	
    	PnProjectSpaceMetaCombo pnProjectSpaceMetaCombo = new PnProjectSpaceMetaCombo();
    	PnProjectSpaceMetaComboPK comp_id = new PnProjectSpaceMetaComboPK();
    	comp_id.setComboValue("Test Property");
    	comp_id.setPropertyId(1);
    	pnProjectSpaceMetaCombo.setComboLabel("Project Meta Properties");
    	pnProjectSpaceMetaCombo.setComp_id(comp_id);
    	
    	list.add(pnProjectSpaceMetaCombo);
    	
    	expect(mockProjectSpaceMetaValueDAO.getValuesOptionListForProperty(1)).andReturn(list);
    	replay(mockProjectSpaceMetaValueDAO);
    	List<PnProjectSpaceMetaCombo> comboValues = spaceMetaValueService.getValuesOptionListForProperty(1);
    	assertEquals(1, comboValues.size());
    	verify(mockProjectSpaceMetaValueDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnProjectSpaceMetaValueServiceImpl#getValuesOptionListForProperty(Integer)
     */
    public final void testGetValuesOptionListForPropertyWithEmptyList() {
    	Integer propertyId = 1;
    	List<PnProjectSpaceMetaCombo> list = new ArrayList<PnProjectSpaceMetaCombo>();
    	
    	expect(mockProjectSpaceMetaValueDAO.getValuesOptionListForProperty(propertyId)).andReturn(list);
    	replay(mockProjectSpaceMetaValueDAO);
    	List<PnProjectSpaceMetaCombo> comboValues = spaceMetaValueService.getValuesOptionListForProperty(propertyId);
    	assertEquals(0, comboValues.size());
    	verify(mockProjectSpaceMetaValueDAO);
    }
}
