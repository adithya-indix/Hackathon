/**
 * Created by indix on 21/10/14.
 */

import scala.io.Source
import scala.util.parsing.json.JSON

object Categories {

  def categories: List[String] = {
    val json = curl("http://api.walmartlabs.com/v1/taxonomy?apiKey=3s8j2a6y72f2vtgyswy8sert")
    json match {
      case Some(categoriesMap: Map[String, Any]) => {
        val categories = categoriesMap.getOrElse("categories", Nil).asInstanceOf[List[Map[String, Any]]]
        val allCategories = categories.map(jsonToCategory)
        val categoryIds = parentCategoryIds(allCategories)
        categoryIds
      }
      case None => Nil
    }
  }

  def curl(url: String) = {
    val html = Source.fromURL(url)
    val str = html.mkString
    JSON.parseFull(str)
  }

  def jsonToCategory(categories: Map[String, Any]): Category = {
    val id = categories.getOrElse("id", null).asInstanceOf[String]
    val name = categories.getOrElse("name", null).asInstanceOf[String]
    val path = categories.getOrElse("path", null).asInstanceOf[String]
    val children = categories.getOrElse("children", Nil).asInstanceOf[List[Map[String, Any]]].map(jsonToCategory)
    Category(id, name, path, children)
  }

  def parentCategoryIds(categories: List[Category]): List[String] = {
    categories.map(_.id)
  }

  def main(args: Array[String]) = println(categories)
}

case class Category(id: String, name: String, path: String, children: List[Category])