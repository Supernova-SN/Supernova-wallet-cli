package org.sn.core.config;

public interface Parameter {

  interface CommonConstant {
	//地址前缀字符串是"T",字节是0x41.
	//地址前缀字符串是"B",字节是0x19.  
    //地址前缀字符串是"S",字节是0x3F.
    byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x3F;   //3F + address
    byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
    int ADDRESS_SIZE = 21;
  }

}
