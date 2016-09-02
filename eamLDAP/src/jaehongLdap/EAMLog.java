package jaehongLdap;

import eamldap.*;
import java.io.*;
import java.util.Date;

/**
	eamldap의 Log Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMLog.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMLog
{
    /** Default Level	*/	public static final int LOG_DEFAULT = 0;
    /** 주의성	Level	*/	public static final int LOG_FATAL 	= 1;
	/** LDAP성 Level	*/	public static final int LOG_LDAP  	= 2;
    /** 경고성	Level	*/	public static final int LOG_WARNING = 3;
    /** INFO성	Level	*/	public static final int LOG_INFO  	= 4;
    /** TRACE성 Level	*/	public static final int LOG_TRACE 	= 5;

	private int 	conf_level 	= 0 ;
	private	EAMConf EAMConf 	= null ;

	/** 
	* Default 생성자
	*/
    public EAMLog()
    {
    }

	/** 
	* EAMConf를 Set하는 생성자
	*/
    public EAMLog(EAMConf EAMConf)
    {
    	this.EAMConf = EAMConf ;
	}

	/** 
	* Log를 남기기
	* @param	level	로그의 레벨
	* @param	msg		로그남기 Message
	*/
 	public void write(int level, String msg)
 	{
 		write(this.EAMConf, level, msg);
 	}

	/** 
	* Log를 남기기
	* @param	EAMConf	EAMConf화일
	* @param	level	로그의 레벨
	* @param	msg		로그남기 Message
	*/
    public static void write(EAMConf EAMConf, int level, String msg)
    {
        if(EAMConf == null)	return ;
        	
        if(EAMConf.getLogLevel() == 10)
            System.out.println(msg);
        else if(level <= EAMConf.getLogLevel())
        {
			Date now = new Date() ;
            String dir  = EAMConf.getLogDir();
			int slash = dir.lastIndexOf(File.separator) ;

			String file = dir.substring(slash+1) ;

			//System.out.println("file:"+file) ;
			if(file.lastIndexOf('.') < 0)
				file = file + EAMUtil.toYYYYMMDD(now) + ".log" ;
			//System.out.println("....file:"+file) ;
			
			dir = dir.substring(0,slash+1) + file ;
			//System.out.println("....dir:"+dir) ;
            try
            {
                PrintWriter printwriter = new PrintWriter(new FileOutputStream(dir, true));
                printwriter.write(EAMUtil.toYYYYMMDDHHMMSS(now) + ":(" + level + ")" + msg + System.getProperty("line.separator"));
                printwriter.close();
            }
            catch(IOException ioexception)
            {
                ioexception.printStackTrace();
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
}
