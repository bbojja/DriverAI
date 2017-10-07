public class GeneticLearner
{
    private PerceptronLearner[] cars;

    public GeneticLearner(int numLearners)
    {
        cars = new PerceptronLearner[numLearners];
        for (int i = 0; i < numLearners; i++)
        {
            cars[i] = new PerceptronLearner();
            cars[i].randomizeWeights();
        }
    }
}