package org.smallfoot.xpath ;

/** Exception to show that no document has yet been loaded or is available for consideration */
public class NoDocumentException extends XPathException {
	NoDocumentException(String m) { super(m); }
}

