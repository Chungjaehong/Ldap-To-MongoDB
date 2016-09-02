package jaehongLdap;

import eamldap.*;
import java.io.*;
import java.util.Date;

/**
	eamldap�� Log Class.<br>
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
    /** ���Ǽ�	Level	*/	public static final int LOG_FATAL 	= 1;
	/** LDAP�� Level	*/	public static final int LOG_LDAP  	= 2;
    /** ���	Level	*/	public static final int LOG_WARNING = 3;
    /** INFO��	Level	*/	public static final int LOG_INFO  	= 4;
    /** TRACE�� Level	*/	public static final int LOG_TRACE 	= 5;

	private int 	conf_level 	= 0 ;
	private	EAMConf EAMConf 	= null ;

	/** 
	* Default ������
	*/
    public EAMLog()
    {
    }

	/** 
	* EAMConf�� Set�ϴ� ������
	*/
    public EAMLog(EAMConf EAMConf)
    {
    	this.EAMConf = EAMConf ;
	}

	/** 
	* Log�� �����
	* @param	level	�α��� ����
	* @param	msg		�α׳��� Message
	*/
 	public void write(int level, String msg)
 	{
 		write(this.EAMConf, level, msg);
 	}

	/** 
	* Log�� �����
	* @param	EAMConf	EAMConfȭ��
	* @param	level	�α��� ����
	* @param	msg		�α׳��� Message
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
