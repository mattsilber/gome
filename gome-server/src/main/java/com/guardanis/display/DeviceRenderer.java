package com.guardanis.display;

import com.guardanis.Device;

import javax.swing.*;
import java.awt.*;

public class DeviceRenderer extends JPanel implements ListCellRenderer {

    private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

    private JTextArea nameLabel = new JTextArea();
    private JTextArea ipLabel = new JTextArea();

    public DeviceRenderer() {
        this.setLayout(new BorderLayout());

        nameLabel.setOpaque(true);
        nameLabel.setMargin(new Insets(10, 12, 2, 12));

        add(nameLabel, BorderLayout.PAGE_START);

        ipLabel.setOpaque(true);
        ipLabel.setMargin(new Insets(0, 12, 10, 12));

        add(ipLabel, BorderLayout.PAGE_END);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Device device = (Device) value;
        nameLabel.setText(device.getName());
        ipLabel.setText(device.getIpAddress());

        if (isSelected) {
            setBackground(HIGHLIGHT_COLOR);
            setForeground(Color.white);
        }
        else {
            setBackground(Color.white);
            setForeground(Color.black);
        }

        return this;
    }
}
