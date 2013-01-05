/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.project.hibernate.dao.IPnObjectLinkDAO;
import net.project.hibernate.model.PnObjectLink;
import net.project.hibernate.model.PnObjectLinkPK;
import net.project.hibernate.service.impl.PnObjectLinkServiceImpl;

public class PnObjectLinkServiceImplTest extends TestCase {
	
    private PnObjectLinkServiceImpl service;
    
    private IPnObjectLinkDAO mockObjectLinkDAO;
    
	public PnObjectLinkServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockObjectLinkDAO = createMock(IPnObjectLinkDAO.class);
		service = new PnObjectLinkServiceImpl();
		service.setPnObjectLinkDAO(mockObjectLinkDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectLinkService#findByFilter(net.project.hibernate.service.filters.IPnObjectLinkFilter)
	 */
	public final void testFindByFilter() {
        List<PnObjectLink> result = new ArrayList<PnObjectLink>();
        result.add(new PnObjectLink(new PnObjectLinkPK(333, 444, 3131)));
        result.add(new PnObjectLink(new PnObjectLinkPK(331, 442, 2121)));       
        
        List<PnObjectLink> all = new ArrayList<PnObjectLink>();
        all.add(new PnObjectLink(new PnObjectLinkPK(333, 444, 3131)));
        //added new PnObjectLink to search in all list 
        all.add(new PnObjectLink(new PnObjectLinkPK(331, 442, 2121)));
        
        expect(mockObjectLinkDAO.findAll()).andReturn(all);
        replay(mockObjectLinkDAO);
        all = mockObjectLinkDAO.findAll();
        assertEquals(2, all.size());
        verify(mockObjectLinkDAO);
    }

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectLinkService#getObjectsById(java.lang.Integer, java.lang.Integer)
	 */
	public final void testGetObjectsById() {
		List<PnObjectLink> list = new ArrayList<PnObjectLink>();
		list.add(new PnObjectLink(new PnObjectLinkPK(333, 444, 3131)));
		Integer parentId =  333;
		Integer contextId = 3131;		
		expect(mockObjectLinkDAO.getObjectsById(parentId, contextId)).andReturn(list);
		replay(mockObjectLinkDAO);
		list = mockObjectLinkDAO.getObjectsById(parentId, contextId);
		assertEquals(1, list.size());
		verify(mockObjectLinkDAO);
	}
	
	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectLinkService#getObjectLinksByParent(java.lang.Integer)
	 */
	public final void testGetObjectLinksByParent() {
		List<PnObjectLink> list = new ArrayList<PnObjectLink>();
		list.add(new PnObjectLink(new PnObjectLinkPK(333, 444, 3131)));
		Integer fromObjectId = 333;
		expect(mockObjectLinkDAO.getObjectLinksByParent(fromObjectId)).andReturn(list);
		replay(mockObjectLinkDAO);
		list = mockObjectLinkDAO.getObjectLinksByParent(fromObjectId);
		assertEquals(1, list.size());
		verify(mockObjectLinkDAO);
	}
	
	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectLinkService#getObjectLinksByChild(java.lang.Integer)
	 */
	public final void testGetObjectLinksByChild(){
		List<PnObjectLink> list = new ArrayList<PnObjectLink>();
		list.add(new PnObjectLink(new PnObjectLinkPK(333, 444, 3131)));
		Integer toObjectId = 444;
		expect(mockObjectLinkDAO.getObjectLinksByChild(toObjectId)).andReturn(list);
		replay(mockObjectLinkDAO);
		list = mockObjectLinkDAO.getObjectLinksByChild(toObjectId);
		assertEquals(1, list.size());
		verify(mockObjectLinkDAO);
	}
	
}
