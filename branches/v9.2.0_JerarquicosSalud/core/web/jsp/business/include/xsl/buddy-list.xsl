<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
  	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >

<xsl:output method="html"/>

<!-- Build translation properties node list -->
<xsl:variable name="translation" 
    select="/ProjectXML/Properties/Translation/property" />
<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />
<xsl:variable name="securedConnection" select="//buddy_list/securedConnection" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="buddy_list">
	<table cellpadding="1" cellspacing="1" width="100%" border="0" name="tableWithEvenRowsWOH">
		<xsl:apply-templates select="user"/>
	</table>
</xsl:template>

<xsl:template match="buddy_list/user">
	<tr>
		<xsl:choose>
			<xsl:when test="display:getBoolean('prm.global.skype.isenabled')">
				<td class="tableContent" NOWRAP="1" width="10">
					<xsl:choose>
						<xsl:when test="skype != ''">
							<xsl:element name="a">
								<xsl:attribute name="href">skype:<xsl:value-of select="skype"/>?chat</xsl:attribute>
									<xsl:element name="img">
										<xsl:choose>
										<xsl:when test="$securedConnection = 'false'">
											<xsl:attribute name="src">http://mystatus.skype.com/smallicon/<xsl:value-of select="skype"/></xsl:attribute>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="src">../images/skype/SkypeBlue_16X16.png</xsl:attribute>
										</xsl:otherwise>
					            		</xsl:choose>
										<xsl:attribute name="style">border: none;</xsl:attribute>
										<xsl:attribute name="width">16</xsl:attribute>
										<xsl:attribute name="height">16</xsl:attribute>
										<xsl:attribute name="title">My Status</xsl:attribute>
									</xsl:element>
							</xsl:element>
						</xsl:when>
					</xsl:choose>
				</td>
			</xsl:when>
		</xsl:choose>
		<td class="tableContent" NOWRAP="1">
		<xsl:choose>
		<xsl:when test="./activity_status != 'Active'">
			<a class="user" href="{$JSPRootURL}/blog/view/{user-id}/{user-id}/person/140?module=140">
				<xsl:value-of select="./full-name"/>
            </a>
		</xsl:when>	
		<xsl:otherwise>
		  	<a class="user-online" href="{$JSPRootURL}/blog/view/{user-id}/{user-id}/person/140?module=140">
		 		<xsl:value-of select="./full-name"/>
            </a>
		</xsl:otherwise>
		</xsl:choose>			
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet>
