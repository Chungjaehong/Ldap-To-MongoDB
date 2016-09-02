package eamldap.objectclass;

import	eamldap.objectclass.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP�� �������� Class.<br>
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
	/**	Default ������							*/	public EAMService()						{						}
	/**	LDAP�� ����	������						*/	public EAMService(LDAPAttributeSet l)	{	super.set(l) ;		}
	
	/** <b>[�ʼ� Add��]</b>��������ID�� Set	*/	public void setParentID(String id)		{	super.setProperty("pid"		, id	) ;	}
	/** <b>[�ʼ�]</b>����ID(ssosid)�� Set		*/	public void setServiceID(String id)		{	super.setProperty("ssosid"	, id	) ;	}
	/** <b>[�ʼ�]</b>���񽺸�(cn)�� Set 		*/	public void setServiceName(String cn)	{	super.setProperty("cn"		, cn	) ;	}

	/** ��������ID�� ���						*/	public String	getParentID()			{	return super.getProperty("pid"			,""				) ;	}
	/** ����ID�� ���							*/	public String	getServiceID()			{	return super.getProperty("ssosid"		,""				) ;	}
	/** ���񽺸� ��� 						*/	public String	getServiceName()		{	return super.getProperty("cn"			,""				) ;	}
	/** ����ID��θ� ���(findService�� Set)	*/	public String[]	getServiceIDPath()		{	return super.getProperty("svcidpath"	, new String[0]	) ;	}
	/** ���񽺸��θ� ���(findService�� Set)	*/	public String[]	getServiceNamePath()	{	return super.getProperty("svcnamepath"	, new String[0]	) ;	}
}

