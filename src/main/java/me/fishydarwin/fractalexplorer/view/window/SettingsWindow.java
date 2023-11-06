package me.fishydarwin.fractalexplorer.view.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class SettingsWindow extends AppWindow {

    private final MainWindow caller;

    public SettingsWindow(MainWindow caller) throws IOException {
        super("Fractal Settings");
        this.caller = caller;

        initComponents();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 300);
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
        settingsPanel.add(redSliderLabel, settingsPanelConstraints);
        JSlider redSlider = new JSlider(0, 100,
                (int) ((caller.getFractalRenderer().getPaletteR() - 1) * 100));
        redSlider.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 1;
        settingsPanel.add(redSlider, settingsPanelConstraints);

        JLabel greenSliderLabel = new JLabel("Green Color Offset");
        greenSliderLabel.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 2;
        settingsPanel.add(greenSliderLabel, settingsPanelConstraints);
        JSlider greenSlider = new JSlider(0, 100,
                (int) ((caller.getFractalRenderer().getPaletteG() - 1) * 100));
        greenSlider.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 3;
        settingsPanel.add(greenSlider, settingsPanelConstraints);

        JLabel blueSliderLabel = new JLabel("Blue Color Offset");
        blueSliderLabel.setPreferredSize(new Dimension(250, 16));
        settingsPanelConstraints.gridy = 4;
        settingsPanel.add(blueSliderLabel, settingsPanelConstraints);
        JSlider blueSlider = new JSlider(0, 100,
                (int) ((caller.getFractalRenderer().getPaletteB() - 1) * 100));
        settingsPanelConstraints.gridy = 5;
        blueSlider.setPreferredSize(new Dimension(250, 16));
        settingsPanel.add(blueSlider, settingsPanelConstraints);

        useLowDetailChecked = caller.getFractalRenderer().getDetailScale() == 2;
        JCheckBox useLowDetail = new JCheckBox("Use half-resolution (faster)", useLowDetailChecked);
        useLowDetail.addItemListener(e -> useLowDetailChecked = e.getStateChange() == ItemEvent.SELECTED);
        settingsPanelConstraints.gridy = 6;
        settingsPanel.add(useLowDetail, settingsPanelConstraints);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> {
            caller.getFractalRenderer().setPaletteR(redSlider.getValue() / 100.0 + 1);
            caller.getFractalRenderer().setPaletteG(greenSlider.getValue() / 100.0 + 1);
            caller.getFractalRenderer().setPaletteB(blueSlider.getValue() / 100.0 + 1);

            caller.getFractalRenderer().setDetailScale(useLowDetailChecked ? 2 : 1);

            caller.getFractalRenderer().render(true);
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        okButton.setMaximumSize(new Dimension(300, 32));
        add(okButton, BorderLayout.PAGE_END);

    }

}
