package fp

/** Part 2 — Transformations (20 min)
  *
  * `map`, `filter`, `flatMap`, `collect`, `forall`, `mkString` and `for`-comprehensions.
  *
  * Use the shared dataset in [[Data]] to experiment in `sbt console`.
  */
object Ex02Transformations:

  /** Example (solved): the names of all students. */
  def names(students: List[Student]): List[String] =
    students.map(_.name)

  /** Names of the students belonging to `group`. */
  def groupNames(students: List[Student], group: String): List[String] =
    // solution:begin
    students.filter(_.group == group).map(_.name)
    // solution:end

  /** Names of the students who have at least one grade and whose grades are ALL >= threshold. */
  def passing(students: List[Student], threshold: Int): List[String] =
    // solution:begin
    students
      .filter(s => s.grades.nonEmpty && s.grades.forall(_ >= threshold))
      .map(_.name)
    // solution:end

  /** All the grades of all the students, in a single list. */
  def allGrades(students: List[Student]): List[Int] =
    // solution:begin
    students.flatMap(_.grades)
    // solution:end

  /** Names of the students without any grade. */
  def studentsWithoutGrades(students: List[Student]): List[String] =
    // solution:begin
    students.filter(_.grades.isEmpty).map(_.name)
    // solution:end

  /** Keeps only the strings that are valid integers. Hint: `toIntOption`. */
  def parseInts(xs: List[String]): List[Int] =
    // solution:begin
    xs.flatMap(_.toIntOption)
    // solution:end

  /** All the pairs (x, y). Use a `for`-comprehension. */
  def pairs[A, B](xs: List[A], ys: List[B]): List[(A, B)] =
    // solution:begin
    for
      x <- xs
      y <- ys
    yield (x, y)
    // solution:end

  /** All (a, b, c) with 1 <= a <= b <= c <= n and a² + b² = c², ordered by a then b.
    *
    * Use a `for`-comprehension with a guard (`if`).
    */
  def pythagoreanTriples(n: Int): List[(Int, Int, Int)] =
    // solution:begin
    (for
      a <- 1 to n
      b <- a to n
      c <- b to n
      if a * a + b * b == c * c
    yield (a, b, c)).toList
    // solution:end

  /** The first letters of all names, concatenated. */
  def initials(students: List[Student]): String =
    // solution:begin
    students.map(_.name.head).mkString
    // solution:end

  /** Squares of the even numbers only, using a single `collect`. */
  def collectEvenSquares(xs: List[Int]): List[Int] =
    // solution:begin
    xs.collect { case x if x % 2 == 0 => x * x }
    // solution:end

  /** ⭐ Bonus: keeps the defined values. */
  def flattenOptions[A](xs: List[Option[A]]): List[A] =
    // solution:begin
    xs.flatten
    // solution:end
