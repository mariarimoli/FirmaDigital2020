package com.tokensigning.form;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.IconLoader;
import com.tokensigning.utils.LanguageOption;

/**
* PINVerification: dialog to input PIN
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class PINVerification {	
	static volatile int countClick = 0; 
	
	/**
     * Show PIN dialog
     *
     * @param 
     * @return PIN
     */
	public static String show() {
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		JPasswordField passwordField = new JPasswordField();
		passwordField.addAncestorListener(new RequestFocusListener());
		final JComponent[] inputs = new JComponent[] { new JLabel(LanguageOption.PIN_DIALOG_LABEL),
				passwordField };		
		ImageIcon iconInfom = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconInfo()));
		String OS = System.getProperty("os.name");
		
		if (!OS.startsWith("Windows"))
		{
			passwordField.addMouseListener(new MouseAdapter(){
	            @Override
	            public void mouseClicked(MouseEvent e){
	            	if (countClick > 2 || e.getClickCount() == 2)
	            	{
	            		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_ALERT_CONTENT, LanguageOption.COMMON_DIALOG_TITLE, 
	            				JOptionPane.INFORMATION_MESSAGE, iconInfom);
	            		countClick = 0;
	            	}
	            	else
	            	{
	            		countClick++;
	            	}
	            }
	        });
		}
		
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconQuestion()));
		String pass = null;

    	do
    	{
    		int result = JOptionPane.showConfirmDialog(frame, inputs, LanguageOption.PIN_DIALOG_TITLE,
    				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
    		
    		if (result == JOptionPane.OK_OPTION) {
    			pass = new String(passwordField.getPassword());
    			if (pass.length() < 4)
    			{
    				pass = null;
    				passwordField.setText("");
    				JOptionPane.showMessageDialog(frame,  LanguageOption.PIN_DIALOG_WARNING_INVALID_SIZE, LanguageOption.COMMON_DIALOG_TITLE
    						, JOptionPane.INFORMATION_MESSAGE, iconInfom);
    			}
    		}
    		else
    		{
    			pass = "";
    		}
    	}
    	while(null == pass);    	
		
		return pass;
	}	
	
	/**
     * Show warning PIN Incorrect
     *
     * @param 
     * @return 
     */
	public static void showPINIncorrect()
	{
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_WARNING_INCORRECT, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}
	
	/**
     * Show warning PIN Length invalid
     *
     * @param 
     * @return 
     */
	public static void showNumberOfPinInputInvalid()
	{
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_WARNING_MAX_INPUT, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}
	
	/**
     * Alert token is locked
     *
     * @param 
     * @return 
     */
	public static void showTokenLocked()
	{
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_WARNING_LOCK, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}
	
	/**
     * Show warning dialog
     *
     * @param 
     * @return 
     */
	public static void showErrorMessage(String text)
	{
		final JFrame frame = new JFrame(LanguageOption.WARNING_CAPTION);
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
		JOptionPane.showMessageDialog(frame,  text, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}	
}
 