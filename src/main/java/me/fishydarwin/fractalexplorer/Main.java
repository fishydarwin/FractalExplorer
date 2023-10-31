package me.fishydarwin.fractalexplorer;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import me.fishydarwin.fractalexplorer.model.os.GenericOSAppSetup;
import me.fishydarwin.fractalexplorer.model.os.MacOSAppSetup;
import me.fishydarwin.fractalexplorer.model.os.OSAppSetup;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;
import org.apache.commons.math3.complex.Complex;

import javax.swing.*;
import java.io.IOException;

public class Main {

    private static OSAppSetup appSetup;
    public static OSAppSetup getAppSetup() {
        return appSetup;
    }

    public static void main(String[] args) {

        System.out.println("Running Fractal Explorer on " + System.getProperty("os.name"));
        // initialize OS System Properties
        appSetup = new GenericOSAppSetup();
        if (SystemInfo.isMacOS) {
            appSetup = new MacOSAppSetup();
        }
        appSetup.setupSystemProperties();

        // then after Sys Properties
        SwingUtilities.invokeLater(() -> {

            // setup theme
            FlatLightLaf.setup();
            // setup other things too
            appSetup.setupApp();

            // choose theming
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }

            // init main window
            MainWindow window = null;
            try {
                window = new MainWindow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            window.setVisible(true);

            // show Mandelbrot initially
            // TODO: should be moved from here
            MainWindow finalWindow = window;
            new Thread(() -> finalWindow.getFractalRenderer().render((zcPair -> {
                Complex z = zcPair.getFirst();
                Complex c = zcPair.getSecond();
                return z.multiply(z).add(c);
            }), true)).start();

        });

    }

}