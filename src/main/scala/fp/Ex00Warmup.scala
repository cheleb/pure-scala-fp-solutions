package fp

extension [A] (xa: List[A])
  def mymap[B](f: A => B): List[B] = xa match
    case head :: next => f(head) :: xa.mymap(f)
    case Nil => Nil
  def myflatMap[B](f: A => List[B]): List[B] = xa match
    case head :: next => f(head) ++ xa.myflatMap(f)
    case Nil => Nil
  


/** Part 0 — Warm-up (5 min)
  *
  * Goal: check that your setup works. Replace `???` with an implementation, then run:
  * {{{
  *   sbt "~testOnly fp.Ex00*"
  * }}}
  */
object Ex00Warmup:

  /** Returns "Hello, <name>!" */
  def greet(name: String): String =
    // solution:begin
    s"Hello, $name!"
    // solution:end

  /** true when age is 18 or more. */
  def isAdult(age: Int): Boolean =
    // solution:begin
    age >= 18
    // solution:end
