<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 	xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="text" />

<xsl:template match="LicenseNotification">
Hello <xsl:value-of select="person/full_name"/>,
	
This is to inform you that you are associated to the following license for using Project.net.
Please contact the responsible user of this license if you have any questions. Provide the following license information in your communications.

<xsl:value-of select="Message"/>
	
You can also see your current license information by navigating to Personal Space --> Setup --> Personal Profile --> License
	
<xsl:apply-templates select="License" />
</xsl:template>


<xsl:template match="License">
    License Information 
    ===================
	<xsl:choose>
<xsl:when test="LicenseStatus='200'">
    This license has been disabled by the administrator.</xsl:when>
<xsl:when test="LicenseStatus='300'">
    This license has been cancelled by the administrator.</xsl:when>
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
	  
    <xsl:apply-templates select="LicenseCertificate" />
</xsl:template>

<xsl:template match="LicenseCertificate">
	    <xsl:apply-templates select="LicenseModelCollection" />
</xsl:template>

<xsl:template match="LicenseModelCollection">
  <xsl:apply-templates select="TimeLimit"/>
</xsl:template>

<xsl:template match="LicenseModelCollection/TimeLimit">
    Create/Modify Date:        
        <xsl:call-template name="Date">
            <xsl:with-param name="date" select="StartDate" />
        </xsl:call-template>
    End Date:
       <xsl:call-template name="Date">
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
