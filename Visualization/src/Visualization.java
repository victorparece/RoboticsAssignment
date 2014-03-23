import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Visualization extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;

    private static int sizeX = 500;
    private static int sizeY = 500;

    //private static String file = "A*Planner-Trial0.txt";
    private static String file = "A*Planner-Trial1.txt";
    //private static String file = "QLearning-Trial0.txt";
    //private static String file = "QLearning-Trial1.txt";

    private WorldAnimation worldAnimation;

    public static void main(String[] a)
    {
        Visualization app = new Visualization();
        app.CreateControls();
    }

    public Visualization()
    {
        setResizable(false);
        setTitle("Grid World");
        setSize(sizeX, sizeY);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void CreateControls()
    {
        JPanel jPanel = new JPanel();

        JButton buttonStart = new JButton("Start");
        buttonStart.addActionListener(this);
        jPanel.add(buttonStart);

        this.add(jPanel, BorderLayout.NORTH);
        setVisible(true);

        worldAnimation = new WorldAnimation(sizeX, sizeY-60, file);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.add(worldAnimation, BorderLayout.CENTER);
        this.validate();
    }

    class WorldAnimation extends JPanel implements ActionListener
    {
        private static final long serialVersionUID = 1L;
        private int currentFrame = 0;
        private Timer timer;
        private ArrayList<char[][]> frames = new ArrayList<char[][]>();

        private int sizeX, sizeY;

        public WorldAnimation(int sizeX, int sizeY, String file)
        {
            this.sizeX = sizeX;
            this.sizeY = sizeY;

            timer = new Timer(1000, this);
            timer.start();

            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();

                int worldWidth = 0, worldHeight = 0;

                //Read all frames in from input file
                while (line != null)
                {
                    //New frame
                    if (line.startsWith("----"))
                    {
                        line = br.readLine();

                        char[][] frame = new char[line.length()][line.length()];
                        int currentLine = 0;

                        while (line != null && !line.startsWith("----"))
                        {
                            frame[currentLine] = line.toCharArray();

                            line = br.readLine();
                            currentLine++;
                        }

                        frames.add(frame);
                        continue;
                    }

                    line = br.readLine();
                }
            } catch (Exception e){}
        }

        public void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);
            Graphics2D graphics2d = (Graphics2D)graphics;

            char[][] frame = frames.get(currentFrame);

            int worldHeight = frame.length;
            int worldWidth = frame[0].length;

            int squareSizeY = (sizeY)/worldHeight;
            int squareSizeX = (sizeX)/worldWidth;

            for (int i = 0; i < frame.length; i++)
                for (int j = 0; j < frame[i].length; j++)
                {
                    Color color = Color.white;

                    switch (frame[i][j])
                    {
                        case '0':
                            color = Color.darkGray;
                            break;
                        case '1':
                            color = Color.gray;
                            break;
                        case 'G':
                            color = Color.blue;
                            break;
                        case '.':
                        case 'R':
                        case 'X':
                            color = Color.red;
                            break;
                        case 'o':
                            color = Color.cyan;
                            break;
                        case 'x':
                            color = Color.yellow;
                            break;
                    }

                    graphics.setColor(color);
                    graphics2d.fillRect(j*squareSizeX, i*squareSizeY, squareSizeX, squareSizeY);
                }
        }

        /*
         * The timer will fire an action every 10 milliseconds, moving
         * our circle by 1 each time.
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {

            if (currentFrame == frames.size()-1)
            {
                timer.stop();
                //setVisible(false);
            }
            else
            {
                repaint();
                currentFrame++;
            }
        }
    }
}
