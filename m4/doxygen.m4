dnl
dnl based on https://github.com/chickenandpork/wwndesc/blob/master/configure.ac
dnl
#serial 20140218

AC_DEFUN([CHECK_DOXYGEN],
[
  DOXYFILE_IN="$1"
  case "$DOXYFILE_IN" in
  "")
	DOXYFILE_IN=Doxyfile.in ;;
  *)
	;;
  esac

  AC_ARG_WITH(doxygen,
  [AS_HELP_STRING([--with-doxygen],[use Doxygen to parse code markup (default=search)])],
  [
    case "$withval" in
        ""|"yes")
                AC_PATH_PROGS(DOXYGEN, [doxygen], missing, $PATH:/Applications/Doxygen.app/Contents/Resources)
                ;;
        "no")  DOXYGEN=missing ;;
        *) DOXYGEN=$withval ;;
    esac

    case "$DOXYGEN" in
        "missing") HAVE_DOXYGEN_MARKDOWN=no ;;
        *)
            case `$DOXYGEN --version` in
                1.[0-7]* ) HAVE_DOXYGEN_MARKDOWN=no;;
                *) HAVE_DOXYGEN_MARKDOWN=yes;;
            esac ;;
    esac
],
[
        DOXYGEN=missing
        HAVE_DOXYGEN_MARKDOWN=no
])
AC_SUBST(HAVE_DOXYGEN_MARKDOWN)
AM_CONDITIONAL(DO_DOXYGEN_MD, test "x$HAVE_DOXYGEN_MARKDOWN" = "xyes")

AC_PATH_PROGS(DOT, [dot], missing, $PATH:/usr/local/bin:/usr/local/graph*/bin)
AM_CONDITIONAL(DO_DOT, test "x$DOT" != "xmissing")
AM_CONDITIONAL(DO_DOXYGEN, test "x$DOXYGEN" != "xmissing" -a "x$DOT" != "xmissing")

AM_CONDITIONAL(DO_DOXYFILE, test "x$DOXYGEN" != "xmissing")
AC_SUBST(DOXYFILE_IN)

#AM_COND_IF not available on automake-1.10 (2006, latest in XCode)
if test "x$DOXYGEN" != "xmissing"
then
  AC_CONFIG_FILES([Doxyfile:${DOXYFILE_IN}],,[DOXYFILE_IN=$DOXYFILE_IN])
fi

AC_SUBST(PATHDOT, [`dirname "${DOT}"`])
])
