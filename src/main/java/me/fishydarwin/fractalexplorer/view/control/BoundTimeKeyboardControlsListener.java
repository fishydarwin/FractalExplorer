package me.fishydarwin.fractalexplorer.view.control;

import me.fishydarwin.fractalexplorer.view.component.JBoundTimeFractalRenderer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BoundTimeKeyboardControlsListener implements KeyListener {

    private final JBoundTimeFractalRenderer boundRenderer;

    private boolean isHoldingShift = false;
    private int largerMove() {
        return isHoldingShift ? 4 : 1;
    }

    public BoundTimeKeyboardControlsListener(JBoundTimeFractalRenderer boundRenderer) {
        this.boundRenderer = boundRenderer;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (boundRenderer.isRenderingFractal()) return;
        boolean render = true;
        boolean recalc = false;
        switch (e.getKeyCode()) {
            default -> render = false;
            case KeyEvent.VK_SHIFT -> {
                isHoldingShift = true;
                render = false;
            }
            case KeyEvent.VK_LEFT -> boundRenderer.setOffsetX(boundRenderer.getOffsetX()
                    - 20 * largerMove() / boundRenderer.getZoomScale());
            case KeyEvent.VK_RIGHT -> boundRenderer.setOffsetX(boundRenderer.getOffsetX()
                    + 20 * largerMove() / boundRenderer.getZoomScale());
            case KeyEvent.VK_UP -> boundRenderer.setOffsetY(boundRenderer.getOffsetY()
                    - 20 * largerMove() / boundRenderer.getZoomScale());
            case KeyEvent.VK_DOWN -> boundRenderer.setOffsetY(boundRenderer.getOffsetY()
                    + 20* largerMove() / boundRenderer.getZoomScale());
            case KeyEvent.VK_Z -> boundRenderer.setZoomScale(boundRenderer.getZoomScale() * 2);
            case KeyEvent.VK_X -> boundRenderer.setZoomScale(boundRenderer.getZoomScale() / 2);
            case KeyEvent.VK_I -> {
                boundRenderer.setMaxIterations(boundRenderer.getMaxIterations()
                        + boundRenderer.getMaxIterations() / 4);
                recalc = true;
            }
            case KeyEvent.VK_O -> {
                boundRenderer.setMaxIterations(boundRenderer.getMaxIterations()
                        - boundRenderer.getMaxIterations() / 4);
                recalc = true;
            }
        }
        if (render) {
            boundRenderer.reRender(recalc);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            isHoldingShift = false;
        }
    }

}
