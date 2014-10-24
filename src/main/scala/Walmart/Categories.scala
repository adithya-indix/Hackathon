package Walmart



import scala.io.Source
import scala.util.parsing.json.JSON

object Categories {

  def categories: List[String] = {
    val str = curl("http://api.walmartlabs.com/v1/taxonomy?apiKey=3s8j2a6y72f2vtgyswy8sert")
    val json = JSON.parseFull(str)
    json match {
      case Some(categoriesMap: Map[String, Any]) => {
        val categoriesListJson = categoriesMap.getOrElse("categories", Nil).asInstanceOf[List[Map[String, Any]]]
        val categoriesList = categoriesListJson.map(jsonToCategory)
        val categoryIds = parentCategoryIds(categoriesList)
        categoryIds
      }
      case _ => Nil
    }
  }

  def curl(url: String): String = {
    Source.fromURL(url).mkString
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

  def main(args: Array[String]): Unit = categories.map("http://api.walmartlabs.com/v1/feeds/items?apiKey=3s8j2a6y72f2vtgyswy8sert&categoryId="+_).map(println)
}

case class Category(id: String, name: String, path: String, children: List[Category])
