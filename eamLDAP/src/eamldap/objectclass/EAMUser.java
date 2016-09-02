package eamldap.objectclass;

import	eamldap.objectclass.*;
import	eamldap.attribute.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP�� ��������� Class.<br>
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
public class EAMUser extends EAMProperties
{
	/**	Default ������		*/	public EAMUser()					{						}
	/**	LDAP�� ����	������	*/	public EAMUser(LDAPAttributeSet l)	{	super.set(l) ;		}

	/** <b>[�ʼ�]</b>�����ID(uid)�� Set		*/	public void setUserID(String id)		{	super.setProperty("uid"			, id					) ;	}
	/** <b>[�ʼ�]</b>������̸�(cn)�� Set 		*/	public void setUserName(String cn)		{	super.setProperty("cn"			, cn					) ;	}
	/** <b>[�ʼ�]</b>��й�ȣ(userpassword) Set	*/	public void setPassword(String pw)		{	super.setProperty("userpassword", pw					) ;	}
	/** �ֹι�ȣ(ssorrn)�� Set					*/	public void setRrn(String rrn)			{	super.setProperty("ssorrn"		, rrn					) ;	}
	/** �����ּ�(mail)�� Set 					*/	public void setMail(String mail)		{	super.setProperty("mail"		, mail					) ;	}
	/** Profile����(ssoprofile)�� Set			*/	public void setProfile(EAMNVDS profile)	{	super.setProperty("ssoprofile"	, profile.getQuery()	) ;	}
	/** <b>[�ʼ�]</b>�Ҽ�����(ssousorgid)�� Set */	public void setOrgID(String id)			{	super.setProperty("ssousorgid"	, id					) ;	}
	/** �Ҽ��ֻ�������(ssousdivid)�� Set		*/	public void setDivID(String id)			{	super.setProperty("ssousdivid"	, id					) ;	}
	/** ����� ��踦 Set						*/	public void lock()						{	super.setProperty("ssolocked"	, "TRUE"				) ;	}
	/** ����� Ǯ���� Set						*/	public void unLock()					{	super.setProperty("ssolocked"	, "FALSE"				) ;	}
	/** ����� ���Ұ��� Set					*/	public void enable()					{	super.setProperty("ssodisabled"	, "0"					) ;	}
	/** ����� ��밡�� Set 					*/	public void disable()					{	super.setProperty("ssodisabled"	, "1"					) ;	}

	/** �����ID(uid)�� ���					*/	public String	getUserID()		{	return super.getProperty("uid"			, ""		) ;	}
	/** ������̸�(cn)�� ��� 					*/	public String	getUserName()	{	return super.getProperty("cn"			, ""		) ;	}
	/** �ֹι�ȣ(ssorrn)�� ���					*/	public String	getRrn()		{	return super.getProperty("ssorrn"		, ""		) ;	}
	/** �����ּ�(mail)�� ��� 					*/	public String	getMail()		{	return super.getProperty("mail"			, ""		) ;	}
	/** Profile����(ssoprofile)�� ���			*/	public EAMNVDS	getProfile()	{	return new EAMNVDS(super.getProperty("ssoprofile")	) ;	}
	/** �Ҽ�����(ssousorgid)�� ��� 			*/	public String	getOrgID()		{	return super.getProperty("ssousorgid"	, ""		) ;	}
	/** �Ҽ��ֻ�������(ssousdiv)�� ��� 		*/	public String	getDivID()		{	return super.getProperty("ssousdivid"	, ""		) ;	}
	/** ����� ��迩�θ� ���					*/	public boolean	isLocked()		{	return "TRUE".equals(super.getProperty("ssolocked")) ;	}
	/** ����� ��밡�ɿ��θ� ��� 				*/	public boolean	isDisabled()	{	return !("0".equals(super.getProperty("ssodisabled")));	}
}