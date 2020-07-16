package com.tokensigning.form;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.IconLoader;
import com.tokensigning.utils.LanguageOption;


/**
* LanguageForm: select language
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/
public class LanguageForm {
	
	public static String show(){
		final JFrame frame = new JFrame(LanguageOption.LANGUAGE_DIALOG);
		frame.setAlwaysOnTop(true);
        Object[] obj = new Object[2];
        obj[0] = "Portuguese";
        obj[1] = "Spanish";        
        
        ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconWarning()));
        String result = (String) JOptionPane.showInputDialog(frame, LanguageOption.LANGUAGE_DIALOG_REQUEST, LanguageOption.LANGUAGE_DIALOG_CAPTION,
                JOptionPane.OK_CANCEL_OPTION, icon, obj, obj[0]);          
        
        if (result != null)
        {
        	if (result.equals(obj[0]))
        		return "por";
        	if (result.equals(obj[1]))
        		return "spa";
        }
        return null;
	}
}
