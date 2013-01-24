package net.project.hibernate.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** @author Hibernate CodeGenerator */
public class PnLdapDirectoryConfig implements Serializable {

    /** identifier field */
    private BigDecimal contextId;

    /** nullable persistent field */
    private String hostnameValues;

    /** nullable persistent field */
    private String secureHostnameValues;

    /** nullable persistent field */
    private Integer isUseSsl;

    /** nullable persistent field */
    private String searchBaseDn;

    /** nullable persistent field */
    private String searchTypeId;

    /** nullable persistent field */
    private String usernameAttributeName;

    /** nullable persistent field */
    private String searchSubtrees;

    /** nullable persistent field */
    private String nonAuthAccessTypeId;

    /** nullable persistent field */
    private String specificUserRelativeDn;

    /** nullable persistent field */
    private String specificUserPassword;

    /** nullable persistent field */
    private Integer isAvailableDirectorySearch;

    /** nullable persistent field */
    private String directorySearchDisplayName;

    /** nullable persistent field */
    private String searchFilterExpression;

    /** nullable persistent field */
    private Integer allowsAutomaticRegistration;

    /** persistent field */
    private Set pnLdapDirectoryAttrMaps;

    /** full constructor */
    public PnLdapDirectoryConfig(BigDecimal contextId, String hostnameValues, String secureHostnameValues, Integer isUseSsl, String searchBaseDn, String searchTypeId, String usernameAttributeName, String searchSubtrees, String nonAuthAccessTypeId, String specificUserRelativeDn, String specificUserPassword, Integer isAvailableDirectorySearch, String directorySearchDisplayName, String searchFilterExpression, Integer allowsAutomaticRegistration, Set pnLdapDirectoryAttrMaps) {
        this.contextId = contextId;
        this.hostnameValues = hostnameValues;
        this.secureHostnameValues = secureHostnameValues;
        this.isUseSsl = isUseSsl;
        this.searchBaseDn = searchBaseDn;
        this.searchTypeId = searchTypeId;
        this.usernameAttributeName = usernameAttributeName;
        this.searchSubtrees = searchSubtrees;
        this.nonAuthAccessTypeId = nonAuthAccessTypeId;
        this.specificUserRelativeDn = specificUserRelativeDn;
        this.specificUserPassword = specificUserPassword;
        this.isAvailableDirectorySearch = isAvailableDirectorySearch;
        this.directorySearchDisplayName = directorySearchDisplayName;
        this.searchFilterExpression = searchFilterExpression;
        this.allowsAutomaticRegistration = allowsAutomaticRegistration;
        this.pnLdapDirectoryAttrMaps = pnLdapDirectoryAttrMaps;
    }

    /** default constructor */
    public PnLdapDirectoryConfig() {
    }

    /** minimal constructor */
    public PnLdapDirectoryConfig(BigDecimal contextId, Set pnLdapDirectoryAttrMaps) {
        this.contextId = contextId;
        this.pnLdapDirectoryAttrMaps = pnLdapDirectoryAttrMaps;
    }

    public BigDecimal getContextId() {
        return this.contextId;
    }

    public void setContextId(BigDecimal contextId) {
        this.contextId = contextId;
    }

    public String getHostnameValues() {
        return this.hostnameValues;
    }

    public void setHostnameValues(String hostnameValues) {
        this.hostnameValues = hostnameValues;
    }

    public String getSecureHostnameValues() {
        return this.secureHostnameValues;
    }

    public void setSecureHostnameValues(String secureHostnameValues) {
        this.secureHostnameValues = secureHostnameValues;
    }

    public Integer getIsUseSsl() {
        return this.isUseSsl;
    }

    public void setIsUseSsl(Integer isUseSsl) {
        this.isUseSsl = isUseSsl;
    }

    public String getSearchBaseDn() {
        return this.searchBaseDn;
    }

    public void setSearchBaseDn(String searchBaseDn) {
        this.searchBaseDn = searchBaseDn;
    }

    public String getSearchTypeId() {
        return this.searchTypeId;
    }

    public void setSearchTypeId(String searchTypeId) {
        this.searchTypeId = searchTypeId;
    }

    public String getUsernameAttributeName() {
        return this.usernameAttributeName;
    }

    public void setUsernameAttributeName(String usernameAttributeName) {
        this.usernameAttributeName = usernameAttributeName;
    }

    public String getSearchSubtrees() {
        return this.searchSubtrees;
    }

    public void setSearchSubtrees(String searchSubtrees) {
        this.searchSubtrees = searchSubtrees;
    }

    public String getNonAuthAccessTypeId() {
        return this.nonAuthAccessTypeId;
    }

    public void setNonAuthAccessTypeId(String nonAuthAccessTypeId) {
        this.nonAuthAccessTypeId = nonAuthAccessTypeId;
    }

    public String getSpecificUserRelativeDn() {
        return this.specificUserRelativeDn;
    }

    public void setSpecificUserRelativeDn(String specificUserRelativeDn) {
        this.specificUserRelativeDn = specificUserRelativeDn;
    }

    public String getSpecificUserPassword() {
        return this.specificUserPassword;
    }

    public void setSpecificUserPassword(String specificUserPassword) {
        this.specificUserPassword = specificUserPassword;
    }

    public Integer getIsAvailableDirectorySearch() {
        return this.isAvailableDirectorySearch;
    }

    public void setIsAvailableDirectorySearch(Integer isAvailableDirectorySearch) {
        this.isAvailableDirectorySearch = isAvailableDirectorySearch;
    }

    public String getDirectorySearchDisplayName() {
        return this.directorySearchDisplayName;
    }

    public void setDirectorySearchDisplayName(String directorySearchDisplayName) {
        this.directorySearchDisplayName = directorySearchDisplayName;
    }

    public String getSearchFilterExpression() {
        return this.searchFilterExpression;
    }

    public void setSearchFilterExpression(String searchFilterExpression) {
        this.searchFilterExpression = searchFilterExpression;
    }

    public Integer getAllowsAutomaticRegistration() {
        return this.allowsAutomaticRegistration;
    }

    public void setAllowsAutomaticRegistration(Integer allowsAutomaticRegistration) {
        this.allowsAutomaticRegistration = allowsAutomaticRegistration;
    }

    public Set getPnLdapDirectoryAttrMaps() {
        return this.pnLdapDirectoryAttrMaps;
    }

    public void setPnLdapDirectoryAttrMaps(Set pnLdapDirectoryAttrMaps) {
        this.pnLdapDirectoryAttrMaps = pnLdapDirectoryAttrMaps;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("contextId", getContextId())
            .toString();
    }

}
