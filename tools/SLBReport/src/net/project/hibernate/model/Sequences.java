package net.project.hibernate.model;

import java.io.Serializable;

public class Sequences implements Serializable{
	
	private String sequenceName;
	
	private Integer lastNumber;

	public Integer getLastNumber() {
		return lastNumber;
	}

	public void setLastNumber(Integer lastNumber) {
		this.lastNumber = lastNumber;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	

}
