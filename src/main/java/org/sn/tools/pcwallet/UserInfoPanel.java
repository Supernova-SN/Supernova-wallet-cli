package org.sn.tools.pcwallet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.border.Border;

import org.sn.api.GrpcAPI.EasyTransferResponse;
import org.sn.common.utils.ByteArray;
import org.sn.common.utils.Utils;
import org.sn.core.exception.CipherException;
import org.sn.keystore.Wallet;
import org.sn.protos.Protocol.Account;
import org.sn.protos.Protocol.Transaction;
import org.sn.tools.OfflineSignature;
import org.sn.walletserver.WalletApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInfoPanel extends JPanel{
	private static final Logger logger = LoggerFactory.getLogger("UserInfoPanel");
	
	JFrame parent;
	
	JPanel groupbox; 
	JButton btn_transfer;
	JButton btn_cancel;
	
	public UserInfoPanel(JFrame parent) {
		this.parent = parent;
		this.setBounds(0, 0, 1000, 700);
		
		Font font = new Font("΢���ź�",Font.PLAIN,24);
		
		groupbox = new JPanel();
		groupbox.setLayout(null);
		groupbox.setPreferredSize(new Dimension(780, 150));
		groupbox.setBounds(10, 10, 970, 150);
		Border border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE);
		groupbox.setBorder(BorderFactory.createTitledBorder(border,""));   //��ӱ߿�����
		this.add(groupbox);
		
		
		JLabel lbAddress = new JLabel("�˻���ַ");
		lbAddress.setBounds(30, 30, 150, 50);
		lbAddress.setFont(font);
		JLabel lbAdd = new JLabel(UI.address);
		lbAdd.setBounds(180, 30, 720, 50);
		lbAdd.setFont(font);
		
		JLabel lbBalance = new JLabel("���");
		lbBalance.setBounds(30, 80, 150, 50);
		lbBalance.setFont(font);
		JTextField tf_Balance  = new JTextField(34);
		tf_Balance.setBounds(180, 80, 680, 50);
		tf_Balance.setFont(font);
		tf_Balance.setEditable(false);
		tf_Balance.setText("---");
		
		JButton jbBalance = new JButton("��ѯ");
		jbBalance.setBounds(860, 80, 90, 50);
		jbBalance.setFont(font);	
		
		JLabel lbToAddress = new JLabel("ת����ַ");
		lbToAddress.setBounds(30, 200, 150, 50);
		lbToAddress.setFont(font);		
		JTextField tf_ToAdd  = new JTextField(34);
		tf_ToAdd.setBounds(180, 200, 720, 50);
		tf_ToAdd.setFont(font);
		
		JLabel lbValue = new JLabel("ת�����");
		lbValue.setBounds(30, 260, 150, 50);
		lbValue.setFont(font);		
		JTextField tf_Value  = new JTextField(34);
		tf_Value.setBounds(180, 260, 720, 50);
		tf_Value.setFont(font);
		JLabel lbSn = new JLabel("SN");
		lbSn.setBounds(900, 260, 100, 50);
		lbSn.setFont(font);
		
		btn_transfer = new JButton("ת��");
		btn_transfer.setBounds(580, 500, 150, 50);
		btn_transfer.setFont(font);	
		btn_cancel = new JButton("ȡ��");
		btn_cancel.setBounds(750, 500, 150, 50);
		btn_cancel.setFont(font);	
		
		groupbox.add(lbAddress);
		groupbox.add(lbAdd);
		groupbox.add(lbBalance);
		groupbox.add(tf_Balance);
		groupbox.add(jbBalance);
		
		
		this.add(lbToAddress);
		this.add(tf_ToAdd);		
		this.add(lbValue);
		this.add(tf_Value);
		this.add(lbSn);
		this.add(btn_transfer);
		this.add(btn_cancel);
		
		this.setLayout(null);
		
		//��ѯ���
		jbBalance.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				//��ѯ���
				Account account = WalletApi.queryAccount(UI.wallapi.getAddress());
			    if (account == null) {
//			    	System.out.println("GetAccount failed !!!!");
			    	logger.error("GetAccount failed !!!!");
			    } else {
//			    	System.out.println("\n" + Utils.printAccount(account));
			    	logger.info(Utils.printAccount(account));
			    	
			    	UI.account = account;
			    	double fv = UI.account.getBalance()/1000000.0D;
					NumberFormat formatter = new DecimalFormat("0.000000");		 
					String formmatedFloatValue = formatter.format(fv);
			    	tf_Balance.setText(formmatedFloatValue);
			    }
				
			}
		});	
		
		btn_transfer.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				String to  = tf_ToAdd.getText();
				if (to.trim().equals("")) {
					JOptionPane.showMessageDialog(null, "������ת����ַ");
					return;
				}
				byte[] tobyte = WalletApi.decode58Check(to);
				if (tobyte==null) {
					JOptionPane.showMessageDialog(null, "����ĵ�ַ����");
					return;
				};
				String value = tf_Value.getText();
				if (value.trim().equals("")) {
					JOptionPane.showMessageDialog(null, "��������");
					return;
				}
				Double fv;
				try {
					fv = Double.valueOf(value);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "������Ϊ����");
					return;
				}
				long v = (long) (fv*1000000);
				if (v<=0) {
					JOptionPane.showMessageDialog(null, "����С��0.000001");
					return;
				}
				
				//�������������
				String passwd;
				JPasswordField pwd = new JPasswordField();
				pwd.setEchoChar('*');
				Object[] message = {"��������:", pwd};
				int res = JOptionPane.showConfirmDialog(parent, message, "��������", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				//System.out.println("pwd="+new String(pwd.getPassword()));
				if(res==0) {
					passwd = new String(pwd.getPassword());
					//������Կ�ļ�����֤����
					if(passwd.equals(UI.passwd)) {
//						System.out.println("������֤�ɹ���");
//						System.out.println("Easy transfer Thread start...");
						System.out.println("from="+UI.address+",  to="+to+",  value="+v);
						logger.info("������֤�ɹ���");
						logger.info("Easy transfer Thread start...");
						logger.info("from="+UI.address+",  to="+to+",  value="+v);
						
						UI.cmd =1;
						WalletThread transferThread = new WalletThread(1, UI.address, to, v);//������һ�����������߳�
//						System.out.println("----0000 ");
						transferThread.start();//���������߳�
//						System.out.println("----1111 ");
						(new ThreadWaitingDialog(parent, transferThread ,"����ִ�У���ȴ�......")).start();
//						System.out.println("----2222 ");
					}else {
//						System.out.println("������֤ʧ�ܣ�");
						logger.error("������֤ʧ�ܣ�");
						JOptionPane.showMessageDialog(null, "������֤ʧ�ܣ�", "�������",JOptionPane.ERROR_MESSAGE); 
					}
				}else {
					return;
				}				
			}
		});	
		
		btn_cancel.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				tf_ToAdd.setText("");
				tf_Value.setText("");
			}
		});	
		
	}
	
	

}
