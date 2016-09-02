package eamldap.objectclass;

import	eamldap.objectclass.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP�� �׷����� Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMGroup.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMGroup extends EAMProperties
{
	/**	Default ������				*/	public EAMGroup()							{	super();			}
	/**	LDAP�� ����	������			*/	public EAMGroup(LDAPAttributeSet l)			{	super.set(l) ;		}
	/**	EAMProperties������			*/	//public EAMGroup(EAMProperties prop)			{	super.set(prop) ;	}

	/** <b>[�ʼ�]</b>�׷�ID(uid)�� Set*/	public void setGroupID(String id)			{	super.setProperty("ssogid"		, id		) ;	}
	/** <b>[�ʼ�]</b>�׷��(cn)�� Set 	*/	public void setGroupName(String cn)			{	super.setProperty("cn"			, cn		) ;	}
	/** �����ּ�(mail)�� Set 			*/	public void setMember(String[] userids)		{	super.setProperty("ssomember"	, userids	) ;	}
	/** �Ҽ�����(ssousorgid)�� Set 	*/	public void setOrgID(String id)				{	super.setProperty("ssousorgid"	, id		) ;	}
	/** �Ҽ��ֻ�������(ssousdiv)�� Set */	public void setDivID(String id)				{	super.setProperty("ssousdivid"	, id		) ;	}

	/** �׷�ID(uid)�� ���			*/	public String	getGroupID()	{	return super.getProperty("ssogid"		,""				) ;	}
	/** �׷��̸�(cn)�� ��� 			*/	public String	getGroupName()	{	return super.getProperty("cn"			,""				) ;	}
	/** �ֹι�ȣ(ssorrn)�� ���		*/	public String[]	getMember()		{	return super.getProperty("ssomember"	, new String[0]	) ;	}
	/** �Ҽ�����(ssousorgid)�� ��� 	*/	public String	getOrgID()		{	return super.getProperty("ssousorgid"	,""				) ;	}
	/** �Ҽ��ֻ�������(ssousdiv)�� ��� */	public String	getDivID()		{	return super.getProperty("ssousdivid"	,""				) ;	}


	/**
		[Test��]
	*/
    public static void main (String args []) throws Exception
    {
    	EAMGroup grp = new EAMGroup() ;
    	grp.setGroupName("����") ;

    	EAMProperties prp = (EAMProperties)grp ;
    	System.out.println("cn:"+ prp.getProperty("cn")) ;
    }
}

