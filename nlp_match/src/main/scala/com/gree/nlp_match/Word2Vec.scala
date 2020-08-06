//package com.gree.nlp_match
//
//import java.util.HashMap
//import org.ansj.splitWord.analysis.ToAnalysis
//import org.ansj.library.DicLibrary
//import org.ansj.recognition.impl.StopRecognition
//
//import com.gree.nlp_match.commonFunction.{Loadjson, regJson, regOption}
//
//import org.apache.spark.ml.feature.Word2Vec
//import org.apache.spark.{SparkConf,SparkContext}
//import org.apache.spark.sql.{SparkSession,Column,SQLContext}
//import org.apache.spark.sql.types._
//
//
//object Word2Vec {
//  def main(args: Array[String]):Unit={
//    val config_str = Loadjson()
//    val sparkConf = regJson(config_str.get("Spark"))
//    val readConf = regJson(config_str.get("Read"))
//    val readSql = regJson(config_str.get("ReadSql"))
//
//    val conf = new SparkConf().setAppName(regOption(sparkConf.get("appName")))
//    for((k,v)<-sparkConf)
//      conf.set(k,v.toString)
//
//    val readOptions = new HashMap[String, String]()
//    for((k,v)<-readConf)
//      readOptions.put(k,v.toString)
//
//
//    val spark = SparkSession.builder().config(conf).master("local").getOrCreate()
//
//    val WX_context = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("WX_context"))).load()
//    WX_context.show()
//    import spark.implicits._
//    val filter = new StopRecognition()
//    filter.insertStopNatures("w"," ",null)  //过滤标点和null
//    filter.insertStopWords(" ")
//    val ss = WX_context.map(t => ToAnalysis
//      .parse(t(1).toString)
//      .recognition(filter)
//      .toStringWithOutNature()
//      .split(" "))
////    ss.collect().foreach(println)
//    var sc = new SparkContext(conf)
//    val sqlContext = new SQLContext(sc)
//    val df = sqlContext.createDataFrame
//    val word2Vec = new Word2Vec()
//      .setInputCol("value")
//      .setOutputCol("result")
//      .setVectorSize(20)
//      .setMinCount(0)
//    val model = word2Vec.fit(ss)
////    val res = model.transform(ss)
////    ss.rdd.foreach(println)
//    model.
//    model.findSynonyms("主板",10).collect().foreach(println)
////    import spark.implicits._
////    val c = WX_context.rdd
////
////    val filter = new StopRecognition()
////    val splitWordRdd = c.map(line => {
////      val str = line.getAs[String]("context")
////      val splited = ToAnalysis.parse(str)
////            .recognition(filter)
////            .toString(",")
////            .split(",").toList
////      splited
////    })
////    //c.take(10).foreach(println())
////    case class WX_data(GowKey: String, context: String)
////    val splitWordDF = splitWordRdd.map(attributes => WX_data(attributes(0),attributes(1))).toDF()
////    splitWordDF.show()
////    val schemaString = "GrowKey context"
////    val fields = schemaString.split(" ").map(fieldname => StructField(fieldname,StringType,nullable = true))
////    val schema = StructType(fields)
////    val DF = spark.createDataFrame(splitWordRdd,schema)
//
//
//
//
//  }
//}
