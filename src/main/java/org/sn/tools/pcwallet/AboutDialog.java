package org.sn.tools.pcwallet;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

/** * @author  ���� E-mail: 
 * @date ����ʱ�䣺2016��6��16�� ����10:12:19 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  */
public class AboutDialog  extends JDialog{

    Image icon; 
    
	public AboutDialog(JFrame parent) {
		icon = Toolkit.getDefaultToolkit().getImage("images/logo2.png");

		this.setModal(true);  //��Ϊģ̬����

		this.setSize(500, 440);
		this.setTitle("���� ");
		this.setLocationRelativeTo(parent);
		this.setVisible(false); // Ĭ�����أ��������ʾ
		this.setResizable(false);

	}

	// public void paintComponent(Graphics g) {
	public void paint(Graphics g) {
		// TODO �Զ����ɵķ������
		super.paint(g);
		g.drawImage(icon, 10, 40, 380, 110, this);

		g.setFont(new Font("����",Font.PLAIN,32));
		g.drawString("SN PC��Ǯ��", 30,200);
		g.setFont(new Font("΢���ź�",Font.PLAIN,24));
		g.drawString("�汾�� V0.2", 30, 250);
		g.drawString("��Ȩ���У� @�����ȵ�", 30, 300);
		g.drawString("���䣺dev@bithot.org", 30, 330);
		g.drawString("��ַ��https://github.com/BitcoinHot", 30, 360);

	}
}