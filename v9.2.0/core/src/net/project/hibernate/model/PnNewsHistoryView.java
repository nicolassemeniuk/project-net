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
package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnNewsHistoryView generated by hbm2java
 */
@Entity
@Table(name = "PN_NEWS_HISTORY_VIEW")
public class PnNewsHistoryView implements Serializable {

	private PnNewsHistoryViewPK comp_id;

	public PnNewsHistoryView() {
	}

	public PnNewsHistoryView(PnNewsHistoryViewPK comp_id) {
		this.comp_id = comp_id;
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "newsId", column = @Column(name = "NEWS_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "topic", column = @Column(name = "TOPIC", nullable = false, length = 80)),
			@AttributeOverride(name = "newsHistoryId", column = @Column(name = "NEWS_HISTORY_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "action", column = @Column(name = "ACTION", nullable = false, length = 80)),
			@AttributeOverride(name = "actionName", column = @Column(name = "ACTION_NAME", nullable = false, length = 80)),
			@AttributeOverride(name = "actionById", column = @Column(name = "ACTION_BY_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "actionBy", column = @Column(name = "ACTION_BY", nullable = false, length = 7)),
			@AttributeOverride(name = "actionDate", column = @Column(name = "ACTION_DATE", nullable = false, length = 7)),
			@AttributeOverride(name = "actionComment", column = @Column(name = "ACTION_COMMENT", nullable = false)) })
	public PnNewsHistoryViewPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnNewsHistoryViewPK comp_id) {
		this.comp_id = comp_id;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

}
