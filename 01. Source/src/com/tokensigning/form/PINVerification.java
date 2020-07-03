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
import com.tokensigning.utils.LanguageOption;

public class PINVerification {
	//private static final String LABEL = "Nhập mã PIN:";
	//private static final String TITLE = "Xác thực mã PIN";
	private static final String ICON_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAADlAAAA5QGP5Zs8AAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAQtQTFRF////JLbbIL/fILXVKbjWKLXSJrbSJbjTJbbTJbfTJbjTJbjSJbbTJbfTJbfTJbfTJbfTJbfTJrfTJ7jTKLjUKbjUK7nULbrVLrrVMbvVMrvWM7zWNLzWOb3XQMDZR8LaTMTbUMXcUcbcVcfdWMjdWcjeXcneYMrfac7hb9Dic9HjddLjeNLkedPkf9Xlhtfmh9fniNjnidjni9nojdnojtrokdvpktvpl93qm97rnd/rnt/ro+HspOHtsOXvseXvs+bwuOjxwOrywevzwuvzxOzz2fP32/P43fT43/X54/b56fj77fn77vn87/r88vv89Pv99fz99vz9+f3++/7+/P7+/f7//v//////ncxmegAAABF0Uk5TAAcIGBktSYSXmMHI2uPy8/XVqDFbAAABWUlEQVQ4y41TV1vCQBDcQEgzlEHsvWDF3nvvIooK8/9/iQ/cJbmgH87bzOzd7e7tikSwHD8Ii8Uw8B1LumF7BUQoeHbKzrglGCi5maSf7UMX+rKxn8vjF+Rz0Xnt9y8eXV5szA/rCHVHRt8/9USS5FdNv9LJw1V07o0ah+WO5IqI2Cr/6SbJq5Xa3TfJZVWLLSKeuuCY5DkAbJG8VKInYun+PJOfkwAw1iQbA6pjljjKH3gljwEAlQb5XlGyI74ue+n+ZhQAsEnyVqu+BOn+rLfI1oJmgYQpf48kdyMaStH0D0hyO+bFVEC1TX6vIRlgPnFHcjUphKkk6+S1IQRxmQBQ+SDXjQA/alQHJ+2XEUNw4lZ3MFExaMGKPwsAMLRSNQK8xHcDQPmR3E/4JTs5MABmST4kAlxz5IDBOnka+2rk4qEFZs52xiOSz/537HsvTu/V6728f6//D21jUsff8MCAAAAAAElFTkSuQmCC";
	public  static final String ICON_INFO   = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAADlAAAA5QGP5Zs8AAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAPZQTFRF////JLbbIL/fILXVKbjWKLXSJrbSJbjTJbbTJbfTJbjTJbjSJbbTJbfTJbfTJbfTJbfTJbfTJrfTJ7jTKLjULLnULbrVLrrVMLvVMrvWNr3WOr7XO77XPL/YP7/YRMHZR8LaSMPaS8PbU8bcVcfdWcjeW8neXcneXcreY8vfY8zgZczgac3hb8/idNHjetPkgdXmg9bmitjnjNnokdvpldzqmN3qmd3qmt7rnt/sqeLuquPutObwuOjxu+nxvOnyvuryx+z00O/20fD20/D21fH31/L33fT44fX55Pb68Pr88/v99fz9+v3+/P7+/f7//v//////MZEFEQAAABF0Uk5TAAcIGBktSYSXmMHI2uPy8/XVqDFbAAABO0lEQVQ4y4VTV1vCUAxNS+migwDujRNRcSPiHihOzv//Mz4gvUn5lPPU5KRJbnJClMFygzBK0ygMXIvG4fgJZ0h8J0fbXpkVyp4t+UKJx1AqGL4YC6LSOJhiZua4mP0veb4ETodf8W8OW+Wf/gC6oyrDPjxVutLH9/rI8IiInFz/9bNV8xaHiHz+Bz6RlSjP0tF9tyYmZpEr6WpnAGBbeFwKhDVzAwCDeeEKKDTGwlP/+AroyZwhRcZo7VW5B3RkQESp6nERQFM60lxAC/ia1QGRCrgGHlmXCKVZewcuWDcpn8lbAHZVQKAH1c63wK4e9TPwoPjEUsuqfQLt/LLkutcANKrNu7patxDMPoDz17cdVoIRkjsBgJcVI2w7J9oNYHA7l/FxYUz2m4fLpkEj+8mHM/n0Jh/v3+f/AxHgSjy/wBsUAAAAAElFTkSuQmCC";
	public  static final String ICON_WARNING= "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA3QAAAN0BcFOiBwAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAASbSURBVFiFxZdtTJVlGMd/1/2cl+cgHOSgCKjTlGFEFDrUGAIBQ8DICeopRXHK8syXWetbH3RuuvVFp261am2t1Rc3+yK51cS5UEcvY9ZW0idbrJjhS04BhfN29eEArgx4Dtj6f7xf/tfvvq77fp77FlXl/5RruhM3bg/laDTccOtmX0Qi5sKlS1/emI6PJJuB9aFQimcw8plCA0D/jd8THcoXw2n2pu729gf/GUAwGPTF3KnngOq56bBqmZDlvc+ZzkGu98cALsaHBxu7uroeOvVMqgRRz6xDolQ/lS0c3m5ItQFm0/BCKvuO/8FPfbFq4007BLzl1NNxBja1tC1RtMcyeE+ELObP+Xv/3b4hmt6+TTTOiFj6zOWOjl+c+BqnpKp6DPA2rJTHggNkZKfwymoPgFejcsypryOA5q27qhGa/CkQrJhgiiW01ftJ9wkITWU1ddVPBCAYDFpGOAmwpcowy060D4fh9FdxPmi/x8ORRBk9gRT2vOgdNdaTwWDQmjFA3J26W6Fo8TyhdrmMt3dcVc5cVk5ffMDZKwOJRkt4qSyNvCyDKkV9d+7unhFAY0tLhsIRgF11gjyKz683E6s2xuJ6X2S8XdJ9vLk2kSZROVJe3pgxbQAb72Egs7RAKFwkkw19JEt4rmAWVctcAJm4w4enBdD86msFCnvdLthR6/iwJOS32V9j43GBwt7SytqCpAHEip0AXBtKhbnpycXHMsyb72PrKg+Ay2XJiaQAmrftagTqMv3QVJbk6sfkt9lW6iUrTVCoK6+qa3QEEAqF3KJyHGB7jcHrnl58LIMdsNlTmTiWKnq8pKTkMbfHAG4PRQ+A5i9bIJQ/63DjTSS/TW2hm6L5FkC+Ly1wYFKA5tbWLFQPCtBWP8PgAJYBv83rNV4EQDhYVleXNSGAibqPAulVxcLSnMkBvGP/UVVszyRj/TZP51isK3KjkG4iHP1XgKaWncUq2ubzQEvV1BuvuliYZYPHFWNdaerEA0ezEKrwkOIRFG0rq1pbPNY9fh8wcAowm8oNsyfxG1NervDhGxaqOZNnAMBvE7g/zI5SN+91ho0Ip4DK0bjQ3LJzM0hFdgAaVzuvvdfN1MFhPAvBEg8LMgxARXnN2s0AVm9vrx2OSTswe/96w8K5zgDuDcG7n8c5d+UOK/K9+LxTlM1jYQ2OkO0XLvwcBVj16ccfvW8GwqYVWPT8EmFlvvPVn7+qdPUo3/REOXt5cOoJo1lYk+di5WILYFFUPK1GVesBNq5J7tjl5YIAqnEKFnucTUpLfJR2lCbGq2q9C6HSCOQvSA5g+VLhnX0WkWgWC7Mc3m1dFhihMNfCCMSh0iD0xxV6epN/IWUHcB4cIBKDuPL9bzHiCiL0G1HtBDj7tTISmcphhhoYZjiinP4uDIDG6ZSXt+ye45bINSDL54XKIiE303k5HgxNvQEDPojcH+Fab5jz16IMhRXgplsjhaKqbNi2s8il8olC8VRm/9T40yw5/RCPaWtXZ8eP4w+TUCjkvjUQXY3oCkECTlxU1XWrvy/qNGpc+dNo/OrDgbvfdnd3R2Aaj9Mnrb8A0TtykI+7cqgAAAAASUVORK5CYII=";
	static volatile int countClick = 0; 
	public static String show() {
		//Set action click vao o password => bat len alert nhap ma pin => bam OK => se nhap dc
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		JPasswordField passwordField = new JPasswordField();
		passwordField.addAncestorListener(new RequestFocusListener());
		final JComponent[] inputs = new JComponent[] { new JLabel(LanguageOption.PIN_DIALOG_LABEL),
				passwordField };		
		ImageIcon iconInfom = new ImageIcon(Base64Utils.base64Decode(ICON_INFO));
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
		
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_BASE64));
		String pass = null;

//    	if (!OS.startsWith("Windows"))// is MAC OS or Linux
//    	{
//    		Thread t1 = new Thread(){
//    			public void run() {
//    				try {
//    					Thread.sleep(2000);
//    					System.out.println("thread is running...");
//    					//frame.setAlwaysOnTop(false);
//    					passwordField.invalidate();
//    					//frame.repaint();
//    					frame.invalidate();
//    				} catch (InterruptedException e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
//    			}
//    		};  
//    		t1.start();
//    	}		
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
	public static void showPINIncorrect()
	{
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_WARNING));
		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_WARNING_INCORRECT, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}
	public static void showNumberOfPinInputInvalid()
	{
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_WARNING));
		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_WARNING_MAX_INPUT, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}
	public static void showTokenLocked()
	{
		final JFrame frame = new JFrame("PIN Dialog");
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_WARNING));
		JOptionPane.showMessageDialog(frame, LanguageOption.PIN_DIALOG_WARNING_LOCK, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}
	public static void showErrorMessage(String text)
	{
		final JFrame frame = new JFrame(LanguageOption.WARNING_CAPTION);
		frame.setAlwaysOnTop(true);
		frame.pack();
		ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_WARNING));
		JOptionPane.showMessageDialog(frame,  text, LanguageOption.WARNING_CAPTION, JOptionPane.WARNING_MESSAGE, icon);
	}	
}
 