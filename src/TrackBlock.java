import java.awt.*;

public class TrackBlock
{
    // size of blocks in pixels
    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;

    public enum BlockTypes {EMPTY, VERTICAL, HORIZONTAL, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST}

    private BlockTypes blockType;

    public TrackBlock(BlockTypes type)
    {
        blockType = type;
    }

    public BlockTypes getBlockType()
    {
        return blockType;
    }

    public void drawBlock(int xPos, int yPos, Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        switch (blockType)
        {
            case EMPTY:
                break;
            case VERTICAL:
                g.drawLine(xPos + 9, yPos, xPos + 9, yPos + HEIGHT - 1);
                g.drawLine(xPos + 89, yPos, xPos + 89, yPos + HEIGHT - 1);
                break;
            case HORIZONTAL:
                g.drawLine(xPos, yPos + 9, xPos + WIDTH - 1, yPos + 9);
                g.drawLine(xPos, yPos + 89, xPos + WIDTH - 1, yPos + 89);
                break;
            case NORTH_EAST:
                g.drawArc(xPos + 9, yPos - 89, WIDTH * 2 - 21, HEIGHT * 2 - 22, 180, 90);
                g.drawArc(xPos + 89, yPos - 9, 19, 18, 180, 90);
                break;
            case NORTH_WEST:
                g.drawArc(xPos - 90, yPos - 89, WIDTH * 2 - 21, HEIGHT * 2 - 22, 270, 90);
                g.drawArc(xPos - 9, yPos - 9, 18, 18, 270, 90);
                break;
            case SOUTH_EAST:
                g.drawArc(xPos + 9, yPos + 9, WIDTH * 2 - 21, HEIGHT * 2 - 21, 90, 90);
                g.drawArc(xPos + 89, yPos + 89, 19, 19, 90, 90);
                break;
            case SOUTH_WEST:
                g.drawArc(xPos - 90, yPos + 9, WIDTH * 2 - 21, HEIGHT * 2 - 21, 0, 90);
                g.drawArc(xPos - 9, yPos + 89, 18, 19, 0, 90);
                break;
        }

        g2d.setStroke(new BasicStroke(1));
    }
}
