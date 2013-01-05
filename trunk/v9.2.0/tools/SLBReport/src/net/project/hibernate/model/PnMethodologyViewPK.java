package net.project.hibernate.model;

import java.io.Serializable;

import java.sql.Clob;
import java.util.Date;

public class PnMethodologyViewPK implements Serializable {
	
    /** identifier field */
    private Integer methodologyId;

    /** identifier field */
    private Integer parentSpaceId;

    /** identifier field */
    private Integer childSpaceId;
    
    public PnMethodologyViewPK(Integer methodologyId, Integer parentSpaceId, Integer childSpaceId) {
        this.methodologyId = methodologyId;
        this.parentSpaceId = parentSpaceId;
        this.childSpaceId = childSpaceId;
    }
    
    /** default constructor */
    public PnMethodologyViewPK() {
    }
    
    public Integer getMethodologyId() {
        return this.methodologyId;
    }

    public void setMethodologyId(Integer methodologyId) {
        this.methodologyId = methodologyId;
    }

    public Integer getParentSpaceId() {
        return this.parentSpaceId;
    }

    public void setParentSpaceId(Integer parentSpaceId) {
        this.parentSpaceId = parentSpaceId;
    }

    public Integer getChildSpaceId() {
        return this.childSpaceId;
    }

    public void setChildSpaceId(Integer childSpaceId) {
        this.childSpaceId = childSpaceId;
    }

}
