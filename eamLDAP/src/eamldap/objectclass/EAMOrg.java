package eamldap.objectclass;

import	eamldap.objectclass.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP의 조직정보 Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMOrg.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMOrg extends EAMProperties
{
	/**	Default 생성자						*/	public EAMOrg()							{						}
	/**	LDAP에 의한	생성자					*/	public EAMOrg(LDAPAttributeSet l)		{	super.set(l) ;		}
	
	/** <b>[필수 Add시]</b>상위조직ID를 Set	*/	public void setParentOrgID(String id)	{	super.setProperty("pid"		, id	) ;	}
	/** <b>[필수]</b>조직ID(ssousid)를 Set	*/	public void setOrgID(String id)			{	super.setProperty("ssousid"	, id	) ;	}
	/** <b>[필수]</b>조직명(cn)를 Set 		*/	public void setOrgName(String cn)		{	super.setProperty("cn"		, cn	) ;	}

	/** 상위조직ID를 얻기					*/	public String	getParentOrgID()		{	return super.getProperty("pid"			,""				) ;	}
	/** 조직ID를 얻기						*/	public String	getOrgID()				{	return super.getProperty("ssousid"		,""				) ;	}
	/** 조직명를 얻기 						*/	public String	getOrgName()			{	return super.getProperty("cn"			,""				) ;	}
	/** 조직ID경로를 얻기(findOrg시 Set)	*/	public String[]	getOrgIDPath()			{	return super.getProperty("orgidpath"	, new String[0]	) ;	}
	/** 조직명경로를 얻기(findOrg시 Set)	*/	public String[]	getOrgNamePath()		{	return super.getProperty("orgnamepath"	, new String[0]	) ;	}
}

