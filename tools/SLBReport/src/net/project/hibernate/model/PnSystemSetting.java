package net.project.hibernate.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 *        Provides system settings irrespective of configuration and across all instances of a web application.
 *     
*/
public class PnSystemSetting implements Serializable {

    /** identifier field */
    private String name;

    /** nullable persistent field */
    private String value;

    /** full constructor */
    public PnSystemSetting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /** default constructor */
    public PnSystemSetting() {
    }

    /** minimal constructor */
    public PnSystemSetting(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("name", getName())
            .toString();
    }

}
