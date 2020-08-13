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
		this.setSize(1000, 700); // �ƶ������ô��ڴ�С
		this.setLayout(null);
		
		this.setTitle("SN �����ȵ�PC��Ǯ��");
		Image icon = this.getToolkit().getImage("images/sn-logo.png");
		this.setIconImage(icon);
		
		// �����˵�
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
	
	// �����˵�
	public void initMenuBar() {
		Font font = new Font("΢���ź�",Font.PLAIN,20);
		
		JMenuBar menubar = new JMenuBar();
		this.setJMenuBar(menubar);		

		JMenu jmUser;     // �û�
		JMenuItem jmiUser_info;  //ת��
		JMenuItem jmiUser_exit;  // �˳�
		
		JMenu jmTransactions;    // ���׼�¼
		JMenuItem jmiTrans_Out;  //ת����¼
		JMenuItem jmiTrans_In;   //ת���¼

		JMenu jmHelp;            // ����
		JMenuItem jmiHelp_about; // ����

		// ----����User�˵�------------
		jmUser = new JMenu("  �û�  "); 
		jmiUser_info = new JMenuItem("   ת��   ");
		jmiUser_exit = new JMenuItem("   �˳�   ");
		jmUser.setFont(font);
		jmiUser_info.setFont(font);
		jmiUser_exit.setFont(font);
		
		jmUser.add(jmiUser_info);
		jmUser.add(jmiUser_exit);
		menubar.add(jmUser); 
		
		
		jmTransactions = new JMenu("  ���׼�¼  "); 
		jmiTrans_Out = new JMenuItem("  ת����¼  ");
		jmiTrans_In = new JMenuItem("  ת���¼  ");
		jmTransactions.setFont(font);
		jmiTrans_Out.setFont(font);
		jmiTrans_In.setFont(font);
		jmTransactions.add(jmiTrans_Out);
		jmTransactions.add(jmiTrans_In);
		menubar.add(jmTransactions); 

		// ------���������˵�----------------
		jmHelp = new JMenu("  ����  "); // �������������˵�
		jmiHelp_about = new JMenuItem("  ����  ");
		jmHelp.setFont(font);		
		jmiHelp_about.setFont(font);		
		jmHelp.add(jmiHelp_about);
		menubar.add(jmHelp);       // �˵���ӵ� �˵���
		
		// ---------  �����¼������� ----------------------
		
		//��ʾ���׼�¼ҳ
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
		
		//��ʾ�û�ת��ҳ
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
