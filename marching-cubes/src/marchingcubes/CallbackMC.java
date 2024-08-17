package marchingcubes;

import java.util.ArrayList;

/**
 * Created by Primoz on 8.7.2016.
 */
public abstract class CallbackMC implements Runnable {
    private ArrayList<float []> vertices;

    public void setVertices(ArrayList<float []> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<float []> getVertices() {
        return this.vertices;
    }
}