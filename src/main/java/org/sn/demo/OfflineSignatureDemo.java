package org.sn.demo;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Arrays;
import org.sn.api.GrpcAPI.Return;
import org.sn.api.GrpcAPI.TransactionExtention;
import org.sn.common.crypto.ECKey;
import org.sn.common.crypto.Sha256Hash;
import org.sn.common.utils.ByteArray;
import org.sn.common.utils.TransactionUtils;
import org.sn.core.exception.CancelException;
import org.sn.protos.Contract;
import org.sn.protos.Protocol.Block;
import org.sn.protos.Protocol.Transaction;
import org.sn.walletserver.WalletApi;

public class OfflineSignatureDemo {

  public static Transaction setReference(Transaction transaction, Block newestBlock) {
    long blockHeight = newestBlock.getBlockHeader().getRawData().getNumber();
    byte[] blockHash = getBlockHash(newestBlock).getBytes();
    byte[] refBlockNum = ByteArray.fromLong(blockHeight);
    Transaction.raw rawData = transaction.getRawData().toBuilder()
        .setRefBlockHash(ByteString.copyFrom(ByteArray.subArray(blockHash, 8, 16)))
        .setRefBlockBytes(ByteString.copyFrom(ByteArray.subArray(refBlockNum, 6, 8)))
        .build();
    return transaction.toBuilder().setRawData(rawData).build();
  }

  public static Sha256Hash getBlockHash(Block block) {
    return Sha256Hash.of(block.getBlockHeader().getRawData().toByteArray());
  }

  public static String getTransactionHash(Transaction transaction) {
    String txid = ByteArray.toHexString(Sha256Hash.hash(transaction.getRawData().toByteArray()));
    return txid;
  }


  public static Transaction createTransaction(byte[] from, byte[] to, long amount) {
    Transaction.Builder transactionBuilder = Transaction.newBuilder();
    Block newestBlock = WalletApi.getBlock(-1);
    System.out.println("newestBlock=");
    System.out.println(newestBlock);

    Transaction.Contract.Builder contractBuilder = Transaction.Contract.newBuilder();
    Contract.TransferContract.Builder transferContractBuilder = Contract.TransferContract
        .newBuilder();
    transferContractBuilder.setAmount(amount);
    ByteString bsTo = ByteString.copyFrom(to);
    ByteString bsOwner = ByteString.copyFrom(from);
    transferContractBuilder.setToAddress(bsTo);
    transferContractBuilder.setOwnerAddress(bsOwner);
    try {
      Any any = Any.pack(transferContractBuilder.build());
      contractBuilder.setParameter(any);
    } catch (Exception e) {
      return null;
    }
    contractBuilder.setType(Transaction.Contract.ContractType.TransferContract);
    transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
        .setTimestamp(System.currentTimeMillis())
        .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000);
    Transaction transaction = transactionBuilder.build();
    Transaction refTransaction = setReference(transaction, newestBlock);
    return refTransaction;
  }


  private static byte[] signTransaction2Byte(byte[] transaction, byte[] privateKey)
      throws InvalidProtocolBufferException {
    ECKey ecKey = ECKey.fromPrivate(privateKey);
    Transaction transaction1 = Transaction.parseFrom(transaction);
    byte[] rawdata = transaction1.getRawData().toByteArray();
    byte[] hash = Sha256Hash.hash(rawdata);
    byte[] sign = ecKey.sign(hash).toByteArray();
    System.out.println("sign="+ByteArray.toHexString(sign));
    
    return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build().toByteArray();
  }

  private static Transaction signTransaction2Object(byte[] transaction, byte[] privateKey)
      throws InvalidProtocolBufferException {
    ECKey ecKey = ECKey.fromPrivate(privateKey);
    Transaction transaction1 = Transaction.parseFrom(transaction);
    byte[] rawdata = transaction1.getRawData().toByteArray();
    byte[] hash = Sha256Hash.hash(rawdata);
    byte[] sign = ecKey.sign(hash).toByteArray();
    return transaction1.toBuilder().addSignature(ByteString.copyFrom(sign)).build();
  }

  private static boolean broadcast(byte[] transactionBytes) throws InvalidProtocolBufferException {
    return WalletApi.broadcastTransaction(transactionBytes);
  }

  private static void base58checkToHexString() {
    String base58check = "TGehVcNhud84JDCGrNHKVz9jEAVKUpbuiv";
    String hexString = ByteArray.toHexString(WalletApi.decodeFromBase58Check(base58check));
    System.out.println(hexString);
  }

  private static void hexStringTobase58check() {
    String hexString = "414948c2e8a756d9437037dcd8c7e0c73d560ca38d";
    String base58check = WalletApi.encode58Check(ByteArray.fromHexString(hexString));
    System.out.println(base58check);
  }

  public static void main(String[] args) throws InvalidProtocolBufferException, CancelException {
    String privateStr = "3fe705248bfbc71c3ab172aeb220b53aed988a5838e9478bd27ce4a8c65f2f6a";
    byte[] privateBytes = ByteArray.fromHexString(privateStr);
    
    byte[] from = WalletApi.decodeFromBase58Check("BHzkmSnaCZ7unyexShpwVBWv2FmecGDFNN");
    byte[] to = WalletApi.decodeFromBase58Check("B5ZFTu2gYgjQXENsU1L9f9pbE6jG5dTKg4");
    long amount = 20_000_000L; //100 SN, api only receive sn in drop, and 1 sn = 1000000 drop
    
//    Transaction transaction = createTransaction(from, to, amount);
//    byte[] transactionBytes = transaction.toByteArray();
    String trans = "0a86010a027f5b220868d12e0303c46f1040a0ebe891842e5a68080112640a2d747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e5472616e73666572436f6e747261637412330a151994973611cbf9a0a9aeb605f44249f5eaf68ecc981215190c22d79c37176df5cff31600facac21d5022faf11880dac40970eba0e591842e";
    byte[] transactionBytes = ByteArray.fromHexString(trans);
    System.out.println("transactionBytes ::::: " + ByteArray.toHexString(transactionBytes));
    

    //sign a transaction in byte format and return a Transaction in byte format
    byte[] transaction4 = signTransaction2Byte(transactionBytes, privateBytes);
    System.out.println("transaction4 ::::: " + ByteArray.toHexString(transaction4));
 
//    boolean result = broadcast(transaction4);

//    System.out.println(result);
  }
}
