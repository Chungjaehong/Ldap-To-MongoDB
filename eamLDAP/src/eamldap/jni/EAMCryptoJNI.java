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
    	������
    */    
	public EAMCryptoJNI()
	{
	}

	/**
		�ʱ�ȭ �Լ�
		@param	bServerCertFilePath		PS���� ������File Path
		@param	bServerKeyFilePath		PS���� ����ŰFile Path
		@param	bCaCertFilePath			������� ������File Path
		@param	bServerKeyPassword		PS���� ����Ű ��й�ȣ
	*/
	public native void native_init(byte[] bServerCertFilePath,
						byte[] bServerKeyFilePath,
						byte[] bCaCertFilePath,
						byte[] bServerKeyPassword);
						
	/**
		PS�������� ��ȣȭ�ϴ� �Լ�
		@param	decryptedData	��ȣȭ�� Data
		@return decryptedData	��ȣȭ�� Data[Base64 Encoding�Ǿ� �ֽ�]
	*/
	public native byte[] native_encrypt(byte[] decryptedData);
	
	/**
		PS�������� ��ȣȭ�ϴ� �Լ�
		@param	encryptedData	��ȣȭ�� Data[Base64 Encoding�Ǿ� �ִ� Data]
		@return encryptedData	��ȣȭ�� Data
	*/
	public native byte[] native_decrypt(byte[] encryptedData);
}