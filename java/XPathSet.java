package org.smallfoot.xpath;

import org.smallfoot.xpath.XPathTool;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import gnu.getopt.Getopt;

/**
 * A driving class to interpret commandline arguments and pass them to the underlying XPathTool
 */
public class XPathSet
{
    private int replacements = 0;
 
    /**
     * Class Constructor, uncomplicated
     */
    public XPathSet() { }

    /**
     * main(args) handles the processing of arguments as sequential actions to pass to the underlying XPathTool.
     *
     * @param args commandline arguments for processing:
     * <ul>
     *   <li>-i   loads a file (see {@link XPathTool#load(String)} )</li>
     *   <li>-o   saves a file (see {@link XPathTool#save(String)} )</li>
     *   <li>-r   sets a replacement value: anything "found" while running a "-f" argument is replaced by this value</li>
     *   <li>-f   starts a search, replacing the value of all matching nodes and attributes with the replacement (-r) value given</li>
     *   <li>-R   sets a replacement pattern: ie /from/to/g : anything "found" while running a "-f" argument, all "from" are replaced by "to".  "g" as the last parameter allows multiple replacements per matching node</li>
     *   <li>-F   starts a search, triggering -R replacement on values on all matching nodes</li>
     * </ul>
     */
    public static void main(String[] args)
    {
        XPathTool tool = new XPathTool();
    	Getopt g = new Getopt("XPathSet", args, "i:o:r:f:R:F:");
    	int c; String arg;
	String replace = "";
        String editMatch = "";
        String editReplace = "";
        boolean global = false;
	String expression;

    	while ((c = g.getopt()) != -1)
    	{
      	    switch(c)
      	    {
		/* Direct-overwrite replacement actions */

        	case 'r':       // set the replacement value
          	replace = g.getOptarg();
          	break;

		/*
		 * ~/src/cnp/tools/xpathget EDD_TEXT.xml '//ScanTask[@name="VWVersionSearch"]/@file'
		 *
		 * ~/src/cnp/tools/xpathset EDD_TEXT.xml '//ScanTask[@name="VWVersionSearch"]/@file' 'VW/this/is/a/test.xml'
		 */

       		case 'f':       // set the search value and execute a search
	        /*
		 * String expression = "//ScanTask[@name='VWVersionSearch']/@file";
		 */
      		expression = g.getOptarg();
		int replacements = 0;

		try {
	        replacements = tool.setAttr( (NodeList)tool.search(expression, XPathConstants.NODESET), replace);
		} catch (org.smallfoot.xpath.NoDocumentException e) {
		    System.out.println ("Cannot search with " + expression + " : " + e.getMessage());
		}
		System.out.println (replacements + " replaced.");
       		break;

		/* Replacement/Regex actions */

                case 'R': // set the replacement pattern
                String opt = g.getOptarg();
                String[] res = opt.split(opt.substring(0, 1),4);
                editMatch = res[1];
                editReplace = res[2];
                if (java.lang.reflect.Array.getLength(res) >= 2)
                        global = (res[3].equalsIgnoreCase("g"));
                else
                        global = false;
                break;

                case 'F':
                /*
                 * String expression = "//ScanTask[@name='VWVersionSearch']/@file";
                 */
                expression = g.getOptarg();
                int substitutions = 0;

                try {
                    substitutions = tool.subAttr( (NodeList)tool.search(expression, XPathConstants.NODESET), editMatch, editReplace, global);
                } catch (org.smallfoot.xpath.NoDocumentException e) {
                    System.out.println ("Cannot search with " + expression + " : " + e.getMessage());
                }
                System.out.println (substitutions + " substitutions.");
                break;

       		case 'o':       // output the current file
       		tool.save(g.getOptarg());
       		break;

       		case 'i':       // open a new file
       		tool.load(g.getOptarg());
       		break;
	    }
   	}
    }
}
