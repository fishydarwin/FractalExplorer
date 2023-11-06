package me.fishydarwin.fractalexplorer.view.component;

import me.fishydarwin.fractalexplorer.model.evaluator.compiler.FEXLCompiler;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.utils.FEMathUtils;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class JFractalRenderer extends JPanel {

    private final JImagePanel imagePanel;
    private final AppWindow belongingAppWindow;

    public JFractalRenderer(AppWindow belongingAppWindow) {
        super();
        this.belongingAppWindow = belongingAppWindow;

        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 35));

        imagePanel = new JImagePanel();
        imagePanel.setBackground(new Color(42, 42, 42));
        add(imagePanel);
        // TODO: right click save rendered frame
    }

    private int maxIterations = 100;

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    private double offsetX = 0;
    private double offsetY = 0;
    private double zoomScale = 1;


    private IStatement fexlInput;
    private final Map<Complex, Integer> evaluatedAlready = new ConcurrentHashMap<>(10000);
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    public boolean isRenderingFractal() { return isRendering.get(); }
    private int detailScale = 4;
    public void resetDetailScale() { detailScale = 4; }

    public void render(boolean reEvaluate) {
        if (reEvaluate) evaluatedAlready.clear();

        belongingAppWindow.setResizable(false);
        if (belongingAppWindow instanceof MainWindow)
            ((MainWindow) belongingAppWindow).getRenderProgressBar().setValue(5);
        isRendering.getAndSet(true);

        imagePanel.setImage(new BufferedImage(
                getWidth() / detailScale,
                getHeight() / detailScale,
                BufferedImage.TYPE_INT_RGB));
        int imageWidth = imagePanel.getImage().getWidth();
        int imageHeight = imagePanel.getImage().getHeight();

        detailScale /= 2;
        boolean detailFurther = true;
        if (detailScale < 1) {
            detailScale = 1;
            detailFurther = false;
        }
        final boolean fDetailFurther = detailFurther;

        double imageScaleX = ((double) getWidth()) / getHeight();
        int halfWidth = imageWidth / 2;
        int halfHeight = imageHeight / 2;
        double epsilon = 0.005 * (1 / zoomScale);

        int minBufferSize = Math.max(imageWidth, imageHeight);
        final int[] result = new int[minBufferSize * minBufferSize];

        AtomicInteger renderThreadsDone = new AtomicInteger(0);
        final int processorCount = Runtime.getRuntime().availableProcessors();
        final int regionLength = result.length / (processorCount - 1);

        for (int threadId = 0; threadId < processorCount - 1; threadId++) {

            final int fTid = threadId;
            new Thread(() -> {

                for (int i = fTid * regionLength; i < (fTid + 1) * regionLength; i++) {
                    int x = i / imageWidth - halfWidth;
                    int y = i % imageWidth - halfHeight;

                    double xScaled = ((double) x + halfWidth / 2.0)
                            / imageWidth * imageScaleX + offsetX * zoomScale;
                    xScaled = (xScaled * 4 - 2) * (1 / zoomScale);

                    double yScaled = ((double) y + halfHeight)
                            / imageHeight + offsetY * zoomScale;
                    yScaled = (yScaled * 4 - 2) * (1 / zoomScale);

                    Complex z = new Complex(0, 0);
                    Complex c = new Complex(xScaled, yScaled);

                    double roundScale = 125 * zoomScale;
                    Complex roundDownC = new Complex(
                            ((int) (xScaled * roundScale)) / roundScale,
                            ((int) (yScaled * roundScale)) / roundScale
                    );
                    if (evaluatedAlready.containsKey(roundDownC)) {
                        result[i] = evaluatedAlready.get(roundDownC);
                        continue;
                    }

                    Function<Pair<Complex, Complex>, Pair<Complex, Double>> fcxEval
                            = FEXLCompiler.generateFunction(fexlInput);

                    int iterations = 0;
                    while (iterations < maxIterations) {
                        Pair<Complex, Double> fcxRes = fcxEval.apply(new Pair<>(z, c));
                        z = fcxRes.getFirst();
                        double bound = fcxRes.getSecond();

                        double abs = z.abs();
                        if (abs < epsilon) break;
                        if (abs > bound) break;

                        iterations++;
                    }

                    double pointPercentage = ((double) iterations) / maxIterations;

                    int r = (int) Math.floor(
                            FEMathUtils.clamp(1 - Math.pow(3 * pointPercentage - 2, 2), 0, 1) * 255);
                    int g = (int) Math.floor(
                            FEMathUtils.clamp(1 - Math.pow(3 * pointPercentage - 1.5, 2), 0, 1) * 255);
                    int b = (int) Math.floor(
                            FEMathUtils.clamp(1 - Math.pow(3 * pointPercentage - 1, 2), 0, 1) * 255);

                    int rgb = (new Color(r, g, b)).getRGB();
                    evaluatedAlready.put(roundDownC, rgb);
                    result[i] = rgb;

                }

                imagePanel.setBuffer(result);
                imagePanel.calculateAndResizeImage();
                imagePanel.repaint();

                renderThreadsDone.getAndAdd(1);

            }).start();

        }

        new Thread(() -> {

            int whatIsDoneSoFar = 0;
            int whatAtomicSays;
            do {
                whatAtomicSays =  renderThreadsDone.get();
                if (whatIsDoneSoFar != whatAtomicSays) {
                    whatIsDoneSoFar++;
                    if (belongingAppWindow instanceof MainWindow) { // TODO: new interface instead of MainWindow?
                        ((MainWindow) belongingAppWindow).getRenderProgressBar()
                                .setValue((int) ((double) whatIsDoneSoFar / (processorCount - 1) * 100));
                    }
                }
            } while (whatIsDoneSoFar < processorCount - 1);

            if (belongingAppWindow instanceof MainWindow)
                ((MainWindow) belongingAppWindow).getRenderProgressBar().setValue(0);
            belongingAppWindow.setResizable(true);
            isRendering.getAndSet(false);

            if (fDetailFurther) EventQueue.invokeLater(() -> render(false));

        }).start();

    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(double zoomScale) {
        this.zoomScale = zoomScale;
    }

    public void reRender(boolean reEvaluate) {
        render(reEvaluate);
    }

    public IStatement getFexlInput() {
        return fexlInput;
    }

    public void setFexlInput(IStatement fexlInput) {
        this.fexlInput = fexlInput;
    }
}
