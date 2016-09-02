package eamldap.util;

import	eamldap.*;
import	eamldap.objectclass.*;
import	eamldap.attribute.*;
import	java.util.*;
import	java.lang.*;
import	java.io.*;

/**
	<b>ssoUsPathid �缳�� �� Gabbage Clear�ϴ� Utiliy�� Class</b><br>
<br>
	Copyright (C) 2000-2004 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	sso35ldap API Module<br>
	FileName	:	EAMTool.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	History		:	20030210 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMTool
{
	protected static EAMLdap ldap = null ;
	
	protected static int msg_len = 0 ;
    	
	public static String getKeyIn() throws IOException
	{
		byte[] keyin = new byte[256] ;
		System.in.read(keyin,0,256) ;
		int i ;
		for(i = 0; i < 256 && i < keyin.length; i++)
		{
			if(keyin[i] == 0x00)	break ;
			if(keyin[i] == 0x0D)	break ;
			if(keyin[i] == 0x0A)	break ;
		}
		return new String(keyin, 0, i) ;
	}
	
	protected static void LineDisplay(String msg)
	{
		EAMTool.LineDisplay(msg,false) ;
	}
	
	/**
	*	�ѱ�Length
	*	@param	str		���ڿ�
	*/
	public static int getHanLength(String str)
	{
		byte[] s = str.getBytes() ;
		return s.length ;
	}
	
	protected static void LineDisplay(String msg, boolean LineFeed)
	{
		for(int i = 0; i < EAMTool.msg_len;i++)	System.out.print("\b") ;
		for(int i = 0; i < EAMTool.msg_len;i++)	System.out.print(" ") ;
		for(int i = 0; i < EAMTool.msg_len;i++)	System.out.print("\b") ;
		
		if(LineFeed)
		{	
			System.out.println(msg) ;
			EAMTool.msg_len = 0 ;
		}
		else
		{
			System.out.print(msg) ;
			EAMTool.msg_len = EAMTool.getHanLength(msg) ;
		}
	}

	public static void main (String args []) throws IOException
	{
		EAMTool.ldap = new EAMLdap() ;
		System.out.println("") ;
		System.out.println("[1]. ��� (Export) �ϴ� �͵�") ;
		System.out.println("[2]. ��������ü(Certificate Change) �ϴ� �͵�") ;
		System.out.println("[1,2]�߿��� �����Ͽ� �ֽñ� �ٶ��ϴ�.[0]�� Quit�Դϴ�.") ;

		
		String key = EAMTool.getKeyIn() ;
		if(key == null)			return ;
		if(key.length() == 0)	return ;
				
		switch(key.charAt(0))
		{
			case '1' :	ListTool.interAction();		break ;
			case '2' :	CertTool.interAction();		break ;
			default  :	return ;
		}
	}
}
