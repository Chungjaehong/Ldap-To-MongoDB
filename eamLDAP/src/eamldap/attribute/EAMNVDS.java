package eamldap.attribute;

import eamldap.attribute.*;
import java.util.*;

/**
	Name-Value(NVDS format String, ssoprofile��)�� ���� Class.<br>
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
	*  ���ǹ��� �� ���ڿ��� �ʱ�ȭ�Ѵ�.
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
	*  ���ǹ��� Name�� Value �� Name-Value���� Delimiter(������)�� �����Ͽ� ���ڿ��� �ʱ�ȭ�Ѵ�.
	*  @param nv_sep : Name�� Value�� ������
	*  @param nv_del : Name-Value���� Delimiter(������)
	*/
	public EAMNVDS (char nv_sep, char nv_del)
	{
		this.nv_sepc = nv_sep ;
		this.nv_delc = nv_del ;
		this.escape	= "%" + nv_sep + nv_del ;
		this.resetQuery();
	}
	
	/**
	 *  ���ǹ��� �־��� ���ڿ��� �ʱ�ȭ�Ѵ�.
	 *
	 * @param query �ʱ�ȭ�� ���ǹ� ���ڿ�
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
	 *  ���ǹ��� �־��� ���ڿ��� �ʱ�ȭ�Ѵ�.
	 *
	 * @param query �ʱ�ȭ�� ���ǹ� ���ڿ�
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
	 *  'Name-Value' ���� ���ǹ��� �߰��Ѵ�.
	 * �߰��� �����Ϳ� ���� ���ڵ��� �ڵ����� ����ȴ�.
	 *
	 *
	 * @param tag �߰��� �±׸�
	 * @param value �߰��� ������
	 */
	public String add(String tag, String value)
	{
		return this.add(tag, value, false) ;
	}

	/**
	*	'Name-Value' ���� ���ǹ��� �߰��Ѵ�.
	*	�߰��� �����Ϳ� ���� ���ڵ��� �ڵ����� ����ȴ�.
	*
	*
	*	@param tag �߰��� �±׸�
	*	@param value �߰��� ������
	*	@param isPrefix ���ʿ� ���� ���ΰ�.
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
	*  ������ 'Name-Value' ���� ã�� �����Ѵ�.
	* �Ű������� �Է¹��� �̸�(tag)�� Name-Value ���� ã�� �Է¹��� ��(updateValue)��
	* �ٲ��ش�. ���� �̸��� �±װ� �������� ��� ��� �ٲ��ְ� �ٲ��� Ƚ���� �����ش�.
	*
	*	@param	tag				ã������ �±׸�
	*	@param	updateValue		�����Ϸ��� ������
	*
	*	@return ������ Ƚ��
	*/
	public int update(String tag, String updateValue)
	{
		return update(tag, updateValue, false) ;
	}

	/**
	*	������ 'Name-Value' ���� ã�� �����Ѵ�.
	*	�Ű������� �Է¹��� �̸�(tag)�� Name-Value ���� ã�� �Է¹��� ��(updateValue)��
	*	�ٲ��ش�. ���� �̸��� �±װ� �������� ��� ��� �ٲ��ְ� �ٲ��� Ƚ���� �����ش�.
	*
	*	@param	tag				ã������ �±׸�
	*	@param	updateValue		�����Ϸ��� ������
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return ������ Ƚ��
	*/
	public int update(String tag, String updateValue, boolean isIgnoreCase)
	{
		int count = 0;

		String name[] = this.getNames() ;		// UnEscape�� ����
		String nv[] = this.getNameValues() ;	// Escape�� ����

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
	*	'Name-Value' ���� ���ǹ����� �����Ѵ�.
	*	������ �����Ͱ� ������ �����ϴ�	��쿡�� ��� �����Ѵ�.
	*
	*	@param	tag				������ �±׸�
	*
	*	@return 0�� �Ѱ��ش�.
	*/
	public int delete (String tag)
	{
			return this.delete(tag,false) ;
	}

	/**
	*	'Name-Value' ���� ���ǹ����� �����Ѵ�.
	*	 ������ �����Ͱ� ������ �����ϴ� ��쿡�� ��� �����Ѵ�.
	*
	*	@param	tag				������ �±׸�
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return 0�� �Ѱ��ش�.
	*/
	public int delete (String tag, boolean isIgnoreCase)
	{
		int count = 0;

		String name[] = this.getNames() ;		// UnEscape�� ����
		String nv[] = this.getNameValues() ;	// Escape�� ����

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
	*  �ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	* ���� ���� ã�Ƴ� �����͸� �Ѱ��ش�.
	*
	* @param tag �˻��� �±׸�
	*
	* @return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String find (String tag)
	{
		return this.search(tag, 0, false);
	}

	/**
	*  �ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	* ���� ���� ã�Ƴ� �����͸� �Ѱ��ش�.
	*
	*	@param	tag				�˻��� �±׸�
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	* @return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String find (String tag, boolean isIgnoreCase)
	{
		return this.search(tag, 0, isIgnoreCase);
	}

	/**
	*  �ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	* ����ڰ� ������ �ε����� �ش��ϴ� �����͸� ã�Ƽ� �Ѱ��ش�.
	*
	* @param tag �˻��� �±׸�
	* @param index �˻��� �������� �ε���
	*
	* @return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String find (String tag, int index)
	{
		return this.search(tag, index, false);
	}

	/**
	* 	�ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	*	����ڰ� ������ �ε����� �ش��ϴ� �����͸� ã�Ƽ� �Ѱ��ش�.
	*
	*	@param	tag				�˻��� �±׸�
	*	@param	index			�˻��� �������� �ε���
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String find (String tag, int index, boolean isIgnoreCase)
	{
		return this.search(tag, index, isIgnoreCase);
	}

	/**
	*	�ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	*	���� ���� ã�Ƴ� �����͸� �Ѱ��ش�.
	*
	*	@param tag �˻��� �±׸�
	*
	*	@return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String search (String tag)
	{
		return this.search(tag, 0, false);
	}

	/**
	*	�ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	*	���� ���� ã�Ƴ� �����͸� �Ѱ��ش�.
	*
	*	@param	tag				�˻��� �±׸�
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String search (String tag, boolean isIgnoreCase)
	{
		return this.search(tag, 0, isIgnoreCase);
	}

	/**
	* 	�ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	*	����ڰ� ������ �ε����� �ش��ϴ� �����͸� ã�Ƽ� �Ѱ��ش�.
	*
	*	@param	tag				�˻��� �±׸�
	*	@param	index			�˻��� �������� �ε���
	*
	*	@return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
	*/
	public String search (String tag, int index)
	{
		return this.search(tag,index, false) ;
	}

	/**
	*	�ش� �±׸��� ���� 'Name-Value' ���� ã�Ƴ��� �� �����͸� �Ѱ��ش�. �� �޼ҵ��
	*	����ڰ� ������ �ε����� �ش��ϴ� �����͸� ã�Ƽ� �Ѱ��ش�.
	*
	*	@param tag �˻��� �±׸�
	*	@param index �˻��� �������� �ε���
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return �˻��� �����͸� �Ѱ��ش�. �˻��� �����ϸ� null�� �Ѱ��ش�.
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
	*	������ 'Name-Value' ������ �ʵ尡 �־����� �� ������ 'Name-Value' �ֿ�
	*	�ش��ϴ� �ε��� ���� �����ش�.
	*
	*	@param	sTag			�˻��� Ű�� �Ǵ� �±��� �±׸�
	*	@param	sValue			�˻��� Ű�� �Ǵ� �±��� ������ ��
	*
	*	@return	ã���� �ϴ� 'Name-Value'���� �ε��� ��. �˻��� �����ϸ� -1�� �Ѱ��ش�.
	*/
	public int getIndex (String sTag, String sValue)
	{
		return this.getIndex(sTag, sValue, false) ;
	}

	/**
	*	������ 'Name-Value' ������ �ʵ尡 �־����� �� ������ 'Name-Value' �ֿ�
	*	�ش��ϴ� �ε��� ���� �����ش�.
	*
	*	@param	sTag			�˻��� Ű�� �Ǵ� �±��� �±׸�
	*	@param	sValue			�˻��� Ű�� �Ǵ� �±��� ������ ��
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return	ã���� �ϴ� 'Name-Value'���� �ε��� ��. �˻��� �����ϸ� -1�� �Ѱ��ش�.
	*/
	public int getIndex (String sTag, String sValue, boolean isIgnoreCase)
	{
		// String Ÿ�� ������ �Է����� �޾� ã�����ϴ� �ܾ ã�Ƴ��ִ� Ŭ�����̴�.
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
	*   ���� ���ǹ��� �Ѱ��ش�. �� ���ǹ��� SSO ������ ���� ��û���� ����Ѵ�.
	*
	*	@return ���� ���ǹ��� �Ѱ��ش�.
	*/
	public String getQuery ()
	{
		return this.query;
	}

	/**
	*	���ǹ��� ������ ���ڿ��� �����Ѵ�. ���⼭�� ������ ���ڿ��� Name-Value ����
	*	�� ���� �˻��Ѵ�. �׷��Ƿ� ���ǹ��� ������� �������� �˻����� ���Ѵٴ� ����
	*	�����ؾ� �Ѵ�.
	*
	*	@param query �����ϰ��� �ϴ� ���ǹ� ������
	*
	*	@return ���� ���ǹ��� �Ѱ��ش�.
	*/
	public String setQuery (String query)
	{
		// ���ǹ� ��ȿ�� �˻縦 ���� ����� ������
		int termCount = 0;
		int operCount = 0;
		String temp = "" ;

		// �Ű������� �Է¹��� ���ǹ��� null�̸� �⺻ �����ڸ� ȣ���ϰ� �����Ѵ�.
		if (query == null)		return this.resetQuery();
		if (query.length() < 3)	return this.resetQuery();	// 2004.07.06 Add zhang


		if(query.charAt(query.length()-1) != this.nv_delc)
			query = query + this.nv_delc ;

		//  "-*"�� Null Name Value���� �����Ѵ�.
		for(int i=0; i < query.length();i++)
		{
			if(query.substring(i).startsWith("" + this.nv_sepc + this.nv_delc) && (i == 0 || query.charAt(i-1) == this.nv_delc))
				i++ ;
			else
				temp = temp + query.substring(i,i+1) ;
		}
		query = temp ;

		// �Ű������� �Է¹��� ���ǹ��� Name-Value ������ ��ȿ�ϰ� �̷�������� �˻��Ѵ�.
		for (int i=0; i < query.length(); i++)
		{
			if (query.charAt(i) == this.nv_sepc)	termCount++;
			if (query.charAt(i) == this.nv_delc)	operCount++;
		}

		// ��ȿ�� �����̸� ���ǹ����� �����Ѵ�.
		if (termCount > 0 && operCount > 0 && termCount == operCount)
			this.query = query;
		else
			resetQuery();

		return this.getQuery() ;
		// ���⼭�� Name-Value ���� �� ���� �˻��Ѵ�. �׷��Ƿ� ���ǹ��� �������
		// �������� �˻����� ���Ѵ�.
	}

	/**
	*	���ǹ��� �� ���ڿ��� �ʱ�ȭ�Ѵ�.
	*
	*	@return ���� ���ǹ��� �Ѱ��ش�.
	*/
	public String resetQuery ()
	{
		query = "";

		return this.getQuery() ;
	}

	/**
	*	���ǹ��� ������ ���ڿ��� �����Ѵ�. ���⼭�� ������ ���ڿ��� Name-Value ����
	*	�� ���� �˻��Ѵ�. �׷��Ƿ� ���ǹ��� ������� �������� �˻����� ���Ѵٴ� ����
	*	�����ؾ� �Ѵ�.
	*
	*	@param query 	�����ϰ��� �ϴ� ���ǹ� ������
	*
	*	@return ���� ���ǹ��� �Ѱ��ش�.
	*/
	public String addQuery(String query)
	{
		return this.addQuery(query, false) ;
	}

	/**
	*	���ǹ��� ������ ���ڿ��� �߰��Ѵ�. ���⼭�� ������ ���ڿ��� Name-Value ����
	*	�� ���� �˻��Ѵ�. �׷��Ƿ� ���ǹ��� ������� �������� �˻����� ���Ѵٴ� ����
	*	�����ؾ� �Ѵ�.
	*
	*	@param query 	�����ϰ��� �ϴ� ���ǹ� ������
	*	@param isPrefix ���ʿ� ���� ���ΰ�.
	*
	*	@return ���� ���ǹ��� �Ѱ��ش�.
	*/
	public String addQuery (String query, boolean isPrefix)
	{
		// ���ǹ� ��ȿ�� �˻縦 ���� ����� ������
		int termCount = 0;
		int operCount = 0;
		String temp = "" ;

		// �Ű������� �Է¹��� ���ǹ��� null�̸� �⺻ �����ڸ� ȣ���ϰ� �����Ѵ�.
		if (query == null)		return this.getQuery();

		if(query.charAt(query.length()-1) != this.nv_delc)
			query = query + this.nv_delc ;

		//  "-*"�� Null Name Value���� �����Ѵ�.
		for(int i=0; i < query.length();i++)
		{
			if(query.substring(i).startsWith(""+this.nv_sepc+this.nv_delc) && (i == 0 || query.charAt(i-1) == this.nv_delc))
				i++ ;
			else
				temp = temp + query.substring(i,i+1) ;
		}
		query = temp ;

		// �Ű������� �Է¹��� ���ǹ��� Name-Value ������ ��ȿ�ϰ� �̷�������� �˻��Ѵ�.
		for (int i=0; i < query.length(); i++) {
			if (query.charAt(i) == this.nv_sepc)	termCount++;
			if (query.charAt(i) == this.nv_delc)	operCount++;
		}

		// ��ȿ�� �����̸� ���ǹ����� �����Ѵ�.
		if (termCount > 0 && operCount > 0 && termCount == operCount)
			this.query = (isPrefix) ? (query + this.query) : (this.query + query) ;


		// ���⼭�� Name-Value ���� �� ���� �˻��Ѵ�. �׷��Ƿ� ���ǹ��� �������
		// �������� �˻����� ���Ѵ�.
		return this.getQuery() ;
	}

	/**
	*	'Name-Value' �ֿ��� Name���� toLowerCase()ó����.
	*
	*	@return	Escape�� ���ڿ�
	*/
	public String toNameLowerCase()
	{
		String[] ret = this.getNameValues() ;		// Escape�� ����
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
	*	'Name-Value' �ֿ��� Name���� toUpperCase()ó����.
	*
	*	@return	Escape�� ���ڿ�
	*/
	public String toNameUpperCase()
	{
		String[] ret = this.getNameValues() ;		// Escape�� ����
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
	*	'%', '-', '*'�� Escape�Ѵ�. ��,'%' --> "%25", '-' --> "%2D", '*' --> '%2A'ó���Ѵ�.
	*	@param	sFrom	Escape�� ���ڿ�
	*	@return	Escape�� ���ڿ�
	*/
	protected String encode( String sFrom )
	{
		if( sFrom==null )	return null;

		for(int i = 0; i < this.escape.length(); i++)
			sFrom=replaceString(sFrom, this.escape.substring(i,i+1), "%" + encodeHex(this.escape.substring(i,i+1)));

		return sFrom;
	}

	/**
	*	'%', '-', '*'�� Unescape�Ѵ�. ��,"%25" --> '%', "%2D" --> '-', '%2A' --> '*'ó���Ѵ�.
	*	@param	sFrom	Unescape�� ���ڿ�
	*	@return	Unescape�� ���ڿ�
	*/
	protected String decode( String sFrom )
	{
		if( sFrom==null ) 	return null;

		for(int i = this.escape.length() - 1; i >= 0; i--)
			sFrom=replaceString(sFrom, "%" + encodeHex(this.escape.substring(i,i+1)), this.escape.substring(i,i+1));

		return sFrom;
	}

	/**
	* sSource���ڿ��� sFrom�� �ش繮�ڿ��� sTo�� �����ϱ�
	*/
	protected static String replaceString( String sSource, String sFrom, String sTo )
	{
		// �Ű����� ���ռ� �˻�
		if( sSource==null || sFrom==null || sFrom.equals("") )
			return null;

		//////////////////////////////////////////////////////////////////////
		StringBuffer sResult = new StringBuffer();	// ��ġ�� ���ڿ��� ������ ����
		int from=0, to;

		while (true)
		{
			// from ��° ���ڿ����� sFrom ���ڿ��� ã�´�.
			to=sSource.indexOf ( sFrom, from );

			// sSource ���� sFrom ���ڿ��� ã����
			if( to >= 0 )
			{
				// sFrom �պκ��� ���ڿ� �κ��� �� ���ۿ� �߰��Ѵ�.
				sResult=sResult.append( sSource.substring(from,to) );

				// ��ġ�� ���ڿ�(sTo)�� �� ���ۿ� �߰��Ѵ�.
				if( sTo!=null )
					sResult=sResult.append( sTo );

				from=to+sFrom.length();
			}
			else
			{
				// ��ġ�� �κ��� �� ������ ���ڿ��� ������ �κ��� �߰��ؼ�
				// ��ü ���ڿ��� �ϼ��ϰ� ������ ����������.
				sResult=sResult.append( sSource.substring( from ));
				break;
			}
		}
		return new String(sResult);
	}

	/**
	*	��� Name-Value���� Return�Ѵ�.
	*	@return	Name-Value�� �迭
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
	*	tag�� �ش��ϴ� Name�� �����ϴ°�.
	*
	*	@return	�ش��ϴ� Name ������ true, ������ false
	*/
	public boolean existName(String tag)
	{
		return this.existName(tag, false) ;
	}

	/**
	*	tag�� �ش��ϴ� Name�� �����ϴ°�.
	*
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return	�ش��ϴ� Name ������ true, ������ false
	*/
	public boolean existName(String tag, boolean isIgnoreCase)
	{
		String[] name = this.getNames() ;		// UnEscape�� ����
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
	* 	'Name-Value' �ֿ��� Name���� �迭�� �ޱ�
	*
	*	@return Name�迭
	*/
	public String[] getNames()
	{
		String[] ret = this.getNameValues() ;		// Escape�� ����

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
	*	'Name-Value' �ֿ��� Name���� �ߺ��Ǵ� �� �����Ͽ� �迭�� �ޱ�
	*
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return Name�迭
	*/
	public String[] getUniqueNames(boolean isIgnoreCase)
	{
		String[] temp = this.getNames() ;		// UnEscape�� ����
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
	*  'Name-Value' �ֿ��� Name���� �ߺ��Ǵ� �� �����Ͽ� �迭�� �ޱ�
	*
	*	@return Name�迭
	*/
	public String[] getUniqueNames()
	{
		return this.getUniqueNames(false) ;
	}

	/**
	*  'Name-Value' �ֿ��� ������ Name�� Value���� �迭�� �ޱ�
	*
	*	@return Value�迭
	*/
	public String[] getValues(String name)
	{
		return this.getValues(name, false) ;
	}


	/**
	*  'Name-Value' �ֿ��� ������ Name�� Value���� �迭�� �ޱ�
	*
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return Value�迭
	*/
	public String[] getValues(String name, boolean isIgnoreCase)
	{
		String[] names= this.getNames() ;		// UnEscape�� ����
		String[] value= this.getValues() ;		// UnEscape�� ����
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
	*  'Name-Value' �ֿ��� Value���� �迭�� �ޱ�
	*
	*
	* @return Value�迭
	*/
	public String[] getValues()
	{
		String[] ret = this.getNameValues() ;		// Escape�� ����
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
	*  'Name-Value' ���� ������ Return�Ѵ�.
	*
	*
	* @return 'Name-Value'�� ����
	*/
	public int size ()
	{
		int count = 0 ;
		for(int i = 0;i < this.query.length();i++)
			if(this.query.charAt(i) == this.nv_sepc)	count++ ;
		return count ;
	}

	/**
	*	�� EAMNVDS���� ������ EAMNVDS String�� <b>�߰�����</b>�Ͽ� Query�� ��´�.
	*
	*	@param	nvds			�߰������� EAMNVDS String
	*
	*	@return Update���� Query
	*/
	public String update(String nvds)
	{
		EAMNVDS nv = new EAMNVDS(nvds) ;
		nv = this.update(nv, false) ;
		return nv.getQuery() ;
	}

	/**
	*  �� EAMNVDS���� ������ EAMNVDS String�� <b>�߰�����</b>�Ͽ� Query�� ��´�.
	*
	*	@param	nvds			�߰������� EAMNVDS String
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return Update���� Query
	*/
	public String update(String nvds, boolean isIgnoreCase)
	{
		EAMNVDS nv = new EAMNVDS(nvds) ;
		nv = this.update(nv, isIgnoreCase) ;
		return nv.getQuery() ;
	}

	/**
	*  �� EAMNVDS���� ������ EAMNVDS String�� <b>�߰�����</b>�� ��ü�� ��´�.
	*
	*
	*	@param	nvds			�߰� �Ǵ� ������ EAMNVDS��ü
	*
	*	@return Update�� ��ü
	*/
	public EAMNVDS update(EAMNVDS nvds)
	{
		return this.update(nvds, false) ;
	}

	/**
	*  �� EAMNVDS���� ������ EAMNVDS String�� <b>�߰�����</b>�� ��ü�� ��´�.
	*
	*
	*	@param	nvds			�߰� �Ǵ� ������ EAMNVDS��ü
	*	@param	isIgnoreCase	Name�� ��ҵ���(true)�ΰ�
	*
	*	@return Update�� ��ü
	*/
	public EAMNVDS update(EAMNVDS nvds, boolean isIgnoreCase)
	{
		String thisname[] = this.getNames() ;		// UnEscape�� ����
		String nvdsname[] = nvds.getNames() ;		// UnEscape�� ����
		String nvdsvalue[] = nvds.getValues() ;		// UnEscape�� ����
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
	*  �� EAMNVDS�� ���Ѵ�.
	*/
	public boolean equals(EAMNVDS nvds)
	{
		String this_nv[] = this.getNameValues() ;	// Escape�� ����
		String nvds_nv[] = nvds.getNameValues() ;	// Escape�� ����

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
		Name-Value�ֿ��� MultiValue������ ù��° ������ ���Ͽ� Properties��ü�� Return�Ѵ�.
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
