package org.spark.practice

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StructType,StructField,StringType,DoubleType,LongType}
import org.apache.spark.sql.functions.{avg,min,max,sum,trim,translate,instr,upper}
import org.apache.spark.sql.Column

/* This program is demonstration of sql function provided by spark
 * Refer below url for API doc of spark Scala
 * https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.functions$
 * 
 */

object SparkSQLFuncDemo {
  def main(args:Array[String])
  {
    
    //create spark session
    
    val master = "local"
    val spark = SparkSession
    .builder
    .master(master)
    .appName("Dataframe Demo")
    .getOrCreate()
 
    // Change log level avoid unrequired logs so as to have better visibility to out output.
    spark.sparkContext.setLogLevel("ERROR")
 
    val schemaNew = StructType(
        StructField("auctionid",LongType,true)::
        StructField("bid",DoubleType,true)::
        StructField("bidtime",DoubleType,true)::
        StructField("bidder",StringType,true)::
        StructField("bidderrate",StringType,true)::
        StructField("openbid",DoubleType,true)::
        StructField("price",DoubleType,true)::
        StructField("item",StringType,true)::
        StructField("auction_type",StringType,true)::Nil
        )

     val df4 =spark.
     read.
     schema(schemaNew).
     option("header","true")
    .csv("resources/ebay_data.csv")
     
     df4.printSchema()
     df4.show(5)
     
     
     // Numeric functions : avg,min,max,sum,
     df4.select(avg(df4.col("price"))).show()
     df4.select(min(df4.col("price"))).show()
     df4.select(max(df4.col("price"))).show()
     
     // Another way is without using col function
     df4.select(avg(df4("price"))).show()
     
     /*Thus, you can access column two ways
      * 1. using col function:  df.col("colname")
      * 2. without using col function:  df("colname")
      */
     
     
     // String functions: trim,translate,instr,upper
      df4.select(trim(df4.col("item"))).show()
      df4.select(upper(df4("item"))).show()
      df4.select(instr(df4("item"),"a")).show()
      df4.select(translate(df4("item"),"abc","pqr")).show()
      
    
    println("-----End-----")
  }
}