package com.gree.nlp_match

import java.util.HashMap
import scala.collection.JavaConversions._//Scala与Java的隐式转换
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import com.gree.nlp_match.commonFunction.{Loadjson, regJson, regOption,match_text,match_ID}
import scala.collection.mutable.ArrayBuffer



object Str_Macth {
  def main(args: Array[String]):Unit={
//    var a=new HashMap[String, String]()
//    a.put("C1", "主板故障")
//    a.put("C2", "芯片故障")
//    var b=new HashMap[String, String]()
//    b.put("M1", "更换调整吹塑管")
//    b.put("M2", "更换室内机控制板")
//    var c=new HashMap[String, String]()
//    c.put("P2", "主板 Z71351H")
//    c.put("P3", "排水软管")
//
//
//    var Match = Map[String,String]()
//
//    var Map_matter_part = Map[String,String]()
//    var Map_cause_matter = Map[String,String]()
//    var Map_cause_part = Map[String,String]()
//
//    Map_matter_part = match_text(b,c)
//    println(Map_matter_part)
//    Map_cause_matter = match_ID(a,b)
//    println(Map_cause_matter)
//
//    var Count = 1
//    Match += ("hdvjklasvjb" -> "")
//
//    for ((k,v) <- Map_cause_matter) {
//      //val temp = v.split("\\+\\+", 0)
//      val temp = if(v!=" ") v.split("\\+\\+", 0) else Array(" ")
//      var str = ""
//      if (temp(0) != " "){
//        for (x <- temp) {
//          str += "("+ a(k) + "---"+ Map_matter_part(x.split(" ").mkString) + ")"
//        }
//      }
//      else{
//        str = "()"
//      }
//      Match += ("hdvjklasvjb" -> (Match("hdvjklasvjb")+s"${Count}、${str}"))
//      Count += 1
//    }
//    println(Match)

    //val a = "a-b-c".split("-", 0)
//    val a = "Ma++Md++".split("\\+\\+",0)
//    println(a.length)
//    for ( x <- a ) {
//      println( x )
//    }

//    var a = ArrayBuffer[Array[Any]]()
//    a += Array(1,2)
//    println(a)
//    a += Array(3,4,5)
//    println(a(1)(2))
//    var simil=new HashMap[String, String]()
//    simil.put("hh","5")
//    simil.put("xasx","6")
//    simil.put("aewx","7")
//
////
////   // println(simil)
//    val ss = Map("hh" -> "sss", "kk" -> "mmm", "jj" -> "6tr")
//    val sse = Map("hh" -> "wes", "xas" -> "byt")
//    val gg = simil.map{
//      case (k, v) => k -> {
//        val ve = sse.getOrElse(k, "") //在这里可以调用sse
//        v + ve
//      }
//    }
//    println(gg)
    val names = List("Alice","james","Apple")
    val char = names.flatMap(x =>x.toUpperCase())
    println(char)

    val config_str = Loadjson()
    val sparkConf = regJson(config_str.get("Spark"))
    val readConf = regJson(config_str.get("Read"))
    val readSql = regJson(config_str.get("ReadSql"))

    val conf = new SparkConf().setAppName(regOption(sparkConf.get("appName")))
    for((k,v)<-sparkConf)
      conf.set(k,v.toString)

    val readOptions = new HashMap[String, String]()
    for((k,v)<-readConf)
      readOptions.put(k,v.toString)


    val spark = SparkSession.builder().config(conf).master("local").getOrCreate()

    val MasterID = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("MasterID"))).load()
    val CauseTable = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("CauseTable"))).load()
    val PartTable = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("PartTable"))).load()
    val MatterTable = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("MatterTable"))).load()

//    val table1 = MasterID.join(CauseTable, Seq("GrowKey"), "left")
//    val table2 = table1.join(PartTable, Seq("GrowKey"), "left")
//    val Main_table = table2.join(MatterTable, Seq("GrowKey"), "left")
//    Main_table.show()


    //val Growkey = Main_table

    //将ID存入Array
    val id: Array[String] = for (row <- MasterID.select("GrowKey").collect()) yield row.getString(0)
//    import spark.implicits._
    for (i<- 0 until id.length){

      val wx_cause = CauseTable.where(s"GrowKey='${id(i)}'")
      val wx_part = PartTable.where(s"GrowKey='${id(i)}'")
      val wx_matter = MatterTable.where(s"GrowKey='${id(i)}'")


      var map_cause = new HashMap[String, String]()
      for (row <- wx_cause.select("XXYYGUID","故障描述").collect()) yield map_cause.put(row.getString(0),row.getString(1))

      var map_part = new HashMap[String, String]()
      for (row <- wx_part.select("WXJSMXGUID","旧配件名称").collect()) yield map_part.put(row.getString(0),row.getString(1))

      var map_matter = new HashMap[String, String]()
      for (row <- wx_matter.select("WXXMGUID","描述").collect()) yield map_matter.put(row.getString(0),row.getString(1))

//      wx_cause.foreach(
//        line=>{
//          val a = line.getAs[String]("XXYYGUID")
//          val b = line.getAs[String]("故障描述")
//          map_cause.put(a,b)
//      }
//      )

      var Match = Map[String,String]()

      //遍历values并合并成一个字符串
//      var ax = map_part.values.toArray.mkString(",")
//      println(ax)
//      if (ax.length != 0){
//        for(i <- 0 until ax.length)
//          println(ax(i))
//      }
      var Map_matter_part = Map[String,String]()
      var Map_cause_matter = Map[String,String]()
      var Map_cause_part = Map[String,String]()

      if(map_cause.isEmpty){
        if(map_matter.isEmpty){
          val Values = map_part.values.toArray.mkString(",")
          Match += (s"${id(i)}" -> s"1、更换配件：${Values}")
        }
        else{
          Map_matter_part = match_text(map_matter,map_part)
          var Count = 1
          Match += (s"${id(i)}" -> "")
          for ((k,v) <- Map_matter_part){
            Match += (s"${id(i)}" -> (Match(s"${id(i)}")+s"${Count}、${v}"))
            Count += 1
          }
        }
      }
      else{
        if(map_matter.isEmpty){
          Map_cause_part = match_text(map_cause,map_part)
          var Count = 1
          Match += (s"${id(i)}" -> "")
          for ((k,v) <- Map_cause_part){
            Match += (s"${id(i)}" -> (Match(s"${id(i)}")+s"${Count}、${v}"))
            Count += 1
          }
        }
        else{
          Map_matter_part = match_text(map_matter,map_part)
          Map_cause_matter = match_ID(map_cause,map_matter)
          var Count = 1
          Match += (s"${id(i)}" -> "")
          for ((k,v) <- Map_cause_matter) {
            //val temp = v.split("\\+\\+", 0)
            val temp = if(v!=" ") v.split("\\+\\+", 0) else Array(" ")
            var str = ""
            if (temp(0) != " "){
              for (x <- temp) {
                str += "("+ map_cause(k) + "---"+ Map_matter_part(x.split(" ").mkString) + ")"
              }
            }
            else{
              str = "()"
            }
            Match += (s"${id(i)}" -> (Match(s"${id(i)}")+s"${Count}、${str}"))
            Count += 1
          }
        }
      }
      println(Match)

    }
  }
}



