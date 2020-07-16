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
import com.tokensigning.common.IconLoader;
import com.tokensigning.common.LOG;
import com.tokensigning.utils.LanguageOption;
import com.tokensigning.utils.Utils;

/**
* SystemTrayEvent: System tray menu
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class SystemTrayEvent {	
	static SystemTray systemTray;
	
	public static void Show() {
		if (!SystemTray.isSupported()) {
			LOG.write("systemTray", "System tray is not supported !!! ");
			return;
		}
		systemTray = SystemTray.getSystemTray();
		BufferedImage img = null;
		try {
			img = ImageIO.read(new ByteArrayInputStream(Base64Utils.base64Decode(IconLoader.getIconApp())));
		} catch (IOException e1) {
			LOG.write("SystemTray", e1.getMessage());
		}
		// popupmenu
		PopupMenu trayPopupMenu = new PopupMenu();

		// 1t menuitem for popupmenu
		final JFrame frame = new JFrame("TokenSigning");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon iconInfom = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconInfo()));
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
		
		//
		MenuItem config = new MenuItem("Config");
		config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProxyConfigurationForm form = new ProxyConfigurationForm();
				form.setLocationRelativeTo(null);
				form.setVisible(true);
			}
		});
		trayPopupMenu.add(config);
		
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
	
}