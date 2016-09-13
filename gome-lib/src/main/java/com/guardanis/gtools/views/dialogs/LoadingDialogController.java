package com.guardanis.gtools.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.guardanis.gtools.views.fonts.FontHelper;

public class LoadingDialogController {
	
	private JDialog dialog;
	private JLabel messageLabel;

	public void show(JFrame frame){
		show(frame, "Please wait...");
	}

	public void show(JFrame frame, String message){
		show(frame, message, () -> { });
	}
	
	public void show(JFrame frame, String message, Runnable runBeforeShowing){
		dismiss();
		
		dialog = new JDialog(frame);
		
		messageLabel = new JLabel(message, SwingConstants.CENTER);
		
	    messageLabel.setFont(FontHelper.getInstance()
	    		.get(FontHelper.FONT_ROBOTO, 14));
			    
	    dialog.setUndecorated(true);
	    dialog.setPreferredSize(new Dimension(350, 100));
	    dialog.getContentPane().setLayout(new BorderLayout());
	    dialog.getContentPane().add(messageLabel, BorderLayout.CENTER);
	    dialog.pack();
	    dialog.setLocationRelativeTo(frame);
	    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    dialog.setModal(true);
	    
	    if(dialog.isAlwaysOnTopSupported())
	    	dialog.setAlwaysOnTop(true);
	    
	    if(runBeforeShowing != null)
	    	runBeforeShowing.run();
	    
	    dialog.setVisible(true);
	}
	
	public void setText(String text){
		try{
			messageLabel.setText(text);
			messageLabel.invalidate();
		}
		catch(Throwable e){ e.printStackTrace(); }
	}
	
	public void dismiss(){
		try{
			if(dialog != null)
				dialog.dispose();
		}
		catch(Throwable e){ e.printStackTrace(); }
		
		dialog = null;
	}

}