package net.alperemre.gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * component that hosts:
 *  - A list of model names
 *  - A list of data files
 *  - A "Run model" button
 *
 *  it exposes getters to retrieve the selected model, data, and the button itself.
 */
public class ModelDataPanel extends JPanel {

    private JList<String> modelList;
    private JList<String> dataList;
    private JButton runModelButton;

    public ModelDataPanel() {
        super(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel selectLabel = new JLabel("Select model and data");
        selectLabel.setFont(selectLabel.getFont().deriveFont(Font.BOLD, 14f));

        gbc.gridy = 0;
        add(selectLabel, gbc);

        // model list
        modelList = new JList<>();
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane modelScroll = new JScrollPane(modelList);
        modelScroll.setBorder(BorderFactory.createTitledBorder("Models"));

        gbc.gridy = 1;
        add(modelScroll, gbc);

        // data list
        dataList = new JList<>();
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane dataScroll = new JScrollPane(dataList);
        dataScroll.setBorder(BorderFactory.createTitledBorder("Data files"));

        gbc.gridy = 2;
        add(dataScroll, gbc);

        // run model button
        runModelButton = new JButton("Run model");

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        add(runModelButton, gbc);
    }

    // setters
    public void setModelListData(List<String> modelNames) {
        modelList.setListData(modelNames.toArray(new String[0]));
        if (!modelNames.isEmpty()) {
            modelList.setSelectedIndex(0);
        }
    }

    public void setDataListData(List<String> dataFiles) {
        dataList.setListData(dataFiles.toArray(new String[0]));
        if (!dataFiles.isEmpty()) {
            dataList.setSelectedIndex(0);
        }
    }

    // getters
    public String getSelectedModel() {
        return modelList.getSelectedValue();
    }

    public String getSelectedData() {
        return dataList.getSelectedValue();
    }

    public JButton getRunModelButton() {
        return runModelButton;
    }
}