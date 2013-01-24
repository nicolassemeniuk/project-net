package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNextDocRepository implements Serializable {

    /** identifier field */
    private Integer repositorySequence;

    /** full constructor */
    public PnNextDocRepository(Integer repositorySequence) {
        this.repositorySequence = repositorySequence;
    }

    /** default constructor */
    public PnNextDocRepository() {
    }

    public Integer getRepositorySequence() {
        return this.repositorySequence;
    }

    public void setRepositorySequence(Integer repositorySequence) {
        this.repositorySequence = repositorySequence;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("repositorySequence", getRepositorySequence())
            .toString();
    }

}
