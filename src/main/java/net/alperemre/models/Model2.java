package net.alperemre.models;

import net.alperemre.annotations.Bind;

public class Model2 extends BaseModel {

    @Bind
    public double[] twINW;
    @Bind
    public double[] INW;

    @Override
    public void run() {
        // Handle the first year if LL >= 1
        if (LL >= 1) {
            INW[0] = 200 * twINW[0];
        }

        // Process subsequent years if they exist
        for (int i = 1; i < LL; i++) {
            INW[i] = INW[i - 1] * twINW[i];
        }
    }

    @Override
    public String getModelName() {
        return "Model2";
    }
}
