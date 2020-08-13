package org.sn.tools.pcwallet;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

/** * @author  作者 E-mail: 
 * @date 创建时间：2016年6月16日 下午10:12:19 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  */
public class AboutDialog  extends JDialog{

    Image icon; 
    
	public AboutDialog(JFrame parent) {
		icon = Toolkit.getDefaultToolkit().getImage("images/logo2.png");

		this.setModal(true);  //设为模态窗口

		this.setSize(500, 440);
		this.setTitle("关于 ");
		this.setLocationRelativeTo(parent);
		this.setVisible(false); // 默认隐藏，点击后显示
		this.setResizable(false);

	}

	// public void paintComponent(Graphics g) {
	public void paint(Graphics g) {
		// TODO 自动生成的方法存根
		super.paint(g);
		g.drawImage(icon, 10, 40, 380, 110, this);

		g.setFont(new Font("黑体",Font.PLAIN,32));
		g.drawString("SN PC版钱包", 30,200);
		g.setFont(new Font("微软雅黑",Font.PLAIN,24));
		g.drawString("版本： V0.2", 30, 250);
		g.drawString("版权所有： @比特热点", 30, 300);
		g.drawString("邮箱：dev@bithot.org", 30, 330);
		g.drawString("网址：https://github.com/BitcoinHot", 30, 360);

	}
}