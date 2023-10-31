package me.fishydarwin.fractalexplorer.view.window;

import me.fishydarwin.fractalexplorer.utils.FEIOUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public abstract class AppWindow extends JFrame {

    protected JPanel mainPanel;

    public AppWindow(String title) throws IOException {
        super(title);
        setIconImage(FEIOUtils.getIconImage());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        initComponents();
        add(mainPanel);
    }

    protected void initComponents() {}
}
