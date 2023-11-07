package me.fishydarwin.fractalexplorer.view.window.popup;

import me.fishydarwin.fractalexplorer.Main;
import me.fishydarwin.fractalexplorer.view.window.AppWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class PopupWindow extends AppWindow {

    private static String splitMessageToFit(String message) {
        String[] split = message.split(" ");
        StringBuilder result = new StringBuilder();
        StringBuilder current = new StringBuilder();
        for (String part : split) {
            if (current.length() > 36) {
                result.append("\n").append(current);
                current = new StringBuilder();
            }
            current.append(part).append(" ");
        }
        if (!current.isEmpty()) {
            result.append("\n").append(current);
        }
        String finalResult = result.toString();
        finalResult = finalResult.replaceFirst("\n", "");
        return finalResult;
    }

    public static PopupWindow make(String title, String message) throws IOException {
        PopupWindow window = new PopupWindow(title, splitMessageToFit(message));
        window.setVisible(true);
        EventQueue.invokeLater(window::toFront);
        return window;
    }

    private final String message;

    public PopupWindow(String title, String message) throws IOException {
        super(title);
        this.message = message;

        Main.getAppSetup().appWindowSetup(this);
        initComponents();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(300, 200);
        setMaximumSize(new Dimension(300, 200));
        setPreferredSize(new Dimension(300, 200));
        setMinimumSize(new Dimension(300, 200));
    }

    @Override
    public void initComponents() {

        JPanel textPanel = new JPanel(new GridBagLayout());
        add(textPanel, BorderLayout.CENTER);

        GridBagConstraints textPanelConstraints = new GridBagConstraints();

        JTextArea messageTextArea = new JTextArea(message);
        messageTextArea.setEditable(false);
        textPanelConstraints.gridx = 0;
        textPanelConstraints.gridy = 0;
        textPanelConstraints.gridwidth = 1;
        textPanelConstraints.gridheight = 1;
        textPanel.add(messageTextArea, textPanelConstraints);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        okButton.setMaximumSize(new Dimension(300, 32));
        add(okButton, BorderLayout.PAGE_END);

    }
}
