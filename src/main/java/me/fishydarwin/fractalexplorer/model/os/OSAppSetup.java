package me.fishydarwin.fractalexplorer.model.os;

import me.fishydarwin.fractalexplorer.utils.FEIOUtils;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;
import me.fishydarwin.fractalexplorer.view.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public abstract class OSAppSetup {

    public void setupSystemProperties() {}

    public void setupApp() {
        Desktop desktop = Desktop.getDesktop();
        if(desktop.isSupported(Desktop.Action.APP_ABOUT )) {
            desktop.setAboutHandler( e -> {
                JFrame about = new JFrame("About Fractal Explorer");
                about.setSize(480, 200);

                JPanel aboutPanel = new JPanel();
                aboutPanel.setLayout(new GridBagLayout());
                GridBagConstraints aboutGbc = new GridBagConstraints();
                about.add(aboutPanel);

                JLabel header = new JLabel("Fractal Explorer");
                header.setFont(new Font(Font.DIALOG, Font.BOLD, 18));
                try {
                    header.setIcon(new ImageIcon(FEIOUtils.getAboutIconImage()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                aboutGbc.gridx = 0;
                aboutGbc.gridy = 0;
                aboutGbc.gridwidth = 1;
                aboutGbc.gridheight = 1;
                aboutPanel.add(header, aboutGbc);
                JLabel description = new JLabel("Written in Java 17 by Bozga Rareș Ionuț.");
                aboutGbc.gridx = 0;
                aboutGbc.gridy = 1;
                aboutGbc.gridwidth = 1;
                aboutGbc.gridheight = 1;
                aboutPanel.add(description, aboutGbc);

                about.setVisible(true);
                about.setResizable(false);
                about.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            } );
        }
    }

    public void appWindowSetup(AppWindow window) {
        try {
            window.setIconImage(FEIOUtils.getIconImage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (window instanceof MainWindow) {
            ((MainWindow) window).getTitleBar().add(Box.createHorizontalStrut(8), 0);
        }
    }

}
