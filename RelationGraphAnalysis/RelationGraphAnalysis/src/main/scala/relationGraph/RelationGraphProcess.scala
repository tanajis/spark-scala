/**
* The Relation graph is an example of graph processing using spark graphx package
*
* @author  Tanaji SUtar
* @version 1.0
* @since   2014-03-31 
*/

package relationGraph
import org.apache.spark.sql.SparkSession
import org.apache.spark.graphx.GraphLoader
import org.apache.spark.rdd.RDD

import org.apache.spark.graphx._
import org.apache.spark.SparkContext._
import scala.annotation.meta.field
import org.apache.spark.SparkContext
import java.awt.TextField


object RelationGraphProcess extends App {
  
  // Set master and appName
  val master = "local"
  val appName = "relation Graph Processing"
  
  // Create Spark Session
  val spark = SparkSession
  .builder()
  .master(master)
  .appName(appName)
  .getOrCreate()
  
  // Create Spark Context
  val sc = spark.sparkContext
  sc.setLogLevel("ERROR")
  
  
   println(
    """
    -------------------------------------------------------------------------------------
    1. Create Graph using Graphx module
    -------------------------------------------------------------------------------------
    """)
    
  /**
   * Create vertex RDD
   * key as vertex id and value as (name,age)
   * Ex. 1,Mike,48  
   */

  
  //Note: type VertexId is RDD bases class for Graphx

  val vertices: RDD[  (VertexId,(String,String)) ] =
    sc.textFile("data/relation_vertex.csv")
    .map{ line =>
          val fields = line.split(",")
          ( fields(0).toLong,(fields(1),fields(2))  )
        }
  
  /**
   *  Create edge RDD
   * 	Ex. 6,1,Sister
   *  
   */
  
    val edges: RDD[ Edge[String] ] =
      sc.textFile("data/relation_edges.csv")
      .map{ line =>
        val fields = line.split(",")
        Edge(fields(0).toLong,fields(1).toLong,fields(2))
        }
      
    // In case a connection or a vertex is missing, then
    // graph is constructed using default
    
    val default = ("Unknown", "Missing")
    val graph = Graph(vertices, edges, default)
    
    println("\tHow many vertices?: "+graph.vertices.count())
    println("\tHow many edges?: "+graph.edges.count())


    println(
    """
    -------------------------------------------------------------------------------------
    2. Graph subgraph and filtering
    -------------------------------------------------------------------------------------
    """)
    // Graph filtering and creating subgraph
    //filter by the person's age or relationships?
    
    val graph2 = graph.vertices.filter{
      case (id, (name, age)) => age.toLong > 40
    }
    println("\tSubgraph with age more than 40.vertices cound: "+ graph2.count())
    
    //filter by relationship
    val graph3 = graph.edges.filter{
      case Edge(from,to,relation) => relation == "father" || relation == "mother"
    }
    println("\tSubgraph with relation Mother and father. edges cound: "+ graph3.count())
    
    println(
    """
    -------------------------------------------------------------------------------------
    3. Apply PageRank algorithm
    -------------------------------------------------------------------------------------
    The PageRank algorithm provides a ranking value for each of the vertices in a graph.
    It makes the assumption that the vertices that are connected to the most edges are the
    most important ones. 
    Search engines use PageRank to provide ordering for the page display during a web search
    -------------------------------------------------------------------------------------
    """)
    
    val tolerance = 0.0001
    val ranking = graph.pageRank(tolerance).vertices
    
    // Join this ranking with vertices RDD of our graph to get names
    val rankByPerson = vertices.join(ranking).map{
      case(id, ((person,age),rank)) =>(rank ,id ,person)
    }
    
    // Print Ranks
    rankByPerson.collect().foreach{
      case(rank,id,person) => println(f"\tRank $rank%1.2f  id $id person $person")
    }
    
    
    println(
    """
    -------------------------------------------------------------------------------------
    4. Apply Triangle Counting algorithm
    -------------------------------------------------------------------------------------
    - count of the number of triangles, associated with this vertex.
    """)
    
    val tricnt = graph.triangleCount().vertices
    
    tricnt.collect().foreach{
          case(vid,cnt)  => println(f"\t Vertex $vid has triangle count:$cnt")
        }
    
    
    /**
     * Note : mkString is another way to dispaly RDD
     * println(tricnt.collect().mkString("\n"))
     */   
    
     println(
    """
    -------------------------------------------------------------------------------------
    5. Apply Connected Component algorithm
    -------------------------------------------------------------------------------------
    -subgraph in which any two vertices are connected to each other by paths, 
    and which is connected to no additional vertices in the supergraph.
    
    -When a large graph is created from the data, it might contain unconnected
    subgraphs, that is, subgraphs that are isolated from each other, and contain no
    bridging or connecting edges between them. 
    
    -This algorithm provides a measure of this connectivity. 
    It might be important, depending upon your processing, to know that all the vertices are connected.

    """)
  
    /**
     * calls two graph methods: 
     * 1.connectedComponents
		 * 2.stronglyConnectedComponents .The strong method required a maximum iteration
		 * count, which has been set to 1000 .
		 * 
     */
    
    val iterations = 1000
    val connected_comp = graph.connectedComponents().vertices
    val strong_connected_comp = graph.stronglyConnectedComponents(iterations).vertices
    
    // Join the vertices with vertex names
    
    val connCompByPerson = vertices.join(connected_comp).map{
       case(id,((person,age),conn)) => (conn,id,person)
     }
     
     val strongconnCompByPerson = vertices.join(strong_connected_comp).map{
       case(id,((person,age),conn)) => (conn,id,person)
     }
    
     
     // Print Connected
     connCompByPerson.collect().foreach{
       case(conn,id,person) => println(f"\tWeak Conn comp $conn id:$id person:$person")
     }
     
     println("\n\n")
     strongconnCompByPerson.collect().foreach{
       case(conn,id,person) => println(f"\tStrong Conn comp $conn id:$id person:$person")
     }
     
   // Stop Spark Session
    println("\n-----------------------END------------------------------------------")
    spark.stop()
    
}