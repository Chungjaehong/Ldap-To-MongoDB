package jaehongLdap;

import	jaehongLdap.*;
import	eamldap.attribute.*;
import	eamldap.objectclass.*;
import	netscape.ldap.*;
import	netscape.ldap.util.DN;
import	netscape.ldap.util.ConnectionPool;

import	java.util.*;
import	java.lang.*;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import	java.io.*;

import javax.naming.directory.SearchResult;

import org.apache.catalina.util.Base64;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class EAMLdap
{
    protected	static	ConnectionPool	ldPool = null;
    protected	static	EAMConf			conf = null ;
	protected	static	EAMLog			log	= null ;
	protected			EAMError		err = new EAMError() ;

	private static MongoClient mongoClient = null;
	private static DB db = null;
	
    static
    {
        try
        {
            conf	= new EAMConf() ;
	        log		= new EAMLog(conf) ;

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
		생성자
	*/
    public EAMLdap()
    {
    }

	/**
		소멸자
	*/
    public void destroy()
    {
	}

	/** 실행후 에러코드	얻기 	*/	public int 		getErrorCode()	{	return this.err.getError() ;	}
	/** 실행후 에러메시지 얻기	*/	public String	getErrorMsg()	{	return this.err.getErrorMsg() ;	}


    public static void main (String args []) throws Exception
    {
    	EAMLdap ldap = new EAMLdap() ;

    	//String[] userAttr = { "uid", "cn", "ssouspathid" ,"mail", "ssorrn","ssoprofile" } ;
		//Boolean user = ldap.searchVector("ou=user,o=sso30root", 1, "(objectclass=*)", userAttr, null, 0);
		
    	//String[] groupAttr = { "ssogid", "cn", "uniquemember" ,"ssomember", "ssousorgid"} ;
		//Boolean group = ldap.searchVector("ou=group,o=sso30root", 1, "(objectclass=*)", groupAttr , null, 0);

    	String[] serviceAttr = { "ssogid", "cn", "ssossid" ,"ssouacl"} ;
    	Boolean service = ldap.searchVector("ou=service,o=sso30root", 1, "(objectclass=*)", serviceAttr , null, 0);

    	/*String[] orgAttr = { "ssousid", "cn"} ;
    	Boolean org = ldap.searchVector("ssousid=1000000000,ou=org,o=sso30root", 2, "(objectclass=*)", orgAttr , null, 20);*/

		
    }
    
	
	/**
		@param	basedn		Search Base DN(찾기 기준 위치)
		@param	scope		0:Base Scope(BaseDN만 찾기)1:One Level(바로 아래만 찾기), 2:SubScope(BaseDN포함 그 아래 모두에서 찾기)
		@param	filter		찾기조건
		@param	attrs		찾을 Attribute Name들
		@param	sortAttr	정렬할 Attribute Name
		@param	maxResults	찾기건수 제한( 0:이면 무한 )
	*/
	private Boolean searchVector(String basedn, int scope, String filter, String[] attrs, String sortAttr, int maxResults)
	{
		err.reset() ;

		if(ldPool == null)	return false ;

		LDAPConnection ld = null ;

		try
		{
			ld = ldPool.getConnection();
			LDAPSearchConstraints 	cons = ld.getSearchConstraints();
			cons.setBatchSize( 1 );
			cons.setMaxResults( maxResults );

			LDAPSearchResults res = ld.search(basedn, scope, filter, attrs, false, cons);
			if(sortAttr != null)	res.sort( new LDAPCompareAttrNames(sortAttr, true) );
			int i=0;
			while ( res.hasMoreElements() )
			{
				try
				{
					if(i==0){
						mongodbConnect();
					}/*else if (i==5000) {
						i=0;
						mongodbDisConnect();
						mongodbConnect();
					}
					*/
					i++;
					LDAPEntry findEntry = res.next();
					
					if(basedn.endsWith("ou=user,o=sso30root"))
					{
						printEntry(findEntry, attrs, "user");
					}
					else if(basedn.endsWith("ou=group,o=sso30root"))
					{
						printEntry(findEntry, attrs, "group");
					}
					else if(basedn.endsWith("ssousid=1000000000,ou=org,o=sso30root"))
					{
						printEntry(findEntry, attrs, "org");
					}
					else if(basedn.endsWith("ou=service,o=sso30root"))
					{
						printEntry(findEntry, attrs, "service");
					}
				} catch ( LDAPException e ) { continue;	}
			}
			mongodbDisConnect();


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
		return true;
	}

	private ArrayList searchSub(String basedn, int scope, String filter, String[] attrs, String sortAttr, int maxResults, String id)
	{
		err.reset() ;
		if(ldPool == null)	return null ;
		LDAPConnection ld = null ;

		try
		{
			ld = ldPool.getConnection();
			LDAPSearchConstraints 	cons = ld.getSearchConstraints();
			cons.setBatchSize( 1 );
			cons.setMaxResults( maxResults );
			
			LDAPSearchResults res = ld.search(basedn, scope, filter, attrs, false, cons);
			
			if(sortAttr != null)	res.sort( new LDAPCompareAttrNames(sortAttr, true) );
			
			int i=0;
			
			BasicDBObject obj;
			
			ArrayList ssoList = new ArrayList<Object>();
			
			while ( res.hasMoreElements() )
			{
				try
				{
					LDAPEntry findEntry = res.next();
					
					if(basedn.endsWith("ssossid="+id+",ou=service,o=sso30root"))
					{
						//System.out.println("here>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
						obj = printSubEntry(findEntry, attrs, "service_sub");
						ssoList.add(obj);
					}/*else if(basedn.endsWith("ou=service,o=sso30root"))
					{
						printSubEntry(findEntry, attrs, "service");
					}*/
				} catch ( LDAPException e ) { continue;	}
			}
			return ssoList;
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
		return null;
	}

	public void mongodbConnect(){
	    try {
	      mongoClient = new MongoClient();
	       
	      // Old way to get database - deprecated now
	      db = mongoClient.getDB("hanssem");
	      
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	}

	public void mongodbDisConnect(){
		try {
			mongoClient.close();

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public void mongDBInsert(BasicDBObject param,String col) throws DuplicateKeyException{
	    try {
	      DBCollection collection = db.getCollection(col);
	      //System.out.println("collection: " + collection);
	      
	      collection.insert(param);
	      
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }finally{
	//      mongoClient.close();
	    }
		
	}

	public boolean mongDBselect(String id, String col) throws DuplicateKeyException{
		
		try {
			DBObject  foundDoc = db.getCollection(col).findOne(id);
			if (foundDoc ==null){
				return false;
			}else{
				//System.out.println(foundDoc.toString());
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
//			mongoClient.close();
		}
		
		return false;
	}

	public boolean mongDBupdate(BasicDBObject param, String col) throws DuplicateKeyException{
		
		
		try {
			final BasicDBObject updateQuery = new BasicDBObject("_id", param.get("_id"));
/*			param.remove("cn");
			param.append("cn", "정재홍");
			
			System.out.println(">>update>>>>>>>>>>>"+param.toJson());*/
			
			final BasicDBObject updateParam = new BasicDBObject("$set", param);
			//System.out.println(">>>>>"+db.toString());
			db.getCollection(col).update(updateQuery, updateParam);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		//	mongoClient.close();
		}
		
		return false;
	}
	
	
	 public void printEntry(LDAPEntry entry, String[] attrs,String col) {
		    
/*		 	String[] array = entry.getDN().split(",");
		 	System.out.println(array.length);

		 	System.out.println("DN: " + entry.getDN());
		 	for (String string : array) {
		 		System.out.println(">>>>>>>>>>>>>>>>>"+string);
			}
		 	
//	        System.out.println(entry.getAttribute("ssoUsid"));
*/	        
			final BasicDBObject paramCh = new BasicDBObject();
			ArrayList ssoUsList = new ArrayList<Object>();

			ArrayList ssomember = new ArrayList<Object>();
			ArrayList uniquemember = new ArrayList<Object>();

			ArrayList ssosid = new ArrayList<Object>();
			ArrayList ssouacl = new ArrayList<Object>();
			
			boolean statFlag = false;
			
	        for (int i=0; i < attrs.length; i++) {
	            LDAPAttribute attr = entry.getAttribute(attrs[i]);
	            if (attr == null) {
	                System.out.println("    [" + attrs[i] + ": not present]");
	                continue;
	            }

	            Enumeration enumVals = attr.getStringValues();
	            boolean hasVals = false;
	            while ( (enumVals != null) && (enumVals.hasMoreElements()) ) {
	                String val = (String) enumVals.nextElement();

	                if(col.equals("user")){
	                	if(attrs[i].equals("uid")){
	                		paramCh.put("_id", val);
	                		statFlag=mongDBselect(val, col);
	                		
	                		if(!statFlag){
	                			paramCh.put("pwd", new String(Base64.encode(val.getBytes())));
	                			paramCh.put("loginFlag", "0");
	                			paramCh.put("loginRetryCnt", "0");
	                		}
	                		
	                	}else if (attrs[i].equals("ssouspathid")) {
	                		ssoUsList.add(val);
	                		paramCh.put("ssouspathid", ssoUsList);
	                	}else{
	                		paramCh.put(attrs[i], val);
	                	}
                	
	                }//user End
	                
	                if(col.equals("group")){
	                	if(attrs[i].equals("ssogid")){
	                		paramCh.put("_id", val);
	                		
	                		statFlag=mongDBselect(val, col);
	                		
	                	}else if (attrs[i].equals("ssomember")) {
	                		ssomember.add(val);
	                		paramCh.put("ssomember", ssomember);
	                		
	                	}else if (attrs[i].equals("uniquemember")) {
	                		//uid=0000D160,ou=user,o=sso30root
	                    	String[] repVal = val.replace(",", "=").split("=");
	                    	
	                    	uniquemember.add(repVal[1].toString());
	                    	paramCh.put("uniquemember", uniquemember);
	                		
	                	}else{
	                		paramCh.put(attrs[i], val);
	                	}
	                	//System.out.println(attrs[i]+"//////"+val);
	                }

	                if(col.equals("service")){
	                	//serviceAttr = { "ssogid", "cn", "ssossid" ,"ssouacl", "ssosid"} ;
	                	//System.out.println(">>>>>>"+attrs[i]+"////val>>>>>>"+ val);
	                	if(attrs[i].equals("ssossid")){
	                		paramCh.put("_id", val);
	                		
	                		String[] serviceSubAttr = { "ssosid", "cn","ssouacl"} ;
	                		ssosid = searchSub("ssossid="+val+",ou=service,o=sso30root", 1, "(objectclass=*)", serviceSubAttr, null, 0, val);
	                		paramCh.put("ssosid", ssosid);
	                		
	                		statFlag=mongDBselect(val, col);
	                	}else if (attrs[i].equals("ssouacl")) {
	                		ssouacl.add(val);
	                		paramCh.put("ssouacl", ssouacl);
	                	}else{
	                		paramCh.put(attrs[i], val);
	                	}
	                }

	                /*if(col.equals("org")){
	                	//String[] orgAttr = { "ssousid", "cn"} ;
	                	if(attrs[i].equals("ssousid")){
	                		paramCh.put("_id", val);
	                		
	                		//String[] serviceSubAttr = { "ssosid", "cn","ssouacl"} ;
	                		//ssosid = searchSub("ssossid="+val+",ou=service,o=sso30root", 1, "(objectclass=*)", serviceSubAttr, null, 0, val);
	                		paramCh.put("ssosid", ssosid);
	                		
	                		statFlag=mongDBselect(val, col);
	                	}else{
	                		//System.out.println(attrs[i] +"//////"+ val);
	                		paramCh.put(attrs[i], val);
	                	}
	                }*/
	                
	                hasVals = true;
	            }
	            if (!hasVals) {
	                System.out.println("    [" + attrs[i] + ": has no values]");
	            }
	        }
	        log.write(EAMLog.LOG_INFO,"MongoInsert....["+col + "] collaction") ;
	        //System.out.println("["+col + "] collaction...............");
	        //System.out.println(">>>>>>>>>>"+paramCh.toJson());
	        //System.out.println("-------------------------row-------------------------");
	        if(statFlag){
	        	mongDBupdate(paramCh,col);
	        }else{
	        	mongDBInsert(paramCh,col);
	        }
	    }

	 public BasicDBObject printSubEntry(LDAPEntry entry, String[] attrs,String col) {
		 
		 //System.out.println("DN: " + entry.getDN());
		 
		 final BasicDBObject paramCh = new BasicDBObject();

		 ArrayList ssouacl = new ArrayList<Object>();
		 
		 boolean statFlag = false;
		 
		 for (int i=0; i < attrs.length; i++) {
			 LDAPAttribute attr = entry.getAttribute(attrs[i]);
			 if (attr == null) {
				 //System.out.println("    [" + attrs[i] + ": not present]");
				 continue;
			 }
			 
			 Enumeration enumVals = attr.getStringValues();
			 boolean hasVals = false;
			 while ( (enumVals != null) && (enumVals.hasMoreElements()) ) {
				 String val = (String) enumVals.nextElement();
				 
				 if(col.equals("service_sub")){
					 if(attrs[i].equals("ssosid")){
						 paramCh.put("ssosid", val);
					 }else if (attrs[i].equals("ssouacl")) {
						 
						 ssouacl.add(val);
						 paramCh.put("ssouacl", ssouacl);
						 
					 }else{
						 paramCh.put(attrs[i], val);
					 }
				 }
				 
				 hasVals = true;
			 }
			 if (!hasVals) {
				 System.out.println("    [" + attrs[i] + ": has no values]");
			 }
		 }
		 log.write(EAMLog.LOG_INFO,"MongoInsert....["+col + "] collaction") ;
		 //System.out.println("["+col + "] collaction...............");
		 //System.out.println(">sub>>>>>>>>>"+paramCh.toJson());
		 
		 return paramCh; 
	 }
	 
}

