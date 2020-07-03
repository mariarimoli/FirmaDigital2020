package com.tokensigning.form;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.utils.LanguageOption;

public class CertificateChooser {
	private static final String ICON_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAAA3NCSVQICAjb4U/gAAAACXBIWXMAAADhAAAA4QFwGBwuAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAelQTFRF//////8AAICAgICAQEBAVVWAJElJ/7Zt379gVVVx48ZV6NFd6r9VTmJ268RiJElJSWFt68RiLD5PK0RNSGJv7MddK0NP7chb7she78VgKj5ORl9u8cVg7sdf7she78Vf8Mld8MZf7shd7sddK0FO78df8MhfK0BM8MdfRl5s8MdeK0BOSF9t7sZf7she78ZfRl9rK0FMLEJNSF9r7sZfSF9t78ZdSGBs78de7sdeLEJN78de27tgKkBO8MdfK0FN7sde78heR19s07Nb8MdecntoYmZS8Mdd7sheLEFMR19sR19sLEFN78de78deR19s78de78deK0FNK0FNK0FN78de78ZeR19s8Mde78df78deR19t78deK0FNK0FN78deR19s78deK0FN78deK0FN78deR19s78deK0FNMUdUNEpXQVhlR19sb3hpcnJTc3podXNTfIBofoFnkodWnJRlqZpgsKBjv6Zaxa1i07Rc17NU17hg2LRU2LRV2bVV2bhc2rVV2rZV27ZW3LdW3bhW3bhX3bxg3rlX37pX4LtY4btY4rxY4r1Z471Z5L1Z5L5Z5b5a5b9a5r9a5sBa58Fb6MFb6sNc68Nc68Rc7MRd7MVd7MVe7cVd7cVe7cZd7cZe7sZd7sZe78dew+FrUgAAAGh0Uk5TAAECAgQGBwcICQkLDA0NDhUaHR4nKSoqLjAxMzU7PD5CQ0pNTk5TVFZXV1hZWVxeaXJ0eXl8foCAiIySlZeXmZqdoaKlqre3uLvBwsfS09ra293g4uPm5+fo6evr8fT29/j7+/39/v7EKUnyAAAB9ElEQVQYGXXBB0MSARiA4c+2ZpYtbVq2p7aH7b1tD7WstKS69v6CSzwNRE8EBdHI5P2lwckJVD6P5Nt2tW75iiPX1sgkNj/B0VIt/7H33JUWsh5dPrtb/rIER2JgIIFjkRQ6AAwHvJrmDQwD+6XAqrvQ7zXtweHhQdv09sOtlZIz+zgQ0mASRzKoIWirmymuKiCmNjA0MDAE2BoDKsVVAWPtFiQDmhZIgtU+BuXiKmkjqnFSHeroSBHXKM1F4toIIRNsVX9fn1/VBjME6ySrtAGsAFiqCUioWhCw4F6pjKsBfD3gUz9pfvVBjw+okXFlTWAFwFJNQELVgoAFTWWStR5CJvSq+sNhv2ovmCHYLq6iZqI6RMqvDn+KIY3SNktc5TBqWjDSpWldI2CZo1AhrkogpjYQj0TigK0xoEpcJceAbg2O4hgNajdwqkxyVt+GsNe0Y8lkzDa9YWiolgKHgESnOjoTwEkpdJqMVDwSiafIOCMF5j388OXbT7J+ff38/vF8ybfPSHv7G0fqlSftoOQpbjQy3v349Prlx+9vPBn350pOrZHzzJO1UyZMrTcmvPC4bk4T18ILdwzXc4+j9cb5wwskz5xlWy8aafWXPGlHF8+Qf00/YRhG7QaPp3WHTGLP08biKdcfrJVJbdolsmWp5PsD6YcwtrCsmS8AAAAASUVORK5CYII=";
	
	public static String show(List<CertificateInfo> certs){
		final JFrame frame = new JFrame("Certificate chooser");
		frame.setAlwaysOnTop(true);
        Object[] obj = new Object[certs.size()];
        
        for (int i = 0; i < certs.size(); i ++) {
        	CertificateInfo cert = certs.get(i);
        	String message = cert.getSubjectCN() + " - " + cert.getSerial();
            obj[i] = message;
        }
        ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_BASE64));
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
