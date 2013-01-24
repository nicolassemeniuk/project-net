package net.project.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pn_user")
public class PnUser implements Serializable {
	
    public PnUser() {
	}

	/** identifier field */
    private Integer userId;

    /** persistent field */
    private String username;
    
    /** identifier field */
    private Integer domainId;

	@Id
	@Column(name = "user_id", unique = true, nullable = false, length = 20)
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Column(name = "username", nullable = false, length = 32)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "domain_id", nullable = false, length = 20)
	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
    
    
    
}
