/*-------------------------
 * (c)2002 Matthew Flower
 * All rights reserved
 *-------------------------*/
package net.project.devtools.pnetplugin;

import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class responds to the keystroke to add a new property while replacing
 * the existing text.
 *
 * @author Matthew Flower
 * @since Version 0.1
 */
public class DeletePropertyAction extends EditorAction {
    private static class AddPropertyEditorAction extends EditorWriteActionHandler {
        public void executeWriteAction(Editor editor, DataContext dataContext) {
            Document document = editor.getDocument();
            SelectionModel selectionModel = editor.getSelectionModel();

            //Get the text that we are going to add as a property
            String selectedText = selectionModel.getSelectedText();
            if (selectedText == null) {
                selectedText = "";
            } else {
                if (selectedText.startsWith("\"")) {
                    selectedText = selectedText.substring(1);
                }
                if (selectedText.endsWith("\"")) {
                    selectedText = selectedText.substring(0, selectedText.length()-1);
                }
            }

            String replacementValue = "";

            int result = JOptionPane.showConfirmDialog(null, "Delete property \""+selectedText+"\"?", "Delete Property", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                Settings settings = Settings.getInstance(editor.getProject());

                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
                    Connection connection = java.sql.DriverManager.getConnection(settings.database,
                        settings.dbUserName, settings.dbPassword);
                    try {
                        //Get the existing property value
                        PreparedStatement ps = connection.prepareStatement(
                            "select " +
                            "  property_value " +
                            "from " +
                            "  pn_property " +
                            "where " +
                            "  property = ?"
                        );

                        ps.setString(1, selectedText);
                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            replacementValue = rs.getString("property_value");
                        } else {
                            JOptionPane.showMessageDialog(null, "Property \""+selectedText+"\" not found.");
                            return;
                        }

                        ps = connection.prepareStatement(
                            "delete from pn_property " +
                            "where property = ?"
                        );
                        ps.setString(1, selectedText);
                        ps.executeUpdate();
                    } finally {
                        connection.close();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Unexpected exception: " + e);
                }
                
                document.deleteString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
                document.insertString(selectionModel.getSelectionStart(), replacementValue);
            }

        }
    }

    public DeletePropertyAction() {
        super(new AddPropertyEditorAction());
    }
}

