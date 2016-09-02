package eamldap;

import java.lang.*;
import java.util.*;
import netscape.ldap.*;
import eamldap.objectclass.*;
import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.Properties;
import eamldap.EAMUtil;

/**
	eamldap/props/error.props화일을 기준으로 Error Message를 관리하는 Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMUser.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMError
{
	private int 			code 	= 0 ;
	private String 			message	= "" ;
    private EAMProperties 	errProp = null ;
    
    public EAMError()						{	this.errProp = new EAMProperties("eamldap/props/error.props") ;	}
    public EAMError(File f)					{	this.errProp = new EAMProperties(f) ;							}
    public EAMError(int code)				{	this.errProp = new EAMProperties("eamldap/props/error.props") ;	this.set(code) ;	}
	public int set(LDAPException e)			{	this.code = 30000 + e.getLDAPResultCode() ;	this.message = this.codeToString(this.code) ;	return this.code ;	}
    public int set(int code)				{	this.code = code ;							this.message = this.codeToString(this.code) ;	return this.code ;	}
    public int reset()						{	this.code = 0 ;								this.message = "";								return this.code ;	}	

    public String codeToString(int code)
    {
    	if(code < 0)	code = code * -1 ;
       	if(this.errProp == null)	return "Cannot find eamldap/props/error.props" ;
    	else						return this.errProp.getProperty(""+code, "Unknown Error!!") ;
    }

    public String getErrorMessage()			{	return this.message;	}
    public String getErrorMsg()				{	return this.message;	}
    public int getErrorCode()				{	return this.code;		}
    public int getError()					{	return this.code;		}
    public String toString()				{	return "EAMError:" + this.code + ":" + this.message;	}
    
    public static void main (String args []) throws Exception 
    {
    	File		fp	= null ;
    	EAMError	err = null ;
    	
    	switch (args.length)
    	{
    		case	2	:
    			fp = new File(args[0]) ;
    			
    		case	1	:
    			if(fp == null)	err = new EAMError() ;
				else			err = new EAMError(fp) ;
				
				err.set(Integer.parseInt(args[args.length - 1])) ;
				
				System.out.println(err) ;
				
    			break ;
    					
    		default		:
    			System.out.println("java eamldap.EAMError <error>") ;
    			System.out.println("java eamldap.EAMError [ErrorFile] <error>") ;
    			break ;		    		
    	}
    }
}
