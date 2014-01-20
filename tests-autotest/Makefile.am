EXTRA_DIST = testsuite.at.in testsuite $(TESTSUITE)
CONFIG_CLEAN_FILES = atconfig package.m4 testsuite testsuite.log testsuite.at
TESTSUITE = $(srcdir)/testsuite

check-local: atconfig $(TESTSUITE)
	$(SHELL) '$(TESTSUITE)' $(TESTSUITEFLAGS) AUTOTEST_PATH=$$(dirname $(JAVA))
	rm -fr $(srcdir)/testsuite.dir

installcheck-local: atconfig $(TESTSUITE)
	$(SHELL) '$(TESTSUITE)' AUTOTEST_PATH='$(bindir)' $(TESTSUITEFLAGS)
	rm -fr $(srcdir)/testsuite.dir

clean-local:
	test ! -f '$(TESTSUITE)' || $(SHELL) '$(TESTSUITE)' --clean

AUTOTEST = $(AUTOM4TE) --language=autotest
$(TESTSUITE): $(srcdir)/testsuite.at $(srcdir)/package.m4
	@echo Making $@...
	$(AUTOTEST) -I $(srcdir)/$(subdir) $@.at -o $@.tmp && mv $@.tmp $@

atconfig: $(top_builddir)/config.status
	cd $(top_builddir) && \
	  $(SHELL) ./config.status $(subdir)/$@

$(srcdir)/package.m4: $(top_srcdir)/configure.ac
	@echo Making $@...
	@{						\
	  echo 'm4_define([abs_top_srcdir],      [$(abs_top_srcdir)])'; \
	  echo 'm4_define([AT_PACKAGE_NAME],      [@PACKAGE_NAME@])'; \
	  echo 'm4_define([AT_PACKAGE_TARNAME],   [@PACKAGE_TARNAME@])'; \
	  echo 'm4_define([AT_PACKAGE_VERSION],   [@PACKAGE_VERSION@])'; \
	  echo 'm4_define([AT_PACKAGE_STRING],    [@PACKAGE_STRING@])'; \
	  echo 'm4_define([AT_PACKAGE_BUGREPORT], [@PACKAGE_BUGREPORT@])'; \
	} >$(srcdir)/package.m4

