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
import net.project.hibernate.dao.IPnObjectDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.service.impl.PnObjectServiceImpl;

public class PnObjectServiceImplTest extends TestCase {

	private PnObjectServiceImpl service;
    
    private IPnObjectDAO mockObjectDAO;
    
	public PnObjectServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockObjectDAO = createMock(IPnObjectDAO.class);
		service = new PnObjectServiceImpl();
		service.setPnObjectDAO(mockObjectDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/* 
	 * Test method for
     * @see net.project.hibernate.service.IPnObjectService#generateNewId()
     */
    public final void testGenerateNewId() {
    	Integer objectId = new Integer(999999);
    	expect(mockObjectDAO.generateNewId()).andReturn(objectId);
    	replay(mockObjectDAO);
    	objectId = mockObjectDAO.generateNewId();
		verify(mockObjectDAO);
    }
    
    /*
     * Test method for
     * @see net.project.hibernate.service.IPnObjectService#getObjectByObjectId(java.lang.Integer)
     */
    public final void testGetObjectByObjectId(){
    	Integer objectId = 477997;
		PnObject object = new PnObject();
    	expect(mockObjectDAO.getObjectByObjectId(objectId)).andReturn(object);
    	replay(mockObjectDAO);
    	object = mockObjectDAO.getObjectByObjectId(objectId);
		verify(mockObjectDAO);
    }
    
}
