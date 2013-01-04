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
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * This class responds to the keystroke to add a new property while replacing
 * the existing text.
 *
 * @author Matthew Flower
 * @since Version 0.1
 */
public class ModifyPropertyAction extends EditorAction {
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

            //Show the add property dialog box in the middle of the screen.
            JFrame frame = (JFrame)SwingUtilities.getRoot(editor.getComponent());
            AddPropertyDialog apd = new AddPropertyDialog(frame, true, Settings.getInstance(editor.getProject()));
            apd.setPropertyName(selectedText);
            apd.setPropertyValueFromDB(selectedText);
            apd.setModifyMode(true);
            apd.show();

            if (apd.propertyAdded) {
                if (!selectedText.equals(apd.getAddedProperty())) {
                    document.deleteString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd());
                    document.insertString(selectionModel.getSelectionStart(), apd.getAddedProperty());
                }
            }
        }
    }

    public ModifyPropertyAction() {
        super(new AddPropertyEditorAction());
    }
}


