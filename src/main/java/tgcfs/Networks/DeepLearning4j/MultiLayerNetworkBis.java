package tgcfs.Networks.DeepLearning4j;

import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.Solver;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.heartbeat.Heartbeat;
import org.nd4j.linalg.heartbeat.utils.TaskUtils;
import org.nd4j.linalg.memory.abstracts.DummyWorkspace;
import org.nd4j.linalg.heartbeat.reports.Task;
import org.nd4j.linalg.heartbeat.reports.Environment;
import org.nd4j.linalg.heartbeat.reports.Event;
import org.nd4j.linalg.heartbeat.utils.EnvironmentUtils;



/**
 * Created by Alessandro Zonta on 12/09/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class MultiLayerNetworkBis extends MultiLayerNetwork {
    private INDArray inputFirstLayer;

    public MultiLayerNetworkBis(MultiLayerConfiguration conf) {
        super(conf);
    }

    public MultiLayerNetworkBis(String conf, INDArray params) {
        super(conf, params);
    }

    public MultiLayerNetworkBis(MultiLayerConfiguration conf, INDArray params) {
        super(conf, params);
    }

    /**
     * Fit the model
     *
     * Override method to preserve the status of an internal layer
     *
     * @param features   the examples to classify (one example in each row)
     * @param labels the example labels(a binary outcome matrix)
     * @param featuresMask The mask array for the features (used for variable length time series, etc). May be null.
     * @param labelsMask The mask array for the labels (used for variable length time series, etc). May be null.
     */
    @Override
    public void fit(INDArray features, INDArray labels, INDArray featuresMask, INDArray labelsMask) {
        setInput(features);
        setLabels(labels);
        if (featuresMask != null || labelsMask != null) {
            this.setLayerMaskArrays(featuresMask, labelsMask);
        }
        update(TaskUtils.buildTask(features, labels));

        MemoryWorkspace workspace =
                layerWiseConfigurations.getTrainingWorkspaceMode() == WorkspaceMode.NONE ? new DummyWorkspace()
                        : Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(
                        workspaceConfigurationExternal, workspaceExternal);

        MemoryWorkspace cache =
                layerWiseConfigurations.getTrainingWorkspaceMode() == WorkspaceMode.NONE ? new DummyWorkspace()
                        : Nd4j.getWorkspaceManager().getWorkspaceForCurrentThread(
                        ComputationGraph.workspaceConfigurationCache,
                        ComputationGraph.workspaceCache);

        if (layerWiseConfigurations.isPretrain()) {
            try (MemoryWorkspace wsCache = cache.notifyScopeEntered()) {
                try (MemoryWorkspace ws = workspace.notifyScopeEntered()) {
                    pretrain(features);
                }
            }
        }

        if (layerWiseConfigurations.isBackprop()) {
            if (layerWiseConfigurations.getBackpropType() == BackpropType.TruncatedBPTT) {
                doTruncatedBPTT(features, labels, featuresMask, labelsMask);
            } else {
                if (solver == null) {
                    try (MemoryWorkspace wsO = Nd4j.getMemoryManager().scopeOutOfWorkspaces()) {
                        solver = new Solver.Builder().configure(conf()).listeners(getListeners()).model(this).build();
                    }
                }

                try (MemoryWorkspace wsCache = cache.notifyScopeEntered()) {
                    try (MemoryWorkspace ws = workspace.notifyScopeEntered()) {
                        solver.optimize();
                    }
                }
            }
        }

        if (featuresMask != null || labelsMask != null) {
            clearLayerMaskArrays();
        }

        this.inputFirstLayer = this.getLayer(1).input().dup();

        clearLayersStates();
    }


    private void update(Task task) {
        if (!initDone) {
            initDone = true;
            Heartbeat heartbeat = Heartbeat.getInstance();
            task = ModelSerializer.taskByModel(this);
            Environment env = EnvironmentUtils.buildEnvironment();
            heartbeat.reportEvent(Event.STANDALONE, env, task);
        }
    }


    /**
     * Getter fot input hidden layer
     * @return {@link INDArray}
     */
    public INDArray getInputFirstLayer() {
        return this.inputFirstLayer;
    }
}
