package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnGlobalDomainPK implements Serializable {

    /** identifier field */
    private String tableName;

    /** identifier field */
    private String columnName;

    /** identifier field */
    private Integer code;

    /** full constructor */
    public PnGlobalDomainPK(String tableName, String columnName, Integer code) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.code = code;
    }

    /** default constructor */
    public PnGlobalDomainPK() {
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("tableName", getTableName())
            .append("columnName", getColumnName())
            .append("code", getCode())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof PnGlobalDomainPK) ) return false;
        PnGlobalDomainPK castOther = (PnGlobalDomainPK) other;
        return new EqualsBuilder()
            .append(this.getTableName(), castOther.getTableName())
            .append(this.getColumnName(), castOther.getColumnName())
            .append(this.getCode(), castOther.getCode())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getTableName())
            .append(getColumnName())
            .append(getCode())
            .toHashCode();
    }

}
