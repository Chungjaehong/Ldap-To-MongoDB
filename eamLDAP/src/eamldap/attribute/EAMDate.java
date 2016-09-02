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
	등의 값을 setString()하거나 new EAMDate(String b)를 사용하시면 좋습니다.<br>

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
public class EAMDate extends Date
{
	public EAMDate () 
	{
	}

	public EAMDate(long d)
	{
		super(d) ;
	}

	public EAMDate(String d)
	{
		long ldate = 0; 
		if(d != null)
		{
			if((d.startsWith("20") || d.startsWith("19"))  && d.endsWith("Z") && (d.length() == 15))
			{
				try
				{
					Calendar temp = Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"))  ;
					temp.set(	Integer.parseInt(d.substring( 0, 4))	,
								Integer.parseInt(d.substring( 4, 6)) -1	, 
								Integer.parseInt(d.substring( 6, 8))	,
								Integer.parseInt(d.substring( 8,10))	,
								Integer.parseInt(d.substring(10,12))	,
								Integer.parseInt(d.substring(12,14))	) ;
					//ldate = temp.getTimeInMillis() ;	// 1.4에서는 1.3에서는 안됨.
					ldate = temp.getTime().getTime() ;	// 1.4 및 1.3에서도 됨.
				}
				catch(NumberFormatException e)	{}
			}
			else
			{
				try
				{
					if(d.length() <= 10)	ldate = Long.parseLong(d) * 1000 ;
					else					ldate = Long.parseLong(d) ;
				}
				catch(NumberFormatException e)	{}
			}
		}
		
		super.setTime(ldate) ;
		//System.out.println("EAMDate["+d+"]:"+super.toString()) ;
	}
	
	public String getLongString()
	{
		return "" + (super.getTime() / 1000) ;
	}

    public static void main (String args []) throws Exception 
    {	
    	EAMDate modifyTimestamp = new EAMDate("20050329020518Z") ;
   		System.out.println("getLongString():" + modifyTimestamp.getLongString()) ;
   		
   		EAMDate ssoPwdChangeTime  = new EAMDate("1111991574") ;
   		System.out.println("getLongString():" + ssoPwdChangeTime.getLongString()) ;
   		
   		ssoPwdChangeTime  = new EAMDate("1111991574000") ;
   		System.out.println("getLongString():" + ssoPwdChangeTime.getLongString()) ;
   		
   		EAMDate tokenTime  = new EAMDate("1138779989") ;
   		System.out.println("tokenTime():" + tokenTime) ;
   		
   		tokenTime  = new EAMDate("1138779970") ;
   		System.out.println("tokenTime():" + tokenTime) ;
    }
}
