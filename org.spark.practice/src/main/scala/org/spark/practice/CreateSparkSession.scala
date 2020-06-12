package org.spark.practice
import org.apache.spark.sql.SparkSession
object CreateSparkSession {

  def main(args:Array[String]){
  /*Spark Session is single point entry to all contexts
   * It is added in spark 2x
   * 
   */
    
    // We can give spark master URL instead of local below.
    val master = "local"
    val spark = SparkSession
          .builder()
          .appName("SparkSession")
          .master(master)
          .getOrCreate()
         
  
    // Change log level avoid unrequired logs so as to have better visibility to out output.
    spark.sparkContext.setLogLevel("ERROR")
    
    // New Way of creating SparkCOntext
     val sc =spark.sparkContext
     val array = Array(3,4,5,6,7)
     val arrayRDD = sc.parallelize(array,3)
     print("RDD is created!!!")
     println("Total records in rdd:"+arrayRDD.count())
  
     //print using foreach
     println("----foreach---------")
     arrayRDD.foreach(println)
     
     println("---partition size: "+ arrayRDD.partitions.size)
     
  }
}