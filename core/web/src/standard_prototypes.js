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
//   Method prototypes for the "actions" object.  Each
//   entry in the action object MUST have a corresponding
//   method defined below.

//   These methods are overriden by the individual application
//   pages.  

//   The prototypes should do nothing other than "return false"


function cancel() {
	return false;
}

function submit () {
	return false;
}

function create () {
	return false;
}

function modify () {
	return false;
}

function properties () {
	return false;
}

function remove () {
	return false;
}

function reset () {
	return false;
}

function link () {
	return false;
}

function search () {
	return false;
}

function help () {
	return false;
}

function security () {
	return false;
}

function notify () {
	return false;
}
