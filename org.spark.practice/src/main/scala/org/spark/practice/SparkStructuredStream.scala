package org.spark.practice

/* ****************************************************************************
 * @Auther : Tanaji Sutar
 * @Date	 : 26-07-2020
 * @Desc	 : This is demo of spark Streaming Context:StructuredStream
 * 1. We need to add dependency in maven : spark-streaming_2.12
 * 2. We need to give folder and add the file to it after starting spark job.
 *  Note that files added prior to starting jobs are not considered
 * 4.Always consider the file size and time given in second to process it.
 * 5.copy the files to directoy:resources/structStreamFIles after job start.
 * 6.Need to pass folder, no need of / at the end
 * 6.While reading, we can pass option like .option("maxFilesPerTrigger", 2)
 * 	 Which will take specified number of files only at a time(per trigger)
 * 7.No need to import Spark Streaming Context
 * 8.Use readStream and writeStream methods of spark session
 * 9.Need to add .start()
 * 10.Need to import sql.streaming.OutputMode
 * 11. Need to  use .awaitTermination() method at the end.
 * 12. transformation must be before applying .writestream
 * 13.Sample output:
-------------------------------------------
Batch: 0
-------------------------------------------
+---+-----------------+------+
| id|             name|   pin|
+---+-----------------+------+
|  1|				'atpadi'	|415301|
|  2|         'sangli'|415415|
|  3|       'islampur'|415409|
|  4|           'jath'|416404|
|  5|          'walwa'|416221|
+---+-----------------+------+

******************************************************************************/

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{StructType,StructField,IntegerType,StringType}
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.functions._
object SparkStructuredStream {
    def main(args: Array[String])
  {  
    // Define Conf
    val conf =  new SparkConf()
    .setMaster("local[2]")
    .setAppName("Spark Streaming Demo 1")
    
    // Create Streaming Context
    // val ssc = new StreamingContext(conf = conf, Seconds(15))
    
    val spark = SparkSession
    .builder
    .master("local")
    .getOrCreate()
    
    spark.sparkContext.setLogLevel("ERROR")
    
     
    
    // Define schema
    val inputschema = StructType(
        StructField("id",IntegerType,true) ::
        StructField("name",StringType,true)::
        StructField("pin",IntegerType,true)::Nil)
    
    

    
    // -- Processing Logic ----
    val inputFilePath = "resources/structStreamFIles"  
    
    
     val df1 = spark
      .readStream
      .format("csv")
      .option("maxFilesPerTrigger", 2)
      .schema(inputschema)
      .load(inputFilePath)
     
      
      val df2 = df1.filter("name != 'pune'")
      
      println("*** Write Spark Structuired Stream to consol ***")
      // Create writeStream and use .start() menthod and queryName
      val qryResult = df2.writeStream
      .format(source ="console")
      .queryName(queryName ="filterQuery")
      .outputMode(OutputMode.Update)
      .start()
  
     println("*** Apply Transformations on Spark Structuired Stream***")
      
      /* Transformations on Spark Structured streaming
       * 1.select
       * 2.filter and where and so on
       * Note : transformation must be before applying .writestream 
       */
      
       val qryResult2 = df2
       .filter("name != 'pune'")
       .select("name","pin")
        .writeStream
        .format(source ="console")
        .queryName(queryName ="StructTransformQuery")
        .outputMode(OutputMode.Update)
        .start()
      
        
     println("*** Apply Aggregations***")
      
     // Apply Aggregations like count using groupBy and agg methods
      
     val aggregatedData =  df2
      .groupBy("id")  
      .agg(count("name"))
        
     val qryResult3 = aggregatedData
        .writeStream
        .format(source ="console")
        .queryName(queryName ="AggregatedData")
        .outputMode(OutputMode.Update)
        .start()  
         
        // Wait till user terminate job manually.
       qryResult.awaitTermination()
        
  }
}