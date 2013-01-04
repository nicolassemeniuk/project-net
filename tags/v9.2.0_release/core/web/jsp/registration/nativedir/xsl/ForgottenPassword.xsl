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
<xsl:value-of select="display:get('prm.notification.dear')"/> <xsl:value-of select="forgotten-pswd/user/first-name"/><xsl:text> </xsl:text><xsl:value-of select="forgotten-pswd/user/last-name"/>,
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>

<xsl:value-of select="display:get('prm.notification.registration.forgotten.password.complete_reset_link')"/>
<xsl:text>&#xa;</xsl:text>
<xsl:value-of select="url:getAppURL()"/>/registration/nativedir/ForgottenPasswordWizard2.jsp?code=<xsl:value-of select="forgotten-pswd/verification-code"/><xsl:value-of disable-output-escaping="yes" select="$emailStr"/><xsl:value-of select="forgotten-pswd/user/email"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>
     
<xsl:value-of select="display:get('prm.notification.registration.forgotten.password.link_does_not_work')"/>
<xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>
    <xsl:value-of select="display:get('prm.notification.registration.forgotten.password.login_page')"/><xsl:value-of select="url:getAppURL()"/>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>    </xsl:text><xsl:value-of select="display:get('prm.notification.registration.regver.verification_code')"/><xsl:value-of select="forgotten-pswd/verification-code"/>
    <xsl:text>&#xa;</xsl:text><xsl:text>&#xa;</xsl:text>  

<xsl:value-of select="display:get('prm.notification.registration.forgotten.password.questions_contact_help')"/><xsl:value-of select="url:formatAppURL('/help/HelpDesk.jsp')"/>.

</xsl:template>
</xsl:stylesheet>


