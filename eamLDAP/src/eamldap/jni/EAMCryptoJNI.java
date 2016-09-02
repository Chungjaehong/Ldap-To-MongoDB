package eamldap.jni;

import java.lang.*;
import java.util.*;
import java.io.*;

public class EAMCryptoJNI
{	
	private byte[] bServerCertFilePath = null;
	private byte[] bServerKeyFilePath = null;
	private byte[] bCaCertFilePath = null;
	private byte[] bServerKeyPassword = null;
	
	static {
		try {
			System.loadLibrary("jreEAMCrypto");
		}
		catch (SecurityException e)
		{
			System.out.println("[EAMCryptoJNI]: security exception occurred. loading library failed.");
		}
		catch (UnsatisfiedLinkError e)
		{
			System.out.println("[EAMCryptoJNI]: unsatisfied link error. loading library failed.");
			System.out.println(e.getMessage());
		}
	}
    
    /**
    	생성자
    */    
	public EAMCryptoJNI()
	{
	}

	/**
		초기화 함수
		@param	bServerCertFilePath		PS서버 인증서File Path
		@param	bServerKeyFilePath		PS서버 개인키File Path
		@param	bCaCertFilePath			인증기관 인증서File Path
		@param	bServerKeyPassword		PS서버 개인키 비밀번호
	*/
	public native void native_init(byte[] bServerCertFilePath,
						byte[] bServerKeyFilePath,
						byte[] bCaCertFilePath,
						byte[] bServerKeyPassword);
						
	/**
		PS인증서로 암호화하는 함수
		@param	decryptedData	암호화할 Data
		@return decryptedData	암호화된 Data[Base64 Encoding되어 있슴]
	*/
	public native byte[] native_encrypt(byte[] decryptedData);
	
	/**
		PS인증서로 복호화하는 함수
		@param	encryptedData	복호화할 Data[Base64 Encoding되어 있는 Data]
		@return encryptedData	복호화된 Data
	*/
	public native byte[] native_decrypt(byte[] encryptedData);
}