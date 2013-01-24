<%--
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
--%>

<%@page import="net.project.security.SessionManager"%>
<a name="interchange_config"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Configuring viecon.interchange</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>

<h1 class="ChapterTitle">
<a name="pgfId=1252"></a>Customizing Conversion Configuration</h1>

<div class="BodyAfterTitle"><a name="pgfId=4080"></a>This chapter covers
tables and other parameters which allow users to develop a customized set
of configuration routines for Viecon.interchange. As most of changes involve
changing the mappings of the parameters such as layers, line weight, colors,
line styles, and fonts, this chapter shows how to effect those changes.</div>

<h2 class="h1">
<a name="pgfId=7630"></a>Details on dwgcontrol.bas</h2>

<div class="Body"><a name="pgfId=7639"></a>This chapter discusses modifications
of the Interchange BASIC file "dwgcontrol.bas" used to store and control
translation settings. This program is more fully discussed in the File
Portability/Translation Guide DAA-016840-1/0001 which is available via
the web on the Bentley documentation site.</div>

<h2 class="h1">
<a name="pgfId=5825"></a>Configuration Code</h2>

<div class="Body"><a name="pgfId=6003"></a>Most of the parameters used
in the conversion process are stored in the \home\msint directory. The
primary file used to manipulate the parameters is DWGCONTROL.BAS.</div>

<h2 class="h1">
<a name="pgfId=7153"></a>Additional Configuration Files</h2>

<div class="Body"><a name="pgfId=7154"></a>In addition to the control program
dwgcontrol.bas, there are a number of *.tbl files that can be set to provide
specific data mappings. All files are bi-directional mapping tables with
the exception of dwghatch. The files provided with the Interchange product
are:</div>

<br>&nbsp;
<table border >
<tr>
<th>
<div class="CellHeading"><a name="pgfId=7219"></a>File</div>
</th>

<th>
<div class="CellHeading"><a name="pgfId=7221"></a>Usage</div>
</th>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7223"></a>dwgchar.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7225"></a>Character mapping</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7227"></a>dwgcolor.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7229"></a>Color mapping</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7231"></a>dwgfont.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7270"></a>Font mapping</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7235"></a>dwghatch.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7237"></a>Hatch mapping</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7239"></a>dwglevel.tbllevel mapping</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7241"></a>Layer - level mapping table.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7243"></a>dwgline.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7245"></a>Line type - line style mapping
table</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7247"></a>dwgwtco.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7249"></a>Line weight - color mapping</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7251"></a>dwgwtwt1</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7253"></a>Line weight - line weight
mapping for AutoCAD 2000. See <a href="#14638" class="XRef">See
AutoCAD 2000 Line Weight Mapping</a> .</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7255"></a>dwgwtwt2</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7287"></a>Another file for line weight
- line weight mapping for AutoCAD 2000. See <a href="#14638" class="XRef">See
AutoCAD 2000 Line Weight Mapping</a> .</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7259"></a>shxfont.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7261"></a>Maps MicroStation fonts
to Shape File (shx) fonts.</div>
</td>
</tr>

<tr>
<td>
<div class="CellBody"><a name="pgfId=7263"></a>ttfont.tbl</div>
</td>

<td>
<div class="CellBody"><a name="pgfId=7265"></a>TrueType font mapping</div>
</td>
</tr>
</table>

<h2 class="h1">
<a name="pgfId=7512"></a>Exporting Attached Image Files</h2>

<div class="Body"><a name="pgfId=7513"></a>You can make a copy of an attached
image file on export.</div>

<div class="Body"><a name="pgfId=7514"></a>By default, if a raster attachment
in a DGN file is an AutoCAD supported format, the resultant DWG image attachment
will reference the same raster file without copying it. If the raster format
is not supported by AutoCAD but can be converted to TIFF file, which is
a supported format in AutoCAD, the raster file will be converted to a TIFF
file with the same root name, on the same path but with a .tif extension
name.</div>

<div class="Body"><a name="pgfId=7515"></a>There is an option to copy attached
raster file to a user destination. For example:</div>


<p class="Code"><a name="pgfId=7516"></a>MbeDWGExportSettings.copyRasterFileTo
= "c:\\delivery\\imagefiles\\"

<p class="Body"><a name="pgfId=7517"></a>will make a copy of attached raster
file to folder c:\\delivery\\imagefiles\\. If a raster format is not supported
in AutoCAD, a converted TIFF file, instead of original file, will be copied
to this folder. Consequently the resultant image attachments in DWG file
will reference those in the new folder, instead of those in original folder.

<p class="Body"><a name="pgfId=7518"></a>If an asterisk character * is
used for file location, raster files will be copied to the directory to
where the DWG is exported.
<h2 class="h2">
<a name="pgfId=7519"></a>Coordinate system transformation</h2>

<div class="Body"><a name="pgfId=7520"></a>You can create an AutoCAD UCS
from a MicroStation rotated view.</div>

<div class="Body"><a name="pgfId=7521"></a>A rotated view in MicroStation
may have an arbitrary orientation. This does not hold true in AutoCAD where
the cursor and UCS icon are always aligned with the view. While this is
acceptable for storing data, views may not appear as desired. If an oriented
look is desired, however, the following option can be switched on such
that a UCS will be created to orient the view in AutoCAD. Because in a
UCS coordinates readouts may be different than they were in MicroStation
this option is not recommended for general purposes.</div>

<div class="Body"><a name="pgfId=7522"></a>The command is:</div>


<p class="Code"><a name="pgfId=7523"></a>MbeDWGExportSettings.convertRotatedViewToUcs
= MBE_On

<p class="Code"><a name="pgfId=7524"></a>

<p class="Body"><a name="pgfId=7525"></a>where MBE_on will converts rotated
view to UCS and MBE_Off will not.
<h2 class="h2">
<a name="pgfId=7526"></a>Convert special characters</h2>

<div class="Body"><a name="pgfId=7527"></a>When exporting text, special
symbols such as , , &Oslash;, or any other ASCII value that is greater
than 127 can be (or not) converted to AutoCAD special codes prefixed with
%%. The following setting can be used for this purpose:</div>

<div class="Code"><a name="pgfId=7528"></a>MbeDWGExportSettings.convertSpecialCharacters
= MBE_On</div>

<div class="Body"><a name="pgfId=7529"></a></div>


<p class="Body"><a name="pgfId=7530"></a>will make the translator convert
all special symbols, except for those in multibyte languages, to AutoCAD
codes. This setting will produce the effect that the characters look the
same. However, each of these characters is prefixed with %%. Sometimes
this is not desirable, particularly if you map a font to a shape file that
supports special symbols in AutoCAD as they do in MicroStation. In that
case, consider using MBE_Off to not convert these characters.
<h2 class="h1">
<a name="pgfId=7666"></a><a name="14638"></a>AutoCAD 2000 Line Weight Mapping</h2>

<div class="Body"><a name="pgfId=7667"></a>The following strings are used
in this mapping:</div>

<div class="Code"><a name="pgfId=7668"></a>MbeWeightWeightTable.addImportEntry
AcadLineWeight, MsLineWeight</div>

<div class="Code"><a name="pgfId=7669"></a>MbeWeightWeightTable.addExportEntry
AcadLineWeight, MsLineWeight</div>


<p class="Code"><a name="pgfId=7670"></a>MbeWeightWeightTable.addImportExportEntry
AcadLineWeight, MsLineWeight

<p class="Code"><a name="pgfId=7671"></a>MbeWeightWeightTable.addImportEntryFromFile
TableFileName[, EnvVar]

<p class="Code"><a name="pgfId=7672"></a>MbeWeightWeightTable.addExportEntryFromFile
TableFileName[, EnvVar]

<p class="Code"><a name="pgfId=7673"></a>MbeWeightWeightTable.addImportExportEntryFromFIle
TableFileName[, EnvVar]

<p class="Code"><a name="pgfId=7674"></a>

<p class="Body"><a name="pgfId=7675"></a>Where: <i>AcadLineWeight</i> is
the line weight index in AutoCAD

<p class="Body"><a name="pgfId=7676"></a><i>MsLineWeight</i> is the line
weight index in MicroStation

<p class="Body"><a name="pgfId=7677"></a><i>TableFileName</i> is the name
of the table file that maps the weights. The text file can be created and
modified using any text editor, such as MicroStation Basic Editor, notepad,
MicroSoft Word (as an ASCII text file), etc. The first column is AutoCAD
line weight and the second is MicroStation line weight.

<p class="Body"><a name="pgfId=7678"></a>The following immediately following
example will convert AutoCAD lineweight 30 (0.3mm or 0.012") to MicroStation
weight number 2. Entities with weights BYLAYER/BYBLOCK will converted to
the weights according to the layers or blocks they belong to. Those with
weight DEFAULT will be forced to be weight number 3.
<h3 class="h3">
<a name="pgfId=7679"></a>Examples</h3>

<div class="Code"><a name="pgfId=7680"></a>MbeWeightWeightTable.addImportEntry
30, 2</div>

<div class="Body"><a name="pgfId=7681"></a></div>

<div class="Code"><a name="pgfId=7682"></a>MbeWeightWeightTable.addImportEntry
MBE_WEIGHT_ByLayer, -1</div>


<p class="Body"><a name="pgfId=7683"></a>

<p class="Body"><a name="pgfId=7684"></a>will use the line weight of the
layer an entity is on and map that line weight index to MicroStation line
weight

<p class="Code"><a name="pgfId=7685"></a>MbeWeightWeightTable.addImportEntry
MBE_WEIGHT_ByBlock, -1

<p class="Body"><a name="pgfId=7686"></a>

<p class="Body"><a name="pgfId=7687"></a>will use the line weight of the
block an entity belongs to and map that line weight index to MicroStation
line weight
<h3 class="h3">
<a name="pgfId=7688"></a>Example</h3>

<div class="Body"><a name="pgfId=7689"></a>MbeWeightWeightTable.addImportEntry
MBE_WEIGHT_Default, 3</div>

<div class="Body"><a name="pgfId=7690"></a>will map AutoCAD line weight
DEFAULT to line weight 3</div>

<div class="Body"><a name="pgfId=7691"></a>Like color and linetype, AutoCAD
2000 line weight has BYLAYER and BYBLOCK. The behavior in line weight is
the same as they are in color and linetype. Map BYLAYER/BYBLOCK to -1 to
obtain the line weight value of the layer/block. If BYLAYER/BYBLOCK is
mapped to a valid MicroStation line weight index, any entity with BYLAYER/BLOCK
will be forced to use that line weight value, instead of the value of the
layer/block. Another special value is DEFAULT which is present in line
weight. Similar to BYLAYER/BYBLOCK, DEAFAULT may be mapped to -1 to get
default value of an entity. Any other value will force DEFAULT to be set
to that specific value.</div>

<h1 class="h1">
<a name="pgfId=7510"></a>More Documentation</h1>

<div class="Body"><a name="pgfId=7281"></a>For additional material see
the File Portability/Translation User Guide from the Bentley web site.

