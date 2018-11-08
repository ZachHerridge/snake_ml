package uwrf.edu.nueral_net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NeuralNetwork {

    private final List<List<Neuron>> hiddenLayers;
    private final List<Neuron> outputLayer;

    public NeuralNetwork(int numberOfInputs, int numberOfOutputs, int numberOfHiddenLayers, int numberOfNeuronsPerHiddenLayer) {
        // Initialize the hidden layers
        this.hiddenLayers = new ArrayList<List<Neuron>>(numberOfHiddenLayers);

        for (int i = 0; i < numberOfHiddenLayers; i++) {
            ArrayList<Neuron> neurons = new ArrayList<Neuron>();
            for (int j = 0; j < numberOfNeuronsPerHiddenLayer; j++) {
                if (i == 0) {
                    neurons.add(new Neuron(numberOfInputs + 1));
                } else {
                    neurons.add(new Neuron(numberOfNeuronsPerHiddenLayer + 1));
                }
            }
            this.hiddenLayers.add(neurons);
        }

        // Initialize the outputs with as many inputs as the number of neurons
        // per hidden layer
        this.outputLayer = new ArrayList<Neuron>();
        for (int i = 0; i < numberOfOutputs; i++) {
            Neuron neuron = new Neuron(numberOfNeuronsPerHiddenLayer + 1);
            this.outputLayer.add(neuron);
        }
    }

    public List<Double> update(List<Double> inputs) {
        List<Double> hiddenLayerOutput = evaluateHiddenLayers(inputs);
        return evaluateOutputLayer(hiddenLayerOutput);
    }

    public List<Double> update(Double... inputs) {
        List<Double> inputList = Arrays.asList(inputs);
        return update(inputList);
    }

    public void importWeights(Float[] weights) {
        int weightsIndex = 0;

        for (List<Neuron> layer : hiddenLayers) {
            for (Neuron neuron : layer) {
                List<Float> weightsList = new ArrayList<Float>();
                for (int i = 0; i < neuron.getWeights().length; i++) {
                    weightsList.add(weights[weightsIndex++]);
                }
                neuron.setWeights(weightsList.toArray(new Double[weightsList.size()]));
            }
        }

        for (Neuron neuron : outputLayer) {
            List<Float> weightsList = new ArrayList<Float>();
            for (int i = 0; i < neuron.getWeights().length; i++) {
                weightsList.add(weights[weightsIndex++]);
            }
            neuron.setWeights(weightsList.toArray(new Double[weightsList.size()]));
        }
    }

    public List<Double> exportWeights() {
        List<Double> weights = new ArrayList<>();

        for (List<Neuron> layer : hiddenLayers) {
            for (Neuron neuron : layer) {
                for (int i = 0; i < neuron.getWeights().length; i++) {
                    weights.add(neuron.getWeights()[i]);
                }
            }
        }

        for (Neuron neuron : outputLayer) {
            for (int i = 0; i < neuron.getWeights().length; i++) {
                weights.add(neuron.getWeights()[i]);
            }
        }

        return weights;

    }

    public Integer getWeightCount() {
        List<Double> exportWeights = exportWeights();
        return exportWeights != null ? exportWeights.size() : null;
    }

    private List<Double> evaluateHiddenLayers(List<Double> inputs) {
        List<Double> hiddenLayerInput = inputs;
        List<Double> hiddenLayerOutput = new ArrayList<>();

        for (int i = 0; i < hiddenLayers.size(); i++) {
            List<Neuron> layer = hiddenLayers.get(i);
            for (Neuron neuron : layer) {
                hiddenLayerOutput.add(neuron.evaluate(hiddenLayerInput.toArray(new Double[inputs.size()])));
            }
            hiddenLayerInput = hiddenLayerOutput;
        }
        return hiddenLayerOutput;
    }

    private List<Double> evaluateOutputLayer(List<Double> hiddenLayerOutput) {
        List<Double> outputs = new ArrayList<>();

        for (int i = 0; i < outputLayer.size(); i++) {
            Neuron neuron = outputLayer.get(i);

            outputs.add(neuron.evaluate(hiddenLayerOutput.toArray(new Double[hiddenLayerOutput.size()])));
        }

        return outputs;
    }

    @Override
    public String toString() {
        String s = "NeuralNetwork [\n";
        s += "  Hidden Layers [";
        for (List<Neuron> list : hiddenLayers) {
            s += "\n    Layer [\n      ";
            for (Neuron neuron : list) {
                s += neuron.toString() + "\n      ";
            }
            s += "]";
        }

        s += "\n  ]\n  Output Layer [";
        for (Neuron neuron : outputLayer) {
            s += neuron.toString() + "\n  ";
        }

        return s + "\n]";
    }

}