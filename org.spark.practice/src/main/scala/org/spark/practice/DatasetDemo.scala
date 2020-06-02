package org.spark.practice

import org.apache.spark.sql.SparkSession

 
object DatasetDemo extends App{
    // Create Spark Session first
    val spark = SparkSession
    .builder()
    .master("local")
    .getOrCreate()
    
    //-------Create Case class.-------------------------------
    //1. It must be declared inside Object
    //2. No main method needed
    //3.Object needs to be extended as App
    //--------------------------------------------------------
      case class EbayData(
              auctionid:Long,
              bid:Double,
              bidtime:Double,
              bidder:String,
              bidderrate:String,
              openbid:Double,
              price:Double,
              item:String,
              auction_type:String
              )

      //import Spark session implicits
      import spark.implicits._
      
      // Now read CSV as dataset
      print("1.Creating Datasets")
      val ds1 = spark.read
                .option("header", "true")
                .option("inferSchema", "true")  
                .csv("resources/ebay_data.csv")
                .as[EbayData]
      
      ds1.show()
      ds1.printSchema()

      // Basic operations on Datasets
      
      println("2.Add filter to dataset")
      //filter syntax of datasets is little bit different than that of dataframe.
      val ds2= ds1.filter(ratingObj=>ratingObj.price > 100)
      
      ds2.show(5)
      
      println("3.Apply Where to dataset ")
      val ds3 = ds1.where("price > 100")
      //You can also use below syntax:
      //val ds3 = ds1.where(ds1("price") > 100)
      ds3.show(5)
      
      println("Select with dataset")
      //below will return dataframe and not dataset as we dont have case class
      //defined  for these selected columns
      val selected_col_df = ds1.select("auctionid","bid","bidtime","bidder")
      println("Dataframe from dataset")
      selected_col_df.show(4)
      
      // Now if we want it as dataset then define  case class
      case class Selected_cols_ds(
             auctionid:Long,
              bid:Double,
              bidtime:Double,
              bidder:String)
      
       print("Select dataset as dataset")
       val selected_col_ds = ds1
       .select("auctionid","bid","bidtime","bidder")
       .as[Selected_cols_ds]
       
     selected_col_ds.show(2)
    
 }
