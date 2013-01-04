package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnClassList implements Serializable {

    /** identifier field */
    private net.project.hibernate.model.PnClassListPK comp_id;

    /** nullable persistent field */
    private String listName;

    /** nullable persistent field */
    private Integer fieldCnt;

    /** nullable persistent field */
    private String listDesc;

    /** persistent field */
    private int isShared;

    /** persistent field */
    private int isAdmin;

    /** persistent field */
    private BigDecimal ownerSpaceId;

    /** nullable persistent field */
    private Date crc;

    /** persistent field */
    private String recordStatus;

    /** nullable persistent field */
    private net.project.hibernate.model.PnClass pnClass;

    /** persistent field */
    private Set pnSpaceHasClassLists;

    /** persistent field */
    private Set pnClassListFields;

    /** full constructor */
    public PnClassList(net.project.hibernate.model.PnClassListPK comp_id, String listName, Integer fieldCnt, String listDesc, int isShared, int isAdmin, BigDecimal ownerSpaceId, Date crc, String recordStatus, net.project.hibernate.model.PnClass pnClass, Set pnSpaceHasClassLists, Set pnClassListFields) {
        this.comp_id = comp_id;
        this.listName = listName;
        this.fieldCnt = fieldCnt;
        this.listDesc = listDesc;
        this.isShared = isShared;
        this.isAdmin = isAdmin;
        this.ownerSpaceId = ownerSpaceId;
        this.crc = crc;
        this.recordStatus = recordStatus;
        this.pnClass = pnClass;
        this.pnSpaceHasClassLists = pnSpaceHasClassLists;
        this.pnClassListFields = pnClassListFields;
    }

    /** default constructor */
    public PnClassList() {
    }

    /** minimal constructor */
    public PnClassList(net.project.hibernate.model.PnClassListPK comp_id, int isShared, int isAdmin, BigDecimal ownerSpaceId, String recordStatus, Set pnSpaceHasClassLists, Set pnClassListFields) {
        this.comp_id = comp_id;
        this.isShared = isShared;
        this.isAdmin = isAdmin;
        this.ownerSpaceId = ownerSpaceId;
        this.recordStatus = recordStatus;
        this.pnSpaceHasClassLists = pnSpaceHasClassLists;
        this.pnClassListFields = pnClassListFields;
    }

    public net.project.hibernate.model.PnClassListPK getComp_id() {
        return this.comp_id;
    }

    public void setComp_id(net.project.hibernate.model.PnClassListPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getListName() {
        return this.listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public Integer getFieldCnt() {
        return this.fieldCnt;
    }

    public void setFieldCnt(Integer fieldCnt) {
        this.fieldCnt = fieldCnt;
    }

    public String getListDesc() {
        return this.listDesc;
    }

    public void setListDesc(String listDesc) {
        this.listDesc = listDesc;
    }

    public int getIsShared() {
        return this.isShared;
    }

    public void setIsShared(int isShared) {
        this.isShared = isShared;
    }

    public int getIsAdmin() {
        return this.isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public BigDecimal getOwnerSpaceId() {
        return this.ownerSpaceId;
    }

    public void setOwnerSpaceId(BigDecimal ownerSpaceId) {
        this.ownerSpaceId = ownerSpaceId;
    }

    public Date getCrc() {
        return this.crc;
    }

    public void setCrc(Date crc) {
        this.crc = crc;
    }

    public String getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public net.project.hibernate.model.PnClass getPnClass() {
        return this.pnClass;
    }

    public void setPnClass(net.project.hibernate.model.PnClass pnClass) {
        this.pnClass = pnClass;
    }

    public Set getPnSpaceHasClassLists() {
        return this.pnSpaceHasClassLists;
    }

    public void setPnSpaceHasClassLists(Set pnSpaceHasClassLists) {
        this.pnSpaceHasClassLists = pnSpaceHasClassLists;
    }

    public Set getPnClassListFields() {
        return this.pnClassListFields;
    }

    public void setPnClassListFields(Set pnClassListFields) {
        this.pnClassListFields = pnClassListFields;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getComp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnClassList) ) return false;
        PnClassList castOther = (PnClassList) other;
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
