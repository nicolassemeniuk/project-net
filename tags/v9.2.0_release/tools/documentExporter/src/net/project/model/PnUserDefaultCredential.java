package net.project.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pn_user_default_credentials")
public class PnUserDefaultCredential implements Serializable {
	
	public PnUserDefaultCredential() {
	}

	/** identifier field */
    private Integer userId;

    /** identifier field */
    private Integer domainId;
    
    private String password;
    
	@Column(name = "user_id", nullable = false, length = 20)
    public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "domain_id", nullable = false, length = 20)
	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	@Id
	@Column(name = "password", nullable = false, length = 240)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
