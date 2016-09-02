package eamldap.attribute;

import eamldap.*;
import eamldap.attribute.*;
import java.io.*;
import java.util.Vector;

/**
	EAM�� ssoAccountProfile�����Ͽ� Class.<br>

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
		������
	*/
	public EAMAccountProfile() 
	{
	}

	/**
		������(�ٷ�, &ltAccount>�� Parsing�� EAMAccount�� �� Parsing(��ȣȭ)�Ѵ�)
		@param	conf	Configure��ü[EAMLdap���� ���ٶ�.]
		@param	xml		Parsing�� AccountProfile ��üXML��
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
		&ltAccount>�� Parsing�� EAMAccount�� �� Parsing(��ȣȭ)�Ѵ�
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
		&ltAccount>�� serviceDN�� �ش��ϴ� ��ġIndex(Zero Base)���
		@param	serviceDN	����DN
		@return ��ġIndex(Zero Base)
	*/
	private int findIndex(String serviceDN)
	{
		for(int i = 0; i < this.account.size(); i++)
			if(serviceDN.equalsIgnoreCase(((EAMAccount)this.account.get(i)).getServiceDn()))
				return i ;
		return -1 ;
	}
	
	/**
		serviceDN�� �ش��ϴ� EAMAccount��ü ���
		@param	serviceDN	����DN
		@return �ش��ϴ� EAMAccount��ü
	*/
	public EAMAccount find(String serviceDN)
	{
		int index = this.findIndex(serviceDN) ;
		return (index >= 0) ? (EAMAccount)this.account.get(index) : null ;
	}

	/**
		EAMAccount��ü�� �߰� �Ǵ� �����ϱ�
		@param	acc	������ EAMAccount��ü
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
		Account(EAMAccount��ü)�� �����Ͽ� �߰� �Ǵ� �����ϱ�
		@param	serviceDN	������ ����DN
		@param	userID		������ �ش缭���� ����ID
		@param	password	������ �ش缭���� ������й�ȣ
	*/
	public void update(String serviceDN, String userID, String password)
	{
		this.update(new EAMAccount(this.conf, serviceDN, userID, password)) ;
	}
	
	/**
		Account(EAMAccount��ü)�� �����Ͽ� �߰� �Ǵ� �����ϱ�
		@param	newconf		������ ������ �� ����Ű�� �ִ� Configure
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
		Account(EAMAccount��ü)�� �����ϱ�
		@param	serviceDN	������ ����DN
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
		��� Account(EAMAccount��ü)�� ���
	*/
	public EAMAccount[] get()
	{
		EAMAccount[] ret = new EAMAccount[this.account.size()] ;
		for(int i = 0; i < this.account.size(); i++)
			ret[i] = (EAMAccount)this.account.get(i) ;
		return ret ;
	}

	/**
		����� ���ο� AccountProfile ��üXML�� ����(��ȣȭ)�ϱ�
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
	
	/** LDAP�� Write�� XML��	*/	public String getXML()		{	return this.xml	;		}
	/** No Comment				*/	public String toString()	{	return this.getXML() ;	}
		
	/**
		�־��� XML���� ���° �ش� Tag�κ� ���
		@param	xml		XML��
		@param	name	TagName
		@param	index	���° Tag(Zero Base)
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
		// ������ �ٲٱ� TEST
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
