package com.tokensigning.form;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.common.IconLoader;
import com.tokensigning.utils.LanguageOption;

/**
* CertificateChooser: select certificate in store
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class CertificateChooser {
	public static String show(List<CertificateInfo> certs){
		final JFrame frame = new JFrame("Certificate chooser");
		frame.setAlwaysOnTop(true);
        Object[] obj = new Object[certs.size()];
        
        for (int i = 0; i < certs.size(); i ++) {
        	CertificateInfo cert = certs.get(i);
        	String message = cert.getSubjectCN() + " - " + cert.getSerial();
            obj[i] = message;
        }
        ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(IconLoader.getIconCertificate()));
        String result = (String) JOptionPane.showInputDialog(frame, LanguageOption.CERT_DIALOG_REQUEST, LanguageOption.CERT_DIALOG_CAPTION,
                JOptionPane.OK_CANCEL_OPTION, icon, obj, obj[0]);          
        
        if (result != null)
        {
        	String[] spit = result.split(" - ");
        	if (spit.length > 1)
        	{
        		String serial = spit[spit.length - 1];
        		return serial;
        	}
        }
        return null;
	}
}
