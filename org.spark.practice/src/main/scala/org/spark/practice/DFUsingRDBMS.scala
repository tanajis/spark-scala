package org.spark.practice

import org.apache.spark.sql.SparkSession
import java.util.Properties


/*
 * 1.Read from Postgressql as dataframe ad dataset
 * 2.Query pushdown
 * 3.perform transformations
 * 4.write to DB
 * 5.Best Practices
 */
object DFUsingRDBMS {
  
   def main(args:Array[String])
  { 
      
    //create spark session
    
    val master = "local"
    val spark = SparkSession
    .builder
    .master(master)
    .config("spark.jars","/home/tms/Downloads/code_consumer_complaints/jars/postgresql-42.2.12.jar")
    .appName("Dataframe Demo")
    .getOrCreate()
    
    // Change log level avoid unrequired logs so as to have better visibility to out output.
    spark.sparkContext.setLogLevel("ERROR")
    
    // Read postgresql table
     
    val dburl = "jdbc:postgresql:localhost:5432//consumer_complaints"
    val table  ="public.dim_company"
    val user = "postgres"
    val password = "postgres"

    
  val jdbcDF = spark.read
  .format("jdbc")
  .option("url", dburl)
  .option("dbtable",table )
  .option("user", user)
  .option("password", password)
  .option("driver", "org.postgresql.Driver")
  .load()
    
  jdbcDF.show()
}
}