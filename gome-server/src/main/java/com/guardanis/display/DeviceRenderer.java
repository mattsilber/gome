package com.guardanis.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import com.guardanis.gtools.gome.Device;
import com.guardanis.gtools.views.fonts.FontHelper;

public class DeviceRenderer extends JPanel implements ListCellRenderer {

	private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	private JTextArea nameLabel = new JTextArea();
	private JTextArea ipLabel = new JTextArea();

	public DeviceRenderer() {
		this.setLayout(new BorderLayout());
		
		nameLabel.setOpaque(true);
		nameLabel.setMargin(new Insets(10, 12, 2, 12));

		nameLabel.setFont(FontHelper.getInstance()
				.get(FontHelper.FONT_ROBOTO, 14));
		
		add(nameLabel, BorderLayout.PAGE_START);
		
		ipLabel.setOpaque(true);
		ipLabel.setMargin(new Insets(0, 12, 10, 12));

		ipLabel.setFont(FontHelper.getInstance()
				.get(FontHelper.FONT_ROBOTO, 10));
		
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
