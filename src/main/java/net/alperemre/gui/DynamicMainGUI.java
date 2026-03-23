package net.alperemre.gui;

import net.alperemre.models.BaseModel;
import net.alperemre.controllers.Controller;
import net.alperemre.util.ModelDiscovery;
import net.alperemre.gui.components.ModelDataPanel;
import net.alperemre.gui.components.ResultTablePanel;
import net.alperemre.gui.components.ScriptButtonsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main JFrame that orchestrates the ModelDataPanel, ResultTablePanel, and ScriptButtonsPanel.
 */
public class DynamicMainGUI extends JFrame {

    private ModelDataPanel modelDataPanel;
    private ResultTablePanel resultTablePanel;
    private ScriptButtonsPanel scriptButtonsPanel;

    // Mapping modelName -> Class
    private Map<String, Class<? extends BaseModel>> modelClassMap = new HashMap<>();

    // The controller (will be assigned after user picks a model/data)
    private Controller controller;

    public DynamicMainGUI() {
        super("ALPINE Modeller");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1) Optional top label
        JLabel titleLabel = new JLabel("ALPINE INC. Modeller", JLabel.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        add(titleLabel, BorderLayout.NORTH);

        // 2) Build sub-panels
        modelDataPanel = new ModelDataPanel();
        resultTablePanel = new ResultTablePanel();
        scriptButtonsPanel = new ScriptButtonsPanel();

        // 3) Fill model list with discovered model classes
        Set<Class<? extends BaseModel>> foundModels = ModelDiscovery.findAllModelClasses();
        List<String> modelNames = foundModels.stream()
                .map(Class::getSimpleName)
                .sorted()
                .collect(Collectors.toList());

        for (Class<? extends BaseModel> cl : foundModels) {
            modelClassMap.put(cl.getSimpleName(), cl);
        }
        modelDataPanel.setModelListData(modelNames);

        // 4) Fill data list from "data" folder
        File dataFolder = new File("data");
        String[] dataFiles = dataFolder.list((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (dataFiles == null) dataFiles = new String[0];
        Arrays.sort(dataFiles);
        modelDataPanel.setDataListData(Arrays.asList(dataFiles));

        // 5) Layout:
        // Left side: modelDataPanel, center: tablePanel, bottom: scriptButtonsPanel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(modelDataPanel, BorderLayout.WEST);
        centerPanel.add(resultTablePanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(scriptButtonsPanel, BorderLayout.SOUTH);

        // 6) Wire up action listeners
        modelDataPanel.getRunModelButton().addActionListener(this::onRunModel);

        scriptButtonsPanel.getRunScriptFileButton().addActionListener(this::onRunScriptFile);
        scriptButtonsPanel.getRunAdHocScriptButton().addActionListener(this::onRunAdHocScript);

        // Final size
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void onRunModel(ActionEvent e) {
        String selectedModelName = modelDataPanel.getSelectedModel();
        String selectedData = modelDataPanel.getSelectedData();
        if (selectedModelName == null || selectedData == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a model and a data file.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // base initialization
            Class<? extends BaseModel> cl = modelClassMap.get(selectedModelName);
            BaseModel modelInstance = cl.getDeclaredConstructor().newInstance();

            // controller initialization
            controller = new Controller(modelInstance);

            // fetch the data and run the model
            controller.readDataFrom("data/" + selectedData);
            controller.runModel();

            // update data panel
            resultTablePanel.updateTableFromBinding(controller);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error running model: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRunScriptFile(ActionEvent e) {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Please run a model first before executing a script.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser("scripts");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Groovy files",
                "groovy", "txt"));

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                controller.runScriptFromFile(file.getAbsolutePath());
                // Rebuild table from the binding
                resultTablePanel.updateTableFromBinding(controller);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error running script: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onRunAdHocScript(ActionEvent e) {
        if (controller == null) {
            JOptionPane.showMessageDialog(this,
                    "Please run a model first before executing a script.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showAdHocScriptDialog();
    }

    private void showAdHocScriptDialog() {
        JDialog dialog = new JDialog(this, "Ad hoc script", true);

        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea scriptArea = new JTextArea(10, 50);

        scriptArea.setLineWrap(true);
        scriptArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(scriptArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton("Ok");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(ae -> {
            String scriptText = scriptArea.getText().trim();

            if (!scriptText.isEmpty()) {
                try {
                    controller.runScript(scriptText);
                    resultTablePanel.updateTableFromBinding(controller);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                            "Error running ad hoc script: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            dialog.dispose();
        });

        cancelButton.addActionListener(ae -> dialog.dispose());

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DynamicMainGUI gui = new DynamicMainGUI();
            gui.setVisible(true);
        });
    }
}
