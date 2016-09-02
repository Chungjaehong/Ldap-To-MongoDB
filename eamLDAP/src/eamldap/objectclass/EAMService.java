package eamldap.objectclass;

import	eamldap.objectclass.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP의 서비스정보 Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMService.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMService extends EAMProperties
{
	/**	Default 생성자							*/	public EAMService()						{						}
	/**	LDAP에 의한	생성자						*/	public EAMService(LDAPAttributeSet l)	{	super.set(l) ;		}
	
	/** <b>[필수 Add시]</b>상위서비스ID를 Set	*/	public void setParentID(String id)		{	super.setProperty("pid"		, id	) ;	}
	/** <b>[필수]</b>서비스ID(ssosid)를 Set		*/	public void setServiceID(String id)		{	super.setProperty("ssosid"	, id	) ;	}
	/** <b>[필수]</b>서비스명(cn)를 Set 		*/	public void setServiceName(String cn)	{	super.setProperty("cn"		, cn	) ;	}

	/** 상위서비스ID를 얻기						*/	public String	getParentID()			{	return super.getProperty("pid"			,""				) ;	}
	/** 서비스ID를 얻기							*/	public String	getServiceID()			{	return super.getProperty("ssosid"		,""				) ;	}
	/** 서비스명를 얻기 						*/	public String	getServiceName()		{	return super.getProperty("cn"			,""				) ;	}
	/** 서비스ID경로를 얻기(findService시 Set)	*/	public String[]	getServiceIDPath()		{	return super.getProperty("svcidpath"	, new String[0]	) ;	}
	/** 서비스명경로를 얻기(findService시 Set)	*/	public String[]	getServiceNamePath()	{	return super.getProperty("svcnamepath"	, new String[0]	) ;	}
}

