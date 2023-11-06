package me.fishydarwin.fractalexplorer;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import me.fishydarwin.fractalexplorer.model.os.GenericOSAppSetup;
import me.fishydarwin.fractalexplorer.model.os.MacOSAppSetup;
import me.fishydarwin.fractalexplorer.model.os.OSAppSetup;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;

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

            // show something initially
            window.setFexlInput(
                    """
                    // Burning Ship: like Mandelbrot but you ABS the za and zb components.

                    // grab za, zb...
                    abs_za = RE[z];
                    abs_zb = IM[z];

                    // ABS them both
                    abs_za = ABS[abs_za];
                    abs_zb = ABS[abs_zb];

                    // redefine the z number based on the new numbers
                    z = complex: abs_za, abs_zb;

                    // perform classic Mandelbrot
                    z = z * z;
                    z = z + c;
                    """
            );

        });

    }

}