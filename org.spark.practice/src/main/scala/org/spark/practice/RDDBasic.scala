package org.spark.practice
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object RDDBasic {
  
  def main(args:Array[String])
  {
    // Create spark context --old style(Not using SparkSession)
    val conf =  new SparkConf()
    conf.setMaster("local")
    conf.setAppName("RDDBasic")
    val sc = new SparkContext(conf)
    
    
    
    // 1. Create RDD with parallelize
    // Create an Array
    val array = Array(1,2,3,4,5,6,7,8)
    val numSlices = 3
    
    //Create an RDD by parallelizing this array.
    val arrayRDD = sc.parallelize(array, numSlices)
    
    // Check the count of elements in the RDD
    val cnt  = arrayRDD.count()
    println("Total numbers in array :" + cnt)
 
    // Collect RDD as glome
    println("Collect RDD as glome")
    arrayRDD.glom.collect().foreach(println)
    // RDD Filter
    println("RDD Filter using _ ")
    arrayRDD.filter( _ <5 ).collect().foreach(print)
    //RDD filter using Function
    
    
    // 2. Create RDD with csv
    val fileRDD = sc.textFile("resources/ebay_data.csv",5)
    println("Number of rows in fileRDD:"+fileRDD.count())
    println("Show First 5 line in fileRDD:")
    fileRDD.take(5).foreach(println)
    
    //Filter RDD -  skip csv header
    val header = fileRDD.first() 
    val fileRDD_Noheader = fileRDD.filter( _ != header)
    //println("Filter RDD -  skip csv header:"+fileRDD.filter(_ != header).take(5))
    fileRDD_Noheader.take(5).foreach(println)
    
    // RDD map function
     val csvArray = fileRDD_Noheader.map(line =>line.split(",")) 
     println("RDD map function:")
     csvArray.take(3).foreach(println)
     
     //pick sepcific field/column from RDD
     val bidder = fileRDD_Noheader.map(line =>line.split(",")(3))
     println("Specific column from RDD")
     bidder.take(5).foreach(println)
     
     
     //save RDD to csv
     println("Saving RDD as csv file")
     fileRDD.saveAsTextFile("resources/output1")
     
     //save RDD to parquet
     //fileRDD.saveAsParquet("resources/output2")
     
     println("------END--------------------------------------------") 
       
  }
  
}