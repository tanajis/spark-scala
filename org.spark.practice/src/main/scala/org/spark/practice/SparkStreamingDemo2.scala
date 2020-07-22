
/* ****************************************************************************
 * @Auther : Tanaji Sutar
 * @Date	 : 22-07-2020
 * @Desc	 : This is demo of spark Streaming Context:textFileStream
 * 1. We need to add dependency in maven : spark-streaming_2.12
 * 2. We need to give folder and add the file to it after starting spark job.
 *  Note that files added prior to starting jobs are not considered
 * 3. we need to start spark context and for stopping we give awaitfor stop means 
 * it will run for infinite time but we need to terminate it manually.We can pass specific interval as
 *  well Ex. 10 min etc
 * 4.Always consider the file size and time given in second to process it.
 * 5.Sample output should be as below
-------------------------------------------
Time: 1595411880000 ms
-------------------------------------------
5

******************************************************************************/


package org.spark.practice


import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.sql.execution.datasources.text.TextFileFormat
import breeze.linalg.split


object SparkStreamingDemo2 {
    def main(args: Array[String])
  {  
    // Define Conf
    val conf =  new SparkConf()
    .setMaster("local[2]")
    .setAppName("Spark Streaming Demo 1")
    
    // Create Streaming Context
    val ssc = new StreamingContext(conf = conf, Seconds(15))
    
    ssc.sparkContext.setLogLevel("ERROR")
    
      
    // -- Processing Logic ----
    val inputFilePath = "resources/streamingFiles/"  
    val streamRDD = ssc.textFileStream(directory = inputFilePath)
    
    // Add header to be skipped if any 
    val header = "DEST_COUNTRY_NAME,ORIGIN_COUNTRY_NAME,count"
    val filteredRDD = streamRDD.filter(_!= header)
    
    // Generate key value pair RDD
    val pairedRDD = filteredRDD.map {
      row =>
       val rowElements = row.split(",")
       (rowElements(0),row)
       }
    
    // calculate count
    val groupRDD = pairedRDD.groupByKey().count()
    
    println("Showung count:")
    groupRDD.print()
    
    ssc.start()
    ssc.awaitTermination()
  
  }
}