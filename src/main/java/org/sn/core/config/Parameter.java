package org.sn.core.config;

public interface Parameter {

  interface CommonConstant {
	//��ַǰ׺�ַ�����"T",�ֽ���0x41.
	//��ַǰ׺�ַ�����"B",�ֽ���0x19.  
    //��ַǰ׺�ַ�����"S",�ֽ���0x3F.
    byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x3F;   //3F + address
    byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
    int ADDRESS_SIZE = 21;
  }

}
