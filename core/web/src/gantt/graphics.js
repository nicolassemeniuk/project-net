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
function Graphics(canvas)
{
	this.canvas = canvas;
	this.cache = new Array;
	this.shapes = new Object;
	this.nObject = 0;

	// defaults
	this.penColor = "black";
	this.zIndex = 0;
}

Graphics.prototype.createPlotElement = function(x,y,w,h) 
{
	// detect canvas
	if ( !this.oCanvas )
	{
		if ( (this.canvas == undefined) || (this.canvas == "") ) 
			this.oCanvas = document.body;
		else 
			this.oCanvas = document.getElementById(this.canvas);
	}

	// retrieve DIV
	var oDiv;
	if ( this.cache.length )
		oDiv = this.cache.pop();
	else 
	{
		oDiv = document.createElement('div');
		this.oCanvas.appendChild(oDiv);

		oDiv.style.position = "absolute";
		oDiv.style.margin = "0px";
		oDiv.style.padding = "0px";
		oDiv.style.overflow = "hidden";
		oDiv.style.border = "0px";
	}

	// set attributes
	oDiv.style.zIndex = this.zIndex;
	oDiv.style.backgroundColor = this.penColor;
	
	oDiv.style.left = x;
	oDiv.style.top = y;
	oDiv.style.width = w + "px";
	oDiv.style.height = h + "px";

	oDiv.style.visibility = "visible";
	
	return oDiv;
}

Graphics.prototype.releasePlotElement = function(oDiv)
{
	oDiv.style.visibility = "hidden";
	this.cache.push(oDiv);
}

Graphics.prototype.addShape = function(shape)
{
	shape.oGraphics = this;
	shape.graphicsID = this.nObject;
	this.shapes[this.nObject] = shape;
	this.nObject++;
	shape.draw();
	return shape;
}

Graphics.prototype.removeShape = function(shape)
{
	if ( (shape instanceof Object) && 
		(shape.oGraphics == this) && 
		(this.shapes[shape.graphicsID] == shape) )
	{
		shape.undraw();
		this.shapes[shape.graphicsID] = undefined;
		shape.oGraphics = undefined;
	}
}
Graphics.prototype.clear = function()
{
	for ( var i in this.shapes )
		this.removeShape(this.shapes[i]);
}


//=============================================================================
// Point
Graphics.prototype.drawPoint = function(x,y)
{
	return this.addShape(new Point(x,y))
}

function Point(x,y)
{
	this.x = x;
	this.y = y;
}
Point.prototype.draw = function()
{
	this.oDiv = this.oGraphics.createPlotElement(this.x,this.y,1,1);
}
Point.prototype.undraw = function()
{
	this.oGraphics.releasePlotElement(this.oDiv);
	this.oDiv = undefined;
}

//=============================================================================
// Line
Graphics.prototype.drawLine = function(x1,y1,x2,y2)
{
	return this.addShape(new Line(x1,y1,x2,y2))
}

function Line(x1,y1,x2,y2)
{
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
}

Line.prototype.draw = function()
{
	this.plots = new Array;

	var dx = this.x2 - this.x1;
	var dy = this.y2 - this.y1;
	var x = this.x1;
	var y = this.y1;

	if (dx == 0) {
	    //There is no height change, draw the width as one big div
	    this.oGraphics.createPlotElement(Math.round(x), Math.round(y), 1, dy);
	} else if (dy == 0) {
	    //There is no width change, draw the height as one big div
	    this.oGraphics.createPlotElement(Math.round(x), Math.round(y), dx, 1);
	} else {
        var n = Math.max(Math.abs(dx),Math.abs(dy));
        dx = dx / n;
        dy = dy / n;
        for ( i = 0; i <= n; i++ )
        {
            this.plots.push(this.oGraphics.createPlotElement(Math.round(x),Math.round(y),1,1));

            x += dx;
            y += dy;
        }
    }
}
Line.prototype.undraw = function()
{
	while ( this.plots.length )
		this.oGraphics.releasePlotElement(this.plots.pop());
	this.plots = undefined;
}

//=============================================================================
// Circle
Graphics.prototype.drawCircle = function(x,y,r)
{
	return this.addShape(new Circle(x,y,r))
}

function Circle(x,y,r)
{
	this.x = x;
	this.y = y;
	this.radius = r;
}

Circle.prototype.draw = function()
{
	this.plots = new Array;

	var r2 = this.radius * this.radius;
	var x = 0;
	var y = this.radius;

	while ( x <= y )
	{
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x + x), Math.round(this.y + y), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x - x), Math.round(this.y + y), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x + x), Math.round(this.y - y), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x - x), Math.round(this.y - y), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x + y), Math.round(this.y + x), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x + y), Math.round(this.y - x), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x - y), Math.round(this.y + x), 1, 1));
		this.plots.push(this.oGraphics.createPlotElement(Math.round(this.x - y), Math.round(this.y - x), 1, 1));

		x++;
		y = Math.round(Math.sqrt(r2 - x*x));
	}
}
Circle.prototype.undraw = Line.prototype.undraw;

//=============================================================================
// FillRectangle
Graphics.prototype.fillRectangle = function(x,y,w,h)
{
	return this.addShape(new FillRectangle(x,y,w,h))
}

function FillRectangle(x,y,w,h)
{
	this.x = x;
	this.y = y;
	this.w = w;
	this.h = h;
}

FillRectangle.prototype.draw = function()
{
	this.oDiv = this.oGraphics.createPlotElement(this.x,this.y,this.w,this.h);
}
FillRectangle.prototype.undraw = Point.prototype.undraw;

//=============================================================================

var gr = new Graphics("theCanvas");
var c = null;

var p = null;
var a = 0;
function drawProp()
{
	if ( p ) gr.removeShape(p);
	
	var x = Math.round(Math.sin(a) * 45);
	var y = Math.round(Math.cos(a) * 45);
	a -= Math.PI / 25;
	gr.penColor = "black";
	p = gr.drawLine(100 + x, 100 + y, 100 - x, 100 - y);
	window.setTimeout("drawProp();", 10);
}

function drawShapes()
{
	gr.penColor = "red";
	gr.drawLine(10,10,190,190);

	gr.penColor = "green";
	gr.drawLine(190,10,10,190);

	gr.penColor = "blue";
	c = gr.drawCircle(100,100,45);

	gr.zIndex = 1;	
	gr.penColor = "lime";
	gr.fillRectangle(50,70,100,20);
	gr.zIndex = 0;
	
	drawProp();
}