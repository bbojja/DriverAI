import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class DrawCanvas extends JPanel implements ActionListener, KeyListener
{
    private static final int NUM_CARS = 6;
    private static final double BLEND_PROB = 0.7;
    private static final double WEIGHT_MUTATE_RATE = 0.1;
    private static final double MAX_MUTATE_RATE = 1.0;
    private static final double MAX_SCORE = 1700;
    private static final double MUTATE_STEP = 0.1;

    private Track track;
    private int keyCode = -1;
    private boolean upArrow = false;
    private int generation = 1;
    private double prevAvgScore = 0;

    private CarStruct[] cars, newCars;

    private class CarStruct implements Comparable<CarStruct>
    {
        private Car car;
        private double prevXPos, prevYPos, checkXPos, checkYPos;
        private boolean crashed;
        private double score;
        private PerceptronLearner learner;
        private double[][] rayEnds;

        public CarStruct()
        {
            car = new Car(50, 650);
            prevXPos = car.getX();
            prevYPos = car.getY();
            checkXPos = car.getX();
            checkYPos = car.getY();
            crashed = false;
            score = 0;
            learner = new PerceptronLearner();
            learner.randomizeWeights();
            rayEnds = new double[3][2];
        }

        @Override
        public int compareTo(CarStruct o)
        {
            return (int) (score - o.score);
        }

        public String toString()
        {
            return "" + score;
        }
    }

    public DrawCanvas(int width, int height)
    {
        cars = new CarStruct[NUM_CARS];
        for (int i = 0; i < NUM_CARS; i++) {
            cars[i] = new CarStruct();
        }

        track = new Track(height/TrackBlock.HEIGHT, width/TrackBlock.WIDTH);


        Timer timer = new Timer(10, this);
        timer.start();

        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        drawTrack(g);
        drawCars(g);
        drawScores(g);
        g.drawString("Generation: " + generation, getWidth() - 150, getHeight() - 20);
    }

    private void drawScores(Graphics g)
    {
        g.setColor(Color.GREEN);
        for (int i = 0; i < NUM_CARS; i++)
        {
            g.drawString(i + " - Score: " + cars[i].score, getWidth() - 150, i * 20 + 20);
        }
    }

    private void drawTrack(Graphics g)
    {
        TrackBlock[][] blocks = track.getTrackBlocks();

        for (int i = 0; i < track.getNumRowBlocks(); i++)
        {
            for (int j = 0; j < track.getNumColBlocks(); j++)
            {
                blocks[i][j].drawBlock(j * TrackBlock.WIDTH, i * TrackBlock.HEIGHT, g);
            }
        }
    }

    private void drawCars(Graphics g)
    {
        /*RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);*/

        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < NUM_CARS; i++)
        {
            double rotation = Math.toRadians(cars[i].car.getDirection());
            double rotateX = Car.getSpriteWidth() / 2;
            double rotateY = Car.getSpriteHeight() / 2;
            AffineTransform tf = AffineTransform.getRotateInstance(rotation, rotateX, rotateY);
            AffineTransformOp op = new AffineTransformOp(tf, AffineTransformOp.TYPE_BILINEAR);
            g2d.drawImage(op.filter(Car.getImage(), null), (int) (cars[i].car.getX() - cars[i].car.getXOffset()), (int) (cars[i].car.getY() - cars[i].car.getYOffset()), this);

            /*int[][] boundingBox = car.getBoundingBox();
            g.setColor(Color.YELLOW);
            int[] xPts = {
                    boundingBox[0][0],
                    boundingBox[1][0],
                    boundingBox[2][0],
                    boundingBox[3][0]};
            int[] yPts = {
                    boundingBox[0][1],
                    boundingBox[1][1],
                    boundingBox[2][1],
                    boundingBox[3][1]};
            g.drawPolygon(xPts, yPts, xPts.length);*/
            g.setColor(Color.YELLOW);
            int[] tip = cars[i].car.getTipLocation();
            g.drawLine(tip[0], tip[1], (int) cars[i].rayEnds[0][0], (int) cars[i].rayEnds[0][1]);
            g.drawLine(tip[0], tip[1], (int) cars[i].rayEnds[1][0], (int) cars[i].rayEnds[1][1]);
            g.drawLine(tip[0], tip[1], (int) cars[i].rayEnds[2][0], (int) cars[i].rayEnds[2][1]);
        }
    }

    public boolean checkCollision(Graphics g, int carIndex)
    {
        int[][] boundingBox = cars[carIndex].car.getBoundingBox();
        TrackBlock[][] trackBlocks = track.getTrackBlocks();

        for (int i = 0; i < boundingBox.length; i++)
        {
            int col = boundingBox[i][0] / TrackBlock.WIDTH;
            int row = boundingBox[i][1] / TrackBlock.HEIGHT;

            switch(trackBlocks[row][col].getBlockType())
            {
                case VERTICAL:
                    if (boundingBox[i][0] <= col * TrackBlock.WIDTH + 9 || boundingBox[i][0] >= col * TrackBlock.WIDTH + 89)
                        return true;
                    break;
                case HORIZONTAL:
                    if (boundingBox[i][1] <= row * TrackBlock.HEIGHT + 9 || boundingBox[i][1] >= row * TrackBlock.HEIGHT + 89)
                        return true;
                    break;
                case NORTH_WEST:
                    if (Math.pow(boundingBox[i][0] - col * 100, 2) + Math.pow(boundingBox[i][1] - row * 100, 2) <= 100 ||
                            Math.pow(boundingBox[i][0] - col * 100, 2) + Math.pow(boundingBox[i][1] - row * 100, 2) >= 8100)
                        return true;
                    break;
                case NORTH_EAST:
                    if (Math.pow(boundingBox[i][0] - col * 100 - 99, 2) + Math.pow(boundingBox[i][1] - row * 100, 2) <= 100 ||
                            Math.pow(boundingBox[i][0] - col * 100 - 99, 2) + Math.pow(boundingBox[i][1] - row * 100, 2) >= 8100)
                        return true;
                    break;
                case SOUTH_WEST:
                    if (Math.pow(boundingBox[i][0] - col * 100, 2) + Math.pow(boundingBox[i][1] - row * 100 - 99, 2) <= 100 ||
                            Math.pow(boundingBox[i][0] - col * 100, 2) + Math.pow(boundingBox[i][1] - row * 100 - 99, 2) >= 8100)
                        return true;
                    break;
                case SOUTH_EAST:
                    if (Math.pow(col * 100 + 99 - boundingBox[i][0], 2) + Math.pow(row * 100 + 99 - boundingBox[i][1], 2) <= 100 ||
                            Math.pow(col * 100 + 99 - boundingBox[i][0], 2) + Math.pow(row * 100 + 99 - boundingBox[i][1], 2) >= 8100)
                        return true;
                    break;
            }
        }
        return false;
    }

    private double[] getRayDistances(int carIndex)
    {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        g2.dispose();

        double[] distances = new double[cars[carIndex].rayEnds.length];
        int[] tip = cars[carIndex].car.getTipLocation();

        cars[carIndex].rayEnds[0] = getRay(image, Math.toRadians(135 - cars[carIndex].car.getDirection()), carIndex);
        distances[0] = Math.pow(Math.pow(cars[carIndex].rayEnds[0][0] - tip[0], 2) + Math.pow(cars[carIndex].rayEnds[0][1] - tip[1], 2), 0.5);
        cars[carIndex].rayEnds[1] = getRay(image, Math.toRadians(90 - cars[carIndex].car.getDirection()), carIndex);
        distances[1] = Math.pow(Math.pow(cars[carIndex].rayEnds[1][0] - tip[0], 2) + Math.pow(cars[carIndex].rayEnds[1][1] - tip[1], 2), 0.5);
        cars[carIndex].rayEnds[2] = getRay(image, Math.toRadians(45 - cars[carIndex].car.getDirection()), carIndex);
        distances[2] = Math.pow(Math.pow(cars[carIndex].rayEnds[2][0] - tip[0], 2) + Math.pow(cars[carIndex].rayEnds[2][1] - tip[1], 2), 0.5);

        return distances;
    }

    private double[] getRay(BufferedImage image, double rayDirection, int carIndex)
    {
        int[] tip = cars[carIndex].car.getTipLocation();
        double[] ray = {tip[0], tip[1]};
        final double MAX_RAY_DISTANCE = 100;
        final double STEP_SIZE = 1;
        double dx = STEP_SIZE * Math.cos(rayDirection);
        double dy = STEP_SIZE * Math.sin(rayDirection);

        for (int i = 0; i < MAX_RAY_DISTANCE; i += STEP_SIZE)
        {
            if ((int) Math.round(ray[0]) < 0 || (int) Math.round(ray[0]) >= getWidth() ||
                    (int) Math.round(ray[1]) < 0 || (int) Math.round(ray[1]) >= getHeight())
                break;
            int pixel = image.getRGB((int) ray[0], (int) ray[1]);
            if (pixel == Color.WHITE.getRGB())
                break;
            ray[0] += dx;
            ray[1] -= dy;
        }

        // Take a step back
        ray[0] -= (2 / STEP_SIZE) * dx;
        ray[1] += (2 / STEP_SIZE) * dy;

        return ray;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        boolean notAllCarsCrashed = false;

        for (int i = 0; i < NUM_CARS; i++) {
            if (cars[i].score >= MAX_SCORE)
            {
                System.out.print(getAverageScore() + "-");
                System.out.println(cars[i].score);
                System.out.println(Arrays.toString(cars[i].learner.getWeights()));
                try {
                    System.in.read();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (!cars[i].crashed && checkCollision(this.getGraphics(), i)) {
                cars[i].score += (Math.pow(cars[i].car.getX() - cars[i].checkXPos, 2) + Math.pow(cars[i].car.getY() - cars[i].checkYPos, 2)) / 200;
                cars[i].crashed = true;
            } else if (!cars[i].crashed) {
                if (keyCode == KeyEvent.VK_RIGHT)
                    cars[i].car.turnRight();
                if (keyCode == KeyEvent.VK_LEFT)
                    cars[i].car.turnLeft();
                if (upArrow)
                    cars[i].car.move();

                double[] dist = getRayDistances(i);
                Learner.CarCommands commands = cars[i].learner.getCommands(dist);

                // execute commands
                switch (commands) {
                    case GO_STRAIGHT:
                        break;
                    case TURN_LEFT:
                        cars[i].car.turnLeft();
                        break;
                    case TURN_RIGHT:
                        cars[i].car.turnRight();
                        break;
                }

                cars[i].car.move();
            }
            updateScore(i);

            if (!cars[i].crashed)
                notAllCarsCrashed = true;
        }

        if (!notAllCarsCrashed)
        {
            generateOffspring();
            generation++;
        }

        repaint();
    }

    private double getAverageScore()
    {
        double scoreSum = 0;
        for (int i = 0; i < NUM_CARS; i++)
        {
            scoreSum += cars[i].score;
        }
        return scoreSum / NUM_CARS;
    }

    private void updateScore(int carIndex)
    {
        if ((int) (cars[carIndex].car.getX() / 100) != (int) (cars[carIndex].prevXPos / 100) ||
                (int) (cars[carIndex].car.getY() / 100) != (int) (cars[carIndex].prevYPos / 100))
        {
            cars[carIndex].score += 100;
            cars[carIndex].checkXPos = cars[carIndex].car.getX();
            cars[carIndex].checkYPos = cars[carIndex].car.getY();
        }
        cars[carIndex].prevXPos = cars[carIndex].car.getX();
        cars[carIndex].prevYPos = cars[carIndex].car.getY();
    }

    private void generateOffspring()
    {
        Arrays.sort(cars, Collections.reverseOrder());
        double offspringMutateRate = (prevAvgScore / getAverageScore()) * MAX_MUTATE_RATE;
        if(offspringMutateRate < 0) offspringMutateRate = 0;
        //System.out.println(offspringMutateRate);

        prevAvgScore = getAverageScore();

        double[] prob = new double[NUM_CARS];
        prob[0] = 1;
        newCars = new CarStruct[NUM_CARS];

        for (int i = 1; i < NUM_CARS; i++)
            prob[i] = prob[i - 1] - (2 * (NUM_CARS - i + 1) / (NUM_CARS * (NUM_CARS + 1.0)));

        double rand;
        int rank1Index, rank2Index;
        for (int i = 0; i < NUM_CARS; i++)
        {
            if (cars[i].score >= MAX_SCORE)
            {
                try {
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            DenseMatrix64F result = new DenseMatrix64F(1, PerceptronLearner.NUM_ROWS * PerceptronLearner.NUM_COLS);

            // Blend cars
            if (Math.random() <= BLEND_PROB)
            {
                rank1Index = rank2Index = NUM_CARS - 1;

                rand = Math.random();
                for (int j = 1; j < NUM_CARS; j++)
                {
                    if (rand > prob[j])
                    {
                        rank1Index = j - 1;
                        break;
                    }
                }

                do
                {
                    rand = Math.random();
                    for (int j = 1; j < NUM_CARS; j++)
                    {
                        if (rand > prob[j])
                        {
                            rank2Index = j - 1;
                            break;
                        }
                    }
                } while (rank2Index == rank1Index);

                DenseMatrix64F weights1 = new DenseMatrix64F(1, PerceptronLearner.NUM_ROWS * PerceptronLearner.NUM_COLS,
                        true, cars[rank1Index].learner.getWeights());
                DenseMatrix64F weights2 = new DenseMatrix64F(1, PerceptronLearner.NUM_ROWS * PerceptronLearner.NUM_COLS,
                        true, cars[rank2Index].learner.getWeights());
                CommonOps.add(weights1, weights2, result);
                CommonOps.divide(result, 2);
            }
            // Clone cars
            else
            {
                rank1Index = NUM_CARS - 1;
                rand = Math.random();
                for (int j = 1; j < NUM_CARS; j++)
                {
                    if (rand > prob[j])
                    {
                        rank1Index = j - 1;
                        break;
                    }
                }
                rank1Index = 0;

                result.setData(cars[rank1Index].learner.getWeights());
            }

            offspringMutateRate = 0.5;
            // Mutate cars
            if (Math.random() <= offspringMutateRate)
            {
                for (int j = 0; j < PerceptronLearner.NUM_ROWS * PerceptronLearner.NUM_COLS; j++)
                {
                    if (Math.random() <= WEIGHT_MUTATE_RATE)
                    {
                        if (Math.random() <= 0.5)
                            result.set(j, result.get(j) + MUTATE_STEP);
                        else
                            result.set(j, result.get(j) - MUTATE_STEP);
                        result.set(j, Math.random());
                    }
                }
            }

            newCars[i] = new CarStruct();
            newCars[i].learner.setWeights(result.data);
        }

        //System.out.println(Arrays.toString(cars));
        System.out.print(getAverageScore() + "-");
        System.out.println(cars[0].score);

        cars = newCars;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            cars = newCars;
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT)
            keyCode = e.getKeyCode();
        if(e.getKeyCode() == KeyEvent.VK_UP)
            upArrow = true;
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            try {
                System.in.read();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            cars = newCars;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT)
            keyCode = -1;
        if(e.getKeyCode() == KeyEvent.VK_UP)
            upArrow = false;
    }
}