package com.guardanis.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import com.guardanis.gtools.ListUtils;
import com.guardanis.gtools.Logger;
import com.guardanis.gtools.ListUtils.Converter;
import com.guardanis.gtools.general.Callback;
import com.guardanis.gtools.gome.Device;

public class DisplayController implements Callback<String> {
	
	private List<String> ipAddresses;
	
	private JFrame frame;
	
	private JTextArea ipAddressesView;
	private JTextArea logView;
	
	private JScrollPane listScroller;
	private JList deviceListView;
	private DefaultListModel deviceListModel = new DefaultListModel();

	private List<Device> devices;
	private List<String> logData = new ArrayList<String>();
	
	public DisplayController(List<String> ipAddresses){
		this.ipAddresses = ipAddresses;
	}
	
	public DisplayController show(List<Device> devices){
		this.devices = devices;
		this.frame = buildFrame();
		this.deviceListView = buildListView();
		
		ipAddressesView = new JTextArea("IP Addresses: \n" 
				+ ListUtils.from(ipAddresses)
				.join("\n"));
		
		ipAddressesView.setMargin(new Insets(10, 12, 10, 12));
		
		frame.add(ipAddressesView, BorderLayout.PAGE_START);
		
		for(Device d : devices)
			deviceListModel.addElement(d);

		this.listScroller = new JScrollPane(deviceListView);
		listScroller.setPreferredSize(new Dimension(frame.getWidth() / 2, frame.getHeight()));
		
		frame.add(listScroller, BorderLayout.LINE_START);
		
		logView = new JTextArea("Log starting...");
		logView.setPreferredSize(new Dimension((frame.getWidth() / 2) - 5, frame.getHeight()));
		logView.setMargin(new Insets(10, 12, 10, 12));
		
		frame.add(logView, BorderLayout.LINE_END);
		
		frame.pack();
		
		Logger.getInstance()
				.registerLogListener(this);
		
		return this;
	}
	
	public void onDeviceAdded(Device device){
		deviceListModel.addElement(device);
		
		frame.revalidate();
	}
	
	public void onDeviceRemoved(Device device){
		deviceListModel.removeElement(device);
		
		frame.revalidate();		
	}
	
	private JFrame buildFrame(){		
		JFrame frame = new JFrame("gome");
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setResizable(false);	    
		
	    frame.getContentPane()
	    	.setLayout(new BorderLayout());
	    
	    Dimension screenSize = Toolkit.getDefaultToolkit()
	    		.getScreenSize();
	    
	    frame.setPreferredSize(new Dimension((int) (screenSize.getWidth() * .35),
	    		(int) (screenSize.getHeight() / 3)));
	    
	    frame.pack();
	    
	    frame.setVisible(true);
	    
	    return frame;
	}
	
	private JList buildListView(){		
		JList list = new JList(deviceListModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		
		return list;
	}

	@Override
	public void onCalled(String value) {
		java.awt.EventQueue.invokeLater(() -> {
			logData.add(value);
			
			while(10 < logData.size())
				logData.remove(logData.size() - 1);
			
			logView.setText(ListUtils.from(logData)
					.join("\n"));
		});
	}
}
