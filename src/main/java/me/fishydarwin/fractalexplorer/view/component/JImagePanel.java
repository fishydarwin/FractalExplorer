package me.fishydarwin.fractalexplorer.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class JImagePanel extends JPanel {

    private BufferedImage image;
    private BufferedImage scaledImage;

    public JImagePanel(BufferedImage image) {
        super();
        setLayout(new FlowLayout());
        this.image = image;
        assignResizeEvent();
    }

    public JImagePanel(int initialWidth, int initialHeight) {
        this(new BufferedImage(initialWidth, initialHeight, BufferedImage.TYPE_INT_RGB));
    }

    public JImagePanel() {
        this(64, 64);
    }

    public BufferedImage getImage() { return image; }

    public void setImage(BufferedImage newImage) {
        image = newImage;
    }

    public void directDrawNoRecalculate(int x, int y, Color color) {
        image.setRGB(x, y, color.getRGB());
    }

    public void calculateAndResizeImage() {
        int calculatedWidth = (int) (image.getWidth() * ((float) this.getHeight() / image.getHeight()));
        resizeImage(calculatedWidth, this.getHeight());
    }

    public void repaintScaled() {
        calculateAndResizeImage();
        repaint();
    }

    public void directDraw(int x, int y, Color color) {
        directDrawNoRecalculate(x, y, color);
        calculateAndResizeImage();
    }

    public void setBuffer(int[] buffer) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                image.setRGB(i, j, buffer[i * image.getWidth() + j]);
            }
        }
    }

    private void assignResizeEvent() {
        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                int calculatedWidth =
                        (int) (image.getWidth()
                                * ((float) e.getComponent().getHeight() / image.getHeight()));
                resizeImage(calculatedWidth, e.getComponent().getHeight());
            }
            public void componentHidden(ComponentEvent e) {}

            public void componentMoved(ComponentEvent e) {}

            public void componentShown(ComponentEvent e) {}
        });

    }

    private void resizeImage(int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, 0, 0, newWidth, newHeight,
                0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
        scaledImage = resized;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(scaledImage,
                (this.getWidth() - scaledImage.getWidth()) / 2,
                (this.getHeight() - scaledImage.getHeight()) / 2,
                this);
        g.dispose();
    }

}
