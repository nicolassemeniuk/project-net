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

<xsl:variable name="securedConnection" select="//roster/securedConnection" />

<xsl:template match="/">
    <xsl:apply-templates />
</xsl:template>

<xsl:template match="ProjectXML">
    <xsl:apply-templates select="Content" />
</xsl:template>

<xsl:template match="Content">
    <xsl:apply-templates select="roster" />
</xsl:template>
	
	<xsl:template match="roster">
		<table class="compactTable" style="overflow-x:auto">
		<tr class="table-header">
			<td class="over-table"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<xsl:choose>
				<xsl:when test="display:getBoolean('prm.global.skype.isenabled')">
					<td class="over-table" align="left"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
				</xsl:when>
			</xsl:choose>
			<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.roster.column.name')"/></td>
			<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.roster.column.costperhour')"/></td>
			<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.roster.column.officephone')"/></td>
			<td class="over-table" align="left"><xsl:value-of select="display:get('prm.directory.directorypage.roster.column.email')"/></td>
		</tr>
			<xsl:apply-templates select="person"/>		
		</table>
	</xsl:template>
	
  <xsl:template match="roster/person">

  		<xsl:variable name="invitee_status_id" select="InviteeStatus/id" />
		<xsl:variable name="invitee_status_name" select="InviteeStatus/name" />
		
       	<tr class="tableContent">
			<td class="tableContent" align="center">
				<input type="radio" name="selected" value="{person_id}" />
			</td>
			<xsl:choose>
				<xsl:when test="display:getBoolean('prm.global.skype.isenabled')">
					<td class="tableContent">
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
			<td class="tableContent">
			<xsl:choose>	
			<xsl:when test="$invitee_status_id != 'Deleted'">
				<!-- a name="projectMember" href="{$translation[@name='JSPRootURL']}/roster/PersonalDirectoryView.htm?module=140&amp;memberid={person_id}"><xsl:value-of select="full_name"/></a -->
				<a name="projectMember" href="{$translation[@name='JSPRootURL']}/blog/view/{person_id}/{person_id}/person/140?module=140"><xsl:value-of select="full_name"/></a>

			</xsl:when>
			<xsl:otherwise>
				<i><xsl:value-of select="full_name"/></i>
            </xsl:otherwise>
			</xsl:choose>			
			</td>
			
			<td class="tableContent"><xsl:value-of select="cost_by_hour"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			<td class="tableContent"><xsl:value-of select="Address/officePhone"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
			
			
			<td class="tableContent">

			<xsl:choose>	
			<xsl:when test="$invitee_status_id != 'Deleted'">
				<a name="projectMemberMail" value="{email_address}" href="mailto:{email_address}"><xsl:value-of select="email_address"/></a>
			</xsl:when>
			<xsl:otherwise>
				<i><xsl:value-of select="email_address"/></i>
			</xsl:otherwise>
			</xsl:choose>

			</td>
	 </tr>
	<tr class="tableLine">
		<td colspan="9">
			<img src="../images/spacers/trans.gif" width="1" height="1" border="0" />
		</td>
	</tr>
	</xsl:template>
</xsl:stylesheet>
