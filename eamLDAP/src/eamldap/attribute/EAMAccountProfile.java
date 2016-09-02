package eamldap.attribute;

import eamldap.*;
import eamldap.attribute.*;
import java.io.*;
import java.util.Vector;

/**
	EAM의 ssoAccountProfile관련하여 Class.<br>

	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMAccountProfile.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX,Windows<br>
	Compiler	:	javac<br>
	History		:	20050329 Creation.<br>
	TAB Length	: 	4 Byte.<br>
<br>
*/
public class EAMAccountProfile
{
	boolean	isUpdate= false ;
	String	xml		= null ;
	Vector	account	= new Vector() ;
	EAMConf	conf	= null ;
	
	/**
		생성자
	*/
	public EAMAccountProfile() 
	{
	}

	/**
		생성자(바로, &ltAccount>를 Parsing후 EAMAccount로 또 Parsing(복호화)한다)
		@param	conf	Configure객체[EAMLdap에서 얻기바람.]
		@param	xml		Parsing할 AccountProfile 전체XML문
	*/
	public EAMAccountProfile(EAMConf conf, String xml)
	{
		this.conf	= conf ;
		this.xml 	= xml ;
		this.parseingXML() ;
	}
	
	public EAMAccountProfile(EAMConf conf, EAMAccount[] accs)
	{
		this.conf	= conf ;
		if(accs != null)
		{
			for(int i = 0; i < accs.length;i++)
				this.account.add(i,accs[i]) ;
		}
		this.isUpdate = true ;
		this.makeXML() ;
	}
	
	/**
		&ltAccount>를 Parsing후 EAMAccount로 또 Parsing(복호화)한다
	*/
	private void parseingXML()
	{ 
		if(this.conf == null)	return ;
		if(this.xml	== null)	return ;
		
		this.account.clear() ;
		int i = 0 ;
		while(true)
		{
			//System.out.println("this.xml:" + this.xml) ;
			String temp = this.getValue(this.xml, "Account", i) ;
			if(temp == null)		break ;
			if(temp.length() == 0)	break ;
			//System.out.println("temp:" + temp) ;
			this.account.add(new EAMAccount(conf, temp)) ;
			i++ ;
		}
	}

	/**
		&ltAccount>의 serviceDN에 해당하는 위치Index(Zero Base)얻기
		@param	serviceDN	서비스DN
		@return 위치Index(Zero Base)
	*/
	private int findIndex(String serviceDN)
	{
		for(int i = 0; i < this.account.size(); i++)
			if(serviceDN.equalsIgnoreCase(((EAMAccount)this.account.get(i)).getServiceDn()))
				return i ;
		return -1 ;
	}
	
	/**
		serviceDN에 해당하는 EAMAccount객체 얻기
		@param	serviceDN	서비스DN
		@return 해당하는 EAMAccount객체
	*/
	public EAMAccount find(String serviceDN)
	{
		int index = this.findIndex(serviceDN) ;
		return (index >= 0) ? (EAMAccount)this.account.get(index) : null ;
	}

	/**
		EAMAccount객체를 추가 또는 변경하기
		@param	acc	변경할 EAMAccount객체
	*/
	public void update(EAMAccount acc)
	{
		if(acc == null)									return ;
		if(acc.equals(this.find(acc.getServiceDn())))	return ;
		
		this.isUpdate = true ;
		int index = this.findIndex(acc.getServiceDn()) ;
		if(index >= 0)
		{
			this.account.remove(index) ;
			this.account.add(index, acc) ;
		}
		else
			this.account.add(acc) ;
			
		this.makeXML() ;
		
		return ;
	}
	
	/**
		Account(EAMAccount객체)를 생성하여 추가 또는 변경하기
		@param	serviceDN	변경할 서비스DN
		@param	userID		변경할 해당서비스의 계정ID
		@param	password	변경할 해당서비스의 계정비밀번호
	*/
	public void update(String serviceDN, String userID, String password)
	{
		this.update(new EAMAccount(this.conf, serviceDN, userID, password)) ;
	}
	
	/**
		Account(EAMAccount객체)를 생성하여 추가 또는 변경하기
		@param	newconf		변경할 인증서 및 개인키가 있는 Configure
	*/
	public void update(EAMConf newconf)
	{
		//System.out.println("account:"+this.account.size()) ;
		for(int i = 0; i < this.account.size();i++)
		{
			this.isUpdate = true ; 
			EAMAccount oldAcc = (EAMAccount)this.account.get(i) ;
			//System.out.println("["+oldAcc+"]:"+oldAcc) ;
			if(oldAcc.getPassword() != null)
			{
				EAMAccount newAcc = new EAMAccount(newconf, oldAcc.getServiceDn(), oldAcc.getUserID(), oldAcc.getPassword()) ;
				this.account.set(i,newAcc) ;
			}
		}
		
		this.makeXML() ;
	}

	/**
		Account(EAMAccount객체)를 삭제하기
		@param	serviceDN	삭제할 서비스DN
	*/
	public void remove(String serviceDN)
	{
		int index = this.findIndex(serviceDN) ;
		if(index >= 0)
		{
			this.isUpdate = true ;
			this.account.remove(index) ;
			this.makeXML() ;
		}
		
		return ;
	}

	/**
		모든 Account(EAMAccount객체)를 얻기
	*/
	public EAMAccount[] get()
	{
		EAMAccount[] ret = new EAMAccount[this.account.size()] ;
		for(int i = 0; i < this.account.size(); i++)
			ret[i] = (EAMAccount)this.account.get(i) ;
		return ret ;
	}

	/**
		변경시 새로운 AccountProfile 전체XML문 생성(암호화)하기
	*/
	private void makeXML()
	{
		if(this.isUpdate)
		{		
			this.xml = "<?xml version=\"1.0\" ?><AccountProfile version=\"1.0\">" ;
			for(int i = 0; i < this.account.size(); i++)
				this.xml += ((EAMAccount)this.account.get(i)).getAccountXML() ;
			this.xml += "</AccountProfile>" ;
			this.isUpdate = false ;
		}
	}
	
	/** LDAP에 Write할 XML문	*/	public String getXML()		{	return this.xml	;		}
	/** No Comment				*/	public String toString()	{	return this.getXML() ;	}
		
	/**
		주어진 XML에서 몇번째 해당 Tag부분 얻기
		@param	xml		XML문
		@param	name	TagName
		@param	index	몇번째 Tag(Zero Base)
	*/
	private String getValue(String xml, String name, int index)
	{
		String startTag = "<"+name+">" ;
		String endTag = "</"+name+">" ;
		
		int startIndex = -1 ;
		for(int i = 0; i <= index; i++)
			startIndex = xml.indexOf(startTag, startIndex+1) ;
		
		int endIndex   = xml.indexOf("</"+name+">",startIndex) ;
		
		if(startIndex < 0)	return "" ;
		if(endIndex	 < 0)	return "" ;
		
		return xml.substring(startIndex + startTag.length(), endIndex) ;
	}

    public static void main (String args []) throws Exception 
    {
    	/*****
    	EAMConf 	conf= new EAMConf() ;
   		EAMAccountProfile	accProfile = new EAMAccountProfile(conf,"<?xml version=\"1.0\" ?><AccountProfile version=\"1.0\"><Account><SID>c3NvU2lkPWRvbWlub2xvZ2luLHNzb1NzaWQ9NTEwMCxvdT1zZXJ2aWNlLG89c3NvMzByb290</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>YXNkZg==</Id><Password>CamT06FkKIOeElPhKo+GA6S+eXq6QqioiDW4oLUrLIxsKoS45/tj1GvKJN/LETqG/2fGvXhIsSG3W/iXGqqJzRn/JyQQ2ttz60aEXe1RSjlD60c+uJrSKbJ+McoDg1YCzDWzWQOX7+lU/fSfsE7PNiObqgJrCEZwP3pU2MnSQWw=</Password></IdPwd><Etc></Etc></Account><Account><SID>c3NvU2lkPWxvZ2luLHNzb1NzaWQ9NTIwMCxvdT1zZXJ2aWNlLG89c3NvMzByb290</SID><AuthMethod>SURQV0Q=</AuthMethod><IdPwd><Id>bWlyYWdybw==</Id><Password>ARzIwXdrALUpcUvN2P9C7Fqy3fQ7IpCkA7npxVZBy3NQH25DRF/OK1518WQfETPkBNryyAMUlbksI4Ab/bkpSocnSa0T7WHXN3Aea1aVLjSvesnvIGcVlmc2frcvIoqEsSj/M6cOqMVIBEGWkCfLkJ89tsE3rDLDJwe7Bl4hD6E=</Password></IdPwd><Etc></Etc></Account></AccountProfile>") ;
   		EAMAccount[] pwd = accProfile.get() ;
		System.out.println("xml:" + accProfile) ;
   		for(int i = 0; i < pwd.length;i++)	System.out.println("pwd["+i+"]:" + pwd[i]) ;
   		
   		EAMAccount newPW = new EAMAccount(conf,"ssoSid=dominologin,ssoSsid=5100,ou=service,o=sso30root", "1234", "1234" ) ;
   		System.out.println("newPW:" + newPW) ;
   		
   		EAMAccount accPW = accProfile.find("ssoSid=dominologin,ssoSsid=5100,ou=service,o=sso30root") ;
   		System.out.println("accPW:" + newPW) ;
   		System.out.println("newPW.equals(accPW):" + newPW.equals(accPW)) ;
   		
   		System.out.println("xml:" + accProfile) ;
   		if(newPW.equals(accPW) == false)
   			accProfile.update(newPW) ;
   			
   		pwd = accProfile.get() ;	
		for(int i = 0; i < pwd.length;i++)	System.out.println("pwd["+i+"]:" + pwd[i]) ;
		
		System.out.println("xml:" + accProfile) ;
		accProfile.setXML(accProfile.getXML()) ;
   		pwd = accProfile.get() ;	
		for(int i = 0; i < pwd.length;i++)	System.out.println("pwd["+i+"]:" + pwd[i]) ;
		
		///////////////////////////////////////
		// 인증서 바꾸기 TEST
		EAMConf newconf = new EAMConf(new File("D:\\newconf.props")) ;
		System.out.println("") ;
		System.out.println("") ;
		System.out.println("newconf.getPSCertFilePath()  :["+newconf.getPSCertFilePath()	+"]") ;
		System.out.println("newconf.getPSPrivateKeyPath():["+newconf.getPSPrivateKeyPath()	+"]") ;
		System.out.println("newconf.getPSPassword()      :["+newconf.getPSPassword()		+"]") ;
		
		System.out.println("old.xml:"+accProfile.getXML()) ;
		accProfile.update(newconf) ;
		System.out.println("new.xml:"+accProfile.getXML()) ;
		accProfile = new EAMAccountProfile(newconf, accProfile.getXML()) ;
		//accProfile.setXML(accProfile.getXML()) ;
		pwd = accProfile.get() ;
		System.out.println("pwd.length:" + pwd.length) ;
		for(int i = 0; i < pwd.length;i++)	System.out.println("pwd["+i+"]:" + pwd[i]) ;
		*****/
    }
}
