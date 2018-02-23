package service

import akka.http.scaladsl.model.Uri
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr => _, element => _, elementList => _, text => _}

import scala.collection.immutable.IndexedSeq

case class Speech(uid: String, title: String, lead: String, body: Seq[String], category: String)

class ScraperService {

  val browser: Browser = JsoupBrowser()

  def scrape(): IndexedSeq[Speech] = {
    for {
      currentType <- IndexedSeq("beszedek", "interjuk")
      doc = browser.get(s"http://www.miniszterelnok.hu/category/$currentType/")
      pageNumber <- 0 until (doc >> element(".navigation ul li:nth-last-child(2) a") >> text toInt)
      currentParentPage = browser.get(s"http://www.miniszterelnok.hu/category/$currentType/page/$pageNumber/")
      speechLinks <- currentParentPage >> elementList("#category_element a") >?> attr("href")("a")
      currentLink <- speechLinks
      currentPage = browser.get(currentLink)
      title = currentPage >> "#post_title" >> text
      lead = currentPage >> "#post_lead" >> text
      body = currentPage >> elementList("#post_content_col_1>p") >> text
      uid = Uri(currentLink).path.toString
    } yield Speech(uid, title, lead, body, currentType)
  }
}