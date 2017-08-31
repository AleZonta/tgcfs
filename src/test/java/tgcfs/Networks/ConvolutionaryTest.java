package tgcfs.Networks;

import org.datavec.image.loader.NativeImageLoader;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by Alessandro Zonta on 22/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ConvolutionaryTest {
    @Test
    public void getConditionalPicture() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        NativeImageLoader imageLoader = new NativeImageLoader();
        INDArray one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/480x480/a.jpg"));
        convolutionary.setConditionalPicture(one);
        assertNotNull(convolutionary.getConditionalPicture());
    }

    @Test
    public void setConditionalPicture() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        NativeImageLoader imageLoader = new NativeImageLoader();
        INDArray one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/480x480/a.jpg"));
        convolutionary.setConditionalPicture(one);

    }

    @Test
    public void getNumberInputArrays() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        assertEquals(2, convolutionary.getNumberInputArrays().longValue());
    }

    @Test
    public void getNumberOutputArrays() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        assertEquals(1, convolutionary.getNumberOutputArrays().longValue());
    }

    @Test
    public void getNumPar() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        assertEquals(476737, convolutionary.getNumPar().longValue());
    }

    @Test
    public void computeOutput() throws Exception {
        Nd4j.enableFallbackMode(Boolean.TRUE);
        Convolutionary convolutionary = new Convolutionary(64);
        //lets discover how big has to be the input picture
        //lets try 480X480
        NativeImageLoader imageLoader = new NativeImageLoader();
        //test with 32x32
        INDArray cond = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/boh/480x480/c.jpg"));
        INDArray one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/boh/480x480/d.jpg"));
//        INDArray one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Documents/tgcfs/image.png"));
//        INDArray two = imageLoader.asMatrix(new File("/Users/alessandrozonta/Documents/tgcfs/cond.png"));

        INDArray out = null;
        try {
            out = convolutionary.computeOutput(one);
        }catch (NullPointerException e){
            assertEquals("Conditional picture not setted", e.getMessage());
        }
        convolutionary.setConditionalPicture(cond);
        out = convolutionary.computeOutput(one);

        assertNotNull(out);
        System.out.println(out);
    }

    @Test
    public void fit() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        INDArray input = Nd4j.rand(1,10);
        INDArray output = Nd4j.rand(1,10);
        try {
            convolutionary.fit(input,output);
        }catch (Error e){
            assertEquals("Method not implemented", e.getMessage());
        }
    }

    @Test
    public void fit1() throws Exception {
        Nd4j.enableFallbackMode(Boolean.TRUE);
        Convolutionary convolutionary = new Convolutionary(64);
        //lets discover how big has to be the input picture
        //lets try 480X480
        NativeImageLoader imageLoader = new NativeImageLoader();
        //test with 64x64
        INDArray cond = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/boh/480x480/c.jpg"));
        INDArray one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/boh/480x480/d.jpg"));
        INDArray two = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/boh/480x480/e.jpg"));
        INDArray three = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/boh/480x480/g.jpg"));
        INDArray[] vect = new INDArray[2];
        vect[1] = cond;
        vect[0] = one;

        INDArray out = Nd4j.rand(1,3);
        INDArray[] vecOu = new INDArray[1];
        vecOu[0] = out;
        convolutionary.fit(vect,vecOu);


        vect = new INDArray[2];
        vect[1] = cond;
        vect[0] = two;
        out = Nd4j.rand(1,3);
        vecOu = new INDArray[1];
        vecOu[0] = out;
        convolutionary.fit(vect,vecOu);


        vect = new INDArray[2];
        vect[1] = cond;
        vect[0] = three;
        out = Nd4j.rand(1,3);
        vecOu = new INDArray[1];
        vecOu[0] = out;
        convolutionary.fit(vect,vecOu);


    }

    @Test
    public void getSummary() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        assertNotNull(convolutionary.getSummary());
    }

    @Test
    public void getWeights() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        assertNotNull(convolutionary.getWeights());
    }

    @Test
    public void setWeights() throws Exception {
        Convolutionary convolutionary = new Convolutionary(32);
        INDArray ind = Nd4j.rand(1,476737);
        convolutionary.setWeights(ind);

        INDArray secInd = convolutionary.getWeights();

        for(int i=0;i<476737;i++){
            assertTrue(ind.getScalar(i).equals(secInd.getScalar(i)));
        }

    }

    @Test
    public void computeOutput1() throws Exception {
        Nd4j.enableFallbackMode(Boolean.TRUE);
        Convolutionary convolutionary = new Convolutionary(32);
        //lets discover how big has to be the input picture
        //lets try 480X480
        NativeImageLoader imageLoader = new NativeImageLoader();
        //test with 32x32
        INDArray one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/480x480/a.jpg"));
        INDArray two = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/480x480/b.jpg"));
        INDArray[] vect = new INDArray[2];
        vect[0] = one;
        vect[1] = two;
        INDArray out = null;
        out = convolutionary.computeOutput(vect);
        assertNotNull(out);
        System.out.println(out);


        convolutionary = new Convolutionary(64);
        //lets discover how big has to be the input picture
        //test with 64x64
        one = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/480x480/c.jpg"));
        two = imageLoader.asMatrix(new File("/Users/alessandrozonta/Desktop/480x480/d.jpg"));
        INDArray[] vectA = new INDArray[2];
        vectA[0] = one;
        vectA[1] = two;
        out = null;
        out = convolutionary.computeOutput(vectA);
        assertNotNull(out);
        System.out.println(out);

    }

    @Test
    public void test() throws Exception {
        Nd4j.enableFallbackMode(Boolean.TRUE);
        INDArray a = Nd4j.rand(64, 256);
        INDArray b = Nd4j.rand(256, 128);
        INDArray z = a.mmul(b);
    }

}