package com.indix.bootcamp.crawler

import edu.uci.ics.crawler4j.crawler.{Page, WebCrawler}
import edu.uci.ics.crawler4j.parser.HtmlParseData
import com.indix.bootcamp.parser.{Parser, FlipkartParser, JabongParser}
import java.io.{PrintWriter, File}
import scala.util.Random
import edu.uci.ics.crawler4j.url.WebURL

abstract class BaseCrawler extends WebCrawler {
  val parser: Parser
  val writer = new PrintWriter(new File("/tmp/crawler4j-scala/results-" + Random.nextInt(Int.MaxValue) + ".csv"))

  /*
    TODO: By default the crawler extracts urls from all the tags like link, script, embed, img, a etc.
      Write an exclude filter for ignoring all the css / js / images / audio / video formats from the urls.
      Also make sure you don't want to download urls that emits ZIP / TAR / GZ files.

      An example is provided for reference.
   */
  def excludeFilters = List(
    "(?i)(.*(\\.(zip|pdf|mp3|jpg|rar|exe|wmv|" +
      "doc|avi|ppt|mpg|tif|wav|mov|psd|wma|sitx|" +
      "sit|eps|cdr|ai|xls|mp4|m4a|rmvb|bmp|pps|aif|" +
      "pub|dwg|gif|qbb|mpeg|indd|swf|asf|png|dat|rm|" +
      "mdb|chm|jar|dvf|dss|dmg|iso|flv|wpd|cda|m4b|7z|gz|" +
      "fla|qxd|rtf|aiff|msi|jpeg|3gp|cdl|vob|ace|m4p|divx|" +
      "pst|cab|ttf|xtm|hqx|qbw|sea|ptb|bin|mswmm|ifo|tgz|log|" +
      "dll|mcd|ss|m4v|eml|mid|ogg|ram|lnk|torrent|ses|mp2|vcd|" +
      "bat|asx|ps|bup|cbr|amr|wps|sql))(\\?.*)*)$"
  )

  override def shouldVisit(url: WebURL): Boolean = {
    val urlStr = url.getURL
    !excludeFilters.exists(urlStr.matches)
  }

  override def visit(page: Page) {
    println(s"Fetched ${page.getWebURL.getURL} from ${page.getWebURL.getAnchor}")
    page.getParseData match {
      case data: HtmlParseData =>
        val result = parser.parse(data.getHtml, page.getWebURL.getURL)
        println(s"Parsed successfully as ${result}")
        writer.append(result.toCsv)
        writer.append("\n")
    }
  }

  override def onBeforeExit() {
    writer.close()
  }
}

class FlipkartCrawler extends BaseCrawler {
  override val parser: Parser = new FlipkartParser
}

class JabongCrawler extends BaseCrawler {
  override val parser: Parser = new JabongParser
}