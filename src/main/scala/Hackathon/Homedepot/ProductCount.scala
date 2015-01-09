package Hackathon.Homedepot

import java.util

import Hackathon.Messages
import Hackathon.Messages.Config
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import scala.io.Source
import scala.util.Try

/**
 * Created by indix on 8/1/15.
 */
object ProductCount {

  def main(args: Array[String]) = {
    val count = ProductCount.getCountOf("http://www.homedepot.com")
    println(count)
  }

  def getCountOf(url:String):Int={
    val config: Config = Messages.getConfigFor(url)
    val homePageContent=curl(config.seedUrls)
    val headCategories: Elements = Jsoup.parse(homePageContent).select(config.headListCSSSelector)
    val itr: util.ListIterator[Element] = headCategories.listIterator
    var headCategoriesList = List[String]()
    while(itr.hasNext)
    {
      headCategoriesList = itr.next().attr("href") :: headCategoriesList
    }
    println("Head Categories" + headCategoriesList)
    val allListingPages = headCategoriesList.flatMap(getListingPage(config,0))
    val productCounts = allListingPages.map(getCount(config)).foldRight(0)(_+_)
    productCounts
    //println(getCount(config)("http://www.homedepot.com/b/Tools-Hardware-Power-Tools-Power-Tool-Combo-Kits/N-5yc1vZc2ec"))
  }

  def getCount(config: Config)(url: String): Int = {
    val productCountContent=curl(url)
    val html = Jsoup.parse(productCountContent).select(config.ListingPageProductCount).text()
    println(html)
    val productCount = Try((config.productCountRegex.r findAllMatchIn  html).mkString.toString.toInt).getOrElse(0)
    println("Product count from "+url+" is "+productCount)
    productCount
  }

  def getListingPage(config: Config, depth: Int)(url: String): List[String] = {
    val subCategoryContent=curl(url)
    val subCategories = Jsoup.parse(subCategoryContent).select(config.subCategoryCSSSelector(depth))
    //println(subCategories)
    val itr: util.ListIterator[Element] = subCategories.listIterator
    var subCategoriesList = List[String]()
    while(itr.hasNext)
    {
      subCategoriesList = itr.next().attr("href") :: subCategoriesList
    }
    println("Subcategories for "+ url + " are " + subCategoriesList)

    subCategoriesList
  }

  def curl(url: String): String = {
    //println("Fetching: "+url)
    val content = Try(Source.fromURL(url).mkString).getOrElse("")
    Thread.sleep(100)
    content
  }
}
