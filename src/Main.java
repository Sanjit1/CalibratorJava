//Import Libraries
/*
 Page setup is somewhat like this
            _____________________________________________________________________________________________________
           |                   Resistance  Temperature                                                           |
           |                 R1:_________  T1:________                                                           |
           |                 R2:_________  T2:________                                                           |
           |                 R3:_________  T3:________                                                           |
           |                 Calculate    <- Button                                                              |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |                                                                                                     |
           |_____________________________________________________________________________________________________|
Once you enter the values correctly and press calculate, then it will look somewhat like this:
            _____________________________________________________________________________________________________
           |   Resistance  Temperature                      |                         Graph                      |
           | R1:_________  T1:________                      | ..                                                 |
           | R2:_________  T2:________                      |   ..                                               |
           | R3:_________  T3:________                      |     ..                                             |
           | Calculate    <- Button                         |      ..                                            |
           | A = ..........                                 |        ..                                          |
           | B = ..........                                 |          ..                                        |
           | C = ..........                                 |            ..                                      |
           | Copy As Number                   <-Button      |               ..                                   |
           | Copy As Declaration for Arduino  <-Button      |                  ..                                |
           | Model Tester                                   |                       ..                           |
           | Res:________                                   |                            ..                      |
           | Tes            <-Button                        |                                ..                  |
           | Temp =                                         |                                     ..             |
           |________________________________________________|____________________________________________________|
And drawing a tree chart of the variable names:
For all JPanel, JLabel, etc variables, come here
                                             __________________parent_________________
                                            |                                         |
          __________________________________Left__________________________________   Right______
         |      |    |  |  |         |             |        |      |       |    |             |
     __rtFull__  calc  A  B  C  copyAsDeclaration  justCopy  mT _inpHolder_ tst  tmp           cp
    |        |                                                 |           |
 resTem  ___RTVal_______                                     resInp       inp
        |    |    |     |
      rPr  rVal  tPr  tVal
     R1Pr, R1    T1Pr
     R2Pr, R2    T2Pr
     R3Pr  R3 ....... Ya i think you get this part(I made the stuff in RTVal as a list..... yeaaa... I guess you get it)

 */
import Jama.Matrix; //Library to solve matrices
import org.jfree.chart.ChartFactory; // Graph plotting library
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.awt.*; // Font and Toolkit libraries
import java.awt.datatransfer.Clipboard; // Clipboard String Selection etc classes
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {
    // Declare Res temp and coefficient variables.
    private double RVal1; // Resistance input 1 value
    private double RVal2; // Resistance input 2 value
    private double RVal3; // Resistance input 2 value
    private double TVal1; // Temperature input 1 value
    private double TVal2; // Temperature input 2 value
    private double TVal3; // Temperature input 3 value
    private double AVal;  // Coefficient A value
    private double BVal;  // Coefficient B value
    private double CVal;  // Coefficient C value

    // Declare UI variables
    private JTextField R1; // Resistance input 1 GUI
    private JTextField R2; // Resistance input 2 GUI
    private JTextField R3; // Resistance input 3 GUI
    private JTextField T1; // Temperature input 1 GUI
    private JTextField T2; // Temperature input 2 GUI
    private JTextField T3; // Temperature input 3 GUI
    private JLabel A; // Coefficient A GUI
    private JLabel B; // Coefficient B GUI
    private JLabel C; // Coefficient C GUI
    private Clipboard clipboard; // Clipboard variable, for copying
    private JPanel parent; // Parent Layout: holds left and right (see line 37)

    public static void main(String[] args) { // Main function
        java.awt.EventQueue.invokeLater(() -> {
            Main frame = new Main(); // Main form : The screen object. See line 101;
            frame.update(frame.getGraphics()); // update graphics
        });
    }

    private Main() {
        Font textFont = new Font("Arial", Font.PLAIN, 14); // Initialise the main font that will be used in the JForm
        KeyAdapter numberInputAdapter = new KeyAdapter() { // Function to remove any letters or special characters from the JTextInput, so that the code only inputs numbers
            @Override
            public void keyTyped(KeyEvent kp) {
                if (!"0123456789.-".contains(String.valueOf(kp.getKeyChar()))) kp.consume(); // Check if the input char belongs to the string, if not remove it.
            }
        };
        ChartPanel cp = new ChartPanel(defaultCh()); // Chart object (see line 37 cp)
        JFrame frame = new JFrame(); // Frame object main function looks for the frame object in this class and makes it visible        // Orientation: H
        JPanel right = new JPanel(); // right side of the frame. Includes the chart.(see line .. ya you know the drill for this)        // Orientation: H
        JPanel left = new JPanel(); // left side of the frame. Includes the res and temp inputs, A,B,C Copy buttons, and model tester.  // Orientation: V
        JPanel rtFull = new JPanel(); // holds the resistance and temperature texts, inputs, and input hints                            // Orientation: V
        JPanel RTVal = new JPanel(); // Holds just the inputs and hints                                                                 // Orientation: H
        JPanel RVal = new JPanel(); // Holds the resistance inputs                                                                      // Orientation: V
        JPanel TVal = new JPanel(); // Holds the temperature inputs                                                                     // Orientation: V
        JPanel rPr = new JPanel(); // Holds the resistance hints                                                                        // Orientation: V
        JPanel tPr = new JPanel(); // Holds the temperature hints                                                                       // Orientation: V
        JLabel resTemp = new JLabel(); // Resistance Temperature label, indicating what to input, and in what unit to input
        JLabel R1Pr = new JLabel(); // Hint
        JLabel R2Pr = new JLabel(); // Hint
        JLabel R3Pr = new JLabel(); // Hint
        JLabel T1Pr = new JLabel(); // Hint
        JLabel T2Pr = new JLabel(); // Hint
        JLabel T3Pr = new JLabel(); // Hint
        JButton calc = new JButton(); // Calculate button, to Calculate the coefficients based of the inputs
        JButton copyAsDeclaration = new JButton(); // Button to copy the Coefficients, and paste then in the Arduino IDE, as a declaration
        JButton justCopy = new JButton(); // Copy as numbers Button
        JLabel mT = new JLabel(); // Label to indicate that users can test their models, with the model tester
        JPanel inpHolder = new JPanel(); // Holds the resistance hint, and input                                                        // Orientation: V
        JLabel resInp = new JLabel(); // Resistance hint
        JTextField inp = new JTextField(10); // Resistance input
        JButton tst = new JButton(); // Button to tell program to find temp for inputted resistance
        JLabel tmp = new JLabel(); // Label to show the tested temperature, to verify the coefficients
        String nL = System.lineSeparator(); // String, to make new line in most cases probably /n
        parent = new JPanel(); // Parent object

        R1 = new JTextField(10); // Initialisation
        R2 = new JTextField(10);
        R3 = new JTextField(10);
        T1 = new JTextField(10);
        T2 = new JTextField(10);
        T3 = new JTextField(10);
        A = new JLabel();
        B = new JLabel();
        C = new JLabel();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // Set all UI invisible, because we do not want the users to see the UI, until they have calculated
        A.setVisible(false);
        B.setVisible(false);
        C.setVisible(false);
        justCopy.setVisible(false);
        copyAsDeclaration.setVisible(false);
        mT.setVisible(false);
        inpHolder.setVisible(false);
        tst.setVisible(false);
        tmp.setVisible(false);
        cp.setVisible(false);


        frame.setTitle("Thermistor Calibrator"); // Do NOT tell me you do not know what this means(Unless you have not run the program)
        frame.setSize(1100, 550); // By now you should know...........
        calc.setText("Calculate"); // Set Button text
        resTemp.setText("Resistance Ω     Temperature °C"); // Basically this section of Code sets text font, etc Pretty self-explanatory
        resTemp.setFont(textFont); // Set every font to that same font
        R1Pr.setText("R1:");
        R1Pr.setFont(textFont);
        resInp.setText("Res");
        resInp.setFont(textFont);
        R1.addKeyListener(numberInputAdapter); // The number input adapter, that only allows 0123456789.-
        R2Pr.setText("R2:");
        R2Pr.setFont(textFont);
        R2.addKeyListener(numberInputAdapter);
        R3Pr.setText("R3:");
        R3Pr.setFont(textFont);
        R3.addKeyListener(numberInputAdapter);
        T1Pr.setText("T1:");
        T1Pr.setFont(textFont);
        T1.addKeyListener(numberInputAdapter);
        T2Pr.setText("T2:");
        T2Pr.setFont(textFont);
        T2.addKeyListener(numberInputAdapter);
        T3Pr.setText("T3:");
        T3Pr.setFont(textFont);
        T3.addKeyListener(numberInputAdapter);

        calc.addActionListener((ActionEvent e) -> { // Set listener for Calculate
            RVal1 = Double.parseDouble(R1.getText());   // To calculate, we need to convert the users input to a number. So......
            RVal2 = Double.parseDouble(R2.getText());
            RVal3 = Double.parseDouble(R3.getText());
            TVal1 = Double.parseDouble(T1.getText());
            TVal2 = Double.parseDouble(T2.getText());
            TVal3 = Double.parseDouble(T3.getText());

            Matrix ABC = coolMathGames(RVal1, RVal2, RVal3, TVal1, TVal2, TVal3); // Find the resulting matrix, from the Res and temp values.
            // See Function coolMathGames for more. Why did I name it that? IDK
            // The returned matrix is in the form {{A},{B},{C}} so the indexes(Is that the right spelling tho?) will be 0,0; 1,0; 2,0
            // THat should explain the next few lines
            AVal = ABC.get(0, 0);
            BVal = ABC.get(1, 0);
            CVal = ABC.get(2, 0);

            A.setVisible(true); // So now that we have the values, we will set the values and make them visible. Whats that? I made them visible first?
            B.setVisible(true); // Well First of all It is the same thing. Second of all I don't care lol
            C.setVisible(true);
            justCopy.setVisible(true);
            copyAsDeclaration.setVisible(true);
            mT.setVisible(true);
            inpHolder.setVisible(true);
            tst.setVisible(true);
            tmp.setVisible(true);
            cp.setVisible(true);
            A.setText("A = " + AVal); // Happy? I set the values
            B.setText("B = " + BVal);
            C.setText("C = " + CVal);
            DefaultXYDataset ds = new DefaultXYDataset();  // Dataset Object
            double[][] data = new double[2][]; // Make the data array
            data[0] = new double[1000]; // Initialise the arrays inside the array
            data[1] = new double[1000];
            // I wanted to include up to 50000 values, but that is too much, so I just did a thousand, but covered up to 50000, by multiplying 1 to 1000 by 50
            // So I have intervals of 50. I also start from 1, because if I did 0, then that would get Temp, for res = 50*0 = 0, which is illegal(I did not make these laws Physics did)
            for (int i = 1; i < 999; i++) {
                data[1][i] = i * 50; // Why times 50? To make it fast  // Resistance Y
                data[0][i] = Double.parseDouble(getTemp(i * 50));   // Temperature X (Because temperature depends on resistance: Dependant variable goes on Y
            }

            ds.addSeries("Temp Res graph", data); // Add series name and data
            JFreeChart chart = ChartFactory.createXYLineChart("Temp Res Chart", "Temperature", "Resistance", ds, PlotOrientation.VERTICAL, true, true, false);
            // Set title axises Orientation etc
            XYPlot plot = chart.getXYPlot(); // From here I try to change Domain
            NumberAxis axis = (NumberAxis) plot.getDomainAxis();
            axis.setRange(-10, 100);


            cp.setChart(chart); // Add the chart to the GUI element
        });

        // Ok now we are back out of the onclick. So all this happens before onClick
        // These are the GUI elements, that are seen after the Calculate button
        A.setFont(textFont);
        B.setFont(textFont);
        C.setFont(textFont);
        justCopy.setText("Copy numbers"); // Setting texts, etc Same old stuff
        copyAsDeclaration.setText("Copy as Arduino declaration");
        // Ok so the next listeners are the copy button onClicks.
        // Since they are single line expressions, there is no need to but brackets after ->
        // To copy to clipboard we need string selection, which needs data(string) and owner-> which does not matter, so null
        // as for data, if we copy as declaration, we have a different string, than Just copy.
        // Also the nL is line separator string for New line (/n)
        copyAsDeclaration.addActionListener((ActionEvent e) -> clipboard.setContents(new StringSelection("double A = " + AVal + ";" + nL + "double B = " + BVal + ";" + nL + "double C = " + CVal + ";"), null));
        justCopy.addActionListener((ActionEvent e) -> clipboard.setContents(new StringSelection("A = " + AVal + nL + "B = " + BVal + nL + "C = " + CVal), null));
        mT.setText("Model Tester"); // Set text
        mT.setFont(textFont); // Same ol' Stuff
        inp.addKeyListener(numberInputAdapter);
        tmp.setText("Temp = ");
        tst.setText("Test");
        tst.addActionListener((ActionEvent e) -> { // When someone clicks the test button
            tmp.setText("Temp = " + getTemp(Double.parseDouble(inp.getText())));
            tmp.setFont(textFont);
        });


        rPr.setLayout(new BoxLayout(rPr, BoxLayout.Y_AXIS)); // Set Orientation
        rPr.add(R1Pr); // Add children (See line 37 for more)
        rPr.add(R2Pr);
        rPr.add(R3Pr);
        RTVal.add(rPr);
        RVal.setLayout(new BoxLayout(RVal, BoxLayout.Y_AXIS));
        RVal.add(R1);
        RVal.add(R2);
        RVal.add(R3);
        RTVal.add(RVal);
        tPr.setLayout(new BoxLayout(tPr, BoxLayout.Y_AXIS));
        tPr.add(T1Pr);
        tPr.add(T2Pr);
        tPr.add(T3Pr);
        RTVal.add(tPr);
        TVal.setLayout(new BoxLayout(TVal, BoxLayout.Y_AXIS));
        TVal.add(T1);
        TVal.add(T2);
        TVal.add(T3);
        RTVal.add(TVal);
        rtFull.setLayout(new BoxLayout(rtFull, BoxLayout.Y_AXIS));
        rtFull.add(resTemp);
        rtFull.add(RTVal);
        left.add(rtFull);
        left.add(calc);
        left.add(A);
        left.add(B);
        left.add(C);
        left.add(justCopy);
        left.add(copyAsDeclaration);
        left.add(mT);
        inpHolder.add(resInp);
        inpHolder.add(inp);
        left.add(inpHolder);
        left.add(tst);
        left.add(tmp);
        right.add(cp);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        parent.add(left);
        parent.add(right);
        frame.add(parent);
        frame.setVisible(true); // Maybe i do not need this but I do not care

    }

    private JFreeChart defaultCh() { // This should be ignores while initialising the chart, it does not like to be alone, and needs a chart, so..... ya
        DefaultXYDataset ds = new DefaultXYDataset();
        double[][] data = {
                {0, 1, 2},
                {2, 3, 4}
        };
        ds.addSeries("Temp Res graph", data);
        return ChartFactory.createXYLineChart("Temp Res Chart", "x", "y", ds, PlotOrientation.VERTICAL, true, true, false);

    }

    private Matrix coolMathGames(double R1, double R2, double R3, double T1, double T2, double T3) { // Math time and theory time
        T1 = T1 + 273.15;
        T2 = T2 + 273.15;
        T3 = T3 + 273.15;

        double[][] M1 = {
                {1, ln(R1), cb(ln(R1))},
                {1, ln(R2), cb(ln(R2))},
                {1, ln(R3), cb(ln(R3))}
        };
        double[] M3 = {1 / T1, 1 / T2, 1 / T3};
        Matrix lhs = new Matrix(M1);
        Matrix rhs = new Matrix(M3, 3);
        return lhs.solve(rhs);
    }

    private String getTemp(double R) {
        return (1 / (AVal + (BVal * ln(R)) + (CVal * cb(ln(R)))) - 273.15) + "";
    }

    private double ln(double numb) {
        return Math.log(numb) / Math.log(Math.E);
    }

    private double cb(double numb) {
        return numb * numb * numb;
    }


}
