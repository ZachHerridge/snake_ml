package uwrf.edu.nueral_net;

import java.util.concurrent.ThreadLocalRandom;

public class Neuron {

    private Double[] weights;

    public Neuron(int numberOfInputs) {
        this.weights = new Double[numberOfInputs];
        for (int i = 0; i < weights.length; i++) {
            this.weights[i] = (double) (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat());
        }
    }

    public Double evaluate(Double[] inputs) {
        float net = 0;


        for (int i = 0; i < inputs.length; i++) {
            net += inputs[i] * weights[i];
        }
        net += weights[weights.length - 1] * -1f;

        return NNMath.sigmoid(net);
    }

    public Double[] getWeights() {
        return weights;
    }

    public Neuron setWeights(Double[] weights) {
        this.weights = weights;
        return this;
    }
}