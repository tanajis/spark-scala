


object ImpliciteClassDemo {
   implicit class IntTimes(x: Int) {
      def times [A](f: =>A): Unit = {
         def loop(current: Int): Unit =
         
         if(current > 0){
            f
            loop(current - 1)
         }
         loop(x)
      }
   }
}

/*
 * Call this in Demo2.scala 
  import ImpliciteClassDemo._
  object Demo2 {
     def main(args: Array[String]) {
        4 times println("hello")
     }}
 * 
 * 
 */


