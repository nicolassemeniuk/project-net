package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnJobDescriptionFeedback implements Serializable {

    /** identifier field */
    private String otherJobDescription;

    /** full constructor */
    public PnJobDescriptionFeedback(String otherJobDescription) {
        this.otherJobDescription = otherJobDescription;
    }

    /** default constructor */
    public PnJobDescriptionFeedback() {
    }

    public String getOtherJobDescription() {
        return this.otherJobDescription;
    }

    public void setOtherJobDescription(String otherJobDescription) {
        this.otherJobDescription = otherJobDescription;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("otherJobDescription", getOtherJobDescription())
            .toString();
    }

}
