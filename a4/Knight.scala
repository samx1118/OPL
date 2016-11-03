package a4

// TODO: Fix CycleTour StackOverflow

object Knight extends App {

  def time[A](a: => A) = {
    val now = System.nanoTime
    val result = a
    val micros = (System.nanoTime - now) / 1000
    println("%f seconds".format(micros/1.0e6))
    result
  }

  type Loc = (Int, Int)

  def home = (1,1)
  def moves = List((1,2), (1,-2), (-1,2), (-1,-2), (2,1), (2,-1), (-2,1), (-2,-1))
  def success = (map: Map[Loc, Int]) => {
    println("YEAH")
    Some(map.keys.toList.sortBy(x => map.get(x)))
  }
  def fail    = () => None

  def isValidMove(at: Loc, n: Int, visited: Map[Loc, Int]): Boolean = {
    (at, n) match {
      case((row, col), n) =>  (row <= n) &&
                              (col <= n) &&
                              (row > 0) &&
                              (col > 0) &&
                              !visited.contains(at)
    }
  }

  // get all the possible and valid moves
  def getPossibleMoves(at: Loc, n: Int, visited: Map[Loc, Int]): List[Loc] = {
    moves.map(move => (at._1 + move._1, at._2 + move._2))
        .filter((move => isValidMove(move, n, visited)))
  }

  // DFS -> StackOverflow
  // BFS -> Good Life =)
  def isConnected(at: Loc, n: Int, visited: Map[Loc, Int]): Boolean = {
    def isConnected_helper(ats: List[Loc], visited: Map[Loc, Int]): Boolean = {
      ats match {
        case Nil => visited.size == n * n
        case _ => {
          val next_ats = ats.map(at => getPossibleMoves(at, n, visited))
                            .flatten
          val new_visited = next_ats.foldLeft(visited)((acc, at) => acc + (at -> 1))
          isConnected_helper(next_ats, new_visited)
        }
      }
    }
    isConnected_helper(List(at), visited)
  }

  def hasRouteHome(at: Loc, n: Int, visited: Map[Loc, Int]): Boolean = {
    def hasRouteHome_helper(ats: List[Loc], visited: Map[Loc, Int]): Boolean = {
      ats match {
        case Nil => false
        case _ => if (ats.contains(home)) true else {
          val next_ats = ats.map(at => getPossibleMoves(at, n, visited))
                            .flatten
          val new_visited = next_ats.foldLeft(visited)((acc, at) => acc + (at -> 1))
          hasRouteHome_helper(next_ats, new_visited)
        }
      }
    }
    hasRouteHome_helper(List(at), visited)
  }

  def Warnsdorff(at: Loc, n: Int, visited: Map[Loc, Int]): Int = {
    at match {
      case (1, 1) => 99
      case _ => {
        getPossibleMoves(at, n, visited)
        .filter(move => isConnected(move, n, visited))
        .length
      }
    }
  }

  def findAllCycles(n: Int): List[List[(Int,Int)]] = ???

  // StackOverflow Again !!!
  // PS: Always end with (1, 1) since guarantee to have route home
  def findOneCycle(n: Int): Option[List[(Int,Int)]] = {
    val totalLocs = n * n
    def nextMove(at: Loc, visited: Map[Loc, Int],
                onSuccess: (Map[Loc, Int]) => Option[List[Loc]],
                onFail: () => Option[List[Loc]]): Option[List[Loc]] = {
      // println(at, visited.size, totalLocs, visited)
      println(visited.size, at, visited.keys.toList.sortBy(x => visited.get(x)))
      (at, visited.isEmpty, visited.size == totalLocs) match {
        case ((1,1), false, true) => onSuccess(visited)
        case ((1,1), false, _) => onFail()
        case _ => {
          // val possibleMoves = getPossibleMoves(at, n, visited)
          //                     .filter(move => isConnected(move, n, visited))
          //
          // possibleMoves.map(move => nextMove(move, visited + (move -> visited.size), onSuccess, onFail))
          //              .find(x => x != None)
          //              .flatten

          // val possibleMoves = getPossibleMoves(at, n, visited)
          //                     .filter(move => isConnected(move, n, visited))
          //                     .sortBy(move => Warnsdorff(move, n, visited))
          //                     // .filter(move => hasRouteHome(move, n, new_visited))
          // val trialCont = possibleMoves.foldRight(onFail)((move, callBack) =>
          //   () => nextMove(move, visited + (move -> visited.size), onSuccess, callBack)
          // )
          // trialCont()
        }
      }
    }
    nextMove(home, Map(), success, fail)
  }

  val sol = time { findOneCycle(args(0).toInt) }
  println("sol: "+ sol)

}