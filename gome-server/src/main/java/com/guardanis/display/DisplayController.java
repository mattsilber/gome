package com.guardanis.display;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import com.guardanis.gtools.ListUtils;
import com.guardanis.gtools.ListUtils.Converter;
import com.guardanis.gtools.gome.Device;

public class DisplayController {
	
	private JFrame frame;
	private JScrollPane listScroller;
	private JList deviceListView;
	private DefaultListModel deviceListModel = new DefaultListModel();

	private List<Device> devices;
	
	public DisplayController show(List<Device> devices){
		this.devices = devices;
		this.frame = buildFrame();
		this.deviceListView = buildListView();
		
		for(Device d : devices)
			deviceListModel.addElement(d);

		this.listScroller = new JScrollPane(deviceListView);
		listScroller.setPreferredSize(new Dimension(frame.getWidth() / 3, frame.getHeight()));
		
		frame.add(listScroller);
		frame.pack();
		
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
	    	.setLayout(new FlowLayout());
	    
	    Dimension screenSize = Toolkit.getDefaultToolkit()
	    		.getScreenSize();
	    
	    frame.setPreferredSize(new Dimension((int) (screenSize.getWidth() * .75),
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
}
