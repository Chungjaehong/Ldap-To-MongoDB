package eamldap.objectclass;

import	eamldap.objectclass.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP�� �������� Class.<br>
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
	/**	Default ������						*/	public EAMOrg()							{						}
	/**	LDAP�� ����	������					*/	public EAMOrg(LDAPAttributeSet l)		{	super.set(l) ;		}
	
	/** <b>[�ʼ� Add��]</b>��������ID�� Set	*/	public void setParentOrgID(String id)	{	super.setProperty("pid"		, id	) ;	}
	/** <b>[�ʼ�]</b>����ID(ssousid)�� Set	*/	public void setOrgID(String id)			{	super.setProperty("ssousid"	, id	) ;	}
	/** <b>[�ʼ�]</b>������(cn)�� Set 		*/	public void setOrgName(String cn)		{	super.setProperty("cn"		, cn	) ;	}

	/** ��������ID�� ���					*/	public String	getParentOrgID()		{	return super.getProperty("pid"			,""				) ;	}
	/** ����ID�� ���						*/	public String	getOrgID()				{	return super.getProperty("ssousid"		,""				) ;	}
	/** ������ ��� 						*/	public String	getOrgName()			{	return super.getProperty("cn"			,""				) ;	}
	/** ����ID��θ� ���(findOrg�� Set)	*/	public String[]	getOrgIDPath()			{	return super.getProperty("orgidpath"	, new String[0]	) ;	}
	/** �������θ� ���(findOrg�� Set)	*/	public String[]	getOrgNamePath()		{	return super.getProperty("orgnamepath"	, new String[0]	) ;	}
}

