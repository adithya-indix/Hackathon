package Hackathon.AtgStores

import java.net.URL
import java.util

import Hackathon.Messages
import Hackathon.Messages.Config
import edu.uci.ics.crawler4j.url.WebURL
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.Try

/**
  * Created by indix on 8/1/15.
  */
object ProductCount {

  var totalcount = 0

   def main(args: Array[String]) = {
     val count = ProductCount.getCountOf("http://www.build.com")
     println(count)
   }

   def getCountOf(url:String):Int={
     val config: Config = Messages.getConfigFor(url)
     val homePageContent=curl(config.seedUrls)
     val headCategories: Elements = Jsoup.parse(homePageContent).select(config.headListCSSSelector)
     //println("Headcategories list:" + headCategories)
     val itr: util.ListIterator[Element] = headCategories.listIterator
     var headCategoriesList = List[String]()
     while(itr.hasNext)
     {
       val relativeUrl = itr.next().attr("href")
       headCategoriesList = resolveUrl(relativeUrl, url) :: headCategoriesList
     }

     headCategoriesList = headCategoriesList.filter(_!="")
     println("Head list output: " + headCategoriesList)
     val allListingPages: List[(String, Int)] =headCategoriesList.flatMap(getListingPage(config)) ++ headCategoriesList.map(a=>(a,checkIfListing(config)(a))).filter(_._2._1).map(a=> (a._1, a._2._2))
     val productCounts = allListingPages.foldRight("", 0)((a,b) => ("", a._2 + b._2))._2
     productCounts
   }

    def getListingPage(config: Config)(url: String): List[(String, Int)] = {
     val subCategoryContent=curl(url)

      (config.subCategoryCSSSelector, subCategoryContent) match {
        case (_,"")=>{
          println("no content")
          List(("",0))
        }
        case (Nil,_)=>{
          println("No subcategories!")
          List(("",0))
        }
        case (x::xs,_) => {
          if(config.subCategoryCSSSelector.head=="") {println("No subctegory css");List(("",0))}
          else {
            val subCategoriesElements: List[Elements] = config.subCategoryCSSSelector.map(Jsoup.parse(subCategoryContent).select)
            subCategoriesElements.filter(_.size > 0).flatMap(getListingPagesForEach(url, config))
          }
        }

      }
    }

    def getListingPagesForEach(url: String, config: Config)(subCategories: Elements): List[(String, Int)] = {
      val itr: util.ListIterator[Element] = subCategories.listIterator
      var subCategoriesList = List[String]()
      while (itr.hasNext) {
      val relativeUrl = itr.next().attr("href")
      subCategoriesList = resolveUrl(relativeUrl, url) :: subCategoriesList
      }
      subCategoriesList = subCategoriesList.filter(_ != "")
      val (listing, non_listing) = subCategoriesList.map(a => (a, checkIfListing(config)(a)))
      .partition(_._2._1)

      val s = listing.map(a=> (a._1, a._2._2))
      val t: List[String] = non_listing.map(_._1)

      listing.map(a=> (a._1, a._2._2)) ++ non_listing.map(_._1).flatMap(getListingPage(config))
    }
  def checkIfListing(config:Config)(url: String): (Boolean,Int) = {
    val pageContent=curl(url)

    val count = getCount(config, pageContent)
    if(count>0) {
      println(url + " :" + count)
      totalcount = totalcount+ count
      println("Total count: " + totalcount)
      (true, count)
    }
    else {
      (false,0)
    }
  }

  def getCount(config: Config, pageContent: String): Int = {
    //println("config.ListingPageProductCount=>"+config.ListingPageProductCount)
    (config.ListingPageProductCount,pageContent) match {
      case (_,"") => {
        println("no content")
        0
      }
      case ("",_) => {
        //println(pageContent.length)
        val itemsPerPage = Try(Jsoup.parse(pageContent).select(config.itemsPerPage).get(0).text().toInt).getOrElse(0)
        val numberOfPages = Try(Jsoup.parse(pageContent).select(config.totalPages).last().text().toInt).getOrElse(0)
        println(itemsPerPage)
        println(numberOfPages)
        itemsPerPage * numberOfPages


      }
      case (_,_) => {
        //println(config.productCountRegex)
        val html = Jsoup.parse(pageContent).select(config.ListingPageProductCount).text()
        Try((config.productCountRegex.r findAllMatchIn  html).toList.mkString.toInt).getOrElse(0)
      }
    }
  }

   def curl(url: String): String = {
     val content = Try(Source.fromURL(url,"iso-8859-1").mkString).getOrElse("")
     content.length match {
       case 0 => println("Unable to fetch URL: " + url)
       case _ => println("Fetching: "+url)
     }
     Thread.sleep(100)
     content
   }


  def resolveUrl(relativeUrl: String, url: String): String = {
    val rel: Option[URL] = Try(new URL(relativeUrl)).toOption
    rel match {
      case Some(a) if new URL(relativeUrl).getHost == new URL(url).getHost => relativeUrl
      case None if relativeUrl != "" => new URL(url).getProtocol + "://" + new URL(url).getHost + relativeUrl
      case _ => ""
    }
  }
 }
