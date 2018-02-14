package service

import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import net.ruippeixotog.scalascraper.dsl.DSL._

class ScraperService {

  val browser: Browser = JsoupBrowser()

  def returnExample() = {
    val doc: browser.DocumentType = browser.get("http://www.miniszterelnok.hu/#beszedek_container")

    (doc >> "#beszedek_holder").toString
  }
}
