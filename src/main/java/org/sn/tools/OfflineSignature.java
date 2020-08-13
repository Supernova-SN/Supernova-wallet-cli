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
	JLabel lbTranHex = new JLabel("��������(HEX):"); 
	JLabel lbSignedTranHex = new JLabel("ǩ����Ľ�������(HEX):"); 
	JLabel lbTranInfo = new JLabel("��������:");
	JLabel lbFile = new JLabel("��Կ�ļ�:");
	
	JButton btnSign  = new JButton("ǩ��");
	JButton btnSave  = new JButton("�����txt�ļ�");
	JButton btnFile  = new JButton("...");
	JFileChooser fileChooser = new JFileChooser();
	
	JTextArea taTranHex = new JTextArea(5,10); 	
	JTextArea taSignedTranHex = new JTextArea(5,10);

	JTextArea taTranInfo = new JTextArea(5,10);
	JScrollPane scr = new JScrollPane(taTranInfo);
	
	JTextArea taHelp = new JTextArea(5,10);
	JTextField tfFile = new JTextField(20); 
	
	String passwd;      // ��Կ�ļ�������
	byte[] privateKey;  // ��Կ�ļ���˽Կ
	WalletApi  wallapi;     // Ǯ������
	WalletFile walletFile;  // Ǯ���ļ�����
	
	// ���캯��
	public OfflineSignature(){
		this.setTitle("����ǩ������_20200214_V1.1");
		Font font20 = new Font("����",Font.PLAIN,20);
		
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
		
		
		FileFilter filter = new FileNameExtensionFilter("��Կ�ļ���json��", "json");// �����ļ���������ֻ�г�json��ʽ���ļ�
		fileChooser.setFileFilter(filter);
		btnFile.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				int i = fileChooser.showOpenDialog(getContentPane());// ��ʾ�ļ�ѡ��Ի���				
				// �ж��û��������Ƿ�Ϊ���򿪡���ť
				if (i == JFileChooser.APPROVE_OPTION) {					
					File selectedFile = fileChooser.getSelectedFile();// ���ѡ�е��ļ�����
					tfFile.setText(selectedFile.getAbsolutePath());// ��ʾѡ���ļ�������
				}
			}
		});		
		
		taHelp.setBounds(450,450,430,140);
		taHelp.setFont(font20);
		taHelp.setLineWrap(true);
		taHelp.setText("˵����\nSN����������ǩ��ר�ù��ߣ���ѡ����Կ�ļ���ִ��ǩ�����������롣");
		taHelp.setEditable(false);
		taHelp.setOpaque(false);
		
		
		btnSign.setBounds(500,600,100,50);
		btnSign.setFont(font20);
		btnSave.setBounds(620,600,200,50);
		btnSave.setFont(font20);
		btnSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//������������
				byte[] transactionBytes = ByteArray.fromHexString(taTranHex.getText().trim());
				Transaction trans;
				try {
					trans = Transaction.parseFrom(transactionBytes);
					taTranInfo.setText(Utils.printTransaction(trans));
				} catch (InvalidProtocolBufferException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}				
				
				//��������
				JPasswordField pwd = new JPasswordField();
				Object[] message = {"��������:", pwd};
				int res = JOptionPane.showConfirmDialog(OfflineSignature.this, message, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				//System.out.println("pwd="+new String(pwd.getPassword()));
				if(res==0) {
					passwd = new String(pwd.getPassword());
				}else {
					passwd="";
				}
				
				//������Կ�ļ�����֤����
				try {
					wallapi = loadPrivateKeyFile(tfFile.getText(), passwd);
					System.out.println("��֤�ɹ���");
					privateKey = Wallet.decrypt2PrivateBytes(ByteArray.fromString(passwd), walletFile);
					//System.out.println("privateKey="+ByteArray.toHexString(privateKey));
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("�ļ���ȡʧ�ܣ�");
					System.out.println(e1.getMessage());
					return;
				} catch (CipherException e1) {
					// TODO Auto-generated catch block
					System.out.println("������֤ʧ�ܣ�");
					System.out.println(e1.getMessage());
					return;
				}
				
				//��ʼǩ������
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
					String fileContent = taSignedTranHex.getText().replaceAll("\n", "\r\n");//�˴���Ҫ��Ϊ����Windows��ʵ�ֻ��в���
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
		walletApi.setLogin();  //�������ʧ�ܾͻ��׳��쳣���ɹ������óɵ�¼״̬��
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
