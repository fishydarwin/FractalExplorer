package me.fishydarwin.fractalexplorer.view.window;

import me.fishydarwin.fractalexplorer.Main;
import me.fishydarwin.fractalexplorer.view.component.JImagePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class BoundTimeSettingsWindow extends AppWindow {

    private final MainWindow caller;

    public BoundTimeSettingsWindow(MainWindow caller) throws IOException {
        super("Fractal Settings");
        this.caller = caller;

        initComponents();
        Main.getAppSetup().appWindowSetup(this);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(300, 300);
        setMaximumSize(new Dimension(300, 300));
        setPreferredSize(new Dimension(300, 300));
        setMinimumSize(new Dimension(300, 300));
    }

    private boolean useLowDetailChecked = false;

    @Override
    public void initComponents() {
        if (caller == null) return;

        JPanel settingsPanel = new JPanel(new GridBagLayout());
        add(settingsPanel, BorderLayout.CENTER);

        GridBagConstraints settingsPanelConstraints = new GridBagConstraints();

        JLabel redSliderLabel = new JLabel("Red Color Offset");
        redSliderLabel.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridx = 0;
        settingsPanelConstraints.gridy = 0;
        settingsPanelConstraints.gridheight = 1;
        settingsPanelConstraints.gridwidth = 3;
        settingsPanelConstraints.ipady = 10;
        settingsPanel.add(redSliderLabel, settingsPanelConstraints);
        JSlider redSlider = new JSlider(0, 100,
                (int) ((caller.getFractalRenderer().getPaletteR() - 1) * 100));
        redSliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        redSlider.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 1;
        settingsPanel.add(redSlider, settingsPanelConstraints);

        JLabel greenSliderLabel = new JLabel("Green Color Offset");
        greenSliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greenSliderLabel.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 2;
        settingsPanel.add(greenSliderLabel, settingsPanelConstraints);
        JSlider greenSlider = new JSlider(0, 100,
                (int) ((caller.getFractalRenderer().getPaletteG() - 1) * 100));
        greenSlider.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 3;
        settingsPanel.add(greenSlider, settingsPanelConstraints);

        JLabel blueSliderLabel = new JLabel("Blue Color Offset");
        blueSliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        blueSliderLabel.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 4;
        settingsPanel.add(blueSliderLabel, settingsPanelConstraints);
        JSlider blueSlider = new JSlider(0, 100,
                (int) ((caller.getFractalRenderer().getPaletteB() - 1) * 100));
        settingsPanelConstraints.gridy = 5;
        blueSlider.setPreferredSize(new Dimension(250, 16));
        settingsPanel.add(blueSlider, settingsPanelConstraints);

        JLabel renderScaleSliderLabel = new JLabel("Render Scale");
        renderScaleSliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        renderScaleSliderLabel.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 6;
        settingsPanel.add(renderScaleSliderLabel, settingsPanelConstraints);
        JSlider renderScaleSlider = new JSlider(1, 8, (int) (JImagePanel.dpiScale * 2));
        renderScaleSlider.setSnapToTicks(true);
        renderScaleSlider.setPaintLabels(true);
        renderScaleSlider.setPreferredSize(new Dimension(125, 16));
        settingsPanelConstraints.gridy = 7;
        settingsPanel.add(renderScaleSlider, settingsPanelConstraints);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> {
            caller.getFractalRenderer().setPaletteR(redSlider.getValue() / 100.0 + 1);
            caller.getFractalRenderer().setPaletteG(greenSlider.getValue() / 100.0 + 1);
            caller.getFractalRenderer().setPaletteB(blueSlider.getValue() / 100.0 + 1);

            JImagePanel.dpiScale = renderScaleSlider.getValue() / 2.0;

            caller.getFractalRenderer().render(true);
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        okButton.setMaximumSize(new Dimension(300, 32));
        add(okButton, BorderLayout.PAGE_END);

    }

}
