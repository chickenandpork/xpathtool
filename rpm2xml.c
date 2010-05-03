#include <time.h>

#ifdef HAVE_CONFIG_H
#include <cnp-config.h>
#endif

#ifdef HAVE_LIBRPM_H
#include <librpm.h>
#endif

#ifdef HAVE_RPM_LIBRPM_H
#include <rpm/librpm.h>
#endif

#include <stdlib.h>
#include <rpm/rpmcli.h>
#include <rpm/rpmdb.h>
#include <rpm/rpmds.h>
#include <rpm/rpmts.h>

//char rfc3986Reserved[] = "%&'()*+,/:;<=>?@[]";
char rfc3986Reserved[] = "%&'()*+,/:;<=>?@[]";
char hex[] = "0123456789abcdef";

//void *bsearch(const void *key, const void *base, size_t nel, size_t width, int (*compar)(const void *, const void *));

int rescomp (const char *k, const char *t)
{
    if (*k < *t) return -1;
    if (*k > *t) return 1;
    return 0;
}

int countRes (const char *s)
{
    int res = 0;

    for (; *s; s++)
	if (NULL != bsearch (s, rfc3986Reserved, strlen(rfc3986Reserved), sizeof (char), rescomp))
	    res ++;

    return res;
}

void xmlPrint(char *element, char *string)
{
    char buf[200];
    int i = 0;
    printf("<%s>", element);
    for (; *string; string++)
    {
	switch (*string)
	{
	    case '&': buf[i++] = '&'; buf[i++] = 'a'; buf[i++] = 'm'; buf[i++] = 'p'; buf[i++] = ';';
		break;
	
	    case '<': buf[i++] = '&'; buf[i++] = 'l'; buf[i++] = 't'; buf[i++] = ';';
		break;
	
	    case '>': buf[i++] = '&'; buf[i++] = 'g'; buf[i++] = 't'; buf[i++] = ';';
		break;

	    default:
		buf[i++] = *string;
		break;
	}

	// if there's insufficient room to push another largest (&amp;) char, shift it out
	if (i > (sizeof(buf)-5))
	{ buf[i] = 0; printf ("%s", buf); i = 0; }
    }

    buf[i] = 0;
    printf("%s</%s>\n", buf, element);
}

void urlPrint(char *element, char *string)
{
    char buf[200];
    int i = 0;
    printf("<%s>", element);
    for (; *string; string++)
    {
	if ((*string < 128) && (*string > 0) && (NULL == bsearch (string, rfc3986Reserved, strlen(rfc3986Reserved), sizeof (char), rescomp)))
	    buf[i++] = *string;
	else { buf[i++] = '%'; buf[i++] = hex[*string / 16]; buf[i++] = hex[*string % 16]; }
	if (i > (sizeof(buf)-3))
	{ buf[i] = 0; printf ("%s", buf); i = 0; }
    }
    buf[i] = 0;
    printf("%s</%s>\n", buf, element);
}

/*
 * http://refspecs.freestandards.org/LSB_3.1.0/LSB-Core-generic/LSB-Core-generic/pkgformat.html
 * http://rpm5.org/docs/api/header_8h.html
 * http://rpm5.org/docs/api/rpmlib_8h.html
 */
int printRes(int res_type, char *element, void *res)
{
    union {
        char *c;
	unsigned *u;
    } *r = res;

    switch (res_type)
    {
	case RPM_STRING_TYPE:
	    //printf("<%s>%s</%s>\n", element, (char *) res, element);
	    xmlPrint(element, (char *) res);
	    return 0;

	case RPM_INT32_TYPE:
	    printf("<%s>%u</%s>\n", element, r->u, element);
	    //printf("<%s>%08x %u</%s>\n", r->u, r->u);
	    return 0;

	default:
	    printf("<unknown>type %02d</unknown>\n", res_type);
    }

    return 1;
}

int getAndPrintHeader (char *filename)
{
    Header header;
    rpmts ts;
    FD_t fd;
    int ret = 0;

    fd = Fopen(filename, "r.ufdio");
    if (fd == NULL || Ferror(fd))
    {
	rpmError(RPMERR_OPEN, "open of %s failed: %s\n", filename, Fstrerror(fd));
	if (fd) Fclose(fd);

	return -1;
    }

    ts = rpmtsCreate();
    switch (rpmReadPackageFile(ts, fd, filename, &header))
    {
        case RPMRC_OK:
	case RPMRC_NOKEY:
	    {
		int res_type;
		void *res;
		int res_count;
		struct stat s;

		char *base = strrchr(filename, '/');
		if (NULL == base)
		    base=filename;
		else
		    base++;

		printf("<rpm>\n");
		xmlPrint("filename", base);
		if (0 == stat (filename, &s))
		{
		    printRes(RPM_INT32_TYPE,"size", &s.st_size);
		    printISODate(RPM_INT32_TYPE, "fileTime", &s.st_mtime);
		    print822Date(RPM_INT32_TYPE, "rfc822FileTime", &s.st_mtime);
		}

		if (1 == rpmHeaderGetEntry(header, RPMTAG_NAME, &res_type, &res, &res_count))
		    printRes(res_type, "name", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_EPOCH, &res_type, &res, &res_count))
		{
		    printRes(res_type, "epoch", res);
		    printf("<!-- note: epoch tends to indicate that packager or upstream predicts incompetency of version-discipline -->\n", (char *) res);
		}
		if (1 == rpmHeaderGetEntry(header, RPMTAG_VERSION, &res_type, &res, &res_count))
		    printRes(res_type, "version", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_RELEASE, &res_type, &res, &res_count))
		    printRes(res_type, "release", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_ARCH, &res_type, &res, &res_count))
		    printRes(res_type, "arch", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_LICENSE, &res_type, &res, &res_count))
		    printRes(res_type, "license", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_URL, &res_type, &res, &res_count))
		    printRes(res_type, "url", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_BUILDTIME, &res_type, &res, &res_count))
		{
		    printRes (res_type, "epochBuildTime", res);
		    printISODate(res_type, "buildTime", res);
		    print822Date(res_type, "rfc822BuildTime", res);
		}
		if (1 == rpmHeaderGetEntry(header, RPMTAG_SUMMARY, &res_type, &res, &res_count))
		    printRes(res_type, "summary", res);
		if (1 == rpmHeaderGetEntry(header, RPMTAG_DESCRIPTION, &res_type, &res, &res_count))
		    printRes(res_type, "description", res);
		printf("</rpm>\n");
            }
	    break;

	default:
	    rpmError(RPMERR_OPEN, "%s cannot be read (err: %d)\n", filename, ret);
	    return -2;
    }

    ts = rpmtsFree(ts);
    Fclose(fd);

    return 0;
}

int print822Date(int res_type, char *element, /* void *res */ void *dd)
{
    char buf[100];
    time_t t = *((unsigned *) dd);
    struct tm *tt = gmtime (&t);

    switch (res_type)
    {
	case RPM_INT32_TYPE:
	    strftime(buf, sizeof(buf), "%a, %d %b %Y %H:%M:%S %z", tt);
	    printf("<%s>%s</%s>\n", element, buf, element);
	    return 0;

	default:
	    printf("<bad-type>type %02d</bad-type>\n", res_type);
	    return 1;
    }
}

int printISODate(int res_type, char *element, /* void *res */ void *dd)
{
    char buf[100];
    time_t t = *((unsigned *) dd);
    struct tm *tt = gmtime (&t);

    switch (res_type)
    {
	case RPM_INT32_TYPE:
	    strftime(buf, sizeof(buf), "%Y-%m-%d %H:%M:%S", tt);
	    printf("<%s>%s</%s>\n", element, buf, element);
	    return 0;

	default:
	    printf("<bad-type>type %02d</bad-type>\n", res_type);
	    return 1;
    }
}

int main(int argc, char *argv[])
{
    int i;

    printf("<?xml version='1.0'?>\n<rpms>\n");
    for (i = 1; i < argc; ) getAndPrintHeader (argv[i++]);
    printf("</rpms>\n");

    return 0;
}
