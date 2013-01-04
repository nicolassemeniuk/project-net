/*-------------------------
 * (c)2002 Matthew Flower
 * All rights reserved
 *-------------------------*/
package net.project.devtools.pnetplugin;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.diagnostic.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.jdom.Element;
import java.util.List;

/**
 * Plugin class required by IntelliJ to create a plugin.
 *
 * @author Matthew Flower
 * @since Version 0.l
 */
public class PnetPlugin implements ProjectComponent, Configurable, JDOMExternalizable {
    private static final Logger logger = Logger.getInstance(PnetPlugin.class.getName());
    private SettingsPanel settingsPanel;
    private Settings settings = new Settings();

    public Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }

        return settings;
    }

    /**
     * Invoked when the project corresponding to this component instance is opened.<p>
     * Note that components may be created for even unopened projects and this method can be never
     * invoked for a particular component intance (for example for default project).
     */
    public void projectOpened() {
    }

    /**
     * Invoked when the project corresponding to this component instance is closed.<p>
     * Note that components may be created for even unopened projects and this method can be never
     * invoked for a particular component intance (for example for default project).
     */
    public void projectClosed() {
    }

    /**
     * Unique name of this component. If there is another component with the same name or
     * name is null internal assertion will occur.
     *
     * @return the name of this component
     */
    public String getComponentName() {
        return "ProjectNet.PnetPlugin";
    }

    /**
     *  Component should do initialization and communication with another components in this method.
     */
    public void initComponent() {
    }

    /**
     *  Component should dispose system resources or perform another cleanup in this method.
     */
    public void disposeComponent() {
    }

    public String getDisplayName() {
        return "PnetPlugin";
    }

    public Icon getIcon() {
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new SettingsPanel(getSettings());
        }

        return settingsPanel;
    }

    public boolean isModified() {
        logger.info("isModified() called.  ("+!settingsPanel.getDatabaseAlias().equals(getSettings().database)+")");

        boolean isModified = false;
        isModified = isModified || !settingsPanel.getDatabaseAlias().trim().equals(getSettings().database);
        isModified = isModified || !settingsPanel.getDBUsername().trim().equals(getSettings().dbUserName);
        isModified = isModified || !settingsPanel.getDBPassword().trim().equals(getSettings().dbPassword);
        isModified = isModified || !settingsPanel.getCCDatabaseAlias().trim().equals(getSettings().ccDatabase);

        return isModified;
    }

    /**
     * Store the settings from configurable to other components.
     */
    public void apply() throws ConfigurationException {
        getSettings().database = settingsPanel.getDatabaseAlias().trim();
        getSettings().ccDatabase = settingsPanel.getCCDatabaseAlias().trim();
        getSettings().dbUserName = settingsPanel.getDBUsername().trim();
        getSettings().dbPassword = settingsPanel.getDBPassword().trim();

        logger.info("Settings Applied for PnetPlugin");
        logger.info("database="+getSettings().database);
    }

    /**
     * Load settings from other components to configurable.
     */
    public void reset() {
    }

    public void disposeUIResources() {
        settingsPanel = null;
    }

    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(settings, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(settings, element);
    }
}
