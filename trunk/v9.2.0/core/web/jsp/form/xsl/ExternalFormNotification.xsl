<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    xmlns:url="xalan://net.project.notification.XSLFormatNotificationURL"
        extension-element-prefixes="display format url" >
	
<xsl:output method="text"/>
<xsl:template match="/">
<xsl:value-of select="display:get('prm.notification.form.externalformnotification.data_entered_for_form')"/><xsl:value-of disable-output-escaping="yes" select="notification/form_name"/>
<xsl:text>&#xa;</xsl:text>
    
<xsl:value-of disable-output-escaping="yes" select="notification/form_data"/>

</xsl:template>
</xsl:stylesheet>