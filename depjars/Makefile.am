depjardir = @datadir@/@PACKAGE@/jars
dist_depjar_DATA = $(JARS)

JARS := java-getopt-1.0.13.jar

EXTRA_DIST = $(nodist_pkgdata_JAVA)
CONFIG_CLEAN_FILES = $(JARS)
BUILT_SOURCES := $(JARS)

java-getopt-1.0.13.jar:
	test -f $${TMPDIR-/tmp}/$(@F) && cp $${TMPDIR-/tmp}/$(@F) $@ || true
	test -f $@ || wget -O $@ http://download.java.net/maven/2/gnu/getopt/java-getopt/1.0.13/$(@F) || curl -Lo $@ http://download.java.net/maven/2/gnu/getopt/java-getopt/1.0.13/$(@F)
	test -f $${TMPDIR-/tmp}/$(@F) || cp $@ $${TMPDIR-/tmp}/$(@F)

# catch that case of downloading content, the website claims it's sending content, but it's really sending a "hi, the content is over here, behind this ad..." webpage

check-local: $(JARS)
	for i in $<; do jar tf $$i >/dev/null || exit 1; done

