<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:display="xalan://net.project.base.property.PropertyProvider"
	xmlns:format="xalan://net.project.util.XSLFormat"
    extension-element-prefixes="display format" >
    
	<xsl:output method="html" encoding="UTF-8"/>

    <!-- Declare external variables -->
    <xsl:param name="JspRootUrl" />

	<xsl:variable name="translation"
    	select="/ProjectXML/Properties/Translation/property" />
	<xsl:variable name="JSPRootURL" select="$translation[@name='JSPRootURL']" />

	<xsl:template match="/">
    	<xsl:apply-templates />	
	</xsl:template>

	<xsl:template match="ProjectXML">
    	<xsl:apply-templates select="Content" />
	</xsl:template>

	<xsl:template match="Content">
    	<xsl:apply-templates />
	</xsl:template>

    <xsl:template match="assignment_list">
    <table width="100%" cellpadding="0" border="0" cellspacing="0">
        <tr class="tableHeader" align="left">
            <th nowrap="1" width="1%" valign="bottom" class="tableHeader">
                <xsl:if test="assignment">
                    <input type="checkbox" name="changeCheckedState" onClick="changeSelection();" title="{display:get('prm.schedule.list.togglecheckboxes.message')}"/>
                </xsl:if>
            </th>
            <th nowrap="1" width="24%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.assignment.label')"/></th>
            <th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.type.label')"/></th>
            <th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.space.label')"/></th>
            <th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.status.label')"/></th>
            <th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.percent.label')"/></th>
            <th nowrap="1" width="15%" valign="bottom" class="tableHeader"><xsl:value-of select="display:get('prm.personal.main.assignmentrole.label')"/></th>
        </tr>
        <tr class="tableLine">
            <td  colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
        </tr>
        <xsl:apply-templates select="assignment" />
    </table>
    </xsl:template>
  
    <xsl:template match="assignment">
        <xsl:variable name="is_owner" select = "./primary_owner" />
        <tr class="tableContent">
            <td> 
                <xsl:element name="INPUT">
                    <xsl:attribute name="type">checkbox</xsl:attribute>
                    <xsl:attribute name="id">newItemsCheckbox_<xsl:value-of select="./object_name"/>
                    </xsl:attribute>
                    <xsl:attribute name="name">assn_status</xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="./object_id"/>_<xsl:value-of select="./object_type"/>
                    </xsl:attribute>
                </xsl:element> 	
            </td>
            <td class="tableContent">
                <a id="newItems_{object_name}" href="{$JspRootUrl}/servlet/AssignmentController/View?module=160&amp;objectID={object_id}&amp;personID={person_id}">
                    <xsl:value-of select="./object_name"/>
                </a>
            </td>
            <td class="tableContent"><xsl:value-of select="./object_type_pretty"/></td>
            <td class="tableContent"><xsl:value-of select="./space_name"/></td>
            <td class="tableContent"><xsl:value-of select="./status"/></td>
            <td class="tableContent"><xsl:value-of select="format:formatPercentDecimal(percent_assigned)"/></td>
            <td class="tableContent"><xsl:value-of select="./role"/><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
        </tr>
        <tr class="tableLine">
          <td colspan="7" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
