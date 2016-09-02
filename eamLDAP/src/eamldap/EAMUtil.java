package eamldap;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
	Utilty성 Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMUtil.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMUtil
{
	/**
	*	String을 int으로 변환
	*	@param	str			변환하고자하는 String
	*	@param	null_int	null이나 Null String일때 초기값
	*/
	public static int toInteger(String str, int null_int)
	{
		int ret = null_int ;
		if(str == null || str.length() == 0)
			return null_int ;
		else
		{
			try
        	{
        		ret = Integer.parseInt(str) ;
        	}
        	catch(Exception e)
        	{
        		;
        	}
        	finally
        	{
        	}
        	return ret ;
		}
	}

	/**
	*	String을 int으로 변환
	*	@param	str			변환하고자하는 String
	*/
	public static int toInteger(String str)
	{
		return EAMUtil.toInteger(str,0) ;
	}

	/**
	*	String을 int으로 변환
	*	@param	str			변환하고자하는 String
	*	@param	null_int	null이나 Null String일때 초기값
	*/
	public static long toLong(String str, long null_int)
	{
		if(str == null || str.length() == 0)
			return null_int ;
		else
			return Long.parseLong(str) ;
	}

	/**
	*	String을 int으로 변환
	*	@param	str			변환하고자하는 String
	*/
	public static long toLong(String str)
	{
		return EAMUtil.toLong(str,0) ;
	}

	/**
	*	String을 boolean으로 변환
	*	@param	str			변환하고자하는 String
	*	@param	null_bool	null이나 Null String일때 초기값
	*/
	public static boolean toBoolean(String str, boolean null_bool)
	{
		if(str == null || str.length() == 0)
			return null_bool ;
		else
			return Boolean.valueOf(str).booleanValue() ;
	}

	/**
	*	String을 boolean으로 변환
	*	@param	str			변환하고자하는 String
	*/
	public static boolean toBoolean(String str)
	{
		return EAMUtil.toBoolean(str,false) ;
	}

	/**
	*	String을 String으로 변환(Null이나 Null String에 대한 초기값을 가지고)
	*	@param	str			변환하고자하는 String
	*	@param	null_str	null이나 Null String일때 초기값
	*/
	public static String toString(String str, String null_str)
	{
		if(str == null || str.length() == 0)
			return null_str ;
		else
			return str ;
	}

	/**
	*	String을 String으로 변환(Null이나 Null String에 대한 초기값("")으로 초기화한다.)
	*	@param	str			변환하고자하는 String
	*/
	public static String toString(String str)
	{
		return EAMUtil.toString(str,"") ;
	}

	/**
	*	boolean을 String으로 변환[Upper Case]
	*	@param	bool			변환하고자하는 boolean
	*/
	public static String toString(boolean bool)
	{
		String temp = (bool) ? "TRUE" : "FALSE" ;
		return temp ;
	}

	/**
	*	int을 String으로 변환
	*	@param	i		변환하고자하는 int
	*/
	public static String toString(long i)
	{
		String temp = String.valueOf(i) ;
		return temp ;
	}

	/**
	*	int을 String(고정길이 "0")으로 변환
	*	@param	i		변환하고자하는 int
	*	@param	len		고정길이 만들기 (printf format string의 %10d 처럼)
	*/
	public static String toString(long i, int len)
	{
		String temp = String.valueOf(i) ;
		String ret = temp ;
		for(int ii = len ; ii > temp.length() ; ii--)
			ret = "0" + ret ;
		return ret ;
	}

	/**
	*	int을 String으로 변환
	*	@param	i		변환하고자하는 int
	*/
	public static String toString(int i)
	{
		String temp = String.valueOf(i) ;
		return temp ;
	}

	/**
	*	int을 String(고정길이 "0")으로 변환
	*	@param	i		변환하고자하는 int
	*	@param	len		고정길이 만들기 (printf format string의 %10d 처럼)
	*/
	public static String toString(int i, int len)
	{
		String temp = String.valueOf(i) ;
		String ret = temp ;
		for(int ii = len ; ii > temp.length() ; ii--)
			ret = "0" + ret ;
		return ret ;
	}

	/**
	*	String의 주어진 문자들에 대하여 Trim(좌,우,중간 모두)한다.
	*	@param	str		Trim하고자 하는 String
	*	@param	trim	Trim하려는 문자들
	*/
	public static String trimCenter(String str, String trim)
	{
		String temp = str.trim() ;
		String rtmp = "" ;

		for(int i = 0; i < temp.length(); i++)
		{
			if(trim.indexOf(temp.charAt(i)) < 0)	
				rtmp = rtmp + temp.charAt(i) ;
		}

		return rtmp ;
	}

	/**
	*	한글특수문자중 Ascii로 표현할 수 있는(KeyBoard상) 문자들을 Ascii한 Byte로 변환한다.
	*/
	public static String toOneByte(String str)
	{
		byte[] btmp = str.getBytes() ;
		byte[] rtmp = new byte[btmp.length];
		int j = 0 ;
		for(int i = 0;i < btmp.length;i++)
		{
			if(( (btmp[i] & (byte)(0x80)) == (byte)(0x80) ) && i + 1 < btmp.length)
			{
				if(btmp[i] == (byte)(0xa1) && btmp[i+1] == (byte)(0xa1))
				{
					rtmp[j++] = (byte)(0x20) ; 
					i++;					
				}
				else if(btmp[i] == (byte)(0xA3) && (btmp[i+1] & (byte)(0x80)) == (byte)(0x80))	
				{
					rtmp[j++] = (byte)(0x7f & btmp[i+1]) ;
					i++ ;
				}
				else
				{
					rtmp[j++] = btmp[i++] ;
					rtmp[j++] = btmp[i] ;
				}
			}
			else
				rtmp[j++] = btmp[i] ;
		}

		return new String(rtmp,0,j) ;
	}

	/**
	*	한글2Byte Space(0xa1a1)마저도 Trim한다.
	*/
	public static String trimHangul(String str)
	{
		byte[] btmp = str.getBytes() ;
		byte[] rtmp = new byte[btmp.length];
		String temp = "" ;
		
		int j = 0 ;
		for(int i =0; i < btmp.length; i++)
		{			
			if(( (btmp[i] & (byte)(0x80)) == (byte)(0x80) ) && i + 1 < btmp.length)
			{
				if(btmp[i] == (byte)(0xa1) && btmp[i+1] == (byte)(0xa1))
				{
					rtmp[j] = (byte)(0x20) ;
					j++ ;
					i++ ;
				}
				else
				{
					rtmp[j] = btmp[i] ;
					j++ ;
					i++ ;
					rtmp[j] = btmp[i] ;
					j++ ;
					
				}
			}
			else
			{
				if(i + 1 == btmp.length && btmp[i] == (byte)(0xa1))
				{
					rtmp[j] = (byte)(0x20) ;
				}
				else
				{
					rtmp[j] = btmp[i] ;
				}

				j++;
			}
		}
		//System.out.println("") ;

		temp = new String(rtmp,0,j) ;
		//System.out.println("trm[" + temp.trim() + "]") ;
		return temp.trim() ;
	}
	
	/**
	*	String의 좌측편을 주어진 문자들에 대하여 Trim한다.
	*	@param	str		Trim하고자 하는 String
	*	@param	trim	Trim하려는 문자들
	*/
	public static String trimLeft(String str, String trim)
	{
		if(str == null)	return str ;
		int i ;
		for(i=0; i < str.length(); i++)
		{
			if(trim.indexOf(str.charAt(i)) < 0)
				break ;
		}
		return str.substring(i) ;
	}

	/**
	*	String의 우측편을 주어진 문자들에 대하여 Trim한다.
	*	@param	str		Trim하고자 하는 String
	*	@param	trim	Trim하려는 문자들
	*/
	public static String trimRight(String str, String trim)
	{
		if(str == null)	return str ;
		int i ;
		for(i = str.length() - 1; i >= 0 ; i--)
		{
			if(trim.indexOf(str.charAt(i)) < 0)
				break ;
		}
		return str.substring(0,i+1) ;
	}

	/**
	*	String의 양편을 주어진 문자들에 대하여 Trim한다.
	*	@param	str		Trim하고자 하는 String
	*	@param	trim	Trim하려는 문자들
	*/
	public static String trim(String str, String trim)
	{
		return EAMUtil.trimRight(EAMUtil.trimLeft(str,trim),trim) ;
	}

	/**
	*	한글이 포함된 문자열인가?
	*	@param	str		검사할 문자열
	*/
	public static boolean isHangul(String str)
	{
		byte[] s = str.getBytes() ;

		for(int i = 0; i < s.length;i++)
			if(!(0x00 <= s[i] && s[i] <= 0x7e))	return true ;

		return false ;
	}
	
	/**
	*	한글Length
	*	@param	str		문자열
	*/
	public static int getHanLength(String str)
	{
		return str.getBytes().length ;
	}

	/**
	*	String[]을 String으로 변환(Null이나 Null String에 대한 초기값("")으로 초기화한다.)
	*	@param	array			변환하고자하는 String Array
	*	@param	delimiter		구분자
	*	@return	String
	*/
	public static String Array2String(String[] array,String delimiter)
	{
		String temp = "" ;
		if(array == null)	return "" ;

		for(int i = 0; i < array.length; i++)
			temp += (array[i] + delimiter) ;
		
		int size = temp.length() ;
		return (size > 0) ? temp.substring(0,size - delimiter.length()) : "" ;
	}

	/**
	*	String[]을 String으로 변환(Null이나 Null String에 대한 초기값("")으로 초기화한다.)
	*	@param	array		변환하고자하는 String Arry
	*	@return	String
	*/
	public static String[] ArrayReverse(String[] array)
	{
		String[] temp = new String[array.length] ;
		if(array == null)	return null ;

		for(int i = 0; i < array.length; i++)
			temp[i] = array[array.length-i-1] ;
		
		return temp ;
	}

	/**
	*	String을 String으로 변환(Null이나 Null String에 대한 초기값("")으로 초기화한다.)
	*	@param	str			변환하고자하는 String
	*	@return	String
	*/
	public static String[] String2Array(String str,String delimiter, boolean trim)
	{

		if(str == null || delimiter == null || delimiter.length() == 0)	return null ;

		str = EAMUtil.trim(str, delimiter) ;
		int len = 1;
		for(int i=0; i < str.length();i++)
			if(str.substring(i).startsWith(delimiter))
				len++ ;
		
		String[] temp = new String[len] ;

		for(int i =0; i < temp.length; i++)
			temp[i] = "" ;

		for(int i = 0,j = 0; i < str.length() && j < len;)
		{
			if(str.substring(i).startsWith(delimiter))
			{
				i += delimiter.length() ;
				j++ ;
			}
			else
			{
				temp[j] = temp[j] + str.charAt(i) ;
				if(trim)	temp[j] = EAMUtil.trim(temp[j]," ") ;
				i++ ;
			}
		}

		return temp ;
	}
	
	/**
	*	String을 String으로 변환(Null이나 Null String에 대한 초기값("")으로 초기화한다.)
	*	@param	str			변환하고자하는 String
	*	@return	String
	*/
	public static String[] String2Array(String str,String delimiter)
	{
		return EAMUtil.String2Array(str,delimiter,false) ;
	}

	/**
	*	binary가 Text Character로 되어 있는가.
	*	@param	bin		Check할 Binary
	*	@return String으로 변환할 수 있으면 true
	*/
	public static boolean isText( byte[] bin)
	{
		for(int i = 0; i < bin.length; i++)
		{
			if((byte)(bin[i] & (byte)0x7F) <  (byte)0x20)	return false ;
			if((byte)(bin[i] & (byte)0x7F) == (byte)0x7F) 	return false ;
		}
		return true ;
	}

	/**
	*	binary가 Text Character로 되어 있는가.
	*	@param	bin		Check할 Binary
	*	@return String으로 변환할 수 있으면 true
	*/
	public static boolean isTextAscii( byte[] bin)
	{
		for(int i = 0; i < bin.length; i++)
		{
			if((byte)(bin[i] & (byte)0x80) == (byte)0x80) 	continue ;
			if((byte)(bin[i] & (byte)0x7F) <  (byte)0x20)	return false ;
		}
		return true ;
	}

	/**
	*	binary가 Printable Character로 되어 있는가.
	*	@param	bin		Check할 Binary
	*	@return String으로 변환할 수 있으면 true
	*/
	public static boolean isPrintableText( byte[] bin)
	{
		for(int i = 0; i < bin.length; i++)
		{
			if((byte)(bin[i] & (byte)0x7F) == (byte)0x0D)	continue ;
			if((byte)(bin[i] & (byte)0x7F) == (byte)0x0A)	continue ;
			if((byte)(bin[i] & (byte)0x7F) <  (byte)0x20)	return false ;
			if((byte)(bin[i] & (byte)0x7F) == (byte)0x7F) 	return false ;
		}
		return true ;
	}

	/**
	*	해당 문자가 숫자인가.
	*	@param	ch	검사할 문자
	*/
	public static boolean isNumeric(char ch)
	{
		return (Character.isDigit(ch)) ? true : false ;
	}
	
	/**
	*	해당 문자가 영자인가.
	*	@param	ch	검사할 문자
	*/
	public static boolean isAlpha(char ch)
	{
		if(Character.isLowerCase(ch))	return true ;
		if(Character.isUpperCase(ch))	return true ;
		return false ;
	}

	/**
	*	해당 문자가 영숫자인가.
	*	@param	ch	검사할 문자
	*/
	public static boolean isAlphaNumeric(char ch)
	{
		if(Character.isDigit(ch))		return true ;
		if(Character.isLowerCase(ch))	return true ;
		if(Character.isUpperCase(ch))	return true ;
		return false ;
	}

	/**
	*	해당 문자열이 숫자인가.
	*	@param	str	검사할 문자열
	*/
	public static boolean isNumeric(String str)
	{
		if(str == null)	return false ;
		for(int i = 0; i < str.length(); i++)
			if(!Character.isDigit(str.charAt(i)))	return false ;
		return true ;
	}
	
	public static boolean isAllLowerCase(String str)
	{
		if(str == null)	return false ;
		for(int i = 0; i < str.length(); i++)
			if(!(Character.isLowerCase(str.charAt(i))))	return false ;
		return true ;		
	}

	/**
	*	해당 문자열이 영자인가.
	*	@param	str	검사할 문자열
	*/
	public static boolean isAlpha(String str)
	{
		if(str == null)	return false ;
		for(int i = 0; i < str.length(); i++)
			if(!(Character.isLowerCase(str.charAt(i)) || Character.isUpperCase(str.charAt(i))))	return false ;
		return true ;
	}

	/**
	*	해당 문자열이 영숫자인가.
	*	@param	str	검사할 문자열
	*/
	public static boolean isAlphaNumeric(String str)
	{
		if(str == null)	return false ;
		for(int i = 0; i < str.length(); i++)		
			if(!(Character.isLowerCase(str.charAt(i)) || Character.isUpperCase(str.charAt(i)) || Character.isDigit(str.charAt(i))))	return false ;
		return true ;
	}

	/**
	*	해당 문자열이 영자와숫자 혼용하였는가.
	*	@param	str	검사할 문자열
	*/
	public static boolean isAlphaAndNumeric(String str)
	{
		boolean alpha = false ;
		boolean numeric = false ;
		
		if(str == null)	return false ;
		
		for(int i = 0; i < str.length(); i++)
		{
			if(Character.isLowerCase(str.charAt(i)))		alpha = true ;
			else if(Character.isUpperCase(str.charAt(i)))	alpha = true ;
			else if(Character.isDigit(str.charAt(i)))		numeric = true ;
		}
		
		return (alpha && numeric) ;
	}

	/**
	*	해당 문자열이 영자와숫자와 기타문자를 혼용하였는가.
	*	@param	str	검사할 문자열
	*/
	public static boolean isLetter(String str)
	{
		boolean type[] = { false, false, false } ;
		int count = 0 ;
		
		for(int i = 0; i < str.length(); i++)
		{
			if(Character.isLowerCase(str.charAt(i)))		type[0] = true ;
			else if(Character.isUpperCase(str.charAt(i)))	type[0] = true ;
			else if(Character.isDigit(str.charAt(i)))		type[1] = true ;
			else 											type[2] = true ;
		}
		
		for(int i = 0; i < 3; i++)
			if(type[i])	count++ ;
		
		return (count >= 2)	? true : false ;
	}

	/**
	*	Date를 YYYYMMDDHHMMSS형식의 문자열로 변환
	*/
	public static String toYYYYMMDDHHMMSS(Date date)
	{
		return toYYYYMMDDHHMMSS(date.getTime()) ;
	}

	/**
	*	long값를 YYYYMMDDHHMMSS형식의 문자열로 변환
	*/
	public static String toYYYYMMDDHHMMSS(long ldate)
	{
		return toYYYYMMDDHHMMSS(EAMUtil.toString(ldate)) ;
	}

	/**
	*	GeneralizedTime의 date를 YYYYMMDDHHMMSS형식의 문자열로 변환
	*/
	public static String toYYYYMMDDHHMMSS(String date)
	{
		long 	ldate = 0L;
		String	ret = "" ;

		Calendar curr = Calendar.getInstance() ;

		if(date.charAt(date.length() - 1) == 'Z')
		{	
			curr.set(	Integer.parseInt(date.substring(0,4)),		//YYYY
						Integer.parseInt(date.substring(4,6)),		//MM
						Integer.parseInt(date.substring(6,8)),		//DD
						Integer.parseInt(date.substring(8,10)),		//HH
						Integer.parseInt(date.substring(10,12)),	//MM
						Integer.parseInt(date.substring(12,14))		//SS
			) ;

			Date temp = curr.getTime() ;
			ldate = temp.getTime() ;
			ldate = ldate + (long)(9*60*60*1000) ;
			ret = "" ;
		}
		else
		{ 
			ldate = Long.parseLong(date) ;
			if(date.length() <= 10)
				ldate = ldate * (long)(1000);
		}
		
		curr.setTime(new Date(ldate)) ;
		
		ret = ret + curr.get(Calendar.YEAR) ;
		ret += "/" ;
		if(curr.get(Calendar.MONTH) < 10)	ret += "0" ;
		ret = ret + (curr.get(Calendar.MONTH) + 1);
		ret += "/" ;
		if(curr.get(Calendar.DATE) < 10)	ret += "0" ;
		ret = ret + curr.get(Calendar.DATE) ;
		ret += " " ;
		if(curr.get(Calendar.HOUR_OF_DAY) < 10)	ret += "0" ;
		ret = ret + curr.get(Calendar.HOUR_OF_DAY) ;
		ret += ":" ;
		if(curr.get(Calendar.MINUTE) < 10)	ret += "0" ;
		ret = ret + curr.get(Calendar.MINUTE) ;
		ret += ":" ;
		if(curr.get(Calendar.SECOND) < 10)	ret += "0" ;
		ret = ret + curr.get(Calendar.SECOND) ;

		return ret ;
	}

	/**
	*	Date를 YYYYMMDD형식의 문자열로 변환
	*/
	public static String toYYYYMMDD(Date date)
	{
		return toYYYYMMDD(date.getTime()) ;
	}

	/**
	*	long값를 YYYYMMDD형식의 문자열로 변환
	*/
	public static String toYYYYMMDD(long ldate)
	{
		return toYYYYMMDD(EAMUtil.toString(ldate)) ;
	}

	/**
	*	GeneralizedTime의 date를 YYYYMMDD형식의 문자열로 변환
	*/
	public static String toYYYYMMDD(String date)
	{
		long 	ldate = 0L;
		String	ret = "" ;

		Calendar curr = Calendar.getInstance() ;

		if(date.charAt(date.length() - 1) == 'Z')
		{	
			curr.set(	Integer.parseInt(date.substring(0,4)),		//YYYY
						Integer.parseInt(date.substring(4,6)),		//MM
						Integer.parseInt(date.substring(6,8)),		//DD
						Integer.parseInt(date.substring(8,10)),		//HH
						Integer.parseInt(date.substring(10,12)),	//MM
						Integer.parseInt(date.substring(12,14))		//SS
			) ;

			Date temp = curr.getTime() ;
			ldate = temp.getTime() ;
			ldate = ldate + (long)(9*60*60*1000) ;
			ret = "" ;
		}
		else
		{ 
			ldate = Long.parseLong(date) ;
			if(date.length() <= 10)
				ldate = ldate * (long)(1000);
		}
		
		curr.setTime(new Date(ldate)) ;
		
		ret = ret + curr.get(Calendar.YEAR) ;
		if(curr.get(Calendar.MONTH) < 10)	ret += "0" ;
		ret = ret + (curr.get(Calendar.MONTH) + 1);
		if(curr.get(Calendar.DATE) < 10)	ret += "0" ;
		ret = ret + curr.get(Calendar.DATE) ;

		return ret ;
	}

	/**
	* BaseDN을 기준으로 DN의 위치정보를
	* ID값들로 Unix의 FileFullPath처럼 얻는 함수
	* @param Dn		BaseDn아래의 Dn으로 IDPath를 얻고자하는 Dn
	* @param BaseDN	기준 Dn
	* @return IDPath
	*/
	public static String getIDPath(String Dn,String BaseDN)
	{
		String IDPath = "" ;
		String TempDN ;
		int j , k;
		
		for(int i = Dn.length() - BaseDN.length() - 2;i > 0;i--)
		{
			i = Dn.lastIndexOf(',',i)  ;
			TempDN = Dn.substring(i+1) ;
			
			j = TempDN.indexOf('=')  ;
			k = TempDN.indexOf(',')  ;
			IDPath = IDPath + "/" + TempDN.substring(j+1,k) ;
		}
			
		return IDPath ;
	}

	/**
	* str를 len길이 String으로 변환하되 뒤쪽을 Space로 Padding(채운다.)
	*/
	public static String toSpacing(String str,int len)
	{
		if(str == null)	str = "" ;
		for(int i = len - str.length(); i > 0; i--)
			str += " " ;
		return str ;
	}

	public static boolean contains(String[] a, String b)
	{
		if(a == null)	return false ;
		if(b == null)	return false ;
		
		for(int i = 0; i < a.length; i++)	
			if(b.equals(a[i]))
				return true ;
				
		return false ;
	}
	
	/**
	* 문자열 배열간 비교.
	*/
	public static boolean equals(String[] a, String[] b)
	{
		if(a == null && b == null)	return true ;
		if(a == null)				return false ;
		if(b == null)				return false ;
		if(a.length != b.length)	return false ;
		
		for(int i = 0; i < a.length ; i++)
		{
			int j = 0 ;
			for(; j < b.length ; j++)
				if(a[i].equals(b[j]))
					break ;
					
			if(j == b.length)	return false ;
		}
		
		return true ;
	}
	
	/**
	* 문자열 배열간 비교(대소동일).
	*/
	public static boolean equalsIgnoreCase(String[] a, String[] b)
	{
		if(a == null && b == null)	return true ;
		if(a == null)				return false ;
		if(b == null)				return false ;
		if(a.length != b.length)	return false ;
		
		for(int i = 0; i < a.length ; i++)
		{
			int j = 0 ;
			for(; j < b.length ; j++)
				if(a[i].equalsIgnoreCase(b[j]))
					break ;
					
			if(j == b.length)	return false ;
		}
		
		return true ;
	}
	
	/**
	* 문자열 배열간 합병.
	*/
	public static String[] concat(String[] a, String[] b)
	{		 
		if(a == null)	a = new String[0] ;
		if(b == null)	b = new String[0] ;
		
		String[] ret = new String[a.length + b.length];
		for(int i = 0; i < a.length; i++)
			ret[i] = a[i] ;
		for(int i = a.length,j = 0; j < b.length;i++,j++)
			ret[i] = b[j] ;

		return ret ;
	}
	
	/**
	* 문자열 배열간 집합으로 보고 Subtract한다(a - b).
	*/	
	public static String[] subtract(String[] a, String[] b, boolean isIgnoreCase)
	{
		if(a == null)	a = new String[0] ;
		if(b == null)	b = new String[0] ;
		
		Vector temp = new Vector();
		
		for(int i = 0; i < a.length;i++)
		{
			int j = 0 ;
			for(; j < b.length;j++)
			{
				if(isIgnoreCase)
				{
					if(a[i].equalsIgnoreCase(b[j]))
						break ;
				}
				else
				{
					if(a[i].equals(b[j]))
						break ;
				}
			}
			
			if(j == b.length)
				temp.addElement(a[i]) ;
		}
		
		String[] ret = new String[temp.size()] ;
		ret = (String[])temp.toArray(ret) ;
		
		return ret ;
	}

	/**
	* String을 new String(str.getBytes("8859_1"),"EUC_KR"));처리한다.
	*/
	public static String en2ko(String str)
	{
		try {
			if (str != null)
				return (new String(str.getBytes("8859_1"),"EUC_KR"));
			return str;
		} catch (UnsupportedEncodingException e) {
			return "Encoding Error";
		}
	}

	/**
	* String을 new String(str.getBytes("EUC_KR"),"8859_1"));처리한다.
	*/
	public static String ko2en(String str)
	{
		try {
			if (str != null)
				return (new String(str.getBytes("EUC_KR"),"8859_1"));
			return str;
		} catch (UnsupportedEncodingException e) {
			return "Encoding Error";
		}
	}
	
	/**
	* String을 new String(str.getBytes("EUC_KR"),"8859_1"));처리한다.
	*/
	public static String ko2utf(String str)
	{
		try {
			if (str != null)
				return (new String(str.getBytes("EUC_KR"),"UTF-8"));
			return str;
		} catch (UnsupportedEncodingException e) {
			return "Encoding Error";
		}
	}

	/**
	* UTF-8 byte[]를 "EUC_KR"처리한다.
	*/
	public static String utf2ko(byte[] s) 
	{
		try {
			if(s != null)
			{
				String ss  = new String(s,"UTF-8") ;
				return new String((ss.trim()).getBytes("EUC_KR")) ;
			}
			return null ;
		} catch (UnsupportedEncodingException e) {
			return "Encoding Error";
		}
	}

	/**
	* EUC_KR byte[]를 "UTF-8"처리한다.[검증 않됨]
	*/
	public static String ko2utf(byte[] str)
	{
		try {
			if (str != null)
			{
				String ss  = new String(str,"EUC_KR") ;
				return new String((ss.trim()).getBytes("UTF-8")) ;
			}
			return new String() ;
		} catch (UnsupportedEncodingException e) {
			return "Encoding Error";
		}
	}
	
	public static void main (String args []) throws IOException
	{
		return ;
	}
}


