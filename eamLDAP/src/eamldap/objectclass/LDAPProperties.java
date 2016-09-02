package eamldap.objectclass;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;

import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPModificationSet;
import netscape.ldap.LDAPModification;
import eamldap.attribute.*;
import netscape.ldap.util.ByteBuf;
import netscape.ldap.util.MimeBase64Encoder;
import netscape.ldap.util.MimeBase64Decoder;

/**
 * getProperty(String)만들기
 * load에서 특수문자 제거하기
 * list에서 Multi Value 지원하기
 * loadLDIF()만들기
 * listLDIF()만들기
 * @author  zhang@softforum.com
 */
public class LDAPProperties extends Hashtable
{
    protected LDAPProperties defaults;

    public LDAPProperties()							{	this(null);					}
    public LDAPProperties(LDAPProperties defaults)	{	this.defaults = defaults;	}
    
    public synchronized Object setProperty(String key, String value)		{	return put(key, value);	}
    public synchronized Object setProperty(String key, String[] value)		{	return put(key, value);	}
    public synchronized Object setProperty(String key, byte[] value)		{	return put(key, value);	}
    public synchronized Object setProperty(String key, byte[][] value)		{	return put(key, value);	}

    /**
    	LDAPAttributeSet를 Setting하기
    */
    public void set(LDAPAttributeSet las)
    {
    	if(las == null)		return ;

    	for (Enumeration e = las.getAttributes() ; e.hasMoreElements() ;)
		{
			String type			= "SS" ;
			LDAPAttribute attr 	= (LDAPAttribute)e.nextElement() ;

			String name 		= attr.getName().toLowerCase() ;

			if(name.equals("ssopwd"				))	type = "BS" ;
			if(name.equals("ssopwdhistory"		))	type = "BM" ;
			if(name.equals("ssomember"			))	type = "SM" ;
			if(name.equals("uniquemember"		))	type = "SM" ;
			if(name.equals("ssouspathid"		))	type = "SM" ;
			if(name.equals("objectclass"		))	type = "SM" ;
			if(name.equals("ssouacl"			))	type = "SM" ;
			if(name.equals("ssomacl"			))	type = "SM" ;
			if(name.equals("ssoprofileconfig"	))	type = "SM" ;

			if(type.equals("SS"))
			{
				String temp[] = attr.getStringValueArray() ;
				if(temp == null)		this.setProperty(name,"") ;
				if(temp.length == 0)	this.setProperty(name,"") ;
				else					this.setProperty(name,temp[0]) ;
			}
			else if(type.equals("SM"))
			{
				String temp[] = attr.getStringValueArray() ;
				if(temp == null)		this.setProperty(name, new String[0]) ;
				else					this.setProperty(name, temp) ;
			}
			else if(type.equals("BS"))
			{
				byte[][] temp = attr.getByteValueArray() ;
				if(temp == null)		this.setProperty(name,new byte[0]) ;
				if(temp.length == 0)	this.setProperty(name,new byte[0]) ;
				else					this.setProperty(name,temp[0]) ;
			}
			else if(type.equals("BM"))
			{
				byte[][] temp = attr.getByteValueArray() ;
				if(temp == null)		this.setProperty(name,new byte[0][]) ;
				if(temp.length == 0)	this.setProperty(name,new byte[0][]) ;
				else					this.setProperty(name,temp) ;
			}

			if(name.equals("ssoprofile"))	//200505
			{
				EAMNVDS nvds = new EAMNVDS(this.getProperty(name, "")) ;
				String[] n = nvds.getUniqueNames() ;
				for(int i = 0; i < n.length;i++)
					this.setProperty(n[i], nvds.find(n[i])) ;
			}
		}

    	return ;
    }
    
    private static final String keyValueSeparators = "=: \t\r\n\f";
    private static final String strictKeyValueSeparators = "=:";
    private static final String specialSaveChars = "=: \t\r\n\f#!";
    private static final String whiteSpaceChars = " \t\r\n\f";

    public synchronized void load(InputStream inStream) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
		while (true)
		{
            // Get next line
            String line = in.readLine();
            if (line == null)
                return;

            if (line.length() > 0)
            {    
                // Find start of key
                int len = line.length();
                int keyStart;
                for (keyStart=0; keyStart<len; keyStart++)
                    if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1)
                        break;

                // Blank lines are ignored
                if (keyStart == len)
                    continue;

                // Continue lines that end in slashes if they are not comments
                char firstChar = line.charAt(keyStart);
                if ((firstChar != '#') && (firstChar != '!'))
                {
                    while (continueLine(line))
                    {
                        String nextLine = in.readLine();
                        if (nextLine == null)
                            nextLine = "";
                        String loppedLine = line.substring(0, len-1);
                        // Advance beyond whitespace on new line
                        int startIndex;
                        for (startIndex=0; startIndex<nextLine.length(); startIndex++)
                            if (whiteSpaceChars.indexOf(nextLine.charAt(startIndex)) == -1)
                                break;
                        nextLine = nextLine.substring(startIndex,nextLine.length());
                        line = new String(loppedLine+nextLine);
                        len = line.length();
                    }

                    // Find separation between key and value
                    int separatorIndex;
                    for (separatorIndex=keyStart; separatorIndex<len; separatorIndex++)
                    {
                        char currentChar = line.charAt(separatorIndex);
                        if (currentChar == '\\')
                            separatorIndex++;
                        else if (keyValueSeparators.indexOf(currentChar) != -1)
                            break;
                    }

                    // Skip over whitespace after key if any
                    int valueIndex;
                    for (valueIndex=separatorIndex; valueIndex<len; valueIndex++)
                        if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                            break;

                    // Skip over one non whitespace key value separators if any
                    if (valueIndex < len)
                        if (strictKeyValueSeparators.indexOf(line.charAt(valueIndex)) != -1)
                            valueIndex++;

                    // Skip over white space after other separators if any
                    while (valueIndex < len)
                    {
                        if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1)
                            break;
                        valueIndex++;
                    }
                    String key = line.substring(keyStart, separatorIndex);
                    String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

                    // Convert then store key and value
                    key = loadConvert(key);
                    value = loadConvert(value);
                    put(key, value);
                }
            }
		}
    }

    private boolean continueLine(String line)
    {
        int slashCount = 0;
        int index = line.length() - 1;
        while ((index >= 0) && (line.charAt(index--) == '\\'))
            slashCount++;
        return (slashCount % 2 == 1);
    }

    private String loadConvert(String theString)
    {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x=0; x<len; )
        {
            aChar = theString.charAt(x++);
            if (aChar == '\\')
            {
                aChar = theString.charAt(x++);

				if (aChar == 't') aChar = '\t';
				else if (aChar == 'r') aChar = '\r';
				else if (aChar == 'n') aChar = '\n';
				else if (aChar == 'f') aChar = '\f';
				
				outBuffer.append(aChar);
            }
            else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    private String saveConvert(String theString, boolean escapeSpace)
    {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len*2);

        for(int x=0; x<len; x++)
        {
            char aChar = theString.charAt(x);
			switch(aChar)
			{
				case ' ':
							if (x == 0 || escapeSpace) 
								outBuffer.append('\\');

							outBuffer.append(' ');
							break;
				case '\\':
							outBuffer.append('\\');
							outBuffer.append('\\');
							break;
                case '\t':
                			outBuffer.append('\\');
                			outBuffer.append('t');
							break;
                case '\n':
                			outBuffer.append('\\');
                			outBuffer.append('n');
							break;
                case '\r':
                			outBuffer.append('\\');
                			outBuffer.append('r');
							break;
                case '\f':
							outBuffer.append('\\');
							outBuffer.append('f');
							break;
                default:

                       		if (specialSaveChars.indexOf(aChar) != -1)
                           		outBuffer.append('\\');
                       		outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

    public synchronized void save(OutputStream out, String header) 
    {
        try
        {
            store(out, header);
        }
        catch (IOException e)
        {
        }
    }

    public synchronized void store(OutputStream out, String header) throws IOException
    {
        BufferedWriter awriter;
        awriter = new BufferedWriter(new OutputStreamWriter(out));
        if (header != null)
            writeln(awriter, "#" + header);
        writeln(awriter, "#" + new Date().toString());
        for (Enumeration e = keys(); e.hasMoreElements();)
        {
            String key = (String)e.nextElement();
            String val = (String)get(key);
            key = saveConvert(key, true);

	    /* No need to escape embedded and trailing spaces for value, hence
	     * pass false to flag.
	     */
            val = saveConvert(val, false);
            writeln(awriter, key + "=" + val);
        }
        awriter.flush();
    }

    private static void writeln(BufferedWriter bw, String s) throws IOException
    {
        bw.write(s);
        bw.newLine();
    }

	/**
		
	*/
    //public String getProperty(String key)
    //{
	//	Object oval = super.get(key);
	//	String sval = (oval instanceof String) ? (String)oval : null;
	//	return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    //}

	public boolean isMultiValue(String key)
	{
		Object oval = super.get(key);
		if(oval instanceof byte[][])	return true ;
		if(oval instanceof String[])	return true ;
		return false ;
	}

    public byte[] getProperty(String key, byte[] defaultValue)
    {
    	Object oval = super.get(key);

    	byte[] mbval = (oval instanceof byte[]) ? (byte[])oval : null;
    	if(mbval == null)
    	{
    		byte[][] mmbval = (oval instanceof byte[][]) ? (byte[][])oval : null;
    		if(mmbval == null)			mbval = null ;
    		else if(mmbval.length == 0)	mbval = null ;
    		else						mbval = mmbval[0] ;
    	}
    	
    	mbval = ((mbval == null) && (defaults != null)) ? defaults.getProperty(key, defaultValue) : mbval;
		
		return (mbval == null) ? defaultValue : mbval;
    }
    	
    public byte[][] getProperty(String key, byte[][] defaultValue)
    {
    	Object oval = super.get(key);

    	byte[][] mmbval = (oval instanceof byte[][]) ? (byte[][])oval : null;
    	if(mmbval == null)
    	{
    		byte[] mbval = (oval instanceof byte[]) ? (byte[])oval : null;
    		if(mbval != null)
    		{
    			mmbval = new byte[1][] ;
    			mmbval[0] = mbval ;
    		}
    	}
    	
    	mmbval = ((mmbval == null) && (defaults != null)) ? defaults.getProperty(key, defaultValue) : mmbval;
		
		return (mmbval == null) ? defaultValue : mmbval;
    }

    public String getProperty(String key, String defaultValue)
    {
    	Object oval = super.get(key);
		
		String sval = (oval instanceof String) ? (String)oval : null;
    	if(sval == null)
    	{
    		String[] msval = (oval instanceof String[]) ? (String[])oval : null;
    		if(msval == null)			sval = null ;
    		else if(msval.length == 0)	sval = null ;
    		else						sval = msval[0] ;
    	}
    	
    	sval = ((sval == null) && (defaults != null)) ? defaults.getProperty(key, defaultValue) : sval;
		
		return (sval == null) ? defaultValue : sval;
    }
    
    public String[] getProperty(String key, String[] defaultValue)
    {
    	Object oval = super.get(key);
		
		String[] msval = (oval instanceof String[]) ? (String[])oval : null;
    	if(msval == null)
    	{
    		String sval = (oval instanceof String) ? (String)oval : null;
    		if(sval != null)
    		{
    			msval = new String[1] ;
    			msval[0] = sval ;
    		}	
    	}
    	
    	msval = ((msval == null) && (defaults != null)) ? defaults.getProperty(key, defaultValue) : msval;
		
		return (msval == null) ? defaultValue : msval;
    }

    public Enumeration propertyNames()
    {
		Hashtable h = new Hashtable();
		enumerate(h);
		return h.keys();
    }

	public void listLDIF(PrintWriter out)
	{
		Hashtable h = new Hashtable();
		enumerate(h);
		
		String temp = enumerateLDIF("dn",h.get("dn")) ;
		if(temp != null)	out.print(temp) ;
			
		for (Enumeration e = h.keys() ; e.hasMoreElements() ;)
		{		
			String key = (String)e.nextElement();
			if("dn".equals(key) == false)
			{
				temp = enumerateLDIF(key,h.get(key)) ;
				if(temp != null)	out.print(temp) ;
			}
		}
	}
	
	private String enumerateLDIF(String name, Object oval)
	{
		byte[][] temp = null;
		if(oval instanceof byte[][])
		{
			temp = (byte[][])oval ;
		}
		else if(oval instanceof byte[])
		{
			temp = new byte[1][] ;
			temp[0] = (byte[])oval ;
		}
		else if(oval instanceof String[])
		{
			String[] tmp = (String[])oval ;
			temp = new byte[tmp.length][] ;
			for(int i = 0;i < tmp.length;i++)
				try	{	temp[i] = tmp[i].getBytes("UTF-8") ;	}	catch (Exception e) {}
		}
		else if(oval instanceof String)
		{
			String tmp = (String)oval ;
			temp = new byte[1][] ;
			try	{	temp[0] = tmp.getBytes("UTF-8") ;	}	catch (Exception e) {}
		}
		if(temp == null)	return null ;
		
		StringBuffer ret = new StringBuffer() ;
		for(int i = 0; i < temp.length;i++)
		{
			boolean isbase64 = false ;
			for(int j = 0; j < temp[i].length; j++)
			{
				if((temp[i][j] & 0x80) == 0x80)
				{		
					isbase64 = true ;
					break ;
				}
				if((temp[i][j] & 0x7F) < 0x20)
				{
					isbase64 = true ;
					break ;
				}
			}
			
			
			if(isbase64)
			{
				ByteBuf in = new ByteBuf() ;
				in.append(temp[i]) ;
				ByteBuf ou = new ByteBuf() ;
				MimeBase64Encoder base64 = new MimeBase64Encoder() ;
				base64.translate(in, ou) ;
				
				ret.append(name) ;
				ret.append(":: ") ;
				ret.append(ou.toString()) ;
				ret.append("\n") ;
			}
			else
			{
				ret.append(name) ;	
				ret.append(": ") ;
				ret.append(new String(temp[i])) ;
				ret.append("\n") ;
			}
		}
		return ret.toString() ;
	}
	
	public void listLDIF(PrintStream out)
	{
		Hashtable h = new Hashtable();
		enumerate(h);
		String temp ;
		temp = enumerateLDIF("dn",h.get("dn")) ;
		if(temp != null)	out.print(temp) ;
		temp = enumerateLDIF("objectclass",h.get("objectclass")) ;
		if(temp != null)	out.print(temp) ;
		
			
		for (Enumeration e = h.keys() ; e.hasMoreElements() ;)
		{		
			String key = (String)e.nextElement();
			if("dn".equals(key) == false && "objectclass".equals(key) == false)
			{
				temp = enumerateLDIF(key,h.get(key)) ;
				if(temp != null)	out.print(temp) ;
			}
		}
	}

    private synchronized void enumerate(Hashtable h)
    {
		if (defaults != null)
	    	defaults.enumerate(h);

		for (Enumeration e = keys() ; e.hasMoreElements() ;)
		{
			String key = (String)e.nextElement();
			h.put(key, get(key));
		}
    }

    private static char toHex(int nibble)
    {
		return hexDigit[(nibble & 0xF)];
    }
    private static final char[] hexDigit = { '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };
    
	public static void main (String args []) throws Exception
    {
    	LDAPProperties prop = new LDAPProperties() ;
    	
    	String[] multivalue = { "123", "가나다" } ;
    	String value = "ABC" ;
		byte[][] mmb = new byte[2][2] ;
		mmb[0][0] = 0x30 ;
		mmb[0][1] = 0x31 ;
		mmb[1][0] = 0x31 ;
		mmb[1][1] = 0x32 ;
		prop.setProperty("0", mmb) ;
		prop.setProperty("00", mmb[1]) ;
    	prop.setProperty("1", multivalue) ;
    	prop.setProperty("2", value) ;

		byte[] tp = new byte[0] ;
		tp = prop.getProperty("0", new byte[0]) ;
    	System.out.println("[x/"+tp.length+"]") ;
    	for(int i = 0; i < tp.length;i++)
    		System.out.println("["+i+"]:"+tp[i]) ;
    		
		tp = prop.getProperty("00", new byte[0]) ;
    	System.out.println("[x/"+tp.length+"]") ;
    	for(int i = 0; i < tp.length;i++)
    		System.out.println("["+i+"]:"+tp[i]) ;
    		
		tp = prop.getProperty("1", new byte[0]) ;
    	System.out.println("[x/"+tp.length+"]") ;
    	for(int i = 0; i < tp.length;i++)
    		System.out.println("["+i+"]:"+tp[i]) ;
    		
		tp = prop.getProperty("2", new byte[0]) ;
    	System.out.println("[x/"+tp.length+"]") ;
    	for(int i = 0; i < tp.length;i++)
    		System.out.println("["+i+"]:"+tp[i]) ;
    		
		System.out.println("--------------------") ;
    		    	
    	System.out.println("[]"+prop.getProperty("0","")) ;
    	System.out.println("[]"+prop.getProperty("00","")) ;
    	System.out.println("[]"+prop.getProperty("1","")) ;
    	System.out.println("[]"+prop.getProperty("2","")) ;
    	System.out.println("--------------------") ;
    	
		byte[][] ttp = new byte[0][] ;
		ttp = prop.getProperty("0", new byte[0][]) ;
    	System.out.println("[x/"+ttp.length+"]") ;
    	for(int i = 0; i < ttp.length;i++)
    		System.out.println("["+i+"]:"+ttp[i][i]) ;

		ttp = prop.getProperty("00", new byte[0][]) ;
    	System.out.println("[x/"+ttp.length+"]") ;
    	for(int i = 0; i < ttp.length;i++)
    		System.out.println("["+i+"]:"+ttp[i][i]) ;
    		    		
		ttp = prop.getProperty("1", new byte[0][]) ;
    	System.out.println("[x/"+ttp.length+"]") ;
    	for(int i = 0; i < ttp.length;i++)
    		System.out.println("["+i+"]:"+ttp[i][i]) ;
    		
		ttp = prop.getProperty("2", new byte[0][]) ;
    	System.out.println("[x/"+ttp.length+"]") ;
    	for(int i = 0; i < ttp.length;i++)
    		System.out.println("["+i+"]:"+ttp[i][i]) ;
    	
    	String[] tmp = new String[0] ;
		System.out.println("--------------------") ;
    	tmp = prop.getProperty("0", new String[0]) ;
    	System.out.println("[x/"+tmp.length+"]") ;
    	for(int i = 0; i < tmp.length;i++)
    		System.out.println("["+i+"]:"+tmp[i]) ;

    	tmp = prop.getProperty("00", new String[0]) ;
    	System.out.println("[x/"+tmp.length+"]") ;
    	for(int i = 0; i < tmp.length;i++)
    		System.out.println("["+i+"]:"+tmp[i]) ;
    		    		    		    	
    	tmp = prop.getProperty("1", new String[0]) ;
    	System.out.println("[x/"+tmp.length+"]") ;
    	for(int i = 0; i < tmp.length;i++)
    		System.out.println("["+i+"]:"+tmp[i]) ;
    		
    	tmp = prop.getProperty("2", new String[0]) ;
    	System.out.println("[x/"+tmp.length+"]") ;
    	for(int i = 0; i < tmp.length;i++)
    		System.out.println("["+i+"]:"+tmp[i]) ;
    }
}

