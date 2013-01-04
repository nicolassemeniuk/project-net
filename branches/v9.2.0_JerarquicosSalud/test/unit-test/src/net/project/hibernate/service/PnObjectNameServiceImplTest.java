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
import junit.framework.TestCase;
import net.project.hibernate.dao.IPnObjectNameDAO;
import net.project.hibernate.service.impl.PnObjectNameServiceImpl;

import org.apache.commons.lang.StringUtils;

public class PnObjectNameServiceImplTest extends TestCase{

    private PnObjectNameServiceImpl service;
    
    private IPnObjectNameDAO mockObjectNameDAO;
    
	public PnObjectNameServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockObjectNameDAO = createMock(IPnObjectNameDAO.class);
		service = new PnObjectNameServiceImpl();
		service.setPnObjectNameDAO(mockObjectNameDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/*
	 * Test Method for  
     * @see net.project.hibernate.service.IPnObjectNameService#getNameFofObject(java.lang.Integer)
     */
	public final void testGetNameFofObject() {
		Integer objectId = 477997;
		String name = StringUtils.EMPTY;
		expect(mockObjectNameDAO.getNameFofObject(objectId)).andReturn(name);
		replay(mockObjectNameDAO);
		name = mockObjectNameDAO.getNameFofObject(objectId);
		verify(mockObjectNameDAO);		
    }
	
}