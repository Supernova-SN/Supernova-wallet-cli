package org.sn.tools.pcwallet;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

public class ThreadWaitingDialog extends Thread{

    private Thread currentThread = null;   //ʵ�ʵ���ʱ����TestThread�������߳�
    private String messages = "";          //��ʾ�����ʾ��Ϣ
    private JFrame parentFrame = null;     //��ʾ��ĸ�����
    private JDialog clueDiag = null;       // ���߳��������С���ʾ��


    public ThreadWaitingDialog(JFrame parentFrame,Thread currentThread,String messages)
    {
        this.parentFrame = parentFrame;
        this.currentThread = currentThread;

        this.messages= messages;
        initDiag();//��ʼ����ʾ��
    }

    protected void initDiag()
    {
    	Font font = new Font("΢���ź�",Font.PLAIN,20);
        clueDiag = new JDialog(parentFrame,"�ȴ���...",true);
        clueDiag.setSize(new Dimension(500,400));
        clueDiag.setLocationRelativeTo(null);
        clueDiag.setFont(font);        
        clueDiag.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        JPanel panel = (JPanel) clueDiag.getContentPane();
        panel.setLayout(null);
        Image smallImage = UI.snIcon.getScaledInstance(100,100,Image.SCALE_FAST);
        ImageIcon icon = new ImageIcon(smallImage);   
        JLabel lbIcon = new JLabel(icon);        
        lbIcon.setBounds(200, 100, 100, 100); 

        JLabel lbMsg = new JLabel(messages);
        lbMsg.setBounds(150, 230, 200, 50);
        lbMsg.setFont(font);
        panel.add(lbIcon);
        panel.add(lbMsg);
        
        clueDiag.setContentPane(panel);
        clueDiag.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        (new DisposeDiag()).start();//�����ر���ʾ���߳�
    }

    public void run()
    {
//        ��ʾ��ʾ��
    	System.out.println("ThreadWaitingDialog  run ...000 ");
        System.out.println("ThreadWaitingDialog  run ...111 ");
        clueDiag.setVisible(true);
        System.out.println("ThreadWaitingDialog  run ...222 ");
        System.out.println("UI.ret="+UI.ret);
        
        //��ʾִ���߳����н��
        if(UI.ret==true) {
        	if(UI.cmd ==1) {
        		UI.walletMain.userInfoPanel.btn_cancel.doClick();
        	            	    
        	    JTextArea tx = new JTextArea();
        		tx.setPreferredSize(new Dimension(200,100));
        		tx.setLineWrap(true);        //�����Զ����й��� 
        		tx.setText(UI.txid);
        		Object[] message = {"ִ�гɹ�, txid:", tx};
        		JOptionPane.showMessageDialog(null, message, "ִ�гɹ�", JOptionPane.INFORMATION_MESSAGE, null);
        		
        	}else if(UI.cmd ==2) {
        		UI.walletMain.transOutPanel.refreshTable();
        		JOptionPane.showMessageDialog(null, "ִ�гɹ�");
        	}else if(UI.cmd ==3) {
        		UI.walletMain.transInPanel.refreshTable();
        		JOptionPane.showMessageDialog(null, "ִ�гɹ�");
        	}          	
        }else {
        	if(UI.cmd ==1) {
        		UI.walletMain.userInfoPanel.btn_cancel.doClick();
        		JOptionPane.showMessageDialog(null, 
	                      "ִ��ʧ�ܣ�\ncode="+UI.code+"\nmessage="+UI.message,
	                      "ִ��ʧ��",
	                      JOptionPane.ERROR_MESSAGE);  
        	}else if(UI.cmd ==2) {
        		//JOptionPane.showMessageDialog(null, "ִ��ʧ��");
        	}else if(UI.cmd ==3) {
        		//JOptionPane.showMessageDialog(null, "ִ��ʧ��");
        	}        	
        }
        
    }

    class DisposeDiag extends Thread
    {
        public void run()
        {
            try
            {
            	System.out.println("DisposeDiag  run ...");
                currentThread.join();//�ȴ��������߳̽���
            }catch(InterruptedException e){
                System.out.println("Exception:" + e);
            }
            System.out.println("DisposeDiag  run ... end");
            clueDiag.dispose();//�ر���ʾ��
            System.out.println("DisposeDiag  run ... end  11111");
        }
    } 
}