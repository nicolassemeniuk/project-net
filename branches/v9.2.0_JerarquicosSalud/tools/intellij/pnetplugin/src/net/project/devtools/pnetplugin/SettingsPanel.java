/*-------------------------
 * (c)2002 Matthew Flower
 * All rights reserved
 *-------------------------*/
package net.project.devtools.pnetplugin;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**
 * This class represents the panel of options that appears in the project
 * options.
 *
 * @author Matthew Flower
 * @since Version 0.2
 */
public class SettingsPanel extends JPanel {
    Settings settings;
    private JTextField databaseName;
    private JTextField ccDatabaseName;
    private JTextField dbUserName;
    private JPasswordField dbPassword;

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public SettingsPanel(Settings settings) {
        this.settings = settings;

        //Build the dialog box
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);

        //Set up constraints for all of the text boxes
        GridBagConstraints textBoxConstraints = new GridBagConstraints();
        textBoxConstraints.gridwidth = GridBagConstraints.REMAINDER;
        textBoxConstraints.anchor = GridBagConstraints.WEST;
        textBoxConstraints.ipadx = 4;
        textBoxConstraints.ipady = 2;

        //Set up constraints for all of the labels
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridwidth = GridBagConstraints.RELATIVE;
        labelConstraints.anchor = GridBagConstraints.EAST;
        labelConstraints.ipadx = 4;
        labelConstraints.ipady = 2;

        JLabel databaseNameLabel = new JLabel("Development Database: ");
        layout.setConstraints(databaseNameLabel, labelConstraints);
        this.add(databaseNameLabel);

        databaseName = new JTextField(settings.database, 45);
        layout.setConstraints(databaseName, textBoxConstraints);
        this.add(databaseName);

        JLabel ccDatabaseLabel = new JLabel("CC Database: ");
        layout.setConstraints(ccDatabaseLabel, labelConstraints);
        this.add(ccDatabaseLabel);

        ccDatabaseName = new JTextField(settings.ccDatabase, 45);
        layout.setConstraints(ccDatabaseName, textBoxConstraints);
        this.add(ccDatabaseName);

        JLabel dbUsernameLabel = new JLabel("Database Username: ");
        layout.setConstraints(dbUsernameLabel, labelConstraints);
        this.add(dbUsernameLabel);

        dbUserName = new JTextField(settings.dbUserName, 15);
        layout.setConstraints(dbUserName, textBoxConstraints);
        this.add(dbUserName);

        JLabel dbPasswordLabel = new JLabel("Database Password: ");
        layout.setConstraints(dbPasswordLabel, labelConstraints);
        this.add(dbPasswordLabel);

        dbPassword = new JPasswordField(settings.dbPassword, 15);
        layout.setConstraints(dbPassword, textBoxConstraints);
        this.add(dbPassword);

        //Create dummy components to take up the horizontal and vertical space
        JLabel label1 = new JLabel();
        this.add(label1);
        GridBagConstraints spaceFiller = new GridBagConstraints();
        spaceFiller.weightx = 1.0;
        spaceFiller.weighty = 1.0;
        JLabel label2 = new JLabel();
        layout.setConstraints(label2, spaceFiller);
        this.add(label2);
    }

    public String getDatabaseAlias() {
        return databaseName.getText();
    }

    public String getDBPassword() {
        return new String(dbPassword.getPassword());
    }

    public String getDBUsername() {
        return dbUserName.getText();
    }

    public String getCCDatabaseAlias() {
        return ccDatabaseName.getText();
    }
}
