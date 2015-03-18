import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics; 
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

// Welcome to this code

/**
 * Version 1.0
 * This version adds functional buttons to select which attributes you would like displayed
 * when the graph is opened.
 * 
 * The graph is drawn on a panel and included on the frame.
 * 
 * Design goals:
 * -fit to screen
 * -add button functionality
 * 
 * @author jwhitehead
 *
 */
public class LedaReaderv1 extends JPanel 
{    

    static JFrame frame = new JFrame("");
    static JFrame selectionFrame = new JFrame("Graph info");
    static JPanel buttonPanel = new JPanel();
    static JPanel infoPanel = new JPanel();
    static JPanel test = new JPanel();
    static File file;
    static JTextArea infoBox = new JTextArea(10,15);
    static LedaReaderv1 graph = new LedaReaderv1();
    
    
  //node Labeling
    private static boolean drawCenter = false;
    private static boolean drawCoords = false;
    private static boolean drawLabel  = true;
    private static boolean drawNodes  = true;
    private static boolean drawEdges  = false;
    private static boolean drawLegend = false;
    private static boolean drawGrid   = false;
    

        /**
        * 
        */
       private static final long serialVersionUID = 8498348670754342419L;

        public void updateInfo(int transRange, int fieldSize, int nodes)
        {
             infoBox.setText(null);
             infoBox.append("\n\n Transmission Range: " + transRange);
             infoBox.append("\n\n Field Size: " + fieldSize + "m x " + fieldSize + "m");
             infoBox.append("\n Number of Nodes: " + nodes);
        }

        public void paintComponent (Graphics g)     
        {

            super.paintComponent(g);
            Graphics2D g2d  = (Graphics2D) g;
            g2d.setColor(Color.blue);

            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int xCoord = (int) (dimension.getWidth() - 10);
            int yCoord = (int) (dimension.getHeight() - 100);

            System.out.println("xCoord = " + xCoord);
            System.out.println("yCoord = " + yCoord);

            
            
            
            //parse name information first
            String filename = file.getName();
            int nodes = Integer.parseInt(filename.substring(filename.indexOf("N") + 1, filename.indexOf("-F")));
            int fieldSize = Integer.parseInt(filename.substring(filename.indexOf("F") + 1, filename.indexOf("-T")));
            fieldSize = yCoord;
            int drawOffset = fieldSize / 2;
            int transRange = Integer.parseInt(filename.substring(filename.indexOf("T") + 1, filename.indexOf("T") + 4));

            updateInfo(transRange, fieldSize, nodes);


            Scanner filescanner;
            try {
                    filescanner = new Scanner(file);

                    Node[] nodeArray = new Node[nodes];
                    int nodeLabel = 0;

                    int x = 0;
                    int y = 0;
                    int nodeRadius = 12;
                    int lineCount = 1;



                    while (filescanner.hasNextLine()) 
                    {
                        String line = filescanner.nextLine();
                        //draw nodes
                        if (line.startsWith("|{("))
                        {
                            x = Integer.parseInt(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                            double x1 = x/500.0 * drawOffset;
                            x = (int)x1;
                            System.out.println("x = " + x);
                            
                            
                            y = Integer.parseInt(line.substring(line.indexOf(",") + 1, line.indexOf(")")));
                            double y1 = y/500.0 * drawOffset;
                            y = (int)y1;
                            System.out.println("y = " + y);
                            
                            
                            nodeArray[nodeLabel] = new Node(x, y);
                            nodeLabel++;
                            if (drawCenter) 
                            {//centerpoint
                                g2d.setColor(Color.GREEN);
                                g2d.drawLine(x + drawOffset,
                                                         y + drawOffset,
                                                         x + drawOffset,
                                                         y + drawOffset);
                                g2d.setColor(Color.BLUE);
                            }
                            if (drawCoords){//coords
                                g2d.drawString(""+x+","+y,
                                                           x + drawOffset - 25,
                                                           y + drawOffset + 25);
                            }
                            if (drawLabel){//node number
                                    g2d.drawString("" + nodeLabel, 
                                                               x + drawOffset - (nodeRadius/2),
                                                               y + drawOffset + (nodeRadius/2));
                            }
                            if (drawNodes){//node
                                g2d.setStroke(new BasicStroke(1));
                                    g2d.setColor(Color.RED);
                                    g2d.drawOval(x - nodeRadius + drawOffset,
                                                             y - nodeRadius + drawOffset, 
                                                             2 * nodeRadius, 
                                                             2 * nodeRadius);
                                    g2d.setColor(Color.BLUE);
                            }
                        }
                        else if (lineCount == 6 + nodes) {
                                    infoBox.append("\n\n Number of Edges:" + line);
                        }
                        
//draw edges- remember to subtract 1 from nodes to get label
                        else if (drawEdges && lineCount > (6 + nodes)) {
                                    int n1 = (Integer.parseInt(line.substring(0, line.indexOf(" ")))) -1;
                                    int n2 = (Integer.parseInt(line.substring(line.indexOf(" ") +1, line.indexOf(" 0")))) -1;
//						if (n1 == 0) //uncomment for testing just one node
                                    g2d.drawLine(nodeArray[n1].getX() + drawOffset,
                                                             nodeArray[n1].getY() + drawOffset,
                                                             nodeArray[n2].getX() + drawOffset,
                                                             nodeArray[n2].getY() + drawOffset);
                        }

                            lineCount++;
                    }


                    if (drawGrid) {
                            g2d.setColor(Color.LIGHT_GRAY);
                            g2d.setStroke(new BasicStroke(1));
                            for (int i = 0 ; i < fieldSize; i += fieldSize/20) {
                                    g2d.drawLine(0, i, fieldSize, i);
                                    g2d.drawLine(i,0, i, fieldSize);
                            }
                            g2d.setColor(Color.BLUE);
                    }
                    
                    if (drawLegend) {
                            g2d.setColor(Color.RED);
                            g2d.setStroke(new BasicStroke(1));
                            g2d.drawLine(50, 50, 300, 50);
                            g2d.drawLine(50, 45, 50, 55);
                            g2d.drawLine(300, 45, 300, 55);
                            g2d.drawString("250m", 50, 40);
                            g2d.drawString("Nodes: "+ nodes,50, 70);
                            g2d.drawString("Area: " + getWidth() + " x " + getHeight() , 50,85);
                    }


                    

            
            
            
            //grid lines
            g2d.setColor(Color.BLACK);
            //origin
            g2d.drawString("0", (fieldSize/2)+5, (fieldSize/2) - 5);

            //horz
            g2d.drawLine(0, fieldSize / 2, fieldSize, fieldSize / 2);
            g2d.drawString("-" + fieldSize/2, 0, (fieldSize/2) - 5);
            g2d.drawString("" + fieldSize/2 , fieldSize - 25, (fieldSize/2) - 5);


            //vert
            g2d.drawLine(fieldSize / 2, 0, fieldSize / 2, fieldSize);
            g2d.drawString("" + fieldSize/2, (fieldSize/2) + 5 , fieldSize - 30);
            g2d.drawString("-" + fieldSize/2 , (fieldSize/2) + 5, 15);

            //crosses
            for (int i = fieldSize/10 ; i < fieldSize; i += fieldSize/10) {
                    g2d.drawLine((fieldSize/2)-5, i, (fieldSize/2)+5, i);
                    g2d.drawLine(i,(fieldSize/2)-5, i, (fieldSize/2)+5);
            }
            for (int i = fieldSize/20 ; i < fieldSize; i += fieldSize/20) {
                    g2d.drawLine((fieldSize/2)-2, i, (fieldSize/2)+2, i);
                    g2d.drawLine(i,(fieldSize/2)-2, i, (fieldSize/2)+2);
            }


            } //end try

            catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    System.out.println("FAIL!");
            }   

        }


           public static void main(String[] args) 
           {
               
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                int xCoord = (int) (dimension.getWidth() - 10);
                int yCoord = (int) (dimension.getHeight() -  50);
                
                System.out.println("xCoord = " + xCoord);
                System.out.println("yCoord = " + yCoord);
               
               //button frame
               selectionFrame = new JFrame("Welcome to LEDA Reader");
               selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               selectionFrame.setResizable(false);


               infoBox.setEditable(false);
               infoPanel.add(infoBox);

               selectionFrame.setLayout(new BorderLayout());
               selectionFrame.add(infoPanel, BorderLayout.WEST);

               final JCheckBox buttonEdges = new JCheckBox("Show Edges");
               buttonEdges.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                            if (!drawEdges) {
                                    drawEdges = true;
                                    graph.repaint();
                            }
                            else {
                                    drawEdges = false;
                                    graph.repaint();
                            }
                    }
               });

               final JCheckBox buttonNodes = new JCheckBox("Show Nodes");
               buttonNodes.setSelected(true);
               buttonNodes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                            if (!drawNodes) {
                                    drawNodes = true;
                                    graph.repaint();
                            }
                            else {
                                    drawNodes = false;
                                    graph.repaint();
                            }
                    }
               });

               final JCheckBox buttonCenter = new JCheckBox("Show Center");
               buttonCenter.addActionListener(new ActionListener() {
                               @Override
                               public void actionPerformed(ActionEvent e) {
                                       if (!drawCenter) {
                                               drawCenter = true;
                                               graph.repaint();
                                       }
                                       else {
                                               drawCenter = false;
                                               graph.repaint();
                                       }
                               }
               });

               final JCheckBox buttonLegend = new JCheckBox("Show Legend");
               buttonLegend.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                                       if (!drawLegend) {
                                               drawLegend = true;
                                               graph.repaint();
                                       }
                                       else {
                                               drawLegend = false;
                                               graph.repaint();
                                       }
                       }
               });

               final JCheckBox buttonCoords = new JCheckBox("Show Coords");
               buttonCoords.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) {
                                       if (!drawCoords) {
                                               drawCoords = true;
                                               graph.repaint();
                                       }
                                       else {
                                               drawCoords = false;
                                               graph.repaint();
                                       }
                               }
               });

               final JCheckBox buttonLabel = new JCheckBox("Show Labels");
                buttonLabel.setSelected(true);
               buttonLabel.addActionListener(new ActionListener() {
                               @Override
                               public void actionPerformed(ActionEvent e) {
                                       if (!drawLabel) {
                                               drawLabel = true;
                                               graph.repaint();
                                       }
                                       else {
                                               drawLabel = false;
                                               graph.repaint();
                                       }
                               }
               });

               final JCheckBox buttonGrid = new JCheckBox("Show Grid");
               //buttonGrid.setSelected(true);
               buttonGrid.addActionListener(new ActionListener() {
                               @Override
                               public void actionPerformed(ActionEvent e) {
                                       if (!drawGrid) {
                                               drawGrid = true;
                                               graph.repaint();
                                       }
                                       else {
                                               drawGrid = false;
                                               graph.repaint();
                                       }
                               }
               });



               selectionFrame.add(buttonPanel, BorderLayout.EAST);
               buttonPanel.setLayout(new GridLayout(7,1));
               buttonPanel.add(buttonNodes);
               buttonPanel.add(buttonLegend);
               buttonPanel.add(buttonCenter);
               buttonPanel.add(buttonCoords);
               buttonPanel.add(buttonEdges);
               buttonPanel.add(buttonLabel);
               buttonPanel.add(buttonGrid);
               buttonPanel.setVisible(true);

               selectionFrame.pack();

               JFileChooser chooser = new JFileChooser();
                   int returnVal = chooser.showOpenDialog(chooser);
                   if(returnVal == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile().getName().startsWith("Graph")) {

                       frame.setTitle(("Displaying Graph: " + chooser.getSelectedFile().getName()));
                       file = chooser.getSelectedFile();

                   }
                   else {
                       JOptionPane.showMessageDialog(frame, "Bad File. Try Again.");
                       System.exit(0);
                   }

                   frame.setLocationByPlatform(true);
                   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                   frame.setResizable(false);

//		    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		    frame.setBounds(0,0,screenSize.width, screenSize.height);
                
                   
                frame.add(graph);
                frame.setSize(yCoord, yCoord);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
                selectionFrame.setLocationRelativeTo(frame);
                selectionFrame.setLocation(yCoord/2 - selectionFrame.getSize().width - 50, 0);
                selectionFrame.pack();
                selectionFrame.setAlwaysOnTop(true);
                selectionFrame.setVisible(true);
            }

}
