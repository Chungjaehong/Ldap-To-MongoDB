package eamldap.util;

import	eamldap.*;
import	eamldap.objectclass.*;
import	eamldap.attribute.*;
import	java.security.cert.* ;
import	java.util.*;
import	java.lang.*;
import	java.io.*;
import	java.text.*;

/**
<br>
	Copyright (C) 2000-2004 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	sso35ldap API Module<br>
	FileName	:	CertTool.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	History		:	20030210 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class CertTool extends EAMTool
{
	
	
	public static void main (String args []) throws IOException
	{
		if(EAMTool.ldap == null)	EAMTool.ldap = new EAMLdap() ;
		CertTool.interAction();
	}
	
	public static void interAction() throws IOException
	{
		System.out.println("") ;
		System.out.println("[1]. LDAP의 PS인증서 및 ssoAccountProfile 교체하기") ;
		System.out.println("[2]. LDAP의 PS인증서교체하기") ;
		System.out.println("[3]. ssoAccountProfile얻기") ;
		System.out.println("[4]. ssoAccountProfile넣기") ;
		System.out.println("[1,2,3,4]중에서 선택하여 주시기 바랍니다.[0]은 Quit입니다.") ;
		
		String key = EAMTool.getKeyIn() ;
		if(key == null)			return ;
		if(key.length() == 0)	return ;

		switch(key.charAt(0))
		{
			case '1' :	CertTool.LDAPAccountProfileChange();	break ;
			case '2' :	CertTool.LDAPPSCertChange();			break ;
			case '3' :	CertTool.ExportAccountProfile();		break ;
			case '4' :	CertTool.ImportAccountProfile();		break ;
			default  :	return ;
		}
	}
	
	public static void ExportAccountProfile() throws IOException
	{
		System.out.println("") ;
		System.out.println("Expert File 위치?:") ;

		String key = EAMTool.getKeyIn() ;
		if(key == null)			return ;
		if(key.length() == 0)	return ;

		System.out.println("key:"+key) ;
		File accfile = new File(key) ;
		if(accfile == null)
		{
			System.out.println("쓸 수가 없습니다!!") ;
			return ;
		}
				
		PrintWriter prn = new PrintWriter(new FileOutputStream(accfile)) ;
		EAMConf conf	= new EAMConf() ;
		{
			File certfile = new File(conf.getPSCertFilePath()) ;
			X509Certificate cert = null ;
			try 
			{
  				InputStream inStream = new FileInputStream(certfile);
 				CertificateFactory cf = CertificateFactory.getInstance("X.509");
 				cert = (X509Certificate)cf.generateCertificate(inStream);
 				inStream.close();
 			}
 			catch(FileNotFoundException e)
			{
				System.out.println(certfile + "File Not Found!!!") ;
				return ;
			}
 			catch(IOException e)
			{
				System.out.println(certfile + "File Not Read!!!") ;
				return ;
			}
 			catch(CertificateException e)
			{
				System.out.println(certfile + "File Not Read!!!") ;
				return ;
			}
			
			if(cert == null)	return ;
			
			System.out.println("IssuerDN :"+cert.getIssuerDN());
			System.out.println("SubjectDN:"+cert.getSubjectDN());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ; 
			System.out.println("NotBefore:"+df.format(cert.getNotBefore()));
			System.out.println("NotAfter :"+df.format(cert.getNotAfter()));
			System.out.println("PS Certificate[Y/N] ? ");
			
			key = EAMTool.getKeyIn() ;
			if(key == null)			return ;
			if(key.length() == 0)	return ;
			
			if(!(key.charAt(0) == 'y' || key.charAt(0) == 'Y'))
				return ;
		}
		
		EAMProperties[] orgs = ldap.getEntrys("ou=org,o=sso30root",2,"(ssousid=*)","ssousid") ;
		for(int z = 0; z < orgs.length;z++)
		{
			String orgID = orgs[z].getProperty("ssousid","") ;
			
			if(orgID.length() <= 0)	continue; 	// 무시
			
			EAMProperties[] users = ldap.getEntrys("ou=user,o=sso30root",1,"(&(ssousorgid="+orgID+")(ssoaccountprofile=*AccountProfile>))", "ssoaccountprofile") ;
			//EAMProperties[] users = ldap.getEntrys("ou=user,o=sso30root",1,"(ssoaccountprofile=*AccountProfile>)", "ssoaccountprofile") ;
			for(int i = 0; i < users.length; i++)
			{
				EAMNVDS account = new EAMNVDS() ;
				EAMAccountProfile accProfile = new EAMAccountProfile(conf,users[i].getProperty("ssoaccountprofile")) ;
			
				account.add("USERDN",users[i].getProperty("dn")) ;
				EAMAccount[] acc =  accProfile.get() ;
				for(int j = 0; j < acc.length; j++)
				{
					if(acc[j].getPassword() != null)
					{
						account.add("DN",acc[j].getServiceDn()	) ;
						account.add("ID",acc[j].getUserID()		) ;
						account.add("PW",acc[j].getPassword()	) ;
					}
				}
				prn.println(account.getQuery()) ;
				//LineDisplay("["+(i+1)+"/"+users.length+"]:"+users[i].getProperty("dn")) ;
				LineDisplay("["+(z+1)+"/"+orgs.length+"]("+orgID+")["+(i+1)+"/"+users.length+"]:"+users[i].getProperty("dn")) ;
			}
		}
		prn.close() ;
		System.out.println() ;
	}
	
	public static void ImportAccountProfile() throws IOException
	{
		System.out.println("") ;
		System.out.println("Import File 위치?:") ;

		String key = EAMTool.getKeyIn() ;
		if(key == null)			return ;
		if(key.length() == 0)	return ;

		File accfile = new File(key) ;
		if(accfile == null)
		{
			System.out.println("읽을 수가 없습니다!!!") ;
			return ;
		}
		if(accfile.canRead() == false)
		{
			System.out.println("읽을 수가 없습니다!!!") ;
			return ;
		}
		
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(new FileInputStream(accfile)));
		EAMConf conf	= new EAMConf() ;
		{
			File certfile = new File(conf.getPSCertFilePath()) ;
			X509Certificate cert = null ;
			try 
			{
  				InputStream inStream = new FileInputStream(certfile);
 				CertificateFactory cf = CertificateFactory.getInstance("X.509");
 				cert = (X509Certificate)cf.generateCertificate(inStream);
 				inStream.close();
 			}
 			catch(FileNotFoundException e)
			{
				System.out.println(certfile + "File Not Found!!!") ;
				return ;
			}
 			catch(IOException e)
			{
				System.out.println(certfile + "File Not Read!!!") ;
				return ;
			}
 			catch(CertificateException e)
			{
				System.out.println(certfile + "File Not Read!!!") ;
				return ;
			}
			
			if(cert == null)	return ;
			
			System.out.println("IssuerDN :"+cert.getIssuerDN());
			System.out.println("SubjectDN:"+cert.getSubjectDN());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ; 
			System.out.println("NotBefore:"+df.format(cert.getNotBefore()));
			System.out.println("NotAfter :"+df.format(cert.getNotAfter()));
			System.out.println("PS Certificate[Y/N] ? ");
			
			key = EAMTool.getKeyIn() ;
			if(key == null)			return ;
			if(key.length() == 0)	return ;
			
			if(!(key.charAt(0) == 'y' || key.charAt(0) == 'Y'))
				return ;
		}
		
		int i = 0;
        while(true)
        {
            String line = bufferedreader.readLine();
			//System.out.println("line:" + line) ;
            if(line == null)	break;
            
			EAMNVDS account = new EAMNVDS(line) ;
			String userDN = account.find("USERDN") ;
			String[] dn = account.getValues("DN") ;
			String[] id = account.getValues("ID") ;
			String[] pw = account.getValues("PW") ;
			EAMAccount[] acc = new EAMAccount[dn.length] ;
			
			for(int j = 0; j < dn.length; j++)
				acc[j] = new EAMAccount(conf, dn[j], id[j], pw[j]) ;
			
			EAMAccountProfile accProfile = new EAMAccountProfile(conf, acc) ;
			int ret = ldap.modifyDn(userDN, '*', "ssoaccountprofile", accProfile.getXML()) ;
			LineDisplay("["+(++i)+"]:"+userDN+":"+ret,(ret != 0)) ;
		}
		
		bufferedreader.close();
		System.out.println() ;
	}
	
	public static void LDAPAccountProfileChange() throws IOException
	{
		EAMConf newconf = LDAPPSCertChange() ;
		
		if(newconf == null)
		{
			System.out.println("newconf == null") ;
			return ;
		}
		
		EAMConf conf	= new EAMConf() ;
		EAMProperties[] users = ldap.getEntrys("ou=user,o=sso30root",1,"(ssoaccountprofile=*AccountProfile>)", "ssoaccountprofile") ;
		int ret = 0 ;
		System.out.println("users.length:"+users.length) ;
		for(int i = 0; i < users.length; i++)
		{
			//System.out.println("    dn:"+users[i].getProperty("dn")) ;
			//System.out.println("ssoacc:"+users[i].getProperty("ssoaccountprofile")) ;
			EAMAccountProfile	accProfile = new EAMAccountProfile(conf,users[i].getProperty("ssoaccountprofile")) ;
			accProfile.update(newconf) ;
			ret = ldap.modifyDn(users[i].getProperty("dn"), '*', "ssoaccountprofile", accProfile.getXML()) ;;
			LineDisplay("["+i+"/"+users.length+"]:"+users[i].getProperty("dn")+":"+ret,(ret != 0)) ;
		}
	}
	
	public static EAMConf LDAPPSCertChange() throws IOException
	{
		EAMConf newconf = null ;
		X509Certificate cert = null ;
		//byte[] der = null ;
		//////////////////////////////////////////////////////
		//	새로운 인증서 확인하기
		while(newconf == null)
		{
			System.out.println("") ;
			System.out.println("새로운 PS인증서설정된 configureFile 위치?:") ;
		
			String key = EAMTool.getKeyIn() ;
			if(key == null)			continue ;
			if(key.length() == 0)	continue ;
			
			File conffile = new File(key) ;
			if(conffile == null)	continue ;
			if(conffile.canRead() == false)
			{
				System.out.println("읽을 수가 없습니다!!!") ;
				conffile = null ;
				continue ;
			}
			
			newconf	= new EAMConf(conffile) ;
			File certfile = new File(newconf.getPSCertFilePath()) ;
			
			try 
			{
  				InputStream inStream = new FileInputStream(certfile);
 				CertificateFactory cf = CertificateFactory.getInstance("X.509");
 				cert = (X509Certificate)cf.generateCertificate(inStream);
 				//der = cert.getTBSCertificate() ;
 				inStream.close();
 			}
 			catch(FileNotFoundException e)
			{
				System.out.println(certfile + "File Not Found!!!") ;
				newconf = null ;
				continue ;
			}
 			catch(IOException e)
			{
				System.out.println(certfile + "File Not Read!!!") ;
				newconf = null ;
				continue ;
			}
 			catch(CertificateException e)
			{
				System.out.println(certfile + "File Not Read!!!") ;
				newconf = null ;
				continue ;
			}
			
			if(cert == null)
			{
				newconf = null ;
				continue ;
			}
			
			System.out.println("IssuerDN :"+cert.getIssuerDN());
			System.out.println("SubjectDN:"+cert.getSubjectDN());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ; 
			System.out.println("NotBefore:"+df.format(cert.getNotBefore()));
			System.out.println("NotAfter :"+df.format(cert.getNotAfter()));
			System.out.println("New PS Certificate[Y/N] ? ");
			
			key = EAMTool.getKeyIn() ;
			if(key == null)			continue ;
			if(key.length() == 0)	continue ;
			
			if(key.charAt(0) == 'y' || key.charAt(0) == 'Y')
				break ;
			newconf = null ;
		}
		
		FileInputStream fis = new FileInputStream(newconf.getPSCertFilePath()) ;
    	
    	byte[] buff = new byte[4096] ;
    	int len = fis.read(buff) ;
		
		byte[] der = new byte[len] ;
		for(int i = 0; i < der.length; i++)
			der[i] = buff[i] ;
			
		fis.close() ;
		System.out.println("der.length:"+der.length) ;
		int ret = ldap.modifyDn("cn=PS Cert,ou=config,o=sso30root",'*',"userCertificate",der) ;
		if(ret != 0)
		{
			System.out.println("cn=PS Cert,ou=config,o=sso30root의 userCertificate를 확인하세요.") ;
			System.out.println("ret:" + ret) ;
			return null ;
		}
		
		return newconf ;
	}
}


