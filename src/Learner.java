public interface Learner
{
    enum CarCommands {GO_STRAIGHT,TURN_LEFT, TURN_RIGHT}

    CarCommands getCommands(double[] inputs);
}