<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    extension-element-prefixes="url"
    >
<xsl:output method="text"/>
<xsl:template match="*|/"><xsl:apply-templates/></xsl:template>
<xsl:template match="text()|@*"><xsl:value-of select="."/></xsl:template>
<xsl:template match="DomainMigrationNotification">

<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="./user/full-name"/>,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.roster.domain_migration.complete_process')"/>
<xsl:text>&#xa;</xsl:text> <xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.domain_migration.new_domain_name')"/> <xsl:value-of disable-output-escaping="yes" select="./UserDomainMigrationManager/DomainMigration/target_domain_name"/>
<xsl:text>&#xa;</xsl:text> <xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.domain_migration.user_name')"/>  <xsl:value-of disable-output-escaping="yes" select="./user/login"/>
<xsl:text>&#xa;</xsl:text> <xsl:text>&#xa;</xsl:text>
<xsl:value-of select="display:get('prm.notification.roster.domain_migration.help')"/> <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/> <xsl:value-of select="display:get('prm.notification.roster.domain_migration.contact_admin')"/>.
<xsl:text>&#xa;</xsl:text>
</xsl:template>
</xsl:stylesheet>


