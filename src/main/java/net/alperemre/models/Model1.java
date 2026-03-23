package net.alperemre.models;

import net.alperemre.annotations.Bind;

public class Model1 extends BaseModel {

    @Bind
    public double[] twKI;  // Growth index
    @Bind
    public double[] twKS;  // Growth index
    @Bind
    public double[] KI;    // Computed variable (e.g., capital or similar)
    @Bind
    public double[] KS;    // Computed variable (e.g., something else)

    @Override
    public void run() {
        // Handle the first year (i=0) if LL >= 1
        if (LL >= 1) {
            KI[0] = 1000 * twKI[0];
            KS[0] = 300 * twKS[0];
        }

        // Only loop for i=1..(LL-1) if LL > 1
        for (int i = 1; i < LL; i++) {
            KI[i] = KI[i - 1] * twKI[i];
            KS[i] = KS[i - 1] * twKS[i];
        }
    }

    @Override
    public String getModelName() {
        return "Model1";
    }
}
