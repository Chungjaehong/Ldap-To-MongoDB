package eamldap.attribute;

import eamldap.attribute.*;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.lang.Long;
import java.lang.Integer;
/**
	LDAP자체의 Date형식의 createTimestamp, modifyTimestamp<br>
	EAM의 Date(long time / 1000)형식의 ssoLastLogonTime, ssoPwdChangeTime, (ssoPwdHistory), ssoValidFrom, ssoValidTo<br>
	등의 값을 setString()하거나 new EAMUacl(String b)를 사용하시면 좋습니다.<br>

	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMLdap.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX,Windows<br>
	Compiler	:	javac<br>
	History		:	20050329 Creation.<br>
	TAB Length	: 	4 Byte.<br>
<br>
*/
public class EAMUacl
{
	int type = -1 ;
	String ID = "" ;
	String ParentDN = "" ;
	boolean enable = false ;
	String Perm = "" ;
	
	public EAMUacl()				{	this.set("") ;		}
	public EAMUacl(String uacl)		{	this.set(uacl) ;	}
	
	public void set(String uacl)
	{
		if(uacl == null)		return ;
		if(uacl.length() < 4)	return ;
		
		try
		{
			this.type = Integer.parseInt(uacl.substring(0,1)) ;
		}
		catch(NumberFormatException e)
		{
			return ;
		}

		int space = uacl.indexOf(" ") ;
		this.ID = uacl.substring(1, space) ;
		this.enable = "$".equals(uacl.substring(space+1,space+2)) ;
		
		int nextspace = uacl.indexOf(" ",space+1) ;
		if(nextspace >= 0)
		{
			this.Perm = uacl.substring(space+2,nextspace) ;
			this.ParentDN = uacl.substring(nextspace+1) ;
		}
		else	
		{
			this.Perm = uacl.substring(space+2) ;
		}
	}
	
	public int		getType()		{	return this.type ;		}
	public String	getID()			{	return this.ID ;		}
	public boolean	enable()		{	return this.enable ;	}
	public String	getPermission()	{	return this.Perm ;		}
	public String	getParentDN()	{	return this.ParentDN ;	}

    public static void main (String args []) throws Exception 
    {	
    	EAMUacl ii = new EAMUacl("121000 $1 ssoSsid=is,ou=service,o=sso30root") ;
   		System.out.println("getType()      :[" + ii.getType()		+"]") ;
   		System.out.println("getID()        :[" + ii.getID()			+"]") ;
   		System.out.println("enable()       :[" + ii.enable()		+"]") ;
   		System.out.println("getPermission():[" + ii.getPermission()	+"]") ;
   		System.out.println("getParentDN()  :[" + ii.getParentDN()	+"]") ;
   		
   		ii  = new EAMUacl("2G463 $1") ;
   		System.out.println("getType()      :[" + ii.getType()		+"]") ;
   		System.out.println("getID()        :[" + ii.getID()			+"]") ;
   		System.out.println("enable()       :[" + ii.enable()		+"]") ;
   		System.out.println("getPermission():[" + ii.getPermission()	+"]") ;
   		System.out.println("getParentDN()  :[" + ii.getParentDN()	+"]") ;
    }
}
