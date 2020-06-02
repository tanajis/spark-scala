package org.spark.ebayAnalysis
import org.apache.spark.sql.SparkSession
import scala.reflect.io.Path
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.functions._


// define main method (Spark entry point)
object Perform {
  def main(args: Array[String]) {
 
   // initialise spark context
    
  val spark = SparkSession
    .builder()
    .master("local")
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()
 
    
   val srcDF = spark.read.format("com.databricks.spark.csv")
    .option("delimiter", ",")
    .option("header", "true")
    .load("src/ebay_data.csv")

    print("1.----Schema of the source Data----")
    srcDF.printSchema()
    
    print("2.----First 10 rows----")

    srcDF.show(10)
    
    print("3.How many auctions were held?")
    srcDF.select("auctionid").distinct.count
    
    print("4.How many auctions were held?")
    srcDF.select("auctionid").distinct.count
    
    print("5.How many bids per item?")
    srcDF.groupBy("auctionid", "item").count.show
    
     print("6.What's the min,avg and max number of bids per item?") 
    srcDF.groupBy("item", "auctionid").count.agg(min("count"), avg("count"),max("count")).show

    print("7.Get the auctions with closing price > 100") 
    val highprice= srcDF.filter("price > 100")
    highprice.show()
  
    
      // register the DataFrame as a temp table
    srcDF.registerTempTable("auction")
    // How many bids per auction?
    val results = spark.sql(
    "SELECT auctionid, item, count(bid) FROM auction GROUP BY auctionid, item")
    
    // display dataframe in a tabular format
    results.show()
    val results2 = spark.sql("SELECT auctionid, MAX(price) FROM auction GROUP BY item,auctionid")
  results2.show()
  }
}

//https://sparkbyexamples.com/apache-spark-rdd/convert-spark-rdd-to-dataframe-dataset/
