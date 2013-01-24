package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnZipcodeFeedLookup implements Serializable {

    /** identifier field */
    private String zipcode;

    /** persistent field */
    private String feed;

    /** persistent field */
    private Set pnSpaceHasWeathers;

    /** full constructor */
    public PnZipcodeFeedLookup(String zipcode, String feed, Set pnSpaceHasWeathers) {
        this.zipcode = zipcode;
        this.feed = feed;
        this.pnSpaceHasWeathers = pnSpaceHasWeathers;
    }

    /** default constructor */
    public PnZipcodeFeedLookup() {
    }

    public String getZipcode() {
        return this.zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getFeed() {
        return this.feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public Set getPnSpaceHasWeathers() {
        return this.pnSpaceHasWeathers;
    }

    public void setPnSpaceHasWeathers(Set pnSpaceHasWeathers) {
        this.pnSpaceHasWeathers = pnSpaceHasWeathers;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("zipcode", getZipcode())
            .toString();
    }

}
