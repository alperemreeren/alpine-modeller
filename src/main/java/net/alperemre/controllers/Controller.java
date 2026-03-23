package net.alperemre.controllers;

import net.alperemre.models.BaseModel;
import net.alperemre.models.Model1;
import net.alperemre.models.Model2;
import net.alperemre.models.Model3;
import net.alperemre.annotations.Bind;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


// handling model selection, data loading, calculation, and script execution.

public class Controller {
    private BaseModel model;
    private Map<String, double[]> dataMap = new HashMap<>();
    private Binding binding;

    public Controller(BaseModel modelInstance) {
        this.model = modelInstance;
        this.binding = new Binding();
    }

    public Controller(String modelName) {
        switch (modelName) {
            case "Model1":
                model = new Model1();
                break;
            case "Model2":
                model = new Model2();
                break;
            case "Model3":
                model = new Model3();
                break;
            default:
                throw new IllegalArgumentException("Unknown model: " + modelName);
        }
        binding = new Binding();
    }

    public void readDataFrom(String fname) throws Exception {
        dataMap.clear();

        // First, read the entire file line by line, store them temporarily
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        }

        // phase 1: counting the columns in LATA row = LL and aligning the lines in "dataMap".
        int LL = -1;
        for (String line : lines) {
            String[] tokens = line.split("\\s+"); // split by whitespace
            if (tokens[0].equalsIgnoreCase("LATA")) {
                // rest of tokens are years, 2015, 2016 etc.
                LL = tokens.length - 1;

                double[] yearVals = new double[LL];
                for (int i = 0; i < LL; i++) {
                    // parse each year as numeric values
                    yearVals[i] = Double.parseDouble(tokens[i + 1]);
                }

                dataMap.put("LATA", yearVals);
                break;
            }
        }

        if (LL <= 0) {
            throw new RuntimeException("Data file missing 'LATA' row or no columns!");
        }

        // phase 2: parse all other rows.
        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            String varName = tokens[0];

            if (varName.equalsIgnoreCase("LATA")) {
                // we already handled LATA, skip
                continue;
            }

            // the row has tokens.length - 1 numeric values
            int providedCols = tokens.length - 1;
            double[] finalVals = new double[LL];
            if (providedCols <= 0) {
                // no numbers, fill all with 0.0 or skip
                Arrays.fill(finalVals, 0.0);
            } else {
                // parse what we have
                double lastVal = 0.0;
                for (int i = 0; i < providedCols; i++) {
                    double val = Double.parseDouble(tokens[i + 1].replace(",", "."));
                    finalVals[i] = val;
                    lastVal = val;
                }

                // replicate lastVal for the remainder
                for (int i = providedCols; i < LL; i++) {
                    finalVals[i] = lastVal;
                }
            }

            dataMap.put(varName, finalVals);
        }

        model.setLL(LL);

        // injection into model fields
        for (Field field : model.getClass().getFields()) {
            if (field.isAnnotationPresent(Bind.class)) {
                String fieldName = field.getName();
                if (dataMap.containsKey(fieldName)) {
                    field.set(model, dataMap.get(fieldName));
                } else {
                    double[] empty = new double[LL];
                    field.set(model, empty);
                }
            }
        }
    }

    public void runModel() {
        model.run();
        updateBinding();
    }

    public void runScriptFromFile(String fname) throws Exception {
        // making sure the fields are in the binding
        updateBindingDynamic();

        GroovyShell shell = new GroovyShell(binding);
        Script script = shell.parse(new File(fname));
        script.run();

        //updateBindingFromScript()
    }


    public void runScript(String script) {
        updateBindingDynamic();

        GroovyShell shell = new GroovyShell(binding);
        Script s = shell.parse(script);
        s.run();

        //updateBindingFromScript();
    }



    public String getResultsAsTsv() {
        StringBuilder sb = new StringBuilder();
        int LL = model.getLL();

        double[] lataVals = dataMap.get("LATA");
        if (lataVals == null) {
            lataVals = new double[LL];
            for (int i = 0; i < LL; i++) {
                lataVals[i] = i + 1;
            }
        }

        List<Field> bindFields = Arrays.stream(model.getClass().getFields())
                .filter(f -> f.isAnnotationPresent(Bind.class))
                .collect(Collectors.toList());

        // header
        sb.append("Year");

        for (Field f : bindFields) {
            sb.append("\t").append(f.getName());
        }

        sb.append("\n");

        // rows
        for (int i = 0; i < LL; i++) {
            sb.append((int) lataVals[i]);

            for (Field f : bindFields) {
                try {
                    double[] arr = (double[]) f.get(model);
                    sb.append("\t").append(String.format(Locale.ROOT, "%.4f", arr[i]));
                } catch (Exception e) {
                    sb.append("\tNA");
                }
            }

            sb.append("\n");
        }
        return sb.toString();
    }

    private void updateBinding() {
        for (Field field : model.getClass().getFields()) {
            if (field.isAnnotationPresent(Bind.class)) {
                try {
                    Object val = field.get(model);
                    binding.setProperty(field.getName(), val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateBindingDynamic() {
        // gather all fields in model + superclasses
        List<Field> fields = getAllFields(model.getClass());

        for (Field f : fields) {
            try {
                Object value = f.get(model);
                // e.g. if f.getName() == "LL" or "KI", we store them in the binding
                binding.setProperty(f.getName(), value);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateBindingFromScript() {
        List<Field> fields = getAllFields(model.getClass());

        for (Field f : fields) {
            try {
                Object val = binding.getProperty(f.getName());
                // Check if the type matches, e.g. if we expect double[] vs. int, etc.
                if (val != null && f.getType().isAssignableFrom(val.getClass())) {
                    f.set(model, val);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private static List<Field> getAllFields(Class<?> cl) {
        List<Field> fields = new ArrayList<>();

        // loop through the entire inheritance chain
        for (Class<?> c = cl; c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                f.setAccessible(true); // allows access to protected/private
                fields.add(f);
            }
        }

        return fields;
    }


    public BaseModel getModel() {
        return model;
    }

    public Map<String, double[]> getDataMap() {
        return dataMap;
    }

    @SuppressWarnings("unchecked")
    public Map<String, double[]> getBoundArrays() {
        Map<String, double[]> result = new LinkedHashMap<>();

        // Groovy binding returns a raw map, so cast it
        Map<String, Object> vars = (Map<String, Object>) binding.getVariables();

        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof double[]) {
                result.put(entry.getKey(), (double[]) val);
            }
        }

        return result;
    }
}
