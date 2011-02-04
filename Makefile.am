bin_PROGRAMS = xpathget xpathset
if HAVE_RPMBUILD
bin_PROGRAMS += rpm2xml
endif
pkgdata_DATA = xml2rss.xsl
EXTRA_DIST = xml2rss.xsl

xpathget_SOURCES = xmlsoft-xpath1.c
xpathget_CPPFLAGS = @XML_CPPFLAGS@
xpathget_LDADD = @XML_LIBS@

xpathset_SOURCES = xmlsoft-xpath2.c
xpathset_CPPFLAGS = @XML_CPPFLAGS@
xpathset_LDADD = @XML_LIBS@

rpm2xml_SOURCES = rpm2xml.c

