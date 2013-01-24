<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"
		extension-element-prefixes="display format" >
<xsl:output method="html"/>

  <xsl:template match="/"><xsl:for-each select="bookmark">

	<tr>
            <th align="left" colspan="3"></th>
          </tr>
          <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.bookmarkname.label')"/></th>
            <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left" class="tableContent">
	    		<a target="view_window" href="{./url}">
					<img align="bottom" border="0" src="../images/appicons/url.gif"/>
				</a> <a target="view_window" href="{./url}"><xsl:value-of select="./name"/></a>
	        </td>
          </tr>
	            <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.url.label')"/></th>
            <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td  align="left" class="tableContent">
				<a target="view_window" href="{./url}"><xsl:value-of select="./url"/></a>
			</td>
          </tr>
          <tr>
            <th align="right" class="tableHeader">
            	<xsl:value-of select="display:get('prm.document.bookmarkproperties.vaultpath.label')"/>
            </th>
            <th align="right">
            	<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </th>
            <td align="left" class="tableContent">
            	<xsl:apply-templates select="path"/>
			</td>
          </tr>
	  <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.description.label')"/></th>
            <th align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
            <td align="left"  class="tableContent"><xsl:value-of select="./description"/></td>
          </tr>
          <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.owner.label')"/></th>
            <td align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td align="left"  class="tableContent"><xsl:value-of select="./owner"/></td>
          </tr>
		            <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.status.label')"/></th>
            <td align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td align="left"  class="tableContent"><xsl:value-of select="./status"/></td>
          </tr>

		            <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.comments.label')"/></th>
            <td align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td align="left"  class="tableContent"><xsl:value-of select="./notes"/></td>
		</tr>

			    <tr>
            <th align="right" class="tableHeader"><xsl:value-of select="display:get('prm.document.bookmarkproperties.lastmodified.label')"/></th>
            <td align="right"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td align="left"  class="tableContent"><xsl:value-of select="format:formatISODateTime(lastModified)"/></td>
		</tr>
		
		<tr></tr>

     </xsl:for-each>

  </xsl:template>
  
    <xsl:template match="path">
        <xsl:apply-templates select="node">
            <xsl:sort select="level" order="ascending"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="node">
        <xsl:if test="level > 1">
            /
        </xsl:if>
        <a href="{../jsp_root_url}/document/TraverseFolderProcessing.jsp?id={object_id}&amp;module=10">
            <xsl:value-of select="name"/>
        </a>
    </xsl:template>
</xsl:stylesheet>
