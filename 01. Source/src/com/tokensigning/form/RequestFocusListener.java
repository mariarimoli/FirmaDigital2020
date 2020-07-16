package com.tokensigning.form;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;


public class RequestFocusListener implements AncestorListener {
	public void ancestorAdded(final AncestorEvent e) {
		final AncestorListener al = this;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JComponent component = (JComponent) e.getComponent();
				component.requestFocusInWindow();
				component.removeAncestorListener(al);
			}
		});
	}

	public void ancestorMoved(AncestorEvent e) {
	}

	public void ancestorRemoved(AncestorEvent e) {
	}
}