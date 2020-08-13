package org.sn.tools.pcwallet;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.sn.common.utils.ByteArray;
import org.sn.common.utils.Utils;
import org.sn.core.exception.CipherException;
import org.sn.keystore.StringUtils;
import org.sn.keystore.Wallet;
import org.sn.keystore.WalletFile;
import org.sn.keystore.WalletUtils;
import org.sn.walletcli.WalletApiWrapper;
import org.sn.walletserver.WalletApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginWin extends JFrame {
	private static final Logger logger = LoggerFactory.getLogger("LoginWin");

	public LoginWin() {
		this.setSize(800, 500); // 移动并设置窗口大小
		this.setTitle("SN 比特热点PC版钱包");
		Image icon = this.getToolkit().getImage("images/sn-logo.png");
		this.setIconImage(icon);

		// 显示默认面板的登录控件
		initLoginPanel();

		this.setResizable(false); // 禁止最大化
		this.setLocationRelativeTo(null);
		this.setVisible(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// 显示默认面板的登录控件
	public void initLoginPanel(){
		JPanel jp = new JPanel();
		jp.setBounds(0, 0, this.getWidth(), this.getHeight()); // panel和窗口一样大
		jp.setLayout(null);
		
		Font font20 = new Font("黑体",Font.PLAIN,20);
	
		
		JLabel lbFile = new JLabel("密钥文件:");
		JTextField tfFile = new JTextField(20);
		JButton btnFile  = new JButton("...");
		JLabel  lb_psw  = new JLabel("密码:"); 
		final JPasswordField  tf_psw  = new JPasswordField (30); 
		tf_psw.setEchoChar('*');
		
		lbFile.setBounds(100,110,100,50);
		lbFile.setFont(font20);
		tfFile.setBounds(200,110,400,50);
		tfFile.setFont(font20);
		btnFile.setBounds(600,110,50,50);
		btnFile.setFont(font20);			
		
		lb_psw.setBounds(100,190,100,50);
		lb_psw.setFont(font20);
		tf_psw.setBounds(200,190,400,50);
		tf_psw.setFont(font20);
		
		
		// 将按钮添加入窗口的内容面板
		jp.add(lbFile);
		jp.add(tfFile);
		jp.add(btnFile);		
		jp.add(lb_psw);
		jp.add(tf_psw);
		
		JButton btn_login = new JButton("登录");		
		JButton btn_exit = new JButton("退出");
		
		JLabel label_register = new JLabel("<html><u>创建钱包</u><html>");
		label_register.setForeground(Color.BLUE);
		label_register.setBounds(530,250,120,50);
		jp.add(label_register);
		
		JLabel label_importWallet = new JLabel("<html><u>导入钱包</u><html>");
		label_importWallet.setForeground(Color.BLUE);
		label_importWallet.setBounds(430,250,120,50);
		jp.add(label_importWallet);
		
		btn_login.setBounds(275, 320,100,50);
		btn_login.setFont(font20);
		btn_exit.setBounds(425, 320,100,50);
		btn_exit.setFont(font20);
		jp.add(btn_login);
		jp.add(btn_exit);
		
		this.getContentPane().add(jp);
		
		//利用注册表保存上一次的密钥文件路径。注册表节点： HKEY_CURRENT_USER/SOFTWARE/JavaSoft/prefs, 默认打开当前路径
		Preferences pref = Preferences.userRoot().node("snwallet");
		String lastPath = pref.get("lastpath", "");
		FileFilter filter = new FileNameExtensionFilter("密钥文件（json）", "json");// 设置文件过滤器，只列出json格式的文件
		JFileChooser fileChooser;		
		if(!lastPath.equals("")){
			fileChooser = new JFileChooser(lastPath);
//			System.out.println("lastpath:" + lastPath);
			logger.info("lastpath:" + lastPath);
		}else {
			fileChooser = new JFileChooser("./");			
		}
		fileChooser.setFileFilter(filter);		
		btnFile.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				int i = fileChooser.showOpenDialog(getContentPane());// 显示文件选择对话框				
				// 判断用户单击的是否为“打开”按钮
				if (i == JFileChooser.APPROVE_OPTION) {					
					File selectedFile = fileChooser.getSelectedFile();// 获得选中的文件对象
					tfFile.setText(selectedFile.getPath());// 显示选中文件的名称
					String path = selectedFile.getParent();
					pref.put("lastpath", path);
				}
			}
		});		
		
		//密码框监听回车安监，直接登录
		tf_psw.addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent e){
			   if(e.getKeyCode()==KeyEvent.VK_ENTER){
				   btn_login.doClick();//登陆事件
			   }
			}
		});
		
		btn_login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
		
				//加载密钥文件，验证密码
				UI.passwd = tf_psw.getText();
				try {
					UI.wallapi = loadPrivateKeyFile(tfFile.getText(), UI.passwd);
					System.out.println("验证成功！");
					byte[] pKey = Wallet.decrypt2PrivateBytes(ByteArray.fromString(UI.passwd), UI.walletFile);
					UI.privateKey = ByteArray.toHexString(pKey);
					UI.address = WalletApi.encode58Check(UI.wallapi.getAddress());
//					System.out.println("UI.privateKey="+UI.privateKey);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					System.out.println("文件读取失败！");
					System.out.println(e1.getMessage());
					logger.error("文件读取失败！");
					logger.error(e1.getMessage());
					JOptionPane.showMessageDialog(null, "密钥文件读取失败！", "文件错误",JOptionPane.ERROR_MESSAGE);
					return;
				} catch (CipherException e1) {
					// TODO Auto-generated catch block
//					System.out.println("密码验证失败！");
//					System.out.println(e1.getMessage());
					logger.error("密码验证失败！");
					logger.error(e1.getMessage());
					JOptionPane.showMessageDialog(null, "密码验证失败！", "密码错误",JOptionPane.ERROR_MESSAGE); 
					return;
				}				
				logger.info("登录成功");
			    
				UI.walletMain = new PCWalletMain();
				UI.walletMain.setVisible(true);
				LoginWin.this.setVisible(false);
			}
		});
		
		btn_exit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub	
				System.exit(0);
			}
		});
		
		//创建钱包
		label_register.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				//表示移入jl时变成手型
				label_register.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent e){
				//表示离开jl时变回默认手型
				label_register.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			//label点击事件
			public void mouseClicked(MouseEvent e){
				registerWallet();
			}
		});
		
		//导入钱包
		label_importWallet.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				//表示移入jl时变成手型
				label_importWallet.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent e){
				//表示离开jl时变回默认手型
				label_importWallet.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			//label点击事件
			public void mouseClicked(MouseEvent e){
				try {
					importWallet();
				} catch (CipherException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public WalletApi loadPrivateKeyFile(String filename, String pwd) throws IOException, CipherException {
		UI.walletFile = WalletUtils.loadWalletFile(new File(filename));
		WalletApi walletApi = new WalletApi(UI.walletFile);
		System.out.println("ppp"+new String(ByteArray.fromString(pwd)));
		walletApi.checkPassword(ByteArray.fromString(pwd));
		walletApi.setLogin(); // 密码检验失败就会抛出异常，成功就设置成登录状态。
		return walletApi;
	}

	// 创建钱包
	private void registerWallet() {
		JPasswordField pwdField = new JPasswordField();
		Object[] message = { "请输入密码:", pwdField };
		int selectedSiteName = JOptionPane.showConfirmDialog(null, message,"创建钱包", JOptionPane.YES_NO_OPTION);
		if (selectedSiteName != 0) {
			return;
		}
		String pwdFirst = pwdField.getText();
		
		Boolean pwdbool = WalletApi.passwordValid(pwdFirst.toCharArray());
		if (pwdbool == false) {
			return;
		}
		JPasswordField pwdField1 = new JPasswordField();
		Object[] message1 = { "请确认您的密码:", pwdField1 };
		int selectedSiteName1 = JOptionPane.showConfirmDialog(null, message1,"创建钱包", JOptionPane.YES_NO_OPTION);
		if (selectedSiteName1 != 0) {
			return;
		}
		String pwdSecond = pwdField1.getText();
		if (pwdFirst.equals(pwdSecond)) {
			try {
				WalletFile walletFile = WalletApi.CreateWalletFile(ByteArray.fromString(pwdSecond));
				String keystoreName = WalletApi.store2Keystore(walletFile);
				if (null == keystoreName) {
					JOptionPane.showMessageDialog(null, "创建钱包失败");
					return;
				}
				JOptionPane.showMessageDialog(null, "创建钱包成功，钱包名字为:\n" + keystoreName);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (CipherException e1) {
				e1.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "密码不一致，请重新创建");
		}
	}
	
	//导入钱包
	private void importWallet() throws CipherException, IOException {
		JPasswordField pwdField = new JPasswordField();
		Object[] message = {"请输入密码:", pwdField};
		int result = JOptionPane.showConfirmDialog(null, message,"导入钱包", JOptionPane.YES_NO_OPTION);
		if (result != 0) {
			return;
		}
		JPasswordField pwdField1 = new JPasswordField();
		Object[] message1 = {"请确认您的密码:", pwdField1};
		int result1 = JOptionPane.showConfirmDialog(null, message1,"导入钱包", JOptionPane.YES_NO_OPTION);
		if (result1 != 0) {
			return;
		}
		String pwdFirst = pwdField.getText();
		Boolean pwdbool = WalletApi.passwordValid(pwdFirst.toCharArray());
		if (pwdbool==false) {
			return;
		}
		String pwdSecond = pwdField1.getText();
		if (pwdFirst.equals(pwdSecond)) {
			try {
				JPasswordField privateField = new JPasswordField();
				Object[] message2 = {"请输入您的私钥:", privateField};
				int result2 = JOptionPane.showConfirmDialog(null, message2,"导入钱包", JOptionPane.YES_NO_OPTION);
				if (result2 != 0) {
					return;
				}
				char[] pwdChar = pwdFirst.toCharArray();
				byte[] priKey = inputPrivateKey(privateField.getText());
				String fileName = new WalletApiWrapper().importWallet(pwdChar, priKey);
			    StringUtils.clear(pwdChar);
			    StringUtils.clear(priKey);
			    if (null == fileName) {
			    	JOptionPane.showMessageDialog(null, "导入钱包失败");
			    	return;
			    }
			    JOptionPane.showMessageDialog(null, "导入钱包成功，钱包名字为"+fileName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch (CipherException e1) {
				e1.printStackTrace();
			}
		}else {
			JOptionPane.showConfirmDialog(null, "密码不一致，请重新导入");
		}
	}
	
	private byte[] inputPrivateKey(String privateKey1) throws IOException {
	    byte[] temp = new byte[128];
	    byte[] result = null;
	    System.out.println("Please input private key.");
	    while (true) {
	      //int len = System.in.read(temp, 0, temp.length);
	      int len = new StringBufferInputStream(privateKey1).read(temp, 0, temp.length);
	      if (len >= 64) {
	        byte[] privateKey = Arrays.copyOfRange(temp, 0, 64);
	        result = StringUtils.hexs2Bytes(privateKey);
	        StringUtils.clear(privateKey);
	        if (WalletApi.priKeyValid(result)) {
	          break;
	        }
	      }
	      StringUtils.clear(result);
	      break;
	    }
	    StringUtils.clear(temp);
	    return result;
	}

}