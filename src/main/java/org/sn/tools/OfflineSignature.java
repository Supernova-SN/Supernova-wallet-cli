package org.sn.tools;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.sn.common.crypto.ECKey;
import org.sn.common.crypto.Sha256Hash;
import org.sn.common.utils.ByteArray;
import org.sn.common.utils.Utils;
import org.sn.core.exception.CipherException;
import org.sn.keystore.Wallet;
import org.sn.keystore.WalletFile;
import org.sn.keystore.WalletUtils;
import org.sn.protos.Protocol.Transaction;
import org.sn.walletserver.WalletApi;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class OfflineSignature extends JFrame{
	
	JPanel jp;
	JLabel lbTranHex = new JLabel("交易数据(HEX):"); 
	JLabel lbSignedTranHex = new JLabel("签名后的交易数据(HEX):"); 
	JLabel lbTranInfo = new JLabel("交易内容:");
	JLabel lbFile = new JLabel("密钥文件:");
	
	JButton btnSign  = new JButton("签名");
	JButton btnSave  = new JButton("保存成txt文件");
	JButton btnFile  = new JButton("...");
	JFileChooser fileChooser = new JFileChooser();
	
	JTextArea taTranHex = new JTextArea(5,10); 	
	JTextArea taSignedTranHex = new JTextArea(5,10);

	JTextArea taTranInfo = new JTextArea(5,10);
	JScrollPane scr = new JScrollPane(taTranInfo);
	
	JTextArea taHelp = new JTextArea(5,10);
	JTextField tfFile = new JTextField(20); 
	
	String passwd;      // 密钥文件的密码
	byte[] privateKey;  // 密钥文件的私钥
	WalletApi  wallapi;     // 钱包对象
	WalletFile walletFile;  // 钱包文件对象
	
	// 构造函数
	public OfflineSignature(){
		this.setTitle("离线签名工具_20200214_V1.1");
		Font font20 = new Font("宋体",Font.PLAIN,20);
		
		jp = new JPanel();
		jp.setLayout(null);
		
		lbTranHex.setBounds(10,0,150,30);
		lbTranHex.setFont(font20);
		taTranHex.setBounds(10,30,430,250);
		taTranHex.setFont(font20);
		taTranHex.setLineWrap(true);
		
		lbSignedTranHex.setBounds(450,0,300,30);
		lbSignedTranHex.setFont(font20);
		taSignedTranHex.setBounds(450,30,430,250);
		taSignedTranHex.setFont(font20);
		taSignedTranHex.setLineWrap(true);
		
		lbTranInfo.setBounds(10,280,150,30);
		lbTranInfo.setFont(font20);
		taTranInfo.setBounds(10,310,430,440);
		taTranInfo.setFont(font20);
		taTranInfo.setLineWrap(true);
		scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scr.setBounds(10,310,430,440);
		
		
		lbFile.setBounds(450,280,150,30);
		lbFile.setFont(font20);
		tfFile.setBounds(450,310,390,40);
		tfFile.setFont(font20);
		btnFile.setBounds(840,310,40,40);
		btnFile.setFont(font20);
		
		
		FileFilter filter = new FileNameExtensionFilter("密钥文件（json）", "json");// 设置文件过滤器，只列出json格式的文件
		fileChooser.setFileFilter(filter);
		btnFile.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				int i = fileChooser.showOpenDialog(getContentPane());// 显示文件选择对话框				
				// 判断用户单击的是否为“打开”按钮
				if (i == JFileChooser.APPROVE_OPTION) {					
					File selectedFile = fileChooser.getSelectedFile();// 获得选中的文件对象
					tfFile.setText(selectedFile.getAbsolutePath());// 显示选中文件的名称
				}
			}
		});		
		
		taHelp.setBounds(450,450,430,140);
		taHelp.setFont(font20);
		taHelp.setLineWrap(true);
		taHelp.setText("说明：\nSN区块链离线签名专用工具，请选择密钥文件后执行签名，输入密码。");
		taHelp.setEditable(false);
		taHelp.setOpaque(false);
		
		
		btnSign.setBounds(500,600,100,50);
		btnSign.setFont(font20);
		btnSave.setBounds(620,600,200,50);
		btnSave.setFont(font20);
		btnSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//解析交易数据
				byte[] transactionBytes = ByteArray.fromHexString(taTranHex.getText().trim());
				Transaction trans;
				try {
					trans = Transaction.parseFrom(transactionBytes);
					taTranInfo.setText(Utils.printTransaction(trans));
				} catch (InvalidProtocolBufferException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}				
				
				//输入密码
				JPasswordField pwd = new JPasswordField();
				Object[] message = {"输入密码:", pwd};
				int res = JOptionPane.showConfirmDialog(OfflineSignature.this, message, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				//System.out.println("pwd="+new String(pwd.getPassword()));
				if(res==0) {
					passwd = new String(pwd.getPassword());
				}else {
					passwd="";
				}
				
				//加载密钥文件，验证密码
				try {
					wallapi = loadPrivateKeyFile(tfFile.getText(), passwd);
					System.out.println("验证成功！");
					privateKey = Wallet.decrypt2PrivateBytes(ByteArray.fromString(passwd), walletFile);
					//System.out.println("privateKey="+ByteArray.toHexString(privateKey));
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("文件读取失败！");
					System.out.println(e1.getMessage());
					return;
				} catch (CipherException e1) {
					// TODO Auto-generated catch block
					System.out.println("密码验证失败！");
					System.out.println(e1.getMessage());
					return;
				}
				
				//开始签名计算
			    //sign a transaction in byte format and return a Transaction in byte format			    
				try {
					byte[] transaction  = signTransaction2Byte(transactionBytes, privateKey);
					System.out.println("transaction4 ::::: " + ByteArray.toHexString(transaction));
					taSignedTranHex.setText(ByteArray.toHexString(transaction));
				} catch (InvalidProtocolBufferException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			}
		});
			
		btnSave.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				try {
					FileWriter fw = new FileWriter("outputSignedTrans.txt");
					String fileContent = taSignedTranHex.getText().replaceAll("\n", "\r\n");//此处主要是为了在Windows中实现换行操作
					fw.write(fileContent);
					fw.flush();
					fw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});		
		
		
		jp.add(lbTranHex);
		jp.add(lbSignedTranHex);
		jp.add(lbTranInfo);
		
		
		jp.add(taTranHex);
		jp.add(taSignedTranHex);
		jp.add(scr);
		
		jp.add(lbFile);
		jp.add(tfFile);
		jp.add(btnFile);
		
		jp.add(taHelp);
		jp.add(btnSign);
		jp.add(btnSave);		
		
		this.getContentPane().add(jp);		

		this.setSize(900, 800);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OfflineSignature mainwin = new OfflineSignature();
	}

	public WalletApi  loadPrivateKeyFile(String filename,String pwd) throws IOException, CipherException {
		walletFile = WalletUtils.loadWalletFile(new File(filename));			
		WalletApi walletApi = new WalletApi(walletFile);		
		walletApi.checkPassword(ByteArray.fromString(pwd));
		walletApi.setLogin();  //密码检验失败就会抛出异常，成功就设置成登录状态。
		return walletApi;
	}
	
	private static byte[] signTransaction2Byte(byte[] transaction, byte[] privateKey)
		      throws InvalidProtocolBufferException {
	    ECKey ecKey = ECKey.fromPrivate(privateKey);
	    Transaction transaction1 = Transaction.parseFrom(transaction);
	    byte[] rawdata = transaction1.getRawData().toByteArray();
	    byte[] hash = Sha256Hash.hash(rawdata);
	    byte[] sign = ecKey.sign(hash).toByteArray();
	    //System.out.println("sign="+ByteArray.toHexString(sign));
	    
	    return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
	  }
}
