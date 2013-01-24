<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="text" />

<xsl:template match="LicenseNotification">
Hello,
	
You are invited to join the following license for using Project.net.
You can see and switch your current license by navigating to Personal Space --> Setup --> Personal Profile --> License

<xsl:value-of select="Message"/>

<xsl:apply-templates select="License" />
</xsl:template>


<xsl:template match="License">
    License Information 
    ===================
	<xsl:choose>
          	<xsl:when test="LicenseStatus='200'">This license has been disabled by the administrator. </xsl:when>
			<xsl:when test="LicenseStatus='300'">This license has been cancelled by the administrator.</xsl:when>
            <xsl:otherwise> </xsl:otherwise>
   	</xsl:choose>
	<xsl:choose>
	<xsl:when test="ResponsibleUser=''"> 
    Responsible User: None </xsl:when>
	<xsl:otherwise>	
    Responsible User: <xsl:value-of select="ResponsibleUser" /></xsl:otherwise>
	</xsl:choose>	
	<xsl:choose>
	<xsl:when test="ResponsibleUserEmail=''"> 
    Responsible User Email: None </xsl:when>
	<xsl:otherwise>	
    Responsible User Email: <xsl:value-of select="ResponsibleUserEmail" /></xsl:otherwise>
	</xsl:choose>
    <xsl:choose>
    <xsl:when test="IsTrial='1'">
    Trial License: Yes </xsl:when>
    <xsl:otherwise> 
    Trial License: No </xsl:otherwise>
    </xsl:choose>
    License Key: <xsl:value-of select="LicenseKey/DisplayString" />
	
   	<xsl:apply-templates select="LicenseCertificate/LicenseModelCollection/TimeLimit" />

</xsl:template>

<xsl:template match="LicenseCertificate/LicenseModelCollection/TimeLimit">
    End Date:<xsl:call-template name="Date">
          <xsl:with-param name="date" select="EndDate" />
       </xsl:call-template>
    Days Remaining: <xsl:value-of select="DaysRemaining" />
    Original Time Limit(Days): <xsl:value-of select="DayDuration" />
</xsl:template>

<xsl:template name="Date">
    <xsl:param name="date" />
    <xsl:choose>
        <xsl:when test="$date">
            <xsl:value-of select="format:formatISODateTime($date)" />
        </xsl:when>
        <xsl:otherwise>
         </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
