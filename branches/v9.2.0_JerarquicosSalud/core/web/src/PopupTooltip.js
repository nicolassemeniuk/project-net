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
var ie = (document.all);
var n6 = (window.sidebar);
var globalPopupName;
var windowWidth;
var windowHeight;

if (ie) {
    document.onmousedown = phide;
    document.onmousemove = mouseMove;
} else {
    document.addEventListener("mousedown",phide,true);
    document.addEventListener("mousemove",mouseMove,true);
}

function mouseMove(e) {
    var popup = document.getElementById(globalPopupName);
    if (!popup) return;
    var maxleft;
    var maxtop;
    var newX;
    var newY;
    var popupWidth = popup.offsetWidth;
    var popupHeight =  popup.offsetHeight;
    if (popup.style.visibility == "visible") {
        if (ie) { 			// for Internet Explorer
            newX = event.x;
            newY = event.y;
            popup.style.left = (getWindowWidth() - newX) < (popupWidth + 10) ? (newX - (popupWidth + 10)) : (newX + 10);;
            popup.style.top = (getWindowHeight() - newY) < (popupHeight + 10) ? (newY - (popupHeight + 10)) :(newY + 10);
        } else if (n6) {	// for mozila firefox
            newX = e.pageX;
            newY = e.pageY;
            popup.style.left = ((getWindowWidth() - newX) < (popupWidth + 10) ? (newX - (popupWidth + 10)) : (newX + 10)) + "px";
            popup.style.top = ((getWindowHeight() - newY) < (popupHeight + 10) ? (newY - (popupHeight + 10)) :(newY + 10)) + "px";
        } else {			// for other browers
            newX = e.clientX;
            newY = e.clientY;
            popup.style.left = ((getWindowWidth() - newX) < (popupWidth + 10) ? (newX - (popupWidth + 10)) : (newX + 10)) + "px";
            popup.style.top = ((getWindowHeight() - newY) < (popupHeight + 10) ? (newY - (popupHeight + 10)) :(newY + 10)) + "px";
        }
    }
}

function pup(popupName) {
    var popup = document.getElementById(popupName);
    globalPopupName = popupName;

    popup.style.left = -500 + (n6 ? "px" : "");
    popup.style.top = -500 + (n6 ? "px" : "");

    popup.style.visibility = "visible";
}

function phide(popupName) {
    var popup = document.getElementById(popupName);

    if (popup)
        popup.style.visibility = "hidden";
}

function getScrollXY() {
  var xLocation = 0;
  var yLocation = 0;

  if (typeof( window.pageYOffset ) == 'number') {
    //Should work with Netscape
    yLocation = window.pageYOffset;
    xLocation = window.pageXOffset;
  } else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
      //Should work with DOM compliant browsers
      yLocation = document.body.scrollTop;
      xLocation = document.body.scrollLeft;
  } else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
      //Should work with IE6
      yLocation = document.documentElement.scrollTop;
      xLocation = document.documentElement.scrollLeft;
  }

  return [ xLocation, yLocation ];
}