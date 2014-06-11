package com.indix.bootcamp.parser

import com.indix.bootcamp.models.{Product, Price, Result}
import org.jsoup.nodes.Document

class FlipkartParser extends Parser {
  override def parseProduct(document: Document, pageUrl: String): Product = {
    val title = document.select("h1[itemprop=name]").text()
    val description = document.select("#specifications").text()
    Product(title, description, pageUrl)
  }

  override def parsePrice(document: Document): Price = {
    val listPrice = document.select(".old-price") match {
      case empty if empty.isEmpty => Double.NaN
      case elem => elem.text().split(" ")(1).toDouble
    }
    val salePrice = document.select("meta[itemprop=price]") match {
      case empty if empty.isEmpty => Double.NaN
      case elem => elem.attr("content").toDouble
    }
    (listPrice, salePrice) match {
      case (Double.NaN, Double.NaN) => throw new Exception("unable to parse")
      case _ => Price(listPrice, salePrice)
    }
  }
}

class JabongParser extends Parser {
  override def parseProduct(document: Document, pageUrl: String): Product = {
    val title = document.select("#qa-title-product").text()
    val description = document.select("#description").text()
    Product(title, description, pageUrl)
  }

  override def parsePrice(document: Document): Price = {
    val listPrice = document.select("#product_price") match {
      case empty if empty.isEmpty => Double.NaN
      case elem => elem.text().toDouble
    }
    val salePrice = document.select("#product_special_price") match {
      case empty if empty.isEmpty => Double.NaN
      case elem => elem.text().toDouble
    }
    (listPrice, salePrice) match {
      case (Double.NaN, Double.NaN) => throw new Exception("unable to parse")
      case _ => Price(listPrice, salePrice)
    }
  }
}
