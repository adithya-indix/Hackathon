package Sears.XML

import java.io.{Serializable, InputStream, FileWriter}

import org.jsoup.Jsoup

import scala.io.Source
import scala.util.Try
import scala.xml.XML


object Categories {

  def fetch(url: String): String = {
    Source.fromURL(url).mkString
  }

  def partNumberFromProductSearchURL(url: String): List[String] = {
    val xml = XML.loadString(fetch(url))
    println("URL Fetched: "+url)
    val partNumbers = (xml \\ "PartNumber").map(_.text).toList
    println("Produced "+partNumbers.length+" part numbers")
    partNumbers
  }
  def seedUrls = List(
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Accessories&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Character&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Chronograph&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Diamond&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Kids&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=201&endIndex=400",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=401&endIndex=600",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=601&endIndex=800",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=801&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=201&endIndex=400",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=401&endIndex=600",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=601&endIndex=800",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=801&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1001&endIndex=1200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Pocket+Watches&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Watch+Sets&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|0%24-25&startIndex=1&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|25%24-45&startIndex=1&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|45%24-100&startIndex=1&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|100%24-500&startIndex=1&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|500%24-1500&startIndex=1&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Team%20Sports&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|0%24-50&startIndex=1&endIndex=1000",
    "http://api.developer.sears.com/v2.1/products/browse/products/Sears/xml?category=Jewelry|Watches|Team%20Sports&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|50%24-150&startIndex=1&endIndex=1000"
  )

  def productDetailURLs = {
    val partNumbers = seedUrls.flatMap(partNumberFromProductSearchURL)
    println("Total part numbers produced: "+partNumbers.length)
    val uniquePartNumbers = partNumbers.toSet.toList
    println("Unique part numbers produced: "+uniquePartNumbers.length)
    uniquePartNumbers.map("http://api.developer.sears.com/v2.1/products/details/Sears/xml/"+_+"?apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&showSpec=true")
  }

  def main(args: Array[String]): Unit = {

    //println(upcFromPartNumber("09505815000"))
    formTSV()
    //println(getProductDetails("http://api.developer.sears.com/v2.1/products/details/Sears/xml/04440250000P?apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&showSpec=true"))
  }

  def writeToFile(in: String) = {
    val fw = new FileWriter("/home/indix/IdeaProjects/FeedsAPI/src/main/resources/sears_watches241106.csv", true)
    fw.write(in)
    fw.close()
  }

  def upcFromPartNumber(partNumber: String) = {
    val upcForPNo = Source.fromInputStream(getClass.getResourceAsStream("/upcs.txt")).getLines().toList
    val upcmap = upcForPNo.map(x => x.split('\t')(0).trim -> x.split('\t')(1).trim)
    val upc = upcmap.find(_._1==partNumber).getOrElse("", "NO_UPC")._2
    println("UPC for part number - " + partNumber + " - "+upc)
    upc
    //println(upcForPNo)
    //""
  }
  def xmlToObject(in: String): ProductRecord = {
    val elem = XML.loadString(in)
    val partNumber = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Id" \ "PartNumber").text
    val mfgPartNumber = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Id" \ "MfgPartNumber").text
    val brandName = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "BrandName").text
    val name = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "DescriptionName").text
    val shortDescription = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "shortdescription").text
    val longDescription = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "longdescription").text
    val mainImageUrl = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "Images" \ "MainImageUrl").text
    val addnImageURLs = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "Images" \ "ImageURLs" \ "ImageURL").map(_.text).toList
    val attributes = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Description" \ "Specifications" \ "Specification" \\ "Attribute").map(_.text).toList
    val minSalePrice = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Price" \ "SalePrice").text
    val minListPrice = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Price" \ "RegularPrice").text
    val inStock = (elem \\ "ProductDetail" \ "SoftHardProductDetails" \ "Availability" \ "InStock").text
    val color = Try(attributes.map(format).map(a=> a.split(":").toList).filter(a=> a(0) == "Band Color").head(1)).toOption match {
      case Some(c) => c
      case None => ""
    }
    ProductRecord(format(name), format(partNumber), format(mfgPartNumber), upcFromPartNumber(partNumber.replace("P","")), format(brandName), format(shortDescription), format(longDescription), format(mainImageUrl), addnImageURLs.map(format), format(minSalePrice), format(minListPrice), format(inStock), format(color))
  }

  def format(str: String) = {
    Jsoup.parse(str.replace("<![CDATA[", "").replace("]]>", "").trim).text()
  }
  def formTSV() = productDetailURLs.map(a => writeToFile(getProductDetails(a)))

  def getProductDetails(url: String): String = {
    val xml = fetch(url)
    println("URL Fetched: "+url)
    val parsedData = xmlToObject(xml)
    println("URL Parsed: "+url)
    parsedData.asTSV
  }

  def htmlText(str: String) = Jsoup.parse(str).text()

  case class ProductRecord(name: String, partNumber: String, mfgPartNumber: String, upc: String, brandName: String, shortDescription: String, longDescription: String, mainImageUrl: String, addnImageURLs: List[String], minSalePrice: String, listPrice:String, availability: String, color: String) {
    def asTSV = name+"\t"+partNumber+"\t"+mfgPartNumber+"\t"+upc+"\t"+brandName+"\t"+shortDescription+"\t"+longDescription+"\t"+mainImageUrl+"\t"+addnImageURLs.mkString(",")+"\t"+minSalePrice+"\t"+listPrice+"\t"+availability+"\t"+color+"\n"
  }
}
