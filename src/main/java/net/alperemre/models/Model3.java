package net.alperemre.models;

import net.alperemre.annotations.Bind;

public class Model3 extends BaseModel {

    @Bind
    public double[] twEKS;
    @Bind
    public double[] twIMP;
    @Bind
    public double[] EKS;
    @Bind
    public double[] IMP;
    @Bind
    public double[] PKB;

    @Override
    public void run() {
        // handle the first year if LL >= 1
        if (LL >= 1) {
            EKS[0] = 500 * twEKS[0];
            IMP[0] = 400 * twIMP[0];
            PKB[0] = EKS[0] + IMP[0];
        }

        // for subsequent years, build on the previous year's values
        for (int i = 1; i < LL; i++) {
            EKS[i] = EKS[i - 1] * twEKS[i];
            IMP[i] = IMP[i - 1] * twIMP[i];
            PKB[i] = EKS[i] + IMP[i];
        }
    }

    @Override
    public String getModelName() {
        return "Model3";
    }
}
