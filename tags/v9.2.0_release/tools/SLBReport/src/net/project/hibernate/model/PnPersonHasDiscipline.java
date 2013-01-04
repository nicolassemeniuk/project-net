package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnPersonHasDiscipline implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnPersonHasDisciplinePK comp_id;

    /** nullable persistent field */
    private String otherDiscipline;

    /** nullable persistent field */
    private net.project.hibernate.model.PnPerson pnPerson;

    /** nullable persistent field */
    private net.project.hibernate.model.PnDisciplineLookup pnDisciplineLookup;

    /** full constructor */
    public PnPersonHasDiscipline(net.project.hibernate.model.PnPersonHasDisciplinePK comp_id, String otherDiscipline, net.project.hibernate.model.PnPerson pnPerson, net.project.hibernate.model.PnDisciplineLookup pnDisciplineLookup) {
        this.comp_id = comp_id;
        this.otherDiscipline = otherDiscipline;
        this.pnPerson = pnPerson;
        this.pnDisciplineLookup = pnDisciplineLookup;
    }

    /** default constructor */
    public PnPersonHasDiscipline() {
    }

    /** minimal constructor */
    public PnPersonHasDiscipline(net.project.hibernate.model.PnPersonHasDisciplinePK comp_id) {
        this.comp_id = comp_id;
    }

    public net.project.hibernate.model.PnPersonHasDisciplinePK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnPersonHasDisciplinePK comp_id) {
        this.comp_id = comp_id;
    }

    public String getOtherDiscipline() {
        return this.otherDiscipline;
    }

    public void setOtherDiscipline(String otherDiscipline) {
        this.otherDiscipline = otherDiscipline;
    }

    public net.project.hibernate.model.PnPerson getPnPerson() {
        return this.pnPerson;
    }

    public void setPnPerson(net.project.hibernate.model.PnPerson pnPerson) {
        this.pnPerson = pnPerson;
    }

    public net.project.hibernate.model.PnDisciplineLookup getPnDisciplineLookup() {
        return this.pnDisciplineLookup;
    }

    public void setPnDisciplineLookup(net.project.hibernate.model.PnDisciplineLookup pnDisciplineLookup) {
        this.pnDisciplineLookup = pnDisciplineLookup;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnPersonHasDiscipline) ) return false;
        PnPersonHasDiscipline castOther = (PnPersonHasDiscipline) other;
        return new EqualsBuilder()
            .append(this.getComp_id(), castOther.getComp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getComp_id())
            .toHashCode();
    }

}
