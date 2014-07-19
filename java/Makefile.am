nodist_java_JAVA  = XPathTool.java XPathSet.java XPathSub.java XPathException.java NoDocumentException.java
nodist_java_JAVA += version.java
nodist_java_DATA  = @PACKAGE@.jar
javadir = $(pkgdir)

EXTRA_DIST = $(nodist_java_JAVA) version.java.in XPathSet.java.in
CONFIG_CLEAN_FILES = @PACKAGE@.jar version.java XPathSet.java

JAVAROOT = bld
BUILT_SOURCES = javaroot.stamp

javaroot.stamp: $(nodist_java_JAVA)
	mkdir -p $(JAVAROOT) && touch $@

AM_JAVACFLAGS= @JAVAC_FLAGS@ -cp $(JAVAROOT):@GETOPTJAR@

@PACKAGE@.jar: classjava.stamp $(nodist_java_JAVA)
	awk -v BUILDTOP="bld" '/^package / {gsub (";$$",""); gsub ("\\.","/",$$2); SUBS[$$2]=$$2 } END { FIRST=0; for (n in SUBS) { if (0 == FIRST++) printf "jar cvf $@"; printf " -C %s %s",BUILDTOP,n }; printf "\n"; }' $^ | $(SHELL)
	touch @PACKAGE@.class

clean-local:
	CLASSES=`for i in $(nodist_java_JAVA); do awk -v FILE="$$i" '/^package / {gsub (";$$",""); gsub (".java",".class",FILE); gsub ("\\\\.","/",$$2); printf "%s/%s\n",$$2,FILE}' $(srcdir)/$$i; done`; (cd $(top_builddir)/ && rm -f $$CLASSES)
	rm -fr $(JAVAROOT) javaroot.stamp
	
