package com.tokensigning.form;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tokensigning.common.FileUtil;
import com.tokensigning.common.LOG;
import com.tokensigning.configuraion.ProxyInfo;
import com.tokensigning.utils.Utils;

/**
* ProxyConfigurationForm: proxy configuration form
*
* @author  Tuan
* @version 1.0
* @since   2020-07-12 
*/

public class ProxyConfigurationForm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtProxyServer;
	private JTextField txtProxyPort;
	private JTextField txtProxyUser;
	private JTextField txtProxyPass;
	private JCheckBox checkBoxUseProxy;
	private static Gson _gson = new GsonBuilder().disableHtmlEscaping().create();
	static ProxyConfigurationForm frame = new ProxyConfigurationForm();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ProxyConfigurationForm() {
		setResizable(false);

		setTitle("Proxy configuration");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 297);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(5, 37, 424, 152);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Proxy server");
		lblNewLabel.setBounds(10, 11, 98, 14);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Proxy port");
		lblNewLabel_1.setBounds(10, 51, 98, 14);
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Username");
		lblNewLabel_2.setBounds(10, 87, 98, 14);
		panel.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Password");
		lblNewLabel_3.setBounds(10, 125, 98, 14);
		panel.add(lblNewLabel_3);
		
		txtProxyServer = new JTextField();
		txtProxyServer.setEditable(false);
		txtProxyServer.setBounds(140, 8, 274, 20);
		panel.add(txtProxyServer);
		txtProxyServer.setColumns(10);
		
		txtProxyPort = new JTextField();
		txtProxyPort.setEditable(false);
		txtProxyPort.setColumns(10);
		txtProxyPort.setBounds(140, 48, 274, 20);
		panel.add(txtProxyPort);
		
		txtProxyUser = new JTextField();
		txtProxyUser.setEditable(false);
		txtProxyUser.setColumns(10);
		txtProxyUser.setBounds(140, 84, 274, 20);
		panel.add(txtProxyUser);
		
		txtProxyPass = new JTextField();
		txtProxyPass.setEditable(false);
		txtProxyPass.setColumns(10);
		txtProxyPass.setBounds(140, 122, 274, 20);
		panel.add(txtProxyPass);
		
		checkBoxUseProxy = new JCheckBox("Use proxy");
		checkBoxUseProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setConfigStatus(checkBoxUseProxy.isSelected());
			}
		});
		checkBoxUseProxy.setBounds(6, 7, 97, 23);
		contentPane.add(checkBoxUseProxy);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(5, 206, 424, 41);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (checkBoxUseProxy.isSelected())
				{
					ProxyInfo proxyInfo = new ProxyInfo();
					if (txtProxyServer.getText() == null || txtProxyServer.getText().isEmpty())
					{
						JFrame f = new JFrame();  
					    JOptionPane.showMessageDialog(f, "Input poxy server, please!");
					    return;
					}
					if (txtProxyPort.getText() == null || txtProxyPort.getText().isEmpty())
					{
						JFrame f = new JFrame();  
					    JOptionPane.showMessageDialog(f, "Input proxy port, please!");
					    return;
					}
					proxyInfo.setServer(txtProxyServer.getText().trim());
					proxyInfo.setPort(txtProxyPort.getText().trim());
					proxyInfo.setUserName(txtProxyUser.getText());
					proxyInfo.setPassword(txtProxyPass.getText());
					String jsStr = _gson.toJson(proxyInfo);
					try {
						FileUtil.writeToFile(jsStr.getBytes(StandardCharsets.UTF_8), Paths.get(Utils.getConfigFolder(), Utils.PROXY_CONFIG_FILE).toString());
						JFrame f = new JFrame();				
					    JOptionPane.showMessageDialog(f, "Config successfully!");
					} catch (IOException e1) {
						LOG.write("ProxyConfigurationForm", e1.getMessage());
						JFrame f = new JFrame();				
					    JOptionPane.showMessageDialog(f, "Config failed!");
					}					
				}
				else
				{
					try
					{
						File file = new File(Paths.get(Utils.getConfigFolder(), Utils.PROXY_CONFIG_FILE).toString()); 
				        file.delete();	
					}
					catch (Exception ex) {
						// TODO: handle exception
					}
				}
				closeApplication();
				 
			}
		});
		btnOK.setBounds(226, 5, 89, 36);
		panel_1.add(btnOK);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeApplication();
			}
		});
		btnCancel.setBounds(325, 5, 89, 36);
		panel_1.add(btnCancel);
		//
		getConfig();
	}
	private void closeApplication(){
		  dispose();
		}
	private void setConfigStatus(boolean enable)
	{
		txtProxyServer.setEditable(enable);
		txtProxyPort.setEditable(enable);
		txtProxyUser.setEditable(enable);
		txtProxyPass.setEditable(enable);
	}
	
	private void getConfig()
	{
		ProxyInfo proInfo = getProxyConfig();
		if (proInfo != null)
		{
			// 
			checkBoxUseProxy.setSelected(true);
			txtProxyServer.setEditable(true);
			txtProxyPort.setEditable(true);
			txtProxyUser.setEditable(true);
			txtProxyPass.setEditable(true);
			//
			txtProxyServer.setText(proInfo.getServer());
			txtProxyPort.setText(proInfo.getPort());
			txtProxyUser.setText(proInfo.getUserName());
			txtProxyPass.setText(proInfo.getPassword());
		}
		
	}
	
	public static ProxyInfo getProxyConfig()
	{
		ProxyInfo proInfo = null;
		try {
			byte[] cfgByts = FileUtil.readBytesFromFile(Paths.get(Utils.getConfigFolder(), Utils.PROXY_CONFIG_FILE).toString());
			String cfgStr = new String(cfgByts);
			proInfo = _gson.fromJson(cfgStr, ProxyInfo.class);
			return proInfo;
		} catch (Exception e) {
			LOG.write("getProxyConfig", e.getMessage());
		}
		
		return null;
		
	}
}
