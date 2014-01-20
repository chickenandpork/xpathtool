package org.smallfoot.xpath ;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
//import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * XPathTool is a relatively uncomplicated wrapper for an XPath action: it doesn't know what you're doing, and doesn't assume you'll do things in a specific order, but tries to stop you from making really silly errors like saving or searching an unloaded file.
 */

public class XPathTool
{
    private org.w3c.dom.Document xmlDocument;

    /**
     * Class Constructor to create with an initial file to load.
     *
     * @param xmlFile File to load at start
     *
     * @see #load(String)
     */
    public XPathTool(String xmlFile)
    {
        load(xmlFile);
    }
	    
    /**
     * Class Constructor with no initial file.
     */
    public XPathTool()
    {
    }

    /**
     * Open a file.
     *
     * This is actually a wrapper for the complex usage of javax.xml.parsers.DocumentBuilderFactory to find a singleton to instantiate a document from a file.
     *
     * @param xmlFile file to load
     * @throws java.io.IOException on issues loading the file given as a parameter
     * @throws javax.xml.parsers.ParserConfigurationException when the javax.xml.parsers.DocumentBuilderFactory is butchered
     * @throws org.xml.sax.SAXException on parsing issues
     */
    protected void _load(String xmlFile)
	throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException, java.io.IOException
    {
	xmlDocument = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
    }

    /**
     * Wrapper to just load the file, spitting out exceptions and stacks as they occur.
     *
     * @param xmlFile file to load
     */
    public void load(String xmlFile)
    {
	try
	{
	    _load (xmlFile);
	} catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the current XML Document to a new file.
     * <p>
     * based partially on <a href="http://download.oracle.com/javase/1.5.0/docs/api/javax/xml/parsers/DocumentBuilderFactory.html">DocumentBuilderFactory.java</a> and 
     * influenced by <a href="http://stackoverflow.com/questions/2290945/writing-xml-on-android/3551691#3551691">Writing XML on Android</a>
     *
     * @param xmlFile filename to save into
     * @throws java.io.FileNotFoundException on issues creating the output filestream on xmlFile (ie missing interposing directory or permissions)
     * @throws javax.xml.transform.TransformerConfigurationException when the javax.xml.transform.TransformerFactory is butchered
     * @throws javax.xml.transform.TransformerException for exceptions on the final step, the transformation itself
     * @throws org.smallfoot.xpath.NoDocumentException if the user has tried to search before a document is loaded
     */
    protected void _save(String xmlFile)
	throws javax.xml.transform.TransformerConfigurationException, java.io.FileNotFoundException, javax.xml.transform.TransformerException, org.smallfoot.xpath.NoDocumentException
    {
	if (null == xmlDocument) throw new org.smallfoot.xpath.NoDocumentException("cannot save without a loaded document");

	javax.xml.transform.TransformerFactory factory = javax.xml.transform.TransformerFactory.newInstance();
	javax.xml.transform.Transformer transformer = factory.newTransformer();

	javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(xmlDocument.getDocumentElement());
	javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(new java.io.FileOutputStream(xmlFile));

	transformer.transform (domSource, result);
    }

    /**
     * Wrapper to just save the file, spitting out exceptions and stacks as they occur.
     *
     * @param out filename to save into
     */
    public void save (String out)
    {
	try {
	    _save (out);
	} catch (javax.xml.transform.TransformerConfigurationException e) {
            e.printStackTrace();
	} catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
	} catch (javax.xml.transform.TransformerException e) {
            e.printStackTrace();
	} catch (org.smallfoot.xpath.NoDocumentException e) {
            e.printStackTrace();
	}
    }

    
    /**
     * Search the current XML Document for a match, returning a list of matching nodes.
     * 
     * @param expression XPath expression to search for
     * @param returnType namespace context against which the expression is evaluated
     * @throws org.smallfoot.xpath.NoDocumentException if the user has tried to search before a document is loaded
     */
    public Object search(String expression, QName returnType)
	throws org.smallfoot.xpath.NoDocumentException
    {
	if (null == xmlDocument) throw new org.smallfoot.xpath.NoDocumentException("cannot search without a loaded document");

        XPath xPath = XPathFactory.newInstance().newXPath();

	try {
            XPathExpression xPathExpression = xPath.compile(expression);
	    return xPathExpression.evaluate(xmlDocument, returnType);
	} catch (javax.xml.xpath.XPathExpressionException ex) {
	    ex.printStackTrace();
	    return null;
        }
    }

    /**
     * Consume a list of matches, and for each node (entity or attribute) make a Strong.replaceFirst() or a String.replaceAll()
     *
     * This function is added as a convenience function to allow semi-contextual replacement of strings
     * when converting other XML content -- this allows single atomic commands to rename components to
     * avoid name-clashes and unintended replacements.
     *
     * @param nodelist list of nodes containing targets/matches to alter
     * @param editMatch string to match for replacement
     * @param editReplace string to replace for matches
     * @param global whether to replace globally (like sed -e 's/X/y/g' -- the 'g' at the end) -- which is whether to use a Strong.replaceFirst() or a String.replaceAll()
     * @return number of matches made
     */
    public static int subAttr(NodeList nodelist, String editMatch, String editReplace, boolean global)
    {
        // http://download.oracle.com/javase/1.5.0/docs/api/org/w3c/dom/Node.html
        int res = 0;

        for(int index = 0; index < nodelist.getLength(); index ++)
        {
            res++;
            Node aNode = nodelist.item(index);
            switch (aNode.getNodeType())
            {
                case Node.ELEMENT_NODE:
                    System.out.println ("replacing: "+aNode.getNodeType());
                    if (global)
                        aNode.setTextContent(aNode.getTextContent().replaceAll(editMatch, editReplace));
                    else
                        aNode.setTextContent(aNode.getTextContent().replaceFirst(editMatch, editReplace));
                    break;

                case Node.ATTRIBUTE_NODE:
                    if (global)
                        aNode.setNodeValue(aNode.getTextContent().replaceAll(editMatch, editReplace));
                    else
                        aNode.setNodeValue(aNode.getTextContent().replaceFirst(editMatch, editReplace));
                    break;

                default:
                    System.out.println ("Not replacing: "+aNode.getNodeType());
                    res--;
            }
        }

    return res;
    }

    /**
     * Consume a list of matches, and for each node (entity or attribute) replace the current value
     *
     * This function allows specific programmatic replacement of string values in XML to reduce the chance of user error and typos creating erroneous XML.
     *
     * @param nodelist list of nodes containing targets/matches to alter
     * @param replacement string to replace for matches
     * @return number of matches made
     */
    public static int setAttr(NodeList nodelist, String replacement)
    {
        int res = 0;

        for(int index = 0; index < nodelist.getLength(); index ++)
        {
            res++;
            Node aNode = nodelist.item(index);
            switch (aNode.getNodeType())
            {
                case Node.ELEMENT_NODE:
                    System.out.println ("replacing: "+aNode.getNodeType());
                    aNode.setTextContent(replacement);
                    break;

                case Node.ATTRIBUTE_NODE:
                    aNode.setNodeValue(replacement);
                    break;

                default:
                    System.out.println ("Not replacing: "+aNode.getNodeType());
                    res--;
            }
        }

        return res;
    }

}
