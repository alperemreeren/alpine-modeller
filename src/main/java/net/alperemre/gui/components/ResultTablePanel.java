package net.alperemre.gui.components;

import net.alperemre.controllers.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;


// panel containing a JTable + logic to rebuild data from the binding.
public class ResultTablePanel extends JPanel {

    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ResultTablePanel() {
        super();
        setLayout(new java.awt.BorderLayout());

        tableModel = new DefaultTableModel();
        resultsTable = new JTable(tableModel);

        JScrollPane tableScroll = new JScrollPane(resultsTable);
        add(tableScroll, java.awt.BorderLayout.CENTER);
    }

    // fetch arrays from the controller's binding and updates the table.
    public void updateTableFromBinding(Controller controller) {
        Map<String, double[]> arrays = controller.getBoundArrays();

        if (arrays.isEmpty()) {
            tableModel.setDataVector(null, new Object[] {"No Results"});
            return;
        }

        // check if there's a LATA array
        double[] lata = arrays.get("LATA");
        int rowCount;

        if (lata != null) {
            rowCount = lata.length;
        } else {
            // fallback: use the length of the first array
            rowCount = arrays.values().iterator().next().length;
        }

        // column names are year, plus the rest of the arrays
        java.util.List<String> columnNames = new ArrayList<>();
        columnNames.add("Year");

        // gather all array variable names except LATA
        java.util.List<String> varNames = new ArrayList<>(arrays.keySet());
        varNames.remove("LATA");

        columnNames.addAll(varNames);

        Object[][] rowData = new Object[rowCount][columnNames.size()];

        int baseYear = 2015;

        for (int i = 0; i < rowCount; i++) {
            // year column
            if (lata != null) {
                rowData[i][0] = (int) lata[i]; // cast double to int
            } else {
                rowData[i][0] = baseYear + i;
            }

            // other columns
            for (int col = 1; col < columnNames.size(); col++) {
                String var = columnNames.get(col);
                double[] arr = arrays.get(var);

                rowData[i][col] = String.format("%.4f", arr[i]);
            }
        }

        tableModel.setDataVector(rowData, columnNames.toArray());
    }
}
