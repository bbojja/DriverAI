import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.NormOps;

public class PerceptronLearner implements Learner
{
    private DenseMatrix64F weights;
    public static final int NUM_ROWS = 3;
    public static final int NUM_COLS = 3;

    public PerceptronLearner()
    {
        weights = new DenseMatrix64F(NUM_ROWS, NUM_COLS);
    }

    public PerceptronLearner(double[] array)
    {
        setWeights(array);
    }

    public void setWeights(double[] array)
    {
        weights = new DenseMatrix64F(NUM_ROWS, NUM_COLS, true, array);
        normalizeWeights();
    }

    public double[] getWeights()
    {
        return weights.getData();
    }

    public void randomizeWeights()
    {
        for (int i = 0; i < weights.getNumElements(); i++)
        {
            weights.set(i, Math.random());
        }

        normalizeWeights();
    }

    public void normalizeWeights()
    {
        for (int i = 0; i < weights.getNumRows(); i++)
        {
            DenseMatrix64F temp = CommonOps.extractRow(weights, i, null);
            CommonOps.divide(temp, CommonOps.elementSum(temp));
            CommonOps.insert(temp,weights,i,0);
        }
    }

    @Override
    public CarCommands getCommands(double[] inputs)
    {
        DenseMatrix64F result = new DenseMatrix64F(3, 1);
        CommonOps.multTransAB(weights, new DenseMatrix64F(1, inputs.length, true, inputs), result);
        int maxIndex = 0;
        double max = result.get(maxIndex);

        for (int i = 1; i < result.getNumElements(); i++)
        {
            if (result.get(i) > max)
            {
                max = result.get(i);
                maxIndex = i;
            }
        }

        if (maxIndex == 0)
            return CarCommands.TURN_LEFT;
        if (maxIndex == 2)
            return CarCommands.TURN_RIGHT;
        return CarCommands.GO_STRAIGHT;
    }
}
