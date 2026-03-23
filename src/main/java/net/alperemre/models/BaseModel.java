package net.alperemre.models;

/**
 * Abstract base for all models. Common properties and methods go here.
 */
public abstract class BaseModel {

    /**
     * Number of simulation years (from LATA row).
     */
    protected int LL;

    /**
     * Implement logic for calculations across all simulation years.
     */
    public abstract void run();

    public int getLL() {
        return LL;
    }

    public void setLL(int LL) {
        this.LL = LL;
    }

    public abstract String getModelName();
}
