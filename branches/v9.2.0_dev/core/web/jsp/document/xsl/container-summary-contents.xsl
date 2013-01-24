<?xml version="1.0"?>
<!--
	Deprecated as of release Gecko 4.
	May be removed from the product in the future.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

  <xsl:template match="container">

	<xsl:apply-templates select="container_contents/container_entry_columns" /> 	
	<xsl:apply-templates select="container_contents/doc_container" /> 	
	<xsl:apply-templates select="container_contents/document" /> 	

  </xsl:template>

  <xsl:template match ="container_contents/container_entry_columns">

	 <tr align="left"> 
	 
	 
	 

	     <td bgcolor="#D2D2D2" colspan="2"></td>

	     <td align="left" bgcolor="#D2D2D2"> 
		<div align="left"><a href="/document/ContainerSortProcessing.jsp?sort={name}">Name</a></div>
            </td>

	    <td align="left" bgcolor="#D2D2D2"> 
		<div align="left"><a href="/document/ContainerSortProcessing.jsp?sort={format}">Format</a></div>
            </td>

	    <td align="left" bgcolor="#D2D2D2"> 
		<div align="left"><a href="/document/ContainerSortProcessing.jsp?sort={version_num}">Author</a></div>
            </td>
   
	    <td align="right" bgcolor="#D2D2D2"> 
		<div align="right"><a href="/document/ContainerSortProcessing.jsp?sort={file_size}">Size</a></div>
            </td>

	 </tr>

   </xsl:template> 

   <xsl:template match = "container_contents/doc_container">

        <tr align="center" bgcolor="#FFFFFF"> 

	    <td align="left"> 
		<xsl:element name="input">
			<xsl:attribute name="type">radio</xsl:attribute>
			<xsl:attribute name="name">selected</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
		</xsl:element>

		<xsl:element name="input">
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="./object_id"/></xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="./object_type"/></xsl:attribute>
		</xsl:element>
	    </td>

	    <td align="left"><img src="../images/folder.gif"/></td>

	    <td align="left">
		<xsl:element name="a">
			<xsl:attribute name="href">/document/TraverseFolderProcessing.jsp?id=<xsl:value-of select="./object_id"/>&amp;module=10</xsl:attribute>
			<xsl:value-of select="./name"/>
		</xsl:element>
	    </td> 

	    <td align="center"> 
		<div align="left">File Folder</div>
            </td>

            <td align="left"></td>
            <td align="left"></td>
            
	</tr>

   </xsl:template>


   <xsl:template match = "container_contents/document">

        <tr align="center" bgcolor="#FFFFFF"> 	

	    <td align="left"> 
		<xsl:element name="input">
			<xsl:attribute name="type">radio</xsl:attribute>
			<xsl:attribute name="name">selected</xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="./object_id"/></xsl:attribute>
		</xsl:element>
		
		<xsl:element name="input">
			<xsl:attribute name="type">hidden</xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="./object_id"/></xsl:attribute>
			<xsl:attribute name="value"><xsl:value-of select="./object_type"/></xsl:attribute>
		</xsl:element>
	    </td>

	    <td align="left">
		<a href="{/container/container_properties/app_url}/document/PropertyFrameset.jsp?id={./object_id}"><img border="0" src="..{./app_icon_url}"/></a>
	    </td>

	    <td align="left">
		<a href="{/container/container_properties/app_url}/document/PropertyFrameset.jsp?id={./object_id}"><xsl:value-of select="./name"/></a>
	    </td>

	    <td align="left"><xsl:value-of select="./format"/></td>

	    <td align="left"><xsl:value-of select="./author"/> </td>
 	    <td align="right"><xsl:value-of select="./file_size"/> </td>

        </tr>
   
   </xsl:template>

</xsl:stylesheet>
