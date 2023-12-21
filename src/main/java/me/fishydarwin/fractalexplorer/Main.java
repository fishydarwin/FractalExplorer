package me.fishydarwin.fractalexplorer;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import me.fishydarwin.fractalexplorer.model.os.GenericOSAppSetup;
import me.fishydarwin.fractalexplorer.model.os.LinuxOSAppSetup;
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
        if (SystemInfo.isLinux) {
            appSetup = new LinuxOSAppSetup();
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
            MainWindow window;
            try {
                window = new MainWindow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            window.setVisible(true);

            // show something initially
            window.setFexlInput("z = z * z; z = z + c;");

        });

    }

}