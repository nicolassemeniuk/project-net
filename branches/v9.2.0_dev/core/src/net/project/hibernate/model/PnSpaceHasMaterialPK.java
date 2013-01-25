package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class PnSpaceHasMaterialPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer spaceId;

	private Integer materialId;

	public PnSpaceHasMaterialPK(){		
	}
	
	public PnSpaceHasMaterialPK(Integer spaceId, Integer materialId) {
		super();
		this.spaceId = spaceId;
		this.materialId = materialId;
	}
	
	@Column(name = "SPACE_ID", nullable = false, length = 20)
	public Integer getSpaceId() {
		return this.spaceId;
	}

	public void setSpaceId(Integer spaceId) {
		this.spaceId = spaceId;
	}

	@Column(name = "MATERIAL_ID", nullable = false, length = 20)
	public Integer getMaterialId() {
		return this.materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}
	
	public String toString() {
		return new ToStringBuilder(this).append("spaceId", getSpaceId()).append("materialId", getMaterialId()).toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof PnSpaceHasMaterialPK))
			return false;
		PnSpaceHasMaterialPK castOther = (PnSpaceHasMaterialPK) other;
		return new EqualsBuilder().append(this.getSpaceId(), castOther.getSpaceId()).append(this.getMaterialId(),
				castOther.getMaterialId()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getSpaceId()).append(getMaterialId()).toHashCode();
	}
	
	
}
