package me.fishydarwin.fractalexplorer.view.component;

import me.fishydarwin.fractalexplorer.model.evaluator.compiler.FEXLCompiler;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.utils.FEMathUtils;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;
import me.fishydarwin.fractalexplorer.view.window.popup.PopupWindow;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    private int maxIterations = 50;

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        ((MainWindow) belongingAppWindow).setStatusText("Max Iterations (" + maxIterations + ")");
    }

    private double offsetX = 0;
    private double offsetY = 0;
    private double zoomScale = 1;

    private double paletteR = 2;
    private double paletteG = 1.5;
    private double paletteB = 1;

    private int detailScale = 1;
    private boolean checkerboard = false;

    private IStatement fexlInput;
    private final Map<Complex, Integer> evaluatedAlready = new ConcurrentHashMap<>();
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    public boolean isRenderingFractal() { return isRendering.get(); }

    public void render(boolean reEvaluate) {
        if (reEvaluate) {
            evaluatedAlready.clear();
        }

        belongingAppWindow.setResizable(false);
        if (belongingAppWindow instanceof MainWindow)
            ((MainWindow) belongingAppWindow).getRenderProgressBar().setValue(5);
        isRendering.getAndSet(true);

        imagePanel.setImage(
                new BufferedImage(getWidth() / detailScale, getHeight() / detailScale,
                        BufferedImage.TYPE_INT_RGB)
        );
        int imageWidth = imagePanel.getImage().getWidth();
        int imageHeight = imagePanel.getImage().getHeight();

        double imageScaleX = ((double) getWidth()) / getHeight();
        int halfWidth = imageWidth / 2;
        int halfHeight = imageHeight / 2;
        double epsilon = 0.005 * (1 / zoomScale);

        int minBufferSize = Math.max(imageWidth, imageHeight);
        int[] result = new int[minBufferSize * minBufferSize];

        AtomicInteger renderThreadsDone = new AtomicInteger(0);
        final int processorCount = Runtime.getRuntime().availableProcessors();
        final int regionLength = result.length / (processorCount - 1);

        Function<Pair<Complex, Complex>, Pair<Complex, Double>> fcxEval;
        try {
            fcxEval = FEXLCompiler.generateFunction(fexlInput);
        } catch (Exception ex) {
            try {
                PopupWindow.make("Renderer Error!", ex.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        for (int threadId = 0; threadId < processorCount - 1; threadId++) {

            final int fTid = threadId;
            new Thread(() -> {

                final int stepSize = checkerboard ? 2 : 1;
                final int begin = checkerboard ? fTid * regionLength + 2 :  fTid * regionLength;

                for (int i = begin; i < (fTid + 1) * regionLength; i += stepSize) {
                    int x = i / imageWidth - halfWidth;
                    int y = i % imageWidth - halfHeight;

                    double xScaled = ((double) x + halfWidth / 2.0 + offsetX * zoomScale)
                            / imageWidth * imageScaleX;
                    xScaled = (xScaled * 4 - 2) * (1 / zoomScale);

                    double yScaled = ((double) y + halfHeight + offsetY * zoomScale)
                            / imageHeight;
                    yScaled = (yScaled * 4 - 2) * (1 / zoomScale);

                    Complex z = new Complex(0, 0);
                    Complex c = new Complex(xScaled, yScaled);

                    if (evaluatedAlready.containsKey(c)) {
                        result[i] = evaluatedAlready.get(c);
                        if (checkerboard) {
                            final Color currentColor = new Color(result[i]);
                            final Color previousColor = new Color(result[i - 2]);
                            result[i - 1] = new Color(
                                    (currentColor.getRed() + previousColor.getRed()) / 2,
                                    (currentColor.getGreen() + previousColor.getGreen()) / 2,
                                    (currentColor.getBlue() + previousColor.getBlue()) / 2
                            ).getRGB();
                        }
                        continue;
                    }

                    int iterations = 0;
                    while (iterations < maxIterations) {
                        Pair<Complex, Double> fcxRes = fcxEval.apply(new Pair<>(z, c));
                        z = fcxRes.getFirst();
                        double bound = fcxRes.getSecond();

                        double abs = z.getReal() * z.getReal() + z.getImaginary() * z.getImaginary();
                        if (abs < epsilon * epsilon) break;
                        if (abs > bound * bound) break;

                        iterations++;
                    }

                    double pointPercentage = ((double) iterations) / maxIterations;

                    int r = (int) Math.floor(
                            FEMathUtils.clamp(1 - Math.pow(3 * pointPercentage - paletteR, 2),
                                    0, 1) * 255);
                    int g = (int) Math.floor(
                            FEMathUtils.clamp(1 - Math.pow(3 * pointPercentage - paletteG, 2),
                                    0, 1) * 255);
                    int b = (int) Math.floor(
                            FEMathUtils.clamp(1 - Math.pow(3 * pointPercentage - paletteB, 2),
                                    0, 1) * 255);

                    int rgb = (new Color(r, g, b)).getRGB();
                    evaluatedAlready.put(c, rgb);
                    result[i] = rgb;
                    if (checkerboard) {
                        final Color currentColor = new Color(result[i]);
                        final Color previousColor = new Color(result[i - 2]);
                        result[i - 1] = new Color(
                                (currentColor.getRed() + previousColor.getRed()) / 2,
                                (currentColor.getGreen() + previousColor.getGreen()) / 2,
                                (currentColor.getBlue() + previousColor.getBlue()) / 2
                        ).getRGB();
                    }

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

            if (belongingAppWindow instanceof MainWindow) {
                ((MainWindow) belongingAppWindow).getRenderProgressBar().setValue(0);
                ((MainWindow) belongingAppWindow).setStatusText("");
            }

            isRendering.getAndSet(false);
            belongingAppWindow.setResizable(true);

        }).start();

    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
        ((MainWindow) belongingAppWindow).setStatusText("Translate X-axis (" + offsetX + ")");
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
        ((MainWindow) belongingAppWindow).setStatusText("Translate Y-axis (" + offsetY + ")");
    }

    public double getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(double zoomScale) {
        this.zoomScale = zoomScale;
        ((MainWindow) belongingAppWindow).setStatusText("Scale (x" + (int) zoomScale + ")");
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

    public double getPaletteR() {
        return paletteR;
    }

    public void setPaletteR(double paletteR) {
        this.paletteR = paletteR;
    }

    public double getPaletteG() {
        return paletteG;
    }

    public void setPaletteG(double paletteG) {
        this.paletteG = paletteG;
    }

    public double getPaletteB() {
        return paletteB;
    }

    public void setPaletteB(double paletteB) {
        this.paletteB = paletteB;
    }

    public int getDetailScale() {
        return detailScale;
    }

    public void setDetailScale(int detailScale) {
        if (this.detailScale != detailScale) {
            this.offsetX = 0;
            this.offsetY = 0;
            this.zoomScale = 1;
            this.maxIterations = 50;
        }
        this.detailScale = detailScale;
    }

    public boolean getCheckerboard() {
        return checkerboard;
    }

    public void setCheckerboard(boolean checkerboard) {
        if (this.checkerboard != checkerboard) {
            this.offsetX = 0;
            this.offsetY = 0;
            this.zoomScale = 1;
            this.maxIterations = 50;
        }
        this.checkerboard = checkerboard;
    }
}
