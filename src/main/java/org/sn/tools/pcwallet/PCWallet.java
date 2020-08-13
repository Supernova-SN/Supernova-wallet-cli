package org.sn.tools.pcwallet;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.DimensionUIResource;

public class PCWallet {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//统一设置弹出框的字体
		Font font = new Font("微软雅黑",Font.PLAIN,20);		
	    UIManager.put("OptionPane.font", font);
	    UIManager.put("OptionPane.messageFont", font);
	    UIManager.put("OptionPane.buttonFont", font);
	    UIManager.put("List.font", font);
	    UIManager.put("Label.font", font);
	    UIManager.put("TextField.font", font);
	    UIManager.put("PasswordField.font", font);
	    UIManager.put("TextArea.font", font);
	    UIManager.put("ComboBox.font", font);
	    UIManager.put("Button.font", font);
	    UIManager.put("ComboBox.font", font);
	    
	    UIManager.put("OptionPane.minimumSize", new DimensionUIResource(450, 200));
	    
	    
		UI.login = new LoginWin();
		UI.login.setVisible(true);
		
	}

}
