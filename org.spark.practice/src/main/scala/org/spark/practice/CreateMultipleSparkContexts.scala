package org.spark.practice

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

object CreateMultipleSparkContexts {
  def main(args:Array[String])
  {

    val conf =  new SparkConf()
    conf.setMaster("local")
    conf.setAppName("sparkContext1")
    val sc1 = new SparkContext(conf)
    
    
    val conf2 =  new SparkConf()
    conf2.setMaster("local")
    conf2.setAppName("sparkContext2")
    val sc2 = new SparkContext(conf2)
    

    
    val array1 = Array(1,2,3,4,5)
    val array2 = Array(6,7,8,9,10)
    
    val rdd1 = sc1.parallelize(array1)
    val rdd2 = sc2.parallelize(array2)
    
    print(rdd1.reduce((x,y)=>x+y) )
    print(rdd2.reduce((x,y)=>x+y) )
  }
  
}