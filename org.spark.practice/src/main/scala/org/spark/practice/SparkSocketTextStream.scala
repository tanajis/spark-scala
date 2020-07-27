/* ****************************************************************************
 * @Auther : Tanaji Sutar
 * @Date	 : 21-07-2020
 * @Desc	 : This is demo of spark Streaming Context.
 * 1. We need to add dependency in maven : spark-streaming_2.12
 * 2. To generate stream of word as input, go to terminal and run  cammand:  nc -lk 9999
 * 3. we need to start spark context and for stopping we give awaitfor stop means 
 * it will run for infinite time but we need to termintae it manually.We can pass specific interval as
 *  well Ex. 10 min etc
 * 4.Output should be like below:
  -------------------------------------------
  Time: 1595335990000 ms
  -------------------------------------------
  (aa,1)
  (,1)
  (fff,1)
  (ddde,1)
 ******************************************************************************/
package org.spark.practice
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext



object SparkStreamingDemo1 {
  def main(args: Array[String])
  {
    
    // Define Conf
    val conf =  new SparkConf()
    .setMaster("local[2]")
    .setAppName("Spark Streaming Demo 1")
    
    // Create Streaming Context
    val ssc = new StreamingContext(conf = conf, Seconds(5))
    
    ssc.sparkContext.setLogLevel("ERROR")
    
    // -- Processing Logic ----
    val lines     = ssc.socketTextStream("localhost", 9999)
    val words     = lines.flatMap(x => x.split(" "))
    val pairs     = words.map(word => (word,1))
    val wordcount = pairs.reduceByKey( _ + _ )
    
    // Show wordcount
    wordcount.print()
    
    ssc.start()
    ssc.awaitTermination()
        
  }
}