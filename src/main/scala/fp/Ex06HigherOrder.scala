package fp

/** Part 6 — Writing higher-order functions (20 min)
  *
  * A higher-order function (HOF) takes a function as parameter and/or returns a function.
  *
  * RULE: in the extension methods below, do NOT use the built-in `map`, `filter`, `foldLeft`,
  * `exists` or `flatMap`. Use pattern matching and recursion.
  */
object Ex06HigherOrder:

  extension [A](xs: List[A])

    /** Example (solved): applies f to each element. */
    def myForeach(f: A => Unit): Unit =
      xs match
        case Nil          => ()
        case head :: tail => f(head); tail.myForeach(f)

    def myMap[B](f: A => B): List[B] =
      // solution:begin
      xs match
        case Nil          => Nil
        case head :: tail => f(head) :: tail.myMap(f)
      // solution:end

    def myFilter(p: A => Boolean): List[A] =
      // solution:begin
      xs match
        case Nil                      => Nil
        case head :: tail if p(head) => head :: tail.myFilter(p)
        case _ :: tail                => tail.myFilter(p)
      // solution:end

    def myFoldLeft[B](z: B)(op: (B, A) => B): B =
      // solution:begin
      xs match
        case Nil          => z
        case head :: tail => tail.myFoldLeft(op(z, head))(op)
      // solution:end

    def myExists(p: A => Boolean): Boolean =
      // solution:begin
      xs match
        case Nil          => false
        case head :: tail => p(head) || tail.myExists(p)
      // solution:end

    /** Hint: you may use `++` (or your `concat` from Part 4). */
    def myFlatMap[B](f: A => List[B]): List[B] =
      // solution:begin
      xs match
        case Nil          => Nil
        case head :: tail => f(head) ++ tail.myFlatMap(f)
      // solution:end

  /** `map` written with `myFoldLeft` only (you may use `reverse`). */
  def mapViaFold[A, B](xs: List[A])(f: A => B): List[B] =
    // solution:begin
    xs.myFoldLeft(List.empty[B])((acc, a) => f(a) :: acc).reverse
    // solution:end

  /** `filter` written with `myFoldLeft` only (you may use `reverse`). */
  def filterViaFold[A](xs: List[A])(p: A => Boolean): List[A] =
    // solution:begin
    xs.myFoldLeft(List.empty[A])((acc, a) => if p(a) then a :: acc else acc).reverse
    // solution:end

  /** Returns a function adding n. */
  def adder(n: Int): Int => Int =
    // solution:begin
    x => x + n
    // solution:end

  /** Returns a function multiplying by n. */
  def multiplier(n: Int): Int => Int =
    // solution:begin
    _ * n
    // solution:end

  /** f after g: x => f(g(x)). Do not use the built-in `compose` / `andThen`. */
  def compose[A, B, C](f: B => C, g: A => B): A => C =
    // solution:begin
    a => f(g(a))
    // solution:end

  /** Returns a function applying f twice. */
  def twice[A](f: A => A): A => A =
    // solution:begin
    compose(f, f)
    // solution:end

  /** Chains all functions, from left to right: composeAll(List(f, g))(x) == g(f(x)).
    * The empty list gives the identity function.
    */
  def composeAll[A](fs: List[A => A]): A => A =
    // solution:begin
    fs.foldLeft(identity[A])(_ andThen _)
    // solution:end

  /** Turns a two-parameter function into a chain of one-parameter functions. */
  def curry[A, B, C](f: (A, B) => C): A => B => C =
    // solution:begin
    a => b => f(a, b)
    // solution:end

  /** The inverse of curry. */
  def uncurry[A, B, C](f: A => B => C): (A, B) => C =
    // solution:begin
    (a, b) => f(a)(b)
    // solution:end

  /** A predicate that is true when ALL predicates are true (true for an empty list). */
  def allOf[A](ps: List[A => Boolean]): A => Boolean =
    // solution:begin
    a => ps.forall(p => p(a))
    // solution:end

  /** ⭐ Bonus: returns a function counting the elements satisfying p. */
  def countWhere[A](p: A => Boolean): List[A] => Int =
    // solution:begin
    _.myFoldLeft(0)((n, a) => if p(a) then n + 1 else n)
    // solution:end
