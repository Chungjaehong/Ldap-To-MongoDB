package eamldap.objectclass;

import	eamldap.objectclass.*;
import	eamldap.attribute.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP의 사용자정보 Class.<br>
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
	/**	Default 생성자		*/	public EAMUser()					{						}
	/**	LDAP에 의한	생성자	*/	public EAMUser(LDAPAttributeSet l)	{	super.set(l) ;		}

	/** <b>[필수]</b>사용자ID(uid)를 Set		*/	public void setUserID(String id)		{	super.setProperty("uid"			, id					) ;	}
	/** <b>[필수]</b>사용자이름(cn)를 Set 		*/	public void setUserName(String cn)		{	super.setProperty("cn"			, cn					) ;	}
	/** <b>[필수]</b>비밀번호(userpassword) Set	*/	public void setPassword(String pw)		{	super.setProperty("userpassword", pw					) ;	}
	/** 주민번호(ssorrn)를 Set					*/	public void setRrn(String rrn)			{	super.setProperty("ssorrn"		, rrn					) ;	}
	/** 메일주소(mail)를 Set 					*/	public void setMail(String mail)		{	super.setProperty("mail"		, mail					) ;	}
	/** Profile정보(ssoprofile)를 Set			*/	public void setProfile(EAMNVDS profile)	{	super.setProperty("ssoprofile"	, profile.getQuery()	) ;	}
	/** <b>[필수]</b>소속조직(ssousorgid)를 Set */	public void setOrgID(String id)			{	super.setProperty("ssousorgid"	, id					) ;	}
	/** 소속최상위조직(ssousdivid)를 Set		*/	public void setDivID(String id)			{	super.setProperty("ssousdivid"	, id					) ;	}
	/** 사용자 잠김를 Set						*/	public void lock()						{	super.setProperty("ssolocked"	, "TRUE"				) ;	}
	/** 사용자 풀림를 Set						*/	public void unLock()					{	super.setProperty("ssolocked"	, "FALSE"				) ;	}
	/** 사용자 사용불가를 Set					*/	public void enable()					{	super.setProperty("ssodisabled"	, "0"					) ;	}
	/** 사용자 사용가를 Set 					*/	public void disable()					{	super.setProperty("ssodisabled"	, "1"					) ;	}

	/** 사용자ID(uid)를 얻기					*/	public String	getUserID()		{	return super.getProperty("uid"			, ""		) ;	}
	/** 사용자이름(cn)를 얻기 					*/	public String	getUserName()	{	return super.getProperty("cn"			, ""		) ;	}
	/** 주민번호(ssorrn)를 얻기					*/	public String	getRrn()		{	return super.getProperty("ssorrn"		, ""		) ;	}
	/** 메일주소(mail)를 얻기 					*/	public String	getMail()		{	return super.getProperty("mail"			, ""		) ;	}
	/** Profile정보(ssoprofile)를 얻기			*/	public EAMNVDS	getProfile()	{	return new EAMNVDS(super.getProperty("ssoprofile")	) ;	}
	/** 소속조직(ssousorgid)를 얻기 			*/	public String	getOrgID()		{	return super.getProperty("ssousorgid"	, ""		) ;	}
	/** 소속최상위조직(ssousdiv)를 얻기 		*/	public String	getDivID()		{	return super.getProperty("ssousdivid"	, ""		) ;	}
	/** 사용자 잠김여부를 얻기					*/	public boolean	isLocked()		{	return "TRUE".equals(super.getProperty("ssolocked")) ;	}
	/** 사용자 사용가능여부를 얻기 				*/	public boolean	isDisabled()	{	return !("0".equals(super.getProperty("ssodisabled")));	}
}