import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Car {
    private double xPos, yPos;
    private double speed = 3;
    private double direction;

    private static BufferedImage sprite = loadSprite();
    private static int spriteWidth, spriteHeight;

    private static final double TURN_RATE = 3;
    private static final double SCALE = 8;

    public static double getSpriteWidth() {
        return spriteWidth / SCALE;
    }

    public static double getSpriteHeight() {
        return spriteHeight / SCALE;
    }


    public Car(int startX, int startY) {
        xPos = startX;
        yPos = startY;
        direction = 0;
    }

    private static BufferedImage loadSprite() {
        ImageIcon ii = new ImageIcon(Car.class.getResource("AudiSq.png"));
        spriteWidth = ii.getIconWidth();
        spriteHeight = ii.getIconHeight();

        Image img = ii.getImage();
        return toBufferedImage(img);
    }

    public double getX() {
        return xPos;
    }

    public double getY() {
        return yPos;
    }

    public double getXOffset()
    {
        return 128 / SCALE;
    }

    public double getYOffset()
    {
        return 25 / SCALE;
    }

    public static BufferedImage getImage()
    {
        return sprite;
    }

    public double getDirection()
    {
        return direction;
    }

    public void move()
    {
        xPos += speed * Math.sin(Math.toRadians(direction));
        yPos -= speed * Math.cos(Math.toRadians(direction));
    }

    public void turnLeft()
    {
        direction -= TURN_RATE;
    }

    public void turnRight()
    {
        direction += TURN_RATE;
    }

    public int[][] getBoundingBox()
    {
        int[][] boundingBox = new int[4][2];
        double[] origin = {xPos, yPos + 107.0 / SCALE};
        double theta = Math.toRadians(-direction);
        double[] v1 = {-49.0 / SCALE, -107.0 / SCALE};
        double[] v2 = {49.0 / SCALE, -107.0 / SCALE};
        double[] v3 = {49.0 / SCALE, 107.0 / SCALE};
        double[] v4 = {-49.0 / SCALE, 107.0 / SCALE};

        boundingBox[0][0] = (int) (Math.round(v1[0] * Math.cos(theta) + v1[1] * Math.sin(theta)) + origin[0]);
        boundingBox[0][1] = (int) (Math.round(-v1[0] * Math.sin(theta) + v1[1] * Math.cos(theta)) + origin[1]);
        boundingBox[1][0] = (int) (Math.round(v2[0] * Math.cos(theta) + v2[1] * Math.sin(theta)) + origin[0]);
        boundingBox[1][1] = (int) (Math.round(-v2[0] * Math.sin(theta) + v2[1] * Math.cos(theta)) + origin[1]);
        boundingBox[2][0] = (int) (Math.round(v3[0] * Math.cos(theta) + v3[1] * Math.sin(theta)) + origin[0]);
        boundingBox[2][1] = (int) (Math.round(-v3[0] * Math.sin(theta) + v3[1] * Math.cos(theta)) + origin[1]);
        boundingBox[3][0] = (int) (Math.round(v4[0] * Math.cos(theta) + v4[1] * Math.sin(theta)) + origin[0]);
        boundingBox[3][1] = (int) (Math.round(-v4[0] * Math.sin(theta) + v4[1] * Math.cos(theta)) + origin[1]);

        return boundingBox;
    }

    /**
     * Calculates the location of the tip of the car
     *
     * @return x and y coordinates
     */
    public int[] getTipLocation()
    {
        double[] origin = {xPos - 1, yPos + 107.0 / SCALE};
        double[] vert = {-1, -107 / SCALE};
        double theta = Math.toRadians(-direction);
        int[] loc = {(int) (Math.round(vert[0] * Math.cos(theta) + vert[1] * Math.sin(theta)) + origin[0]),
                (int) (Math.round(-vert[0] * Math.sin(theta) + vert[1] * Math.cos(theta)) + origin[1])};

        return loc;
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        BufferedImage after = new BufferedImage((int)(spriteWidth/SCALE), (int)(spriteHeight/SCALE), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(1.0/SCALE, 1.0/SCALE);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(bimage, after);

        // Return the buffered image
        return after;
    }
}