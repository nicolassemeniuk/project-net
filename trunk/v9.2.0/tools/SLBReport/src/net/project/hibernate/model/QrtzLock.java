package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class QrtzLock implements Serializable {

    /** identifier field */
    private String lockName;

    /** full constructor */
    public QrtzLock(String lockName) {
        this.lockName = lockName;
    }

    /** default constructor */
    public QrtzLock() {
    }

    public String getLockName() {
        return this.lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("lockName", getLockName())
            .toString();
    }

}
