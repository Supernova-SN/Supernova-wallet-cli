package org.sn.tools.pcwallet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.sn.api.GrpcAPI.EasyTransferResponse;
import org.sn.api.GrpcAPI.TransactionExtention;
import org.sn.api.GrpcAPI.TransactionListExtention;
import org.sn.common.utils.ByteArray;
import org.sn.common.utils.Utils;
import org.sn.protos.Contract.TransferContract;
import org.sn.protos.Protocol.Transaction;
import org.sn.protos.Protocol.Transaction.Contract;
import org.sn.protos.Protocol.Transaction.Contract.ContractType;
import org.sn.walletserver.WalletApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletThread extends Thread {
	private static final Logger logger = LoggerFactory.getLogger("UserInfoPanel");
	
	int cmd; // 命令类型
	String from;
	String to;
	long value;
	int offset;
	int limit;

	public WalletThread(int cmd, String from, String to, long value) {
		this.cmd = cmd;
		this.from = from;
		this.to = to;
		this.value = value;
	}

	public WalletThread(int cmd, String from, int offset, int limit) {
		this.cmd = cmd;
		this.from = from;
		this.offset = offset;
		this.limit = limit;
	}

	public void run() {
		if (cmd == 1) {
			transfer(from, to, value);
		} else if (cmd == 2) {
			querryTransOut(from, offset, limit);
		} else if (cmd == 3) {
			querryTransIn(from, offset, limit);
		}
	}

	// 转账交易
	public void transfer(String from, String to, long value) {
		byte[] addrFrom = WalletApi.decodeFromBase58Check(from);
		byte[] addrTo = WalletApi.decodeFromBase58Check(to);

//		System.out.println("transfer start...");
		logger.info("transfer start...");

		EasyTransferResponse response = WalletApi.easyTransferByPrivate(ByteArray.fromHexString(UI.privateKey), addrTo,
				value);
//		System.out.println("transfer start... 111");
		UI.ret = response.getResult().getResult();
		UI.txid = ByteArray.toHexString(response.getTxid().toByteArray());
		if (UI.ret == false) {
//			System.out.println("Easy transfer failed!!!");
			logger.error("Easy transfer failed!!!");
			
			UI.code = response.getResult().getCode().toString();
			UI.message = response.getResult().getMessage().toStringUtf8();
//			System.out.println("Code = " + UI.code);
//			System.out.println("Message = " + UI.message);
			logger.error("Code = " + UI.code);
			logger.error("Message = " + UI.message);
			
		} else {
			System.out.println("Easy transfer successful!!!");
			logger.info("Easy transfer successful!!!");
			
			Transaction transaction = response.getTransaction();
//			System.out.println(Utils.printTransaction(transaction));
			logger.info(Utils.printTransaction(transaction));
		}
//		System.out.println("transfer start... 3333");

	}

	// 查询转出交易
	public void querryTransOut(String from, int offset, int limit) {
		byte[] addrFrom = WalletApi.decodeFromBase58Check(from);

//		System.out.println("querryTransOut start...");
		logger.info("querryTransOut start...");
		
		Vector data = new Vector();

		Optional<TransactionListExtention> result = WalletApi.getTransactionsFromThis2(addrFrom, offset, limit);
		if (result.isPresent()) {
//			System.out.println("GetTransactionsFromThis success !");
			logger.info("GetTransactionsFromThis success !");
			
			TransactionListExtention transactionList = result.get();
			if (transactionList.getTransactionCount() == 0) {
//				System.out.println("No transaction from " + from);
				logger.info("No transaction from " + from);
				UI.ret = false;
				JOptionPane.showMessageDialog(null, "转出记录为空");
				return;
			}
			System.out.println(Utils.printTransactionList(transactionList));
			UI.ret = true;

			int count = transactionList.getTransactionCount();
			List<TransactionExtention> list = transactionList.getTransactionList();
			for (TransactionExtention transactionEx : list) {
				Transaction transaction = transactionEx.getTransaction();
				Transaction.raw raw = transaction.getRawData();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timestamp = sdf.format(raw.getTimestamp());
				// Date timestamp = new Date(raw.getTimestamp());
				try {
					if (raw.getContractCount() > 0) {
						List<Contract> contractList = raw.getContractList();
						for (Contract contract : contractList) {
							if (contract.getType() == ContractType.TransferContract) {
								// String f = contract.getowner_address();
								TransferContract transferContract = contract.getParameter()
										.unpack(TransferContract.class);
								String owner_address = WalletApi
										.encode58Check(transferContract.getOwnerAddress().toByteArray());
								String to_address = WalletApi
										.encode58Check(transferContract.getToAddress().toByteArray());
								long amount = transferContract.getAmount();
								double fv = amount / 1000000.0D;
								NumberFormat formatter = new DecimalFormat("0.000000");
								String value = formatter.format(fv);

								String txid = ByteArray.toHexString(transactionEx.getTxid().toByteArray());
								// 添加一条转出记录
								Vector row = new Vector();
								row.add(txid);
								row.add(to_address);
								row.add(value);
								row.add(timestamp);
								data.add(row);
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}

			}
		} else {
			UI.ret = false;
//			System.out.println("GetTransactionsFromThis failed !");
			logger.info("GetTransactionsFromThis failed !");
		}
		UI.data = data;
//		System.out.println("querryTransOut start... end");
//		System.out.println(UI.data);
		logger.info(UI.data.toString());
	}

	// 查询转入交易
	public void querryTransIn(String to, int offset, int limit) {
		byte[] addr = WalletApi.decodeFromBase58Check(to);

//		System.out.println("querryTransIn start...");
		logger.info("querryTransIn start...");
		Vector data = new Vector();

		Optional<TransactionListExtention> result = WalletApi.getTransactionsToThis2(addr, offset, limit);
		if (result.isPresent()) {
//			System.out.println("GetTransactionsToThis " + " success !!");
			logger.info("GetTransactionsToThis success !");
			
			TransactionListExtention transactionList = result.get();
			if (transactionList.getTransactionCount() == 0) {
				System.out.println("No transaction from " + from);
				UI.ret = false;
				JOptionPane.showMessageDialog(null, "转入记录为空");
				return;
			}
			System.out.println(Utils.printTransactionList(transactionList));
			UI.ret = true;

			int count = transactionList.getTransactionCount();
			List<TransactionExtention> list = transactionList.getTransactionList();
			for (TransactionExtention transactionEx : list) {
				Transaction transaction = transactionEx.getTransaction();
				Transaction.raw raw = transaction.getRawData();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timestamp = sdf.format(raw.getTimestamp());
				// Date timestamp = new Date(raw.getTimestamp());
				try {
					if (raw.getContractCount() > 0) {
						List<Contract> contractList = raw.getContractList();
						for (Contract contract : contractList) {
							if (contract.getType() == ContractType.TransferContract) {
								// String f = contract.getowner_address();
								TransferContract transferContract = contract.getParameter()
										.unpack(TransferContract.class);
								String owner_address = WalletApi
										.encode58Check(transferContract.getOwnerAddress().toByteArray());
								String to_address = WalletApi
										.encode58Check(transferContract.getToAddress().toByteArray());
								long amount = transferContract.getAmount();
								double fv = amount / 1000000.0D;
								NumberFormat formatter = new DecimalFormat("0.000000");
								String value = formatter.format(fv);

								String txid = ByteArray.toHexString(transactionEx.getTxid().toByteArray());
								// 添加一条转出记录
								Vector row = new Vector();
								row.add(txid);
								row.add(owner_address);
								row.add(value);
								row.add(timestamp);
								data.add(row);
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}

			}
		} else {
			UI.ret = false;
//			System.out.println("GetTransactionsToThis failed !");
			logger.info("GetTransactionsToThis failed !");
		}
		UI.data = data;
//		System.out.println("querryTransIn start... end");
//		System.out.println(UI.data);
		logger.info("querryTransIn start... end");
		logger.info(UI.data.toString());
	}

}
