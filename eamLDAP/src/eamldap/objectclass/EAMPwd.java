package eamldap.objectclass;

import	eamldap.objectclass.*;
import	eamldap.attribute.*;
import java.util.Date;
import java.security.MessageDigest;

/**
	EAM의 ssoPwdHistory, ssoPwd관련하여 비밀번호생성 setPassword(),<br>
	보관용도EAMPwd(String pwd),setString(String pwd)로 사용하면 좋습니다.<br>

	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMPwd.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX,Windows<br>
	Compiler	:	javac<br>
	History		:	20050329 Creation.<br>
	TAB Length	: 	4 Byte.<br>
<br>
*/
public class EAMPwd
{
	EAMDate	changeTime	= null ;
	byte[]	pwd 		= null ;
	String	type		= null ;
	
	/** 평문방식 		*/	public static final String ALGO_PLAIN	= "00" ;
	/** SHA1 해쉬방식	*/	public static final String ALGO_SHA1	= "01" ;
	/** MD5	해쉬방식	*/	public static final String ALGO_MD5		= "03" ;
	
	/** Default 생성자							*/	public EAMPwd() 			{							}
	/** Hexa Encoding된 비밀번호에 의한 생성자	*/	public EAMPwd(String pwd)	{	this.setString(pwd) ;	}
	
	/**
		Hexa Encoding된 비밀번호 Set하기
		@param	pwd	Hexa Encoding된 비밀번호
	*/
	public void setString(String pwd)
	{
		if(pwd == null)			return ;
		if(pwd.length() < 2)	return ;
		
		if(pwd.charAt(0) == '0')	// HEXA Encoded ssoPwd
		{
			if(pwd.length() >= 2)
			{
				this.type		= pwd.substring(0,2) ;
				this.pwd		= this.setPwd(decodeHex(pwd.substring(2))) ;
			}
		}
		if(pwd.charAt(0) == '3')	// HEXA Encoded ssoPwdHistory
		{
			if(pwd.length() >= 22)
			{
				this.changeTime	= new EAMDate(new String(decodeHex(pwd.substring(0,20)))) ;
				this.type		= pwd.substring(20,22) ;
				this.pwd		= this.setPwd(decodeHex(pwd.substring(22))) ;
			}
		}
	}

	/**
		PwdHistory의 비밀번호 생성일자넣기
		@param	time	비밀번호생성일자
	*/
	public void setDate(EAMDate time)
	{
		this.changeTime = time ;
	}

	/**
		비밀번호 암호화(해쉬)하여 Set하기
		@param	type	비밀번호 암호화(해쉬)방식
		@param	pwd		비밀번호
	*/
	public int setPassword(String type, String pwd)		
	{
		if(type.equals(EAMPwd.ALGO_PLAIN))
		{
			this.type	= type ;
			this.pwd	= this.setPwd(pwd.getBytes()) ;
		}
		else if(type.equals(EAMPwd.ALGO_SHA1))
		{
			try
			{
				this.type = type ;
				MessageDigest md = MessageDigest.getInstance("SHA-1") ;
				this.pwd	= this.setPwd(md.digest(pwd.getBytes())) ;
			}
			catch(Exception e)
			{
				this.type = null ;	this.pwd = null ;
			}
		}
		else if(type.equals(EAMPwd.ALGO_MD5))
		{		
			try
			{
				this.type = type ;
				MessageDigest md = MessageDigest.getInstance("MD5") ;
				this.pwd	= this.setPwd(md.digest(pwd.getBytes())) ;
			}
			catch(Exception e)
			{
				this.type = null ;	this.pwd = null ;
			}
		}
		else
		{
			return 9001 ;
		}
		
		return 0 ;
	}

	/**
		PwdHistory일 경우 비밀번호 생성일자얻기
		@return 생성일시
	*/
	public EAMDate getDate()
	{
		return this.changeTime ;
	}

	/**
		LDAP에 저장할 값얻기
		@return LDAP에 저장할 값
	*/
	public byte[] getByte()
	{
		return decodeHex(this.toString()) ;
	}

	/*****
		Hexa Encoding된 값 얻기
		@return Hexa Encoding된 값
	public String getData()
	{
		return new String(decodeHex(this.toString())) ;
	}
	******/

	/**
		Hexa Encoding된 ssoPwd또는 ssoPwdHistory값 얻기
		@return Hexa Encoding된 ssoPwd또는 ssoPwdHistory값
	*/
	public String toString()
	{
		String temp = (this.changeTime == null) ? "" : this.changeTime.getLongString() ;
		return encodeHex(temp.getBytes()) + this.type + encodeHex(this.pwd) ;
	}

	/**
		Hexa Encoding된 ssoPwd 또는 ssoPwdHistory의 ssoPwd해당부분값 얻기
		@return Hexa Encoding된 ssoPwd 또는 ssoPwdHistory의 ssoPwd해당부분값
	*/
	public String getPassword()
	{
		return this.type + encodeHex(this.pwd) ;
	}

	/**
		Hexa Encoding된 ssoPwdHistory값 얻기
		@return Hexa Encoding된 ssoPwdHistory값
	*/
	public String getHistory()
	{
		EAMDate	temp ;
		if(this.changeTime == null)	temp = new EAMDate(System.currentTimeMillis()) ;
		else						temp = this.changeTime ;
		
		return encodeHex(temp.getLongString().getBytes()) + this.type + encodeHex(this.pwd) ;
	}
	
    private int checkHexa(byte ch) 
    {
		if ((ch >= 'A') && (ch <= 'F'))			return ch - 'A' + 10 ;	
		else if ((ch >= 'a') && (ch <= 'f'))	return ch - 'a' + 10 ;
		else if ((ch >= '0') && (ch <= '9'))	return ch - '0' ;
		else									return -1 ;
	}
	
	private byte[] setPwd(byte[] a)
	{
		if(a == null)	return null ;
		byte[] ret = new byte[(a.length > 20) ? 20 : a.length] ;
		for(int i = 0;i < ret.length;i++)
			ret[i] = a[i] ;
		return ret ;
	}
	
	private String encodeHex(byte[] a)
	{
		if(a == null)	return null ;
		
		int len = a.length;
		byte[] temp	= new byte[a.length*2];
		byte[] HEXA	= "0123456789ABCDEF".getBytes();

		for (int x = 0, y = 0; x < a.length; x++) 
		{
			temp[y++] = HEXA[(a[x] >> 4) & 0x0F] ;
			temp[y++] = HEXA[(a[x] >> 0) & 0x0F] ;
		}
		
		return new String(temp) ;
	}
		
	private byte[] decodeHex(String a)
	{
		if(a == null)	return null ;
		
		int len = a.length() / 2;
		byte[] temp = new byte[len] ;
		
		for(int i = 0; i < len; i++)
			temp[i] = (byte)(((byte)checkHexa((byte)a.charAt(i*2+0)) << 4) + ((byte)checkHexa((byte)a.charAt(i*2+1)) << 0)) ;
			
		return temp ;
	}
	
	/**
		[Test용]
	*/
    public static void main (String args []) throws Exception 
    {	   		
   		EAMPwd pwd = new EAMPwd("00706C61696E") ;
   		System.out.println("00706C61696E:" + pwd) ;
   		
   		pwd.setString("01AEBC3EBEE2F0C8B08B43D26C2B0055") ;
   		System.out.println("01AEBC3EBEE2F0C8B08B43D26C2B0055:" + pwd) ;
   		
   		pwd.setString("313131313939313331310112DEA96FEC20593566AB75692C9949596833ADC9") ;
   		EAMDate changeTime = pwd.getDate() ;
   		System.out.println("313131313939313331310112DEA96FEC20593566AB75692C9949596833ADC9:" + changeTime) ;
   		System.out.println("313131313939313331310112DEA96FEC20593566AB75692C9949596833ADC9:" + pwd.getPassword()) ;
   		
   		pwd.setPassword(EAMPwd.ALGO_PLAIN	,"plain") ;
   		System.out.println("plain :" + pwd) ;
   		System.out.println("plain :00706C61696E") ;
   		
   		pwd.setPassword(EAMPwd.ALGO_SHA1		,"sha1") ;
   		System.out.println("sha1  :" + pwd) ;
   		System.out.println("sha1  :01415AB40AE9B7CC4E66D6769CB2C081") ;
   		
   		pwd.setPassword(EAMPwd.ALGO_MD5	,"md5") ;
   		System.out.println("md5   :" + pwd) ;
   		System.out.println("md5   :031BC29B36F623BA82AAF6724FD3B167") ;
   		
    }
}
