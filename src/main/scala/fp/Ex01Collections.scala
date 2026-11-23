package fp

/** Part 1 — Collections basics (15 min)
  *
  * Immutable collections: `List`, `Vector`, `Set`, `Map` and `Option`.
  *
  * Useful methods: `headOption`, `drop`, `take`, `takeRight`, `reverse`, `toSet`, `size`, `get`,
  * `getOrElse`, `+`, `intersect`, `:+`, `++`.
  */
object Ex01Collections:

  /** Example (solved): the first element of a list, if any. */
  def firstElement[A](xs: List[A]): Option[A] =
    xs.headOption

  /** The second element of a list, if any. */
  def secondElement[A](xs: List[A]): Option[A] =
    // solution:begin
    xs.drop(1).headOption
    // solution:end

  /** The last `n` elements of a list (the whole list if it is shorter). */
  def lastN[A](xs: List[A], n: Int): List[A] =
    // solution:begin
    xs.takeRight(n)
    // solution:end

  /** Number of distinct elements. */
  def distinctCount[A](xs: List[A]): Int =
    // solution:begin
    xs.toSet.size
    // solution:end

  /** true when the list reads the same forwards and backwards. */
  def isPalindrome[A](xs: List[A]): Boolean =
    // solution:begin
    xs == xs.reverse
    // solution:end

  /** The age of `name`, if known. */
  def lookupAge(ages: Map[String, Int], name: String): Option[Int] =
    // solution:begin
    ages.get(name)
    // solution:end

  /** The age of `name`, or 0 if unknown. */
  def ageOrZero(ages: Map[String, Int], name: String): Int =
    // solution:begin
    ages.getOrElse(name, 0)
    // solution:end

  /** A new map where `name` is associated with `age` (added or replaced). */
  def addOrUpdate(ages: Map[String, Int], name: String, age: Int): Map[String, Int] =
    // solution:begin
    ages + (name -> age)
    // solution:end

  /** Elements present in both sets. */
  def commonElements[A](a: Set[A], b: Set[A]): Set[A] =
    // solution:begin
    a.intersect(b)
    // solution:end

  /** A new vector with `a` inserted at index `i`. Hint: `take`, `drop`, `:+`, `++`. */
  def insertAt[A](v: Vector[A], i: Int, a: A): Vector[A] =
    // solution:begin
    (v.take(i) :+ a) ++ v.drop(i)
    // solution:end

  /** ⭐ Bonus: integer division that does not crash on 0. */
  def safeDivide(a: Int, b: Int): Option[Int] =
    // solution:begin
    if b == 0 then None else Some(a / b)
    // solution:end
