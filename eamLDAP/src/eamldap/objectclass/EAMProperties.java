package eamldap.objectclass;

import java.lang.*;

import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPModificationSet;
import netscape.ldap.LDAPModification;

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Date;
import eamldap.*;
import eamldap.attribute.*;

/**
	EAMProperties는 eamldap.jar안의 props화일, netscape.ldap.LDAPAttributeSet, File를 super.load()및 한글처리하여 Set한다.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMUser.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMProperties extends Properties
{
    /** Default 생성자 		*/ public	EAMProperties()						{	super();			}
    /** 내부자원읽기 생성자	*/ public	EAMProperties(String r)				{	this.load(r) ;		}
    /** 외부화일읽기 생성자	*/ public	EAMProperties(File f)				{	this.load(f) ;		}
    /** LDAP에 의한 생성자	*/ public	EAMProperties(LDAPAttributeSet l)	{	this.set(l) ;		}
    /** EAMProperties생성자	*/ public	EAMProperties(EAMProperties prop)	{	this.set(prop) ;	}

    /**
    	Library의 Resource중에서 읽어드리기
    */
    public void load(String prop)
    {
		try
    	{
			InputStream inputstream = ClassLoader.getSystemResourceAsStream(prop);
			if(inputstream == null)
			{
				String home = System.getProperty("user.home") ;

				int i = prop.lastIndexOf("/") ;
				String filePath = (i > 0) ? prop.substring(i+1) : prop ;

				if(home.charAt(home.length()-1) == File.separatorChar)
					filePath = home + "XecureConf" + File.separator + filePath;
				else
					filePath = home + File.separator + "XecureConf" + File.separator + filePath;

				File f = new File(filePath) ;
				if(!f.canRead())
				{
					System.out.println("Reading File(" + filePath + ")...NG") ;
					return ;
				}
				super.load(new FileInputStream(f)) ;
			}
			else
				super.load(inputstream) ;

			for (Enumeration e = super.propertyNames() ; e.hasMoreElements() ;)
			{
				String name 	= (String)e.nextElement() ;
				this.setProperty(name, this.toKorean(super.getProperty(name))) ;
			}
		}
		catch(Exception e)
		{
		}
		return ;
	}

	public void fromString(String src)
	{
		if(src.charAt(0) == '{' && src.charAt(src.length() - 1) == '}')
		{
			String[] nv = EAMUtil.String2Array(src.substring(1, src.length()-1), ", ") ;

			for(int i = 0; i < nv.length; i++)
			{
				int slash = nv[i].indexOf("=") ;
				if(slash > 0)
				{

					String name		= nv[i].substring(0, slash) ;
					String value	= nv[i].substring(slash+1,nv[i].length()) ;
					EAMNVDS nvds = new EAMNVDS(value,"= ") ;

					String type = nvds.find("T") ;
					if(type == null)			this.setProperty(name, ""+nvds) ;
					else if(type.equals(""))	this.setProperty(name, ""+nvds) ;
					else						super.setProperty(name, ""+nvds) ;
				}
			}
		}
		return ;
	}

	private String toKorean(String data)
	{
		try {
			if (data != null)
				return (new String(data.getBytes("8859_1"),"EUC_KR"));
			return data;
		} catch (UnsupportedEncodingException e) {
			return "Encoding Error";
		}
	}

	private byte[] toUTF8(String data)
	{
		try {
			if (data != null)	return data.getBytes("UTF-8") ;
			return new byte[0];
		} catch (UnsupportedEncodingException e) {
			return new byte[0];
		}
	}

    /**
    	화일에서 읽어드리기
    */
    public void load(File f)
    {
    	try
    	{
    		if(!f.canRead())	return ;

	    	super.load(new FileInputStream(f)) ;	// Properties에서 8859_1 CharSet으로 Read한다.

    		for (Enumeration e = super.propertyNames() ; e.hasMoreElements() ;)
    		{
    			String name	= (String)e.nextElement() ;
				this.setProperty(name, this.toKorean(super.getProperty(name))) ;
			}
		}
		catch(Exception e)
		{
		}
    }

	/**
		입력한 EAMProperties의 Key들 기준으로 현재(this) EAMProperties의 값과 비교하여<br>
		변경해야 할 것들만 EAMProperties로 Return한다.
	*/
    public EAMProperties getMODProperties(EAMProperties prop)
    {
    	EAMProperties ret = new EAMProperties() ;

    	for (Enumeration e = prop.propertyNames() ; e.hasMoreElements() ;)
    	{
   			String name	= (String)e.nextElement() ;

			if((prop.getProperty(name)).equals(this.getProperty(name)))	continue ;

			if(prop.isNull(name))			continue ;
			if("dn".equals(name))			continue ;
			if("pid".equals(name))			continue ;
			if(name.startsWith("org"))		continue ;
			if(name.startsWith("svc"))		continue ;
			if("ssoprofile".equals(name))	continue ;

			if(EAMUtil.isHangul(name) || EAMUtil.isAllLowerCase(name) == false)
			{
				EAMNVDS nvds = new EAMNVDS(this.getProperty("ssoprofile", "")) ;

				if(nvds.find(name) == null)	nvds.add(name, prop.getProperty(name)) ;		// Add ???
				else						nvds.update(name, prop.getProperty(name)) ;		//

				this.setProperty("ssoprofile", nvds.getQuery()) ;
				ret.setProperty("ssoprofile", nvds.getQuery()) ;
			}
			else
			{
				if(prop.isBinary(name))
				{
					if(prop.isArray(name))	ret.setProperty(name, prop.getByteArray(name)	) ;
					else					ret.setProperty(name, prop.getByte(name)		) ;
				}
				else
				{
					if(prop.isArray(name))	ret.setProperty(name, prop.getStringArray(name)	) ;
					else					ret.setProperty(name, prop.getString(name)		) ;
				}
			}
		}

		return ret ;
    }

    /**
    	LDAPModificationSet를 얻기
    */
    public LDAPModificationSet getLDAPModificationSet()
    {
    	LDAPModificationSet ret = new LDAPModificationSet() ;

    	for (Enumeration e = this.propertyNames() ; e.hasMoreElements() ;)
    	{
   			String name	= (String)e.nextElement() ;

			if(this.isNull(name))		continue ;
			if("dn".equals(name))		continue ;
			if(name.startsWith("acc"))	continue ;
			if(name.startsWith("org"))	continue ;
			if(name.startsWith("svc"))	continue ;
			if(EAMUtil.isHangul(name))					continue ;
			if(EAMUtil.isAllLowerCase(name) == false)	continue ;

			String type = this.getType(name) ;
			if(type.equals("BM"))
			{
				byte[][] temp = this.getByteArray(name) ;
				if(temp == null)	{	ret.add(LDAPModification.REPLACE, new LDAPAttribute(name, "")) ;	continue ;	}
				if(temp.length== 0)	{	ret.add(LDAPModification.REPLACE, new LDAPAttribute(name, "")) ;	continue ;	}

				LDAPAttribute attr = new LDAPAttribute(name) ;
				for(int i = 0; i < temp.length;i++)
					attr.addValue(temp[i]) ;
				ret.add(LDAPModification.REPLACE, attr) ;
			}
			else if(type.equals("BS"))
			{
				byte[] temp = this.getByte(name) ;
				if(temp == null)	{	ret.add(LDAPModification.REPLACE, new LDAPAttribute(name, "")) ;	continue ;	}

				ret.add(LDAPModification.REPLACE, new LDAPAttribute(name,temp)) ;
			}
			else if(type.equals("SM"))
			{
				String[] temp = this.getStringArray(name) ;
				if(temp == null)	{	ret.add(LDAPModification.REPLACE, new LDAPAttribute(name, "")) ;	continue ;	}
				if(temp.length==0)	{	ret.add(LDAPModification.REPLACE, new LDAPAttribute(name, "")) ;	continue ;	}

				LDAPAttribute attr = new LDAPAttribute(name) ;
				for(int i = 0; i < temp.length;i++)
					attr.addValue(temp[i]) ;
				ret.add(LDAPModification.REPLACE, attr) ;
			}
			else
			{
				String temp = this.getString(name) ;
				if(temp == null)	{	ret.add(LDAPModification.REPLACE, new LDAPAttribute(name, "")) ;	continue ;	}
				ret.add(LDAPModification.REPLACE, new LDAPAttribute(name,temp)) ;
			}
		}

		return ret ;
    }

    /**
    	LDAPAttributeSet를 얻기
    */
    public LDAPAttributeSet getLDAPAttributeSet()
    {
    	LDAPAttributeSet ret = new LDAPAttributeSet() ;

    	for (Enumeration e = super.propertyNames() ; e.hasMoreElements() ;)
    	{
    		String name	= (String)e.nextElement() ;

			if(isNull(name))			continue ;
			if("dn".equals(name))		continue ;
			if("pid".equals(name))		continue ;
			if(name.startsWith("org"))	continue ;
			if(name.startsWith("svc"))	continue ;

			String type = this.getType(name) ;
			if(type.equals("BM"))
			{
				byte[][] temp = this.getByteArray(name) ;
				if(temp == null)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}
				if(temp.length==0)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}

				LDAPAttribute attr = new LDAPAttribute(name) ;
				for(int i = 0; i < temp.length;i++)
					attr.addValue(temp[i]) ;
				ret.add(attr) ;
			}
			else if(type.equals("BS"))
			{
				byte[] temp = this.getByte(name) ;
				if(temp == null)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}
				if(temp.length==0)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}

				ret.add(new LDAPAttribute(name, temp)) ;
			}
			else if(type.equals("SM"))
			{
				String[] temp = this.getStringArray(name) ;
				if(temp == null)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}
				if(temp.length==0)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}

				LDAPAttribute attr = new LDAPAttribute(name) ;
				for(int i = 0; i < temp.length;i++)
					attr.addValue(temp[i]) ;
				ret.add(attr) ;
			}
			else
			{
				String temp = this.getString(name) ;
				if(temp == null)		{	ret.add( new LDAPAttribute(name, "")) ;	continue ;	}

				ret.add(new LDAPAttribute(name, temp )) ;
			}
		}

		return ret ;
	}

    /**
    	LDAPAttributeSet를 Setting하기
    */
    public void set(LDAPAttributeSet las)
    {
    	if(las == null)		return ;

    	for (Enumeration e = las.getAttributes() ; e.hasMoreElements() ;)
		{
			String type			= "SS" ;
			LDAPAttribute attr 	= (LDAPAttribute)e.nextElement() ;

			String name 		= attr.getName().toLowerCase() ;

			if(name.equals("ssopwd"				))	type = "BS" ;
			if(name.equals("ssopwdhistory"		))	type = "BM" ;
			if(name.equals("ssomember"			))	type = "SM" ;
			if(name.equals("uniquemember"		))	type = "SM" ;
			if(name.equals("ssouspathid"		))	type = "SM" ;
			if(name.equals("objectclass"		))	type = "SM" ;
			if(name.equals("ssouacl"			))	type = "SM" ;
			if(name.equals("ssomacl"			))	type = "SM" ;
			if(name.equals("ssoprofileconfig"	))	type = "SM" ;

			if(type.equals("SS"))
			{
				String temp[] = attr.getStringValueArray() ;
				if(temp == null)		this.setProperty(name,"") ;
				if(temp.length == 0)	this.setProperty(name,"") ;
				else					this.setProperty(name,temp[0]) ;
			}
			else if(type.equals("SM"))
			{
				String temp[] = attr.getStringValueArray() ;
				if(temp == null)		this.setProperty(name, new String[0]) ;
				else					this.setProperty(name, temp) ;
			}
			else if(type.equals("BS"))
			{
				byte[][] temp = attr.getByteValueArray() ;
				if(temp == null)		this.setProperty(name,new byte[0]) ;
				if(temp.length == 0)	this.setProperty(name,new byte[0]) ;
				else					this.setProperty(name,temp[0]) ;
			}
			else if(type.equals("BM"))
			{
				byte[][] temp = attr.getByteValueArray() ;
				if(temp == null)		this.setProperty(name,new byte[0][]) ;
				if(temp.length == 0)	this.setProperty(name,new byte[0][]) ;
				else					this.setProperty(name,temp) ;
			}

			if(name.equals("ssoprofile"))	//200505
			{
				EAMNVDS nvds = new EAMNVDS(this.getProperty(name, "")) ;
				String[] n = nvds.getUniqueNames() ;
				for(int i = 0; i < n.length;i++)
					this.setProperty(n[i], nvds.find(n[i])) ;
			}
		}

    	return ;
    }

    /**
    	EAMProperties를 Setting하기
    */
    public void set(EAMProperties prop)
    {
		for (Enumeration e = prop.keys() ; e.hasMoreElements() ;)
    	{
    		String name	= (String)e.nextElement() ;
    		this.put(name, prop.get(name)) ;
		}

    	return ;
    }
    

    public String 	getProperty(String key)				{	return this.getString(key) ;													}
    public String[] getProperty(String key, String[] d)	{  	String[] ret = this.getStringArray(key) ;	return (ret == null) ? d : ret ;	}
	public String	getProperty(String key, String d)	{	String ret = this.getString(key) ;		   	return (ret == null) ? d : ret ;	}

	public byte[]	getProperty(String key, byte[] d)	{	byte[] ret = this.getByte(key) ;			return (ret == null) ? d : ret ;	}
	public byte[][] getProperty(String key, byte[][] d)	{	byte[][] ret = this.getByteArray(key) ;		return (ret == null) ? d : ret ;	}

	private String getType(String key)
	{
		String temp = super.getProperty(key) ;
		if(temp == null)	return "" ;

		EAMNVDS nvds = new EAMNVDS(temp,"= ") ;
		return nvds.find("T") ;
	}

	public boolean isNull(String key)	{	return (super.getProperty(key) == null) ? true : false ; 	}
	public boolean isArray(String key)
	{
		String temp = this.getType(key) ;
		return ("SM".equals(temp) || "BM".equals(temp)) ;
	}

	public boolean isBinary(String key)
	{
		String temp = this.getType(key) ;
		return ("BS".equals(temp) || "BM".equals(temp)) ;
	}


    public Object setProperty(String key, String value)
    {
    	EAMNVDS nvds = new EAMNVDS("","") ;
    	//System.out.println("value>>>"+value);
    	nvds.add("V", value) ;
    	return super.setProperty(key, ""+nvds) ;
    }

    public Object setProperty(String key, String[] value)
    {
		EAMNVDS nvds = new EAMNVDS("T-SM*","= ") ;
    	if(value != null)	for(int i = 0; i < value.length; i++)	nvds.add("V", value[i]) ;

    	return super.setProperty(key, ""+nvds) ;
    }

    public Object setProperty(String key, byte[] value)
    {
    	EAMNVDS nvds = new EAMNVDS("T-BS*","= ") ;
    	nvds.add("V", this.Escape(value)) ;

    	return super.setProperty(key, ""+nvds) ;
    }

    public Object setProperty(String key, byte[][] value)
    {
    	EAMNVDS nvds = new EAMNVDS("T-BM*","= ") ;
    	if(value != null)	for(int i = 0; i < value.length; i++)	nvds.add("V", this.Escape(value[i])) ;

    	return super.setProperty(key, ""+nvds) ;
    }

	private String[] getStringArray(String key)
	{
		String value = super.getProperty(key) ;
    	if(value == null)		return null ;
    	if(value.length() == 0)	return null ;

		EAMNVDS nvds = new EAMNVDS(value, "= ") ;
		return nvds.getValues("V") ;
	}

	private byte[][] getByteArray(String key)
	{
		String[] temp = this.getStringArray(key) ;
		if(temp == null)		return null ;

		byte[][] ret = new byte[temp.length][] ;
		for(int i = 0; i < temp.length; i++)
		{
			if(this.isBinary(key))	ret[i] = this.decodeHex(temp[i]) ;
			else					ret[i] = temp[i].getBytes() ;
		}
		return ret ;
	}

	private String getString(String key)
	{
		String[] ret = this.getStringArray(key) ;
		if(ret == null)		return null ;
		return (ret.length > 0)	? ret[0] : "" ;
	}

	private byte[] getByte(String key)
	{
		String ret = this.getString(key) ;
		if(ret == null)		return null ;
		if(this.isBinary(key))	return this.decodeHex(ret) ;
		else					return ret.getBytes() ;
	}

	private String Escape(byte[] value)
	{
		if(value == null)	return "" ;
		return new String(this.encodeHex(value)) ;
	}

    private int checkBase64(byte ch)
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

	private byte[] decodeBase64(String base64Data )
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

		if(data.length % 3 == 2)	temp[temp.length-1] = (byte) '=';
		if(data.length % 3 == 1)	temp[temp.length-2] = (byte) '=';

		return new String(temp) ;
	}

    private int checkHexa(byte ch)
    {
		if ((ch >= 'A') && (ch <= 'F'))			return ch - 'A' + 10 ;
		else if ((ch >= 'a') && (ch <= 'f'))	return ch - 'a' + 10 ;
		else if ((ch >= '0') && (ch <= '9'))	return ch - '0' ;
		else									return -1 ;
	}

	private byte[] encodeHex(byte[] a)
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

		return temp ;
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
    	모든 정보를 LDIF형식으로 PrintStream해준다.
    */
    public void listLDIF(PrintStream out)
    {
    	if(isNull("dn"))		return ;

    	out.println("dn: " + this.getProperty("dn")) ;

    	for (Enumeration keys = super.propertyNames() ; keys.hasMoreElements() ;)
    	{
    		String key 		= (String)keys.nextElement();

    		if("dn".equals(key))	continue ;

    		String[] value	= this.getStringArray(key) ;
    		if(value == null)
	    			out.println(key + ": " + value) ;
    		else
    		{
    			if(value.length == 0)
    				out.println(key + ": ") ;
    			for(int i = 0; i < value.length; i++)
    			{
    				if(this.isBinary(key))
    					out.println(key + ":: " + this.encodeBase64(this.decodeHex(value[i]))) ;
    				else if(EAMUtil.isHangul(value[i]))
    					out.println(key + ":: " + this.encodeBase64(this.toUTF8(value[i]))) ;
    				else
    					out.println(key + ": " + value[i]) ;
    			}
    		}
    	}
    	out.println() ;
    }

    /**
    	모든 정보를 PrintStream해준다.
    */
    public void list(PrintStream out)
    {
    	for (Enumeration keys = super.propertyNames() ; keys.hasMoreElements() ;)
    	{
    		String key 		= (String)keys.nextElement();
    		String[] value	= this.getStringArray(key) ;
    		if(value == null)
    			out.println(key + "=" + value) ;
    		else
    		{
    			if(value.length == 0)
    				out.println(key + "=") ;
    			for(int i = 0; i < value.length; i++)
    				out.println(key + "=" + value[i]) ;
    		}
    	}
    }

    /**
    	모든 정보를 PrintWriter해준다.
    */
    public void list(PrintWriter out)
    {
    	for (Enumeration keys = super.propertyNames() ; keys.hasMoreElements() ;)
    	{
    		String key 		= (String)keys.nextElement();
    		String[] value	= this.getStringArray(key) ;
    		if(value == null)
    			out.println(key + "=" + value) ;
    		else
    		{
    			if(value.length == 0)
    				out.println(key + "=") ;
    			for(int i = 0; i < value.length; i++)
    				out.println(key + "=" + value[i]) ;
    		}
    	}
    }

    /**
    	Key들을 얻기
    	@return	Key들
    */
    public String[] getKeys()
    {
    	int count = 0;
    	for (Enumeration keys = super.propertyNames(); keys.hasMoreElements() ;)
    		count++ ;

    	String[] ret = new String[count] ;
    	int i = 0;
    	for (Enumeration keys = super.propertyNames(); keys.hasMoreElements() ;)
    		ret[i++] = (String)keys.nextElement();

    	return ret ;
    }

    public static void main (String args []) throws Exception
    {
    	EAMProperties	prop = null ;

    	switch (args.length)
    	{
    		case	1	:
    			if(args[0].startsWith("eamldap/props/") && args[0].endsWith("props"))
    				prop = new EAMProperties(args[0]) ;
    			else
					prop = new EAMProperties(new File(args[0])) ;

				prop.list(System.out) ;
    			break ;

    		default		:
    			System.out.println("java eamldap.EAMProperties <filename>") ;
    			EAMProperties prp = new EAMProperties() ;
    			System.out.println("prp:" + prp) ;
    			String[] temp = { "가=가, ", "가가" } ;
    			prp.setProperty("가",temp) ;
    			prp.setProperty("나","나,") ;


    			System.out.println("prp:" + prp) ;

    			EAMProperties ppp = new EAMProperties() ;
    			ppp.fromString(prp.toString()) ;

    			String[] tmp = ppp.getProperty("가", new String[0]) ;
    			System.out.println("--------------------") ;
    			for(int i = 0; i < tmp.length; i++)
    				System.out.println("tmp["+i+"]:["+tmp[i]+"]") ;

    			System.out.println("나:[" + ppp.getProperty("나") + "]") ;

    			break ;
    	}
    }
}

