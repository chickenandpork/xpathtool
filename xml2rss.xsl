<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:content="http://purl.org/rss/1.0/modules/content/">

  <xsl:output method="xml" encoding="UTF-8" />

  <xsl:template match="rpms">
    <rss version="2.0"><channel>
      <title>RPMs</title>
    <xsl:apply-templates select="rpm"> <xsl:sort select="fileTime" order="descending"/> <xsl:sort select="name"/> <xsl:sort select="arch"/> </xsl:apply-templates>
    </channel></rss>
  </xsl:template>

  <xsl:template match="rpm">
    <item>
      <title><xsl:value-of select="name"/>-<xsl:value-of select="version"/>-<xsl:value-of select="release"/></title>
      <link>http://repos.chickenandporn.com/CentOS5.4/RPMS.3rdparty/<xsl:value-of select="arch"/>/<xsl:value-of select="filename"/></link>
      <pubDate><xsl:value-of select="rfc822FileTime"/></pubDate>
      <xsl:copy-of select="description"/>
      <content:encoded><xsl:value-of select="description"/>&lt;br/&gt;
        &lt;ul&gt;
          &lt;li&gt;license: <xsl:value-of select="license"/>&lt;/li&gt;
          &lt;li&gt;built: <xsl:value-of select="buildTime"/> (<xsl:value-of select="rfc822BuildTime"/>)&lt;/li&gt;
          &lt;li&gt;size (bytes): <xsl:value-of select="size"/>&lt;/li&gt;
        &lt;/ul&gt;
      </content:encoded>
    </item>
  </xsl:template>

</xsl:stylesheet>
