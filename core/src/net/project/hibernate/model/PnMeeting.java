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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * PnMeeting generated by hbm2java
 */
@Entity
@Table(name = "PN_MEETING")
public class PnMeeting implements java.io.Serializable {

	/** identifier field */
	private Integer meetingId;

	/** persistent field */
	private int nextAgendaItemSeq;

	/** nullable persistent field */
	private PnObject pnObject;

	private PnPerson pnPerson;

	private Set pnAgendaItems = new HashSet(0);

	private PnCalendarEvent pnCalendarEvent;

	public PnMeeting() {
	}

	public PnMeeting(Integer meetingId, int nextAgendaItemSeq) {
		this.meetingId = meetingId;
		this.nextAgendaItemSeq = nextAgendaItemSeq;
	}

	public PnMeeting(Integer meetingId, int nextAgendaItemSeq, PnObject pnObject, PnPerson pnPerson, Set pnAgendaItems,
			PnCalendarEvent pnCalendarEvent) {
		this.meetingId = meetingId;
		this.nextAgendaItemSeq = nextAgendaItemSeq;
		this.pnObject = pnObject;
		this.pnPerson = pnPerson;
		this.pnAgendaItems = pnAgendaItems;
		this.pnCalendarEvent = pnCalendarEvent;
	}

	/** minimal constructor */
	public PnMeeting(Integer meetingId, int nextAgendaItemSeq, net.project.hibernate.model.PnPerson pnPerson,
			net.project.hibernate.model.PnCalendarEvent pnCalendarEvent, Set pnAgendaItems) {
		this.meetingId = meetingId;
		this.nextAgendaItemSeq = nextAgendaItemSeq;
		this.pnPerson = pnPerson;
		this.pnCalendarEvent = pnCalendarEvent;
		this.pnAgendaItems = pnAgendaItems;
	}

	@Id
	@Column(name = "MEETING_ID", nullable = false)
	public Integer getMeetingId() {
		return this.meetingId;
	}

	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}

	@Column(name = "NEXT_AGENDA_ITEM_SEQ", nullable = false, length = 8)
	public int getNextAgendaItemSeq() {
		return this.nextAgendaItemSeq;
	}

	public void setNextAgendaItemSeq(int nextAgendaItemSeq) {
		this.nextAgendaItemSeq = nextAgendaItemSeq;
	}

	@OneToOne(fetch = FetchType.LAZY, targetEntity = PnObject.class)
	@JoinColumn(name = "MEETING_ID")
	public PnObject getPnObject() {
		return this.pnObject;
	}

	public void setPnObject(PnObject pnObject) {
		this.pnObject = pnObject;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnPerson.class)
	@JoinColumn(name = "HOST_ID")
	public PnPerson getPnPerson() {
		return this.pnPerson;
	}

	public void setPnPerson(PnPerson pnPerson) {
		this.pnPerson = pnPerson;
	}

	@OneToMany(fetch = FetchType.LAZY, targetEntity = PnAgendaItem.class)
	public Set getPnAgendaItems() {
		return this.pnAgendaItems;
	}

	public void setPnAgendaItems(Set pnAgendaItems) {
		this.pnAgendaItems = pnAgendaItems;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnCalendarEvent.class)
	@JoinColumn(name = "CALENDAR_EVENT_ID")
	public PnCalendarEvent getPnCalendarEvent() {
		return this.pnCalendarEvent;
	}

	public void setPnCalendarEvent(PnCalendarEvent pnCalendarEvent) {
		this.pnCalendarEvent = pnCalendarEvent;
	}

	public String toString() {
		return new ToStringBuilder(this).append("meetingId", getMeetingId()).toString();
	}

}
