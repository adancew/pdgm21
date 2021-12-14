import scala.annotation.tailrec

object Lista6 {

  // creates a lazyList filled with ints obeying pattern defined in function next()
  def createLlist(length: Int, first: Int, next: Int => Int): LazyList[Int] =
    if length <= 0 then LazyList()
    else first #:: createLlist(length - 1, next(first), next)

  // prints lazy list on the screen
  def printLazy[A](list: LazyList[A]): Unit = {
    if list.isEmpty then print("[]\n")
    else
      print("[")

      @tailrec
      def iter(currentList: LazyList[A]): Unit = {
        if currentList.isEmpty then print("]\n")
        else if currentList.tail.isEmpty then print(s"${currentList.head}]\n")
        else
          print(s"${currentList.head}, ")
          iter(currentList.tail)
      }

      iter(list)
  }

  // ---- ---- task 1 ---- ----
  // returns each n-th element of llist up to the nth element
  def eachNElement[A](list: LazyList[A], n: Int, m: Int): LazyList[A] = {

    def helper(counter: Int, toIgnore: Int, currentList: LazyList[A]): LazyList[A] = {
      if counter <= 0 then LazyList()
      else if currentList.isEmpty then currentList
      else if toIgnore > 1 then
        helper(counter - 1, toIgnore - 1, currentList.tail)
      else currentList.head #:: helper(counter - 1, n, currentList.tail)
    }

    helper(m, 1, list)
  }

  // ---- ---- task 2 ---- ----
  // performs operations on lists
  def lazyExecute(list1: LazyList[Int], list2: LazyList[Int], operation: String): LazyList[Int] = {
    operation match
      case "+" => list1.zipAll(list2, 0, 0).map((x, y) => x + y)
      case "-" => list1.zipAll(list2, 0, 0).map((x, y) => x - y)
      case "*" => list1.zipAll(list2, 1, 1).map((x, y) => x * y)
      case "/" => list1.zipAll(list2, 1, 1).map((x, y) => x / y)
  }

  // ---- ---- task 3 ---- ----
  // repeats elements from first list accordingly to values in second list
  def duplicate[A](listOfElems: LazyList[A], listOfReps: LazyList[Int]): List[A] = {

    def repeatElemNTimes(elem: A, reps: Int): List[A] = {
      if reps <= 0 then List()
      else elem :: repeatElemNTimes(elem, reps - 1)
    }

    def iter(pairs: LazyList[(A, Int)]): List[A] = {
      pairs match
        case (elem, n) #:: tail => repeatElemNTimes(elem, n) ::: iter(tail)
        case _ => List()
    }

    iter(listOfElems.zip(listOfReps))

  }

  // alternative solution: this time returns a LazyList
  // repeats elements from first list accordingly to values in second list
  def lazyDuplicate[A](listOfElems: LazyList[A], listOfReps: LazyList[Int]): LazyList[A] = {

    def repeatElemNTimes(elem: A, reps: Int): LazyList[A] = {
      if reps <= 0 then LazyList()
      else {
        println("!"); elem #:: repeatElemNTimes(elem, reps - 1)
      }
    }

    listOfElems.zip(listOfReps).map((elem, reps) => repeatElemNTimes(elem, reps)).flatten

  }

  // ---- ---- task 4 and 5 ---- ----
  trait Debug:
    def debugName() = this.getClass.getSimpleName

    // doesn't display inherited fields
    def debugVars() = {
      // getDeclaredFields returns an array of Field objects declared in this class.
      this.getClass.getDeclaredFields().toList.map(
        field => {
          field.setAccessible(true)
          List(field.getName, field.getType, field.get(this))
        })
    }

  class Point(xv: Int, yv: Int) extends Debug {
    var x: Int = xv
    var y: Int = yv
    var a: String = "test"
  }

  class Pixel(xv: Int, yv: Int, intensity: Int) extends Point(xv, yv) {
    var in = intensity
  }

  def main(args: Array[String]): Unit = {
    // tests

    //---- ---- task 1 ---- ----
    println("---- ---- task 1 ---- ----")
    printLazy(eachNElement(LazyList(5, 6, 3, 2, 1), 2, 3)) // [5,3]
    printLazy(eachNElement(LazyList(5, 6, 3, 2, 1), 2, 4)) // [5,3]
    printLazy(eachNElement(LazyList('a', 'b', 'c', 'd'), 1, 3)) // ['a', 'b', 'c']
    printLazy(eachNElement(createLlist(10, 1, x => x + 1), 3, 100)) // [1,4,7,10]

    // ---- ---- task 2 ---- ----
    println("\n---- ---- task 2 ---- ----")
    printLazy(lazyExecute(LazyList(1, 2, 3), LazyList(2, 3, 4, 5), "+"))
    // [1,2,3] + [2,3,4,5] = [3,5,7,5]
    printLazy(lazyExecute(LazyList(2, 4, 6, 8), LazyList(10, 12, 14), "-"))
    // [2, 4, 6, 8] - [10, 12, 14] = [-8, -8, -8, 8]
    printLazy(lazyExecute(LazyList(1, 2, 3, 4, 5), LazyList(-1, -2, -3), "*"))
    // [1,2,3,4,5] * [-1,-2,-3] = [-1, -4, -9, 4, 5]
    printLazy(lazyExecute(LazyList(2, 4, 16, 256), LazyList(2, 4, 8, 16, 32, 64), "/"))
    // [2, 4, 16, 256] / [2, 4, 8, 16, 32, 64] = [1, 1, 2, 16, 32, 64]
    printLazy(lazyExecute(LazyList(2, 4, 8, 16, 32, 64), LazyList(2, 4, 16, 256), "/"))
    // [2, 4, 8, 16, 32, 64] / [2, 4, 16, 256] = [1, 1, 0, 0, 32, 64]

    // ---- ---- task 3 ---- ----
    println("\n---- ---- task 3 ---- ----")
    println(duplicate(LazyList(1, 2, 3), LazyList(0, 3, 1, 4))) // [2, 2, 2, 3]
    println(duplicate(LazyList(1, 2, 3, 4, 5), LazyList(2, -1, 3))) // [1, 1, 3, 3, 3]
    println(duplicate(LazyList('a', 'b', 'c'), LazyList(2, 0, 2))) // ['a', 'a', 'c', 'c']
    println(duplicate(LazyList('a', 'b'), LazyList(-1, -2, -3, -4))) // []

    println("\n---- ---- task 3 another solution ---- ----")
    printLazy(lazyDuplicate(LazyList(1, 2, 3), LazyList(0, 3, 1, 4))) // [2, 2, 2, 3]
    printLazy(lazyDuplicate(LazyList(1, 2, 3, 4, 5), LazyList(2, -1, 3))) // [1, 1, 3, 3, 3]
    printLazy(lazyDuplicate(LazyList('a', 'b', 'c'), LazyList(2, 0, 2))) // ['a', 'a', 'c', 'c']
    printLazy(lazyDuplicate(LazyList('a', 'b'), LazyList(-1, -2, -3, -4))) // []

    // ---- ---- task 4 and 5 ---- ----
    println("\n---- ---- tasks 4 and 5 ---- ----")
    println(new Point(3, 4).debugName()) // Point
    println(new Point(3, 4).debugVars())
    // [[x, int, 3], [y, int, 4], [a, java.lang.String, test]]

    println(new Pixel(3, 4, 100).debugName()) // Pixel
    println(new Pixel(3, 4, 100).debugVars())
    // [[in, int, 100]]

  }

}
