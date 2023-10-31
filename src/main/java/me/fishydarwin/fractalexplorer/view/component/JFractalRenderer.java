package me.fishydarwin.fractalexplorer.view.component;

import me.fishydarwin.fractalexplorer.utils.FEMathUtils;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    }

    private int maxIterations = 100;

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    private int offsetX = 0;
    private int offsetY = 0;

    private double zoomScale = 1;

    private Function<Pair<Complex, Complex>, Complex> fcx;
    private final Map<Double, Map<Double, Integer>> evaluatedAlready = new ConcurrentHashMap<>(1000);
    public void render(Function<Pair<Complex, Complex>, Complex> newFcx, boolean reEvaluate) {
        fcx = newFcx;
        if (reEvaluate) evaluatedAlready.clear();

        belongingAppWindow.setResizable(false);
        imagePanel.setImage(new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB));
        int imageWidth = imagePanel.getImage().getWidth();
        int imageHeight = imagePanel.getImage().getHeight();

        double imageScaleX = ((double) getWidth()) / getHeight();
        int halfWidth = imageWidth / 2;
        int halfHeight = imageHeight / 2;
        double epsilon = 0.005 * (1 / zoomScale);

        int minBufferSize = Math.max(imageWidth, imageHeight);
        final int[] result = new int[minBufferSize * minBufferSize];
        for (int i = 0; i < result.length; i++) result[i] = i;

        Arrays.stream(result).parallel().forEach((i) -> {
            int x = i / imageWidth - halfWidth;
            int y = i % imageWidth - halfHeight;

            double xScaled = ((double) x + halfWidth / 2.0 + offsetX * zoomScale) / imageWidth * imageScaleX;
            xScaled = (xScaled * 4 - 2) * (1 / zoomScale);
            double yScaled = ((double) y + halfHeight + offsetY * zoomScale) / imageHeight;
            yScaled = (yScaled * 4 - 2) * (1 / zoomScale);

            Complex z = new Complex(0, 0);
            Complex c = new Complex(xScaled, yScaled);

            if (evaluatedAlready.containsKey(xScaled)) {
                if (evaluatedAlready.get(xScaled).containsKey(yScaled)) {
                    result[i] = evaluatedAlready.get(xScaled).get(yScaled);
                    return;
                }
            } else {
                evaluatedAlready.put(xScaled, new HashMap<>());
            }

            int iterations = 0;
            while (iterations < maxIterations) {
                z = fcx.apply(new Pair<>(z, c));

                double abs = z.abs();
                if (abs < epsilon) break;
                if (abs > 2) break;

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
            evaluatedAlready.get(xScaled).put(yScaled, rgb);
            result[i] = rgb;
        });

        imagePanel.setBuffer(result);

        imagePanel.calculateAndResizeImage();
        imagePanel.repaint();
        belongingAppWindow.setResizable(true);

    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public double getZoomScale() {
        return zoomScale;
    }

    public void setZoomScale(double zoomScale) {
        this.zoomScale = zoomScale;
    }

    public void reRender(boolean reEvaluate) {
        render(fcx, reEvaluate);
    }
}
