package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnJobDescriptionLookup implements Serializable {

    /** identifier field */
    private Integer jobDescriptionCode;

    /** persistent field */
    private String jobDescription;

    /** full constructor */
    public PnJobDescriptionLookup(Integer jobDescriptionCode, String jobDescription) {
        this.jobDescriptionCode = jobDescriptionCode;
        this.jobDescription = jobDescription;
    }

    /** default constructor */
    public PnJobDescriptionLookup() {
    }

    public Integer getJobDescriptionCode() {
        return this.jobDescriptionCode;
    }

    public void setJobDescriptionCode(Integer jobDescriptionCode) {
        this.jobDescriptionCode = jobDescriptionCode;
    }

    public String getJobDescription() {
        return this.jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("jobDescriptionCode", getJobDescriptionCode())
            .toString();
    }

}
