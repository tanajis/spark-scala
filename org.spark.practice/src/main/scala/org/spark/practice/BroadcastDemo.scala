package org.spark.practice

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StringType

object BroadcastDemo {
  def main(args:Array[String]):Unit ={
    
    //create spark session
    
    val master = "local"
    val spark = SparkSession
    .builder
    .master(master)
    .appName("Dataframe Demo")
    .getOrCreate()
    
    // Change log level avoid unrequired logs so as to have better visibility to out output.
    spark.sparkContext.setLogLevel("ERROR")    
    
     //create Dataframe 1
     val data1 = spark.sparkContext.parallelize(Array(
         Row("pune","411057"),
         Row("Atpadi","415301"),
         Row("Sangli","415415")
         ),3)
    val schema1 = StructType(
        StructField("city",StringType,false) ::
        StructField("zip",StringType,false) ::Nil
        )
     
    val df1 = spark.createDataFrame(data1, schema1)
    println("Dataframe DF1")
    df1.show()   
    
     //create Dataframe 2
     val data2 = spark.sparkContext.parallelize(Array(
         Row("pune","Maharashtra"),
         Row("Atpadi","Maharashtra"),
         Row("Sangli","Maharashtra")
         ),3)
    val schema2 = StructType(
        StructField("city",StringType,false) ::
        StructField("state",StringType,false) ::Nil
        )
     
    println("Dataframe DF2")
    val df2 = spark.createDataFrame(data2, schema2)
    df2.show()
    
    // Direct Join without braodcast
    println("***Direct join without using bradcast ****")
    println("""DF1 has 3 partitions so join will take 3 parellel tasks and 
      3 times same copy of df2 will be sent to worker nodes.This is time consuming and inefficient.
""")
    println("Join DF1 and DF2 without using braodcast")
    val df3 = df1.join(df2,usingColumn ="city") 
    df3.show()

    
    println("""To avoid this inefficiency, we will send a common copy of  broadcast variable to
every worker node and that worker node will reuse that copy of DF2.
In this way sending copy with every task is avoided using broadcast variable.""")

	  //Now Braodcast this datafarme
    val brd_df2 = spark.sparkContext.broadcast(df2)
    
    println("Join DF1 and DF2 using braodcast")
    val df4 = df1.join(brd_df2.value,usingColumn ="city") 
    df4.show()
    
    // We can also broadcast simple variables like array
    val myNumbers = 555
    
    val br_myNumbers = spark.sparkContext.broadcast(myNumbers)
    println(""" Read Value from Braodcast:"""+ br_myNumbers.value)
    
  }
}