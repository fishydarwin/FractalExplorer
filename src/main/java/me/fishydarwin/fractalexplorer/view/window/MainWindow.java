package me.fishydarwin.fractalexplorer.view.window;

import com.formdev.flatlaf.util.SystemInfo;
import me.fishydarwin.fractalexplorer.Main;
import me.fishydarwin.fractalexplorer.model.control.KeyboardControlsListener;
import me.fishydarwin.fractalexplorer.utils.FEIOUtils;
import me.fishydarwin.fractalexplorer.view.component.JFractalRenderer;
import org.apache.commons.math3.complex.Complex;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainWindow extends AppWindow {

    private JPanel titleBar;
    public JPanel getTitleBar() {
        return titleBar;
    }

    private JFractalRenderer fractalRenderer;

    public JFractalRenderer getFractalRenderer() {
        return fractalRenderer;
    }

    public MainWindow() throws IOException {
        super("Fractal Explorer");

        // setup window-related things
        Main.getAppSetup().appWindowSetup(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(640, 480));
    }

    @Override
    protected void initComponents() {

        mainPanel.setBackground(Color.blue);

        titleBar = new JPanel();
        titleBar.setLayout(new BoxLayout(titleBar, BoxLayout.LINE_AXIS));
        {
            JLabel title = new JLabel("Fractal Explorer");
            title.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
            title.setForeground(Color.LIGHT_GRAY);
            titleBar.add(title);
        }
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        titleBar.getInsets().set(16, 0, 0, 0);
        titleBar.setBackground(new Color(15, 15, 15));
        mainPanel.add(titleBar, BorderLayout.PAGE_START);

        fractalRenderer = new JFractalRenderer(this);
        mainPanel.add(fractalRenderer);

        addKeyListener(new KeyboardControlsListener(fractalRenderer));

    }

}
