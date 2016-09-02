package eamldap.objectclass;

import	eamldap.objectclass.*;
import	netscape.ldap.LDAPAttributeSet;

/**
	EAM LDAP의 그룹정보 Class.<br>
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
	/**	Default 생성자				*/	public EAMGroup()							{	super();			}
	/**	LDAP에 의한	생성자			*/	public EAMGroup(LDAPAttributeSet l)			{	super.set(l) ;		}
	/**	EAMProperties생성자			*/	//public EAMGroup(EAMProperties prop)			{	super.set(prop) ;	}

	/** <b>[필수]</b>그룹ID(uid)를 Set*/	public void setGroupID(String id)			{	super.setProperty("ssogid"		, id		) ;	}
	/** <b>[필수]</b>그룹명(cn)를 Set 	*/	public void setGroupName(String cn)			{	super.setProperty("cn"			, cn		) ;	}
	/** 메일주소(mail)를 Set 			*/	public void setMember(String[] userids)		{	super.setProperty("ssomember"	, userids	) ;	}
	/** 소속조직(ssousorgid)를 Set 	*/	public void setOrgID(String id)				{	super.setProperty("ssousorgid"	, id		) ;	}
	/** 소속최상위조직(ssousdiv)를 Set */	public void setDivID(String id)				{	super.setProperty("ssousdivid"	, id		) ;	}

	/** 그룹ID(uid)를 얻기			*/	public String	getGroupID()	{	return super.getProperty("ssogid"		,""				) ;	}
	/** 그룹이름(cn)를 얻기 			*/	public String	getGroupName()	{	return super.getProperty("cn"			,""				) ;	}
	/** 주민번호(ssorrn)를 얻기		*/	public String[]	getMember()		{	return super.getProperty("ssomember"	, new String[0]	) ;	}
	/** 소속조직(ssousorgid)를 얻기 	*/	public String	getOrgID()		{	return super.getProperty("ssousorgid"	,""				) ;	}
	/** 소속최상위조직(ssousdiv)를 얻기 */	public String	getDivID()		{	return super.getProperty("ssousdivid"	,""				) ;	}


	/**
		[Test용]
	*/
    public static void main (String args []) throws Exception
    {
    	EAMGroup grp = new EAMGroup() ;
    	grp.setGroupName("하이") ;

    	EAMProperties prp = (EAMProperties)grp ;
    	System.out.println("cn:"+ prp.getProperty("cn")) ;
    }
}

