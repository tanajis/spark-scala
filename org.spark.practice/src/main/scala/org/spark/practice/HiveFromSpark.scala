package org.spark.practice
import java.io.File
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SaveMode

// warehouseLocation points to the default location for managed databases and tables

object HiveFromSpark {

  def main(args:Array[String]){
    val master = "local"
    val spark = SparkSession
          .builder()
          .appName("SparkSession")
          .master(master)
          //.config("hive.metastore.uris", "thrift://localhost:9083")
          //.config("spark.sql.warehouse.dir","hdfs://localhost:54310/user/hive/warehouse")
          .enableHiveSupport()
          .getOrCreate()
     
     spark.sparkContext.setLogLevel("ERROR")
      // Read from hive table
     // Way 1 
     println("way 1:Read hive table using spark.read.table method")
     val df1 = spark.read.table("staging.classData")
         df1.printSchema()
         df1.show()
      
     // Way 2
         println("way 2:Read hive table using spark.sql method")
     
         val df2 = spark.sql("select * from staging.classData")
         df2.printSchema()
         df2.show()         
         
         println("way 3:Read hive table using spark.table method") 
         val df3 = spark.table("staging.classData")
         df3.show(3)
         
         println("Perform SQL query on hive using spark.")
         spark.sql("show schemas").show()
        
        
        
        
      val df4 = spark.read
                 .format("csv")
                 .option("header","true")
                 .option("inferSchema","False")
                 .option("delimeter",",")
                 .load("hdfs://localhost:54310/empData/emp.csv")
        
      df4.show(2)
      println("Save As Hive table.")
      
      println("DROP Table if already exist using spark.sql method.")
      spark.sql("DROP TABLE IF EXISTS emp_data_usingSpark")
      // If mode is overwrite, it throws error if table is already exist
      // So we need to make sure that if table already exist, drop it or keep mode append
      df4.write.mode(SaveMode.Overwrite).saveAsTable("emp_data_usingSpark")
      
      /* Even if we drop external table, path is still exist and hence it throws below error:
       * Exception in thread "main" org.apache.spark.sql.AnalysisException: Can not create the managed table('`emp_data_usingSpark`'). The associated location('hdfs://localhost:54310/user/hive/warehouse/emp_data_usingspark') already exists.;
       * 
       * Thus before running this application,Delete path with below hdfs cammand
       * hdfs dfs -rm -R hdfs://localhost:54310/user/hive/warehouse/emp_data_usingspark
       * 
       */
      // Create Partitioned tables in hive
      println("Save as hive partitioned Table")
      df4.write.partitionBy("deptno").mode(SaveMode.Overwrite).saveAsTable("emp_data_usingSpark")      
      
      

      // Create External Table in Hive
      println("Save as hive Exernal partitioned Table")
      df4.write
      .partitionBy("deptno")
      .option("path", "hdfs://localhost:54310/myexternalTables")
      .mode(SaveMode.Overwrite)
      .saveAsTable("emp_data_usingSpark2")      
     
      
      
      println("Spark InserInto method")
      /*
       * InserInto method
       * 1.table must be already present in the hive
       * 2.Order of the column must be same in table and dataframe.
       * If order is differenet, Create new DF using SELECT 
       * Df2 = DF1.select("col1,col2,col3")
       */

       df4.write.mode(SaveMode.Append).insertInto(tableName="emp_data_usingSpark")
       
      println("---End-----")
  
  }
  
}
