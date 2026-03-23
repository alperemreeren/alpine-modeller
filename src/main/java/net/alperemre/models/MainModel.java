package net.alperemre.models;

import net.alperemre.annotations.Bind;

/**
 * A merged model that includes all fields from Model1, Model2, and Model3.
 * It runs a combined calculation over all variables in one go.
 */
public class MainModel extends BaseModel {

    // ----------------------
    // Model1 fields
    // ----------------------
    @Bind
    public double[] twKI;  // Growth index for private consumption
    @Bind
    public double[] twKS;  // Growth index for public consumption
    @Bind
    public double[] KI;    // Computed private consumption
    @Bind
    public double[] KS;    // Computed public consumption

    // ----------------------
    // Model2 fields
    // ----------------------
    @Bind
    public double[] twINW; // Investment growth index
    @Bind
    public double[] INW;   // Investment

    // ----------------------
    // Model3 fields
    // ----------------------
    @Bind
    public double[] twEKS; // Export growth index
    @Bind
    public double[] twIMP; // Import growth index
    @Bind
    public double[] EKS;   // Exports
    @Bind
    public double[] IMP;   // Imports
    @Bind
    public double[] PKB;   // Some aggregate measure, e.g. GDP


    /**
     * Combined run logic for all variables.
     * If LL >= 1, it initializes the "first year" for all sets of variables,
     * then loops from i=1..LL-1 to multiply by growth indexes.
     */
    @Override
    public void run() {
        // If there's at least 1 year
        if (LL >= 1) {
            // Model1 init
            if (KI != null && twKI != null && KI.length > 0 && twKI.length > 0) {
                KI[0] = 1000 * twKI[0];
            }
            if (KS != null && twKS != null && KS.length > 0 && twKS.length > 0) {
                KS[0] = 300 * twKS[0];
            }

            // Model2 init
            if (INW != null && twINW != null && INW.length > 0 && twINW.length > 0) {
                INW[0] = 200 * twINW[0];
            }

            // Model3 init
            if (EKS != null && twEKS != null && EKS.length > 0 && twEKS.length > 0) {
                EKS[0] = 500 * twEKS[0];
            }
            if (IMP != null && twIMP != null && IMP.length > 0 && twIMP.length > 0) {
                IMP[0] = 400 * twIMP[0];
            }
            if (PKB != null && PKB.length > 0) {
                // PKB = EKS + IMP or something more advanced;
                // here we do EKS + IMP for i=0
                PKB[0] = (EKS != null && EKS.length > 0 ? EKS[0] : 0)
                        + (IMP != null && IMP.length > 0 ? IMP[0] : 0);
            }
        }

        // Loop for i=1..(LL-1)
        for (int i = 1; i < LL; i++) {
            // Model1
            if (KI != null && KI.length > i
                    && twKI != null && twKI.length > i) {
                KI[i] = KI[i - 1] * twKI[i];
            }
            if (KS != null && KS.length > i
                    && twKS != null && twKS.length > i) {
                KS[i] = KS[i - 1] * twKS[i];
            }

            // Model2
            if (INW != null && INW.length > i
                    && twINW != null && twINW.length > i) {
                INW[i] = INW[i - 1] * twINW[i];
            }

            // Model3
            if (EKS != null && EKS.length > i
                    && twEKS != null && twEKS.length > i) {
                EKS[i] = EKS[i - 1] * twEKS[i];
            }
            if (IMP != null && IMP.length > i
                    && twIMP != null && twIMP.length > i) {
                IMP[i] = IMP[i - 1] * twIMP[i];
            }
            if (PKB != null && PKB.length > i) {
                double valEKS = (EKS != null && EKS.length > i) ? EKS[i] : 0;
                double valIMP = (IMP != null && IMP.length > i) ? IMP[i] : 0;
                // e.g. PKB = EKS + IMP
                PKB[i] = valEKS + valIMP;
            }
        }
    }

    @Override
    public String getModelName() {
        return "MainModel";
    }
}
