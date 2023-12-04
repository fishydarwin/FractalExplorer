package me.fishydarwin.fractalexplorer.view.component;

import me.fishydarwin.fractalexplorer.model.evaluator.compiler.FEXLCompiler;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.utils.FEMathUtils;
import me.fishydarwin.fractalexplorer.view.clipboard.ClipboardUtil;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;
import me.fishydarwin.fractalexplorer.view.window.popup.PopupWindow;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class JBoundTimeFractalRenderer extends JPanel {

    private final JImagePanel imagePanel;
    private final AppWindow belongingAppWindow;

    public JBoundTimeFractalRenderer(AppWindow belongingAppWindow) {
        super();
        this.belongingAppWindow = belongingAppWindow;

        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 35));

        imagePanel = new JImagePanel();
        imagePanel.setBackground(new Color(42, 42, 42));
        add(imagePanel);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    showImageContextPopup(e);
            }
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    showImageContextPopup(e);
            }
            private void showImageContextPopup(MouseEvent e) {
                JPopupMenu imageRightClickMenu = new JPopupMenu();

                JMenuItem copyToClipboard = new JMenuItem("Copy to Clipboard");
                JMenuItem saveImage = new JMenuItem("Save as Image");

                copyToClipboard.addActionListener(ev -> ClipboardUtil.copy(imagePanel.getImage()));

                saveImage.addActionListener(ev -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save Fractal Image");
                    fileChooser.setFileFilter(
                            new FileNameExtensionFilter("PNG File", "png")
                    );

                    int userSelection = fileChooser.showSaveDialog(belongingAppWindow);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        if (!fileToSave.getName().endsWith(".png")) {
                            fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                        }

                        try {
                            ImageIO.write(imagePanel.getImage(), "png", fileToSave);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                imageRightClickMenu.add(copyToClipboard);
                imageRightClickMenu.add(saveImage);

                imageRightClickMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

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

    private IStatement fexlInput;
    private final Map<Complex, Integer> evaluatedAlready = new ConcurrentHashMap<>();
    private final AtomicBoolean isRendering = new AtomicBoolean(false);
    public boolean isRenderingFractal() { return isRendering.get(); }

    private Exception renderException;
    private final AtomicBoolean threwRenderError = new AtomicBoolean(false);

    private int performIterationOnPoint(int x, int y, double epsilon,
                                        Function<Pair<Complex, Complex>, Pair<Complex, Double>> fcxEval,
                                        int imageWidth, int imageHeight, double imageScaleX,
                                        int halfWidth, int halfHeight) {

        x -= halfWidth;
        y -= halfHeight;

        double xScaled = ((double) x + halfWidth / 2.0 + offsetX * zoomScale)
                / imageWidth * imageScaleX;
        xScaled = (xScaled * 4 - 2) * (1 / zoomScale);

        double yScaled = ((double) y + halfHeight + offsetY * zoomScale)
                / imageHeight;
        yScaled = (yScaled * 4 - 2) * (1 / zoomScale);

        Complex z = new Complex(0, 0);
        Complex c = new Complex(xScaled, yScaled);

        if (evaluatedAlready.containsKey(c))
            return evaluatedAlready.get(c);

        int iterations = 0;
        while (iterations < maxIterations) {

            Pair<Complex, Double> fcxRes;
            fcxRes = fcxEval.apply(new Pair<>(z, c));

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
        return rgb;
    }

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

        AtomicInteger renderThreadsDone = new AtomicInteger(0);
        final int processorCount = Runtime.getRuntime().availableProcessors();
        final int threadPoolSize = processorCount + processorCount / 8;
        final int chunkColumnSize = imageWidth / threadPoolSize;

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

        threwRenderError.getAndSet(false);
        for (int threadId = 0; threadId < threadPoolSize; threadId++) {

            final int fTid = threadId;
            new Thread(() -> {

                // Multi-thread is done on "column chunks"
                final int chunkXBegin = (int) ((float) fTid / threadPoolSize * chunkColumnSize);
                final int chunkXEnd = chunkXBegin + chunkColumnSize > imageWidth ? chunkXBegin : imageWidth;

                /*
                    Mariani-Silver algorithm using stack of (x, y, regionSize)
                    It works (for bound-time fractals) like so:
                        - because a these kinds of fractals are a *boundary* of a set, we can
                          simply draw a box or rectangle somewhere in our picture
                        - we perform normal iterations on the 4 corners and all the edges between
                          the said corners
                        - if all the corners and all the edges have the exact same color, then
                          we can simply color that entire rectangle in the same color
                        - this is great for regions which are mostly black in the set, as a box size
                          of 256 or so will fill that area in only 256 * 4 steps, compared to the
                          previous 256 ^ 2 steps. [[MARIANI ALGORITHM]]
                        - if the said region had edge differences, then whatever was calculated previously
                          stays calculated, so we can leave it as is, and we split the box into 4 tinier boxes
                          (or we don't at all if the box size is 1 pixel) and try to perform the same trick
                          in that box instead [[SILVER ALGORITHM]]

                     We use a stack to imitate the divide-and-conquer structure of this algorithm, normally this
                     is performed in 1 thread recursively through functions, but this makes the code a bit easier
                     to port from previous older code. Performance should be negligibly impacted.
                     (recursive calls use a stack anyway)
                 */
                Stack<Triple<Integer, Integer, Integer>> regionStack = new Stack<>();

                // pre-fill stack
                {
                    final int initialRegionSize = 256;
                    for (int i = chunkXBegin; i < chunkXEnd; i += initialRegionSize) {
                        for (int j = 0; j < imageHeight; j += initialRegionSize) {
                            regionStack.add(Triple.of(i, j, initialRegionSize));
                        }
                    }
                }

                // run stack
                retry:
                while (!regionStack.empty()) {
                    Triple<Integer, Integer, Integer> region = regionStack.pop();
                    final int x = region.getLeft();
                    final int y = region.getMiddle();
                    final int regionSize = region.getRight();
                    final int xSize = x + regionSize < imageWidth ? x + regionSize : imageWidth - 1;
                    final int ySize = y + regionSize < imageHeight ? y + regionSize : imageHeight - 1;

                    Integer regionRgb = null;
                    // top row
                    for (int xi = x; xi < xSize && xi < imageWidth && y < imageHeight; xi++) {
                        int rgb;
                        try {
                            rgb = performIterationOnPoint(xi, y, epsilon,
                                    fcxEval, imageWidth, imageHeight, imageScaleX,
                                    halfWidth, halfHeight);
                        } catch (Exception ex) {
                            renderException = ex;
                            threwRenderError.getAndSet(true);
                            return;
                        }
                        if (threwRenderError.get()) return;

                        imagePanel.getImage().setRGB(xi, y, rgb);
                        if (regionSize > 1) {
                            if (regionRgb == null) regionRgb = rgb;
                            else if (regionRgb != rgb) {
                                final int halfRegionSize = regionSize / 2;
                                regionStack.add(Triple.of(x, y, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y, halfRegionSize));
                                regionStack.add(Triple.of(x, y + halfRegionSize, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y + halfRegionSize, halfRegionSize));
                                continue retry;
                            }
                        }
                    }
                    // bottom row
                    for (int xi = x; xi < xSize && xi < imageWidth && ySize < imageHeight; xi++) {
                        int rgb;
                        try {
                            rgb = performIterationOnPoint(xi, ySize, epsilon,
                                    fcxEval, imageWidth, imageHeight, imageScaleX,
                                    halfWidth, halfHeight);
                        } catch (Exception ex) {
                            renderException = ex;
                            threwRenderError.getAndSet(true);
                            return;
                        }
                        if (threwRenderError.get()) return;

                        imagePanel.getImage().setRGB(xi, ySize, rgb);
                        if (regionSize > 1) {
                            if (regionRgb == null) regionRgb = rgb;
                            else if (regionRgb != rgb) {
                                final int halfRegionSize = regionSize / 2;
                                regionStack.add(Triple.of(x, y, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y, halfRegionSize));
                                regionStack.add(Triple.of(x, y + halfRegionSize, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y + halfRegionSize, halfRegionSize));
                                continue retry;
                            }
                        }
                    }
                    // left column
                    for (int yi = y + 1; yi < ySize && x < imageWidth && yi < imageHeight; yi++) {
                        int rgb;
                        try {
                            rgb = performIterationOnPoint(x, yi, epsilon,
                                    fcxEval, imageWidth, imageHeight, imageScaleX,
                                    halfWidth, halfHeight);
                        } catch (Exception ex) {
                            renderException = ex;
                            threwRenderError.getAndSet(true);
                            return;
                        }
                        if (threwRenderError.get()) return;

                        imagePanel.getImage().setRGB(x, yi, rgb);
                        if (regionSize > 1) {
                            if (regionRgb == null) regionRgb = rgb;
                            else if (regionRgb != rgb) {
                                final int halfRegionSize = regionSize / 2;
                                regionStack.add(Triple.of(x, y, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y, halfRegionSize));
                                regionStack.add(Triple.of(x, y + halfRegionSize, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y + halfRegionSize, halfRegionSize));
                                continue retry;
                            }
                        }
                    }
                    // right column
                    for (int yi = y; yi < ySize && xSize < imageWidth && yi < imageHeight; yi++) {
                        int rgb;
                        try {
                            rgb = performIterationOnPoint(xSize, yi, epsilon,
                                    fcxEval, imageWidth, imageHeight, imageScaleX,
                                    halfWidth, halfHeight);
                        } catch (Exception ex) {
                            renderException = ex;
                            threwRenderError.getAndSet(true);
                            return;
                        }
                        if (threwRenderError.get()) return;

                        imagePanel.getImage().setRGB(xSize, yi, rgb);
                        if (regionSize > 1) {
                            if (regionRgb == null) regionRgb = rgb;
                            else if (regionRgb != rgb) {
                                final int halfRegionSize = regionSize / 2;
                                regionStack.add(Triple.of(x, y, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y, halfRegionSize));
                                regionStack.add(Triple.of(x, y + halfRegionSize, halfRegionSize));
                                regionStack.add(Triple.of(x + halfRegionSize, y + halfRegionSize, halfRegionSize));
                                continue retry;
                            }
                        }
                    }

                    // if you got here, congratulations, it's a region to fill
                    for (int fillX = x + 1; fillX < xSize; fillX++) {
                        for (int fillY = y + 1; fillY < ySize; fillY++) {
                            imagePanel.getImage().setRGB(fillX, fillY, regionRgb);
                        }
                    }
                }

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

                if (threwRenderError.get()) {
                    try {
                        PopupWindow.make("FEXL Runtime Error!",
                                renderException.getClass().getSimpleName() + "\n" +
                                        renderException.getMessage());
                        try {
                            fexlInput = FEXLCompiler.compileFEXL("z = z * z; z = z + c");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            } while (whatIsDoneSoFar < threadPoolSize);

            imagePanel.calculateAndResizeImage();
            imagePanel.repaint();

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
        ((MainWindow) belongingAppWindow).setStatusText("Scale (x" + String.format("%.0f", zoomScale) + ")");
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

}
