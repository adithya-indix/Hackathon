package Hackathon

import scala.io.Source
import scala.util.matching.Regex

/**
 * Created by indix on 8/1/15.
 */
object Messages {


  def getConfigFor(homePage: String): Config  = {
    val data = Source.fromInputStream(getClass.getResourceAsStream("/config.csv")).getLines().toList.tail

    val config: Config = data.map(toConfig)
      .filter(_.seedUrls==homePage)
      .head

    config
  }

  case class Config(seedUrls: String, headListCSSSelector:String,subCategoryCSSSelector:List[String],listingPageCSSSelector:String,
                    ListingPageProductCount: String, productCountRegex: String, itemsPerPage: String, totalPages: String)

    def toConfig(str:String): Config={
      val tokens = str.split(",").toList.map(a=>{
        if(a=="dummy") ""
        else a
      })
      Config(tokens(0), tokens(1), tokens(2).split("/").toList, tokens(3), tokens(4), tokens(5), tokens(6), tokens(7))
    }


}
