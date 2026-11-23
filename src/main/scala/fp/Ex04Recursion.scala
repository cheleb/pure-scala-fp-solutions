package fp

import scala.annotation.tailrec

/** Part 4 — Recursion and pattern matching (15 min)
  *
  * RULE: no library collection methods here (no `size`, `sum`, `reverse`, `last`, `map`, ...).
  * Only pattern matching on `Nil` and `head :: tail`, and recursion.
  *
  * Use `@tailrec` when asked: the compiler then checks that the call is in tail position.
  */
object Ex04Recursion:

  /** Example (solved): length of a list. */
  def length[A](xs: List[A]): Int =
    xs match
      case Nil       => 0
      case _ :: tail => 1 + length(tail)

  /** Sum of a list (simple recursion). */
  def sum(xs: List[Int]): Int =
    // solution:begin
    xs match
      case Nil          => 0
      case head :: tail => head + sum(tail)
    // solution:end

  /** Sum of a list, tail-recursive (must work on 100 000 elements). Use an inner `@tailrec` helper with an accumulator. */
  def sumTail(xs: List[Int]): Int =
    // solution:begin
    @tailrec
    def loop(rest: List[Int], acc: Int): Int =
      rest match
        case Nil          => acc
        case head :: tail => loop(tail, acc + head)
    loop(xs, 0)
    // solution:end

  /** The last element, if any. Make it tail-recursive and add `@tailrec` above the `def`. */
  def last[A](xs: List[A]): Option[A] =
    // solution:begin
    xs match
      case Nil         => None
      case head :: Nil => Some(head)
      case _ :: tail   => last(tail)
    // solution:end

  /** true if `a` is in the list. Make it tail-recursive and add `@tailrec` above the `def`. */
  def contains[A](xs: List[A], a: A): Boolean =
    // solution:begin
    xs match
      case Nil          => false
      case head :: tail => head == a || contains(tail, a)
    // solution:end

  /** Reverses a list, tail-recursive (must work on 100 000 elements). */
  def reverse[A](xs: List[A]): List[A] =
    // solution:begin
    @tailrec
    def loop(rest: List[A], acc: List[A]): List[A] =
      rest match
        case Nil          => acc
        case head :: tail => loop(tail, head :: acc)
    loop(xs, Nil)
    // solution:end

  /** xs followed by ys. */
  def concat[A](xs: List[A], ys: List[A]): List[A] =
    // solution:begin
    xs match
      case Nil          => ys
      case head :: tail => head :: concat(tail, ys)
    // solution:end

  /** Removes consecutive duplicates: List(1, 1, 2, 3, 3, 1) => List(1, 2, 3, 1). */
  def compress[A](xs: List[A]): List[A] =
    // solution:begin
    xs match
      case a :: (rest @ b :: _) if a == b => compress(rest)
      case a :: rest                      => a :: compress(rest)
      case Nil                            => Nil
    // solution:end

  /** ⭐ Bonus: n-th Fibonacci number (fib(0) = 0, fib(1) = 1), tail-recursive. */
  def fibonacci(n: Int): BigInt =
    // solution:begin
    @tailrec
    def loop(i: Int, a: BigInt, b: BigInt): BigInt =
      if i == 0 then a else loop(i - 1, b, a + b)
    loop(n, 0, 1)
    // solution:end
