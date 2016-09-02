package jaehongLdap;

import	java.io.*;
import	eamldap.objectclass.*;
import	java.util.Locale;
import	java.util.StringTokenizer;

/**
	Configure�� ���� Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMConf.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMConf
{
	private EAMProperties 	confProp = null ;
    public EAMConf()		{	this.confProp = new EAMProperties("eamldap/props/conf.props") ;	}
    public EAMConf(File f)	{	this.confProp = new EAMProperties(f) ;							}
    
	/** Configure Properties�� ���	*/	public EAMProperties getEAMProperties()	{	return this.confProp ;	}
	
	/**	LDAP IP�� ��´�.			*/	public String	getLdapHost1()			{	return confProp.getProperty("ldaphost1","211.218.143.141") ;					}
	/**	LDAP Port�� ��´�.			*/	public int		getLdapPort1()			{	return EAMUtil.toInteger(confProp.getProperty("ldapport1","389")) ;		}	
	/**	LDAP IP�� ��´�.			*/	public String	getLdapHost2()			{	return confProp.getProperty("ldaphost2","211.218.143.141") ;					}
	/**	LDAP Port�� ��´�.			*/	public int		getLdapPort2()			{	return EAMUtil.toInteger(confProp.getProperty("ldapport2","389")) ;		}	
	/**	Binding Dn�� ��´�.		*/	public String	getBindDn()				{	return confProp.getProperty("binddn","") ;								}
	/** Binding PassWord�� ��´�.	*/	public String	getBindPassWord()		{	return confProp.getProperty("bindpassword","") ;						}
	/**	InitPoolSize ��´�.		*/	public int		getInitPoolSize()		{	return EAMUtil.toInteger(confProp.getProperty("initpoolsize","2")) ;	}
	/**	MaxPoolSize�� ��´�.		*/	public int		getMaxPoolSize()		{	return EAMUtil.toInteger(confProp.getProperty("maxpoolsize","4")) ;		}
	
	/**	Loging Directory�� ��´�.	*/	public String	getLogDir()				{	return confProp.getProperty("logdir","") ;								}
	/**	Loging Directory�� ��´�.	*/	public String	getSubLogDir()			{	return confProp.getProperty("sublogdir","") ;							}
	/**	Log Level�� ��´�.			*/	public int		getLogLevel()			{	return EAMUtil.toInteger(confProp.getProperty("loglevel","")) ;			}
	
	/**	UserID Size Check���� ���	*/	public boolean	checkUserIDSize()		{	return "check".equals(confProp.getProperty("useridsize"		,"")) ;		}
	/**	UserID Type Check���� ���	*/	public boolean	checkUserIDType()		{	return "check".equals(confProp.getProperty("useridtype"		,"")) ;		}
	/**	PasswordSizeCheck���� ���	*/	public boolean	checkPasswordSize()		{	return "check".equals(confProp.getProperty("passwordsize"	,"")) ;		}
	/**	PasswordTypeCheck���� ���	*/	public boolean	checkPasswordType()		{	return "check".equals(confProp.getProperty("passwordtype"	,"")) ;		}
	/**	PW  History Check���� ���	*/	public boolean	checkPasswordHistory()	{	return "check".equals(confProp.getProperty("passwordhistory","")) ;		}
	
	/**	AccountProfile��뿩�� ���	*/	public boolean	useAccountProfile()		{	return "true".equals(confProp.getProperty("useaccoutprofile","")) ;		}
	/**	PS������ȭ�ϰ�� ���		*/	public String	getDefaultServiceDn()	{	return confProp.getProperty("servicedn","") ;							}
	/**	PS������ȭ�ϰ�� ���		*/	public String	getPSCertFilePath()		{	return confProp.getProperty("pscert","") ;								}
	/**	PS����Űȭ�ϰ�� ���		*/	public String	getPSPrivateKeyPath()	{	return confProp.getProperty("psprivate","") ;							}
	/**	PS����Ű�� ��й�ȣ ���	*/	public String	getPSPassword()			{	return confProp.getProperty("pspassword","") ;							}
	/**	CA������ȭ�ϰ�� ���		*/	public String	getCACertFilePath()		{	return confProp.getProperty("cacert","") ;								}
	/** Engine IP���				*/	public String	getEngineIP()			{	return confProp.getProperty("engineip","127.0.0.1"); 					}
	/** Engine Port���				*/	public int		getEnginePort()			{	return EAMUtil.toInteger(confProp.getProperty("engineport","7000")); 	}
	
	/**	CA������ȭ�ϰ�� ���		*/	public String	getDefaultGroupOrgID()	{	return confProp.getProperty("defaultgrouporgid","") ;					}
	/**	�������λ�뿩�� ���		*/	public boolean	useOrgNamePath()		{	return "true".equals(confProp.getProperty("useorgnamepath","")) ;		}
	/**	���񽺸��λ�뿩�� ���	*/	public boolean	useServiceNamePath()	{	return "true".equals(confProp.getProperty("useservicenamepath","")) ;	}
	
	/**	DB Driver ���				*/	public String	getDBDriver()			{	return confProp.getProperty("dbdriver","") ;							}
	/**	DB Connect ���				*/	public String	getDBConnect()			{	return confProp.getProperty("dbconnect","") ;							}
	/**	DB User ���				*/	public String	getDBUser()				{	return confProp.getProperty("dbuser","") ;								}
	/**	DB Password ���			*/	public String	getDBPassword()			{	return confProp.getProperty("dbpassword","") ;							}
	
	/**	Default Profile ���		*/	public String	getDefaultUserProfile()	{	return confProp.getProperty("defaultuserprofile","") ;					}
	/** PermissionMethod���		*/	public int		getPermissionMethod()	{	return EAMUtil.toInteger(confProp.getProperty("permissionmethod","0")); }
	

	
    public static void main (String args []) throws Exception 
    {
    	System.out.println("java eamldap.EAMConf [Configure File]") ;
    	
    	EAMConf	conf = null ;
    	
    	switch (args.length)
    	{
    		case	1	:
				conf = new EAMConf(new File(args[0])) ;
    			break ;
    					
    		default		:
    			conf = new EAMConf() ;
    			break ;		    		
    	}
    	
    	if(conf != null)
    	{
			EAMProperties prop = conf.getEAMProperties() ;
			prop.list(System.out) ;
		}	
    }
}
