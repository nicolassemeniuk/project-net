package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnStateLookup implements Serializable {

    /** identifier field */
    private String stateCode;

    /** persistent field */
    private String stateName;

    /** persistent field */
    private net.project.hibernate.model.PnCountryLookup pnCountryLookup;

    /** persistent field */
    private Set pnSpaceHasWeathers;

    /** persistent field */
    private Set pnPersonHasStateRegs;

    /** full constructor */
    public PnStateLookup(String stateCode, String stateName, net.project.hibernate.model.PnCountryLookup pnCountryLookup, Set pnSpaceHasWeathers, Set pnPersonHasStateRegs) {
        this.stateCode = stateCode;
        this.stateName = stateName;
        this.pnCountryLookup = pnCountryLookup;
        this.pnSpaceHasWeathers = pnSpaceHasWeathers;
        this.pnPersonHasStateRegs = pnPersonHasStateRegs;
    }

    /** default constructor */
    public PnStateLookup() {
    }

    public String getStateCode() {
        return this.stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return this.stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public net.project.hibernate.model.PnCountryLookup getPnCountryLookup() {
        return this.pnCountryLookup;
    }

    public void setPnCountryLookup(net.project.hibernate.model.PnCountryLookup pnCountryLookup) {
        this.pnCountryLookup = pnCountryLookup;
    }

    public Set getPnSpaceHasWeathers() {
        return this.pnSpaceHasWeathers;
    }

    public void setPnSpaceHasWeathers(Set pnSpaceHasWeathers) {
        this.pnSpaceHasWeathers = pnSpaceHasWeathers;
    }

    public Set getPnPersonHasStateRegs() {
        return this.pnPersonHasStateRegs;
    }

    public void setPnPersonHasStateRegs(Set pnPersonHasStateRegs) {
        this.pnPersonHasStateRegs = pnPersonHasStateRegs;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("stateCode", getStateCode())
            .toString();
    }

}
