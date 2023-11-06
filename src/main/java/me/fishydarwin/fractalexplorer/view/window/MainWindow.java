package me.fishydarwin.fractalexplorer.view.window;

import me.fishydarwin.fractalexplorer.Main;
import me.fishydarwin.fractalexplorer.view.control.KeyboardControlsListener;
import me.fishydarwin.fractalexplorer.model.evaluator.compiler.FEXLCompiler;
import me.fishydarwin.fractalexplorer.view.component.JFractalRenderer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    private JProgressBar renderProgressBar;
    public JProgressBar getRenderProgressBar() { return renderProgressBar; }

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
        mainPanel.add(fractalRenderer, BorderLayout.CENTER);

        renderProgressBar = new JProgressBar();
        renderProgressBar.setPreferredSize(new Dimension(99999, 8));
        renderProgressBar.setForeground(new Color(160, 160, 160));
        renderProgressBar.setBackground(new Color(25, 25, 25));
        mainPanel.add(renderProgressBar, BorderLayout.PAGE_END);

        addKeyListener(new KeyboardControlsListener(fractalRenderer));

        JMenuBar menuBar = new JMenuBar();
        {
            JMenu fileMenu = new JMenu("File");

            JMenuItem runFEXL = new JMenuItem("Open FEXL Script");
            runFEXL.addActionListener(e -> {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Open FEXL Script");
                fileChooser.setFileFilter(
                        new FileNameExtensionFilter("Fractal Explorer Language", "fexl")
                );

                int userSelection = fileChooser.showOpenDialog(this);

                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToRead = fileChooser.getSelectedFile();
                    if (!fileToRead.getName().endsWith(".fexl")) {
                        return;
                    }
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(fileToRead));
                        StringBuilder input = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            input.append(line).append("\n");
                        }
                        setFexlInput(input.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            });
            fileMenu.add(runFEXL);

            menuBar.add(fileMenu);
        }
        setJMenuBar(menuBar);

    }

    public void setFexlInput(String fexlInput) {
        fractalRenderer.setFexlInput(FEXLCompiler.compileFEXL(fexlInput));
        fractalRenderer.setOffsetX(0);
        fractalRenderer.setOffsetY(0);
        fractalRenderer.setZoomScale(1);
        fractalRenderer.render(true);
    }

}
