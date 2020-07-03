package com.tokensigning.form;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tokensigning.common.Base64Utils;
import com.tokensigning.utils.LanguageOption;

public class LanguageForm {
	private static final String ICON_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAsQAAALEBxi1JjQAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAOzSURBVFiFvZdNbBRlGMd/z7uzH/0WbBQiNW4jGEGlIaYJCX5sIcEsxgMHQowx8YKeuUg9aE+m2MDFmx8HD4KJCRdTE5PKxqotxiZArBgo2AQqtKEBy3bb3dndeTzM7sZ2P2Z2QJ/TOzPP+///Zub9eF7BZ+hPdFCIvAoygMhO0CeAh0qP/waZRfQi6FmMPSp7SPvRFU/j8dbNaPF94E2g1SfvCsoplOOSyF0NBKATtGDHBhE9CrT5NF4feZCTZLIfSJKcbwBNRZ/EcAZ4NqDxesUJ8uGDsi+z4AmgqXAfxnwHPPJgzCtxA0eTkrCn6wKU3vxnL3ON9qAPH0Rbn3ZFVi4hi2cQe84L4jp5q//fX6ICoClimOgk0Fe/v6HYM4jz2FGQyDoqGzN3gtDcMOA0wp8kYyfKY8JU7kvsvcbmUIyP4Gw5Vm0OIBGcnkGK8Y8aSQCym7bYUOUKKlNthgajXbteprD9mxLJPULXhzB33Gtn42sUHx+CUAcA1qUDyNJ4IwqbAttlb+6a+wXced5wqjmb36m0rZm3MPOfgj0P9jxm/hOsK28A6uZueruRFECEMO8CiJ6jk1z0Fh6LTP75WQh3I6t/YF3or5lT6JtCW56C/G3CU71eEBmc3CZDNnLAyxwA4/53WT5fPyd3s5Tb4ikHtGEiSQtkwE92aOYIuuEVzF8jNZ9rZAvauduFXJmumVMdstcqbSyeqebuKNwdrUPXTnHbF2Bibu7C5z4B9DkDGveZXVui6wUKOyfRDndcSPoXzOLXPntLrwV0BjV3ug9R3PoZ5fVMstewLh8GLfqV6DLeOXUi1EYxPlIxN7dPY/02APnFpmQMcC+Iv7bvAmujK3LzY0JXj0DhTrMySwZkNhBAaEOlLelzQSQA/dMgeiFIV5OeRFavIKuXMemJgAByUXQ8chiV0wEV7i9UD4mmaMdEbwHt/7O9uxRLgmWUr4IoFFqeoRDbERTgS0mwXNoNOQ7kmzKP7WBp6xhL274PApEDGYZSQeKWznKyWZVyiI+lfG0HOSEvZWfdvqXQKcJkomPAi351ym9uZX9vwl0nyNgD5ZJsbVE61vYo4cKvQE8Tis1EVVG6ZimWfZkFHE0CN/4T85Am158NqvYCSdjTOJFdwA8PzlsnyFv9sseu+lc1NyNJpBfJ5PaXRqp9H842Ih/SbSdqnYrAz+H0x1gvqsdQXsf/GXEZOAUyXB7t9cIToAKSoh0TSbolnPaBxFlzPNdZkPOoniVnfyv7yfjR/QcTg0c4bv0RyQAAAABJRU5ErkJggg==";
	
	public static String show(){
		final JFrame frame = new JFrame(LanguageOption.LANGUAGE_DIALOG);
		frame.setAlwaysOnTop(true);
        Object[] obj = new Object[2];
        obj[0] = "English";
        obj[1] = "Spanish";        
        
        ImageIcon icon = new ImageIcon(Base64Utils.base64Decode(ICON_BASE64));
        String result = (String) JOptionPane.showInputDialog(frame, LanguageOption.LANGUAGE_DIALOG_REQUEST, LanguageOption.LANGUAGE_DIALOG_CAPTION,
                JOptionPane.OK_CANCEL_OPTION, icon, obj, obj[0]);          
        
        if (result != null)
        {
        	if (result.equals(obj[0]))
        		return "en";
        	if (result.equals(obj[1]))
        		return "spa";
        }
        return null;
	}
}
