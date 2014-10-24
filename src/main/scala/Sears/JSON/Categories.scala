package Sears.JSON

import java.io.FileWriter

import org.jsoup.Jsoup

import scala.io.Source
import scala.util.Try
import scala.util.parsing.json.JSON


object Categories {

   def verticals: List[String] = {
     val str = curl("http://api.developer.sears.com/v2.1/products/browse/categories/Sears/json?category=&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL")
     val json = JSON.parseFull(str)
     val verticals = json
                         .get.asInstanceOf[Map[String, Any]]
                         .getOrElse("SearchResults", Nil).asInstanceOf[Map[String, Any]]
                         .getOrElse("Verticals", Nil).asInstanceOf[List[Map[String, String]]]
                         .map(_.getOrElse("VerticalName", null))
                         .filter(_!=null)
     verticals
   }

   def parseVerticals(verticals: List[String]): List[String] = {
     val categories: List[(String, String)] = verticals.map(urlEncode)
                                                       .map(category => (category, "http://api.developer.sears.com/v2.1/products/browse/categories/Sears/json?category="+category+"&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL"))
     categories.flatMap(cat => subCategories(cat._1, cat._2))
   }

   def subCategories(category: String, url: String): List[String]= {
     val json = JSON.parseFull(curl(url)).get.asInstanceOf[Map[String, Any]]
     val sub = json.getOrElse("SearchResults", Nil).asInstanceOf[Map[String, Any]]
                                           .getOrElse("NavGroups", Nil).asInstanceOf[List[Map[String, Any]]]
                                           .filter(_.getOrElse("GroupType", null) == "ShopByCategoryGroup").head
                                           .getOrElse("ShopByCategories", Nil).asInstanceOf[List[Map[String, String]]]
                                           .map(_.getOrElse("CategoryName", null))
                                           .map(a => category+"|"+urlEncode(a))
     sub
   }

   def curl(url: String): String = {
     Source.fromURL(url).mkString
   }

   def urlEncode(url: String) = java.net.URLEncoder.encode(url, "UTF-8")

   def watchCategories: List[(String, Int)] = {
     val json = JSON.parseFull(curl("http://api.developer.sears.com/v2.1/products/browse/categories/Sears/json?category=Jewelry|Watches&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL")).get.asInstanceOf[Map[String, Any]]
     val watchLeafCategories = json.getOrElse("SearchResults", Nil).asInstanceOf[Map[String, Any]]
         .getOrElse("NavGroups", Nil).asInstanceOf[List[Map[String, Any]]]
         .filter(_.getOrElse("GroupType" ,"")=="ShopByCategoryGroup").head
         .getOrElse("ShopByCategories", Nil).asInstanceOf[List[Map[String, String]]]
       val catName = watchLeafCategories.map(_.getOrElse("CategoryName", ""))
                                        .filter(_!="")
                                        .map("Jewelry|Watches|"+urlEncode(_))
       val catCount = watchLeafCategories.map(_.getOrElse("AggProdCount", ""))
                                        .filter(_!="")
                                        .map(_.toInt)
     catName zip catCount
   }

   def formProductSearchURL(leafCategory: String, count: Int): List[String] = {
     (0 to count/200).toList.map(c => "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category="+leafCategory+"&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex="+(200*c+1)+"&endIndex="+(200*(c+1)))
   }

   def partNumberFromProductSearchURL(url: String): List[String] = {
     val json = JSON.parseFull(curl(url)).get.asInstanceOf[Map[String, Any]]
     val partNumbers = json.getOrElse("SearchResults", Nil).asInstanceOf[Map[String, Any]]
                           .getOrElse("Products", Nil).asInstanceOf[List[Map[String, String]]]
                           .map(_.getOrElse("Id", Nil).asInstanceOf[Map[String, String]])
                           .map(_.getOrElse("PartNumber", ""))
                           .filter(_!="")
     partNumbers
   }
   def listOfurls: List[String] = List("http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Accessories&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Character&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Chronograph&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Diamond&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Kids&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=201&endIndex=400",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=401&endIndex=600",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=601&endIndex=800",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Ladies&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=801&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=201&endIndex=400",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=401&endIndex=600",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=601&endIndex=800",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=801&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Mens&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1001&endIndex=1200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Pocket+Watches&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Watch+Sets&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&startIndex=1&endIndex=200",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|0%24-25&startIndex=1&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|25%24-50&startIndex=1&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|50%24-100&startIndex=1&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|100%24-500&startIndex=1&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|View%20All%20Watch%20Brands&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|500%24-1500&startIndex=1&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Team%20Sports&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|0%24-50&startIndex=1&endIndex=1000",
       "http://api.developer.sears.com/v2.1/products/browse/products/Sears/json?category=Jewelry|Watches|Team%20Sports&apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL&filter=price|50%24-150&startIndex=1&endIndex=1000"
     )

   def productDetailURLs = {
     val partNumbers = listOfurls.flatMap(partNumberFromProductSearchURL)
     val uniquePartNumbers = partNumbers.toSet.toList
     println(uniquePartNumbers.length)
     uniquePartNumbers.map("http://api.developer.sears.com/v2.1/products/details/Sears/json/"+_+"?apikey=iHiCh0VVyIChkSd2dcYNwZWZGWaLiGiL")
   }
   def jsonToObject(in: Map[String, Any]): ProductRecord = {
     val id = in.getOrElse("Id", Nil).asInstanceOf[Map[String, String]]
     val partNumber = id.getOrElse("PartNumber", "").trim
     val mfgPartNumber = id.getOrElse("MfgPartNumber", "").trim
     val description= in.getOrElse("Description", Nil).asInstanceOf[Map[String, String]]
     val brandName = description.getOrElse("BrandName", "").trim
     val name = description.getOrElse("DescriptionName", "").trim
     val shortDescription = description.getOrElse("shortdescription", "").trim
     val longDescription = description.getOrElse("longdescription", "").trim
     val images = description.getOrElse("Images", Nil).asInstanceOf[Map[String, String]]
     val mainImageUrl = images.getOrElse("MainImageUrl", "").trim
     val addnImages = images.getOrElse("ImageURLs", Nil).asInstanceOf[Map[String, List[String]]]
     val addnImageURLs = addnImages.getOrElse("ImageURL", Nil)
     val price = in.getOrElse("Price", Nil).asInstanceOf[Map[String, String]]
     val minSalePrice = price.getOrElse("SalePrice", "").trim
     val minListPrice = price.getOrElse("RegularPrice", "").trim
     val availability =  in.getOrElse("Availability", Nil).asInstanceOf[Map[String, String]]
     val inStock = availability.getOrElse("InStock", "").trim
     ProductRecord(name, partNumber, mfgPartNumber, brandName, shortDescription, longDescription, mainImageUrl, addnImageURLs, minSalePrice, minListPrice, inStock)
   }
   def main(args: Array[String]): Unit = {
     formTSV()
   }

   def writeToFile(in: String) = {
     val fw = new FileWriter("/home/indix/IdeaProjects/FeedsAPI/src/main/resources/sears_watches2410.csv", true)
     fw.write(in)
     fw.close()
   }

   def formTSV() = productDetailURLs.map(a => writeToFile(getProductDetails(a)))

   def getProductDetails(url: String): String = {
     val json = JSON.parseFull(curl(url))
     val data = json.get.asInstanceOf[Map[String, Any]]
     val record = Try(data.getOrElse("ProductDetail", Nil).asInstanceOf[Map[String, Any]]).toOption
     val responseCode = record match {
       case None => {
         data.getOrElse("statusData", Nil).asInstanceOf[Map[String, String]]
             .getOrElse("ResponseCode", "")
       }
       case Some(r) => {
         r.getOrElse("StatusData", Nil).asInstanceOf[Map[String, String]]
           .getOrElse("ResponseCode", "")
       }
     }
     responseCode match {
       case "0" => {
         val productJson = record.get.get("SoftHardProductDetails")
         productJson match {
           case Some(pro: Map[String, Any]) => {
             jsonToObject(pro).asTSV
           }
           case None => {
             val a = record.get.getOrElse("CollectionProductDetails", Nil).asInstanceOf[Map[String, Any]]
               .getOrElse("Products", Nil).asInstanceOf[Map[String, Any]]
               .getOrElse("Product", Nil).asInstanceOf[List[Map[String, Any]]]
             if(a.length>0) {
               a.map(jsonToObject).map(_.asTSV).mkString
             }
             else ""
           }

         }
       }
       case _ => {
         ""
       }
     }


   }

   def htmlText(str: String) = Jsoup.parse(str).text()

   case class ProductRecord(name: String, partNumber: String, mfgPartNumber: String, brandName: String, shortDescription: String, longDescription: String, mainImageUrl: String, addnImageURLs: List[String], minSalePrice: String, listPrice:String, availability: String) {
     def asTSV = name+"\t"+partNumber+"\t"+mfgPartNumber+"\t"+brandName+"\t"+htmlText(shortDescription)+"\t"+htmlText(longDescription)+"\t"+mainImageUrl+"\t"+addnImageURLs.mkString(",")+"\t"+minSalePrice+"\t"+listPrice+"\t"+availability+"\n"
   }
 }
