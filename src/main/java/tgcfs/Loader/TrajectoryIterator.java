package tgcfs.Loader;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Alessandro Zonta on 26/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class TrajectoryIterator implements DataSetIterator {
    private final int batchSize;
    private final int lengthExample;
    private final Feeder feeder;
    private final IdsaLoader idsaLoader;
    private final int numberTrajectories;
    private int cursor = 0;

    public TrajectoryIterator(int batchSize, int lengthExample , IdsaLoader idsaLoader, Feeder feeder, boolean training) throws Exception {
        this.batchSize = batchSize;
        this.lengthExample = lengthExample;
        this.numberTrajectories = feeder.getTrajectories().getTrajectories().size();
        this.idsaLoader = idsaLoader;
        this.feeder = feeder;
        if(training){
            this.feeder.getTrajectories().switchToTrain();
        }else{
            this.feeder.getTrajectories().switchToTest();
        }

    }


    @Override
    public DataSet next(int i) {
        if (cursor >= this.numberTrajectories) throw new NoSuchElementException();
        try{
            return nextDataSet(i);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private DataSet nextDataSet(int num) throws Exception {
        //Create data for training
        //Here: we have reviews.size() examples of varying lengths
        INDArray features = Nd4j.create(new int[]{num, InputNetwork.inputSize, this.lengthExample}, 'f');
        INDArray labels = Nd4j.create(new int[]{num, OutputNetwork.outputSize, this.lengthExample}, 'f');    //Two labels: positive or negative
        //Because we are dealing with reviews of different lengths and only one output at the final time step: use padding arrays
        //Mask arrays contain 1 if data is present at that time step for that example, or 0 if data is just padding
        INDArray featuresMask = Nd4j.zeros(num, this.lengthExample);
        INDArray labelsMask = Nd4j.zeros(num, this.lengthExample);

        int count = 0;
        for( int l=0; count<num && this.cursor<100; l++ ){
            //load trajectories
            Trajectory tra = this.feeder.getTrajectory();
            //new trajectory new apf
            try {
                this.idsaLoader.resetAPF();
            }catch (Exception ignored){ }
            //init potential field with new elements from the current trajectory
            this.idsaLoader.InitPotentialField(this.feeder.getTrajectories());
            // obtain section trajectory
            int start = 0;


            boolean trajectoryEnded = false;
            while (!trajectoryEnded) {
                List<Point> points = new ArrayList<>();
                for (int i = start; i < this.lengthExample + 1; i++) {
                    Point p = this.feeder.getNextPoint(tra);
                    if (p != null) points.add(p);
                }
                //check if the trajectory is long enough
                if (points.size() < this.lengthExample + 1) {
                    trajectoryEnded = true;
                    start = 0;
                }else{
                    //increase the start point
                    start ++;
                    // transform in point with time
                    List<Point> pointNoBearing = new ArrayList<>();
                    List<PointWithBearing> pointWithTime = new ArrayList<>();
                    Point lastTimeUsed = null;
                    int i = 0;
                    for(Point p: points){
                        Point toAdd;
                        if(ReadConfig.Configurations.getTrajectoriesType() == 1 || ReadConfig.Configurations.getTrajectoriesType() == 4 || ReadConfig.Configurations.getTrajectoriesType() == 5){
                            toAdd = new Point(p.getLatitude(), p.getLongitude(), p.getAltitude(),p.getDated(), p.getDates(), p.getTime());
                            lastTimeUsed = toAdd;
                        }else{
                            if(i == 0){
                                toAdd = new Point(p.getLatitude(), p.getLongitude(), p.getAltitude(),0d, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                                lastTimeUsed = toAdd;
                            }else{
                                toAdd = new Point(p.getLatitude(), p.getLongitude(), p.getAltitude(),0d, lastTimeUsed.addTimeToPoint(0.2), lastTimeUsed.addTimeToPoint(0.2));
                                lastTimeUsed = toAdd;
                            }
                        }
                        pointWithTime.add(new PointWithBearing(toAdd.deepCopy()));
                        pointNoBearing.add(toAdd);
                        i++;
                    }

                    List<PointWithBearing> targetPoint = new ArrayList< >();
                    targetPoint.add(pointWithTime.remove(pointWithTime.size() - 1));
                    pointNoBearing.remove(pointNoBearing.size() -1);

                    //compute the potential field for the actualPoint
                    for(Point point: pointWithTime){
                        this.idsaLoader.compute(point);
                    }
                    //return attraction
                    double attraction = idsaLoader.returnAttraction(pointWithTime.get(pointWithTime.size() - 1));
                    //all the input transformed in INDarray
                    List<InputsNetwork> input = this.feeder.obtainInput(pointNoBearing, attraction, null, targetPoint);


                    //target point converted into INDarray
                    //class that compute the conversion point -> speed/bearing
                    PointToSpeedBearing conversion = new PointToSpeedBearing();


                    //add the last point to the end to enable the computation of the output
                    List<Point> realPointToConvert = new ArrayList<>();
                    realPointToConvert.add(pointNoBearing.get(pointNoBearing.size() - 1));
                    realPointToConvert.addAll(targetPoint);


                    Point previousPoint = realPointToConvert.get(0);
                    Point actualPoint = realPointToConvert.get(1);
                    double bearing = conversion.obtainBearing(previousPoint, actualPoint);
                    double speed = conversion.obtainSpeed(previousPoint, actualPoint, this.feeder.lastTime);
                    OutputsNetwork outputConverted = new OutputNetwork(speed, bearing);


                    INDArray singleInput = Nd4j.create(new int[]{1, InputNetwork.inputSize, this.lengthExample}, 'f');
                    for (int j = 0; j < this.lengthExample; j++) {
                        INDArray vector = input.get(j).serialise();
                        singleInput.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
                    }

                    features.put(
                            new INDArrayIndex[] {
                                    NDArrayIndex.point(count), NDArrayIndex.all(), NDArrayIndex.interval(0, this.lengthExample)
                            },
                            singleInput);
                    // Assign "1" to each position where a feature is present, that is, in the interval of [0, seqLength)
                    featuresMask.get(NDArrayIndex.point(count), NDArrayIndex.interval(0, this.lengthExample)).assign(1);

                    INDArray singleOutput = Nd4j.create(new int[]{1, OutputNetwork.outputSize, 1}, 'f');
                    INDArray vector = outputConverted.serialise();
                    singleOutput.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(0)}, vector);

                    labels.put(
                            new INDArrayIndex[] {
                                    NDArrayIndex.point(count), NDArrayIndex.all(), NDArrayIndex.interval(this.lengthExample - 1, this.lengthExample)
                            },
                            singleOutput);

                    labelsMask.get(NDArrayIndex.point(count), NDArrayIndex.interval(this.lengthExample - 1, this.lengthExample)).assign(1);
                    cursor++;
                    count++;
                }
            }


        }

        return new DataSet(features,labels,featuresMask,labelsMask);
    }

    @Override
    public int totalExamples() {
        return 100;
    }

    @Override
    public int inputColumns() {
        return InputNetwork.inputSize;
    }

    @Override
    public int totalOutcomes() {
        return OutputNetwork.outputSize;
    }

    @Override
    public boolean resetSupported() {
        return true;
    }

    @Override
    public boolean asyncSupported() {
        return true;
    }

    @Override
    public void reset() {
        this.cursor = 0;
    }

    @Override
    public int batch() {
        return this.batchSize;
    }

    @Override
    public int cursor() {
        return this.cursor;
    }

    @Override
    public int numExamples() {
        return 100;
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor dataSetPreProcessor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<String> getLabels() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean hasNext() {
        return cursor < numExamples();
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }
}
