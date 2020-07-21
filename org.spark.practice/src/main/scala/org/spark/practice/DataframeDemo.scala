package org.spark.practice


import org.apache.spark.sql.SparkSession
import scala.collection.mutable.Builder
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row
import java.text.Format
import org.apache.spark.sql.SparkSession

object DataframeDemo {
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
     
    // 1.Create Dataframe from RDD 
    print("1. Create Dataframe from RDD")
    val sc = spark.sparkContext
    val arr = Array(1,2,3,4,5,6)
    val rdd1 = sc.parallelize(arr, 2)
    // Create Schema 
    val schema = StructType(
        StructField("Numbers",IntegerType,false) ::Nil
        )
    
    //convert each item of RDD into rows
     val rowRDD = rdd1.map(line=>Row(line))
    
     //create Dataframe
     val df = spark.createDataFrame(rowRDD, schema)
     
     //print schema
     println("Print Schema of the dataframe")
     df.printSchema()
    
     // Show top 5 rows
     println("show top 5 rows of dataframe")
     df.show(5)
     
     //Create dataframe from csv 
     print("Dataframe from csv")
     val df2 = spark.read
     .format("csv")
     .option("header","true")
     .option("inferSchema","False")
     .option("delimeter",",")
     .load("resources/ebay_data.csv")
     
     //Note : If we put inferSchema true, spark will read whole file and then finalize
     //It will cause time and memory consumption.
     //To have faster processing, its always better to provide schema manually.
     println("Show schema")
     df2.printSchema()
     println("Show first 5 rows")
     df2.show(5)
     
      /*
     print("Dataframe from Databrics csv")
     val df3 = spark.read
      .format("csv")
     .option("header","true")
     .option("inferSchema","true")
     .load("resources/ebay_data.csv")

      df3.printSchema()
     */
     
     
     print("Dataframe with schema provided")
     
    // :: is syntax of scala for list and at teh end add " Nil) "
     // Also it needs import org.apache.spark.sql.types._ to be imported
 
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

      /*
       * Syntax 1
     val df4 = spark.read
     .format("csv")
     .schema(schemaNew)
     .option("header","true")
     .option("inferSchema","False")
     .load("resources/ebay_data.csv")
     */
     
        //Syntax 2
     val df4 =spark.
     read.
     schema(schemaNew).
     option("header","true")
    .csv("resources/ebay_data.csv")
     
     df4.printSchema()
     df4.show(5)

     
     //Dataframe operations
     
     //1.Get columsn
     print(df4.columns)
     
     //Describe
     df4.describe("price").show()
     
     //dtypes
     print(df4.dtypes)
     
     //head 
     df4.head(3).foreach(println)
     
     //select
     df4.select("bid","price").head(4).foreach(println)
     
     //filter
     df4.select("bid","price").filter("price>100").show(6)
     
      //where 
     df4.select("bid","price").where("price>100").show(6)   
     
     //groupBy
    val df5 = df4.select("bid","price").where("price>100")
     
    df5.groupBy("bid").mean("price").show()
    df5.groupBy("bid").max("price").show()
    df5.groupBy("bid").min("price").show()
    df5.groupBy("bid").avg("price").show()
    
    //reduce
    //df5.groupBy("bid").agg((x,y)=>x*y).show(4)
    
    // temp tables on dataframe
    // NOTE: RegisterTempTable is Spark 1.X and deprecated in spark 2X
    //Instead of it, use createOrReplaceTempView
    df4.registerTempTable("mytable")
    println("registered temp table and now firing SQL queries.")
    spark.sql("select * from mytable limit 7").show()
    
    
    //Scope is in session only.Once session closes, table get removed.
    df4.createTempView("mytable2")
    println("Create temp view and now firing SQL queries.")
    spark.sql("select * from mytable limit 3").show()   
    
    // If you are creating/updating same table multiple times in same sessions
    //then use createOrReplace
    
    
    //If you want to use this table across multiple sessions,
    //use createGlobalTable
    
    df4.createGlobalTempView("myGlobalTable")
    println("Create temp view and now firing SQL queries.")
    spark.sql("select * from myGlobalTable limit 3").show()  
    
    
    
    println("-----End-----")
  }
}