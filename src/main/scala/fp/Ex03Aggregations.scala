package fp

/** Part 3 — Aggregations (20 min)
  *
  * `foldLeft`, `sum`, `maxByOption`, `groupBy`, `groupMapReduce`, `partition`, `sortBy`,
  * `sliding`, `scanLeft`.
  */
object Ex03Aggregations:

  /** Example (solved): sum with a fold. */
  def total(xs: List[Int]): Int =
    xs.foldLeft(0)(_ + _)

  /** Given helper: lower-cased words of a text. */
  def words(text: String): List[String] =
    text.toLowerCase.split("[^\\p{L}]+").toList.filter(_.nonEmpty)

  /** Average of a list, None if empty. */
  def average(xs: List[Int]): Option[Double] =
    // solution:begin
    if xs.isEmpty then None else Some(xs.sum.toDouble / xs.size)
    // solution:end

  /** The student with the best average (students without grades are ignored). */
  def bestStudent(students: List[Student]): Option[Student] =
    // solution:begin
    students
      .flatMap(s => average(s.grades).map(avg => (s, avg)))
      .maxByOption(_._2)
      .map(_._1)
    // solution:end

  /** Number of students per group. Hint: `groupMapReduce`. */
  def countByGroup(students: List[Student]): Map[String, Int] =
    // solution:begin
    students.groupMapReduce(_.group)(_ => 1)(_ + _)
    // solution:end

  /** Average of ALL the grades of each group. */
  def averageByGroup(students: List[Student]): Map[String, Double] =
    // solution:begin
    students
      .groupBy(_.group)
      .flatMap((group, ss) => average(ss.flatMap(_.grades)).map(avg => group -> avg))
    // solution:end

  /** (names with average >= 10, the others). Students without grades fail. */
  def passFail(students: List[Student]): (List[String], List[String]) =
    // solution:begin
    val (pass, fail) = students.partition(s => average(s.grades).exists(_ >= 10))
    (pass.map(_.name), fail.map(_.name))
    // solution:end

  /** Number of occurrences of each word. Use [[words]]. */
  def wordCount(text: String): Map[String, Int] =
    // solution:begin
    words(text).groupMapReduce(identity)(_ => 1)(_ + _)
    // solution:end

  /** The `n` most frequent words, by decreasing count then alphabetical order. */
  def topWords(text: String, n: Int): List[(String, Int)] =
    // solution:begin
    wordCount(text).toList.sortBy((word, count) => (-count, word)).take(n)
    // solution:end

  /** (name, average) sorted by decreasing average; students without grades are ignored. */
  def ranking(students: List[Student]): List[(String, Double)] =
    // solution:begin
    students
      .flatMap(s => average(s.grades).map(avg => (s.name, avg)))
      .sortBy(-_._2)
    // solution:end

  /** How many times a value is strictly greater than the previous one. Hint: `sliding(2)`. */
  def countIncreases(xs: List[Int]): Int =
    // solution:begin
    xs.sliding(2).count {
      case List(a, b) => b > a
      case _          => false
    }
    // solution:end

  /** ⭐ Bonus: running totals, e.g. List(1, 2, 3) => List(1, 3, 6). Hint: `scanLeft`. */
  def runningTotal(xs: List[Int]): List[Int] =
    // solution:begin
    xs.scanLeft(0)(_ + _).tail
    // solution:end

  /** ⭐ Bonus: the most frequent letter (case-insensitive), None if there is no letter. */
  def mostFrequentLetter(s: String): Option[Char] =
    // solution:begin
    s.toLowerCase
      .filter(_.isLetter)
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .maxByOption(_._2)
      .map(_._1)
    // solution:end
