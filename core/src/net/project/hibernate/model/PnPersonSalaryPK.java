package net.project.hibernate.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class PnPersonSalaryPK implements Serializable {

	private Integer personId;

	public PnPersonSalaryPK() {
	}

	public PnPersonSalaryPK(Integer personId) {
		super();
		this.personId = personId;
	}

	@Column(name = "PERSON_ID", nullable = false, length = 20)
	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String toString() {
		return new ToStringBuilder(this).append("personId", getPersonId()).toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof PnSpaceHasPersonPK))
			return false;
		PnSpaceHasPersonPK castOther = (PnSpaceHasPersonPK) other;
		return new EqualsBuilder().append(this.getPersonId(), castOther.getPersonId()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getPersonId()).toHashCode();
	}

}
