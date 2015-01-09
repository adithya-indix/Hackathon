package Experiment

import scala.io.Source

/**
 * Created by indix on 7/1/15.
 */
object ProductsWithoutVariants {

  def main(args: Array[String]): Unit = {

    def removeHash(url: String) = url.substring(0, url.indexOf('#'))

    println(Source.fromInputStream(getClass.getResourceAsStream("campmor_urls.csv")))
    val c: List[String] = Source
      .fromInputStream(getClass.getResourceAsStream("campmor_urls.csv"))
      .getLines()
      .toList

    val k = c.map(removeHash)
      .distinct
      .size

    println(k)

  }
}
