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
package net.project.hibernate.dao.impl;

import java.util.Iterator;

import net.project.hibernate.dao.IPnDocumentDAO;
import net.project.hibernate.model.PnDocument;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Transactional 
@Repository 
public class PnDocumentDAOImpl extends AbstractHibernateAnnotatedDAO<PnDocument, Integer> implements IPnDocumentDAO {
	
	private static Logger log = Logger.getLogger(PnDocumentDAOImpl.class);
	
    public PnDocumentDAOImpl() {
        super(PnDocument.class);
    }
    
    public PnDocument getDocumentDetailsById(Integer docId){
    	PnDocument document = null;
		String sql = "SELECT d.docId, d.docName, d.recordStatus FROM PnDocument d WHERE d.docId = :docId ";
		try {
			Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(sql);
			query.setInteger("docId", docId);
			Iterator result = query.list().iterator();
			while(result.hasNext()){
				Object[] row = (Object[]) result.next();
				document = new PnDocument();
				document.setDocId((Integer) row[0]);
				document.setDocName((String) row[1]);
				document.setRecordStatus((String) row[2]);
			}			
		} catch (Exception e) {
			log.error("Error occurred while getting document details by id : " + e.getMessage());
		}
		return document;
    }
}
