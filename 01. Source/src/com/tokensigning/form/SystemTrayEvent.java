package com.tokensigning.form;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.LOG;
import com.tokensigning.utils.LanguageOption;
import com.tokensigning.utils.Utils;

public class SystemTrayEvent {
	//private static final String APP_VERSION = "1.0.0.8";
	//private static final String TITLE = "Thông báo";
	
	private static String ICON = "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";
	
	// start of main method
	static SystemTray systemTray;
	public static void Show() {
		if (!SystemTray.isSupported()) {
			System.out.println("System tray is not supported !!! ");
			LOG.write("systemTray","System tray is not supported !!! ");
			return;
		}
		systemTray = SystemTray.getSystemTray();
		BufferedImage img = null;
		try {
			img = ImageIO.read(new ByteArrayInputStream(Base64Utils.base64Decode(ICON)));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// popupmenu
		PopupMenu trayPopupMenu = new PopupMenu();

		// 1t menuitem for popupmenu
		final JFrame frame = new JFrame("TokenSigning");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon iconInfom = new ImageIcon(Base64Utils.base64Decode(PINVerification.ICON_INFO));
		MenuItem action = new MenuItem(LanguageOption.SYSTEM_TRAY_MENU_INFO);
		action.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, LanguageOption.SYSTEM_TRAY_VERSION + " " + Utils.getAppVersion() + ".",
						LanguageOption.COMMON_DIALOG_TITLE, 
						JOptionPane.INFORMATION_MESSAGE, iconInfom);
			}
		});
		trayPopupMenu.add(action);
		
		MenuItem language = new MenuItem(LanguageOption.SYSTEM_TRAY_MENU_LANGUAGE);
		language.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String langConfig = LanguageForm.show();
				if (langConfig != null)
				{
					Utils.setValueToConfig("language", langConfig);
					new LanguageOption(langConfig);
					changeLanguage();
				}
			}
		});
		trayPopupMenu.add(language);
		
		// 3nd menuitem of popupmenu
		MenuItem close = new MenuItem(LanguageOption.SYSTEM_TRAY_MENU_CLOSE);
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frame, LanguageOption.SYSTEM_TRAY_WARNING_CLOSE, 
						LanguageOption.COMMON_DIALOG_TITLE, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, iconInfom);
	    		if (result == JOptionPane.OK_OPTION) {
	    			System.exit(0);
	    		}
			}
		});
		trayPopupMenu.add(close);
		
		TrayIcon trayIcon = new TrayIcon((Image)img, "TokenSigning", trayPopupMenu);
		trayIcon.setImageAutoSize(true);

		try {			
			LOG.write("Show", "Setup show message");
			systemTray.add(trayIcon);
			Thread t1 = new Thread(){
    			public void run() {
    				try {
    					trayIcon.displayMessage(LanguageOption.COMMON_DIALOG_TITLE,
    							"TokenSigning " + Utils.getAppVersion() + " " + LanguageOption.SYSTEM_TRAY_RUNNING, TrayIcon.MessageType.INFO); 
    					
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    		};  
    		t1.start();
		} catch (AWTException awtException) {
			awtException.printStackTrace();
		}

	}// end of main

	public static void changeLanguage()
	{
		PopupMenu popMenu = systemTray.getTrayIcons()[0].getPopupMenu();
		popMenu.getItem(0).setLabel(LanguageOption.SYSTEM_TRAY_MENU_INFO);
		popMenu.getItem(1).setLabel(LanguageOption.SYSTEM_TRAY_MENU_LANGUAGE);
		popMenu.getItem(2).setLabel(LanguageOption.SYSTEM_TRAY_MENU_CLOSE);
	}
	
}// end of class