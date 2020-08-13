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
		
		Font font = new Font("微软雅黑",Font.PLAIN,24);
		
		groupbox = new JPanel();
		groupbox.setLayout(null);
		groupbox.setPreferredSize(new Dimension(780, 150));
		groupbox.setBounds(10, 10, 970, 150);
		Border border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE);
		groupbox.setBorder(BorderFactory.createTitledBorder(border,""));   //添加边框文字
		this.add(groupbox);
		
		
		JLabel lbAddress = new JLabel("账户地址");
		lbAddress.setBounds(30, 30, 150, 50);
		lbAddress.setFont(font);
		JLabel lbAdd = new JLabel(UI.address);
		lbAdd.setBounds(180, 30, 720, 50);
		lbAdd.setFont(font);
		
		JLabel lbBalance = new JLabel("余额");
		lbBalance.setBounds(30, 80, 150, 50);
		lbBalance.setFont(font);
		JTextField tf_Balance  = new JTextField(34);
		tf_Balance.setBounds(180, 80, 680, 50);
		tf_Balance.setFont(font);
		tf_Balance.setEditable(false);
		tf_Balance.setText("---");
		
		JButton jbBalance = new JButton("查询");
		jbBalance.setBounds(860, 80, 90, 50);
		jbBalance.setFont(font);	
		
		JLabel lbToAddress = new JLabel("转出地址");
		lbToAddress.setBounds(30, 200, 150, 50);
		lbToAddress.setFont(font);		
		JTextField tf_ToAdd  = new JTextField(34);
		tf_ToAdd.setBounds(180, 200, 720, 50);
		tf_ToAdd.setFont(font);
		
		JLabel lbValue = new JLabel("转出金额");
		lbValue.setBounds(30, 260, 150, 50);
		lbValue.setFont(font);		
		JTextField tf_Value  = new JTextField(34);
		tf_Value.setBounds(180, 260, 720, 50);
		tf_Value.setFont(font);
		JLabel lbSn = new JLabel("SN");
		lbSn.setBounds(900, 260, 100, 50);
		lbSn.setFont(font);
		
		btn_transfer = new JButton("转账");
		btn_transfer.setBounds(580, 500, 150, 50);
		btn_transfer.setFont(font);	
		btn_cancel = new JButton("取消");
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
		
		//查询余额
		jbBalance.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {				
				//查询余额
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
					JOptionPane.showMessageDialog(null, "请输入转出地址");
					return;
				}
				byte[] tobyte = WalletApi.decode58Check(to);
				if (tobyte==null) {
					JOptionPane.showMessageDialog(null, "输入的地址不符");
					return;
				};
				String value = tf_Value.getText();
				if (value.trim().equals("")) {
					JOptionPane.showMessageDialog(null, "请输入金额");
					return;
				}
				Double fv;
				try {
					fv = Double.valueOf(value);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null, "金额必须为数字");
					return;
				}
				long v = (long) (fv*1000000);
				if (v<=0) {
					JOptionPane.showMessageDialog(null, "金额不能小于0.000001");
					return;
				}
				
				//弹出密码输入框
				String passwd;
				JPasswordField pwd = new JPasswordField();
				pwd.setEchoChar('*');
				Object[] message = {"输入密码:", pwd};
				int res = JOptionPane.showConfirmDialog(parent, message, "输入密码", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				//System.out.println("pwd="+new String(pwd.getPassword()));
				if(res==0) {
					passwd = new String(pwd.getPassword());
					//加载密钥文件，验证密码
					if(passwd.equals(UI.passwd)) {
//						System.out.println("密码验证成功！");
//						System.out.println("Easy transfer Thread start...");
						System.out.println("from="+UI.address+",  to="+to+",  value="+v);
						logger.info("密码验证成功！");
						logger.info("Easy transfer Thread start...");
						logger.info("from="+UI.address+",  to="+to+",  value="+v);
						
						UI.cmd =1;
						WalletThread transferThread = new WalletThread(1, UI.address, to, v);//新生成一个处理事务线程
//						System.out.println("----0000 ");
						transferThread.start();//启动事务线程
//						System.out.println("----1111 ");
						(new ThreadWaitingDialog(parent, transferThread ,"正在执行，请等待......")).start();
//						System.out.println("----2222 ");
					}else {
//						System.out.println("密码验证失败！");
						logger.error("密码验证失败！");
						JOptionPane.showMessageDialog(null, "密码验证失败！", "密码错误",JOptionPane.ERROR_MESSAGE); 
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
