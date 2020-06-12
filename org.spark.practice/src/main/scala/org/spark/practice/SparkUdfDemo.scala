package org.spark.practice

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.Column
import org.apache.spark.sql.types.{StructType,StructField,StringType,DoubleType,LongType}
import org.apache.spark.sql.functions.udf


/* This program is demonstration of Spark UDF function provided by spark
 * Refer below url for API doc of spark Scala
 * Two Steps:
 * 	1.Define udf outside main function of Object
 * 	2.in main function register it as UDF with spark
 *  3. We need to import org.apache.spark.sql.functions.udf
 */

object SparkUdfDemo {
  def main(args:Array[String])
  {
    
    //create spark session
    
    val master = "local"
    val spark = SparkSession
    .builder
    .master(master)
    .appName("Spark UDF Demo")
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
     
     // Register UDF
     println("Register UDF")
    val myFunc1_UDF = udf[Double,Double](myFunc1)
    
    val myFunc2_UDF = udf[String,String](myFunc2)
    
    // param datatypes: two for input, 1 for output 
    val addTwoColumns_UDF = udf[Double,Double,Double](addTwoColumns)
    
    println("Using UDF")
    println("----myFunc1_UDF-----")
    df4.select(myFunc1_UDF(df4("price"))).show()
    
    println("----myFunc2_UDF-----")
    df4.select(myFunc2_UDF(df4("bidder"))).show()
    
    println("----addTwoColumns_UDF-----")
    df4.select(addTwoColumns_UDF(df4("price"),df4("openbid"))).show()
    
    /* Use it in SQL
     * You need to register it with spark SQL.
     * 
     */
    
    df4.createOrReplaceTempView("mytable")
    spark.udf.register("myFunc1_UDF",myFunc1_UDF)
    spark.udf.register("myFunc2_UDF",myFunc2_UDF)
    //spark.sql("Select * from ")
    spark.sql("select myFunc1_UDF(price) as val1, myFunc2_UDF(bidder) as val2 from mytable").show(5)
    
    
    /*
     * You can also directly register to SQL as well as below:
     * You can use it in SQL but 
     * cant use it like df4.select(myFunc1_UDF(CTOF("price")))
     */
   spark.udf.register("CTOF", 
       (
           degreesCelcius: Double) => ((degreesCelcius * 9.0 / 5.0) + 32.0)
       )
   
    spark.sql("select CTOF(price) as price_ctof from mytable").show(5)
    
    
    println("-----End-----")
  }
  
  
  // Define UDF numeric
  def myFunc1(n:Double) :Double = n * 10 
  
  // Define UDF String
  def myFunc2(s:String) :String = s.toLowerCase() 
  
   // Define UDF Two column parameters
  def addTwoColumns(n1:Double,n2:Double) :Double = n1 + n2
}