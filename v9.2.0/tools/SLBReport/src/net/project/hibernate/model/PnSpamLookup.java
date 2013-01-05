package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnSpamLookup implements Serializable {

    /** identifier field */
    private Integer spamTypeCode;

    /** persistent field */
    private String spamType;

    /** persistent field */
    private Set pnPersonPicksSpams;

    /** full constructor */
    public PnSpamLookup(Integer spamTypeCode, String spamType, Set pnPersonPicksSpams) {
        this.spamTypeCode = spamTypeCode;
        this.spamType = spamType;
        this.pnPersonPicksSpams = pnPersonPicksSpams;
    }

    /** default constructor */
    public PnSpamLookup() {
    }

    public Integer getSpamTypeCode() {
        return this.spamTypeCode;
    }

    public void setSpamTypeCode(Integer spamTypeCode) {
        this.spamTypeCode = spamTypeCode;
    }

    public String getSpamType() {
        return this.spamType;
    }

    public void setSpamType(String spamType) {
        this.spamType = spamType;
    }

    public Set getPnPersonPicksSpams() {
        return this.pnPersonPicksSpams;
    }

    public void setPnPersonPicksSpams(Set pnPersonPicksSpams) {
        this.pnPersonPicksSpams = pnPersonPicksSpams;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("spamTypeCode", getSpamTypeCode())
            .toString();
    }

}
