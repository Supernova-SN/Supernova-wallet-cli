package org.sn.tools.pcwallet;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PCWalletMain  extends JFrame {
	private static final Logger logger = LoggerFactory.getLogger("PCWalletMain");
	
	UserInfoPanel userInfoPanel;
	TransInPanel  transInPanel;
	TransOutPanel transOutPanel;
    
	public PCWalletMain() {
		this.setSize(1000, 700); // 移动并设置窗口大小
		this.setLayout(null);
		
		this.setTitle("SN 比特热点PC版钱包");
		Image icon = this.getToolkit().getImage("images/sn-logo.png");
		this.setIconImage(icon);
		
		// 创建菜单
		initMenuBar();
		
		userInfoPanel = new UserInfoPanel(this);
//		userInfoPanel.setVisible(false);
		transInPanel    = new TransInPanel(this);
		transInPanel.setVisible(false);
		transOutPanel    = new TransOutPanel(this);
		transOutPanel.setVisible(false);
		this.add(userInfoPanel);
		this.add(transInPanel);
		this.add(transOutPanel);
		
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// 创建菜单
	public void initMenuBar() {
		Font font = new Font("微软雅黑",Font.PLAIN,20);
		
		JMenuBar menubar = new JMenuBar();
		this.setJMenuBar(menubar);		

		JMenu jmUser;     // 用户
		JMenuItem jmiUser_info;  //转账
		JMenuItem jmiUser_exit;  // 退出
		
		JMenu jmTransactions;    // 交易记录
		JMenuItem jmiTrans_Out;  //转出记录
		JMenuItem jmiTrans_In;   //转入记录

		JMenu jmHelp;            // 帮助
		JMenuItem jmiHelp_about; // 关于

		// ----创建User菜单------------
		jmUser = new JMenu("  用户  "); 
		jmiUser_info = new JMenuItem("   转账   ");
		jmiUser_exit = new JMenuItem("   退出   ");
		jmUser.setFont(font);
		jmiUser_info.setFont(font);
		jmiUser_exit.setFont(font);
		
		jmUser.add(jmiUser_info);
		jmUser.add(jmiUser_exit);
		menubar.add(jmUser); 
		
		
		jmTransactions = new JMenu("  交易记录  "); 
		jmiTrans_Out = new JMenuItem("  转出记录  ");
		jmiTrans_In = new JMenuItem("  转入记录  ");
		jmTransactions.setFont(font);
		jmiTrans_Out.setFont(font);
		jmiTrans_In.setFont(font);
		jmTransactions.add(jmiTrans_Out);
		jmTransactions.add(jmiTrans_In);
		menubar.add(jmTransactions); 

		// ------创建帮助菜单----------------
		jmHelp = new JMenu("  帮助  "); // 创建“帮助”菜单
		jmiHelp_about = new JMenuItem("  关于  ");
		jmHelp.setFont(font);		
		jmiHelp_about.setFont(font);		
		jmHelp.add(jmiHelp_about);
		menubar.add(jmHelp);       // 菜单添加到 菜单栏
		
		// ---------  设置事件监听器 ----------------------
		
		//显示交易记录页
		jmiTrans_Out.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("jmiTrans_Out...");
				logger.info("jmiTrans_Out...");
				
				userInfoPanel.setVisible(false);
				transInPanel.setVisible(false);
				transOutPanel.setVisible(true);
				PCWalletMain.this.repaint();						
			}
		});
		
		jmiTrans_In.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("jmiTrans_In...");
				logger.info("jmiTrans_In...");
				
				userInfoPanel.setVisible(false);
				transOutPanel.setVisible(false);
				transInPanel.setVisible(true);
				PCWalletMain.this.repaint();						
			}
		});
		
		//显示用户转账页
		jmiUser_info.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("jmiUser_info...");
				logger.info("jmiUser_info...");
				
				userInfoPanel.setVisible(true);
				transOutPanel.setVisible(false);
				transInPanel.setVisible(false);
				PCWalletMain.this.repaint();
						
			}
		});
		
		jmiUser_exit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println(gc.mouse.createNewPos());
				System.exit(0);
			}
		});
		
		jmiHelp_about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println(gc.mouse.createNewPos());
				AboutDialog aboutDialog = new AboutDialog(PCWalletMain.this);
				aboutDialog.setVisible(true); 
			}
		});
		
		
		
	}

}
