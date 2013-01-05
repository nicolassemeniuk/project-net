/**
 * 
 */
package net.project.hibernate.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Ritesh S
 *
 */
@Entity
@Table(name = "PN_BUSINESS_HAS_VIEW")
public class PnBusinessHasView {

	private PnBusinessHasViewPK comp_id;
	
	private PnBusiness pnBusiness;
	
	private PnView pnView;

	public PnBusinessHasView() {
		super();
	}
	public PnBusinessHasView(PnBusinessHasViewPK comp_id) {
		super();
		this.comp_id = comp_id;
	}

	public PnBusinessHasView(PnView pnView) {
		super();
		this.pnView = pnView;
	}

	public PnBusinessHasView(PnBusiness pnBusiness) {
		super();
		this.pnBusiness = pnBusiness;
	}

	public PnBusinessHasView(PnBusinessHasViewPK comp_id, PnBusiness pnBusiness) {
		super();
		this.comp_id = comp_id;
		this.pnBusiness = pnBusiness;
	}
	
	public PnBusinessHasView(PnBusinessHasViewPK comp_id, PnBusiness pnBusiness ,PnView pnView) {
		super();
		this.comp_id = comp_id;
		this.pnBusiness = pnBusiness;
		this.pnView = pnView;
	}
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "businessId", column = @Column(name = "BUSINESS_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "viewId", column = @Column(name = "VIEW_ID", nullable = false, length = 20)) })
	public PnBusinessHasViewPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnBusinessHasViewPK comp_id) {
		this.comp_id = comp_id;
	}

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnView.class)
	@JoinColumn(name = "BUSINESS_ID", insertable = false, updatable = false)
	public PnBusiness getPnBusiness() {
		return pnBusiness;
	}
	
	public void setPnBusiness(PnBusiness pnBusiness) {
		this.pnBusiness = pnBusiness;
	}
	
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PnView.class)
	@JoinColumn(name = "VIEW_ID", insertable = false, updatable = false)
	public PnView getPnView() {
		return pnView;
	}
	
	public void setPnView(PnView pnView) {
		this.pnView = pnView;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof PnBusinessHasView))
			return false;
		PnBusinessHasView castOther = (PnBusinessHasView) other;
		return new EqualsBuilder().append(this.getComp_id(), castOther.getComp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getComp_id()).toHashCode();
	}
}
