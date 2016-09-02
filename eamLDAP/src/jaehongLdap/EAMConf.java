package jaehongLdap;

import	java.io.*;
import	eamldap.objectclass.*;
import	java.util.Locale;
import	java.util.StringTokenizer;

/**
	Configure에 관한 Class.<br>
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
    
	/** Configure Properties를 얻기	*/	public EAMProperties getEAMProperties()	{	return this.confProp ;	}
	
	/**	LDAP IP를 얻는다.			*/	public String	getLdapHost1()			{	return confProp.getProperty("ldaphost1","211.218.143.141") ;					}
	/**	LDAP Port를 얻는다.			*/	public int		getLdapPort1()			{	return EAMUtil.toInteger(confProp.getProperty("ldapport1","389")) ;		}	
	/**	LDAP IP를 얻는다.			*/	public String	getLdapHost2()			{	return confProp.getProperty("ldaphost2","211.218.143.141") ;					}
	/**	LDAP Port를 얻는다.			*/	public int		getLdapPort2()			{	return EAMUtil.toInteger(confProp.getProperty("ldapport2","389")) ;		}	
	/**	Binding Dn를 얻는다.		*/	public String	getBindDn()				{	return confProp.getProperty("binddn","") ;								}
	/** Binding PassWord를 얻는다.	*/	public String	getBindPassWord()		{	return confProp.getProperty("bindpassword","") ;						}
	/**	InitPoolSize 얻는다.		*/	public int		getInitPoolSize()		{	return EAMUtil.toInteger(confProp.getProperty("initpoolsize","2")) ;	}
	/**	MaxPoolSize를 얻는다.		*/	public int		getMaxPoolSize()		{	return EAMUtil.toInteger(confProp.getProperty("maxpoolsize","4")) ;		}
	
	/**	Loging Directory를 얻는다.	*/	public String	getLogDir()				{	return confProp.getProperty("logdir","") ;								}
	/**	Loging Directory를 얻는다.	*/	public String	getSubLogDir()			{	return confProp.getProperty("sublogdir","") ;							}
	/**	Log Level를 얻는다.			*/	public int		getLogLevel()			{	return EAMUtil.toInteger(confProp.getProperty("loglevel","")) ;			}
	
	/**	UserID Size Check여부 얻기	*/	public boolean	checkUserIDSize()		{	return "check".equals(confProp.getProperty("useridsize"		,"")) ;		}
	/**	UserID Type Check여부 얻기	*/	public boolean	checkUserIDType()		{	return "check".equals(confProp.getProperty("useridtype"		,"")) ;		}
	/**	PasswordSizeCheck여부 얻기	*/	public boolean	checkPasswordSize()		{	return "check".equals(confProp.getProperty("passwordsize"	,"")) ;		}
	/**	PasswordTypeCheck여부 얻기	*/	public boolean	checkPasswordType()		{	return "check".equals(confProp.getProperty("passwordtype"	,"")) ;		}
	/**	PW  History Check여부 얻기	*/	public boolean	checkPasswordHistory()	{	return "check".equals(confProp.getProperty("passwordhistory","")) ;		}
	
	/**	AccountProfile사용여부 얻기	*/	public boolean	useAccountProfile()		{	return "true".equals(confProp.getProperty("useaccoutprofile","")) ;		}
	/**	PS인증서화일경로 얻기		*/	public String	getDefaultServiceDn()	{	return confProp.getProperty("servicedn","") ;							}
	/**	PS인증서화일경로 얻기		*/	public String	getPSCertFilePath()		{	return confProp.getProperty("pscert","") ;								}
	/**	PS개인키화일경로 얻기		*/	public String	getPSPrivateKeyPath()	{	return confProp.getProperty("psprivate","") ;							}
	/**	PS개인키의 비밀번호 얻기	*/	public String	getPSPassword()			{	return confProp.getProperty("pspassword","") ;							}
	/**	CA인증서화일경로 얻기		*/	public String	getCACertFilePath()		{	return confProp.getProperty("cacert","") ;								}
	/** Engine IP얻기				*/	public String	getEngineIP()			{	return confProp.getProperty("engineip","127.0.0.1"); 					}
	/** Engine Port얻기				*/	public int		getEnginePort()			{	return EAMUtil.toInteger(confProp.getProperty("engineport","7000")); 	}
	
	/**	CA인증서화일경로 얻기		*/	public String	getDefaultGroupOrgID()	{	return confProp.getProperty("defaultgrouporgid","") ;					}
	/**	조직명경로사용여부 얻기		*/	public boolean	useOrgNamePath()		{	return "true".equals(confProp.getProperty("useorgnamepath","")) ;		}
	/**	서비스명경로사용여부 얻기	*/	public boolean	useServiceNamePath()	{	return "true".equals(confProp.getProperty("useservicenamepath","")) ;	}
	
	/**	DB Driver 얻기				*/	public String	getDBDriver()			{	return confProp.getProperty("dbdriver","") ;							}
	/**	DB Connect 얻기				*/	public String	getDBConnect()			{	return confProp.getProperty("dbconnect","") ;							}
	/**	DB User 얻기				*/	public String	getDBUser()				{	return confProp.getProperty("dbuser","") ;								}
	/**	DB Password 얻기			*/	public String	getDBPassword()			{	return confProp.getProperty("dbpassword","") ;							}
	
	/**	Default Profile 얻기		*/	public String	getDefaultUserProfile()	{	return confProp.getProperty("defaultuserprofile","") ;					}
	/** PermissionMethod얻기		*/	public int		getPermissionMethod()	{	return EAMUtil.toInteger(confProp.getProperty("permissionmethod","0")); }
	

	
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
