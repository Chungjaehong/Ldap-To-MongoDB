package eamldap;

import	eamldap.*;
import	eamldap.attribute.*;
import	eamldap.objectclass.*;
import	netscape.ldap.*;
import	netscape.ldap.util.DN;
import	netscape.ldap.util.ConnectionPool;

import	java.util.*;
import	java.lang.*;
import	java.io.*;

import javax.naming.directory.SearchResult;

/**
	LDAP�� ���ʷ� �Ͽ� eamldap API�� ��� Class.<br>

	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMLdap.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
<br>
	�ֿ� Parameter<br>
	<table>
	<tr><td>dn   		</td><td>: Full DN�� �ִ´�. </td></tr>
	<tr><td>basedn		</td><td>: Search�� ���� ���� Full Dn�� �ִ´�.</td></tr>
	<tr><td>scope  		</td><td>: Search�� ���� �������� 1 �� �ٷ� �Ʒ� Object�� ���Ͽ�, 2�� �ڽ��� ������ �Ʒ��� ��� Object�� ���Ͽ�</td></tr>
	<tr><td>filter 		</td><td>: Search�� ���� ������ �ִ´�</td></tr>
	<tr><td>attr     	</td><td>: ����� Value�ϴ� Attribute�� Name�� �ִ´�. (�迭�� ���� ���� �ִ�.)</td></tr>
	</table>
	����> LDAP�� �⺻������ dn��,Attribute�� Name�� �ҹ���ó���� ���������� ó���Ѵ�.(Attribute�� Value�� ��Һ���)<br>
*/
public class EAMLdap
{
    protected	static	ConnectionPool	ldPool = null;
    protected	static	EAMConf			conf = null ;
	protected	static	EAMLog			log	= null ;
	protected			EAMError		err = new EAMError() ;

    static
    {
        try
        {
            conf	= new EAMConf() ;
	        log		= new EAMLog(conf) ;

	        
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "LDAP_HOST1:" + conf.getLdapHost1()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "LDAP_PORT1:" + conf.getLdapPort1()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "LDAP_HOST2:" + conf.getLdapHost2()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "LDAP_PORT2:" + conf.getLdapPort2()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "BIND_DN   :" + conf.getBindDn()  		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "BIND_PW   :" + conf.getBindPassWord()	) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "LOG_DIR   :" + conf.getLogDir() 		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "LOG_LEVEL :" + conf.getLogLevel()		) ;

			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "checkUserIDSize     :" + conf.checkUserIDSize()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "checkUserIDType     :" + conf.checkUserIDType()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "checkPasswordSize   :" + conf.checkPasswordSize()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "checkPasswordType   :" + conf.checkPasswordType()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "checkPasswordHistory:" + conf.checkPasswordHistory()	) ;

			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "useAccountProfile   :" + conf.useAccountProfile()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "getDefaultServiceDn :" + conf.getDefaultServiceDn()	) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "getPSCertFilePath   :" + conf.getPSCertFilePath()		) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "getPSPrivateKeyPath :" + conf.getPSPrivateKeyPath()	) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "getPSPassword       :" + conf.getPSPassword()			) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap]" + "getCACertFilePath   :" + conf.getCACertFilePath()		) ;			
		} 
		catch(Exception e ) 
		{
			e.printStackTrace();
		}
		
		for(int times = 0; times < 6 && ldPool == null; times++)
		{
			try
			{
				if(times % 2 == 0)
				{
					log.write(EAMLog.LOG_INFO,"[EAMLdap]Connect LDAP1....") ;
					
            		ldPool	= new ConnectionPool(conf.getInitPoolSize()	, conf.getMaxPoolSize()	,
            									conf.getLdapHost1()		, conf.getLdapPort1()	,
            									conf.getBindDn()		, conf.getBindPassWord()) ;
					break ;
				}
				else
				{
					log.write(EAMLog.LOG_INFO,"[EAMLdap]Connect LDAP2....") ;
        			ldPool	= new ConnectionPool(conf.getInitPoolSize()	, conf.getMaxPoolSize()	,
            									conf.getLdapHost2()		, conf.getLdapPort2()	,
            									conf.getBindDn()		, conf.getBindPassWord()) ;							
					break ;
				}
        	}   
			catch( LDAPException le ) 
			{   
				ldPool = null ;
				if(le.getLDAPResultCode() == 91 || le.getLDAPResultCode() == 81)
					continue ;
				else
        	    	le.printStackTrace();
        	}
        }
    }

	/**
		������
	*/
    public EAMLdap()
    {
    }

	/**
		�Ҹ���
	*/
    public void destroy()
    {
	}

	/** ������ �����ڵ�	��� 	*/	public int 		getErrorCode()	{	return this.err.getError() ;	}
	/** ������ �����޽��� ���	*/	public String	getErrorMsg()	{	return this.err.getErrorMsg() ;	}


	/**
		��������� ���
		@param	userid	�����ID
		@param	names	������ϴ� Attribute Name��
		@return	���������
	*/
	public EAMUser findUser(String userid, String[] names)
	{
		EAMUser ret = new EAMUser(this.readDn("uid="+userid+",ou=user,o=sso30root", names)) ;
		if(ret == null)			{	err.set(-9100) ;	return null;	}
		if(ret.size() == 0)		{	err.set(-9100) ;	return null;	}

		return ret ;
	}

	/**
		������̸����� ��������� ���
		@param	username	������̸�
		@param	names		������ϴ� Attribute Name��
		@param	sortAttr	Sorting Attribute Name
		@return	�����������
	*/
	public EAMUser[] findUserName(String username, String[] names, String sortAttr)
	{
		EAMUser[] ret = this.getUsers("ou=user,o=sso30root", 1, "(cn="+username+")", names, sortAttr, 0) ;

		if(ret == null)		{	err.set(-9100) ;	return new EAMUser[0] ;	}
		if(ret.length == 0)	{	err.set(-9100) ;	return new EAMUser[0] ;	}

		return ret ;
	}

	public EAMUser[] findUserName(String username)					{	return this.findUserName(username, null, null) ;	}
	public EAMUser[] findUserName(String username, String[] names)	{	return this.findUserName(username, names, null) ;	}

	/**
		�Ҽ��������� ����������� ���
		@param	orgid	�Ҽ�����ID
		@param	names	������ϴ� Attribute Name��
		@return	�����������
	*/
	public EAMUser[] findUserOrg(String orgid, String[] names)
	{
		EAMUser[] ret = this.getUsers("ou=user,o=sso30root", 1, "(ssousorgid="+orgid+")", names, null, 0) ;

		if(ret == null)		{	err.set(-9100) ;	return new EAMUser[0] ;	}
		if(ret.length == 0)	{	err.set(-9100) ;	return new EAMUser[0] ;	}
		
		return ret ;
	}

	//public EAMUser[] findUserOrg(String orgid)	{	return this.findUserOrg(orgid, null) ;	}

	/**
		�׷����� ���
		@param	groupid	�׷�ID
		@return	�׷�����
	*/
	public EAMGroup findGroup(String groupid)
	{
		EAMGroup ret = new EAMGroup(this.readDn("ssogid="+groupid+",ou=group,o=sso30root", null)) ;
		if(ret == null)		{	err.set(-9200) ;	return null	;	}
		if(ret.size() == 0)	{	err.set(-9200) ;	return null	;	}
		return ret ;
	}
	
	/**
		����ڰ� ����� ���� �׷������� ���
		@param	userid	�����ID
		@return	�׷�������
	*/
	public EAMGroup[] findGroupMember(String userid, String[] names)
	{
		EAMGroup[] props = this.getGroups("ou=group,o=sso30root", 1,"(ssomember="+userid+")", names, null, 0) ;
		if(props == null)		{	err.set(-9200) ;	return new EAMGroup[0] ;	}
		if(props.length == 0)	{	err.set(-9200) ;	return new EAMGroup[0] ;	}

		return props ;
	}

	/**
		�������� ���
		@param	baseDn	����ã��BaseDn
		@param	filter	ã��������
		@param	names	������ϴ� Attribute Name��
		@param	sortAttr	������ϴ� Attribute Name���� ������ Name
		@return	��������
	*/
	private EAMOrg[] findAllOrg(String baseDn, String filter, String[] names, String sortAttr)
	{
		EAMOrg[] ret = this.getOrgs(baseDn, 2, filter, names, sortAttr, 0) ;
		if(ret == null)		{	err.set(-9100) ;	return new EAMOrg[0] ;	}
		if(ret.length == 0)	{	err.set(-9100) ;	return new EAMOrg[0] ;	}

		for(int i = 0; i < ret.length; i++)
		{
			ret[i].setProperty("orgidpath"	,this.getPath(ret[i].getProperty("dn",""))) ;
			ret[i].setProperty("pid"		,this.getParentID(ret[i].getProperty("orgidpath",new String[0]))) ;
		}
		
		if(this.conf.useOrgNamePath())
		{
			for(int i = 0; i < ret.length; i++)
			{
				String[] orgIDPath = ret[i].getProperty("orgidpath", new String[0]) ;
				String[] orgNamePath = new String[orgIDPath.length] ;
				
				String tempDN = "ou=org,o=sso30root" ;
				for(int ii = 0; ii < orgIDPath.length - 1; ii++)
				{
					tempDN = "ssousid=" + orgIDPath[ii] + "," + tempDN ;
					EAMProperties prop = this.getEntry(tempDN,"cn") ;
					orgNamePath[ii] = prop.getProperty("cn", "") ;
				}
				orgNamePath[orgIDPath.length-1] = ret[i].getProperty("cn","") ;

				ret[i].setProperty("orgnamepath"	,orgNamePath) ;
			}
		}
		
		return ret ;
	}

	/**
		�������� ���
		@param	orgname	������
		@param	names	������ϴ� Attribute Name��
		@return	��������
	*/
	public EAMOrg[] findOrgName(String orgname, String[] names)
	{
		return this.findAllOrg("ou=org,o=sso30root","(cn="+orgname+")", names, "cn") ;
	}
	
	/**
		�������� ���
		@param	orgid	����ID
		@param	names	������ϴ� Attribute Name��
		@return	��������(������ null)
	*/
	public EAMOrg findOrg(String orgid, String[] names)
	{
		EAMOrg[] temp = this.getOrgs("ou=org,o=sso30root", 2, "(ssousid="+orgid+")", names, null, 0) ;

		if(temp == null)		{	err.set(-9100) ;	return null ;	}
		if(temp.length == 0)	{	err.set(-9100) ;	return null ;	}

		EAMOrg ret = temp[0] ;
		
		ret.setProperty("orgidpath"	,this.getPath(ret.getProperty("dn",""))) ;
		ret.setProperty("pid"		,this.getParentID(ret.getProperty("orgidpath",new String[0]))) ;
			
		if(this.conf.useOrgNamePath())
		{
			String[] orgIDPath = ret.getProperty("orgidpath", new String[0]) ;
			String[] orgNamePath = new String[orgIDPath.length] ;
				
			String tempDN = "ou=org,o=sso30root" ;
			for(int ii = 0; ii < orgIDPath.length - 1; ii++)
			{
				tempDN = "ssousid=" + orgIDPath[ii] + "," + tempDN ;
				EAMProperties prop = this.getEntry(tempDN,"cn") ;
				orgNamePath[ii] = prop.getProperty("cn", "") ;
			}
			orgNamePath[orgIDPath.length-1] = ret.getProperty("cn","") ;

			ret.setProperty("orgnamepath"	,orgNamePath) ;
		}

		return ret ;
	}
	
	//public EAMOrg findOrg(String orgid)	{	return this.findOrg(orgid, null) ;	}
	
	/**
		�������� ���
		@param	names	������ϴ� Attribute Name��
		@return	��������(������ null)
	*/
	public EAMService[] findServices(String baseDN, int scope, String filter, String[] names)
	{	
		EAMService[] temp = this.getServices("ou=service,o=sso30root", scope, filter, names, null, 0) ;

		if(temp == null)		{	err.set(-9400) ;	return new EAMService[0] ;	}
		if(temp.length == 0)	{	err.set(-9400) ;	return new EAMService[0] ;	}
		
		for(int i = 0; i < temp.length; i++)
		{
			temp[i].setProperty("svcidpath"	,this.getPath(temp[i].getProperty("dn",""))) ;
			temp[i].setProperty("pid"		,this.getParentID(temp[i].getProperty("svcidpath",new String[0]))) ;
			
			if(this.conf.useServiceNamePath())
			{
				String[] serviceIDPath = temp[i].getProperty("svcidpath", new String[0]) ;
				String[] serviceNamePath = new String[serviceIDPath.length] ;
				
				String tempDN = "ou=service,o=sso30root" ;
				for(int ii = 0; ii < serviceIDPath.length - 1; ii++)
				{
					tempDN = "ssossid=" + serviceIDPath[ii] + "," + tempDN ;
					EAMProperties prop = this.getEntry(tempDN,"cn") ;
					serviceNamePath[ii] = prop.getProperty("cn", "") ;
				}
				serviceNamePath[serviceIDPath.length-1] = temp[i].getProperty("cn","") ;

				temp[i].setProperty("svcnamepath"	,serviceNamePath) ;
			}
		}

		return temp ;
	}

	/**
		��������� �ֱ�(�̹� ������ ����, ������ �߰�)
		@param	usr	���������
		@return	�����ڵ�
	*/
	public int setUser(EAMUser usr)
	{
		int ret = this.modUser(usr) ;
		if(ret == -9100)	ret = this.addUser(usr) ;
		return ret ;
	}

	/**
		�׷����� �ֱ�(�̹� ������ ����, ������ �߰�)
		@param	grp	�׷�����
		@return	�����ڵ�
	*/
	public int setGroup(EAMGroup grp)
	{
		int ret = this.modGroup(grp) ;
		if(ret == -9200)	ret = this.addGroup(grp) ;
		return ret ;
	}

	/**
		CN���� ������� ���
		@param	dn	DN
		@return	cn[]
	*/
	public String[] getCNPath(String dn)
	{
		err.reset() ;
		DN ssoDN = new DN(dn) ;
		int len = ssoDN.countRDNs() ;
		len = (len <= 2) ? 0 : len - 2 ;

		String[] ret = new String[len] ;
		for(int i = 0; i < ret.length; i++)
		{
			if(i > 0)	ssoDN = ssoDN.getParent() ;
			//ret[ret.length - (i+1)] = ssoDN.toString() ;
			EAMProperties prop = this.getEntry(ssoDN.toString(), "cn") ;
			ret[ret.length - (i+1)] = prop.getProperty("cn","") ;
		}

		return ret ;
	}

	/**
		��������� ����
		@param	usr	������ ���������
		@return	�����ڵ�
	*/
	public int modUser(EAMUser usr)
	{
		err.reset() ;
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.modUser]" + usr) ;

		String userid	= usr.getProperty("uid"			,"") ;
		if(userid.equals(""))	return err.set(-9101) ;

		EAMUser lusr = this.findUser(userid, null) ;
		if(lusr == null)		return err.set(-9100) ;

		EAMProperties prop = lusr.getMODProperties((EAMProperties)usr) ;
		if(prop.size() == 0)	return err.set(-9900) ;
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getMODProperties]" + usr) ;

		if(prop.containsKey("cn"))				// "cn" --> "sn"�� ����
			prop.setProperty("sn"	, prop.getProperty("cn"	, ""	)) ;

		if(prop.containsKey("ssousorgid"))	// "ssousorgid" --> "ssousdivid", "ssouspathid" �� ����
		{
			String orgid = prop.getProperty("ssousorgid"	,"") ;

			log.write(EAMLog.LOG_TRACE,"[EAMLdap.modUser]orgid-->"+orgid) ;
			////////KT���� ����..
			EAMProperties[] props = this.getEntrys("ou=org,o=sso30root", 2,"(ssousid="+orgid+")", "dn") ;
			if(props == null)			return err.set(-9120) ;
			if(props.length == 0)		return err.set(-9120) ;

			String divDn = props[0].getProperty("dn","") ;
			log.write(EAMLog.LOG_TRACE,"[EAMLdap.modUser]divDn:"+divDn) ;
			if(!divDn.endsWith("sso30root"))	return err.set(-9130) ;

			String[] orgpath = this.getPath(divDn) ;
			if(orgpath.length == 0)	return err.set(-9130) ;

			prop.setProperty("ssousdivid"	,orgpath[0]	) ;
			prop.setProperty("ssouspathid"	,orgpath	) ;
		}

		if(prop.containsKey("userpassword"))	// "userpassword" --> "ssopwd", "ssopwdhistory", "ssoaccountprofile", "ssopwdchangetime" �� ����
		{
			String password	= usr.getProperty("userpassword","") ;
			if(password.equals(""))	return err.set(-9111) ;

			String divid 	= usr.getProperty("ssousdivid"	, lusr.getProperty("ssousdivid","")) ;
			if(divid.equals(""))	return err.set(-9130) ;

			EAMProperties div = this.getEntry("ssousid="+divid+",ou=org,o=sso30root") ;

			/////////////////////////////////////////////////////////////////
			//	UserID Check
			if(this.conf.checkUserIDSize())
			{
				int min = EAMUtil.toInteger(div.getProperty("ssouidminsize", "1")	) ;
				int max = EAMUtil.toInteger(div.getProperty("ssouidmaxsize", "32")	) ;

				if(userid.length() < min)	return err.set(-9102) ;
				if(userid.length() > max)	return err.set(-9103) ;
			}

			if(this.conf.checkUserIDType())
			{
				String Digit = "0123456789";
				String Alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" ;

				String except	= div.getProperty("ssouidexceptchar", "") ;
				int first		= EAMUtil.toInteger(div.getProperty("ssouidfirstchar"	, "0")	) ;
				int type		= EAMUtil.toInteger(div.getProperty("ssouidtype"		, "0")	) ;

				if(type == 1)	if(this.contains(Alpha		, userid) == false)			return err.set(-9104) ;
				if(type == 2)	if(this.contains(Digit		, userid) == false)			return err.set(-9105) ;
				if(type == 3)	if(this.contains(Alpha+Digit, userid) == false)			return err.set(-9106) ;

				for(int i = 0; i < except.length(); i++)
				for(int j = 0; j < userid.length();j++)
					if(userid.charAt(j) == except.charAt(i))							return err.set(-9107) ;

				if(first == 1)	if(this.contains(Alpha	, userid.charAt(0)) == false)	return err.set(-9108) ;
				if(first == 2)	if(this.contains(Digit	, userid.charAt(0)) == false)	return err.set(-9109) ;
			}

			/////////////////////////////////////////////////////////////////
			//	PassWord Check
			if(this.conf.checkPasswordSize())
			{
				int min = EAMUtil.toInteger(div.getProperty("ssopwdminsize", "0")	) ;
				int max = EAMUtil.toInteger(div.getProperty("ssopwdmaxsize", "63")	) ;
				if(password.length() < min)	return err.set(-9112) ;
				if(password.length() > max)	return err.set(-9113) ;
			}

			if(this.conf.checkPasswordType())
			{
				String Digit = "0123456789";
				String Alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" ;

				int type		= EAMUtil.toInteger(div.getProperty("ssopwdtype"		, "0")	) ;

				if(type == 1)	if(this.containsCount(Alpha,password) != password.length())	return err.set(-9114) ;
				if(type == 2)	if(this.containsCount(Digit,password) != password.length())	return err.set(-9115) ;
				if(type == 3)
				{
					if(this.containsCount(Alpha			, password) == 0)					return err.set(-9116) ;
					if(this.containsCount(Digit			, password) == 0)					return err.set(-9116) ;
				}
				if(type == 4)
				{
					if(this.containsCount(Alpha			, password) == 0)					return err.set(-9117) ;
					if(this.containsCount(Digit			, password) == 0)					return err.set(-9117) ;
					if(this.containsCount(Alpha+Digit	, password) == password.length())	return err.set(-9117) ;
				}
			}

			/////////////////////////////////////////////////////////////////
			//	PassWord Make
			String pwdmethod = "0" + div.getProperty("ssopwdmethod", "1") ;

			String[] objectclass = { "top", "person", "ssoUser", "sso30User" } ;

			EAMPwd	pwd = new EAMPwd() ;
			pwd.setPassword(pwdmethod,password) ;

			EAMDate	now = new EAMDate(System.currentTimeMillis()) ;

			prop.setProperty("ssopwd"			, pwd.getByte()			) ;
			prop.setProperty("ssopwdchangetime"	, now.getLongString()	) ;

			/////////////////////////////////////////////////////////////////
			//	��й�ȣ History Checking
			int historyNum = EAMUtil.toInteger(div.getProperty("ssopwdhistorynum", "0")) ;
			if(historyNum > 0 && this.conf.checkPasswordHistory())
			{
				byte[][] newPwdHistory = new byte[0][] ;
				String[] ssoPwdHistory = new String[0] ;

				pwd.setDate(now) ;	// ��¥�� �־�, ���ο� History���� �����Ѵ�.
				ssoPwdHistory = lusr.getProperty("ssopwdhistory", ssoPwdHistory) ;	// ������ History���� ��´�.

				int	 findOldest = -1 ;
				Date OldestDate = (Date)now ;

				if(ssoPwdHistory.length < historyNum)
					newPwdHistory = new byte[ssoPwdHistory.length+1][] ;
				else
					newPwdHistory = new byte[historyNum][] ;

				for(int i = 0; (i < ssoPwdHistory.length) && (i < historyNum) ; i++)
				{
					EAMPwd temp = new EAMPwd(ssoPwdHistory[i]) ;
					newPwdHistory[i] = temp.getByte() ;

					if((pwd.getPassword()).equals(temp.getPassword()))	return err.set(-9118) ;
						 ;
					if(OldestDate.after((Date)temp.getDate()))	// ��������� History���� ã�´�.
					{
						findOldest = i ;
						OldestDate = (Date)temp.getDate() ;
					}
				}

				if(ssoPwdHistory.length >= historyNum)
				{
					if(findOldest >= 0)
						newPwdHistory[findOldest]	= pwd.getByte() ;
				}
				else
				{
					newPwdHistory[newPwdHistory.length-1]	= pwd.getByte() ;
				}

				prop.setProperty("ssopwdhistory", newPwdHistory) ;
			}
		}

		log.write(EAMLog.LOG_TRACE,"[EAMLdap.modUser]" + prop) ;

		return this.modifyDn("uid="+userid+",ou=user,o=sso30root", prop.getLDAPModificationSet()) ;
	}

	/**
		�׷����� ����
		@param	grp	������ �׷�����
		@return	�����ڵ�
	*/
	public int modGroup(EAMGroup grp)
	{
		err.reset() ;

		String groupid	= grp.getProperty("ssogid"	,"") ;
		if(groupid.equals(""))	return err.set(-9201) ;

		EAMGroup lgrp = this.findGroup(groupid) ;
		if(lgrp == null)		return err.set(-9200) ;

		EAMProperties prop = lgrp.getMODProperties((EAMProperties)grp) ;
		if(prop.size() == 0)	return err.set(-9900) ;

		if(prop.containsKey("ssousorgid"))	// "ssousorgid" --> "ssousdivid", "ssouspathid" �� ����
		{
			String orgid = prop.getProperty("ssousorgid"	,"") ;

			//System.out.println("orgid-->"+orgid) ;
			////////KT���� ����..
			EAMProperties[] props = this.getEntrys("ou=org,o=sso30root", 2,"(ssousid="+orgid+")", "dn") ;
			if(props == null)			return err.set(-9230) ;
			if(props.length == 0)		return err.set(-9230) ;

			String divDn = props[0].getProperty("dn","") ;
			//System.out.println("divDn:"+divDn) ;
			if(!divDn.endsWith("sso30root"))	return err.set(-9240) ;

			String[] orgpath = this.getPath(divDn) ;
			if(orgpath.length == 0)	return err.set(-9240) ;

			prop.setProperty("ssousdivid"	,orgpath[0]	) ;
			prop.setProperty("ssouspathid"	,orgpath	) ;
		}

		if(prop.containsKey("ssomember"))	// "ssomember" --> "uniquemember" �� ����
		{
			//////////////////////////////////////////////////////////////
			//	uniquemember Setting...
			String[] ssomembers	= new String[0] ;
			ssomembers = prop.getProperty("ssomember"	,ssomembers) ;

			String[] uniquemembers = new String[ssomembers.length+1] ;
			uniquemembers[0] = "cn=policy admin,ou=Administrators,ou=config,o=sso30root" ;
			for(int i = 1; i < uniquemembers.length; i++)
				uniquemembers[i] = "uid="+ssomembers[i-1]+",ou=user,o=sso30root" ;
			prop.setProperty("uniquemember"	, uniquemembers	) ;
		}

		log.write(EAMLog.LOG_TRACE,"[EAMLdap.modGroup]" + prop) ;

		return this.modifyDn("ssogid="+groupid+",ou=group,o=sso30root", prop.getLDAPModificationSet()) ;
	}

	/**
		�������� ����
		@param	org	������ ��������
		@return	�����ڵ�
	*/
	public int modOrg(EAMOrg org)
	{
		boolean moveOrg = false ;
		err.reset() ;
		
		String[] names = { "cn" , "ssousid" } ;
		String orgID = org.getOrgID() ;
		if(orgID.equals(""))	return err.set(-9311) ;

		EAMOrg lorg = this.findOrg(orgID,names) ;
		if(lorg == null)		return err.set(-9300) ;
		
		if(lorg.getParentOrgID().equals(org.getParentOrgID()) == false)	// �ٸ���
		{
			if("".equals(org.getParentOrgID()) == false)	// RootOrg�� �ƴϸ�
			if("".equals(lorg.getParentOrgID()) == false)	// RootOrg�� �ƴϸ�
			{
				moveOrg = true ;
				//int ret = this.movOrg(org.getParentOrgID(), org.getOrgID()) ;
				//log.write(EAMLog.LOG_LDAP,"[EAMLdap.movOrg]("+lorg.getParentOrgID()+"/"+lorg.getOrgID()+"-->"+org.getParentOrgID()+"/"+org.getOrgID()+"):"+ret) ;
			}
		}
		
		EAMProperties prop = lorg.getMODProperties((EAMProperties)org) ;
		if(prop.size() == 0)	return (moveOrg) ? -9399 : err.set(-9900) ;
				
		int ret = this.modifyDn(lorg.getProperty("dn",""), prop.getLDAPModificationSet()) ;
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.modOrg]"+prop+":"+ret) ;
		
		return (moveOrg) ? -9399 : ret ;
	}
	
	public int movOrg(EAMOrg org)
	{
		int ret = 0 ;
		err.reset() ;
		
		String[] names = { "cn" , "ssousid" } ;
		String orgID = org.getOrgID() ;
		if(orgID.equals(""))	return err.set(-9311) ;

		EAMOrg lorg = this.findOrg(orgID,names) ;
		if(lorg == null)		return err.set(-9300) ;
		
		if(lorg.getParentOrgID().equals(org.getParentOrgID()) == false)	// �ٸ���
		{
			if("".equals(org.getParentOrgID()) == false)	// RootOrg�� �ƴϸ�
			if("".equals(lorg.getParentOrgID()) == false)	// RootOrg�� �ƴϸ�
			{
				ret = this.movOrg(org.getParentOrgID(), org.getOrgID()) ;
				log.write(EAMLog.LOG_LDAP,"[EAMLdap.movOrg]("+lorg.getParentOrgID()+"/"+lorg.getOrgID()+"-->"+org.getParentOrgID()+"/"+org.getOrgID()+"):"+ret) ;
			}
		}
		
		return ret ;
	}
	
	private int movOrg(String ParentID, String OrgID)
	{
		err.reset() ;
		
		String[] names = { "cn", "ssousid" } ;
		
		////////////////////////////////////////
		//	���� ���� �� ����
		EAMOrg parentOrg = this.findOrg(ParentID,names) ;
		if(parentOrg == null)	return err.set(-9333) ;	// ������������
		
		EAMOrg currentOrg = this.findOrg(OrgID,names) ;
		if(currentOrg == null)	return err.set(-9300) ;	// �ش���������
		
		EAMOrg[] subOrg = this.findAllOrg(currentOrg.getProperty("dn",""), "(objectClass=ssoUserStructure)",names,null);
		
		if(subOrg.length <= 0)	return err.set(-9300) ;	// �ش���������
		
		//////////////////////////////////////
		//	�������� ������ ����
		for(int i = subOrg.length - 1; i >= 0; i--)
			this.deleteDn(subOrg[i].getProperty("dn","")) ;

		//////////////////////////////////////
		//	���ο� �����ο��� ������ ����
		subOrg[0].setParentOrgID(ParentID) ;			// ���⼭���� �ٽ� ����
		for(int i = 0; i < subOrg.length; i++)
			this.addOrg(subOrg[i]) ;
		
		////////////////////////////////////////
		//	����� ������� �籸��
		currentOrg = this.findOrg(OrgID,names) ;
		if(currentOrg == null)	return err.set(-9300) ;	// �ش���������		
		subOrg = this.findAllOrg(currentOrg.getProperty("dn",""), "(objectClass=ssoUserStructure)",names,null);	
		
		String[] usernames = { "ssousdivid" } ;
		for(int i = 0; i < subOrg.length; i++)
		{
			String[] path = subOrg[i].getOrgIDPath() ;
			EAMUser[] users =this.findUserOrg(subOrg[i].getOrgID(), usernames) ;
			for(int j = 0; j < users.length; j++)
			{
				this.modifyDn(users[j].getProperty("dn",""), '*', "ssouspathid", path) ;
				
				if(path[0].equals(users[j].getDivID()) == false)
					this.modifyDn(users[j].getProperty("dn",""), '*', "ssousdivid", path[0]) ;
			}
		}
			
		return 0 ;
	}

	/**
		����� �����ϱ�
		@param	userid	�����ID
		@return	�����ڵ�
	*/
	public int delUser(String userid)
	{
		return this.deleteDn("uid="+userid+",ou=user,o=sso30root") ;
	}

	/**
		�׷� �����ϱ�
		@param	groupid	�׷�ID
		@return	�����ڵ�
	*/
	public int delGroup(String groupid)
	{
		return this.deleteDn("ssogid="+groupid+",ou=group,o=sso30root") ;
	}

	/**
		����� �߰��ϱ�
		@param	usr		���������
		@return	�����ڵ�
	*/
	public int addUser(EAMUser usr)
	{
		err.reset() ;

		String userid	= usr.getProperty("uid"			,"") ;
		String username	= usr.getProperty("cn"			,"") ;
		String password	= usr.getProperty("userpassword","") ;
		String orgid 	= usr.getProperty("ssousorgid"	,"") ;
		String divid 	= usr.getProperty("ssousdivid"	,"") ;

		if(userid.equals(""))	return err.set(-9101) ;
		if(password.equals(""))	return err.set(-9111) ;
		if(orgid.equals(""))	return err.set(-9121) ;
		if(username.equals(""))	return err.set(-9141) ;

		EAMProperties[] props ;
		if(divid.equals(""))	// ����ڱ����� ������(KT�̿� Site)
			props = this.getEntrys("ou=org,o=sso30root", 2,"(ssousid="+orgid+")", "dn") ;
		else
			props = this.getEntrys("ssousid="+divid+",ou=org,o=sso30root", 2,"(ssousid="+orgid+")", "dn") ;
		if(props == null)			return err.set(-9120) ;
		if(props.length == 0)		return err.set(-9120) ;

		String divDn = props[0].getProperty("dn","") ;
		if(!divDn.endsWith("sso30root"))	return err.set(-9130) ;

		String[] orgpath = this.getPath(divDn) ;
		if(orgpath.length == 0)	return err.set(-9130) ;

		divid 	= orgpath[0] ;
		usr.setProperty("ssousdivid"	,orgpath[0]	) ;
		usr.setProperty("ssouspathid"	,orgpath	) ;

		EAMProperties div = this.getEntry("ssousid="+divid+",ou=org,o=sso30root") ;

		/////////////////////////////////////////////////////////////////
		//	UserID Check
		if(this.conf.checkUserIDSize())
		{
			int min = EAMUtil.toInteger(div.getProperty("ssouidminsize", "1")	) ;
			int max = EAMUtil.toInteger(div.getProperty("ssouidmaxsize", "32")	) ;

			if(userid.length() < min)	return err.set(-9102) ;
			if(userid.length() > max)	return err.set(-9103) ;
		}

		if(this.conf.checkUserIDType())
		{
			String Digit = "0123456789";
			String Alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" ;

			String except	= div.getProperty("ssouidexceptchar", "") ;
			int first		= EAMUtil.toInteger(div.getProperty("ssouidfirstchar"	, "0")	) ;
			int type		= EAMUtil.toInteger(div.getProperty("ssouidtype"		, "0")	) ;

			if(type == 1)	if(this.contains(Alpha		, userid) == false)			return err.set(-9104) ;
			if(type == 2)	if(this.contains(Digit		, userid) == false)			return err.set(-9105) ;
			if(type == 3)	if(this.contains(Alpha+Digit, userid) == false)			return err.set(-9106) ;

			for(int i = 0; i < except.length(); i++)
			for(int j = 0; j < userid.length();j++)
				if(userid.charAt(j) == except.charAt(i))							return err.set(-9107) ;

			if(first == 1)	if(this.contains(Alpha	, userid.charAt(0)) == false)	return err.set(-9108) ;
			if(first == 2)	if(this.contains(Digit	, userid.charAt(0)) == false)	return err.set(-9109) ;
		}

		/////////////////////////////////////////////////////////////////
		//	PassWord Check
		if(this.conf.checkPasswordSize())
		{
			int min = EAMUtil.toInteger(div.getProperty("ssopwdminsize", "0")	) ;
			int max = EAMUtil.toInteger(div.getProperty("ssopwdmaxsize", "63")	) ;
			if(password.length() < min)	return err.set(-9112) ;
			if(password.length() > max)	return err.set(-9113) ;
		}

		if(this.conf.checkPasswordType())
		{
			String Digit = "0123456789";
			String Alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" ;

			int type		= EAMUtil.toInteger(div.getProperty("ssopwdtype"		, "0")	) ;

			if(type == 1)	if(this.containsCount(Alpha,password) != password.length())	return err.set(-9114) ;
			if(type == 2)	if(this.containsCount(Digit,password) != password.length())	return err.set(-9115) ;
			if(type == 3)
			{
				if(this.containsCount(Alpha			, password) == 0)					return err.set(-9116) ;
				if(this.containsCount(Digit			, password) == 0)					return err.set(-9116) ;
			}
			if(type == 4)
			{
				if(this.containsCount(Alpha			, password) == 0)					return err.set(-9117) ;
				if(this.containsCount(Digit			, password) == 0)					return err.set(-9117) ;
				if(this.containsCount(Alpha+Digit	, password) == password.length())	return err.set(-9117) ;
			}
		}

		/////////////////////////////////////////////////////////////////
		//	PassWord Make
		String pwdmethod = "0" + div.getProperty("ssopwdmethod", "1") ;

		String[] objectclass = { "top", "person", "ssoUser", "sso30User" } ;

		EAMPwd	pwd = new EAMPwd() ;
		pwd.setPassword(pwdmethod,password) ;
		EAMDate	now = new EAMDate(System.currentTimeMillis()) ;

		/////////////////////////////////////////////////////////////////
		//	setProperty
		usr.setProperty("objectclass"		, usr.getProperty("objectclass"			, objectclass			)) ;
		usr.setProperty("ssopwd"			, usr.getProperty("ssopwd"				, pwd.getByte()			)) ;
		usr.setProperty("mail"				, usr.getProperty("mail"				, ""					)) ;
		usr.setProperty("ssorrn"			, usr.getProperty("ssorrn"				, ""					)) ;
		usr.setProperty("sn"				, usr.getProperty("cn"					, ""					)) ;
		usr.setProperty("ssoaccessallowed"	, usr.getProperty("ssoaccessallowed"	, "FALSE"				)) ;
		usr.setProperty("ssocertdn1"		, usr.getProperty("ssocertdn1"			, ""					)) ;
		usr.setProperty("ssocertdn2"		, usr.getProperty("ssocertdn2"			, ""					)) ;
		usr.setProperty("description"		, usr.getProperty("description"			, ""					)) ;
		usr.setProperty("ssoaccountprofile"	, usr.getProperty("ssoaccountprofile"	, ""					)) ;
		usr.setProperty("ssodisabled"		, usr.getProperty("ssodisabled"			, "0"					)) ;
		usr.setProperty("ssolocked"			, usr.getProperty("ssolocked"			, "FALSE"				)) ;
		usr.setProperty("ssolastlogontime"	, usr.getProperty("ssolastlogontime"	, now.getLongString()	)) ;
		usr.setProperty("ssoprofile"		, usr.getProperty("ssoprofile"			, ""					)) ;
		usr.setProperty("ssopwdchangetime"	, usr.getProperty("ssopwdchangetime"	, now.getLongString()	)) ;
		usr.setProperty("ssopwdmustchange"	, usr.getProperty("ssopwdmustchange"	, "FALSE"				)) ;
		usr.setProperty("ssopwdretrycount"	, usr.getProperty("ssopwdretrycount"	, "0"					)) ;
		usr.setProperty("ssopwdretrytime"	, usr.getProperty("ssopwdretrytime"		, "-1"					)) ;
		usr.setProperty("ssovalidfrom"		, usr.getProperty("ssovalidfrom"		, "0"					)) ;
		usr.setProperty("ssovalidto"		, usr.getProperty("ssovalidto"			, "2147399999"			)) ;
		usr.setProperty("ssowhen"			, usr.getProperty("ssowhen"				, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")) ;
		usr.setProperty("ssowhere"			, usr.getProperty("ssowhere"			, "00000000FFFFFFFF*"	)) ;

		if(EAMUtil.toInteger(div.getProperty("ssopwdhistorynum", "0")) > 0)
		{
			pwd.setDate(now) ;
			byte[][] history = new byte[1][] ;
			history[0] = pwd.getByte() ;
			usr.setProperty("ssopwdhistory"	, usr.getProperty("ssopwdhistory"		, history				)) ;
		}

		log.write(EAMLog.LOG_TRACE,"[EAMLdap.addUser]" + usr) ;

		return this.addDn("uid="+userid+",ou=user,o=sso30root", usr.getLDAPAttributeSet()) ;
	}

	/**
		�׷� �߰��ϱ�
		@param	grp		�׷�����
		@return	�����ڵ�
	*/
	public int addGroup(EAMGroup grp)
	{
		err.reset() ;

		String groupid	= grp.getProperty("ssogid"		,"") ;
		String groupname= grp.getProperty("cn"			,"") ;
		String orgid 	= grp.getProperty("ssousorgid"	,"") ;
		String divid 	= grp.getProperty("ssousdivid"	,"") ;

		if(groupid.equals(""))		return err.set(-9201) ;
		if(groupname.equals(""))	return err.set(-9202) ;

		if(orgid.equals("") == false)
		{
			EAMProperties[] props ;
			if(divid.equals(""))	// ����ڱ����� ������(KT�̿� Site)
				props = this.getEntrys("ou=org,o=sso30root", 2,"(ssousid="+orgid+")", "dn") ;
			else
				props = this.getEntrys("ssousid="+divid+",ou=org,o=sso30root", 2,"(ssousid="+orgid+")", "dn") ;

			if(props == null)			return err.set(-9230) ;
			if(props.length == 0)		return err.set(-9230) ;

			String divDn = props[0].getProperty("dn","") ;
			if(!divDn.endsWith("sso30root"))	return err.set(-9240) ;

			String[] orgpath = this.getPath(divDn) ;
			if(orgpath.length == 0)	return err.set(-9240) ;

			divid 	= orgpath[0] ;
			grp.setProperty("ssousdivid"	,orgpath[0]	) ;
			grp.setProperty("ssouspathid"	,orgpath	) ;
		}

		//////////////////////////////////////////////////////////////
		//	uniquemember Setting...
		String[] ssomembers	= new String[0] ;
		ssomembers = grp.getProperty("ssomember"	,ssomembers) ;

		String[] uniquemembers = new String[ssomembers.length+1] ;
		uniquemembers[0] = "cn=policy admin,ou=Administrators,ou=config,o=sso30root" ;
		for(int i = 1; i < uniquemembers.length; i++)
			uniquemembers[i] = "uid="+ssomembers[i-1]+",ou=user,o=sso30root" ;
		grp.setProperty("uniquemember"	, uniquemembers	) ;


		String[] objectclass = { "top", "groupOfUniqueNames", "ssoUserGroup" } ;

		/////////////////////////////////////////////////////////////////
		//	setProperty
		grp.setProperty("objectclass"		, grp.getProperty("objectclass"			, objectclass			)) ;
		grp.setProperty("description"		, grp.getProperty("description"			, ""					)) ;

		log.write(EAMLog.LOG_TRACE,"[EAMLdap.addGroup]" + grp) ;

		return this.addDn("ssogid="+groupid+",ou=group,o=sso30root", grp.getLDAPAttributeSet()) ;
	}

	/**
		���� �߰��ϱ�
		@param	org		��������
		@return	�����ڵ�
	*/
	public int addOrg(EAMOrg org)
	{
		err.reset() ;

		String orgid	= org.getProperty("ssousid"		,"") ;
		String orgname	= org.getProperty("cn"			,"") ;
		String parentid	= org.getProperty("pid"			,"") ;
		String parentDn = "" ;

		if(orgid.equals(""))	return err.set(-9301) ;
		if(orgname.equals(""))	return err.set(-9302) ;

		if(orgid.equals("") == false)
		{
			EAMProperties[] props ;
			props = this.getEntrys("ou=org,o=sso30root", 2,"(ssousid="+parentid+")", "dn") ;

			if(props == null)			return err.set(-9303) ;
			if(props.length == 0)		return err.set(-9303) ;

			parentDn = props[0].getProperty("dn","") ;
			if(!parentDn.endsWith("sso30root"))	return err.set(-9303) ;
		}

		String[] objectclass = { "top", "ssoUserStructure" } ;

		/////////////////////////////////////////////////////////////////
		//	setProperty
		org.setProperty("objectclass"	, org.getProperty("objectclass"	, objectclass	)) ;
		org.setProperty("description"	, org.getProperty("description"	, ""			)) ;
		org.setProperty("ssowhen"		, org.getProperty("ssowhen"		, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"	)) ;
		org.setProperty("ssowhere"		, org.getProperty("ssowhere"	, "00000000FFFFFFFF*"							)) ;

		log.write(EAMLog.LOG_TRACE,"[EAMLdap.addOrg]" + org) ;

		return this.addDn("ssousid="+orgid+"," + parentDn, org.getLDAPAttributeSet()) ;
	}

	/**
		�׷��� ���
		@param	groupid	�׷�ID
		@return	��������ID��
	*/
	public String[] getMember(String groupid)
	{
		EAMGroup grp = this.findGroup(groupid) ;
		if(grp == null)		{	err.set(-9200) ;	return new String[0] ;	}
		return grp.getMember() ;
	}
	
	/**
		����ڰ� ����� ���� �׷�ID�� ���
		@param	userid	�����ID
		@return	�׷�ID��
	*/
	public String[] findGroupIDMember(String userid)
	{
		String[] names = { "ssogid" } ;
		EAMGroup[] props = this.getGroups("ou=group,o=sso30root", 1,"(ssomember="+userid+")", names, null, 0) ;
		if(props == null)		{	err.set(-9200) ;	return new String[0] ;	}
		if(props.length == 0)	{	err.set(-9200) ;	return new String[0] ;	}
		
		String[] ret = new String[props.length] ;
		for(int i = 0; i < ret.length; i++)
			ret[i] = props[i].getGroupID() ;
		return ret ;
	}
	
	/**
		�׷��� �ΰ�.
		@param	groupid	�׷�ID
		@param	userid	�����ID
		@return	true(�����), false(����ƴ�)
	*/	
	public boolean isMember(String groupid, String userid)
	{
		String[] names = { "ssogid" } ;
		EAMGroup[] props = this.getGroups("ssogid="+groupid+",ou=group,o=sso30root", 0,"(ssomember="+userid+")", names, null, 0) ;
		if(props == null)		{	err.set(-9200) ;	return false ;	}
		if(props.length == 0)	{	err.set(-9200) ;	return false ;	}
		return true ;
	}

	/**
		�׷��� �߰�
		@param	groupid	�׷�ID
		@param	userid	�߰��� �����ID
		@return	����(�����ڵ�), ���(�߰��� �����)
	*/
	public int addMember(String groupid, String userid)
	{
		if(groupid == null)		return err.set(-9201) ;
		if(groupid.equals(""))	return err.set(-9201) ;
		if(userid == null)		return err.set(-9203) ;
		if(userid.equals(""))	return err.set(-9203) ;

		int ret = this.modifyDn("ssogid="+ groupid + ",ou=group,o=sso30root", '+', "ssomember", userid) ;
		if(ret == 30020)	return err.set(0) ;
		else				return ret ;
	}

	/**
		�׷��� ����
		@param	groupid	�׷�ID
		@param	userid	������ �����ID
		@return	����(�����ڵ�), ���(������ �����)
	*/
	public int delMember(String groupid, String userid)
	{
		if(groupid == null)		return err.set(-9201) ;
		if(groupid.equals(""))	return err.set(-9201) ;
		if(userid == null)		return err.set(-9203) ;
		if(userid.equals(""))	return err.set(-9203) ;

		int ret = this.modifyDn("ssogid="+ groupid + ",ou=group,o=sso30root", '-', "ssomember", userid) ;
		if(ret == 30016)	return err.set(0) ;
		else				return ret ;
	}

	/**
		�Ϲ����� Entry������ ���
		@param	dn		���� Entry DN
		@return EAMProperties
	*/
	public EAMProperties getEntry(String dn)
	{
		return new EAMProperties(this.readDn(dn, null)) ;
	}

	public LDAPProperties getLDAPEntry(String dn)
	{
		LDAPProperties temp = new LDAPProperties() ;
		temp.set(this.readDn(dn, null)) ;
		return temp ;
	}

	/**
		�Ϲ����� Entry������ ���
		@param	dn		���� Entry DN
		@param	attr	���� ���̸�
		@return EAMProperties
	*/
	public EAMProperties getEntry(String dn, String attr)
	{
		String[] temp = new String[1] ;
		temp[0] = attr ;
		return new EAMProperties(this.readDn(dn, temp)) ;
	}

	/**
		�Ϲ����� Entry������ ���
		@param	dn		���� Entry DN
		@param	attrs	���� ���̸���
		@return EAMProperties
	*/
	public EAMProperties getEntry(String dn, String[] attrs)
	{
		return new EAMProperties(this.readDn(dn, attrs)) ;
	}

	public LDAPAttributeSet readDn(String dn, String[] attrs)
	{
		err.reset() ;

		LDAPAttributeSet	attrset = null ;
		LDAPEntry			entry = null ;
		LDAPConnection 		ld = null ;

		if(ldPool == null)	return attrset ;

		try
		{
			ld = ldPool.getConnection();
			if(attrs == null)	entry = ld.read( dn );
			else				entry = ld.read( dn, attrs );

			attrset 	= entry.getAttributeSet() ;
			if(attrset != null)
				attrset.add(new LDAPAttribute("dn", entry.getDN())) ;


		}
		catch( LDAPException e )
		{
			err.set(e) ;
      	}
		//catch( LDAPReferralException re)
		//{
		//	LDAPUrl[] ldapurl = re.getURLs() ;
		//	for(int i = 0; i < ldapurl.length; i++)
		//		log.write(EAMLog.LOG_INFO,"[EAMLdap.readDn][[Referral]]" + ldapurl[i]) ; 
		//}
      	finally
      	{
      		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.readDn]" + err) ;
			log.write(EAMLog.LOG_TRACE,"[EAMLdap.readDn]" + attrset) ;
		}

		return attrset ;
	}

	/**
		�Ϲ����� Entry������ ���
		@param	basedn	ã����� DN
		@param	scope	ã�⿵��
		@param	filter	ã������
		@return EAMProperties��
	*/
	public EAMProperties[] getEntrys(String basedn, int scope, String filter)
	{
		return this.getEntrys(basedn, scope, filter, 0) ;
	}

	public EAMProperties[] getEntrys(String basedn, int scope, String filter, int maxResults)
	{
		EAMProperties[] ret = new EAMProperties[0] ;
		log.write(EAMLog.LOG_INFO,"[EAMLdap.getEntrys]("+basedn+","+scope+","+filter+");") ;
		Vector temp = this.searchVector(basedn, scope, filter, null, null, maxResults) ;
		if(temp != null)
		{
		 	ret = new EAMProperties[temp.size()] ;
		 	for(int i = 0; i < ret.length; i++)
		 	{
		 		ret[i] = (EAMProperties)temp.get(i) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getEntrys]" + ret[i]) ;
		 	}
		}
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getEntrys]" + err) ;

		return ret ;
	}

	/**
		�Ϲ����� Entry������ ���
		@param	basedn	ã����� DN
		@param	scope	ã�⿵��
		@param	filter	ã������
		@param	attr	ã�� ���̸�
		@return EAMProperties��
	*/
	public EAMProperties[] getEntrys(String basedn, int scope, String filter, String attr)
	{
		String[] attrs = new String[1] ;
		attrs[0] = attr ;
		return this.getEntrys(basedn, scope, filter, attrs, null, 0) ;
	}

	public EAMProperties[] getEntrys(String basedn, int scope, String filter, String attr, int maxResults)
	{
		String[] attrs = new String[1] ;
		attrs[0] = attr ;
		return this.getEntrys(basedn, scope, filter, attrs, null, maxResults) ;
	}

	/**
		�Ϲ����� Entry������ ���
		@param	basedn	ã����� DN
		@param	scope	ã�⿵��
		@param	filter	ã������
		@param	attr	ã�� ���̸���
		@return EAMProperties��
	*/
	public EAMProperties[] getEntrys(String basedn, int scope, String filter, String[] attr)
	{
		return this.getEntrys(basedn, scope, filter, attr, null, 0) ;
	}

	public EAMProperties[] getEntrys(String basedn, int scope, String filter, String[] attr, int maxResults)
	{
		return this.getEntrys(basedn, scope, filter, attr, null, maxResults) ;
	}

	public EAMProperties[] getEntrys(String basedn, int scope, String filter, String[] attr, String sortAttr)
	{
		return this.getEntrys(basedn, scope, filter, attr, sortAttr, 0) ;
	}

	protected EAMUser[] getUsers(String basedn, int scope, String filter, String[] attr, String sortAttr, int maxResults)
	{
		EAMUser[] ret = new EAMUser[0] ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.getUsers]("+basedn+","+scope+","+filter+","+sortAttr+");") ;
		Vector temp = this.searchVector(basedn, scope, filter, attr, sortAttr, maxResults) ;
		if(temp != null)
		{
		 	ret = new EAMUser[temp.size()] ;
		 	for(int i = 0; i < ret.length; i++)
		 	{
		 		ret[i] = (EAMUser)temp.get(i) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getUsers]" + ret[i]) ;
		 	}
		}
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getUsers]" + err) ;

		return ret ;
	}
	
	protected EAMGroup[] getGroups(String basedn, int scope, String filter, String[] attr, String sortAttr, int maxResults)
	{
		EAMGroup[] ret = new EAMGroup[0] ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.getGroups]("+basedn+","+scope+","+filter+","+sortAttr+");") ;
		Vector temp = this.searchVector(basedn, scope, filter, attr, sortAttr, maxResults) ;
		if(temp != null)
		{
		 	ret = new EAMGroup[temp.size()] ;
		 	for(int i = 0; i < ret.length; i++)
		 	{
		 		ret[i] = (EAMGroup)temp.get(i) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getGroups]" + ret[i]) ;
		 	}
		}
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getGroups]" + err) ;

		return ret ;
	}
	
	protected EAMOrg[] getOrgs(String basedn, int scope, String filter, String[] attr, String sortAttr, int maxResults)
	{
		EAMOrg[] ret = new EAMOrg[0] ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.getOrgs]("+basedn+","+scope+","+filter+","+sortAttr+");") ;
		Vector temp = this.searchVector(basedn, scope, filter, attr, sortAttr, maxResults) ;
		if(temp != null)
		{
		 	ret = new EAMOrg[temp.size()] ;
		 	for(int i = 0; i < ret.length; i++)
		 	{
		 		ret[i] = (EAMOrg)temp.get(i) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getOrgs]" + ret[i]) ;
		 	}
		}
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getOrgs]" + err) ;

		return ret ;
	}
	
	protected EAMService[] getServices(String basedn, int scope, String filter, String[] attr, String sortAttr, int maxResults)
	{
		EAMService[] ret = new EAMService[0] ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.getServices]("+basedn+","+scope+","+filter+","+sortAttr+");") ;
		Vector temp = this.searchVector(basedn, scope, filter, attr, sortAttr, maxResults) ;
		if(temp != null)
		{
		 	ret = new EAMService[temp.size()] ;
		 	for(int i = 0; i < ret.length; i++)
		 	{
		 		ret[i] = (EAMService)temp.get(i) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getServices]" + ret[i]) ;
		 	}
		}
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getServices]" + err) ;

		return ret ;
	}

	public EAMProperties[] getEntrys(String basedn, int scope, String filter, String[] attr, String sortAttr, int maxResults)
	{
		EAMProperties[] ret = new EAMProperties[0] ;
		log.write(EAMLog.LOG_INFO,"[EAMLdap.getEntrys]("+basedn+","+scope+","+filter+","+sortAttr+");") ;
		Vector temp = this.searchVector(basedn, scope, filter, attr, sortAttr, maxResults) ;
		if(temp != null)
		{
		 	ret = new EAMProperties[temp.size()] ;
		 	for(int i = 0; i < ret.length; i++)
		 	{
		 		ret[i] = (EAMProperties)temp.get(i) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getEntrys]" + ret[i]) ;
		 	}
		}
		log.write(EAMLog.LOG_TRACE,"[EAMLdap.getEntrys]" + err) ;

		return ret ;
	}

	private boolean existDn(String basedn, int scope, String filter)
	{
		boolean ret = false ;
		err.reset() ;
		String[] attrs = { "cn" } ;
		if(ldPool == null)	return false ;
		LDAPConnection ld = null ;
		try
		{		
			log.write(EAMLog.LOG_TRACE,"[EAMLdap.existDn]();") ;
			ld = ldPool.getConnection();
			LDAPSearchConstraints 	cons = ld.getSearchConstraints();
			cons.setBatchSize( 1 );
			cons.setMaxResults( 0 );
			
			log.write(EAMLog.LOG_TRACE,"[EAMLdap.existDn]("+basedn+","+scope+","+filter+");") ;
			LDAPSearchResults res = ld.search(basedn, scope, filter, attrs, false, cons);
			ret = res.hasMoreElements() ;
		}
		catch(LDAPException e)
		{
			ret = false ;
			err.set(e) ;
		}
		finally
		{
			if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO	,"[EAMLdap.existDn]" + err) ;
		}
		return ret ;
	}
	
	/**
		LDAP Search �����Լ�
		@param	basedn		Search Base DN(ã�� ���� ��ġ)
		@param	scope		0:Base Scope(BaseDN�� ã��)1:One Level(�ٷ� �Ʒ��� ã��), 2:SubScope(BaseDN���� �� �Ʒ� ��ο��� ã��)
		@param	filter		ã������
		@param	attrs		ã�� Attribute Name��
		@param	sortAttr	������ Attribute Name
		@param	maxResults	ã��Ǽ� ����( 0:�̸� ���� )
	*/
	private Vector searchVector(String basedn, int scope, String filter, String[] attrs, String sortAttr, int maxResults)
	{
		err.reset() ;

		Vector vec = new Vector() ;
		if(ldPool == null)	return vec ;

		LDAPConnection ld = null ;

		try
		{
			log.write(EAMLog.LOG_TRACE,"[EAMLdap.searchVector]();") ;
			ld = ldPool.getConnection();
			LDAPSearchConstraints 	cons = ld.getSearchConstraints();
			cons.setBatchSize( 1 );
			cons.setMaxResults( maxResults );

			log.write(EAMLog.LOG_TRACE,"[EAMLdap.searchVector]("+basedn+","+scope+","+filter+");") ;
			LDAPSearchResults res = ld.search(basedn, scope, filter, attrs, false, cons);
			//search jaehong
			
			if(sortAttr != null)	res.sort( new LDAPCompareAttrNames(sortAttr, true) );

			while ( res.hasMoreElements() )
			{
				try
				{
					LDAPEntry findEntry = res.next();
					
					if(basedn.endsWith("ou=user,o=sso30root"))
					{
						EAMUser prop = new EAMUser(findEntry.getAttributeSet()) ;
						System.out.println(">>>get DN>>>>>>>>>>"+findEntry.getDN());
						
						//System.out.println(">>>>>>>>>>>>>"+prop.toString());
						prop.setProperty("dn", findEntry.getDN());
						vec.add(prop) ;
					}
					else if(basedn.endsWith("ou=group,o=sso30root"))
					{
						EAMGroup prop = new EAMGroup(findEntry.getAttributeSet()) ;
						prop.setProperty("dn", findEntry.getDN()) ;
						vec.add(prop) ;
					}
					else if(basedn.endsWith("ou=org,o=sso30root"))
					{
						EAMOrg prop = new EAMOrg(findEntry.getAttributeSet()) ;
						prop.setProperty("dn", findEntry.getDN()) ;
						vec.add(prop) ;
					}
					else if(basedn.endsWith("ou=service,o=sso30root"))
					{
						EAMService prop = new EAMService(findEntry.getAttributeSet()) ;
						prop.setProperty("dn", findEntry.getDN()) ;
						vec.add(prop) ;
					}
					else
					{
						EAMProperties prop = new EAMProperties(findEntry.getAttributeSet()) ;
						prop.setProperty("dn", findEntry.getDN()) ;
						vec.add(prop) ;
					}
				} catch ( LDAPException e ) { continue;	}
			}


		}
		catch(LDAPException e)
		{
			err.set(e) ;
		}
		finally
		{
			if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO	,"[EAMLdap.searchVector]" + err) ;
		}
		return vec;
	}

	/**
	* Dn�� �����ϱ�
	* @param	dn	�����ϰ��� �ϴ� Dn
	* @return	�����ڵ�
	*/
	public int deleteDn(String dn)
	{
		err.reset() ;

		if(ldPool == null)			return 9000 ;
		if(dn == null)				return 9001 ;
		if(dn.length() == 0)		return 9011 ;

		int	return_code = 0 ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.deleteDn]"+dn) ;
		LDAPConnection ld = null ;

		try
		{
			ld = ldPool.getConnection();
			if(dn != null && dn.length() != 0)	ld.delete( dn );

		}
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.deleteDn]("+dn+"):"+err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.deleteDn]("+dn+"):"+err) ;
    	}
    	
    	return return_code;
	}

	/**
	* Entry�� �߰��ϱ�
	* @param	dn		Entry�� �߰��� Dn
	* @param	attrset	�߰��� LDAPAttributeSet
	*/
	private int addDn(String dn, LDAPAttributeSet attrset)
	{
		err.reset() ;
		if(ldPool == null)	return 9000 ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.addDn]("+dn+")"+attrset) ;

		LDAPConnection ld = null ;
		LDAPEntry entry = null ;

		try
		{
			ld = ldPool.getConnection();
			entry = new LDAPEntry(dn, attrset) ;
			ld.add( entry );
		}
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.addDn]("+dn+",...):"+err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.addDn]("+dn+","+attrset+"):"+err) ;
		}

    	return err.getError() ;
	}

	/**
	* Entry�� ��Attribute�� �����ϱ�
	* @param	dn		������ Dn
	*/
	public int modifyDn(String dn, LDAPModificationSet mods)
	{
		err.reset() ;
		if(ldPool == null)		return err.set(-9000) ;

		log.write(EAMLog.LOG_INFO,"[EAMLdap.modifyDn]("+dn+")"+mods) ;
		if(mods.size() <= 0)	return err.set(-9500) ;
		LDAPConnection ld = null;

		try
		{
			ld = ldPool.getConnection();
			ld.modify(dn,mods);
		}
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.modifyDn]("+dn+",...):" + err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.modifyDn]("+dn+","+mods+"):"+err) ;
		}
		
    	return err.getError() ;
	}

	/**
	* Entry�� ��Attribute�� �����ϱ�
	* @param	dn		������ Dn
	* @param	op		������ ���'+':Value�� �߰�'-':Value�� ����'*':Value�� ��ü�ϱ�
	* @param	name	������ Attribute Name
	* @param	value	������ Value��
	*/
	private int modifyDn(String dn, char op, String name, String[] value)
	{
		err.reset() ;
		if(ldPool == null)		return err.set(-9000) ;
		LDAPConnection ld = null;
		LDAPModificationSet	mods	=	null ;
		LDAPAttribute	attribute	=	null ;
		
		try
		{
			ld = ldPool.getConnection();

			mods	=	new LDAPModificationSet();
			attribute	=	new LDAPAttribute(name,value) ;

			if(op == '*')	mods.add(LDAPModification.REPLACE,	attribute);
			if(op == '+')	mods.add(LDAPModification.ADD, 		attribute);
			if(op == '-')	mods.add(LDAPModification.DELETE,	attribute);

			ld.modify(dn,mods);
		}
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.modifyDn]("+dn+","+op+","+name+",...):"+err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.modifyDn]("+dn+","+mods+"):"+err) ;
		}

   		return err.getError() ;
	}

	/**
	* Entry�� ��Attribute�� �����ϱ�
	* @param	dn		������ Dn
	* @param	op		������ ���'+':Value�� �߰�'-':Value�� ����'*':Value�� ��ü�ϱ�
	* @param	name	������ Attribute Name
	* @param	value	������ Value��
	*/
	private int modifyDn(String dn, char op, String name, byte[][] value)
	{
		err.reset() ;
		if(ldPool == null)		return err.set(-9000) ;
		
		LDAPConnection ld = null;
		LDAPModificationSet	mods	=	null ;
		LDAPAttribute	attribute	=	null ;

		try
		{
			ld = ldPool.getConnection();

			mods	=	new LDAPModificationSet();
			attribute	=	new LDAPAttribute(name) ;

			for(int i = 0; i < value.length; i++)
				attribute.addValue(value[i]) ;

			if(op == '*')	mods.add(LDAPModification.REPLACE, attribute);
			if(op == '+')	mods.add(LDAPModification.ADD, attribute);
			if(op == '-')	mods.add(LDAPModification.DELETE, attribute);

			ld.modify(dn,mods);

		}
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.modifyDn]("+dn+","+op+","+name+",...):" + err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.modifyDn]("+dn+","+mods+"):"+err) ;
    	}

    	return err.getError() ;
	}

	/**
	* Entry�� ��Attribute�� �����ϱ�
	* @param	dn		������ Dn
	* @param	op		������ ���'+':Value�� �߰�'-':Value�� ����'*':Value�� ��ü�ϱ�
	* @param	name	������ Attribute Name
	* @param	value	������ Value
	*/
	public int modifyDn(String dn, char op, String name, String value)
	{
		err.reset() ;
		if(ldPool == null)		return err.set(-9000) ;

		LDAPConnection ld = null;
		LDAPModificationSet	mods	=	null ;
		LDAPAttribute	attribute	=	null ;
		
		try
		{
			boolean modify = true ;
			
			if("ssomember".equals(name))
			{
				boolean exist = this.existDn(dn, 0, "(ssomember="+value+")") ;
				if(op == '+' && exist == true)	modify = false ;
				if(op == '-' && exist == false) modify = false ;
			}
			else
			{
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getEntry]("+dn+","+op+","+name+","+value+"):" + err) ;
				EAMProperties prop = this.getEntry(dn,name) ;	// "ssomember"�� �̳��� ũ�� �ȵ�.
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getProperty]") ;
				String[] values = prop.getProperty(name, new String[0]) ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.getProperty]values.length:" + values.length) ;
			
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.contains]("+dn+","+op+","+name+","+value+"):" + err) ;
				
				if(op == '*')	if(values.length == 1)	if(value.equals(values[0]))	modify = false ;
				if(op == '+')	if(EAMUtil.contains(values,value) == true)	modify = false ;
				if(op == '-')	if(EAMUtil.contains(values,value) == false)	modify = false ;
				log.write(EAMLog.LOG_TRACE,"[EAMLdap.contains]("+dn+","+op+","+name+","+value+"):" + err) ;
			}
			
			if(modify)
			{
				ld = ldPool.getConnection();
				mods	=	new LDAPModificationSet();
				attribute	=	new LDAPAttribute(name,value) ;

				if(op == '*')	mods.add(LDAPModification.REPLACE	, attribute);
				if(op == '+')	mods.add(LDAPModification.ADD		, attribute);
				if(op == '-')	mods.add(LDAPModification.DELETE	, attribute);

				ld.modify(dn,mods);
			}
		}	
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.modifyDn]("+dn+","+op+","+name+","+value+"):" + err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.modifyDn]("+dn+","+mods+"):"+err) ;
    	}

    	return err.getError() ;
	}
	
	/**
	* Entry�� ��Attribute�� �����ϱ�
	* @param	dn		������ Dn
	* @param	op		������ ���'+':Value�� �߰�'-':Value�� ����'*':Value�� ��ü�ϱ�
	* @param	name	������ Attribute Name
	* @param	value	������ Value��
	*/
	public int modifyDn(String dn, char op, String name, byte[] value)
	{
		err.reset() ;
		if(ldPool == null)		return err.set(-9000) ;
		
		LDAPConnection ld = null;
		LDAPModificationSet	mods	=	null ;
		LDAPAttribute	attribute	=	null ;

		try
		{
			ld = ldPool.getConnection();

			mods	=	new LDAPModificationSet();
			attribute	=	new LDAPAttribute(name) ;
			attribute.addValue(value) ;

			if(op == '*')	mods.add(LDAPModification.REPLACE, attribute);
			if(op == '+')	mods.add(LDAPModification.ADD, attribute);
			if(op == '-')	mods.add(LDAPModification.DELETE, attribute);

			ld.modify(dn,mods);

		}
		catch( LDAPException e )
		{
			err.set(e) ;
		}
    	finally
    	{
    		if(ld != null)	ldPool.close(ld) ;
			log.write(EAMLog.LOG_INFO,"[EAMLdap.modifyDn]("+dn+","+op+","+name+",...):" + err) ;
			log.write(EAMLog.LOG_LDAP,"[EAMLdap.modifyDn]("+dn+","+mods+"):"+err) ;
    	}

    	return err.getError() ;
	}

	/**
		����Dn���� ������ΰ��� �Ʒ��� ���� ���<br>
		dn:ssousid=006140,ssousid=006100,ssousid=005100,ssousid=001005,ou=org,o=sso30root<br>
		ret[0]:001005<br>
		ret[1]:005100<br>
		ret[2]:006100<br>
		ret[3]:006140<br>

		@param	dn	����Dn
		@return		��������� ����ID��
	*/
	private String[] getPath(String dn)
	{
		if(dn == null)			return new String[0] ;
		if(dn.length() == 0)	return new String[0] ;

		String[] temp = LDAPDN.explodeDN(dn, true) ;
		String[] ret ;
		if( temp.length >= 2 )
		{
			ret = new String[temp.length - 2] ;
			for(int i = temp.length - 3, j = 0; i >= 0; i--, j++)
				ret[j] = temp[i].trim().toUpperCase() ;
		}
		else
			ret = new String[0] ;

		return ret ;
	}

	private String getParentID(String[] path)
	{
		if(path == null)		return null ;
		if(path.length == 0)	return null ;
		if(path.length == 1)	return "" ;
		return path[path.length - 2] ;
	}
	
	/**
		value�� ���ڵ��� check���ڵ� �߿� �ִ� ����
	*/
	private int containsCount(String check, String value)
	{
		int ret = 0 ;
		for(int i = 0; i < value.length(); i++)
			if(this.contains(check, value.charAt(i))	== true)
				ret++ ;
		return ret ;
	}

	/**
		value�� ���ڰ� check���ڵ��߿� �ϳ��̸� true
	*/
	private boolean contains(String check, char value)
	{
		for(int i = 0; i < check.length(); i++)
			if(check.charAt(i) == value)
				return true ;
		return false ;
	}

	/**
		value�� ��繮�ڵ��� check���ڵ��߿� �ϳ��̸� true
	*/
	private boolean contains(String check, String value)
	{
		for(int i = 0; i < value.length(); i++)
			if(this.contains(check, value.charAt(i))	== false)
				return false ;
		return true ;
	}

	/**
		[Test��]
	*/
    public static void main (String args []) throws Exception
    {
    	EAMLdap ldap = new EAMLdap() ;

		//LDAPProperties user = ldap.getLDAPEntry("uid=20150461,ou=user,o=sso30root") ;
		
		//System.out.println("get ENdtr>>>>>>>>>>"+ldap.getLDAPEntry("ou=user,o=sso30root"));
		//System.out.println(user.size());
		//ldap.addMember("4050_002","AAAA") ;
		/*ldap.addMember("4050_002","AA") ;
		ldap.delMember("4050_002","AAAA") ;
		
		ldap.addMember("4030_D01","AAAA") ;
		ldap.addMember("4030_D01","AA") ;
		ldap.delMember("4030_D01","AAAA") ;*/
    	String[] attr = { "uid", "cn", "ssouspathid" ,"mail", "ssorrn" } ;
    	
		Vector temp = ldap.searchVector("ou=user,o=sso30root", 2, "(objectclass=*)", attr, null, 5) ;
		
		/*System.out.println("temp.size():" + temp.size()) ;
		 */
		
		System.out.println(temp.toString());
		//temp = ldap.searchVector("ssogid=4030_D01,ou=group,o=sso30root", 0, "(ssomember=AA)", null, null, 0) ;
		//System.out.println("temp.size():" + temp.size()) ;
		
		//System.out.println("AAAA" + ldap.existDn("ssogid=4030_D01,ou=group,o=sso30root", 0, "(ssomember=AAAA)")) ;
		//System.out.println("AA" + ldap.existDn("ssogid=4030_D01,ou=group,o=sso30root", 0, "(ssomember=AA)")) ;
		
		
		//user.list(System.out) ;
		//user.listLDIF(System.out) ;
		
		//String[] names = { "uid", "cn", "ssoprofile" } ;
		//EAMUser user = ldap.findUser("pailcock", names) ;
		//System.out.println("uid:"+user.getUserID()) ;
		//System.out.println("cn:" + user.getUserName()) ;
		//System.out.println("ssoprofile:" + user.getProperty("ssoprofile")) ;
		
		//EAMUser temp = (EAMUser)user.clone() ;
		//user.list(System.out) ;
		//temp.setProperty("������ȭ","12") ;
		//temp.setProperty("All","1234") ;
		//System.out.println("---------------------") ;
		//EAMProperties prop = user.getMODProperties((EAMProperties)temp) ;
		//prop.list(System.out) ;

    	//String[] names = { "uid", "cn", "ssoprofile" } ;
    	//EAMUser[] users = ldap.findUserName("��*", names, "cn") ;

    	//if(users == null)		System.out.println("users == null") ;
		//else
    	//{
    	//	System.out.println("uid Sort:" + users.length) ;
    	//	for(int i=0; i < users.length; i++)
    	//		System.out.println(""+i+"," + users[i].getUserID() + "," + users[i].getUserName() + "") ;
    	//}

   		//users = ldap.findUserName("��*", names) ;
   		//if(users == null)		System.out.println("users == null") ;
		//else
   		//{
   		//	System.out.println("users.length:" + users.length) ;
   		//	for(int i=0; i < users.length; i++)
   		//		System.out.println("["+i+"]:" + users[i].getUserID() + "(" + users[i].getUserName() + ")") ;
   		//}

    	//EAMUser[] users = ldap.findUserName("�强��") ;
    	//if(users == null)	System.out.println("users == null") ;
    	//else
    	//{
    	//	System.out.println("users.length:" + users.length) ;
    	//	for(int i=0; i < users.length; i++)
    	//		System.out.println("["+i+"]:" + users[i].getUserID() + "(" + users[i].getUserName() + ")") ;
    	//}

		//EAMOrg org = ldap.findOrg("0000001971") ;
		//if(org != null)
		//{
		//	System.out.println("org.getOrgID()   :" + org.getOrgID()	) ;
		//	System.out.println("org.getOrgName() :" + org.getOrgName()	) ;

		//	String[] idpath		= org.getOrgIDPath() ;
		//	System.out.println("idpath.length:" + idpath.length) ;
		//	for(int i = 0; i < idpath.length; i++)
		//		System.out.println("["+i+"]:" + idpath[i]) ;
		//	String[] namepath	= org.getOrgNamePath() ;
		//	System.out.println("namepath.length:" + namepath.length) ;
		//	for(int i = 0; i < namepath.length; i++)
		//		System.out.println("["+i+"]:" + namepath[i]) ;
		//}

    	//String[] temp = { "uid","ssopwd", "ssopwdhistory", "ssopwdchangetime" } ;
		//EAMProperties[] props = ldap.getEntrys("ou=user,o=sso30root", 1, "(uid=zhang)", temp) ;
		//for(int i = 0; i < props.length;i++)
		//	props[i].list(System.out) ;
    }
}

