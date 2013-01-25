package net.project.hibernate.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This Entity class models the association between a space and a material
 * 
 */

@Entity
@Table(name = "PN_SPACE_HAS_MATERIAL")
public class PnSpaceHasMaterial {

	private PnSpaceHasMaterialPK comp_id;

	private String recordStatus;

	public PnSpaceHasMaterial() {
	}

	public PnSpaceHasMaterial(PnSpaceHasMaterialPK comp_id, String recordStatus) {
		super();
		this.comp_id = comp_id;
		this.recordStatus = recordStatus;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "spaceId", column = @Column(name = "SPACE_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "materialId", column = @Column(name = "MATERIAL_ID", nullable = false, length = 20)) })
	public PnSpaceHasMaterialPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnSpaceHasMaterialPK comp_id) {
		this.comp_id = comp_id;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String toString() {
		return new ToStringBuilder(this).append("comp_id", getComp_id()).toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof PnSpaceHasMaterial))
			return false;
		PnSpaceHasMaterial castOther = (PnSpaceHasMaterial) other;
		return new EqualsBuilder().append(this.getComp_id(), castOther.getComp_id()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getComp_id()).toHashCode();
	}

}
