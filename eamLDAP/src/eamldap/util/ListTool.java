package eamldap.util;

import	eamldap.*;
import	eamldap.objectclass.*;
import	eamldap.attribute.*;
import	java.util.*;
import	java.lang.*;
import	java.io.*;

/**
<br>
	Copyright (C) 2000-2004 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	sso35ldap API Module<br>
	FileName	:	ListTool.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	History		:	20030210 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class ListTool extends EAMTool
{
	public static void main (String args []) throws IOException
	{
		if(EAMTool.ldap == null)	EAMTool.ldap = new EAMLdap() ;
		
		ListTool.interAction();
	}
	
	public static void interAction() throws IOException
	{
		////////////////////////////////////
		// Language Check
		String language = System.getProperty("user.language");
		if(!(language.indexOf("ko") >= 0 || language.indexOf("kr") >= 0 || language.indexOf("KO") >= 0 || language.indexOf("KR") >= 0))
		{
			System.out.println("System.getProperty(\"user.language\"):" + language) ;
			//return;
		}
			
		System.out.println("") ;
		System.out.println("[1]. 사용권한(UACL) 출력하기") ;
		System.out.println("[2]. 조직Code가 없는 사용자(ssousorgid=) 출력하기") ;
		System.out.println("[1,2]중에서 선택하여 주시기 바랍니다.[0]은 Quit입니다.") ;
		
		String key = EAMTool.getKeyIn() ;
		if(key == null)			return ;
		if(key.length() == 0)	return ;

		switch(key.charAt(0))
		{
			case '1' :	ListTool.UACLinterAction();			break ;
			case '2' :	ListTool.ListUser("(ssousorgid=)");	break ;
			default  :	return ;
		}
	}
	
	public static void UACLinterAction() throws IOException
	{
		System.out.println("") ;
		System.out.println("[1]. 사용자기준 사용권한(UACL) 출력하기") ;
		System.out.println("[2]. 서비스기준 사용권한(UACL) 출력하기") ;
		System.out.println("[1,2]중에서 선택하여 주시기 바랍니다.[0]은 Quit입니다.") ;
		
		String key = EAMTool.getKeyIn() ;
		if(key == null)			return ;
		if(key.length() == 0)	return ;

		switch(key.charAt(0))
		{
			case '1' :	UACLSelect("ou=user,o=sso30root");	break ;
			case '2' :	UACLSelect("ou=service,o=sso30root");	break ;
			default  :	return ;
		}
	}
	
	public static String getPath(String dn)
	{
		return ListTool.getPath(dn,"/") ;
	}
	
	public static String getID(String dn)
	{
		EAMNVDS nvds = new EAMNVDS('=',',') ;
		
		nvds.setQuery(dn) ;
		return nvds.find("ssosid", true);
	}
	
	public static String getRootID(String dn)
	{
		EAMNVDS nvds = new EAMNVDS('=',',') ;
		
		nvds.setQuery(dn) ;
		
		String[] temp = nvds.getValues() ;
		return (temp.length >= 3) ? temp[temp.length - 3] : "" ;
	}
	
	public static String getPath(String dn, String sep)
	{
		EAMNVDS nvds = new EAMNVDS('=',',') ;
		
		nvds.setQuery(dn) ;
		
		String[] temp = nvds.getValues() ;
		
		String ret = "" ;
			
		for(int i = temp.length - 3; i >= 0; i--)
			ret = ret + sep + temp[i] ;
			
		return (ret.length() > 0) ?	ret.substring(1) : "" ;
	}
	
	public static void UACLSelect(String baseDn) throws IOException
	{
		////////////////////////////////////
		// Language Check
		boolean english = false ;
		String language = System.getProperty("user.language");
		if(!(language.indexOf("ko") >= 0 || language.indexOf("kr") >= 0 || language.indexOf("KO") >= 0 || language.indexOf("KR") >= 0))
			english = true ;
			
		String name = "" ;
		int select = 0 ;
		int uacltype = 0 ;
		
		while(select == 0)
		{
			name = "" ;
			while(name.length() < 2)
			{
				System.out.println("") ;
				if("ou=user,o=sso30root".equals(baseDn))
				{
					uacltype = 0 ;
					System.out.print("사용자명 ? ") ;
				}
				else if("ou=service,o=sso30root".equals(baseDn))
				{
					uacltype = -1 ;
					System.out.print("서비스명 ? ") ;
				}
		
				name = EAMTool.getKeyIn() ;
				if(name == null)	name = "" ;
			}
		
			String[] attr = { "cn" } ;

			if(english) 	name = EAMUtil.en2ko(name) ;
			
			EAMProperties[] prop = EAMTool.ldap.getEntrys(baseDn,2,"(cn=*"+name+"*)", attr, "cn", 20) ;
		
			System.out.println("기준되는 것을 선택하여 주십시요.[0]은 다시합니다.") ;
			
			for(int i = 0;i < prop.length; i++)
			{
				if(english)
					System.out.println("["+(i+1)+"]:"+EAMUtil.ko2en(prop[i].getProperty("cn"))+"("+ListTool.getPath(prop[i].getProperty("dn"))+")") ;
				else
					System.out.println("["+(i+1)+"]"+prop[i].getProperty("cn")+"("+ListTool.getPath(prop[i].getProperty("dn"))+")") ;
			}

			try
			{
				select = Integer.parseInt(EAMTool.getKeyIn()) ;
			}
			catch(NumberFormatException e)
			{
				select = 0 ;
			}
			
			if(1 <= select && select <= prop.length)
			{
				//System.out.println("select:"+prop[select-1].getProperty("dn")) ;
				String DN = prop[select-1].getProperty("dn") ;
				String ID = DN.substring(DN.indexOf("=")+1, DN.indexOf(",")).trim() ;
				//System.out.println("ID:["+ID+"]") ;

				if(uacltype == 0)	ListUserUACL(ID) ;
				//if(uacltype == 1)	ListOrgUACL(ID) ;
				//if(uacltype == 2)	ListGroupUACL(ID) ;
				if(uacltype == -1)	ListServiceUACL(DN) ;
				return ;
			}
		}
	}
	
	public static void ListUser(String filter)
	{
		////////////////////////////////////
		// Language Check
		boolean english = false ;
		String language = System.getProperty("user.language");
		if(!(language.indexOf("ko") >= 0 || language.indexOf("kr") >= 0 || language.indexOf("KO") >= 0 || language.indexOf("KR") >= 0))
			english = true ;
		
		EAMConf conf	= new EAMConf() ;	
		EAMProperties[] props = new EAMProperties[0] ;
		
		String[] usrattr = { "uid", "cn" } ;
		props = EAMTool.ldap.getEntrys("ou=user,o=sso30root",1,filter,usrattr) ;
		if(props.length == 0)	System.out.println("없습니다.") ;
		
		for(int i = 0; i < props.length; i++)
		{
			if(english)
				System.out.println((i+1)+"/"+props.length+","+props[i].getProperty("uid","") +","+EAMUtil.ko2en(props[i].getProperty("cn",""))) ;
			else
				System.out.println((i+1)+"/"+props.length+","+props[i].getProperty("uid","") +","+props[i].getProperty("cn","")) ;
				
			//EAMUser usr = new EAMUser() ;
			//usr.setProperty("uid",props[i].getProperty("uid","")) ;
			//usr.setProperty("ssousorgid","00000") ;
			//EAMTool.ldap.modUser(usr) ;
		}
	}
	
	public static void ListUserUACL(String UserID)
	{
		////////////////////////////////////
		// Language Check
		boolean english = false ;
		String language = System.getProperty("user.language");
		if(!(language.indexOf("ko") >= 0 || language.indexOf("kr") >= 0 || language.indexOf("KO") >= 0 || language.indexOf("KR") >= 0))
			english = true ;
			
		EAMConf conf	= new EAMConf() ;
		EAMProperties 	prop = new EAMProperties() ;
		EAMProperties[] props = new EAMProperties[0] ;
		
		Properties names = new Properties() ;
		
		String[] usrattr = { "cn", "ssouspathid" } ;
		prop = EAMTool.ldap.getEntry("uid="+UserID+",ou=user,o=sso30root", usrattr) ;
		
		if(english)
			names.setProperty("0"+UserID		, EAMUtil.ko2en(prop.getProperty("cn"))) ;
		else
			names.setProperty("0"+UserID		, prop.getProperty("cn")) ;

		/////////////////////////////////////////////////////
		//	소속 조직 얻기		
		String[] OrgIDs = prop.getProperty("ssouspathid", new String[0]) ;
		for(int i = 0; i < OrgIDs.length; i++)
		{
			String[] orgattrs = { "cn" } ;
			EAMOrg org = EAMTool.ldap.findOrg(OrgIDs[i], orgattrs) ;
			if(english)
				names.setProperty("1" + OrgIDs[i], EAMUtil.ko2en(org.getProperty("cn"))) ;
			else
				names.setProperty("1" + OrgIDs[i], org.getProperty("cn")) ;
		}
		
		/////////////////////////////////////////////////////
		//	소속 그룹 얻기
		String[] grpattr = { "cn", "ssogid" } ;
		props = EAMTool.ldap.getEntrys("ou=group,o=sso30root", 1,"(ssomember="+UserID+")", grpattr) ;
		for(int i = 0; i < props.length; i++)
			if(english)
				names.setProperty("2"+props[i].getProperty("ssogid"), EAMUtil.ko2en(props[i].getProperty("cn"))) ;
			else
				names.setProperty("2"+props[i].getProperty("ssogid"), props[i].getProperty("cn")) ;

		names.setProperty("3"+	"all"	, "all"	) ;
		names.setProperty("4"+"anyone"	, "anyone") ;
		
		//////////////////////////////////////////////////////////////////////////////////
		//	 Prefix 배열만들기
		int len = 0 ;
		for(Enumeration e = names.propertyNames(); e.hasMoreElements() ; e.nextElement())
			len++ ;
			
		String[] prefixs = new String[len] ;
		len = 0 ;
		for(Enumeration e = names.propertyNames(); e.hasMoreElements() ;)
			prefixs[len++] = (String)e.nextElement();
		
		Hashtable serviceTable = new Hashtable();
		Properties serviceNames = new Properties() ;
		
		//int cnCount = 0 ;
		int uaclCount = 0 ;

		////////////////////////////////////////////////////////////////
		//	권한 방식에 따라.. 
		//	일단, 해당하는 모든 Service
		for(Enumeration e = names.propertyNames(); e.hasMoreElements() ;)
		{
			String uaclprefix = (String)e.nextElement();
			
			String[] attr = { "cn", "ssouacl" } ;
			String filter = "(&(ssouacl="+uaclprefix+" *)(objectClass=sso30Service))" ;
			props = EAMTool.ldap.getEntrys("ou=service,o=sso30root",2, filter, attr) ;
			for(int i = 0; i < props.length; i++)
			{
				if(english)
					serviceNames.setProperty(props[i].getProperty("dn"), EAMUtil.ko2en(props[i].getProperty("cn"))) ;
				else
					serviceNames.setProperty(props[i].getProperty("dn"), props[i].getProperty("cn")) ;
				//cnCount++ ;
				
				String[] ssouacl = props[i].getProperty("ssouacl",new String[0]) ;
				for(int j = 0; j < ssouacl.length; j++)
				{
					if(ssouacl[j].startsWith(uaclprefix))
					{
						//////////////////////////////////////////////////
						//	허용/허용불가 --> 불가우선
						if(conf.getPermissionMethod() == 0)
						{
							EAMUacl uacl = new EAMUacl(ssouacl[j]) ;
							if(uacl.enable() == true)
							{
								EAMUacl temp = (EAMUacl)serviceTable.get(props[i].getProperty("dn")) ;
								if(temp == null)
								{
									if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
									serviceTable.put(props[i].getProperty("dn"), uacl) ;
								}
							}
							else
							{
								if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
								serviceTable.put(props[i].getProperty("dn"), uacl) ;
							}
						}
						//////////////////////////////////////////////////
						//	Bit	: '0','1'의 문자열에서 Bit Oring한다.
						else if(conf.getPermissionMethod() == 1)			// Bit
						{
							EAMUacl uacl = new EAMUacl(ssouacl[j]) ;
							if(uacl.enable() == true)
							{
								String sum = "" ;
								String temp = (String)serviceTable.get(props[i].getProperty("dn")) ;
								if(temp == null)	temp = "" ;
								if(!temp.equals("null"))
								{
									String perm = uacl.getPermission() ;
									//System.out.println("temp:"+temp) ;
									//System.out.println("perm:"+uacl.getPermission()) ;
								
									for(int k = 0; k < temp.length() || k < perm.length(); k++)
										sum = sum + "0" ;
									
									for(int k = 0; k < temp.length(); k++)	
										if(temp.charAt(k) == '1')
											sum = sum.substring(0,k) + "1" + sum.substring(k+1) ;
										
									for(int k = 0; k < perm.length(); k++)	
										if(perm.charAt(k) == '1')
											sum = sum.substring(0,k) + "1" + sum.substring(k+1) ;
										
									//System.out.println(" sum:"+sum) ;	
									
									if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
									serviceTable.put(props[i].getProperty("dn"), sum) ;
								}
							}
							else
							{
								if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
								serviceTable.put(props[i].getProperty("dn"), "null") ;
							}						
						}
						//////////////////////////////////////////////////
						//	Level '0'~'9'의 문자열에서 높은 숫자를 Set한다.
						else if(conf.getPermissionMethod() == 2)	// Level
						{
							EAMUacl uacl = new EAMUacl(ssouacl[j]) ;
							if(uacl.enable() == true)
							{
								EAMUacl temp = (EAMUacl)serviceTable.get(props[i].getProperty("dn")) ;
								if(temp == null)
								{
									//System.out.println("uacl.getPermission():"+uacl.getPermission()) ;

									if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
									serviceTable.put(props[i].getProperty("dn"), uacl) ;						
								}
								else
								{	
									if(temp.enable() == true)
									{
										//System.out.println("uacl.getPermission():"+uacl.getPermission()) ;
										//System.out.println("temp.getPermission():"+temp.getPermission()) ;
										//System.out.println( EAMUtil.toInteger(uacl.getPermission(),-1) + ">" + EAMUtil.toInteger(temp.getPermission(),-1)) ;
										if(EAMUtil.toInteger(uacl.getPermission(),-1) > EAMUtil.toInteger(temp.getPermission(),-1))
											serviceTable.put(props[i].getProperty("dn"), uacl) ;
									}
								}
							}
							else
							{
								if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
								serviceTable.put(props[i].getProperty("dn"), uacl) ;
							}
						}
						//////////////////////////////////////////////////
						//	NVDS처럼하여 USER,DIV,GROUP,ALL,ANY로 Naming하여 표현한다.
						else if(conf.getPermissionMethod() == 3)	// NVDS
						{
							EAMUacl uacl = new EAMUacl(ssouacl[j]) ;
						
							Vector temp = (Vector)serviceTable.get(props[i].getProperty("dn")) ;
							if(temp == null)	temp = new Vector() ;

							temp.add(uacl) ;
						
							if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
							serviceTable.put(props[i].getProperty("dn"), temp) ;
						}
						//////////////////////////////////////////////////
						//	ALL Permission
						else if(conf.getPermissionMethod() == 4)	// ALL
						{
							EAMUacl uacl = new EAMUacl(ssouacl[j]) ;
						
							Vector temp = (Vector)serviceTable.get(props[i].getProperty("dn")) ;
							if(temp == null)	temp = new Vector() ;

							temp.add(uacl) ;
						
							if(serviceTable.containsKey(props[i].getProperty("dn")) == false)	uaclCount++ ;
							serviceTable.put(props[i].getProperty("dn"), temp) ;							
						}
						break ;	
					}
				}
				
				//LineDisplay("["+uaclCount+"/"+cnCount+"]") ;
			}
		}
			
		//System.out.println("") ;
		////////////////////////////////////////////////////////////////////////////////////////
		//	       서비슷구분, 서비스ID, 서비스Path, 서비스명, MappingType, MappingID, Mapping명, 허용, 권한값
		//4anyone :서비슷구분, 서비스ID, 서비스Path, 서비스명,      아무나,    anyone,    anyone,              
		//   3all :서비슷구분, 서비스ID, 서비스Path, 서비스명,        모든,       all,       all,             
		//2GroupID:서비슷구분, 서비스ID, 서비스Path, 서비스명,        그룹,   GroupID,    그룹명,
		//  1OrgID:서비슷구분, 서비스ID, 서비스Path, 서비스명,        조직,     OrgID,    조직명,             
		// 0UserID:서비슷구분, 서비스ID, 서비스Path, 서비스명,      사용자,    UserID,사용자성명,	
		
		String csvfile = UserID +".csv"	;	
		CsvFile csv = new CsvFile(csvfile) ;
		String[] title = { "서비스구분", "서비스ID", "서비스Path", "서비스명", "MappingType", "MappingID", "Mapping명", "허용여부", "권한값" } ;
		csv.print(title) ;
		
		String[] typeName = { "사용자", "조직", "그룹", "모든", "아무나" } ;
		int count = 0 ;
		for (Enumeration e = serviceTable.keys(); e.hasMoreElements() ;)
		{
			LineDisplay("["+(++count)+"/"+uaclCount+"]..."+((int)((count*100)/uaclCount)) +"%") ;
			String serviceDN = (String)e.nextElement();
			String[] line = { "", "", "", "", "", "", "", "", "" } ;
			
			line[0] = ListTool.getRootID(serviceDN) ;
			line[1] = ListTool.getID(serviceDN) ;
			line[2] = ListTool.getPath(serviceDN) ;
			line[3] = serviceNames.getProperty(serviceDN) ;

			if(conf.getPermissionMethod() == 0)					// 가부
			{
				EAMUacl tmp = (EAMUacl)serviceTable.get(serviceDN) ;
			
				if(0 <= tmp.getType() && tmp.getType() < typeName.length)
					line[4] = typeName[tmp.getType()] ;
				
				line[5] = tmp.getID() ;
				line[6] = names.getProperty(tmp.getType() + tmp.getID()) ;
				line[7] = (tmp.enable()) ? "허용" : "불가" ;
				csv.print(line) ;
			}
			else if(conf.getPermissionMethod() == 1)			// Bit
			{
				line[8] = (String)serviceTable.get(serviceDN) ;
				csv.print(line) ;
			}
			else if(conf.getPermissionMethod() == 2)			// Level
			{			
				EAMUacl tmp = (EAMUacl)serviceTable.get(serviceDN) ;
			
				if(0 <= tmp.getType() && tmp.getType() < typeName.length)
					line[4] = typeName[tmp.getType()] ;
				
				line[5] = tmp.getID() ;
				line[6] = names.getProperty(tmp.getType() + tmp.getID()) ;
				line[8] = (tmp.enable()) ? tmp.getPermission() : "null" ;
				csv.print(line) ;
			}
			else if(conf.getPermissionMethod() == 3)			// NVDS
			{
				Vector temp = (Vector)serviceTable.get(serviceDN) ;
				EAMNVDS nvds = new EAMNVDS() ;
				for (Enumeration ee = temp.elements() ; ee.hasMoreElements() ;)
				{
         			EAMUacl tmp = (EAMUacl)ee.nextElement();
         			if(tmp.enable() == true)
         			{
         				if(tmp.getType() == 0)		nvds.add("USER"	,tmp.getPermission()) ;
         				else if(tmp.getType() == 1)	nvds.add("DIV"	,tmp.getPermission()) ;
         				else if(tmp.getType() == 2)	nvds.add("GROUP",tmp.getPermission()) ;
         				else if(tmp.getType() == 3) nvds.add("ALL"	,tmp.getPermission()) ;
         				else if(tmp.getType() == 4) nvds.add("ANY"	,tmp.getPermission()) ;
         			}
         			else
         			{
         				nvds = null ;
         				break ;
         			}
     			}
				line[8] = (nvds == null) ? "null" : nvds.getQuery() ;
				csv.print(line) ;
			}
			else if(conf.getPermissionMethod() == 4)			// ALL
			{
				Vector temp = (Vector)serviceTable.get(serviceDN) ;
				for (Enumeration ee = temp.elements() ; ee.hasMoreElements() ;)
				{
         			EAMUacl tmp = (EAMUacl)ee.nextElement();

					if(0 <= tmp.getType() && tmp.getType() < typeName.length)
						line[4] = typeName[tmp.getType()] ;
					line[5] = tmp.getID() ;
					line[6] = names.getProperty(tmp.getType() + tmp.getID()) ;
					line[7] = (tmp.enable()) ? "허용" : "불가" ;
					line[8] = tmp.getPermission() ;
					
         			csv.print(line) ;
     			}

			}
	    }
	    csv.close() ;
	    System.out.println("") ;
	    System.out.println("\""+csvfile+"\" Viewing....") ;
	//	System.out.println("-----uacl["+i+"/"+uacl.length+"]-------") ;
	//	System.out.println("getType()      :[" + uacl[i].getType()		+"]") ;
	//	System.out.println("getID()        :[" + uacl[i].getID()		+"]") ;
	//	System.out.println("enable()       :[" + uacl[i].enable()		+"]") ;
	//	System.out.println("getPermission():[" + uacl[i].getPermission()+"]") ;
	//	System.out.println("getParentDN()  :[" + uacl[i].getParentDN()	+"]") ;
		return ;
	}

	public static void ListServiceUACL(String ServiceDN)
	{
		////////////////////////////////////
		// Language Check
		boolean english = false ;
		String language = System.getProperty("user.language");
		if(!(language.indexOf("ko") >= 0 || language.indexOf("kr") >= 0 || language.indexOf("KO") >= 0 || language.indexOf("KR") >= 0))
			english = true ;
			
		EAMConf conf	= new EAMConf() ;
		String[] attr = { "cn", "ssouacl" } ;
		EAMProperties prop = EAMTool.ldap.getEntry(ServiceDN, attr)  ;
			
		String[] tempuacl = prop.getProperty("ssouacl",new String[0]) ;
		EAMUacl[] uacl = new EAMUacl[tempuacl.length] ;
		for(int i = 0; i < tempuacl.length; i++) 
			uacl[i] = new EAMUacl(tempuacl[i]) ;

		int Total = 0 ;
		Hashtable userTable = new Hashtable();
		Properties names = new Properties() ;
		for(int i = 0; i < uacl.length; i++)
		{
			//System.out.println("...."+i+".......") ;
			if(uacl[i].getType() == 4)
			{
				names.setProperty(uacl[i].getType() + uacl[i].getID(), "anyone") ;
			}
			else if(uacl[i].getType() == 3)
			{
				names.setProperty(uacl[i].getType() + uacl[i].getID(), "all") ;
			}
			else if(uacl[i].getType() == 2)
			{
				String[] grpattrs = { "cn", "ssomember" } ;
				EAMProperties grp = EAMTool.ldap.getEntry("ssogid="+uacl[i].getID()+",ou=group,o=sso30root",grpattrs) ;
				
				String[] members = grp.getProperty("ssomember",new String[0]) ;
				for(int j = 0; j < members.length; j++)
				{	
					Vector temp = (Vector)userTable.get(members[j]) ;
					if(temp == null)
					{
						Total++ ;
						temp = new Vector() ;
					}
					
					temp.add(uacl[i]) ;
					userTable.put(members[j], temp) ;
				}
				if(english)
					names.setProperty(uacl[i].getType() + uacl[i].getID(), EAMUtil.ko2en(grp.getProperty("cn"))) ;
				else
					names.setProperty(uacl[i].getType() + uacl[i].getID(), grp.getProperty("cn")) ;
			}
			else if(uacl[i].getType() == 1)
			{	
				EAMProperties[] props = EAMTool.ldap.getEntrys("ou=user,o=sso30root",1,"(ssouspathid="+uacl[i].getID()+")","uid") ;
				for(int j = 0; j < props.length; j++)
				{	
					Vector temp = (Vector)userTable.get(props[j].getProperty("uid")) ;
					if(temp == null)
					{
						Total++ ;
						temp = new Vector() ;
					}
					
					temp.add(uacl[i]) ;
					userTable.put(props[j].getProperty("uid"), temp) ;
				}
				
				String[] orgattrs = { "cn" } ;
				EAMOrg org = EAMTool.ldap.findOrg(uacl[i].getID(), orgattrs) ;
				if(english)
					names.setProperty(uacl[i].getType() + uacl[i].getID(), EAMUtil.ko2en(org.getProperty("cn"))) ;
				else
					names.setProperty(uacl[i].getType() + uacl[i].getID(), org.getProperty("cn")) ;
			}
			else if(uacl[i].getType() == 0)
			{	
				Vector temp = (Vector)userTable.get(uacl[i].getID()) ;
				if(temp == null)
				{
					Total++ ;
					temp = new Vector() ;
				}
					
				temp.add(uacl[i]) ;
				userTable.put(uacl[i].getID(), temp) ;
				
				
				EAMProperties usr = EAMTool.ldap.getEntry("uid="+uacl[i].getID()+",ou=user,o=sso30root", "cn") ;
				if(english)
					names.setProperty(uacl[i].getType() + uacl[i].getID(), EAMUtil.ko2en(usr.getProperty("cn"))) ;
				else
					names.setProperty(uacl[i].getType() + uacl[i].getID(), usr.getProperty("cn")) ;
			}
		}

		for(int i = 0; i < uacl.length; i++)
		{
			if(uacl[i].getType() == 4 || uacl[i].getType() == 3)
			{
     			for (Enumeration e = userTable.keys() ; e.hasMoreElements() ;)
     			{
         			String UserID = (String)e.nextElement();
         			Vector temp = (Vector)userTable.get(UserID) ;
         			temp.add(uacl[i]) ;
         			
         			userTable.put(UserID, temp) ;
     			}
			}
		}
		
		//System.out.println("==================") ;
		///////////////////////////////////////////////////////////////////////////////
		//	       사용자ID, 사용자성명, MappingType, MappingID, Mapping명, 허용, 권한값
		//4anyone :      "",         "",      아무나,    anyone,    anyone,              
		//   3all :      "",         "",        모든,       all,       all,             
		//2GroupID:MemberID, Member성명,         "",    GroupID,    그룹명,
		//  1OrgID:MemberID, 조직원성명,        조직,     OrgID,    조직명,             
		// 0UserID:  UserID, 사용자성명,      사용자,        "",        "",	
		String csvfile = ListTool.getPath(ServiceDN,"-")+".csv"	;	
		CsvFile csv = new CsvFile(csvfile) ;

		String[] title = { "사용자ID", "사용자성명", "MappingType", "MappingID", "Mapping명", "허용여부", "권한값" } ;
		csv.print(title) ;
		String[] typeName = { "사용자", "조직", "그룹", "모든", "아무나" } ;
		
		for(int i = 0; i < uacl.length; i++)
		{
			String[] line = { "", "", "", "", "", "", "" } ;
				
			line[2] = typeName[uacl[i].getType()] ;
			line[3] = uacl[i].getID() ;
			line[4] = names.getProperty(uacl[i].getType() + uacl[i].getID(),"") ;
			line[5] = (uacl[i].enable()) ? "허용" : "불가" ;
			line[6] = uacl[i].getPermission() ;
			csv.print(line) ;
		}
				
		int count = 0 ;
		for (Enumeration e = userTable.keys(); e.hasMoreElements() ;)
		{
			LineDisplay("["+(++count)+"/"+Total+"]..."+((int)((count*100)/Total)) +"%") ;
			String UserID = (String)e.nextElement();
			if(UserID.length() == 0)	continue ;
			String[] line = { "", "", "", "", "", "", "" } ;
			line[0] = UserID ;
			
			EAMProperties user = EAMTool.ldap.getEntry("uid="+UserID+",ou=user,o=sso30root", "cn") ;
			if(english)
				line[1] = EAMUtil.ko2en(user.getProperty("cn")) ;
			else
				line[1] = user.getProperty("cn") ;
			
			Vector tmps = (Vector)userTable.get(UserID) ;
			
			//////////////////////////////////////////////////
			//	허용/허용불가 --> 불가우선
			if(conf.getPermissionMethod() == 0)
			{
				boolean enabled = true ;
				for (Enumeration ee = tmps.elements() ; ee.hasMoreElements() && enabled;)
				{
         			EAMUacl tmp = (EAMUacl)ee.nextElement();
         			
					line[2] = typeName[tmp.getType()] ;
					line[3] = tmp.getID() ;
					line[4] = names.getProperty(tmp.getType() + tmp.getID()) ;
         			if(tmp.enable() == false)
         				enabled = false ;
				}
				line[5] = (enabled) ? "허용" : "불가" ;
				csv.print(line) ;
			}
			//////////////////////////////////////////////////
			//	Bit	: '0','1'의 문자열에서 Bit Oring한다.
			else if(conf.getPermissionMethod() == 1)			// Bit
			{
				String sum  = "" ;
				String temp  = "" ;
				boolean enabled = true ;
				for (Enumeration ee = tmps.elements() ; ee.hasMoreElements() && enabled;)
				{
					sum = "" ;
         			EAMUacl tmp = (EAMUacl)ee.nextElement();
         			enabled = tmp.enable() ;
         			if(enabled)
					{
         				String perm = tmp.getPermission() ;
						for(int k = 0; k < temp.length() || k < perm.length(); k++)
							sum = sum + "0" ;
						for(int k = 0; k < temp.length(); k++)	
							if(temp.charAt(k) == '1')
								sum = sum.substring(0,k) + "1" + sum.substring(k+1) ;	
						for(int k = 0; k < perm.length(); k++)	
							if(perm.charAt(k) == '1')
								sum = sum.substring(0,k) + "1" + sum.substring(k+1) ;
					}
					else
					{
						line[2] = typeName[tmp.getType()] ;
						line[3] = tmp.getID() ;
						line[4] = names.getProperty(tmp.getType() + tmp.getID()) ;
						line[5] = (enabled) ? "허용" : "불가" ;
						line[6] = "null" ;
						break ;
					}
						
         			temp = sum ;
				}
				
				if(enabled)					
					line[6] = temp ;
				csv.print(line) ;
			}
			//////////////////////////////////////////////////
			//	Level '0'~'9'의 문자열에서 높은 숫자를 Set한다.
			else if(conf.getPermissionMethod() == 2)	// Level
			{
				int temp = -1 ;
				boolean enabled = true ;
				for (Enumeration ee = tmps.elements() ; ee.hasMoreElements() && enabled;)
				{
         			EAMUacl tmp = (EAMUacl)ee.nextElement();
         			enabled = tmp.enable() ;
         			if(enabled)
					{					
						if(EAMUtil.toInteger(tmp.getPermission(),-1) > temp)	
						{
							temp = EAMUtil.toInteger(tmp.getPermission(),-1) ;
							line[2] = typeName[tmp.getType()] ;
							line[3] = tmp.getID() ;
							line[4] = names.getProperty(tmp.getType() + tmp.getID()) ;
						}
					}
					else
					{
						line[2] = typeName[tmp.getType()] ;
						line[3] = tmp.getID() ;
						line[4] = names.getProperty(tmp.getType() + tmp.getID()) ;
						line[5] = (enabled) ? "허용" : "불가" ;
						line[6] = "null" ;
						break ;
					}
				}
				
				if(enabled)
					line[6] = "" + temp ;
				csv.print(line) ;
			}
			//////////////////////////////////////////////////
			//	NVDS처럼하여 USER,DIV,GROUP,ALL,ANY로 Naming하여 표현한다.
			else if(conf.getPermissionMethod() == 3)	// NVDS
			{
				EAMNVDS nvds = new EAMNVDS() ;
				boolean enabled = true ;
				for (Enumeration ee = tmps.elements() ; ee.hasMoreElements() && enabled;)
				{
         			EAMUacl tmp = (EAMUacl)ee.nextElement();
         			enabled = tmp.enable() ;
         			if(enabled)
					{
         				if(tmp.getType() == 0)		nvds.add("USER"	,tmp.getPermission()) ;
         				else if(tmp.getType() == 1)	nvds.add("DIV"	,tmp.getPermission()) ;
         				else if(tmp.getType() == 2)	nvds.add("GROUP",tmp.getPermission()) ;
         				else if(tmp.getType() == 3) nvds.add("ALL"	,tmp.getPermission()) ;
         				else if(tmp.getType() == 4) nvds.add("ANY"	,tmp.getPermission()) ;
					}
					else
					{
						line[2] = typeName[tmp.getType()] ;
						line[3] = tmp.getID() ;
						line[4] = names.getProperty(tmp.getType() + tmp.getID()) ;
						line[5] = (enabled) ? "허용" : "불가" ;
						line[6] = "null" ;
						nvds = null ;
						break ;
					}
				}

     			if(enabled)
					line[6] = (nvds == null) ? "null" : nvds.getQuery() ;
				csv.print(line) ;
			}
			else if(conf.getPermissionMethod() == 4)	// ALL Permission
			{
				for (Enumeration ee = tmps.elements() ; ee.hasMoreElements();)
				{
         			EAMUacl tmp = (EAMUacl)ee.nextElement();

					line[2] = typeName[tmp.getType()] ;
					line[3] = tmp.getID() ;
					line[4] = names.getProperty(tmp.getType() + tmp.getID()) ;
					line[5] = (tmp.enable()) ? "허용" : "불가" ;
					line[6] = tmp.getPermission() ;

					csv.print(line) ;
				}
			}
     	}
     	csv.close() ;
	    System.out.println("") ;
     	System.out.println("\""+csvfile+"\" Viewing....") ;
	}
}

