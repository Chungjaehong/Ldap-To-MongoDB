package eamldap.util;

import java.io.*;
import java.lang.*;

/**
	Excel(콤마분리된 Excel)의 에 관련된 Util성 Class<br>
<br>
	Copyright (C) 2000-2004 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	sso35ldap API Module<br>
	FileName	:	CsvFile.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	History		:	20030210 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class CsvFile
{
	String			file_name = "temp.txt";
	int				line_count = 0;
	BufferedWriter	buf_writer ;
	
	public CsvFile()
	{
		this.line_count = 0;
	}
	
	public CsvFile(String fname)
	{
		this.line_count = 0;
		this.file_name = fname ;
		this.open(this.file_name) ;
	}

	public void open(String fname)
	{
		this.file_name = fname ;
		try
		{
			this.buf_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file_name)));
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}

	public int getCount()			{	return this.line_count ;	}
	
	public void print(String[] str)
	{
		String temp = "" ;
		if(str != null)
		{
			for(int i = 0; i < str.length; i++)
			{
				if(str[i] == null)
					temp += "<NULL>" ;
				else
					temp += (str[i].indexOf(",") < 0) ?  str[i] : "\"" + str[i] + "\"" ;
				temp += (i + 1 != str.length) ? "," : "" ;
			}
		}

		this.println(temp) ;
	}

	private void println(String str)
	{
		String temp = str + "\n" ;
		try
		{
			buf_writer.write(temp,0,temp.length()) ;
			buf_writer.flush() ;
			this.line_count++ ;
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}

	public void close()
	{
		try
		{
			if(this.buf_writer != null)
				this.buf_writer.close() ;
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}
}