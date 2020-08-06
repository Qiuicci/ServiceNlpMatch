package com.gree.nlp_match
import com.gree.nlp_match.{SimHash,SimhashIndex}

object SimhashTest {
  def main(args: Array[String]):Unit={
    val idfPath = "E:\\idf.utf8"
    val stopwordsPath ="E:\\stop_words.utf8"
    //val idfPath = getClass.getResource("E:\idf.utf8").getPath
    //val stopwordsPath = getClass.getResource("E:\stop_words.utf8").getPath
    val simhash = SimHash(idfPath, stopwordsPath)

    val topN = 15

    val index = SimhashIndex()


    val s1 = "更换调整吹塑管就行了"
    val s2 = "更换了室内机控制板"
    val s3 = "主板"
    val s4 = "排水软管"

    val k1 = simhash.extract(s1, topN)
    val k2 = simhash.extract(s2, topN)
    val k3 = simhash.extract(s3, topN)
    val k4 = simhash.extract(s4, topN)
    println(s"$s1 \n$k1")
    println(s"$s2 \n$k2")
    println(s"$s3 \n$k3")
    println(s"$s4 \n$k4")

    val d1 = index.distance(simhash.getHash(s1, topN), simhash.getHash(s3, topN))
    val d2 = index.distance(simhash.getHash(s1, topN), simhash.getHash(s4, topN))
    val d3 = index.distance(simhash.getHash(s2, topN), simhash.getHash(s3, topN))
    val d4 = index.distance(simhash.getHash(s2, topN), simhash.getHash(s4, topN))
    println(s"$s1 与 $s3 的距离是:$d1")
    println(s"$s1 与 $s4 的距离是:$d2")
    println(s"$s2 与 $s3 的距离是:$d3")
    println(s"$s2 与 $s4 的距离是:$d4")
    println(simhash.getHash(s1, topN))
    println(simhash.getHash(s2, topN))
    println(simhash.getHash(s3, topN))
    println(simhash.getHash(s4, topN))
  }

}
