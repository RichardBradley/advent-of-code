package y2018.day1

import scala.collection.mutable

object Q2 {

  def main(args: Array[String]): Unit = {

    var seenFreqs = mutable.Set[Int]()
    var freq = 0

    while (true) {
      PuzzleInput.frequencyChanges
        .foreach { df =>
          val newFreq = freq + df
          if (!seenFreqs.add(newFreq)) {
            println("Seen frequency twice: " + newFreq)
            return
          }
          freq = newFreq
        }
    }
  }
}
