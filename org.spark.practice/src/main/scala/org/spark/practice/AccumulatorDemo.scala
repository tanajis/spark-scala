package org.spark.practice

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.Row
import org.apache.spark.util.LongAccumulator


/* ****************************************************************************
 * @Auther : Tanaji Sutar
 * @Date	 : 16-07-2020
 * @Desc	 : This is demonstration of spark Accumulator.
 * Accumulators can be used for only associative and cumulative operations.
 * Accumulator can be of two types. - named and unnamed accumulators.
 * used to implements counters, error debuging etc
 * Data Types of Accumulators
 *         1.long, 
 *         2.integer,
 *         3.string,
 *         4.double, 
 *         5.float, 
 *         6.collection
 *         
 ******************************************************************************/

object AccumulatorDemo {
  def main(args:Array[String]): Unit = {
    
    //create spark session
    val master = "local"
    val spark = SparkSession
    .builder
    .master(master)
    .appName("Dataframe Demo")
    .getOrCreate()
    
    // Change log level avoid unrequired logs so as to have better visibility to out output.
    spark.sparkContext.setLogLevel("ERROR")    

    
    val df2 = spark.read
    .option("header",true)
    .option("inferSchema",true)
    .csv("resources/flightData.csv")
    
    df2.show()
    
    
    // ***Unnamed accumulator ******
    val acc_usa = new LongAccumulator()
    spark.sparkContext.register(acc_usa)
    
    /******Named accumulator ******
    * It has two ways to create
    * Way 1 (Need to register manually)  
    * Way 2 (No need to register manually)
    * */
    
    // Way 1 (Need to register manually)
    val acc_ind = new LongAccumulator()
    spark.sparkContext.register(acc_ind,name="india_accm")
    
    // Way 2 (No need to register manually)
    val acc_chn = spark.sparkContext.longAccumulator(name ="china_accm")
    
    
    df2.foreach {
      row =>
      val dest = row(0).toString()
      val origin = row(1).toString()
      val count = row(2).toString().toLong
      
      if (dest.equalsIgnoreCase("united states") ||
          origin.equalsIgnoreCase("united states"))
              {
                acc_usa.add(count)
              }    
      
      if (dest.equalsIgnoreCase("india") ||
          origin.equalsIgnoreCase("india"))
              {
                acc_ind.add(count)
              }    
      
     if (dest.equalsIgnoreCase("china") ||
          origin.equalsIgnoreCase("china"))
              {
                acc_chn.add(count)
              }   
    
    }
    
    // print all the accumulator's values
       
    println("INDIA accumulator count:",acc_ind.value)
    println("USA accumulator count:",acc_usa.value)
    println("CHINA accumulator count:",acc_chn.value)
    

  }
}