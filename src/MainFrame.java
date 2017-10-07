import javax.swing.*;

public class MainFrame extends JFrame
{
    public DrawCanvas canvas;
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 700;

    public MainFrame()
    {
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 39);
        setTitle("Hot Wheels");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        canvas = new DrawCanvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        setContentPane(canvas);
    }

    public static void main(String[] args) throws InterruptedException {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}