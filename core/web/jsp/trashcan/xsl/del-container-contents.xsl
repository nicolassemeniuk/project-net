<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:display="xalan://net.project.base.property.PropertyProvider"
		xmlns:format="xalan://net.project.util.XSLFormat"		
    	extension-element-prefixes="display format" >
<xsl:output method="html"/>

  <xsl:template match="container">
    <table border="0" width="100%" cellpadding="0" cellspacing="0">

        <xsl:apply-templates select="container_contents/container_entry_columns" /> 	
        
        <xsl:for-each select="//container/container_contents/entry[@type='doc_container']">
            <xsl:call-template name="container" />
         </xsl:for-each>

        <xsl:call-template name="contents" />
        
    </table>
  </xsl:template>

  <xsl:template match="container_contents/container_entry_columns">  

	 <tr align="left"> 

	    <td class="tableHeader" colspan="2"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

	    <td align="left" class="tableHeader"> 
            <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={name}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.documentname.link')"/></a></div>
        </td>

        <td align="left" class="tableHeader"> 
            <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={parent_name}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.documentfoldername.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
            <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={format}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.documentformat.link')"/></a></div>
        </td>

	    <td align="left" class="tableHeader"> 
            <div align="left" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={status}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.status.link')"/></a></div>
        </td>

	    <td align="right" class="tableHeader"> 
            <div align="right" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={file_size}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.size.link')"/></a></div>
        </td>

        <td align="right" class="tableHeader"> 
            <div align="right" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={modified_by}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.deleted_by.link')"/></a></div>
        </td>

        <td align="right" class="tableHeader"> 
            <div align="right" class="tableHeader"><a href="{/container/jsp_root_url}/document/ContainerSortProcessing.jsp?sort={date_modified}" class="tableHeader"><xsl:value-of select="display:get('prm.document.main.date_deleted.link')"/></a></div>
        </td>

	    <td colspan="2" align="right" class="tableHeader"></td>
	 </tr>
	 
	<tr class="tableLine">

		<td colspan="10" class="tableLine">
			<xsl:element name="img">
				<xsl:attribute name="src">../images/spacers/trans.gif</xsl:attribute>
				<xsl:attribute name="width">1</xsl:attribute>
				<xsl:attribute name="height">2</xsl:attribute>
				<xsl:attribute name="border">0</xsl:attribute>
			</xsl:element>
		</td>
	</tr>

   </xsl:template> 

   <xsl:template name="container">

    <tr align="center" class="tableContent"> 

	    <td align="left" class="tableContent" width="1%"> 
            <xsl:element name="input">
                <xsl:attribute name="type">radio</xsl:attribute>
                <xsl:attribute name="name">selected</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
            </xsl:element>

            <xsl:element name="input">
                <xsl:attribute name="type">hidden</xsl:attribute>
                <xsl:attribute name="name">item<xsl:value-of select="./object_id"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_type"/></xsl:attribute>
            </xsl:element>
	    </td>

        <td align="left" class="tableContent">
            <xsl:if test="position() > 1">
                <a href="{/container/jsp_root_url}/document/TraverseFolderProcessing.jsp?id={./object_id}&amp;module=330&amp;action=256"><img border="0" src="../images/folder.gif"/></a>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:if>
        </td>

        <td align="left" class="tableContent">
            <xsl:choose>
                <xsl:when test="position() > 1">
                    <xsl:element name="a">
                        <xsl:attribute name="href"><xsl:value-of select = "/container/jsp_root_url" />/document/TraverseFolderProcessing.jsp?id=<xsl:value-of select="./object_id"/>&amp;module=330&amp;action=256</xsl:attribute>
                        <xsl:attribute name="title"><xsl:value-of select="description" /></xsl:attribute>
                        <xsl:value-of select="./name"/>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="./name"/>
                </xsl:otherwise>
            </xsl:choose>
        </td>

        <td align="left" class="tableContent">
            <xsl:choose>
                <xsl:when test="position() = 1">
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                   <xsl:variable name="folder" select="./parent_name" />
                   <xsl:choose>
                        <xsl:when test="string-length($folder) != 0">
                            <xsl:value-of select="$folder"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <div align="left"><xsl:value-of select="display:get('prm.document.container.topfolder.name')"/></div>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </td>

        <td align="center" class="tableContent">
            <xsl:choose>
                <xsl:when test="position() > 1">
                    <div align="left"><xsl:value-of select="display:get('prm.document.main.folder.label')"/></div>
                </xsl:when>
                <xsl:otherwise>
                    <div align="left"><xsl:value-of select="display:get('prm.document.main.currentfolder.label')"/></div>
                </xsl:otherwise>
            </xsl:choose>
        </td>

	    <td colspan="5" align="center" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
	</tr>
	
	<tr class="tableLine">
        <td colspan="10" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>

   </xsl:template>


   <xsl:template name="document">

	<xsl:variable name="user_id" select="./user_id"/>
	
    <tr align="center"> 	

	    <td align="left" class="tableContent"> 
            <xsl:element name="input">
                <xsl:attribute name="type">radio</xsl:attribute>
                <xsl:attribute name="name">selected</xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
            </xsl:element>
            
            <xsl:element name="input">
                <xsl:attribute name="type">hidden</xsl:attribute>
                <xsl:attribute name="name">item<xsl:value-of select="./object_id"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="./object_type"/></xsl:attribute>
            </xsl:element>
	    </td>

	    <td align="left" class="tableContent">
    		<a target="view_window" href="{/container/jsp_root_url}/servlet/DownloadDocument?id={./object_id}&amp;status=D"><img border="0" src="..{./app_icon_url}"/></a>
	    </td>

	    <td align="left" class="tableContent">
            <a target="view_window" href="{/container/jsp_root_url}/servlet/DownloadDocument?id={./object_id}&amp;status=D" title="{description}">
                <xsl:value-of select="./name"/>
            </a>
	    </td>

        <td align="left" class="tableContent">
            <xsl:variable name="folder" select="./parent_name" />
            <xsl:choose>
               <xsl:when test="string-length($folder) != 0">
                    <xsl:value-of select="$folder"/>
                </xsl:when>
                <xsl:otherwise>
                    <div align="left"><xsl:value-of select="display:get('prm.document.container.topfolder.name')"/></div>
                </xsl:otherwise>
            </xsl:choose>
        </td>

	    <td align="left" class="tableContent"><xsl:value-of select="./format"/></td>
        <td align="left" class="tableContent"><xsl:value-of select="./status"/> </td>
 	    <td align="right" class="tableContent"><xsl:value-of select="./file_size"/> </td>
        <td align="right" class="tableContent"><xsl:value-of select="./last_modified_by"/> </td>
        <td align="right" class="tableContent"><xsl:value-of select="format:formatISODateTime(last_modified)"/> </td>
        <td align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
    </tr>
	<tr class="tableLine">
	<td colspan="10" class="tableLine">
		<img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/>
	</td>
	</tr>
   
   </xsl:template>



   <xsl:template name="contents">
    <xsl:for-each select="//container/container_contents/entry">

	<xsl:choose>

		<xsl:when test="@type = 'document'">
			<xsl:call-template name="document" />
		</xsl:when>

		<xsl:when test="@type = 'bookmark'">
			<xsl:call-template name="bookmark" />
		</xsl:when>
	</xsl:choose>


    </xsl:for-each>
   </xsl:template>

   <xsl:template name="bookmark">

    <tr align="center"> 	
	
        <td align="left" class="tableContent"> 
        
            <input type="radio" name="selected" value="{./object_id}" />
            <input type="hidden" name="item{./object_id}" value="{./object_type}" />
            
        </td>

        <td align="left" class="tableContent">
            <a target="view_window" href="{./url}">
                <img border="0" src="../images/appicons/url.gif"/>
            </a>
        </td>

        <td align="left" class="tableContent">
            <a target="view_window" href="{./url}" title="{description}">
                <xsl:value-of select="./name"/>
            </a>
        </td>

        <td align="left" class="tableContent">
            <xsl:value-of select="./format"/>
        </td>
	
        <td align="left" class="tableContent"><xsl:value-of select="./status"/> </td>
        <td align="right" class="tableContent"><xsl:value-of select="./file_size"/> </td>
        <td align="right" class="tableContent"><xsl:value-of select="./modified_by"/> </td>
        <td align="right" class="tableContent"><xsl:value-of select="format:formatISODateTime(date_modified)"/> </td>
        <td colspan="2" align="left" class="tableContent"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

    </tr>
	<tr class="tableLine">
        <td colspan="10" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="1" border="0" alt=""/></td>
	</tr>
   
   </xsl:template>
</xsl:stylesheet>

