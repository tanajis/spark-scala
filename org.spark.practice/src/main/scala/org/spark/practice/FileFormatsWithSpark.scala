
/* **************************************************************************
 * Spark 2.X onwards support by default(No need to add JAR)
 * CSV
 * JSON
 * PARQUET
 * ORC
 * 
 * For Below formats we need to add JAR
 * AVRO
 * CASSENDRA
 * JDBC
 * **************************************************************************/
package org.spark.practice

import org.apache.spark.sql.SparkSession

object FileFormatsWithSpark {
  def main(args:Array[String])
  {
    
    //create spark session
    
    val master = "local"
    val spark = SparkSession
    .builder
    .master(master)
    .appName("Dataframe Demo")
    .getOrCreate()
    
     // Read CSV
    println("1. Reading CSV as dataframe")
    val csvDF = spark.read.csv("resources/ebay_data.csv")
    csvDF.printSchema()
    csvDF.show()
    
    // Read JSON
    println("2. Reading Json as dataframe")
    val jsonDF = spark.read.json("resources/example_2.json")
    jsonDF.printSchema()
    jsonDF.show()

    // Read parquet
    println("3. Reading parquet as dataframe")
    val parquetDF = spark.read.parquet("resources/userdata1.parquet")
    parquetDF.printSchema()
    parquetDF.show()
    
    // Read ORC
    println("4. Reading ORC as dataframe")
    val orcDF = spark.read.orc("resources/userdata2.orc")
    orcDF.printSchema()
    orcDF.show()
    

    println("5. Writing dataframes as file.")
    csvDF.write.csv("resources/output/file1.csv")
    jsonDF.write.json("resources/output/file2.json")
    orcDF.write.orc("resources/output/file3.orc")
    
    print("---END----------------")
    
}
}