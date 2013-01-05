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

Ext.onReady(function(){
    var win;
        
    function callfunc(){
    	setIframeSource(0);
    }
    
    tabPanel = new Ext.TabPanel({
                    el: 'win-tabs',
                    autoTabs:true,
                    activeTab:0,
                    deferredRender:false,
                    border:false                    
                });
                
    var button = Ext.get('show-btn');

    button.on('click', function(){
        // create the window on the first click and reuse on subsequent clicks
        if(!win){
            win = new Ext.Window({
                el: 'manage_select_win',
                layout: 'fit',
                width: 550,
                height: 550,
                closeAction: 'hide',
                clsable: false,
                plain: true,
                modal: true,
                
                items: tabPanel,

                buttons: [{
                    text: 'Close',
                    handler: function(){
                        win.hide();                                              
                    }
                }]
            });
        }
        win.show(this);
    });
});
