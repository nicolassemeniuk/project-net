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
import net.project.hibernate.dao.IPnObjectPermissionDAO;
import net.project.hibernate.model.PnObjectPermission;
import net.project.hibernate.model.PnObjectPermissionPK;
import net.project.hibernate.service.impl.PnObjectPermissionServiceImpl;

public class PnObjectPermissionServiceImplTest extends TestCase {
	
	private PnObjectPermissionServiceImpl service;

	private IPnObjectPermissionDAO mockObjectPermissionDAO;
	
	public PnObjectPermissionServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockObjectPermissionDAO = createMock(IPnObjectPermissionDAO.class);
		service = new PnObjectPermissionServiceImpl();
		service.setPnObjectPermissionDAO(mockObjectPermissionDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectPermissionService#getObjectPermission(net.project.hibernate.model.PnObjectPermissionPK)
	 */
	public final void tesGetObjectPermission() {
		PnObjectPermissionPK pnObjectPermissionId = new PnObjectPermissionPK(477997, 477998);
		PnObjectPermission objectPermission = new PnObjectPermission(pnObjectPermissionId, 65535);
		expect(mockObjectPermissionDAO.findByPimaryKey(pnObjectPermissionId)).andReturn(objectPermission);
		replay(mockObjectPermissionDAO);
		objectPermission = mockObjectPermissionDAO.findByPimaryKey(pnObjectPermissionId);
		assertEquals(pnObjectPermissionId, objectPermission.getComp_id());
		verify(mockObjectPermissionDAO);
	}

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectPermissionService#saveObjectPermission(net.project.hibernate.model.PnObjectPermission)
	 */
	public final void testSaveObjectPermission() {
		PnObjectPermissionPK pnObjectPermissionId = new PnObjectPermissionPK(477997, 477998);
		PnObjectPermission pnObjectPermission = new PnObjectPermission(pnObjectPermissionId, 65535);
		expect(mockObjectPermissionDAO.create(pnObjectPermission)).andReturn(pnObjectPermissionId);
		replay(mockObjectPermissionDAO);
		pnObjectPermissionId = mockObjectPermissionDAO.create(pnObjectPermission);
		assertEquals(pnObjectPermissionId, pnObjectPermission.getComp_id());
		verify(mockObjectPermissionDAO);
	}

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectPermissionService#getAll()
	 */
	public final void testGetAll() {
		List<PnObjectPermission> list = new ArrayList<PnObjectPermission>();
		list.add(new PnObjectPermission(new PnObjectPermissionPK(477997, 477998), 65535));
		expect(mockObjectPermissionDAO.findAll()).andReturn(list);
		replay(mockObjectPermissionDAO);
		list = mockObjectPermissionDAO.findAll();
		assertEquals(1, list.size());
		verify(mockObjectPermissionDAO);
	}

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectPermissionService#getObjectPermissionsForGroup(java.lang.Integer)
	 */
	public final void testGetObjectPermissionsForGroup() {
		List<PnObjectPermission> list = new ArrayList<PnObjectPermission>();
		list.add(new PnObjectPermission(new PnObjectPermissionPK(477997, 477998), 65535));
		Integer groupId = 477998;
		expect(mockObjectPermissionDAO.getObjectPermissionsForGroup(groupId)).andReturn(list);
		replay(mockObjectPermissionDAO);
		list = mockObjectPermissionDAO.getObjectPermissionsForGroup(groupId);
		assertEquals(1, list.size());
		verify(mockObjectPermissionDAO);
	}
	
	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnObjectPermissionService#getObjectPermissionsForObject(java.lang.Integer)
	 */
	public final void testGetObjectPermissionsForObject() {
		List<PnObjectPermission> list = new ArrayList<PnObjectPermission>();
		list.add(new PnObjectPermission(new PnObjectPermissionPK(477997, 477998), 65535));
		Integer objectId = 477997;
	    expect(mockObjectPermissionDAO.getObjectPermissionsForObject(objectId)).andReturn(list);
	    replay(mockObjectPermissionDAO);
	    list = mockObjectPermissionDAO.getObjectPermissionsForObject(objectId);
	    assertEquals(1, list.size());
	    verify(mockObjectPermissionDAO);	    
	}

}
