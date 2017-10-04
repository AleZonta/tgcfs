package tgcfs.Networks;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.LearningRatePolicy;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import tgcfs.NN.Models;

import java.util.Map;

/**
 * Created by Alessandro Zonta on 08/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * class implementing the convolutionary network
 */
public class Convolutionary extends Models implements Network {
    private ComputationGraph net; //neural network, brain of the agent
    private INDArray conditionalPicture;


    /**
     * Constructor with zero parameter
     * It loads the convolutionary network
     * It has two inputs -> conditional cnn
     * Different layers of convolutionary nodes
     *
     * @param dimension height and with of the picture
     *
     * */
    public Convolutionary(int dimension){
        ComputationGraphConfiguration.GraphBuilder graph = new NeuralNetConfiguration.Builder()
                .seed(123)
                .iterations(90)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(1e-2)
                .biasLearningRate(2 * 1e-2)
                .learningRateDecayPolicy(LearningRatePolicy.Step)
                .lrPolicyDecayRate(0.96)
                .lrPolicySteps(320000)
                .updater(Updater.NESTEROVS)
                .weightInit(WeightInit.XAVIER)
                .regularization(true)
                .l2(2e-4)
                .graphBuilder();

        Integer numberFilterOut = 64;

        ConvolutionLayer.Builder firstLayer = new ConvolutionLayer.Builder(5, 5);
        firstLayer.nIn(3); //number of channel as input
        firstLayer.stride(2,2); //stride = 1 -> move pixel by pixel
        firstLayer.kernelSize(2,2);
        firstLayer.nOut(numberFilterOut); //number filter out
        firstLayer.activation(Activation.RELU);

        ConvolutionLayer.Builder secondLayer = new ConvolutionLayer.Builder(5, 5);
        secondLayer.nIn(numberFilterOut); //number filter as input
        secondLayer.stride(2,2); //stride = 1 -> move pixel by pixel
        secondLayer.kernelSize(2,2);
        secondLayer.nOut(numberFilterOut * 2); //number filter out
        secondLayer.activation(Activation.RELU);


        ConvolutionLayer.Builder thirdLayer = new ConvolutionLayer.Builder(3, 3);
        thirdLayer.nIn(numberFilterOut * 4); //number filter as input
        thirdLayer.stride(2,2); //stride = 1 -> move pixel by pixel
        thirdLayer.kernelSize(2,2);
        thirdLayer.nOut(numberFilterOut * 4); //number filter out
        thirdLayer.activation(Activation.RELU);

        ConvolutionLayer.Builder fourthLayer = new ConvolutionLayer.Builder(3, 3);
        fourthLayer.nIn(numberFilterOut * 4); //number filter as input
        fourthLayer.stride(2,2); //stride = 1 -> move pixel by pixel
        fourthLayer.kernelSize(2,2);
        fourthLayer.nOut(numberFilterOut * 4); //number filter out
        fourthLayer.activation(Activation.RELU);

        DenseLayer.Builder fullFirstConnectedLayer = new DenseLayer.Builder();
        //fullFirstConnectedLayer.nIn(numberFilterOut * 4);
        fullFirstConnectedLayer.nOut(50);
        fullFirstConnectedLayer.biasInit(1.0);
        fullFirstConnectedLayer.activation(Activation.TANH);

        DenseLayer.Builder fullSecondConnectedLayer = new DenseLayer.Builder();
        fullSecondConnectedLayer.nIn(50);
        fullSecondConnectedLayer.nOut(50);
        fullSecondConnectedLayer.biasInit(1.0);
        fullSecondConnectedLayer.activation(Activation.TANH);

        OutputLayer.Builder outputlayer = new OutputLayer.Builder();
        outputlayer.lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD);
        outputlayer.nIn(50);
        outputlayer.nOut(3);
        outputlayer.activation(Activation.TANH);


        graph.addInputs("APFinput", "GraphInput")
                .addLayer("cnn1APF", firstLayer.build(), "APFinput")
                .addLayer("cnn2APF", secondLayer.build(), "cnn1APF")
                .addLayer("cnn1Graph", firstLayer.build(), "GraphInput")
                .addLayer("cnn2Graph", secondLayer.build(), "cnn1Graph")
                .addVertex("merge", new MergeVertex(), "cnn2APF", "cnn2Graph")
                .addLayer("max", new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2,2).stride(1,1).build(), "merge")
                .addLayer("cnn3", thirdLayer.build(), "max")
                .addLayer("cnn4", fourthLayer.build(), "cnn3")
                .addLayer("full1", fullFirstConnectedLayer.build(), "cnn4")
                .addLayer("full2", fullSecondConnectedLayer.build(), "full1")
                .addLayer("out", outputlayer.build(), "full2")
                .setOutputs("out")
                .setInputTypes(InputType.convolutionalFlat(dimension,dimension,3),InputType.convolutionalFlat(dimension,dimension,3))
                .backprop(Boolean.TRUE).pretrain(Boolean.FALSE);

        ComputationGraphConfiguration conf = graph.build();

        this.net = new ComputationGraph(conf);
        this.net.init();

        this.conditionalPicture = null;
    }

    /**
     * Get number of the input arrays.
     * Inputs of the network
     * @return Integer number
     */
    public int getNumberInputArrays(){
        return this.net.getNumInputArrays();
    }

    /**
     * Get number of the output arrays.
     * Output of the network
     * @return Integer number
     */
    public int getNumberOutputArrays(){
        return this.net.getNumOutputArrays();
    }

    /**
     * Get number parameter of the network
     * @return integer number
     */
    @Override
    public int getNumPar(){
        return this.net.numParams();
    }

    /**
     * Compute the output of the network given the input
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    @Override
    public INDArray computeOutput(INDArray input) {
        //now I have the two input, I need to concatenate them and pass them to the system
        //fix the conditional picture in the second position
        if(this.conditionalPicture == null) throw new NullPointerException("Conditional picture not setted");
        INDArray[] inputs = new INDArray[2];
        inputs[0] = input;
        inputs[1] = this.conditionalPicture;
        return this.computeOutput(inputs);
    }

    /**
     * fit the network
     * @param inputs input of the network
     * @param labels real point of the input
     */
    @Override
    public void fit(INDArray inputs, INDArray labels) {
        throw new NoSuchMethodError("Method not implemented");
    }

    /**
     * fit the network
     * @param inputs inputs of the network (INDArray)
     * @param labels output of the network (INDArray)
     */
    public void fit(INDArray[] inputs, INDArray[] labels){
        this.net.fit(inputs, labels);
    }


    /**
     * Get string containing description of the network
     * @return String
     */
    @Override
    public String getSummary() {
        return this.net.summary();
    }


    /**
     * Return the parameter of the net
     * @return INDarray
     */
    public INDArray getWeights(){
        return this.net.params();
    }


    /**
     * @implNote Implementation from Interface
     * @param weights list containing all the weights
     * @throws Exception if the length of the list is not correct
     */
    public void setWeights(INDArray weights) throws Exception {
        if (weights.columns() != this.net.numParams()){
            throw new Exception("Length list weights is not correct.");
        }
        this.net.setParams(weights);
    }


    /**
     * Compute the output of the network given the input
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    public INDArray computeOutput(INDArray[] input) {
        Map<String, INDArray> res = this.net.feedForward(input, Boolean.FALSE);
        return res.get("out");
    }

    /**
     * Get the conditional picture
     * @return INDarray flattened of the picture
     */
    public INDArray getConditionalPicture() {
        return conditionalPicture;
    }

    /**
     * Set conditional picture
     * @param conditionalPicture INDarray picture
     */
    public void setConditionalPicture(INDArray conditionalPicture) {
        this.conditionalPicture = conditionalPicture;
    }
}
