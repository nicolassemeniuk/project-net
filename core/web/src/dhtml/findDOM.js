/* 
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
*/
var isDHTML = 0;
var isLayers = 0;
var isAll = 0;
var isID = 0;

if (document.getElementById) {isID = 1; isDHTML = 1;}
else {
if (document.layers) {isLayers = 1; isDHTML = 1;}
else {
if (document.all) {isAll = 1; isDHTML = 1;}
}}

function findDOM(objectID,withStyle) {
	if (withStyle == 1) {
		if (isID) { return (document.getElementById(objectID).style) ; }
		else { 
			if (isAll) { return (document.all[objectID].style); }
		else {
			if (isLayers) { return (document.layers[objectID]); }
		};}
	}
	else {
		if (isID) { return (document.getElementById(objectID)) ; }
		else { 
			if (isAll) { return (document.all[objectID]); }
		else {
			if (isLayers) { return (document.layers[objectID]); }
		};}
	}
}