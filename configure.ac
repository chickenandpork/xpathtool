dnl ***********************************************************************
dnl Process this file with autoconf to produce a configure script.
dnl ***********************************************************************
#AC_INIT(install-sh)
AC_INIT(xpathtool, 1.0)
AC_CONFIG_MACRO_DIR([m4])

AC_CANONICAL_HOST

AM_INIT_AUTOMAKE([foreign dist-bzip2])
AC_CONFIG_TESTDIR([tests-autotest])

dnl ***********************************************************************
dnl Checks for programs used during the build
dnl ***********************************************************************
AC_PROG_CC
AC_PROG_CXX
AC_PROG_INSTALL
AC_PROG_MAKE_SET

AC_SUBST(AUTOM4TE, autom4te)

AM_PATH_XML2()

SVN_CHECK_JDK([1.6],[1.6])
AM_CONDITIONAL(DO_JAVA, test "$JAVAC" != none)

dnl ***********************************************************************
dnl Checks for programs used during test, markup, diagrams. etc.  I'm a huge fan
dnl of automation, and if I can type a single command to make, check, package,
dnl and push a new release, HECK YEAH!
dnl 
dnl This was based on my work for the fctools project (nee vitools) so some may
dnl prune out in the near future
dnl ***********************************************************************
AC_PATH_PROGS(PATHAWK, [awk gawk nawk], [/bin/awk])
AC_PATH_PROGS(XMLLINT, [xmllint], [missing xmllint])
AC_ARG_WITH(xmllintcommentindent,
[AS_HELP_STRING([--with-xmllintcommentindent],[Set XML-comment indentation fudge such as xmllint-2.6.x/2.7.x differences; default=detect])],
[
    case "$withval" in
	"yes") WITH_XMLLINTINDENT="  " ;;
	"no")  WITH_XMLLINTINDENT=""  ;;
	*) case $(xmllint --version 2>&1 | awk '/version/ {print $NF}') in
	    206*) WITH_XMLLINTINDENT="" ;;
	    *) WITH_XMLLINTINDENT="  "  ;;
	    esac ;;
    esac
],
[
    case $(xmllint --version 2>&1 | awk '/version/ {print $NF}') in
	206*) WITH_XMLLINTINDENT="" ;;
	*) WITH_XMLLINTINDENT="  "  ;;
    esac
])
AC_SUBST([XMLLINTINDENT], ${WITH_XMLLINTINDENT})

AC_PATH_PROGS(XSLTPROC, [xsltproc], [missing xsltproc])
CHECK_DOXYGEN(pkg/Doxyfile.in)

AC_PATH_PROGS(ASTYLE, [astyle], missing, $PATH:$HOME/bin)
AM_CONDITIONAL(DO_ASTYLE, test "x$ASTYLE" != "xmissing")
AC_PATH_PROGS(RPMBUILD, [rpmbuild], [missing rpmbuild])
AM_CONDITIONAL(HAVE_RPMBUILD, [test "x$RPMBUILD" != "xmissing rpmbuild"])

if test "x$RPMBUILD" != 'xmissing rpmbuild'
then
AC_CHECK_LIB(rpm, rpmReadPackageFile,,AC_MSG_ERROR(rpm lib not found))
AC_CHECK_LIB(rpm, rpmHeaderGetEntry,,AC_MSG_ERROR(rpm lib not found))
AC_CHECK_HEADERS(rpm/rpmlib.h rpmlib.h)
fi

AC_PATH_PROGS(GIT, [git], [missing])
AC_MSG_CHECKING([version agreement with git tag])
BUILDID=$( (git describe; cat gitdescribe) 2>/dev/null |head -1| cut -d "-" -f 2)
CHKVER=$( (git describe; cat gitdescribe) 2>/dev/null |head -1| cut -d "-" -f 1)
if test "${CHKVER}" = "${VERSION}"
then 
  AC_MSG_RESULT([ok])
else
  AC_MSG_WARN([version mismatch])
fi
AC_SUBST(BUILDID)


AC_CHECK_PROG(GIT, [git], [git],[missing git])
AM_CONDITIONAL(DO_GITFORMATTING, test "$GIT" != "missing git" -a -f .git/config)
AC_PATH_PROGS(ECHO, [echo], [missing])

AC_ARG_ENABLE(autotest,
[AS_HELP_STRING([--enable-autotest],[Enable autotest testcases; breaks: make distcheck; default=disabled])],
[
    case "$enableval" in
	"yes") ENAB=yes ;;
	"no")  ENAB=no  ;;
	*) ENAB=no ;;
    esac
],
[
    ENAB=no
])
AM_CONDITIONAL(DO_AUTOTEST, test "$ENAB" = yes)


AC_ARG_WITH(getoptplus,
[AS_HELP_STRING([--with-getoptplus],[Enable inclusion of an improved getopt .jar; default=search])],
[
    case "$withval" in
        "yes") WITH_GETOPTJAR=$((locate getopt.jar) | grep -v .workdir | head -1) ;;
        "no")  WITH_GETOPTJAR=no  ;;
        *) WITH_GETOPTJAR="$withval" ;;
    esac
],
[
AC_MSG_CHECKING([for getopt.jar])
    WITH_GETOPTJAR=$((locate getopt.jar) | grep -v .workdir | head -1) ;
AC_MSG_RESULT([${WITH_GETOPTJAR}])
])
AM_CONDITIONAL(DO_GETOPTJAR, test "$WITH_GETOPTJAR" != no)
AC_SUBST([GETOPTJAR], ${WITH_GETOPTJAR})



AC_CONFIG_FILES(Makefile
  convjars/Makefile 
  depjars/Makefile 
  java/Makefile
  java/version.java
  src/Makefile src/check_adhoc
  tests-autotest/Makefile tests-autotest/testsuite.at
)

if test "x$RPMBUILD" != "xmissing rpmbuild"
then
  AC_CONFIG_FILES(xpathtool.spec:pkg/rpm/xpathtool.spec.in)
fi

AC_OUTPUT
