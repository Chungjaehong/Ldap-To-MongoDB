package eamldap.attribute;

import eamldap.attribute.*;
import java.util.*;

/**
	Name-Value(NVDS format String, ssoprofile값)에 관한 Class.<br>
<br>
	Copyright (C) 2000-2005 <a href='http://www.softforum.com'>Softforum</a> Corp.<br>
<br>
	Module		:	eamldap API Module<br>
	FileName	:	EAMNVDS.java<br>
	Author		:	zhang sung hoon (<a href='mailto:zhang@softforum.com'>zhang@softforum.com</a>)<br>
	Platform	:	UNIX<br>
	Compiler	:	javac<br>
	History		:	20050406 Creation.<br>
	TAB Length	: 	4 Byte.<br>
*/
public class EAMNVDS
{
	///////////////////////////////////////////////////////////////////////////
	private String query;
	private char   nv_sepc, nv_delc;
	private String escape = "" ;

	///////////////////////////////////////////////////////////////////////////
	/**
	*  질의문을 빈 문자열로 초기화한다.
	*/
	public EAMNVDS ()
	{
		this.nv_sepc = '-' ;
		this.nv_delc = '*' ;
		this.escape	= "%-*" ;
		this.resetQuery();
	}

	public EAMNVDS (char nv_sep, char nv_del, String escape)
	{
		this(nv_sep, nv_del) ;
		this.escape = this.escape + escape ;
	}

	/**
	*  질의문을 Name과 Value 및 Name-Value쌍의 Delimiter(구분자)를 지정하여 문자열로 초기화한다.
	*  @param nv_sep : Name과 Value의 구분자
	*  @param nv_del : Name-Value쌍의 Delimiter(구분자)
	*/
	public EAMNVDS (char nv_sep, char nv_del)
	{
		this.nv_sepc = nv_sep ;
		this.nv_delc = nv_del ;
		this.escape	= "%" + nv_sep + nv_del ;
		this.resetQuery();
	}
	
	/**
	 *  질의문을 주어진 문자열로 초기화한다.
	 *
	 * @param query 초기화할 질의문 문자열
	 */
	public EAMNVDS (String query)
	{
		this.nv_sepc = '-' ;
		this.nv_delc = '*' ;
		this.escape	= "%-*" ;
		this.setQuery(query);
	}

	public EAMNVDS (String query, String escape)
	{
		this(query) ;
		this.escape = this.escape + escape ;
	}

	/**
	 *  질의문을 주어진 문자열로 초기화한다.
	 *
	 * @param query 초기화할 질의문 문자열
	 */
	public EAMNVDS (String query, char nv_sep, char nv_del)
	{
		this.nv_sepc = nv_sep ;
		this.nv_delc = nv_del ;
		this.escape	= "%" + nv_sep + nv_del ;
		this.setQuery(query);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 *  'Name-Value' 쌍을 질의문에 추가한다.
	 * 추가할 데이터에 대한 인코딩은 자동으로 수행된다.
	 *
	 *
	 * @param tag 추가할 태그명
	 * @param value 추가할 데이터
	 */
	public String add(String tag, String value)
	{
		return this.add(tag, value, false) ;
	}

	/**
	*	'Name-Value' 쌍을 질의문에 추가한다.
	*	추가할 데이터에 대한 인코딩은 자동으로 수행된다.
	*
	*
	*	@param tag 추가할 태그명
	*	@param value 추가할 데이터
	*	@param isPrefix 앞쪽에 넣을 것인가.
	*/
	public String add(String tag, String value, boolean isPrefix)
	{
		if(tag == null || tag == "")	return this.getQuery() ;
		if(value == null)	value = "" ;

		StringBuffer temp = new StringBuffer() ;
		if(!isPrefix)	temp.append(this.query);
		temp.append(this.encode(tag)) ;
		temp.append(this.nv_sepc) ;
		temp.append(this.encode(value)) ;
		temp.append(this.nv_delc) ;
		if(isPrefix)	temp.append(this.query);

		this.query = new String(temp) ;

		return this.getQuery() ;
	}

	/**
	*  적절한 'Name-Value' 쌍을 찾아 갱신한다.
	* 매개변수로 입력받은 이름(tag)의 Name-Value 쌍을 찾아 입력받은 값(updateValue)로
	* 바꿔준다. 같은 이름의 태그가 여러개일 경우 모두 바꿔주고 바꿔준 횟수를 돌려준다.
	*
	*	@param	tag				찾으려는 태그명
	*	@param	updateValue		변경하려는 데이터
	*
	*	@return 변경한 횟수
	*/
	public int update(String tag, String updateValue)
	{
		return update(tag, updateValue, false) ;
	}

	/**
	*	적절한 'Name-Value' 쌍을 찾아 갱신한다.
	*	매개변수로 입력받은 이름(tag)의 Name-Value 쌍을 찾아 입력받은 값(updateValue)로
	*	바꿔준다. 같은 이름의 태그가 여러개일 경우 모두 바꿔주고 바꿔준 횟수를 돌려준다.
	*
	*	@param	tag				찾으려는 태그명
	*	@param	updateValue		변경하려는 데이터
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return 변경한 횟수
	*/
	public int update(String tag, String updateValue, boolean isIgnoreCase)
	{
		int count = 0;

		String name[] = this.getNames() ;		// UnEscape된 상태
		String nv[] = this.getNameValues() ;	// Escape된 상태

		String temp = "" ;

		if(isIgnoreCase)
		{
			for(int i = 0;i < name.length;i++)
			{
				if(name[i].equalsIgnoreCase(tag))
				{
					count++ ;
					temp += (this.encode(tag) + this.nv_sepc + this.encode(updateValue) + this.nv_delc);
				}
				else
					temp += nv[i] + this.nv_delc ;
			}
		}
		else
		{
			for(int i = 0;i < name.length;i++)
			{
				if(name[i].equals(tag))
				{
					count++ ;
					temp += (this.encode(tag) + this.nv_sepc + this.encode(updateValue) + this.nv_delc);
				}
				else
					temp += nv[i] + this.nv_delc ;
			}
		}

		this.query = temp ;

		return count ;
	}

	/**
	*	'Name-Value' 쌍을 질의문에서 삭제한다.
	*	삭제할 데이터가 복수로 존재하는	경우에는 모두 삭제한다.
	*
	*	@param	tag				삭제할 태그명
	*
	*	@return 0을 넘겨준다.
	*/
	public int delete (String tag)
	{
			return this.delete(tag,false) ;
	}

	/**
	*	'Name-Value' 쌍을 질의문에서 삭제한다.
	*	 삭제할 데이터가 복수로 존재하는 경우에는 모두 삭제한다.
	*
	*	@param	tag				삭제할 태그명
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return 0을 넘겨준다.
	*/
	public int delete (String tag, boolean isIgnoreCase)
	{
		int count = 0;

		String name[] = this.getNames() ;		// UnEscape된 상태
		String nv[] = this.getNameValues() ;	// Escape된 상태

		String temp = "" ;

		if(isIgnoreCase)
		{
			for(int i = 0;i < name.length;i++)
			{
				if(name[i].equalsIgnoreCase(tag))	count++ ;
				else								temp += nv[i] + this.nv_delc ;
			}
		}
		else
		{
			for(int i = 0;i < name.length;i++)
			{
				if(name[i].equals(tag))				count++ ;
				else								temp += nv[i] + this.nv_delc ;
			}
		}

		this.query = temp ;

		return count ;
	}

	/**
	*  해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	* 가장 먼저 찾아낸 데이터를 넘겨준다.
	*
	* @param tag 검색할 태그명
	*
	* @return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String find (String tag)
	{
		return this.search(tag, 0, false);
	}

	/**
	*  해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	* 가장 먼저 찾아낸 데이터를 넘겨준다.
	*
	*	@param	tag				검색할 태그명
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	* @return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String find (String tag, boolean isIgnoreCase)
	{
		return this.search(tag, 0, isIgnoreCase);
	}

	/**
	*  해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	* 사용자가 지정한 인덱스에 해당하는 데이터를 찾아서 넘겨준다.
	*
	* @param tag 검색할 태그명
	* @param index 검색할 데이터의 인덱스
	*
	* @return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String find (String tag, int index)
	{
		return this.search(tag, index, false);
	}

	/**
	* 	해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	*	사용자가 지정한 인덱스에 해당하는 데이터를 찾아서 넘겨준다.
	*
	*	@param	tag				검색할 태그명
	*	@param	index			검색할 데이터의 인덱스
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String find (String tag, int index, boolean isIgnoreCase)
	{
		return this.search(tag, index, isIgnoreCase);
	}

	/**
	*	해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	*	가장 먼저 찾아낸 데이터를 넘겨준다.
	*
	*	@param tag 검색할 태그명
	*
	*	@return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String search (String tag)
	{
		return this.search(tag, 0, false);
	}

	/**
	*	해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	*	가장 먼저 찾아낸 데이터를 넘겨준다.
	*
	*	@param	tag				검색할 태그명
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String search (String tag, boolean isIgnoreCase)
	{
		return this.search(tag, 0, isIgnoreCase);
	}

	/**
	* 	해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	*	사용자가 지정한 인덱스에 해당하는 데이터를 찾아서 넘겨준다.
	*
	*	@param	tag				검색할 태그명
	*	@param	index			검색할 데이터의 인덱스
	*
	*	@return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String search (String tag, int index)
	{
		return this.search(tag,index, false) ;
	}

	/**
	*	해당 태그명을 갖는 'Name-Value' 쌍을 찾아내서 그 데이터를 넘겨준다. 이 메소드는
	*	사용자가 지정한 인덱스에 해당하는 데이터를 찾아서 넘겨준다.
	*
	*	@param tag 검색할 태그명
	*	@param index 검색할 데이터의 인덱스
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return 검색된 데이터를 넘겨준다. 검색에 실패하면 null을 넘겨준다.
	*/
	public String search (String tag, int index, boolean isIgnoreCase)
	{
		if(this.query == null)		return null;
		if(this.query.equals(""))	return null;

		int count = -1;

		String name[] = this.getNames() ;
		String value[] = this.getValues() ;

		if(isIgnoreCase)
		{
			for(int i = 0;i < name.length;i++)
			{
				if(name[i].equalsIgnoreCase(tag))	count++ ;
				if(count == index)					return value[i] ;
			}
		}
		else
		{
			for(int i = 0;i < name.length;i++)
			{
				if(name[i].equals(tag))				count++ ;
				if(count == index)					return value[i] ;
			}
		}

		return null ;
	}

	/**
	*	복수의 'Name-Value' 데이터 필드가 주어졌을 때 지정된 'Name-Value' 쌍에
	*	해당하는 인덱스 값을 구해준다.
	*
	*	@param	sTag			검색의 키가 되는 태그의 태그명
	*	@param	sValue			검색의 키가 되는 태그의 데이터 값
	*
	*	@return	찾고자 하는 'Name-Value'쌍의 인덱스 값. 검색에 실패하면 -1을 넘겨준다.
	*/
	public int getIndex (String sTag, String sValue)
	{
		return this.getIndex(sTag, sValue, false) ;
	}

	/**
	*	복수의 'Name-Value' 데이터 필드가 주어졌을 때 지정된 'Name-Value' 쌍에
	*	해당하는 인덱스 값을 구해준다.
	*
	*	@param	sTag			검색의 키가 되는 태그의 태그명
	*	@param	sValue			검색의 키가 되는 태그의 데이터 값
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return	찾고자 하는 'Name-Value'쌍의 인덱스 값. 검색에 실패하면 -1을 넘겨준다.
	*/
	public int getIndex (String sTag, String sValue, boolean isIgnoreCase)
	{
		// String 타입 변수를 입력으로 받아 찾고자하는 단어를 찾아내주는 클래스이다.
		String name[] = this.getNames() ;
		String value[] = this.getValues() ;

		if(isIgnoreCase)
		{
			for(int i = 0;i < name.length;i++)
				if(name[i].equalsIgnoreCase(sTag) && value[i].equals(sValue))
					return i ;
		}
		else
		{
			for(int i = 0;i < name.length;i++)
				if(name[i].equals(sTag) && value[i].equals(sValue))
					return i ;
		}

		return -1 ;
	}

	/**
	*   만들어낸 질의문을 넘겨준다. 이 질의문은 SSO 엔진에 대한 요청으로 사용한다.
	*
	*	@return 만들어낸 질의문을 넘겨준다.
	*/
	public String getQuery ()
	{
		return this.query;
	}

	/**
	*	질의문을 제공된 문자열로 설정한다. 여기서는 제공된 문자열의 Name-Value 쌍의
	*	수 만을 검사한다. 그러므로 질의문의 내용상의 문제점은 검사하지 못한다는 점을
	*	유의해야 한다.
	*
	*	@param query 설정하고자 하는 질의문 데이터
	*
	*	@return 만들어낸 질의문을 넘겨준다.
	*/
	public String setQuery (String query)
	{
		// 질의문 유효성 검사를 위해 사용할 변수들
		int termCount = 0;
		int operCount = 0;
		String temp = "" ;

		// 매개변수로 입력받은 질의문이 null이면 기본 생성자를 호출하고 종료한다.
		if (query == null)		return this.resetQuery();
		if (query.length() < 3)	return this.resetQuery();	// 2004.07.06 Add zhang


		if(query.charAt(query.length()-1) != this.nv_delc)
			query = query + this.nv_delc ;

		//  "-*"인 Null Name Value쌍을 제거한다.
		for(int i=0; i < query.length();i++)
		{
			if(query.substring(i).startsWith("" + this.nv_sepc + this.nv_delc) && (i == 0 || query.charAt(i-1) == this.nv_delc))
				i++ ;
			else
				temp = temp + query.substring(i,i+1) ;
		}
		query = temp ;

		// 매개변수로 입력받은 질의문이 Name-Value 쌍으로 유효하게 이루어졌는지 검사한다.
		for (int i=0; i < query.length(); i++)
		{
			if (query.charAt(i) == this.nv_sepc)	termCount++;
			if (query.charAt(i) == this.nv_delc)	operCount++;
		}

		// 유효한 문장이면 질의문으로 설정한다.
		if (termCount > 0 && operCount > 0 && termCount == operCount)
			this.query = query;
		else
			resetQuery();

		return this.getQuery() ;
		// 여기서는 Name-Value 쌍의 수 만을 검사한다. 그러므로 질의문의 내용상의
		// 문제점은 검사하지 못한다.
	}

	/**
	*	질의문에 빈 문자열로 초기화한다.
	*
	*	@return 만들어낸 질의문을 넘겨준다.
	*/
	public String resetQuery ()
	{
		query = "";

		return this.getQuery() ;
	}

	/**
	*	질의문을 제공된 문자열로 설정한다. 여기서는 제공된 문자열의 Name-Value 쌍의
	*	수 만을 검사한다. 그러므로 질의문의 내용상의 문제점은 검사하지 못한다는 점을
	*	유의해야 한다.
	*
	*	@param query 	설정하고자 하는 질의문 데이터
	*
	*	@return 만들어낸 질의문을 넘겨준다.
	*/
	public String addQuery(String query)
	{
		return this.addQuery(query, false) ;
	}

	/**
	*	질의문을 제공된 문자열를 추가한다. 여기서는 제공된 문자열의 Name-Value 쌍의
	*	수 만을 검사한다. 그러므로 질의문의 내용상의 문제점은 검사하지 못한다는 점을
	*	유의해야 한다.
	*
	*	@param query 	설정하고자 하는 질의문 데이터
	*	@param isPrefix 앞쪽에 넣을 것인가.
	*
	*	@return 만들어낸 질의문을 넘겨준다.
	*/
	public String addQuery (String query, boolean isPrefix)
	{
		// 질의문 유효성 검사를 위해 사용할 변수들
		int termCount = 0;
		int operCount = 0;
		String temp = "" ;

		// 매개변수로 입력받은 질의문이 null이면 기본 생성자를 호출하고 종료한다.
		if (query == null)		return this.getQuery();

		if(query.charAt(query.length()-1) != this.nv_delc)
			query = query + this.nv_delc ;

		//  "-*"인 Null Name Value쌍을 제거한다.
		for(int i=0; i < query.length();i++)
		{
			if(query.substring(i).startsWith(""+this.nv_sepc+this.nv_delc) && (i == 0 || query.charAt(i-1) == this.nv_delc))
				i++ ;
			else
				temp = temp + query.substring(i,i+1) ;
		}
		query = temp ;

		// 매개변수로 입력받은 질의문이 Name-Value 쌍으로 유효하게 이루어졌는지 검사한다.
		for (int i=0; i < query.length(); i++) {
			if (query.charAt(i) == this.nv_sepc)	termCount++;
			if (query.charAt(i) == this.nv_delc)	operCount++;
		}

		// 유효한 문장이면 질의문으로 설정한다.
		if (termCount > 0 && operCount > 0 && termCount == operCount)
			this.query = (isPrefix) ? (query + this.query) : (this.query + query) ;


		// 여기서는 Name-Value 쌍의 수 만을 검사한다. 그러므로 질의문의 내용상의
		// 문제점은 검사하지 못한다.
		return this.getQuery() ;
	}

	/**
	*	'Name-Value' 쌍에서 Name들을 toLowerCase()처리함.
	*
	*	@return	Escape한 문자열
	*/
	public String toNameLowerCase()
	{
		String[] ret = this.getNameValues() ;		// Escape된 상태
		String temp = "" ;

		for(int i = 0,j = -1; i < ret.length;i++)
		{
			j = ret[i].indexOf(this.nv_sepc) ;
			if(j < 0)	{	temp = "" ;		break ; 	}
			temp = temp + ret[i].substring(0,j).toLowerCase() + this.nv_sepc + ret[i].substring(j+1) + this.nv_delc;
		}

		if(temp.length() > 0)	this.query = temp ;

		return this.getQuery() ;
	}

	/**
	*	'Name-Value' 쌍에서 Name들을 toUpperCase()처리함.
	*
	*	@return	Escape한 문자열
	*/
	public String toNameUpperCase()
	{
		String[] ret = this.getNameValues() ;		// Escape된 상태
		String temp = "" ;

		for(int i = 0,j = -1; i < ret.length;i++)
		{
			j = ret[i].indexOf(this.nv_sepc) ;
			if(j < 0)	{	temp = "" ;		break ; 	}
			temp = temp + ret[i].substring(0,j).toUpperCase() + this.nv_sepc + ret[i].substring(j+1) + this.nv_delc;
		}

		if(temp.length() > 0)	this.query = temp ;

		return this.getQuery() ;
	}

	/**
	*	'%', '-', '*'를 Escape한다. 즉,'%' --> "%25", '-' --> "%2D", '*' --> '%2A'처리한다.
	*	@param	sFrom	Escape할 문자열
	*	@return	Escape한 문자열
	*/
	protected String encode( String sFrom )
	{
		if( sFrom==null )	return null;

		for(int i = 0; i < this.escape.length(); i++)
			sFrom=replaceString(sFrom, this.escape.substring(i,i+1), "%" + encodeHex(this.escape.substring(i,i+1)));

		return sFrom;
	}

	/**
	*	'%', '-', '*'로 Unescape한다. 즉,"%25" --> '%', "%2D" --> '-', '%2A' --> '*'처리한다.
	*	@param	sFrom	Unescape할 문자열
	*	@return	Unescape한 문자열
	*/
	protected String decode( String sFrom )
	{
		if( sFrom==null ) 	return null;

		for(int i = this.escape.length() - 1; i >= 0; i--)
			sFrom=replaceString(sFrom, "%" + encodeHex(this.escape.substring(i,i+1)), this.escape.substring(i,i+1));

		return sFrom;
	}

	/**
	* sSource문자열중 sFrom에 해당문자열을 sTo로 변경하기
	*/
	protected static String replaceString( String sSource, String sFrom, String sTo )
	{
		// 매개변수 적합성 검사
		if( sSource==null || sFrom==null || sFrom.equals("") )
			return null;

		//////////////////////////////////////////////////////////////////////
		StringBuffer sResult = new StringBuffer();	// 대치된 문자열을 저장할 버퍼
		int from=0, to;

		while (true)
		{
			// from 번째 문자열부터 sFrom 문자열을 찾는다.
			to=sSource.indexOf ( sFrom, from );

			// sSource 에서 sFrom 문자열을 찾으면
			if( to >= 0 )
			{
				// sFrom 앞부분의 문자열 부분을 새 버퍼에 추가한다.
				sResult=sResult.append( sSource.substring(from,to) );

				// 대치될 문자열(sTo)을 새 버퍼에 추가한다.
				if( sTo!=null )
					sResult=sResult.append( sTo );

				from=to+sFrom.length();
			}
			else
			{
				// 대치될 부분이 더 없으면 문자열의 나머지 부분을 추가해서
				// 전체 문자열을 완성하고 루프를 빠져나간다.
				sResult=sResult.append( sSource.substring( from ));
				break;
			}
		}
		return new String(sResult);
	}

	/**
	*	모든 Name-Value쌍을 Return한다.
	*	@return	Name-Value쌍 배열
	*/
	private String[] getNameValues()
	{
		int size = this.size() ;
		String[] ret = new String[size] ;
		int count = 0 ;
		//System.out.println("query:"+this.query) ;
		StringTokenizer st = new StringTokenizer(this.query, "" + this.nv_delc);
		while (true)
		{
			if (st.hasMoreTokens())
			{
				ret[count++] = st.nextToken();
				//System.out.println("getNameValues["+(count-1)+"]:" + ret[count-1]) ;
			}
			else
				return ret;
		}
	}

	/**
	*	tag에 해당하는 Name이 존재하는가.
	*
	*	@return	해당하는 Name 있으면 true, 없으면 false
	*/
	public boolean existName(String tag)
	{
		return this.existName(tag, false) ;
	}

	/**
	*	tag에 해당하는 Name이 존재하는가.
	*
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return	해당하는 Name 있으면 true, 없으면 false
	*/
	public boolean existName(String tag, boolean isIgnoreCase)
	{
		String[] name = this.getNames() ;		// UnEscape된 상태
		if(isIgnoreCase)
		{
			for(int i = 0; i < name.length;i++)
				if(tag.equalsIgnoreCase(name[i]))	return true ;
		}
		else
		{
			for(int i = 0; i < name.length;i++)
				if(tag.equals(name[i]))				return true ;
		}

		return false ;
	}

	/**
	* 	'Name-Value' 쌍에서 Name들을 배열로 받기
	*
	*	@return Name배열
	*/
	public String[] getNames()
	{
		String[] ret = this.getNameValues() ;		// Escape된 상태

		String temp = "";
		for(int i = 0; i < ret.length;i++)
		{
			temp = ret[i] ;
			//System.out.println("temp:"+temp) ;
			int j = temp.indexOf(this.nv_sepc) ;
			ret[i] = this.decode(temp.substring(0,j)) ;
			//System.out.println("getNames["+i+"]:" + ret[i]) ;
		}

		return ret ;
	}

	/**
	*	'Name-Value' 쌍에서 Name들을 중복되는 것 제외하여 배열로 받기
	*
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return Name배열
	*/
	public String[] getUniqueNames(boolean isIgnoreCase)
	{
		String[] temp = this.getNames() ;		// UnEscape된 상태
		int[] count = new int[temp.length] ;

		if(isIgnoreCase)
		{
			for(int i = 0; i < temp.length;i++)
			for(int j = 0, cnt = 0; j < count.length;j++)
				if(temp[i].equalsIgnoreCase(temp[j]))
					count[j] = cnt++ ;
		}
		else
		{
			for(int i = 0; i < temp.length;i++)
			for(int j = 0, cnt = 0; j < count.length;j++)
				if(temp[i].equals(temp[j]))
					count[j] = cnt++ ;
		}

		int uniqueCount = 0;
		for(int j = 0; j < count.length;j++)
			if(count[j] == 0)	uniqueCount++ ;

		String[] ret = new String[uniqueCount] ;
		for(int i = 0,j = 0; i < ret.length && j < temp.length; j++)
		{
			if(count[j] == 0)
				ret[i++] = temp[j] ;
		}

		return ret ;
	}

	/**
	*  'Name-Value' 쌍에서 Name들을 중복되는 것 제외하여 배열로 받기
	*
	*	@return Name배열
	*/
	public String[] getUniqueNames()
	{
		return this.getUniqueNames(false) ;
	}

	/**
	*  'Name-Value' 쌍에서 동일한 Name의 Value들을 배열로 받기
	*
	*	@return Value배열
	*/
	public String[] getValues(String name)
	{
		return this.getValues(name, false) ;
	}


	/**
	*  'Name-Value' 쌍에서 동일한 Name의 Value들을 배열로 받기
	*
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return Value배열
	*/
	public String[] getValues(String name, boolean isIgnoreCase)
	{
		String[] names= this.getNames() ;		// UnEscape된 상태
		String[] value= this.getValues() ;		// UnEscape된 상태
		int len = 0;
		String[] ret = null ;

		if(isIgnoreCase)
		{
			for(int i = 0; i < value.length;i++)
				if(names[i].equalsIgnoreCase(name))
					len++ ;

			ret = new String[len] ;
			for(int i = 0,j = 0; i < value.length;i++)
				if(names[i].equalsIgnoreCase(name))
					ret[j++] = value[i] ;
		}
		else
		{
			for(int i = 0; i < value.length;i++)
				if(names[i].equals(name))
					len++ ;

			ret = new String[len] ;
			for(int i = 0,j = 0; i < value.length;i++)
				if(names[i].equals(name))
					ret[j++] = value[i] ;
		}

		return ret ;
	}

	/**
	*  'Name-Value' 쌍에서 Value들을 배열로 받기
	*
	*
	* @return Value배열
	*/
	public String[] getValues()
	{
		String[] ret = this.getNameValues() ;		// Escape된 상태
		String temp = "";

		for(int i = 0; i < ret.length;i++)
		{
			temp = ret[i] ;
			int j = temp.indexOf(this.nv_sepc) ;
			ret[i] = this.decode(temp.substring(j+1)) ;
			//System.out.println("getValues["+i+"]:" + ret[i]) ;
		}

		return ret ;
	}

	/**
	*  'Name-Value' 쌍의 갯수를 Return한다.
	*
	*
	* @return 'Name-Value'의 갯수
	*/
	public int size ()
	{
		int count = 0 ;
		for(int i = 0;i < this.query.length();i++)
			if(this.query.charAt(i) == this.nv_sepc)	count++ ;
		return count ;
	}

	/**
	*	현 EAMNVDS에서 변경할 EAMNVDS String을 <b>추가변경</b>하여 Query을 얻는다.
	*
	*	@param	nvds			추가변경할 EAMNVDS String
	*
	*	@return Update한후 Query
	*/
	public String update(String nvds)
	{
		EAMNVDS nv = new EAMNVDS(nvds) ;
		nv = this.update(nv, false) ;
		return nv.getQuery() ;
	}

	/**
	*  현 EAMNVDS에서 변경할 EAMNVDS String을 <b>추가변경</b>하여 Query을 얻는다.
	*
	*	@param	nvds			추가변경할 EAMNVDS String
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return Update한후 Query
	*/
	public String update(String nvds, boolean isIgnoreCase)
	{
		EAMNVDS nv = new EAMNVDS(nvds) ;
		nv = this.update(nv, isIgnoreCase) ;
		return nv.getQuery() ;
	}

	/**
	*  현 EAMNVDS에서 변경할 EAMNVDS String을 <b>추가변경</b>한 객체을 얻는다.
	*
	*
	*	@param	nvds			추가 또는 변경할 EAMNVDS객체
	*
	*	@return Update한 객체
	*/
	public EAMNVDS update(EAMNVDS nvds)
	{
		return this.update(nvds, false) ;
	}

	/**
	*  현 EAMNVDS에서 변경할 EAMNVDS String을 <b>추가변경</b>한 객체을 얻는다.
	*
	*
	*	@param	nvds			추가 또는 변경할 EAMNVDS객체
	*	@param	isIgnoreCase	Name의 대소동일(true)인가
	*
	*	@return Update한 객체
	*/
	public EAMNVDS update(EAMNVDS nvds, boolean isIgnoreCase)
	{
		String thisname[] = this.getNames() ;		// UnEscape된 상태
		String nvdsname[] = nvds.getNames() ;		// UnEscape된 상태
		String nvdsvalue[] = nvds.getValues() ;		// UnEscape된 상태
		boolean found = false ;

		if(isIgnoreCase)
		{
			for(int i = 0; i < nvdsname.length; i++)
			{
				found = false ;
				for(int j = 0 ; j < thisname.length; j++)
				{
					if(nvdsname[i].equalsIgnoreCase(thisname[j]))
					{
						found = true ;
						this.update(nvdsname[i], nvdsvalue[i], isIgnoreCase) ;
					}
				}
				if(!found)	this.add(nvdsname[i], nvdsvalue[i]) ;
			}
		}
		else
		{
			for(int i = 0; i < nvdsname.length; i++)
			{
				found = false ;
				for(int j = 0 ; j < thisname.length; j++)
				{
					if(nvdsname[i].equals(thisname[j]))
					{
						found = true ;
						this.update(nvdsname[i], nvdsvalue[i], isIgnoreCase) ;
					}
				}
				if(!found)	this.add(nvdsname[i], nvdsvalue[i]) ;
			}
		}

		return this ;
	}

	public String toString()	{	return this.getQuery() ;	}

	/**
	*  현 EAMNVDS와 비교한다.
	*/
	public boolean equals(EAMNVDS nvds)
	{
		String this_nv[] = this.getNameValues() ;	// Escape된 상태
		String nvds_nv[] = nvds.getNameValues() ;	// Escape된 상태

		if(this_nv.length != nvds_nv.length)	return false ;
		//System.out.println("this_nv.length:" + this_nv.length) ;
		//System.out.println("nvds_nv.length:" + nvds_nv.length) ;
		int size = this_nv.length ;

		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j <= size; j++)
			{
				if(j == size)						return false ;
				if(this_nv[i].equals(nvds_nv[j]))	break;
			}
		}

		return true ;
	}

	/**
		Name-Value쌍에서 MultiValue일지라도 첫번째 값만을 취하여 Properties객체로 Return한다.
	*/
	public Properties getProperties()
	{
		String[] names = this.getUniqueNames() ;
		Properties ret = new Properties() ;

		for(int i = 0; i < names.length; i++)
			ret.setProperty(names[i], this.find(names[i])) ;

		return ret ;
	}

	private String encodeHex(String a)	{	return new String(this.encodeHex(a.getBytes())) ;	}

	private byte[] encodeHex(byte[] a)
	{
		if(a == null)	return null ;

		int len = a.length;
		byte[] temp	= new byte[a.length*2];
		byte[] HEXA	= "0123456789ABCDEF".getBytes();

		for (int x = 0, y = 0; x < a.length; x++)
		{
			temp[y++] = HEXA[(a[x] >> 4) & 0x0F] ;
			temp[y++] = HEXA[(a[x] >> 0) & 0x0F] ;
		}

		return temp ;
	}
}
