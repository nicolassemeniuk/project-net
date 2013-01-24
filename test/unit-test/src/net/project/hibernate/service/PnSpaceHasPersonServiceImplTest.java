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
import net.project.hibernate.dao.IPnSpaceHasPersonDAO;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.service.impl.PnSpaceHasPersonServiceImpl;

public class PnSpaceHasPersonServiceImplTest extends TestCase {
		
	private PnSpaceHasPersonServiceImpl service;
    
    private IPnSpaceHasPersonDAO mockSpaceHasPersonDAO;
    
	public PnSpaceHasPersonServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockSpaceHasPersonDAO = createMock(IPnSpaceHasPersonDAO.class);
		service = new PnSpaceHasPersonServiceImpl();
		service.setPnSpaceHasPersonDAO(mockSpaceHasPersonDAO);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnSpaceHasPersonService#getPnSpaceHasPersonBySecureKey(java.lang.String)
	 */
	public final void testGetPnSpaceHasPersonBySecureKey() {
		final String secureKey = "3F10564319E769BC50C439D437743F03";
		PnSpaceHasPerson spaceHasPerson = new PnSpaceHasPerson(); 
		expect(mockSpaceHasPersonDAO.getPnSpaceHasPersonBySecureKey(secureKey)).andReturn(spaceHasPerson);
		replay(mockSpaceHasPersonDAO);
		spaceHasPerson = mockSpaceHasPersonDAO.getPnSpaceHasPersonBySecureKey(secureKey);
		verify(mockSpaceHasPersonDAO);
	}
	
	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnSpaceHasPersonService#getSpaceHasPersonByProjectandPerson(java.lang.Integer[], java.lang.Integer)
	 */
	public final void testGetSpaceHasPersonByProjectandPerson() {
		List<PnSpaceHasPerson> list = new ArrayList<PnSpaceHasPerson>();
		list.add(new PnSpaceHasPerson(new PnSpaceHasPersonPK(), "A"));
		Integer[] spaceIds = {477997};
		Integer personId = 1;
		expect(mockSpaceHasPersonDAO.getSpaceHasPersonByProjectandPerson(spaceIds, personId)).andReturn(list);
		replay(mockSpaceHasPersonDAO);
		list = mockSpaceHasPersonDAO.getSpaceHasPersonByProjectandPerson(spaceIds, personId);
		verify(mockSpaceHasPersonDAO);
	}

	/* 
	 * Test method for
	 * @see net.project.hibernate.service.IPnSpaceHasPersonService#doesPersonExistsInSpace(java.lang.Integer, java.lang.Integer)
	 */
	public final void testDoesPersonExistsInSpace() {
		boolean result = true;
		Integer personId = 1;
		Integer spaceId = 47797;
		expect(mockSpaceHasPersonDAO.doesPersonExistsInSpace(personId, spaceId)).andReturn(result);
		replay(mockSpaceHasPersonDAO);
		result = mockSpaceHasPersonDAO.doesPersonExistsInSpace(personId, spaceId);
		verify(mockSpaceHasPersonDAO);
	}
}
