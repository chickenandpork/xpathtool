DIST_SUBDIRS =	depjars java convjars src tests-autotest
SUBDIRS =	src depjars
if DO_JAVA
SUBDIRS +=	java convjars
endif
SUBDIRS +=	tests-autotest

ACLOCAL_AMFLAGS = -I m4

# if missing mkinstalldirs install-sh trigger errors, ignore the result code on the "system (cp ...)" command in automake
# CONFIG_CLEAN_FILES = aclocal.m4 config.guess config.sub config.h config.h.in ltconfig ltmain.sh missing mkinstalldirs install-sh Doxyfile @PACKAGE@.spec @PACKAGE@.jar changes.atom
CONFIG_STATUS_DEPENDENCIES = gitdescribe
EXTRA_DIST = @PACKAGE@.spec gitdescribe



if DO_DOXYGEN
doc: htdocs/README.dox Doxyfile
	$(DOXYGEN)
	if test -d $(<D)/.git; then (cd $(<D) && git add -A); if test -n "$(DOXYMESSAGE)" ; then (cd $(<D) && git commit -a -m "$(DOXYMESSAGE)" && git push); fi; else echo "$(<D) subdir not set as a subrepo; see http:// ..."; exit 1; fi

htdocs/README.dox: README.md Makefile
	if test -d $(@D)/.git; then :; else \
		if grep $(@D) .gitignore; then echo found gitignore; else echo $(@D) >> .gitignore && git add .gitignore; fi ; \
		if git branch --list|grep gh-pages; then echo found gh-pages; else echo you need to create the gh-pages branch on github; exit 1; fi ; \
		git clone $$(git config --get remote.origin.url) -b gh-pages $(@D) && find $(@D) -name $(@D) -o -name .git -prune -o -name xml -prune -o -name latex -prune -o -exec rm -fr {} \; 2>/dev/null ; \
		echo "checked out" ; \
	fi
if DO_DOXYGEN_MD
	awk 'BEGIN { print "@file "; print "@mainpage JavaDoc API Markup for @PACKAGE@"; } { printf "%s\n",$$0; } END {print "" }' $< > $@
else
	awk 'BEGIN { print "/** @file "; print "@mainpage JavaDoc API Markup for @PACKAGE@"; } /@PACKAGE@$$/ {next} /^=+$$/ {next} { printf " * %s\n",$$0; } END {print " */" }' $< > $@
endif DO_DOXYGEN_MD_FALSE
endif

if DO_ASTYLE
formatting: src/rpm2xml.c src/xmlsoft-xpath1.c srcxmlsoft-xpath2.c
if DO_GITFORMATTING
	@git ls-files --other --exclude-standard --directory || { echo "###################### unignored local files: git add, git commit ?"; }
	@git diff --exit-code || { echo "###################### unstaged changes: git commit -a"; exit 1; }
	@git diff --cached --exit-code || { echo "###################### staged uncommitted changes: git commit"; exit 1; }
endif
	$(MAKE) check
	for i in $^; do $(ASTYLE) -A1 $$i; done
	$(MAKE) check
if DO_GITFORMATTING
	git commit -a -m 'formatting and whitespace'
endif DO_GITFORMATTING

endif DO_ASTYLE

if DO_GITFORMATTING
gitdescribe: .git/index
	git describe > $@
endif DO_GITFORMATTING


if HAVE_RPMBUILD
srpm: dist-bzip2
	@mkdir -p $$(rpm --eval '%{_srcrpmdir}')
	@RPMBUILD@ -ts $(PACKAGE)-$(VERSION).tar.bz2
if DO_SUDOPOSTMOVE
	mv $$(rpm --eval '%{_srcrpmdir}')/@PACKAGE@-$$(rpm --qf '%{VERSION}\n' -q --specfile @PACKAGE@.spec |head -1)-$$(rpm --qf '%{RELEASE}\n' -q --specfile @PACKAGE@.spec |head -1).src.rpm @SUDOPOSTMOVE@
endif   DO_SUDOPOSTMOVE

if DO_GITFORMATTING
re-dist-bzip2: gitdescribe
	rm -f @PACKAGE@.spec $(PACKAGE)-$(VERSION).tar.bz2
	touch configure
	$(MAKE) dist-bzip2
rpm: re-dist-bzip2
else DO_GITFORMATTING_FALSE
rpm: dist-bzip2
endif DO_GITFORMATTING_FALSE
	@RPMBUILD@ --define 'skipcheck 1' -ta $(PACKAGE)-$(VERSION).tar.bz2
endif HAVE_RPMBUILD


