package org.sn.tools.pcwallet;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

import org.sn.api.GrpcAPI.TransactionListExtention;
import org.sn.keystore.WalletFile;
import org.sn.protos.Protocol.Account;
import org.sn.walletserver.WalletApi;



public class UI {
	public static LoginWin login;
	public static PCWalletMain walletMain;
	
	public static String address;     //�˻���ַ
	public static String passwd;      // ��Կ�ļ�������
	public static String privateKey;  // ��Կ�ļ���˽Կ
	public static Account account;    //�û��˻�����
	
	public static WalletApi  wallapi;     // Ǯ������
	public static WalletFile walletFile;  // Ǯ���ļ�����
	
	public static int cmd=0;  //��������
	
	//ת�˽���ִ���̵߳ķ���ֵ
	public static Boolean ret;
	public static String  txid;
	public static String  code;
	public static String  message;
	
	//ת�����ײ�ѯ���
	public static Vector<Vector<String>> data;
	
	//public static HashMap<String, Player>  usermap = new HashMap<String, Player>(); 
	public static Image snIcon = Toolkit.getDefaultToolkit().getImage("images/sn-logo.png");
	static{
		//usermap.put("admin", new Player("admin","123456"));
		
	}

}
