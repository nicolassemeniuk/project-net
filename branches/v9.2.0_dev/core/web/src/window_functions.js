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
/**
  * Open a new dialog window
  * @param window_name the name of the window
  * @param url (optional) the url to open.  Default is "/blank.html"
  * @param height the height of the window in pixels.  If absent, 192 is the
  * default.
  * @param width the width of the window in pixels.  If absent, 300 is the
  * default.
  * @param scrollbars 0 to not display them, 1 to display them.  If absent, 0 is
  * the default.
  * @return a handle to the window as a Window object
  */
function openwin_dialog (window_name, url, height, width, scrollbars) {
   var dialog_win = null;

   if (arguments.length == 1) {
      url = "../blank.html"
   }

   if (!height)
       height = 192;

   if (!width)
       width = 300;

   if (!scrollbars)
       scrollbars = 0;

   dialog_win = window.open(url, window_name, "directory=0,height="+height+",width="+width+",resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars="+scrollbars+",status=0,toolbar=0");
   return dialog_win;
}

function openwin_dialog_resizable (window_name, url, height, width, scrollbars,resizable) {
   var dialog_win = null;

   if (arguments.length == 1) {
      url = "../blank.html"
   }

   if (!height)
       height = 192;

   if (!width)
       width = 300;

   if (!scrollbars)
       scrollbars = 0;

   dialog_win = window.open(url, window_name, "directory=0,height="+height+",width="+width+",resizable="+resizable+", statusbar=0, hotkeys=0,menubar=0,scrollbars="+scrollbars+",status=0,toolbar=0");
   return dialog_win;
}

/**
  * Open a new small window
  * @param window_name the name of the window
  * @param url (optional) the url to open.  Default is "/blank.html"
  * @return a handle to the window as a Window object
  */
function openwin_small (window_name, url) {
   var small_win = null;

   if (arguments.length == 1) {
      url = "../blank.html"
   }
   
   small_win = window.open(url, window_name, "directory=0,height=250,width=600,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return small_win;
}

function openwin_large (window_name, url) {
   var large_win = null;

   if (arguments.length == 1) {
      url = "../blank.html"
   }
   
   large_win = window.open(url, window_name, "directory=0,height=520,width=750,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return large_win;
}

function openwin_applet(url) {
   var appletl_win = null;

   applet_win = window.open(url, 'theApplet', "directory=0,height=400,width=800,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=0,status=0,toolbar=0");
   return applet_win;
}

function openwin_pingapplet(url) {
   var appletl_win = null;

   applet_win = window.open(url, 'Ping', "directory=0,height=400,width=800,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=0,status=0,toolbar=0");
   return applet_win;
}


function openwin_help(url) {
   var help_win = null;

   help_win = window.open(url, 'theHelp', "status=1,toolbar=1,resizable=1,scrollbars=1");
	
   help_win.focus();
   return help_win;
}

function openwin_wizard(window_name, url) {
   var wizard_win = null;
   
   if (!url) {
      url = "../blank.html";
   }

   wizard_win = window.open(url, window_name, "directory=0,height=600,width=500,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return wizard_win;
}

function openwin_security (window_name) {
   var small_win = null;

   small_win = window.open("../blank.html", window_name, "directory=0,height=600,width=500,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return small_win;
}

function openwin_security_small (window_name) {
   var small_win = null;

   small_win = window.open("../blank.html", window_name, "directory=0,height=200,width=400,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return small_win;
}

function openwin_linker(url) {
   var linker_win = null;
  
   linker_win = window.open(url, 'theLinker', "directory=0,height=420,width=575,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=yes,status=0,toolbar=0");
   return linker_win;
}


function openwin_notification(url) {
   var notification_win = null;
  
   notification_win = window.open(url, 'theNotification', "directory=0,height=520,width=750,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return notification_win;
}

function openwin_domain(url) {
   var domain_win = null;
  
   domain_win = window.open(url, 'theDomain', "directory=0,height=620,width=800,resizable=1, 	statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   return domain_win;
}



shortcut = {
	'all_shortcuts':{},//All the shortcuts are stored in this array
	'add': function(shortcut_combination,callback,opt) {
		//Provide a set of default options
		var default_options = {
			'type':'keydown',
			'propagate':false,
			'disable_in_input':false,
			'target':document,
			'keycode':false
		}
		if(!opt) opt = default_options;
		else {
			for(var dfo in default_options) {
				if(typeof opt[dfo] == 'undefined') opt[dfo] = default_options[dfo];
			}
		}

		var ele = opt.target
		if(typeof opt.target == 'string') ele = document.getElementById(opt.target);
		var ths = this;
		shortcut_combination = shortcut_combination.toLowerCase();

		//The function to be called at keypress
		var func = function(e) {
			e = e || window.event;
			
			if(opt['disable_in_input']) { //Don't enable shortcut keys in Input, Textarea fields
				var element;
				if(e.target) element=e.target;
				else if(e.srcElement) element=e.srcElement;
				if(element.nodeType==3) element=element.parentNode;

				if(element.tagName == 'INPUT' || element.tagName == 'TEXTAREA') return;
			}
	
			//Find Which key is pressed
			if (e.keyCode) code = e.keyCode;
			else if (e.which) code = e.which;
			var character = String.fromCharCode(code);
			
			if(code == 188) character=","; //If the user presses , when the type is onkeydown
			if(code == 190) character="."; //If the user presses , when the type is onkeydown

			var keys = shortcut_combination.split("+");
			//Key Pressed - counts the number of valid keypresses - if it is same as the number of keys, the shortcut function is invoked
			var kp = 0;
			
			//Work around for stupid Shift key bug created by using lowercase - as a result the shift+num combination was broken
			var shift_nums = {
				"`":"~",
				"1":"!",
				"2":"@",
				"3":"#",
				"4":"$",
				"5":"%",
				"6":"^",
				"7":"&",
				"8":"*",
				"9":"(",
				"0":")",
				"-":"_",
				"=":"+",
				";":":",
				"'":"\"",
				",":"<",
				".":">",
				"/":"?",
				"\\":"|"
			}
			//Special Keys - and their codes
			var special_keys = {
				'esc':27,
				'escape':27,
				'tab':9,
				'space':32,
				'return':13,
				'enter':13,
				'backspace':8,
	
				'scrolllock':145,
				'scroll_lock':145,
				'scroll':145,
				'capslock':20,
				'caps_lock':20,
				'caps':20,
				'numlock':144,
				'num_lock':144,
				'num':144,
				
				'pause':19,
				'break':19,
				
				'insert':45,
				'home':36,
				'delete':46,
				'end':35,
				
				'pageup':33,
				'page_up':33,
				'pu':33,
	
				'pagedown':34,
				'page_down':34,
				'pd':34,
	
				'left':37,
				'up':38,
				'right':39,
				'down':40,
	
				'f1':112,
				'f2':113,
				'f3':114,
				'f4':115,
				'f5':116,
				'f6':117,
				'f7':118,
				'f8':119,
				'f9':120,
				'f10':121,
				'f11':122,
				'f12':123
			}
	
			var modifiers = { 
				shift: { wanted:false, pressed:false},
				ctrl : { wanted:false, pressed:false},
				alt  : { wanted:false, pressed:false},
				meta : { wanted:false, pressed:false}	//Meta is Mac specific
			};
                        
			if(e.ctrlKey)	modifiers.ctrl.pressed = true;
			if(e.shiftKey)	modifiers.shift.pressed = true;
			if(e.altKey)	modifiers.alt.pressed = true;
			if(e.metaKey)   modifiers.meta.pressed = true;
                        
			for(var i=0; k=keys[i],i<keys.length; i++) {
				//Modifiers
				if(k == 'ctrl' || k == 'control') {
					kp++;
					modifiers.ctrl.wanted = true;

				} else if(k == 'shift') {
					kp++;
					modifiers.shift.wanted = true;

				} else if(k == 'alt') {
					kp++;
					modifiers.alt.wanted = true;
				} else if(k == 'meta') {
					kp++;
					modifiers.meta.wanted = true;
				} else if(k.length > 1) { //If it is a special key
					if(special_keys[k] == code) kp++;
					
				} else if(opt['keycode']) {
					if(opt['keycode'] == code) kp++;

				} else { //The special keys did not match
					if(character == k) kp++;
					else {
						if(shift_nums[character] && e.shiftKey) { //Stupid Shift key bug created by using lowercase
							character = shift_nums[character]; 
							if(character == k) kp++;
						}
					}
				}
			}
			
			if(kp == keys.length && 
						modifiers.ctrl.pressed == modifiers.ctrl.wanted &&
						modifiers.shift.pressed == modifiers.shift.wanted &&
						modifiers.alt.pressed == modifiers.alt.wanted &&
						modifiers.meta.pressed == modifiers.meta.wanted) {
				callback(e);
	
				if(!opt['propagate']) { //Stop the event
					//e.cancelBubble is supported by IE - this will kill the bubbling process.
					e.cancelBubble = true;
					e.returnValue = false;
	
					//e.stopPropagation works in Firefox.
					if (e.stopPropagation) {
						e.stopPropagation();
						e.preventDefault();
					}
					return false;
				}
			}
		}
		this.all_shortcuts[shortcut_combination] = {
			'callback':func, 
			'target':ele, 
			'event': opt['type']
		};
		//Attach the function with the event
		if(ele.addEventListener) ele.addEventListener(opt['type'], func, false);
		else if(ele.attachEvent) ele.attachEvent('on'+opt['type'], func);
		else ele['on'+opt['type']] = func;
	},

	//Remove the shortcut - just specify the shortcut and I will remove the binding
	'remove':function(shortcut_combination) {
		shortcut_combination = shortcut_combination.toLowerCase();
		var binding = this.all_shortcuts[shortcut_combination];
		delete(this.all_shortcuts[shortcut_combination])
		if(!binding) return;
		var type = binding['event'];
		var ele = binding['target'];
		var callback = binding['callback'];

		if(ele.detachEvent) ele.detachEvent('on'+type, callback);
		else if(ele.removeEventListener) ele.removeEventListener(type, callback, false);
		else ele['on'+type] = false;
	}
}
