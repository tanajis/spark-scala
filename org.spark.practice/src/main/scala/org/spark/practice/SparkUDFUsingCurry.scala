package org.spark.practice

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.expressions.UserDefinedFunction

/* ****************************************************************************
 * This is demonstration of spark Scala CURRY FUNCTIONS. 
 * 
 * @Auther : Tanaji Sutar
 * @Date	 : 18-06-2020
 ******************************************************************************/

object SparkUDFUsingCurry {
  
  def main(args: Array[String]): Unit ={
    
    val data = Seq(("10","50"),("20","100"),("10","50"),("20","100"),("10","50"),("20","100")) 
    
    val spark = SparkSession.builder().master("local").appName("Curry UDF Demo").getOrCreate()
    
    val sc = spark.sparkContext
    
    sc.setLogLevel("ERROR")
    val rdd1 = sc.parallelize(data, 2)

    val df = spark.createDataFrame(rdd1).toDF("col1","col2")
    
    println("Input dataframe")
    df.show()
    
    println("Scala Curry function UDF with column only.")
    
    val diffData = df.withColumn("diff", diffcalc()(df("col1"),df("col2")))
    
    diffData.show()
    
    println("Scala Curry function UDF with column as well as constant/parameter.")
    def amtcalc(k:Int): UserDefinedFunction = udf( (v1:Int) => {v1 * k}  )
    val k = 3
    val amtData2 = df.withColumn("amt", amtcalc(3)(df("col2")))
    
    amtData2.show()
    
  }
  
  
  
  // Define curry function - only column
  def diffcalc(): UserDefinedFunction = udf( (v1:Int,v2:Int) => {v1-v2}  )
  
  // Define curry function - column as well as other constats or parameteres
  // Note : This is advantages of curry function that we can pass constants and other values along  with column
  // In normal udf we can pass just columns of the dataframe.
  
  def amtcalc(k:Int): UserDefinedFunction = udf( (v1:Int) => {v1 * k}  )
}