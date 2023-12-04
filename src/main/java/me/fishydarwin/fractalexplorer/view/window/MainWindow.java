package me.fishydarwin.fractalexplorer.view.window;

import me.fishydarwin.fractalexplorer.Main;
import me.fishydarwin.fractalexplorer.model.evaluator.statement.IStatement;
import me.fishydarwin.fractalexplorer.view.control.BoundTimeKeyboardControlsListener;
import me.fishydarwin.fractalexplorer.model.evaluator.compiler.FEXLCompiler;
import me.fishydarwin.fractalexplorer.view.component.JBoundTimeFractalRenderer;
import me.fishydarwin.fractalexplorer.view.window.popup.PopupWindow;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainWindow extends AppWindow {

    private JPanel titleBar;
    public JPanel getTitleBar() {
        return titleBar;
    }

    private JBoundTimeFractalRenderer fractalRenderer;

    public JBoundTimeFractalRenderer getFractalRenderer() {
        return fractalRenderer;
    }

    private JProgressBar renderProgressBar;
    public JProgressBar getRenderProgressBar() { return renderProgressBar; }

    private JLabel statusText;
    public void setStatusText(String newText) { statusText.setText(newText); }

    public MainWindow() throws IOException {
        super("Fractal Explorer");

        // setup window-related things
        Main.getAppSetup().appWindowSetup(this);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setPreferredSize(new Dimension(1280, 720));
        setMinimumSize(new Dimension(640, 480));
    }

    private BoundTimeSettingsWindow boundTimeSettingsWindow;

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
        {
            statusText = new JLabel("Status");
            statusText.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
            statusText.setForeground(Color.LIGHT_GRAY);
            statusText.setBorder(BorderFactory.createEmptyBorder(2, 8, 0, 0));
            titleBar.add(statusText);
        }
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        titleBar.getInsets().set(16, 0, 0, 0);
        titleBar.setBackground(new Color(15, 15, 15));
        mainPanel.add(titleBar, BorderLayout.PAGE_START);

        fractalRenderer = new JBoundTimeFractalRenderer(this);
        mainPanel.add(fractalRenderer, BorderLayout.CENTER);

        renderProgressBar = new JProgressBar();
        renderProgressBar.setPreferredSize(new Dimension(99999, 8));
        renderProgressBar.setForeground(new Color(160, 160, 160));
        renderProgressBar.setBackground(new Color(25, 25, 25));
        mainPanel.add(renderProgressBar, BorderLayout.PAGE_END);

        addKeyListener(new BoundTimeKeyboardControlsListener(fractalRenderer));

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

            JMenu editMenu = new JMenu("Edit");
            JMenuItem fractalSettings = new JMenuItem("Fractal Settings");
            fractalSettings.addActionListener((e) -> {
                try {
                    if (boundTimeSettingsWindow != null)
                        boundTimeSettingsWindow.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                    boundTimeSettingsWindow = new BoundTimeSettingsWindow(this);
                    boundTimeSettingsWindow.setVisible(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            editMenu.add(fractalSettings);
            menuBar.add(editMenu);
        }
        setJMenuBar(menuBar);

    }

    public void setFexlInput(String fexlInput) {
        IStatement compilationResult;
        try {
            compilationResult = FEXLCompiler.compileFEXL(fexlInput);
        } catch (Exception ex) {
            try {
                PopupWindow.make("FEXL Compilation Error!", ex.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        fractalRenderer.setFexlInput(compilationResult);
        fractalRenderer.setOffsetX(0);
        fractalRenderer.setOffsetY(0);
        fractalRenderer.setZoomScale(1);
        fractalRenderer.render(true);
    }

}
