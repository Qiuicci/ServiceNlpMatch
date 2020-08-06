package com.gree.nlp_match

import java.io.File
import java.util

import scala.io.Source
import scala.util.parsing.json.JSON
import scala.util.control._
import java.util.HashMap

import com.gree.nlp_match.{SimHash, SimhashIndex}

import scala.collection.JavaConversions._//Scala与Java的隐式转换
import scala.collection.mutable.ArrayBuffer


object commonFunction {

  val idfPath = "E:\\idf.utf8"
  val stopwordsPath ="E:\\stop_words.utf8"

  val simhash = SimHash(idfPath, stopwordsPath)
  val topN = 15

  def Loadjson(): Map[String,Any]={
    val fileName = parseArgs(Array("E:\\nlp_match\\sparkConf1.json"))
    val source = Source.fromFile(fileName).mkString
    val JsonStr = regJson(JSON.parseFull(source))
    JsonStr
  }

  def match_text(Matter:HashMap[String,String],Part:HashMap[String,String]):Map[String,String]={
    var Map_temp = Map[String, String]()
    Map_temp = Matter.map{
      case (k, v) => k -> {
        v + "---"
      }
    }.toMap
    for((k1,v1) <- Part){
      var similarity = Map[String, Int]()
      for((k2,v2) <- Matter){
        val index = SimhashIndex()
        val dis = index.distance(simhash.getHash(v1, topN), simhash.getHash(v2, topN))
        similarity += ((k2+"xx000xx"+k1)->dis)
      }
      val Max_sim = similarity.values.min

      //println(Map_temp)
      //相同key的value相加
      val m = max_match(similarity, Max_sim)
      Map_temp = Map_temp.map{
        case (k, v) => k -> {
          //val ve =m.getOrElse(k, "")
          val ve = if(k == m(0)) Part(m(1))+"++" else ""//Matter->Part
          v + ve
        }
      }
    }
    Map_temp
  }

  def match_ID(Cause:HashMap[String,String],Matter:HashMap[String,String]):Map[String,String]={
    var result = Map[String,String]()
    result = Cause.map{
      case (k, v) => k -> {
        " "
      }
    }.toMap
    for((k1,v1) <- Matter){
      var similarity = Map[String, Int]()
      for((k2,v2) <- Cause){
        val index = SimhashIndex()
        val dis = index.distance(simhash.getHash(v1, topN), simhash.getHash(v2, topN))
        similarity += ((k2+"xx000xx"+k1)->dis)
      }
      val Max_sim = similarity.values.min

      //println(max_match(similarity, Max_sim)(0),max_match(similarity, Max_sim)(1))
      //相同key的value相加
      val m = max_match(similarity, Max_sim)
      result = result.map{
        case (k, v) => k -> {
          val ve = if(k == m(0)) m(1)+"++" else "" //Matter->Part
          v + ve
        }
      }
    }
    result
  }

//  def Cause_Matter_match(Matter:HashMap[String,String],Part:HashMap[String,String]):HashMap[String,String]={
//    sd
//  }


  // 找出最佳匹配
  // 输出为: [MatterID,PartID]
  def max_match(sim: Map[String,Int], max_value:Int):Array[String]={
    var result = new Array[String](2)

    val loop = new Breaks
    loop.breakable{
      for((k,v) <- sim){
        if(v == max_value){
          result(0) = k.split("xx000xx",2)(0)
          result(1) = k.split("xx000xx",2)(1)
          loop.break
        }
      }
    }
    result
  }

//  def matchID_text(a:Map[String,String],b:Array[String]): Map[String,String] = a match{
//    case a.contains(b(0)) => a(b(0))
//  }

  def putWaring(): Unit ={
    println(
      """
        |================================================
        |
        |Please Input a Right Config File !!!
        |
        |""".stripMargin)
    System.exit(1)
  }

  def parseArgs(args: Array[String]): String = {
    var result: String = ""
    println(args.length)
    if (args.length == 0 || ! (new File(args(0))).exists()) {
      putWaring()
    }else{
      result  = args(0)
    }
    result
  }

  def regJson(x:Option[Any]) = x match{
    case Some(map: Map[String,Any]) =>map
  }

  def regOption(x:Option[Any]) = {
    val value: Any = x match {
      case Some(x) => x
      case _ => ""
    }
    val result = value.toString()
    result
  }

  def replaceList(str: Any) ={
    val result = str.toString.replace("List(","").replace(")","")
    result
  }


}
