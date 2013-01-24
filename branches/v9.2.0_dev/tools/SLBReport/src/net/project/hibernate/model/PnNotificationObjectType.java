package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnNotificationObjectType implements Serializable {

    /** identifier field */
    private String objectType;

    /** nullable persistent field */
    private String displayName;

    /** nullable persistent field */
    private String isSubscribable;

    /** nullable persistent field */
    private net.project.hibernate.model.PnObjectType pnObjectType;

    /** persistent field */
    private Set pnNotificationTypes;

    /** full constructor */
    public PnNotificationObjectType(String objectType, String displayName, String isSubscribable, net.project.hibernate.model.PnObjectType pnObjectType, Set pnNotificationTypes) {
        this.objectType = objectType;
        this.displayName = displayName;
        this.isSubscribable = isSubscribable;
        this.pnObjectType = pnObjectType;
        this.pnNotificationTypes = pnNotificationTypes;
    }

    /** default constructor */
    public PnNotificationObjectType() {
    }

    /** minimal constructor */
    public PnNotificationObjectType(String objectType, Set pnNotificationTypes) {
        this.objectType = objectType;
        this.pnNotificationTypes = pnNotificationTypes;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIsSubscribable() {
        return this.isSubscribable;
    }

    public void setIsSubscribable(String isSubscribable) {
        this.isSubscribable = isSubscribable;
    }

    public net.project.hibernate.model.PnObjectType getPnObjectType() {
        return this.pnObjectType;
    }

    public void setPnObjectType(net.project.hibernate.model.PnObjectType pnObjectType) {
        this.pnObjectType = pnObjectType;
    }

    public Set getPnNotificationTypes() {
        return this.pnNotificationTypes;
    }

    public void setPnNotificationTypes(Set pnNotificationTypes) {
        this.pnNotificationTypes = pnNotificationTypes;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("objectType", getObjectType())
            .toString();
    }

}
