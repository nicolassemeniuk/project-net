package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnDocRepositoryBase implements Serializable {

    /** identifier field */
    private Integer repositoryId;

    /** persistent field */
    private String repositoryPath;

    /** persistent field */
    private int isActive;

    /** persistent field */
    private Set pnDocContentElements;

    /** full constructor */
    public PnDocRepositoryBase(Integer repositoryId, String repositoryPath, int isActive, Set pnDocContentElements) {
        this.repositoryId = repositoryId;
        this.repositoryPath = repositoryPath;
        this.isActive = isActive;
        this.pnDocContentElements = pnDocContentElements;
    }

    /** default constructor */
    public PnDocRepositoryBase() {
    }

    public Integer getRepositoryId() {
        return this.repositoryId;
    }

    public void setRepositoryId(Integer repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getRepositoryPath() {
        return this.repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public int getIsActive() {
        return this.isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Set getPnDocContentElements() {
        return this.pnDocContentElements;
    }

    public void setPnDocContentElements(Set pnDocContentElements) {
        this.pnDocContentElements = pnDocContentElements;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("repositoryId", getRepositoryId())
            .toString();
    }

}
