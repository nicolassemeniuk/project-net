<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
    extension-element-prefixes="url">
<xsl:output method="text" />
<xsl:template match="/">
<xsl:variable name="emailStr">
	<xsl:text disable-output-escaping="yes">&amp;email=</xsl:text>
</xsl:variable>
<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="forgotten-login/user/first-name"/><xsl:text> </xsl:text><xsl:value-of select="forgotten-login/user/last-name"/>,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.registration.forgotten.login.reminder')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

    <xsl:value-of select="display:get('prm.notification.registration.forgotten.login.name')"/><xsl:value-of select="forgotten-login/user/login"/>
    <xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>
        

<xsl:value-of select="display:get('prm.notification.registration.forgotten.login.questions_contact_help')"/> <xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>.

</xsl:template>
</xsl:stylesheet>


