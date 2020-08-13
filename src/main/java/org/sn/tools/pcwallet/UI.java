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
	
	public static String address;     //账户地址
	public static String passwd;      // 密钥文件的密码
	public static String privateKey;  // 密钥文件的私钥
	public static Account account;    //用户账户对象
	
	public static WalletApi  wallapi;     // 钱包对象
	public static WalletFile walletFile;  // 钱包文件对象
	
	public static int cmd=0;  //命令类型
	
	//转账交易执行线程的返回值
	public static Boolean ret;
	public static String  txid;
	public static String  code;
	public static String  message;
	
	//转出交易查询结果
	public static Vector<Vector<String>> data;
	
	//public static HashMap<String, Player>  usermap = new HashMap<String, Player>(); 
	public static Image snIcon = Toolkit.getDefaultToolkit().getImage("images/sn-logo.png");
	static{
		//usermap.put("admin", new Player("admin","123456"));
		
	}

}
