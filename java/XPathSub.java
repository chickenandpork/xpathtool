package org.smallfoot.xpath;

import org.smallfoot.xpath.XPathTool;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import gnu.getopt.Getopt;

/**
 * A driving class to interpret commandline arguments and pass them to the underlying XPathTool
 */
public class XPathSub {

    private int replacements = 0;
 
    /**
     * Class Constructor, uncomplicated
     */
    public XPathSub() { }

    /**
     * main(args) handles the processing of arguments as sequential actions to pass to the underlying XPathTool.
     *
     * @param args commandline arguments for processing:
     * <ul>
     *   <li>-i   loads a file (see {@link XPathTool#load(String)} )</li>
     *   <li>-o   saves a file (see {@link XPathTool#save(String)} )</li>
     *   <li>-r   sets a replacement expression: anything "found" while running a "-f" argument is munged by this value</li>
     *   <li>-f   starts a search, replacing the value of all matching nodes and attributes with the replcement value given</li>
     * </ul>
     */
    public static void main(String[] args)
    {
        XPathTool tool = new XPathTool();
    	Getopt g = new Getopt("XPathSub", args, "i:o:R:r:F:f:T");
    	int c; String arg;
	String editMatch = "";
	String editReplace = "";
	boolean global=false;
	String expression;

    	while ((c = g.getopt()) != -1)
    	{
      	    switch(c)
      	    {
        	case 'r':       // set the replacement value
		case 'R':
		String opt = g.getOptarg();
          	String[] res = opt.split(opt.substring(0, 1),4);
		editMatch = res[1];
		editReplace = res[2];
		if (java.lang.reflect.Array.getLength(res) >= 2)
			global = (res[3].equalsIgnoreCase("g"));
		else
			global = false;
          	break;

		/*
		 * ~/src/cnp/tools/xpathget EDD_TEXT.xml '//ScanTask[@name="VWVersionSearch"]/@file'
		 *
		 * ~/src/cnp/tools/xpathset EDD_TEXT.xml '//ScanTask[@name="VWVersionSearch"]/@file' 'VW/this/is/a/test.xml'
		 */

       		case 'f':       // set the search value and execute a search
		case 'F':
	        /*
		 * String expression = "//ScanTask[@name='VWVersionSearch']/@file";
		 */
      		expression = g.getOptarg();
		int replacements = 0;

		try {
	        replacements = tool.subAttr( (NodeList)tool.search(expression, XPathConstants.NODESET), editMatch, editReplace, global);
		} catch (org.smallfoot.xpath.NoDocumentException e) {
		    System.out.println ("Cannot search with " + expression + " : " + e.getMessage());
		}
		System.out.println (replacements + " replaced.");
       		break;

       		case 'o':       // output the current file
       		tool.save(g.getOptarg());
       		break;

       		case 'i':       // open a new file
       		tool.load(g.getOptarg());
       		break;

       		case 'T':       // Test dump internal
       		System.out.println ("match: "+editMatch);
       		System.out.println ("replace: "+editReplace);
       		System.out.println ("global: "+global);
       		break;
	    }
   	}
    }

	}
