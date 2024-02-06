package me.fishydarwin.fractalexplorer.model.os;

import me.fishydarwin.fractalexplorer.view.component.JImagePanel;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;

import javax.swing.*;

public class MacOSAppSetup extends OSAppSetup {

    @Override
    public void setupSystemProperties() {
        System.setProperty("apple.awt.application.name", "Fractal Explorer");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        JImagePanel.dpiScale = 2; //TODO: this is not the right way to calculate this
    }

    @Override
    public void appWindowSetup(AppWindow window) {

        window.getRootPane().putClientProperty("apple.awt.fullWindowContent", true);
        window.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        window.getRootPane().putClientProperty("apple.awt.windowTitleVisible", false);
        window.getRootPane().putClientProperty("apple.awt.fullscreenable", false);

        if (window instanceof MainWindow) {
            ((MainWindow) window).getTitleBar().add(Box.createHorizontalStrut(70), 0);
        }

    }

}
