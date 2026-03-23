package net.alperemre.gui.components;

import javax.swing.*;
import java.awt.*;

// panel with the script-related buttons, "Run script from file" and "Create and run ad hoc script".
// the parent frame can grab these buttons and add ActionListeners as needed.

public class ScriptButtonsPanel extends JPanel {

    private JButton runScriptFileButton;
    private JButton runAdHocScriptButton;

    public ScriptButtonsPanel() {
        super(new FlowLayout(FlowLayout.CENTER, 15, 5));

        runScriptFileButton = new JButton("Run script from file");
        runAdHocScriptButton = new JButton("Create and run ad hoc script");

        add(runScriptFileButton);
        add(runAdHocScriptButton);
    }

    public JButton getRunScriptFileButton() {
        return runScriptFileButton;
    }

    public JButton getRunAdHocScriptButton() {
        return runAdHocScriptButton;
    }
}
