package org.spark.practice
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StructType,StructField,IntegerType,StringType}

/* ****************************************************************************
 * This is demonstration of spark Catalog API. 
 * If hive support is enabled then hive metastore is showed as catalog otherwise it shows
 * Spark's default catalog
 * With Catalog, we can fire sql to create,view ,list table,db, functions etc.
 * 
 * @Auther : Tanaji Sutar
 * @Date	 : 18-06-2020
 ******************************************************************************/


object CatalogAPIDemo extends App {
      val master = "local"
      val spark = SparkSession
            .builder()
            .appName("SparkSession")
            .master(master)
            .enableHiveSupport()
            .getOrCreate()

      spark.sparkContext.setLogLevel("ERROR")
      
      val catalog = spark.catalog
       
      println("Current Database:"+ catalog.currentDatabase)
      
      println("List Database:")
      catalog.listDatabases.show()
      println("List Functions:")
      catalog.listFunctions.show()
      
      println("Set Current Database:")
      catalog.setCurrentDatabase(dbName="staging")
      
      println("List Tables:")
      catalog.listTables.show()
      
      print("List function return dataframe")
      val tableList = catalog.listTables()
      tableList.show(2)
      
      
      println("Check if DB exist:"+ catalog.databaseExists("staging"))
      println("Check if DB exist:"+ catalog.functionExists(dbName="staging", functionName ="calcDiff"))
      
      println("Create DB")
      //catalog.create
      
      println("Create Table using catalog.createTable Method")
      val schema1 = StructType(
        StructField("Id",IntegerType,true)::
        StructField("Name",StringType,true)::Nil
      )
      
      catalog.createTable(tableName="mytable2", source ="parquet", schema =schema1,options=
        Map("Comments" -> "Table Created using spark catalog"))
        
      println("Get Table")
      val t1 = catalog.getTable(dbName="staging", tableName="classdata")
      
      println("Current Database:")
      val d1 = catalog.getDatabase("staging")
      
      println("Clear Cashe")
      catalog.clearCache()

}
