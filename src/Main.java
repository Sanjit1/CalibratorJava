import Jama.Matrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {
    private double RVal1;
    private double RVal2;
    private double RVal3;
    private double TVal1;
    private double TVal2;
    private double TVal3;
    private double AVal;
    private double BVal;
    private double CVal;

    private JTextField R1;
    private JTextField R2;
    private JTextField R3;
    private JTextField T1;
    private JTextField T2;
    private JTextField T3;
    private JLabel A;
    private JLabel B;
    private JLabel C;
    private Clipboard clipboard;
    private JPanel parent;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            Main frame = new Main();
            frame.update(frame.getGraphics());
        });
    }

    private Main() {
        Font textFont = new Font("Arial", Font.PLAIN, 14);
        KeyAdapter numberInputAdapter = new KeyAdapter() { @Override public void keyTyped(KeyEvent kp) {if (!"0123456789.-".contains(String.valueOf(kp.getKeyChar()))) kp.consume();}};
        ChartPanel cp = new ChartPanel(defaultCh());
        JFrame frame = new JFrame();
        JPanel right = new JPanel();
        JPanel left = new JPanel();
        JPanel rtFull = new JPanel();
        JPanel RTVal = new JPanel();
        JPanel RVal = new JPanel();
        JPanel TVal = new JPanel();
        JPanel rPr = new JPanel();
        JPanel tPr = new JPanel();
        JLabel resTemp = new JLabel();
        JLabel R1Pr = new JLabel();
        JLabel R2Pr = new JLabel();
        JLabel R3Pr = new JLabel();
        JLabel T1Pr = new JLabel();
        JLabel T2Pr = new JLabel();
        JLabel T3Pr = new JLabel();
        JButton calc = new JButton();
        JButton copyAsDeclaration = new JButton();
        JButton justCopy = new JButton();
        JLabel mT = new JLabel();
        JTextField inp = new JTextField();
        JButton tst = new JButton();
        JPanel inpHolder = new JPanel();
        JLabel tmp = new JLabel();
        String nL = System.lineSeparator();
        parent = new JPanel();

        R1 = new JTextField(10);
        R2 = new JTextField(10);
        R3 = new JTextField(10);
        T1 = new JTextField(10);
        T2 = new JTextField(10);
        T3 = new JTextField(10);
        A = new JLabel();
        B = new JLabel();
        C = new JLabel();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

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



        frame.setTitle("Thermistor Calibrator");
        frame.setSize(1070, 500);
        calc.setText("Calculate");
        resTemp.setText("Resistance Ω     Temperature °C");
        resTemp.setFont(textFont);
        R1Pr.setText("R1:"); R1Pr.setFont(textFont); R1.addKeyListener(numberInputAdapter);
        R2Pr.setText("R2:"); R2Pr.setFont(textFont); R2.addKeyListener(numberInputAdapter);
        R3Pr.setText("R3:"); R3Pr.setFont(textFont); R3.addKeyListener(numberInputAdapter);
        T1Pr.setText("T1:"); T1Pr.setFont(textFont); T1.addKeyListener(numberInputAdapter);
        T2Pr.setText("T2:"); T2Pr.setFont(textFont); T2.addKeyListener(numberInputAdapter);
        T3Pr.setText("T3:"); T3Pr.setFont(textFont); T3.addKeyListener(numberInputAdapter);

        calc.addActionListener((ActionEvent e) -> {
            RVal1 = Double.parseDouble(R1.getText());
            RVal2 = Double.parseDouble(R2.getText());
            RVal3 = Double.parseDouble(R3.getText());
            TVal1 = Double.parseDouble(T1.getText());
            TVal2 = Double.parseDouble(T2.getText());
            TVal3 = Double.parseDouble(T3.getText());
            Matrix ABC = coolMathGames(RVal1, RVal2, RVal3, TVal1, TVal2, TVal3);
            AVal = ABC.get(0,0);
            BVal = ABC.get(1,0);
            CVal = ABC.get(2,0);

            A.setVisible(true);
            B.setVisible(true);
            C.setVisible(true);
            justCopy.setVisible(true);
            copyAsDeclaration.setVisible(true);
            mT.setVisible(true);
            inpHolder.setVisible(true);
            tst.setVisible(true);
            tmp.setVisible(true);
            cp.setVisible(true);
            A.setText(AVal + "");
            B.setText(BVal + "");
            C.setText(CVal + "");
            DefaultXYDataset ds = new DefaultXYDataset();
            double[][] data = new double[2][];
            data[0] = new double[1000];
            data[1] = new double[1000];
            for (int i = 1; i < 999; i++) {
                data[1][i] = i * 50;
                data[0][i] = Double.parseDouble(getTemp(i * 50));
            }

            ds.addSeries("Temp Res graph", data);
            JFreeChart chart = ChartFactory.createXYLineChart("Temp Res Chart", "Temperature","Resistance", ds, PlotOrientation.VERTICAL,true,true,false);
            XYPlot plot = chart.getXYPlot();
            NumberAxis axis = (NumberAxis)plot.getDomainAxis();
            axis.setRange(-10,100);

            cp.setChart(chart);
            parent.add(cp);
        });

        A.setText("A = "); A.setFont(textFont);
        B.setText("B = "); B.setFont(textFont);
        C.setText("C = "); C.setFont(textFont);
        justCopy.setText("Copy numbers");
        copyAsDeclaration.setText("Copy as Arduino declaration");
        copyAsDeclaration.addActionListener((ActionEvent e) -> clipboard.setContents(new StringSelection("double A = " + AVal + ";" + nL + "double B = " + BVal + ";" + nL + "double C = " + CVal + ";"),null));
        justCopy.addActionListener((ActionEvent e) -> clipboard.setContents(new StringSelection("A = " + AVal + nL + "B = " + BVal + nL + "C = " + CVal) , null));
        mT.setText("Model Tester"); mT.setFont(new Font("Arial", Font.PLAIN,15));
        inp.addKeyListener(numberInputAdapter);
        inpHolder.setLayout(new BoxLayout(inpHolder, BoxLayout.Y_AXIS)); inpHolder.setSize(20,1);
        tmp.setText("Temp = ");
        tst.setText("Test");
        tst.addActionListener((ActionEvent e) -> {
            tmp.setText("Temp = " + getTemp(Double.parseDouble(inp.getText()))); tmp.setFont(new Font("Arial", Font.PLAIN,15));
        });



        rPr.setLayout(new BoxLayout(rPr, BoxLayout.Y_AXIS));
        rPr.add(R1Pr);
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
        frame.setVisible(true);

    }

    private JFreeChart defaultCh(){
        DefaultXYDataset ds = new DefaultXYDataset();
        double[][] data = {
                {0,1,2},
                {2,3,4}
        };
        ds.addSeries("Temp Res graph", data);
        return ChartFactory.createXYLineChart("Temp Res Chart", "x","y", ds, PlotOrientation.VERTICAL,true,true,false);

    }

    private Matrix coolMathGames(double R1, double R2, double R3, double T1, double T2, double T3) {
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
