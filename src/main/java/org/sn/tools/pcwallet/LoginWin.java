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
		this.setSize(800, 500); // �ƶ������ô��ڴ�С
		this.setTitle("SN �����ȵ�PC��Ǯ��");
		Image icon = this.getToolkit().getImage("images/sn-logo.png");
		this.setIconImage(icon);

		// ��ʾĬ�����ĵ�¼�ؼ�
		initLoginPanel();

		this.setResizable(false); // ��ֹ���
		this.setLocationRelativeTo(null);
		this.setVisible(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// ��ʾĬ�����ĵ�¼�ؼ�
	public void initLoginPanel(){
		JPanel jp = new JPanel();
		jp.setBounds(0, 0, this.getWidth(), this.getHeight()); // panel�ʹ���һ����
		jp.setLayout(null);
		
		Font font20 = new Font("����",Font.PLAIN,20);
	
		
		JLabel lbFile = new JLabel("��Կ�ļ�:");
		JTextField tfFile = new JTextField(20);
		JButton btnFile  = new JButton("...");
		JLabel  lb_psw  = new JLabel("����:"); 
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
		
		
		// ����ť����봰�ڵ��������
		jp.add(lbFile);
		jp.add(tfFile);
		jp.add(btnFile);		
		jp.add(lb_psw);
		jp.add(tf_psw);
		
		JButton btn_login = new JButton("��¼");		
		JButton btn_exit = new JButton("�˳�");
		
		JLabel label_register = new JLabel("<html><u>����Ǯ��</u><html>");
		label_register.setForeground(Color.BLUE);
		label_register.setBounds(530,250,120,50);
		jp.add(label_register);
		
		JLabel label_importWallet = new JLabel("<html><u>����Ǯ��</u><html>");
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
		
		//����ע�������һ�ε���Կ�ļ�·����ע���ڵ㣺 HKEY_CURRENT_USER/SOFTWARE/JavaSoft/prefs, Ĭ�ϴ򿪵�ǰ·��
		Preferences pref = Preferences.userRoot().node("snwallet");
		String lastPath = pref.get("lastpath", "");
		FileFilter filter = new FileNameExtensionFilter("��Կ�ļ���json��", "json");// �����ļ���������ֻ�г�json��ʽ���ļ�
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
				int i = fileChooser.showOpenDialog(getContentPane());// ��ʾ�ļ�ѡ��Ի���				
				// �ж��û��������Ƿ�Ϊ���򿪡���ť
				if (i == JFileChooser.APPROVE_OPTION) {					
					File selectedFile = fileChooser.getSelectedFile();// ���ѡ�е��ļ�����
					tfFile.setText(selectedFile.getPath());// ��ʾѡ���ļ�������
					String path = selectedFile.getParent();
					pref.put("lastpath", path);
				}
			}
		});		
		
		//���������س����ֱ࣬�ӵ�¼
		tf_psw.addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent e){
			   if(e.getKeyCode()==KeyEvent.VK_ENTER){
				   btn_login.doClick();//��½�¼�
			   }
			}
		});
		
		btn_login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
		
				//������Կ�ļ�����֤����
				UI.passwd = tf_psw.getText();
				try {
					UI.wallapi = loadPrivateKeyFile(tfFile.getText(), UI.passwd);
					System.out.println("��֤�ɹ���");
					byte[] pKey = Wallet.decrypt2PrivateBytes(ByteArray.fromString(UI.passwd), UI.walletFile);
					UI.privateKey = ByteArray.toHexString(pKey);
					UI.address = WalletApi.encode58Check(UI.wallapi.getAddress());
//					System.out.println("UI.privateKey="+UI.privateKey);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					System.out.println("�ļ���ȡʧ�ܣ�");
					System.out.println(e1.getMessage());
					logger.error("�ļ���ȡʧ�ܣ�");
					logger.error(e1.getMessage());
					JOptionPane.showMessageDialog(null, "��Կ�ļ���ȡʧ�ܣ�", "�ļ�����",JOptionPane.ERROR_MESSAGE);
					return;
				} catch (CipherException e1) {
					// TODO Auto-generated catch block
//					System.out.println("������֤ʧ�ܣ�");
//					System.out.println(e1.getMessage());
					logger.error("������֤ʧ�ܣ�");
					logger.error(e1.getMessage());
					JOptionPane.showMessageDialog(null, "������֤ʧ�ܣ�", "�������",JOptionPane.ERROR_MESSAGE); 
					return;
				}				
				logger.info("��¼�ɹ�");
			    
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
		
		//����Ǯ��
		label_register.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				//��ʾ����jlʱ�������
				label_register.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent e){
				//��ʾ�뿪jlʱ���Ĭ������
				label_register.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			//label����¼�
			public void mouseClicked(MouseEvent e){
				registerWallet();
			}
		});
		
		//����Ǯ��
		label_importWallet.addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				//��ʾ����jlʱ�������
				label_importWallet.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent e){
				//��ʾ�뿪jlʱ���Ĭ������
				label_importWallet.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			//label����¼�
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
		walletApi.setLogin(); // �������ʧ�ܾͻ��׳��쳣���ɹ������óɵ�¼״̬��
		return walletApi;
	}

	// ����Ǯ��
	private void registerWallet() {
		JPasswordField pwdField = new JPasswordField();
		Object[] message = { "����������:", pwdField };
		int selectedSiteName = JOptionPane.showConfirmDialog(null, message,"����Ǯ��", JOptionPane.YES_NO_OPTION);
		if (selectedSiteName != 0) {
			return;
		}
		String pwdFirst = pwdField.getText();
		
		Boolean pwdbool = WalletApi.passwordValid(pwdFirst.toCharArray());
		if (pwdbool == false) {
			return;
		}
		JPasswordField pwdField1 = new JPasswordField();
		Object[] message1 = { "��ȷ����������:", pwdField1 };
		int selectedSiteName1 = JOptionPane.showConfirmDialog(null, message1,"����Ǯ��", JOptionPane.YES_NO_OPTION);
		if (selectedSiteName1 != 0) {
			return;
		}
		String pwdSecond = pwdField1.getText();
		if (pwdFirst.equals(pwdSecond)) {
			try {
				WalletFile walletFile = WalletApi.CreateWalletFile(ByteArray.fromString(pwdSecond));
				String keystoreName = WalletApi.store2Keystore(walletFile);
				if (null == keystoreName) {
					JOptionPane.showMessageDialog(null, "����Ǯ��ʧ��");
					return;
				}
				JOptionPane.showMessageDialog(null, "����Ǯ���ɹ���Ǯ������Ϊ:\n" + keystoreName);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (CipherException e1) {
				e1.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "���벻һ�£������´���");
		}
	}
	
	//����Ǯ��
	private void importWallet() throws CipherException, IOException {
		JPasswordField pwdField = new JPasswordField();
		Object[] message = {"����������:", pwdField};
		int result = JOptionPane.showConfirmDialog(null, message,"����Ǯ��", JOptionPane.YES_NO_OPTION);
		if (result != 0) {
			return;
		}
		JPasswordField pwdField1 = new JPasswordField();
		Object[] message1 = {"��ȷ����������:", pwdField1};
		int result1 = JOptionPane.showConfirmDialog(null, message1,"����Ǯ��", JOptionPane.YES_NO_OPTION);
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
				Object[] message2 = {"����������˽Կ:", privateField};
				int result2 = JOptionPane.showConfirmDialog(null, message2,"����Ǯ��", JOptionPane.YES_NO_OPTION);
				if (result2 != 0) {
					return;
				}
				char[] pwdChar = pwdFirst.toCharArray();
				byte[] priKey = inputPrivateKey(privateField.getText());
				String fileName = new WalletApiWrapper().importWallet(pwdChar, priKey);
			    StringUtils.clear(pwdChar);
			    StringUtils.clear(priKey);
			    if (null == fileName) {
			    	JOptionPane.showMessageDialog(null, "����Ǯ��ʧ��");
			    	return;
			    }
			    JOptionPane.showMessageDialog(null, "����Ǯ���ɹ���Ǯ������Ϊ"+fileName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}catch (CipherException e1) {
				e1.printStackTrace();
			}
		}else {
			JOptionPane.showConfirmDialog(null, "���벻һ�£������µ���");
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