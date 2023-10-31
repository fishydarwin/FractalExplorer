package me.fishydarwin.fractalexplorer.model.control;

import me.fishydarwin.fractalexplorer.view.component.JFractalRenderer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardControlsListener implements KeyListener {

    private final JFractalRenderer boundRenderer;

    public KeyboardControlsListener(JFractalRenderer boundRenderer) {
        this.boundRenderer = boundRenderer;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        boolean render = true;
        boolean recalc = false;
        switch (e.getKeyCode()) {
            default -> render = false;
            case KeyEvent.VK_LEFT -> boundRenderer.setOffsetX(boundRenderer.getOffsetX() - 10);
            case KeyEvent.VK_RIGHT -> boundRenderer.setOffsetX(boundRenderer.getOffsetX() + 10);
            case KeyEvent.VK_UP -> boundRenderer.setOffsetY(boundRenderer.getOffsetY() - 10);
            case KeyEvent.VK_DOWN -> boundRenderer.setOffsetY(boundRenderer.getOffsetY() + 10);
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
    public void keyReleased(KeyEvent e) {}

}
