public class Track
{
    public TrackBlock[][] blocks;

    public Track(int rowBlocks, int colBlocks)
    {
        blocks = new TrackBlock[rowBlocks][colBlocks];
        for (int i = 0; i < rowBlocks; i++)
        {
            for (int j = 0; j < colBlocks; j++)
            {
                blocks[i][j] = new TrackBlock(TrackBlock.BlockTypes.EMPTY);
            }
        }

        createTrack2();
    }

    public void fillTrack(TrackBlock block, int x, int y)
    {
        blocks[x][y] = block;
    }

    public void createTrack1()
    {
        blocks[0][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[1][0] = new TrackBlock(TrackBlock.BlockTypes.NORTH_EAST);
        blocks[1][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[1][2] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[1][3] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_WEST);
        blocks[2][3] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[3][3] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[4][3] = new TrackBlock(TrackBlock.BlockTypes.NORTH_WEST);
        blocks[4][2] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[4][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[4][0] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_EAST);
        blocks[5][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
    }

    public void createTrack2()
    {
        blocks[1][0] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_EAST);
        blocks[1][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[1][2] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[1][3] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_WEST);
        blocks[2][3] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[3][3] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[4][3] = new TrackBlock(TrackBlock.BlockTypes.NORTH_WEST);
        blocks[4][2] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[4][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[4][0] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_EAST);
        blocks[5][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[6][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[2][0] = new TrackBlock(TrackBlock.BlockTypes.NORTH_EAST);
        blocks[2][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[2][2] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_WEST);
        blocks[3][2] = new TrackBlock(TrackBlock.BlockTypes.NORTH_WEST);
        blocks[3][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[3][0] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
    }

    public void createTrack3()
    {
        blocks[0][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[1][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[2][0] = new TrackBlock(TrackBlock.BlockTypes.NORTH_EAST);
        blocks[2][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[2][2] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[2][3] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_WEST);
        blocks[3][3] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[4][3] = new TrackBlock(TrackBlock.BlockTypes.NORTH_WEST);
        blocks[4][2] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[4][1] = new TrackBlock(TrackBlock.BlockTypes.HORIZONTAL);
        blocks[4][0] = new TrackBlock(TrackBlock.BlockTypes.SOUTH_EAST);
        blocks[5][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
        blocks[6][0] = new TrackBlock(TrackBlock.BlockTypes.VERTICAL);
    }

    public int getNumRowBlocks()
    {
        return blocks.length;
    }

    public int getNumColBlocks()
    {
        return blocks[0].length;
    }

    public TrackBlock[][] getTrackBlocks()
    {
        return blocks;
    }


}
