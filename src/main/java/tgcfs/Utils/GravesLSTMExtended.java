package tgcfs.Utils;

import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.layers.recurrent.FwdPassReturn;
import org.deeplearning4j.nn.layers.recurrent.GravesLSTM;
import org.deeplearning4j.nn.layers.recurrent.LSTMHelper;
import org.deeplearning4j.nn.layers.recurrent.LSTMHelpers;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Created by Alessandro Zonta on 30/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class GravesLSTMExtended extends GravesLSTM {
    public GravesLSTMExtended(NeuralNetConfiguration conf) {
        super(conf);
    }

    public GravesLSTMExtended(NeuralNetConfiguration conf, INDArray input) {
        super(conf, input);
    }

    private FwdPassReturn activateHelper(boolean training, INDArray prevOutputActivations, INDArray prevMemCellState, boolean forBackprop) {
        if (this.cacheMode == null) {
            this.cacheMode = CacheMode.NONE;
        }

        System.out.println(forBackprop);
        System.out.println(this.cachedFwdPass);

        if (forBackprop && this.cachedFwdPass != null) {
            FwdPassReturn ret = this.cachedFwdPass;
            this.cachedFwdPass = null;
            System.out.println(ret);
            return ret;
        } else {
            INDArray recurrentWeights = this.getParam("RW");
            System.out.println(recurrentWeights);

            INDArray inputWeights = this.getParam("W");
            System.out.println(inputWeights);

            INDArray biases = this.getParam("b");
            System.out.println(biases);

            FwdPassReturn fwd = LSTMHelpers.activateHelper(this, this.conf, ((org.deeplearning4j.nn.conf.layers.GravesLSTM)this.layerConf()).getGateActivationFn(), this.input, recurrentWeights, inputWeights, biases, training, prevOutputActivations, prevMemCellState, forBackprop || this.cacheMode != CacheMode.NONE && training, true, "W", this.maskArray, true, (LSTMHelper)null, forBackprop ? this.cacheMode : CacheMode.NONE);
            System.out.println(fwd);

            if (training && this.cacheMode != CacheMode.NONE) {
                this.cachedFwdPass = fwd;
            }

            return fwd;
        }
    }

    public INDArray rnnTimeStep(INDArray input) {
        this.setInput(input);
        System.out.println("------GravesLSTMExtended---------");
        FwdPassReturn fwdPass = this.activateHelper(false, (INDArray)this.stateMap.get("prevAct"), (INDArray)this.stateMap.get("prevMem"), false);
        INDArray outAct = fwdPass.fwdPassOutput;
        System.out.println(outAct);
        this.stateMap.put("prevAct", fwdPass.lastAct);
        this.stateMap.put("prevMem", fwdPass.lastMemCell);
        System.out.println("------end GravesLSTMExtended---------");
        return outAct;
    }
}
