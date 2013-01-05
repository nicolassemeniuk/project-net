<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:display="xalan://net.project.base.property.PropertyProvider"
    xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format">

<xsl:output method="html"/>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates/>
</xsl:template>

  <xsl:template match="UserDomain">

		  <tr><th align="left" colspan="3"></th></tr>
          <tr>
            <th align="left" class="tableHeader">Domain Name:</th>
            <th align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left" class="tableContent"><xsl:value-of select="name" /></td>
          </tr>
          <tr>
            <th align="left" class="tableHeader">Description:</th>
            <th align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left" class="tableContent"><xsl:value-of select="description" /></td>
          </tr>
		    <tr>
            <th align="left" class="tableHeader">Number of Supported Users:</th>
            <th align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left" class="tableContent"><xsl:value-of select="./userCount"/></td>
          </tr>
          <tr>
            <th align="left" class="tableHeader">Directory &amp; Authentication Provider:</th>
            <th align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left" class="tableContent"><xsl:value-of select="./directoryProviderTypeName"/></td>
          </tr>
          <tr>
            <th align="left" class="tableHeader">Require verification of email address after registration:</th>
              <th align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left" class="tableContent">
                <xsl:choose>
                    <xsl:when test="isVerificationRequired='1'">
                        Yes
                    </xsl:when>
                    <xsl:otherwise>
                        No
                    </xsl:otherwise>
                </xsl:choose>
            </td>
          </tr>
          <tr>
              <th align="left" class="tableHeader">Domain Supports Credit Card Purchases:</th>
              <th align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
              <td align="left" class="tableContent">
                  <xsl:choose>
                      <xsl:when test="supportsCreditCards='1'">
                          Yes
                      </xsl:when>
                      <xsl:otherwise>
                          No
                      </xsl:otherwise>
                  </xsl:choose>
              </td>
          </tr>
          <tr>
            <th align="left" class="tableHeader" colspan="3">Registration Instructions:</th>
          </tr>
          <tr>
            <td align="left" class="tableContent" colspan="3">
                <xsl:choose>
                    <xsl:when test="registrationInstructions!=''">
                        <xsl:value-of select="format:formatText(registrationInstructions)" disable-output-escaping="yes"/>
                    </xsl:when>
                    <xsl:otherwise>
                        (None)
                    </xsl:otherwise>
                </xsl:choose>
            </td>
          </tr>

          <xsl:if test="registrationInstructions!=parsedRegistrationInstructions">
          <tr>
            <th align="left" class="tableHeader" colspan="3">Parsed Registration Instructions (Displayed to user):</th>
          </tr>
          <tr>
            <td align="left" class="tableContent" colspan="3">
                <xsl:choose>
                    <xsl:when test="parsedRegistrationInstructions!=''">
                        <xsl:value-of select="format:formatText(parsedRegistrationInstructions)" disable-output-escaping="yes"/>
                    </xsl:when>
                    <xsl:otherwise>
                        (None)
                    </xsl:otherwise>
                </xsl:choose>
            </td>
          </tr>
          </xsl:if>
		 <tr><th align="left" colspan="3"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th></tr>

	  <tr>
	  	<td colspan="3">
	        <table border="0" cellspacing="0" cellpadding="0" vspace="0" width="100%">
	  			<tr class="channelHeader">
		  			<td width="1%"><img src="../../images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
					 <td class="channelHeader">Supported Configurations</td>
					 <td width="1%" align="right"><img src="../../images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
				</tr>
			</table>
	  	</td>
	  </tr>

	  
		  <tr><th align="left" colspan="3"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th></tr>

          <tr>
		  	<td colspan="3">

  				<table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr class="tableHeader" align="left" valign="top">
						<td class="tableHeader">Configuration Name</td>
						<td class="tableHeader">Description</td>
					</tr>	
					<xsl:call-template name="DomainConfigurationCollection" />
				</table>
			</td>
		</tr>

  </xsl:template>





<xsl:template name="DomainConfigurationCollection">

	<xsl:for-each select="DomainConfigurationCollection/configurationSpace">

		<tr class="tableLine">

			<td colspan="2" class="tableLine">
				<xsl:element name="img">
					<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
					<xsl:attribute name="width">1</xsl:attribute>
					<xsl:attribute name="height">2</xsl:attribute>
					<xsl:attribute name="border">0</xsl:attribute>
				</xsl:element>
			</td>
		</tr>

		<tr align="left" valign="middle" class="tableContent">
		
			<td class="tableContent">
				<a href="{JSPRootURL}/configuration/Main.jsp?id={id}"><xsl:value-of select="name" /></a>
			</td>
            
			<td class="tableContent"><xsl:value-of select="description"/></td>

         </tr>

	<tr class="tableLine">
		<td colspan="2">
			<xsl:element name="img">
				<xsl:attribute name="src">../../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">1</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>
		
  </xsl:for-each>
</xsl:template>
	
</xsl:stylesheet>
