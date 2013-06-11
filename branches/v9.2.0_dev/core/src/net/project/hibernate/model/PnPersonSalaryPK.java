package net.project.hibernate.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Embeddable
public class PnPersonSalaryPK implements Serializable {

	private Integer personSalaryId;

	public PnPersonSalaryPK() {
	}

	public PnPersonSalaryPK(Integer personSalaryId) {
		super();
		this.personSalaryId = personSalaryId;
	}

	@Column(name = "PERSON_SALARY_ID", nullable = false, length = 20)
	public Integer getPersonSalaryId() {
		return this.personSalaryId;
	}

	public void setPersonSalaryId(Integer personSalaryId) {
		this.personSalaryId = personSalaryId;
	}

	public String toString() {
		return new ToStringBuilder(this).append("personSalaryId", getPersonSalaryId()).toString();
	}

	public boolean equals(Object other) {
		if (!(other instanceof PnSpaceHasPersonPK))
			return false;
		PnSpaceHasPersonPK castOther = (PnSpaceHasPersonPK) other;
		return new EqualsBuilder().append(this.getPersonSalaryId(), castOther.getPersonId()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getPersonSalaryId()).toHashCode();
	}

}
