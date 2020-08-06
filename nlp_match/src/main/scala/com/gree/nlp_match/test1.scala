package com.gree.nlp_match

import java.util.HashMap

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import com.gree.nlp_match.commonFunction.{parseArgs, regJson, regOption}

import scala.io.Source
import scala.util.parsing.json.JSON

object test1 {
  def main(args: Array[String]): Unit = {
    val fileName = parseArgs(Array("E:\\nlp_match\\sparkConf1.json"))
    val source = Source.fromFile(fileName).mkString
    val aaa = JSON.parseFull(source)
    val configStr = regJson(aaa)
    val sparkConf = regJson(configStr.get("Spark"))
    val writeConf = regJson(configStr.get("Write"))
    val readConf = regJson(configStr.get("Read"))
    val readSql = regJson(configStr.get("ReadSql"))

    val conf = new SparkConf().setAppName(regOption(sparkConf.get("appName")))
    for((k,v)<-sparkConf)
      conf.set(k,v.toString)

    val writeOptions = new HashMap[String, String]()
    for((k,v)<-writeConf)
      writeOptions.put(k,v.toString)

    val readOptions = new HashMap[String, String]()
    for((k,v)<-readConf)
      readOptions.put(k,v.toString)

    val spark = SparkSession.builder().config(conf).master("local").getOrCreate()

    val BaseMateriel = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("BaseMateriel"))).load()
    val BOM_All = spark.read.format("jdbc").options(readOptions).option("dbtable",regOption(readSql.get("BOM_All"))).load().cache()
    BaseMateriel.show()
    BaseMateriel.foreach {
      line =>
        val col1 = line.getAs[String]("EMPNUM")
        println(col1)
    }

    //
//        var spreadBom = BOM_All.join(BaseMateriel ,
//          BaseMateriel("ERPID")===BOM_All("ERPID_BOM")
//            and BaseMateriel("CheckedMaterielCode")===BOM_All("MaterielCode_BOM"))
//          .groupBy("ERPID","ManufacturingCode_BOM","ManufacturingName_BOM","ManufacturingGroup_BOM",
//            "MaterielCode_BOM","MaterielCode_BOM_ID","MaterielGroup_BOM","MaterielName_BOM","Levels").count()
//          .selectExpr("ERPID"
//            ,"ManufacturingCode_BOM as zzwl"
//            ,"ManufacturingGroup_BOM as zzwlz"
//            ,"ManufacturingName_BOM as zzwlmc"
//            ,"MaterielCode_BOM as wl"
//            ,"MaterielGroup_BOM as wlz"
//            ,"MaterielName_BOM as wlmc"
//            ,"Levels +1 as num"
//            ,"MaterielCode_BOM_ID as basemateriel"
//            ,"MaterielGroup_BOM as basemz"
//            ,"MaterielName_BOM as basemc"
//          )

    //    var bomResult = spreadBom.withColumn("LUP",current_timestamp)

    //    spreadBom.write.mode("overwrite").format("jdbc").options(writeOptions).save()


    spark.close()
    spark.stop()

  }
}