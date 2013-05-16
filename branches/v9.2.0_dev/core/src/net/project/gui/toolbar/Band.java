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

 package net.project.gui.toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import net.project.base.property.PropertyProvider;
import net.project.schedule.Schedule;

/**
 * A toolbar band
 */
public class Band implements java.io.Serializable, net.project.persistence.IXMLPersistence {
    /** Standard toolbar band, string name is "standard". */
    public static String STANDARD = "standard";

    /** Action toolbar band, string name is "action". */
    public static String ACTION = "action";

    /** Document toolbar band, string name is "document". */
    public static String DOCUMENT = "document";

    /** Discussion toolbar band, string name is "discussion". */
    public static String DISCUSSION = "discussion";

    /** Channel toolbar band, string name is "channel". */
    public static String CHANNEL = "channel";

    /** Schedule toolbar band, string name is "schedule". */
    public static String SCHEDULE = "schedule";
    
    /** Document toolbar band, string name is "document". */
    public static String TRASHCAN = "trashcan";
    
    /** Portfolio toolbar band, string name is "portfolio". */
    public static String PORTFOLIO = "portfolio";

    /** Name of band. */
    private String name = null;
    /** group heading can be specific to band */
    private String groupHeading = null;
    /** Show all buttons. */
    private boolean showAll = false;
    /** Show all button labels. */
    private boolean showLabels = false;
    /** Show all button images. */
    private boolean showImages = true;
    /** Enable all buttons. */
    private boolean enableAll = false;
    /** Buttons in this band. */
    private HashMap buttons = null;
    /** Order of buttons added to this band. */
    private ArrayList buttonOrder = null;

    /**
     * Creates new Band.
     */
    Band() {
        buttons = new HashMap();
        buttonOrder = new ArrayList();
    }

    /**
     * Set name of band.
     *
     * @param name the name of the band
     * @throws ToolbarException if an invalid name is given
     */
    public void setName(String name) throws ToolbarException {
        this.name = name;
        setButtons();
    }
    
    public String getName(){
    	return this.name;
    }
    
    public void setGroupHeading(String groupHeading) throws ToolbarException {
        this.groupHeading = groupHeading;
    }    
    
    public String getGroupHeading() {
        return this.groupHeading;
    }  
    /**
     * Set the showAll property.  This sets each button's show property.
     *
     * @param showAll value to set
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
        Iterator it = buttons.values().iterator();
        while (it.hasNext()) {
            ((Button)it.next()).setShow(showAll);
        }

    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        Iterator it = buttons.values().iterator();
        while (it.hasNext()) {
            ((Button)it.next()).setShowLabel(showLabels);
        }
    }

    public void setShowImages(boolean showImages) {
        this.showImages = showImages;
        Iterator it = buttons.values().iterator();
        while (it.hasNext()) {
            ((Button)it.next()).setShowImage(showImages);
        }
    }
    
    public void setEnableAll(boolean enableAll) {
        this.enableAll = enableAll;
        Iterator it = buttons.values().iterator();
        while (it.hasNext()) {
            ((Button)it.next()).setEnable(enableAll);
        }
    }
    
    private Button getButton(String typeName) {
    	if (typeName == null)
    		return null;
        Iterator it = buttons.values().iterator();
        while (it.hasNext()) {
        	Button button = (Button)it.next();
            if (typeName.equals( button.getName()) )
            	return button;            
        }
        return null;
    }
    /**
     * Add a button to band
     * @param typeName the name of the type of the button to add
     */
    public Button addButton(String typeName) throws ToolbarException {
        Button button = getButton(typeName);
        if (button == null) {
            throw new ToolbarException("Band '" + name + "' does not have button of type '" + typeName + "'");
        }
        button.setShowLabel(this.showLabels);
        button.setShowImage(this.showImages);
        button.setEnable(true);
        button.setShow(true);
        return button;
    }

    /**
     * Add a custom button to band.
     * Allows all properties of button to be specified.  Properties assumed to be literal text values.
     * This method also uses the label as the alt
     * text, it is not possible to specifiy other alt text
     * @see net.project.gui.toolbar.Band#addCustomButtonTokens
     */
    public Button addCustomButton(String imageEnabled, String imageDisabled, String imageOver,
        String label, String function) {

        // Create a new button based on literal values, using label for alt value
        Button button = new Button(ButtonType.CUSTOM, false, imageEnabled, imageDisabled, imageOver, label, label, function);
        addButton(button);
        button.setShowLabel(this.showLabels);
        button.setShowImage(this.showImages);
        button.setEnable(true);
        button.setShow(true);
        return button;
    }

    /**
     * Add a custom button to band based on token values.
     * Allows all properties of button to be specified.
     *
     * @param imageEnabledToken the property token representing the path to the
     * image to be displayed when button is enabled
     * @param imageDisabledToken the property token representing the path to the
     * image to be displayed when button is disabled
     * @param imageOverToken the property token representing the path to the
     * image to be displayed for an onMouseOver event
     * @param labelToken the property token representing the label for the
     * button
     * @param altToken the property token representing the alt text for the
     * button
     * @param function the href to be activated when button is clicked. Any HTML
     * href allowed (incl. javascript, URL etc.)
     * @return the newly created buton
     */
    public Button addCustomButtonTokens(String imageEnabledToken, String imageDisabledToken, String imageOverToken,
        String labelToken, String altToken, String function) {

        Button button = new Button(ButtonType.CUSTOM, imageEnabledToken, imageDisabledToken, imageOverToken, labelToken, altToken, function);
        addButton(button);
        button.setShowLabel(this.showLabels);
        button.setShowImage(this.showImages);
        button.setEnable(true);
        button.setShow(true);
        return button;
    }

    /**
     * Add the default buttons to the band
     */
    private void setButtons() throws ToolbarException {

        if (Band.ACTION.equals(name)) {
            addButton(new Button(ButtonType.CANCEL, "all.global.toolbar.action.cancel.image.on", null, null, "all.global.toolbar.action.cancel", "all.global.toolbar.action.cancel.alt", "javascript:cancel();"));
            addButton(new Button(ButtonType.RESET, "all.global.toolbar.action.reset.image.on", null, null, "all.global.toolbar.action.reset", "all.global.toolbar.action.reset.alt", "javascript:reset();"));
            addButton(new Button(ButtonType.UP, "all.global.toolbar.action.up.image.on", null, null, "all.global.toolbar.action.up", "all.global.toolbar.action.up.alt", "javascript:up();"));
            addButton(new Button(ButtonType.DOWN, "all.global.toolbar.action.down.image.on", null, null, "all.global.toolbar.action.down", "all.global.toolbar.action.down.alt", "javascript:down();"));
            addButton(new Button(ButtonType.BACK, "all.global.toolbar.action.back.image.on", null, null, "all.global.toolbar.action.back", "all.global.toolbar.action.back.alt", "javascript:back();"));
            addButton(new Button(ButtonType.UPDATE, "all.global.toolbar.action.update.image.on", null, null, "all.global.toolbar.action.update", "all.global.toolbar.action.update.alt", "javascript:update();"));
            addButton(new Button(ButtonType.JUMP, "all.global.toolbar.action.jump.image.on", null, null, "all.global.toolbar.action.jump", "all.global.toolbar.action.jump.alt", "javascript:jump();"));
            addButton(new Button(ButtonType.SUBMIT, "all.global.toolbar.action.submit.image.on", null, null, "all.global.toolbar.action.submit", "all.global.toolbar.action.submit.alt", "javascript:submit();"));
            addButton(new Button(ButtonType.ACCEPT, "all.global.toolbar.action.accept.image.on", null, null, "all.global.toolbar.action.accept", "all.global.toolbar.action.accept.alt", "javascript:accept();"));
            addButton(new Button(ButtonType.REPLY, "all.global.toolbar.action.reply.image.on", null, null, "all.global.toolbar.action.reply", "all.global.toolbar.action.reply.alt", "javascript:reply();"));
            addButton(new Button(ButtonType.PREVIOUS_POST, "all.global.toolbar.action.previous_post.image.on", null, null, "all.global.toolbar.action.previous_post", "all.global.toolbar.action.previous_post.alt", "javascript:previouspost();"));
            addButton(new Button(ButtonType.NEXT_POST, "all.global.toolbar.action.next_post.image.on", null, null, "all.global.toolbar.action.next_post", "all.global.toolbar.action.next_post.alt", "javascript:nextpost();"));
            addButton(new Button(ButtonType.NEW_POST, "all.global.toolbar.action.new_post.image.on", null, null, "all.global.toolbar.action.new_post", "all.global.toolbar.action.new_post.alt", "javascript:newpost();"));
            addButton(new Button(ButtonType.INFO, "all.global.toolbar.action.info.image.on", null, null, "all.global.toolbar.action.info", "all.global.toolbar.action.info.alt", "javascript:postinfo();"));
            addButton(new Button(ButtonType.MODIFY, "all.global.toolbar.action.modify.image.on", null, null, "all.global.toolbar.action.modify", "all.global.toolbar.action.modify.alt", "javascript:modify();"));
            addButton(new Button(ButtonType.DELETE, "all.global.toolbar.action.delete.image.on", null, null, "all.global.toolbar.action.delete", "all.global.toolbar.action.delete.alt", "javascript:delete();"));
            addButton(new Button(ButtonType.ADD, "all.global.toolbar.action.add.image.on", null, null, "all.global.toolbar.action.add", "all.global.toolbar.action.add.alt", "javascript:add();"));
            addButton(new Button(ButtonType.DECLINE, "all.global.toolbar.action.decline.image.on", null, null, "all.global.toolbar.action.decline", "all.global.toolbar.action.decline.alt", "javascript:decline();"));
            addButton(new Button(ButtonType.NEXT, "all.global.toolbar.action.next.image.on", null, null, "all.global.toolbar.action.next", "all.global.toolbar.action.next.alt", "javascript:next();"));
            addButton(new Button(ButtonType.FINISH, "all.global.toolbar.action.finish.image.on", null, null, "all.global.toolbar.action.finish", "all.global.toolbar.action.finish.alt", "javascript:finish();"));
            addButton(new Button(ButtonType.SEARCH, "all.global.toolbar.action.search.image.on", null, null, "all.global.toolbar.action.search", "all.global.toolbar.action.search.alt", "javascript:search();"));
            addButton(new Button(ButtonType.CREATE, "all.global.toolbar.action.create.image.on", null, null, "all.global.toolbar.action.create", "all.global.toolbar.action.create.alt", "javascript:create();"));

        } else if (Band.DOCUMENT.equals(name)) {
            addButton(new Button(ButtonType.CHECK_OUT, "all.global.toolbar.document.check_out.image.on", "all.global.toolbar.document.check_out.image.off", "all.global.toolbar.document.check_out.image.over", "all.global.toolbar.document.check_out", "all.global.toolbar.document.check_out.alt", "javascript:check_out();"));
            addButton(new Button(ButtonType.CHECK_IN, "all.global.toolbar.document.check_in.image.on", "all.global.toolbar.document.check_in.image.off", "all.global.toolbar.document.check_in.image.over", "all.global.toolbar.document.check_in", "all.global.toolbar.document.check_in.alt", "javascript:check_in();"));
            addButton(new Button(ButtonType.VIEW, "all.global.toolbar.document.view.image.on", "all.global.toolbar.document.view.image.off", "all.global.toolbar.document.view.image.over", "all.global.toolbar.document.view", "all.global.toolbar.document.view.alt", "javascript:view();"));
            addButton(new Button(ButtonType.UNDO_CHECK_OUT, "all.global.toolbar.document.undo_check_out.image.on", "all.global.toolbar.document.undo_check_out.image.off", "all.global.toolbar.document.undo_check_out.image.over", "all.global.toolbar.document.undo_check_out", "all.global.toolbar.document.undo_check_out.alt", "javascript:undo_check_out();"));
            addButton(new Button(ButtonType.CREATE_NEW_FOLDER, "all.global.toolbar.document.create_new_folder.image.on", "all.global.toolbar.document.create_new_folder.image.off", "all.global.toolbar.document.create_new_folder.image.over", "all.global.toolbar.document.create_new_folder", "all.global.toolbar.document.create_new_folder.alt", "javascript:new_folder();"));
            addButton(new Button(ButtonType.MOVE, "all.global.toolbar.document.move.image.on", "all.global.toolbar.document.move.image.off", "all.global.toolbar.document.move.image.over", "all.global.toolbar.document.move", "all.global.toolbar.document.move.alt", "javascript:move();"));
			//addButton(new Button(ButtonType.LIST_DELETED, "all.global.toolbar.standard.listdeleted.image.on", "all.global.toolbar.standard.listdeleted.image.off", "all.global.toolbar.standard.listdeleted.image.over", "all.global.toolbar.standard.listdeleted", "all.global.toolbar.standard.listdeleted.alt", "javascript:listdeleted();"));
			//addButton(new Button(ButtonType.UNDO_DELETE, "all.global.toolbar.standard.undodelete.image.on", "all.global.toolbar.standard.undodelete.image.off", "all.global.toolbar.standard.undodelete.image.over", "all.global.toolbar.undodelete.listdeleted", "all.global.toolbar.standard.undodelete.alt", "javascript:undodelete();"));

        } else if (Band.TRASHCAN.equals(name)) {
			addButton(new Button(ButtonType.UNDO_DELETE, "all.global.toolbar.standard.undodelete.image.on", "all.global.toolbar.standard.undodelete.image.off", "all.global.toolbar.standard.undodelete.image.over", "all.global.toolbar.undodelete.listdeleted", "all.global.toolbar.standard.undodelete.alt", "javascript:undodelete();"));

        }else if (Band.DISCUSSION.equals(name)) {
            addButton(new Button(ButtonType.REPLY, "all.global.toolbar.discussion.reply.image.on", "all.global.toolbar.discussion.reply.image.off", "all.global.toolbar.discussion.reply.image.over", "all.global.toolbar.discussion.reply", "all.global.toolbar.discussion.reply.alt", "javascript:reply();"));
            addButton(new Button(ButtonType.INFO_LINKS, "all.global.toolbar.discussion.info_links.image.on", "all.global.toolbar.discussion.info_links.image.off", "all.global.toolbar.discussion.info_links.image.over", "all.global.toolbar.discussion.info_links", "all.global.toolbar.discussion.info_links.alt", "javascript:postinfo();"));
            addButton(new Button(ButtonType.PREVIOUS_POST, "all.global.toolbar.discussion.previous_post.image.on", "all.global.toolbar.discussion.previous_post.image.off", "all.global.toolbar.discussion.previous_post.image.over", "all.global.toolbar.discussion.previous_post", "all.global.toolbar.discussion.previous_post.alt", "javascript:previouspost();"));
            addButton(new Button(ButtonType.NEXT_POST, "all.global.toolbar.discussion.next_post.image.on", "all.global.toolbar.discussion.next_post.image.off", "all.global.toolbar.discussion.next_post.image.over", "all.global.toolbar.discussion.next_post", "all.global.toolbar.discussion.next_post.alt", "javascript:nextpost();"));

        } else if (Band.CHANNEL.equals(name)) {
            addButton(new Button(ButtonType.EXPAND_ALL, "all.global.toolbar.channel.expandall.image.on", null, null, "all.global.toolbar.channel.expandall", "all.global.toolbar.channel.expandall.alt", "javascript:expand_all();"));
            addButton(new Button(ButtonType.COLLAPSE_ALL, "all.global.toolbar.channel.collapseall.image.on", null, null, "all.global.toolbar.channel.collapseall", "all.global.toolbar.channel.collapseall.alt", "javascript:collapse_all();"));
            addButton(new Button(ButtonType.CREATE, "all.global.toolbar.channel.create.image.on", null, null, "all.global.toolbar.channel.create", "all.global.toolbar.channel.create.alt", "javascript:create();"));
            addButton(new Button(ButtonType.MODIFY, "all.global.toolbar.channel.modify.image.on", null, null, "all.global.toolbar.channel.modify", "all.global.toolbar.channel.modify.alt", "javascript:modify();"));
            addButton(new Button(ButtonType.REMOVE, "all.global.toolbar.channel.remove.image.on", null, null, "all.global.toolbar.channel.remove", "all.global.toolbar.channel.remove.alt", "javascript:remove();"));
            addButton(new Button(ButtonType.PROPERTIES, "all.global.toolbar.channel.properties.image.on", null, null, "all.global.toolbar.channel.properties", "all.global.toolbar.channel.properties.alt", "javascript:properties();"));
            addButton(new Button(ButtonType.ALPHABETIZE, "all.global.toolbar.channel.alphabetize.image.on", null, null, "all.global.toolbar.channel.alphabetize", "all.global.toolbar.channel.alphabetize.alt", "javascript:alphabetize();"));
            addButton(new Button(ButtonType.DISCUSS, "all.global.toolbar.channel.discuss.image.on", null, null, "all.global.toolbar.channel.discuss", "all.global.toolbar.channel.discuss.alt", "javascript:discuss();"));
            addButton(new Button(ButtonType.DOCUMENTS, "all.global.toolbar.channel.documents.image.on", null, null, "all.global.toolbar.channel.documents", "all.global.toolbar.channel.documents.alt", "javascript:documents();"));
            addButton(new Button(ButtonType.SUBMIT, "all.global.toolbar.channel.submit.image.on", null, null, "all.global.toolbar.channel.submit", "all.global.toolbar.channel.submit.alt", "javascript:submit();"));
            addButton(new Button(ButtonType.RECALCULATE, "all.global.toolbar.channel.recalculate.image.on", null, null, "all.global.toolbar.channel.recalculate", "all.global.toolbar.channel.recalculate.alt", "javascript:recalculate();"));
            addButton(new Button(ButtonType.CALENDAR, "all.global.toolbar.channel.calendar.image.on", null, null, "all.global.toolbar.channel.calendar", "all.global.toolbar.channel.calendar.alt", "javascript:calendar():"));
            addButton(new Button(ButtonType.HELP, "all.global.toolbar.channel.help.image.on", null, null, "all.global.toolbar.channel.help", "all.global.toolbar.channel.help.alt", "javascript:help();"));

        } else if (Band.STANDARD.equals(name)) {
        	// Blog-it button 
            addButton(new Button(ButtonType.BLOGIT, "all.global.toolbar.standard.blogit.image.on", "all.global.toolbar.standard.blogit.image.off", "all.global.toolbar.standard.blogit.image.over", "all.global.toolbar.standard.blogit", "all.global.toolbar.standard.blogit.alt", "javascript:blogit();"));
            //others buttons
            addButton(new Button(ButtonType.CREATE, "all.global.toolbar.standard.create.image.on", "all.global.toolbar.standard.create.image.off", "all.global.toolbar.standard.create.image.over", "all.global.toolbar.standard.create", "all.global.toolbar.standard.create.alt", "javascript:create();"));
            addButton(new Button(ButtonType.MODIFY, "all.global.toolbar.standard.modify.image.on", "all.global.toolbar.standard.modify.image.off", "all.global.toolbar.standard.modify.image.over", "all.global.toolbar.standard.modify", "all.global.toolbar.standard.modify.alt", "javascript:modify();"));
            addButton(new Button(ButtonType.REMOVE, "all.global.toolbar.standard.remove.image.on", "all.global.toolbar.standard.remove.image.off", "all.global.toolbar.standard.remove.image.over", "all.global.toolbar.standard.remove", "all.global.toolbar.standard.remove.alt", "javascript:remove();"));
            if (PropertyProvider.getBoolean("prm.crossspace.isenabled", false)) {
                addButton(new Button(ButtonType.ADD_EXTERNAL, "all.global.toolbar.standard.addexternal.image.on", "all.global.toolbar.standard.addexternal.image.off", "all.global.toolbar.standard.addexternal.image.over", "all.global.toolbar.standard.addexternal", "all.global.toolbar.standard.addexternal.alt", "javascript:addExternal();"));
                addButton(new Button(ButtonType.SHARE,  "all.global.toolbar.standard.share.image.on", "all.global.toolbar.standard.share.image.off", "all.global.toolbar.standard.share.image.over", "all.global.toolbar.standard.share", "all.global.toolbar.standard.share.alt", "javascript:share();"));
            }            
            addButton(new Button(ButtonType.REFRESH, "all.global.toolbar.standard.refresh.image.on", "all.global.toolbar.standard.refresh.image.off", "all.global.toolbar.standard.refresh.image.over", "all.global.toolbar.standard.refresh", "all.global.toolbar.standard.refresh.alt", "javascript:reset();"));
            addButton(new Button(ButtonType.PROPERTIES, "all.global.toolbar.standard.properties.image.on", "all.global.toolbar.standard.properties.image.off", "all.global.toolbar.standard.properties.image.over", "all.global.toolbar.standard.properties", "all.global.toolbar.standard.properties.alt", "javascript:properties();"));
            addButton(new Button(ButtonType.COPY, "all.global.toolbar.standard.copy.image.on", "all.global.toolbar.standard.copy.image.off", "all.global.toolbar.standard.copy.image.over", "all.global.toolbar.standard.copy", "all.global.toolbar.standard.copy.alt", "javascript:copy();"));
            addButton(new Button(ButtonType.LINK, "all.global.toolbar.standard.link.image.on", "all.global.toolbar.standard.link.image.off", "all.global.toolbar.standard.link.image.over", "all.global.toolbar.standard.link", "all.global.toolbar.standard.link.alt", "javascript:link();"));
            addButton(new Button(ButtonType.WORKFLOW, "all.global.toolbar.standard.workflow.image.on", "all.global.toolbar.standard.workflow.image.off", "all.global.toolbar.standard.workflow.image.over", "all.global.toolbar.standard.workflow", "all.global.toolbar.standard.workflow.alt", "javascript:workflow();"));
            addButton(new Button(ButtonType.SEARCH, "all.global.toolbar.standard.search.image.on", "all.global.toolbar.standard.search.image.off", "all.global.toolbar.standard.search.image.over", "all.global.toolbar.standard.search", "all.global.toolbar.standard.search.alt", "javascript:search();"));
            addButton(new Button(ButtonType.CAPTURE_WORK, "all.global.toolbar.standard.capturework.image.on", "all.global.toolbar.standard.capturework.image.off", "all.global.toolbar.standard.capturework.image.over", "all.global.toolbar.standard.capturework", "all.global.toolbar.standard.capturework.alt", "javascript:captureWork();"));
            addButton(new Button(ButtonType.NOTIFY, "all.global.toolbar.standard.notify.image.on", "all.global.toolbar.standard.notify.image.off", "all.global.toolbar.standard.notify.image.over", "all.global.toolbar.standard.notify", "all.global.toolbar.standard.notify.alt", "javascript:notify();"));
            addButton(new Button(ButtonType.HELP, "all.global.toolbar.standard.help.image.on", "all.global.toolbar.standard.help.image.off", "all.global.toolbar.standard.help.image.over", "all.global.toolbar.standard.help", "all.global.toolbar.standard.help.alt", "javascript:help();"));
            // import export button
            if (PropertyProvider.getBoolean("prm.schedule.main.importxml.enabled", false)) {
                addButton(new Button(ButtonType.IMPORT, "all.global.toolbar.standard.import.image.on", "all.global.toolbar.standard.import.image.off", "all.global.toolbar.standard.import.image.over", "all.global.toolbar.standard.import", "all.global.toolbar.standard.import.alt", "javascript:importMSP();"));
                addButton(new Button(ButtonType.EXPORT, "all.global.toolbar.standard.export.image.on", "all.global.toolbar.standard.export.image.off", "all.global.toolbar.standard.export.image.over", "all.global.toolbar.standard.export", "all.global.toolbar.standard.export.alt", "javascript:exportMSP();"));
            }
            addButton(new Button(ButtonType.EXPORT_CUSTOM, "all.global.toolbar.standard.export.image.on", "all.global.toolbar.standard.export.image.off", "all.global.toolbar.standard.export.image.over", "all.global.toolbar.standard.export.tasks", "all.global.toolbar.standard.export.tasks.alt", "javascript:exportTasks();"));

            addButton(new Button(ButtonType.SECURITY, "all.global.toolbar.standard.security.image.on", "all.global.toolbar.standard.security.image.off", "all.global.toolbar.standard.security.image.over", "all.global.toolbar.standard.security", "all.global.toolbar.standard.security.alt", "javascript:security();"));
            addButton(new Button(ButtonType.WIKI, "all.global.toolbar.standard.wiki.image.on", "all.global.toolbar.standard.wiki.image.off", "all.global.toolbar.standard.wiki.image.over", "all.global.toolbar.standard.wiki", "all.global.toolbar.standard.wiki.alt", "javascript:wiki();"));
            addButton(new Button(ButtonType.BULKINVITATION, "all.global.toolbar.standard.create.image.on", "all.global.toolbar.standard.create.image.off", "all.global.toolbar.standard.create.image.over", "all.global.toolbar.standard.bulkinvitation", "all.global.toolbar.standard.bulkinvitation.alt", "javascript:bulkInvitation();"));	
            addButton(new Button(ButtonType.PERSONALIZE_PAGE, "all.global.toolbar.standard.personalizepage.image.on", "all.global.toolbar.standard.personalizepage.image.on", "all.global.toolbar.standard.personalizepage.image.over", "all.global.toolbar.standard.personalizepage.label", "all.global.toolbar.standard.personalizepage.alt", "javascript:personalizePage();"));	
            
			addButton(new Button(ButtonType.EXPAND_ALL, "all.global.toolbar.standard.expandall.image.on", null, "all.global.toolbar.standard.expandall.image.over", "all.global.toolbar.channel.expandall", "all.global.toolbar.channel.expandall.alt", "javascript:expand_all();"));
			addButton(new Button(ButtonType.COLLAPSE_ALL, "all.global.toolbar.standard.collapseall.image.on", null, "all.global.toolbar.standard.collapseall.image.over", "all.global.toolbar.channel.collapseall", "all.global.toolbar.channel.collapseall.alt", "javascript:collapse_all();"));
                    
        } else if (Band.SCHEDULE.equals(name)) {
            addButton(new Button(ButtonType.TASK_UP, "all.global.toolbar.schedule.taskup.on", "all.global.toolbar.schedule.taskup.off", "all.global.toolbar.schedule.taskup.over", "all.global.toolbar.schedule.taskup", "all.global.toolbar.schedule.taskup.alt", "javascript:taskup();"));
            addButton(new Button(ButtonType.TASK_DOWN, "all.global.toolbar.schedule.taskdown.on", "all.global.toolbar.schedule.taskdown.off", "all.global.toolbar.schedule.taskdown.over", "all.global.toolbar.schedule.taskdown", "all.global.toolbar.schedule.taskdown.alt", "javascript:taskdown();"));
            addButton(new Button(ButtonType.TASK_LEFT, "all.global.toolbar.schedule.taskleft.on", "all.global.toolbar.schedule.taskleft.off", "all.global.toolbar.schedule.taskleft.over", "all.global.toolbar.schedule.taskleft", "all.global.toolbar.schedule.taskleft.alt", "javascript:unindent();"));
            addButton(new Button(ButtonType.TASK_RIGHT, "all.global.toolbar.schedule.taskright.on", "all.global.toolbar.schedule.taskright.off", "all.global.toolbar.schedule.taskright.over", "all.global.toolbar.schedule.taskright", "all.global.toolbar.schedule.taskright.alt", "javascript:indent();"));
            addButton(new Button(ButtonType.RESOURCES, "all.global.toolbar.schedule.resources.on", "all.global.toolbar.schedule.resources.off", "all.global.toolbar.schedule.resources.over", "all.global.toolbar.schedule.resources", "all.global.toolbar.schedule.resources.alt", "javascript:resources();"));
            addButton(new Button(ButtonType.MATERIALS, "all.global.toolbar.standard.materials.image.on", "all.global.toolbar.standard.materials.image.off", "all.global.toolbar.standard.materials.image.over", "all.global.toolbar.standard.materials", "all.global.toolbar.standard.materials.alt", "javascript:materials();"));
            addButton(new Button(ButtonType.PERCENTAGE, "all.global.toolbar.schedule.percentage.on", "all.global.toolbar.schedule.percentage.off", "all.global.toolbar.schedule.percentage.over", "all.global.toolbar.schedule.percentage", "all.global.toolbar.schedule.percentage.alt", "javascript:percentage();"));
            addButton(new Button(ButtonType.CHOOSE_PHASE, "all.global.toolbar.schedule.phase.on", "all.global.toolbar.schedule.phase.off", "all.global.toolbar.schedule.phase.over", "all.global.toolbar.schedule.phase", "all.global.toolbar.schedule.phase.alt", "javascript:phase();"));
            addButton(new Button(ButtonType.LINK_TASKS, "all.global.toolbar.schedule.linktasks.on", "all.global.toolbar.schedule.linktasks.off", "all.global.toolbar.schedule.linktasks.over", "all.global.toolbar.schedule.linktasks", "all.global.toolbar.schedule.linktasks.alt", "javascript:linkTasks();"));
            addButton(new Button(ButtonType.UNLINK_TASKS, "all.global.toolbar.schedule.unlinktasks.on", "all.global.toolbar.schedule.unlinktasks.off", "all.global.toolbar.schedule.unlinktasks.over", "all.global.toolbar.schedule.unlinktasks", "all.global.toolbar.schedule.unlinktasks.alt", "javascript:unlinkTasks();"));
            addButton(new Button(ButtonType.RECALCULATE, "all.global.toolbar.schedule.recalculate.on", "all.global.toolbar.schedule.recalculate.off", "all.global.toolbar.schedule.recalculate.over", "all.global.toolbar.schedule.recalculate", "all.global.toolbar.schedule.recalculate.alt", "javascript:recalculate();"));
            addButton(new Button(ButtonType.PROPERTIES, "all.global.toolbar.standard.properties.image.on", "all.global.toolbar.standard.properties.image.off", "all.global.toolbar.standard.properties.image.over", "all.global.toolbar.standard.properties", "all.global.toolbar.standard.properties.alt", "javascript:scheduleProperty();"));
            addButton(new Button(ButtonType.EXPORT_PDF, "all.global.toolbar.standard.export.image.on", "all.global.toolbar.standard.export.image.off", "all.global.toolbar.standard.export.image.over", "all.global.toolbar.standard.exportpdf", "all.global.toolbar.standard.exportpdf.alt", "javascript:exportPDF();"));

        } else if (Band.PORTFOLIO.equals(name)) {
        	addButton(new Button(ButtonType.SAVE_CURRENT_SETTINGS, "all.global.toolbar.portfolio.savecurrentsettings.image.on", "all.global.toolbar.portfolio.savecurrentsettings.image.on", "all.global.toolbar.portfolio.savecurrentsettings.image.over", "prm.project.portfolio.toolbox.item.savecurrentsettings.label", "prm.project.portfolio.toolbox.item.savecurrentsettings.label", "javascript:saveCurrentSettings();"));
        	addButton(new Button(ButtonType.DELETE_SAVED_VIEWS, "all.global.toolbar.standard.remove.image.on", "all.global.toolbar.standard.remove.image.off", "all.global.toolbar.standard.remove.image.over", "prm.project.portfolio.toolbox.item.deletesavedview.label", "prm.project.portfolio.toolbox.item.deletesavedview.label", "javascript:deleteViews();"));
        	addButton(new Button(ButtonType.EXPORT_PDF, "all.global.toolbar.portfolio.export.image.on", "all.global.toolbar.portfolio.export.image.on", "all.global.toolbar.portfolio.export.image.over", "prm.project.portfolio.toolbox.item.exportpdf.label", "prm.project.portfolio.toolbox.item.exportpdf.label", "javascript:exportPDF();"));
        	addButton(new Button(ButtonType.EXPORT_EXCEL, "all.global.toolbar.portfolio.export.image.on", "all.global.toolbar.portfolio.export.image.on", "all.global.toolbar.portfolio.export.image.over", "prm.resource.timesheet.exporttoexcel.link", "prm.resource.timesheet.exporttoexcel.link", "javascript:exportExcel();"));
        	addButton(new Button(ButtonType.EXPORT_CSV, "all.global.toolbar.portfolio.export.image.on", "all.global.toolbar.portfolio.export.image.on", "all.global.toolbar.portfolio.export.image.over", "prm.project.portfolio.toolbox.item.exportcsv.label", "prm.project.portfolio.toolbox.item.exportcsv.label", "javascript:exportCSV();"));
        } else {
            throw new ToolbarException("Invalid band name: " + name);
        }

    }

    /**
     * Add button to the button table
     * @param button the button to add
     */
    private void addButton(Button button) {
        buttons.put(button.getLabel(), button);
        buttonOrder.add(button.getLabel());
    }

    /**
     * Return list of buttons in the order they were added
     * @return list of buttons
     */
    ArrayList getButtons() {
        ArrayList buttonList = new ArrayList();
        Iterator it = buttonOrder.iterator();
        while (it.hasNext()) {
            buttonList.add(buttons.get(it.next()));
        }
        return buttonList;
    }

    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Return the XML for a band, including its buttons
     * @return the xml string
     */
    public String getXMLBody() {
        Iterator it = null;
        StringBuffer xml = new StringBuffer();
        xml.append("<band>");

        it = buttons.values().iterator();
        while (it.hasNext()) {
            xml.append(((Button)it.next()).getXMLBody());
        }
        xml.append("</band>");
        return xml.toString();
    }

}
