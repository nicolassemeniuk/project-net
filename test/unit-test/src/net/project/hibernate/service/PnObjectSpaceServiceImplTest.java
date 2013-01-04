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
import net.project.hibernate.dao.IPnObjectSpaceDAO;
import net.project.hibernate.model.PnObjectSpace;
import net.project.hibernate.model.PnObjectSpacePK;
import net.project.hibernate.service.impl.PnObjectSpaceServiceImpl;

public class PnObjectSpaceServiceImplTest extends TestCase {
	
	private PnObjectSpaceServiceImpl service;
	
    private IPnObjectSpaceDAO mockSpaceDAO;
    
    public PnObjectSpaceServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceDAO = createMock(IPnObjectSpaceDAO.class);
		service = new PnObjectSpaceServiceImpl();
		service.setSpaceDAO(mockSpaceDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/* 
	 * Test method for
     * @see net.project.hibernate.service.IPnObjectSpaceService#getByFilter(net.project.hibernate.service.filters.IPnObjectSpaceServiceFilter)
     */
    public final void testGetByFilter() {
    	List<PnObjectSpace> list = new ArrayList<PnObjectSpace>();
    	list.add(new PnObjectSpace(new PnObjectSpacePK(477115, 477997)));
    	expect(mockSpaceDAO.findAll()).andReturn(list);
    	replay(mockSpaceDAO);
        list = mockSpaceDAO.findAll();
        assertEquals(1, list.size());
        verify(mockSpaceDAO);
    }
    
    /* 
     * Test method for
     * @see net.project.hibernate.service.IPnObjectSpaceService#save(net.project.hibernate.model.PnObjectSpace)
     */
    public final void testSave() {
    	PnObjectSpacePK pnObjectSpacePK = new PnObjectSpacePK(477115, 477997);
    	PnObjectSpace pnObjectSpace = new PnObjectSpace(pnObjectSpacePK);
    	expect(mockSpaceDAO.create(pnObjectSpace)).andReturn(pnObjectSpacePK);
    	replay(mockSpaceDAO);
    	pnObjectSpacePK = mockSpaceDAO.create(pnObjectSpace);
    	assertEquals(pnObjectSpacePK, pnObjectSpace.getComp_id());
    	verify(mockSpaceDAO);
    }
}
