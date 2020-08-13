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

    private Thread currentThread = null;   //实际调用时就是TestThread事务处理线程
    private String messages = "";          //提示框的提示信息
    private JFrame parentFrame = null;     //提示框的父窗体
    private JDialog clueDiag = null;       // “线程正在运行”提示框


    public ThreadWaitingDialog(JFrame parentFrame,Thread currentThread,String messages)
    {
        this.parentFrame = parentFrame;
        this.currentThread = currentThread;

        this.messages= messages;
        initDiag();//初始化提示框
    }

    protected void initDiag()
    {
    	Font font = new Font("微软雅黑",Font.PLAIN,20);
        clueDiag = new JDialog(parentFrame,"等待中...",true);
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
        
        (new DisposeDiag()).start();//启动关闭提示框线程
    }

    public void run()
    {
//        显示提示框
    	System.out.println("ThreadWaitingDialog  run ...000 ");
        System.out.println("ThreadWaitingDialog  run ...111 ");
        clueDiag.setVisible(true);
        System.out.println("ThreadWaitingDialog  run ...222 ");
        System.out.println("UI.ret="+UI.ret);
        
        //提示执行线程运行结果
        if(UI.ret==true) {
        	if(UI.cmd ==1) {
        		UI.walletMain.userInfoPanel.btn_cancel.doClick();
        	            	    
        	    JTextArea tx = new JTextArea();
        		tx.setPreferredSize(new Dimension(200,100));
        		tx.setLineWrap(true);        //激活自动换行功能 
        		tx.setText(UI.txid);
        		Object[] message = {"执行成功, txid:", tx};
        		JOptionPane.showMessageDialog(null, message, "执行成功", JOptionPane.INFORMATION_MESSAGE, null);
        		
        	}else if(UI.cmd ==2) {
        		UI.walletMain.transOutPanel.refreshTable();
        		JOptionPane.showMessageDialog(null, "执行成功");
        	}else if(UI.cmd ==3) {
        		UI.walletMain.transInPanel.refreshTable();
        		JOptionPane.showMessageDialog(null, "执行成功");
        	}          	
        }else {
        	if(UI.cmd ==1) {
        		UI.walletMain.userInfoPanel.btn_cancel.doClick();
        		JOptionPane.showMessageDialog(null, 
	                      "执行失败，\ncode="+UI.code+"\nmessage="+UI.message,
	                      "执行失败",
	                      JOptionPane.ERROR_MESSAGE);  
        	}else if(UI.cmd ==2) {
        		//JOptionPane.showMessageDialog(null, "执行失败");
        	}else if(UI.cmd ==3) {
        		//JOptionPane.showMessageDialog(null, "执行失败");
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
                currentThread.join();//等待事务处理线程结束
            }catch(InterruptedException e){
                System.out.println("Exception:" + e);
            }
            System.out.println("DisposeDiag  run ... end");
            clueDiag.dispose();//关闭提示框
            System.out.println("DisposeDiag  run ... end  11111");
        }
    } 
}