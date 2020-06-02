package org.spark.practice

import org.apache.spark.sql.SparkSession

object CreateMultipleSessions {
def main(args:Array[String])
{
  val spark1 = SparkSession.builder().appName("session1").master("local").getOrCreate()
  val spark2 = SparkSession.builder().appName("session2").master("local").getOrCreate()
  
  val df1 = spark1.read.option("header","true").csv("resources/ebay_data.csv")
  val df2 = spark2.read.option("header","true").csv("resources/ebay_data.csv")
  
  println("Dispaly DF from first session")
  df1.show(5)
  println("Dispaly DF from first session")
  df2.show(3)
  
  println("Successfully created two sessions of spark!!!!!")
  print("-----END------------------------------------------")
  }
}
