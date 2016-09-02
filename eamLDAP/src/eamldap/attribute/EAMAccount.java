package eamldap.attribute;

import eamldap.*;
import eamldap.attribute.*;
import eamldap.jni.EAMCryptoJNI;

/**
	EAM의 ssoAccountProfile관련하여 Class.<br>

	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMAccount.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX,Windows<br>
	Compiler	:	javac<br>
	History		:	20050329 Creation.<br>
	TAB Length	: 	4 Byte.<br>
<br>
*/
public class EAMAccount
{
	String	sid 		= "" ;
	String	id			= "" ;
	String	password	= "" ;
	EAMConf	conf		= null ;
	
	/**
		생성자
	*/
	public EAMAccount() 
	{
	}

	/**
		생성자(바로, 서비스DN, 계정ID, 계정비밀번호을 Parsing(복호화)한다)
		@param	conf	Configure객체[EAMLdap에서 얻기바람.]
		@param	xml		"&ltAccount>..&lt/Account>"를 Parsing할 XML문
	*/
	public EAMAccount(EAMConf conf, String xml)
	{
		this.conf	= conf ;
		this.parseingXML(xml) ;
	}

	/**
		생성자
		@param	conf		Configure객체[EAMLdap에서 얻기바람]
		@param	serviceDN	서비스DN
		@param	userID		해당서비스의 계정ID
		@param	password	해당서비스의 계정비밀번호
	*/
	public EAMAccount(EAMConf conf, String serviceDN, String userID, String password)
	{
		this.conf		= conf ;
		this.sid		= serviceDN ;	
		this.id 		= userID ;
		this.password	= password ;
	}

	/**
		같은가(서비스DN, 계정ID, 계정비밀번호가 모두 같은가)
		@param	temp		비교대상
		@return	같으면 true, 틀리면 false
	*/
	public boolean equals(EAMAccount temp)
	{
		if(temp == null)	return false ;
		
		if(this.sid.equalsIgnoreCase(temp.getServiceDn()) == false)	return false ;
		if(this.id.equals(temp.getUserID()) == false)				return false ;
		if(this.password.equals(temp.getPassword()) == false)		return false ;
		
		return true ;
	}

	/**
		서비스DN, 계정ID, 계정비밀번호을 Parsing(복호화)한다
		@param	xml		대상XML문
	*/
	private void parseingXML(String xml)
	{

		byte[] tempSID	= this.decodeBase64(this.getValue(xml,"SID"		)) ;
		byte[] tempID 	= this.decodeBase64(this.getValue(xml,"Id"		)) ;
		byte[] tempPW	= this.getValue(xml,"Password").getBytes() ;
		this.sid		= new String(tempSID) ;
		this.id			= new String(tempID ) ;
				
		if(this.conf != null)
		{
			try
			{
				EAMCryptoJNI jni = new EAMCryptoJNI();
				jni.native_init(conf.getPSCertFilePath().getBytes(), conf.getPSPrivateKeyPath().getBytes(), conf.getCACertFilePath().getBytes(), conf.getPSPassword().getBytes());
				this.password	= new String(jni.native_decrypt(tempPW)) ;
			}
			catch(Exception e)
			{
				this.password = null ;
			}
		}
		else
			this.password	= new String(tempPW) ;
			
		//System.out.println("parseingXML..this:"+this) ;
	}
	
	/** 계정ID 얻기			*/	public String getUserID()		{	return this.id ;		}
	/** 서비스DN 얻기 		*/	public String getServiceDn()	{	return this.sid ;		}
	/** 계정비밀번호 얻기	*/	public String getPassword()		{	return this.password ;	}
	
	/** 계정ID 설정			*/	public void setUserID(String userID)			{	this.id 		= userID ;		}
	/** 서비스DN 설정		*/	public void setServiceDn(String serviceDN)		{	this.sid 		= serviceDN	;	}
	/** 계정비밀번호 설정	*/	public void setPassword(String password)		{	this.password	= password ;	}
	
	/**	No Comment			*/	public String toString()	{	return "<Account><SID>" + this.sid + "</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>" + this.id + "</Id><Password>" + this.password + "</Password></IdPwd><Etc></Etc></Account>" ;	}
	
	/**
		"&ltAccount> &lt/Account>"부분XML만 생성(암호화)하여 얻기
	*/
	public String getAccountXML()
	{	
		String xml = "" ;
		
		if(this.conf != null && this.password != null)
		{
			EAMCryptoJNI jni = new EAMCryptoJNI();
			jni.native_init(conf.getPSCertFilePath().getBytes(), conf.getPSPrivateKeyPath().getBytes(), conf.getCACertFilePath().getBytes(), conf.getPSPassword().getBytes());
			
			xml = xml + "<Account><SID>" ;
			xml = xml + this.encodeBase64(this.sid.getBytes()) ;
			xml = xml + "</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>" ;
			xml = xml + this.encodeBase64(this.id.getBytes()) ;
			xml = xml + "</Id><Password>" ;
			xml = xml + new String(jni.native_encrypt(this.password.getBytes())) ;
			xml = xml + "</Password></IdPwd><Etc></Etc></Account>" ;
		}
		
		return xml ;		
	}

	/**
		단일 Accout로 보고, LDAP에 Write할 XML 생성하여 얻기
	*/
	public String getXML()			
	{	
		String xml = this.getAccountXML() ;
		if(xml.length() > 0)	xml = "<?xml version=\"1.0\" ?><AccountProfile version=\"1.0\">"+xml+"</AccountProfile>" ;
		return xml ;
	}

	/**
		주어진 XML에서 해당 Tag부분 얻기
		@param	xml		XML문
		@param	name	TagName
	*/
	private String getValue(String xml, String name)
	{
		String startTag = "<"+name+">" ;
		String endTag = "</"+name+">" ;
		int startIndex = xml.indexOf(startTag) ;
		int endIndex   = xml.indexOf(endTag) ;
		if(startIndex < 0)	return "" ;
		if(endIndex	 < 0)	return "" ;
		
		return xml.substring(startIndex + startTag.length(), endIndex) ;
	}
	
	private static int checkBase64(byte ch) 
    {
		if ((ch >= 'A') && (ch <= 'Z'))			return ch - 'A' ;	
		else if ((ch >= 'a') && (ch <= 'z'))	return ch - 'a' + 26 ;
		else if ((ch >= '0') && (ch <= '9'))	return ch - '0' + 52 ;
		else if (ch == '+')						return 62 ;
		else if (ch == '/')						return 63 ;
		else if (ch == '=' || ch == '-')		return 64 ;
		else if (ch == 0x00)					return 65 ;
		else 									return -1 ;
	}
	
	private static byte[] decodeBase64(String base64Data ) 
	{
		int base64DataLen = base64Data.length();
		int binaryDataLen = (base64DataLen / 4) * 3;
		byte[] buff = new byte[binaryDataLen];
		byte[] buf = new byte[1];
		int bin_index = 0; 
		int base_index = 0;
		int bin_len = 0;
		int ii = 0 ;

		for (int i=0; i < base64DataLen; i++) 
		{
			buf[0]= (byte)checkBase64((byte)base64Data.charAt(i)) ;
			if(buf[0] == -1)	buf[0]= (byte)checkBase64((byte)base64Data.charAt(++i)) ;
			if(buf[0] == -1)	buf[0]= (byte)checkBase64((byte)base64Data.charAt(++i)) ;
			if(buf[0] >= 64)
			{
				bin_len-- ;
				break ;
			}
			
			switch (base_index % 4) {
	      	case 0 :
		  		buff[ii+0] = (byte)((buf[0] << 2) & 0xfc) ;					// 1234 56xx |
				bin_len++ ;
		  		break ;
	      	case 1 :
	      		buff[ii+0] = (byte)(buff[ii+0] | ((buf[0] >> 4) & 0x03)) ;	// xxxx xx12 |    --->
	      		buff[ii+1] = (byte)((buf[0] << 4) & 0xf0) ;					// xxxx xx12 | 3456
				bin_len++ ;
		  		break ;
	      	case 2 :
				buff[ii+1] = (byte)(buff[ii+1] | ((buf[0] >> 2) & 0x0f)) ;	// xxxx xxxx | xxxx 1234
	      		buff[ii+2] = (byte)((buf[0] << 6) & 0xc0) ;					// xxxx xxxx | xxxx 1234 | 56 -->
				bin_len++ ;
		  		break ;
	      	default:
				buff[ii+2] = (byte)(buff[ii+2] | (buf[0] & 0x3f)) ;			// xxxx xxxx | xxxx xxxx | xx12 3456
				ii += 3 ;
		  		break ;
	    	}
	    	base_index++ ;
		}
		
		byte[] binaryData = new byte[bin_len];

		for(int i = 0; i < bin_len;i++)
			binaryData[i] = buff[i] ;

		return binaryData; 
	}
	
	private static String encodeBase64( byte[] data )
	{
		if(data == null)		return null ;
		if(data.length == 0)	return "" ;
		
		byte[] temp = new byte[( (data.length - 1 ) / 3 + 1 ) * 4] ;	// 1 -> 4, 2 -> 4, 3 -> 4, 4 -> 8
		byte[] BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
		
		for(int d = 0, t = 0;d < data.length;)
		{
			int l = 0x00000000 ;
			if(d < data.length)	l |= ((0x000000FF & data[d++]) << 16) ;
			if(d < data.length)	l |= ((0x000000FF & data[d++]) <<  8) ;
			if(d < data.length)	l |= ((0x000000FF & data[d++]) <<  0) ;
			
			temp[t++] = BASE[(int)((l>>>18)&0x0000003f)] ;
			temp[t++] = BASE[(int)((l>>>12)&0x0000003f)] ;
			temp[t++] = BASE[(int)((l>>> 6)&0x0000003f)] ;
			temp[t++] = BASE[(int)((l>>> 0)&0x0000003f)] ;
		}
		
		if((data.length % 3) == 2)	temp[temp.length-1] = (byte) '=';
		if((data.length % 3) == 1)	temp[temp.length-2] = (byte) '=';

		return new String(temp) ; 
	}

    public static void main (String args []) throws Exception 
    {
    	EAMConf 	conf= new EAMConf() ;
   		//EAMAccount	pwd = new EAMAccount(conf,"<?xml version=\"1.0\" ?><AccountProfile version=\"1.0\"><Account><SID>c3NvU2lkPUVSUCxzc29Tc2lkPVNBUCxvdT1zZXJ2aWNlLG89c3NvMzByb290</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>QUEA</Id><Password>CcaNYFkCPp/sk5Y+W3xwGsJTufpkeCUaaIq2Q5oQa+hUYSjjihAyRjk0z+PVXyeyCIJBfCOMEWgT/SHP1/bA/PvY+51yGQe3j1nq1AO3j7n5cX4KBqVzKQfTPPvDFdAUTTHkRIkt/spqrxZqOf/rk/rBE/1nPVgASmsb76GNETs=</Password></IdPwd><Etc></Etc></Account></AccountProfile>") ;
   		//EAMAccount	pwd = new EAMAccount(conf,"<?xml version=\"1.0\" ?><AccountProfile version=\"1.0\"><Account><SID>c3NvU2lkPUVSUCxzc29Tc2lkPVNBUCxvdT1zZXJ2aWNlLG89c3Nv========</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>QUE=</Id><Password>OP0pnO9NyE5atkkJoCGMde/MKmX6UjJXLgNx6GC/kIDUtqnYIvJ3p1Xw62htKhNx23L21b1X4CcjtjdxUBg5FDU02zTQUCC45Lpa6Kf8aNJiEfRXkoRdmegnOk4kVa7GzTQ5+righUS+S9RSIaqDzKlKm4eg2Pgii5m+dbHevpI=</Password></IdPwd><Etc></Etc></Account></AccountProfile>") ;
   		EAMAccount	pwd = new EAMAccount(conf,"<?xml version=\"1.0\" ?><AccountProfile version=\"1.0\"><Account><SID>c3NvU2lkPWRvbWlub2xvZ2luLHNzb1NzaWQ9NTEwMCxvdT1zZXJ2aWNlLG89c3NvMzByb290</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>MjAwMjA1NjM=</Id><Password>CVy91WS11QjyxJl04xKI+0Rg7pq1dH/kvfBv9Kr4/Ip70sAMJAw/bN6VBUqJkek152w5NemSOTaQ00yFJldO9SfxaYwWYaiJGHRcFR7oAmifKYb02QSqnsIhvN7RLZXSWTDHqtr0UaJHN/T8UG71TYhurJ96afT3ckQocWeFnIo=</Password></IdPwd><Etc></Etc></Account><Account><SID>c3NvU2lkPWxvZ2luLHNzb1NzaWQ9NTIwMCxvdT1zZXJ2aWNlLG89c3NvMzByb290</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>bWlyYWdybw==</Id><Password>L+vSH+p0TynSZ3oVv90NqM76jZo3vn0Nyn+nTpuqYncNsdeYoJeC3mz1Ux+NVG7yWT/RFlsgIqQsfuj0Elmlq6bU1ZLh+L/0QKMCYK/0gD7HroWQ43nNa2aw+XTv72Bk1iKrwUyVL1Nzapk2CvqnTPbhaT/Hoj6BgQQdECW5fNY=</Password></IdPwd><Etc></Etc></Account></AccountProfile>") ;
   		//System.out.println("encodeBase64(\"1\"):["+EAMAccount.encodeBase64("1".getBytes())+"]") ;
   		//System.out.println("encodeBase64(\"11\"):["+EAMAccount.encodeBase64("11".getBytes())+"]") ;
   		//System.out.println("encodeBase64(\"111\"):["+EAMAccount.encodeBase64("111".getBytes())+"]") ;
   		System.out.println("pwd.getUserID()   :["+pwd.getUserID()+"]"		) ;
   		System.out.println("pwd.getServiceDn():["+pwd.getServiceDn()+"]"	) ;
   		System.out.println("pwd.getPassword() :["+pwd.getPassword()+"]"		) ;
    }
}
