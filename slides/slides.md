---
theme: seriph
title: Pure Scala FP
info: |
  ## Pure Scala FP — a 1-hour introduction
  Functional programming with Scala 3 for beginners.
  Companion course for the `pure-scala-fp` exercises.
class: text-center
layout: cover
transition: slide-left
mdc: true
lineNumbers: false
drawings:
  persist: false
---

# Pure Scala FP

Thinking in **values**, not in **steps**

<div class="pt-8 opacity-70">
Scala 3 · 1 hour · beginners · DaMS5 2026
</div>

<!--
Welcome. Goal of this hour: give students every concept they need to tackle the 8 exercise parts (~2h of practice).
We never show the exact exercise solutions: examples use other data (books, numbers, logs).
-->

---

# Today's plan

| # | Topic | Time | Exercise |
|---|-------|-----:|----------|
| 1 | Why functional programming? | 5' | — |
| 2 | Scala 3 in 5 minutes | 5' | `Ex00Warmup` |
| 3 | Immutable collections & `Option` | 10' | `Ex01Collections` |
| 4 | Transforming: `map`, `filter`, `flatMap`, `for` | 10' | `Ex02Transformations` |
| 5 | Aggregating: `foldLeft` & friends | 10' | `Ex03Aggregations` |
| 6 | Recursion & pattern matching | 8' | `Ex04Recursion` |
| 7 | Functions as values & higher-order functions | 10' | `Ex05` · `Ex06` |
| 8 | Putting it all together | 2' | `Ex07Capstone` |

<!--
Each section maps to one exercise file. Students can come back to the slides while coding.
-->

---
layout: section
---

# 1 · Why functional programming?

---

# Say *what*, not *how*

<div class="grid grid-cols-2 gap-8">
<div>

### Imperative 🛠️

```scala
var total = 0
var i = 0
while i < prices.length do
  if prices(i) > 10 then
    total = total + prices(i) * 2
  i = i + 1
```

<v-click>

- mutable state (`var`)
- index juggling, off-by-one bugs
- you must *run it in your head*

</v-click>
</div>
<div>

### Functional ✨

```scala
val total =
  prices
    .filter(_ > 10)
    .map(_ * 2)
    .sum
```

<v-click>

- no mutation, no index
- reads like the specification
- each step is a small, testable function

</v-click>
</div>
</div>

<!--
Same result. Ask the class: which one would you rather debug at 2 am?
-->

---

# Pure functions

A function is **pure** when:

<v-clicks>

1. the **same input** always gives the **same output**
2. it has **no side effect** (no printing, no mutation, no I/O, no exception thrown at you)

</v-clicks>

<div class="grid grid-cols-2 gap-6 mt-6">
<div v-click>

✅ Pure

```scala
def area(w: Double, h: Double): Double =
  w * h

def initial(name: String): Char =
  name.head.toUpper
```

</div>
<div v-click>

❌ Impure

```scala
var counter = 0
def next(): Int =
  counter += 1      // mutates the outside world
  counter

def now(): Long =
  System.currentTimeMillis() // different each call
```

</div>
</div>

<div v-click class="mt-6 p-3 rounded bg-teal-500/10">

**Referential transparency**: you can replace `area(2, 3)` by `6` anywhere — nothing changes.
That is what makes FP code easy to reason about and to test.

</div>

---

# Immutability

Once created, a value **never changes**. Operations build **new** values.

```scala {1|2|3-4|5}
val xs = List(1, 2, 3)
val ys = 0 :: xs           // List(0, 1, 2, 3)
xs                         // still List(1, 2, 3) !
val zs = xs.map(_ * 10)    // List(10, 20, 30)
// xs = List(4)            // ❌ does not compile: reassignment to val
```

<v-click>

```
 ys ──► [0] ──┐
              ▼
 xs ────────► [1] ─► [2] ─► [3] ─► Nil      structural sharing: nothing is copied
```

</v-click>

<v-click>

> 🧭 Rule for the exercises: **no `var`, no `while`, no `for` loops with side effects.**
> Only `val`, expressions and functions.

</v-click>

---
layout: section
---

# 2 · Scala 3 in 5 minutes

---

# Values, methods, expressions

```scala {1-2|4-5|7-10|12-14}
val pi: Double = 3.14159        // a constant
val city = "Nice"               // type inferred: String

def square(x: Int): Int = x * x // a method
def hello(name: String): String = s"Hi, $name!"   // string interpolation

def sign(x: Int): String =      // indentation-based syntax
  if x > 0 then "positive"      // if/then/else is an EXPRESSION:
  else if x < 0 then "negative" // it returns a value
  else "zero"

def describe(x: Int): String =
  val s = sign(x)               // local val
  s"$x is $s"                   // last expression = result (no `return`)
```

<v-click>

<div class="mt-4 p-3 rounded bg-teal-500/10">

Everything is an **expression**: `if`, `match`, blocks… all produce a value.

</div>

</v-click>

<!--
Ex00Warmup is exactly this: string interpolation and a boolean expression.
-->

---

# Case classes: data done right

```scala {1|3-4|6|8|10-12}
case class Book(title: String, author: String, year: Int, pages: Int)

val dune   = Book("Dune", "Herbert", 1965, 412)   // no `new`
val hobbit = Book("The Hobbit", "Tolkien", 1937, 310)

dune.title                                       // "Dune"

dune == Book("Dune", "Herbert", 1965, 412)       // true — compared by VALUE

val dune2 = dune.copy(year = 2021)               // a modified COPY
dune.year                                        // still 1965
```

<v-click>

We will reuse this `books` list in the next slides:

```scala
val books = List(dune, hobbit,
  Book("Foundation", "Asimov", 1951, 255), Book("The Robots", "Asimov", 1950, 0))
```

</v-click>

<!--
In the exercises, the case class is Student(name, group, grades) in Data.scala.
-->

---

# Generics & extension methods

**Generic** code works for any type `A`:

```scala
def firstOrElse[A](xs: List[A], default: A): A =
  if xs.isEmpty then default else xs.head

firstOrElse(List(3, 4), 0)       // 3
firstOrElse(List.empty[String], "?") // "?"
```

<v-click>

**Extension methods** add methods to existing types:

```scala
extension (s: String)
  def shout: String = s.toUpperCase + "!"

"hello".shout                    // "HELLO!"

extension [A](xs: List[A])
  def second: A = xs.tail.head   // available on every List[A]

List(7, 8, 9).second             // 8
```

</v-click>

<!--
Ex06 uses `extension [A](xs: List[A])` to write myMap, myFilter... directly on lists.
-->

---
layout: section
---

# 3 · Immutable collections & `Option`

---

# The four collections you need

| Collection | Ordered? | Duplicates? | Use it for… | Example |
|---|:-:|:-:|---|---|
| `List[A]`   | ✅ | ✅ | sequences, recursion (fast head) | `List(3, 1, 3)` |
| `Vector[A]` | ✅ | ✅ | fast access by index anywhere | `Vector('a', 'b')` |
| `Set[A]`    | ❌ | ❌ | membership, uniqueness | `Set("x", "y")` |
| `Map[K, V]` | ❌ | keys ❌ | lookup by key | `Map("fr" -> "Paris")` |

<v-click>

All are **immutable** by default: every "modification" returns a **new** collection.

```scala
val v  = Vector(10, 20, 30)
v :+ 40          // Vector(10, 20, 30, 40)   append
5 +: v           // Vector(5, 10, 20, 30)    prepend
v ++ Vector(1,2) // Vector(10, 20, 30, 1, 2) concatenate
v                // unchanged: Vector(10, 20, 30)
```

</v-click>

---

# A toolbox of everyday methods

<div class="grid grid-cols-2 gap-6">
<div>

```scala
val xs = List(4, 8, 15, 16, 23, 42)

xs.size            // 6
xs.head            // 4   (💥 on empty list!)
xs.headOption      // Some(4)
xs.take(2)         // List(4, 8)
xs.drop(2)         // List(15, 16, 23, 42)
xs.takeRight(2)    // List(23, 42)
xs.reverse         // List(42, 23, 16, 15, 8, 4)
xs.contains(15)    // true
List(1, 1, 2).toSet // Set(1, 2)
```

</div>
<div>

```scala
val capitals = Map("fr" -> "Paris", "it" -> "Rome")

capitals.get("fr")              // Some("Paris")
capitals.get("de")              // None
capitals.getOrElse("de", "?")   // "?"
capitals + ("de" -> "Berlin")   // new Map, 3 entries
capitals + ("fr" -> "Lyon")     // replaces the value

val a = Set(1, 2, 3)
val b = Set(2, 3, 4)
a.intersect(b)   // Set(2, 3)
a.union(b)       // Set(1, 2, 3, 4)
a.diff(b)        // Set(1)
```

</div>
</div>

<v-click>

💡 Try them live: `sbt console` then `import fp.*` and play with `Data.students`.

</v-click>

---

# `Option`: no more `null`

A value that **may be absent** has type `Option[A]`:

<div class="grid grid-cols-2 gap-6">
<div>

```scala
val some: Option[Int] = Some(42)
val none: Option[Int] = None

"42".toIntOption    // Some(42)
"abc".toIntOption   // None
List().headOption   // None
```

</div>
<div v-click>

```scala
def safeSqrt(x: Double): Option[Double] =
  if x < 0 then None
  else Some(math.sqrt(x))
```

</div>
</div>

<v-click>

Use it **without** unwrapping it:

```scala
some.map(_ + 1)           // Some(43)
none.map(_ + 1)           // None — no crash
some.getOrElse(0)         // 42
none.getOrElse(0)         // 0
some.exists(_ > 40)       // true
none.exists(_ > 40)       // false
```

</v-click>

<v-click>

<div class="p-3 rounded bg-teal-500/10">

Think of `Option` as a **list of 0 or 1 element** — `map`, `filter`, `flatMap`, `exists` all work the same way.

</div>

</v-click>

---
layout: section
---

# 4 · Transformations

`map` · `filter` · `flatMap` · `collect` · `for`

---

# `map` and `filter`

<div class="grid grid-cols-2 gap-6">
<div>

### `map`: transform **every** element

```
[ 1 ,  2 ,  3 ,  4 ]
  │    │    │    │     _ * 10
  ▼    ▼    ▼    ▼
[10 , 20 , 30 , 40 ]
```

```scala
List(1, 2, 3, 4).map(_ * 10)
books.map(_.title)
// List("Dune", "The Hobbit", ...)
```

</div>
<div>

### `filter`: keep **some** elements

```
[ 1 ,  2 ,  3 ,  4 ]
  ✗    ✓    ✗    ✓     _ % 2 == 0
[      2 ,       4 ]
```

```scala
List(1, 2, 3, 4).filter(_ % 2 == 0)
books.filter(_.year < 1960)
```

</div>
</div>

<v-click>

### Chain them into a pipeline

```scala
books
  .filter(_.author == "Asimov")   // keep Asimov's books
  .map(_.title)                   // List("Foundation", "The Robots")
```

</v-click>

<v-click>

Predicates on a whole collection: `xs.forall(p)` (all?) · `xs.exists(p)` (at least one?) · `xs.isEmpty` / `xs.nonEmpty`

</v-click>

---

# `flatMap` = `map` + `flatten`

When each element gives **a collection**, `map` produces nested lists:

```scala {1-2|4-5|7-8}
val sentences = List("to be", "or not")
sentences.map(_.split(" ").toList)     // List(List("to", "be"), List("or", "not"))

sentences.map(_.split(" ").toList).flatten  // List("to", "be", "or", "not")
sentences.flatMap(_.split(" ").toList)      // same thing, in one step

val shelves = List(List(dune), List(hobbit, dune))
shelves.flatten                              // List(dune, hobbit, dune)
```

<v-click>

### Works with `Option` too — great to drop invalid values

```scala
List("7", "x", "12").map(_.toIntOption)   // List(Some(7), None, Some(12))
List(Some(7), None, Some(12)).flatten     // List(7, 12)
```

So `xs.flatMap(f)` with `f: A => Option[B]` keeps **only the successful results**.

</v-click>

---

# `collect` and `mkString`

`collect` = `filter` + `map` in **one** step, with a *partial function*:

```scala
val mixed = List(-3, 5, -1, 8)
mixed.collect { case x if x > 0 => x * 100 }   // List(500, 800)

books.collect { case Book(t, "Tolkien", _, _) => t }  // List("The Hobbit")
```

<v-click>

`mkString` turns a collection into a `String`:

```scala
List('a', 'b', 'c').mkString          // "abc"
List(1, 2, 3).mkString(", ")          // "1, 2, 3"
List(1, 2, 3).mkString("[", "; ", "]") // "[1; 2; 3]"
```

</v-click>

<v-click>

And the other way:

```scala
"Dune".head                 // 'D'   (a String is a sequence of Char)
"a;b;c".split(";").toList   // List("a", "b", "c")
```

</v-click>

---

# `for`-comprehensions

Nested `flatMap`/`map` become readable:

<div class="grid grid-cols-2 gap-6">
<div>

```scala
val sizes  = List("S", "M")
val colors = List("red", "blue")

for
  s <- sizes
  c <- colors
yield s"$s-$c"
// List(S-red, S-blue, M-red, M-blue)
```

</div>
<div v-click>

The compiler rewrites it to:

```scala
sizes.flatMap(s =>
  colors.map(c =>
    s"$s-$c"))
```

</div>
</div>

<v-click>

### With a guard (`if`) and ranges

```scala
for
  x <- 1 to 20          // a Range: 1, 2, ..., 20
  y <- x to 20          // depends on x!
  if x * y == 24
yield (x, y)            // Vector((2,12), (3,8), (4,6))  →  .toList if you need a List
```

</v-click>

<!--
The guard becomes a withFilter call. Ranges produce an IndexedSeq, hence the .toList.
This is the toolbox for the pairs / pythagorean triples exercises.
-->

---
layout: section
---

# 5 · Aggregations

From many values to **one** value

---

# `foldLeft`: the mother of all loops

```scala
List(2, 3, 4).foldLeft(1)((acc, x) => acc * x)    // 24
//                     ▲   └──── how to combine ───┘
//               initial value
```

<v-clicks>

- start: `acc = 1`
- `x = 2` → `acc = 1 * 2 = 2`
- `x = 3` → `acc = 2 * 3 = 6`
- `x = 4` → `acc = 6 * 4 = 24` ✅ result

</v-clicks>

<v-click>

The accumulator can be **anything**, not just a number:

```scala
List("a", "bb", "ccc").foldLeft(0)((acc, s) => acc + s.length)  // 6
List(1, 2, 3).foldLeft(List.empty[Int])((acc, x) => x :: acc)   // List(3, 2, 1)
```

Short form with placeholders: `xs.foldLeft(1)(_ * _)`

</v-click>

---

# Ready-made aggregations

```scala
val pages = books.map(_.pages)      // List(412, 310, 255, 0)

pages.sum                           // 977
pages.max                           // 412    (💥 on empty list)
pages.sum.toDouble / pages.size     // 244.25 — beware integer division!

books.maxByOption(_.pages)          // Some(Book("Dune", ...))
List.empty[Book].maxByOption(_.pages) // None — safe

books.sortBy(_.year)                // oldest first
books.sortBy(b => -b.year)          // newest first (negate to reverse)
books.sortBy(b => (b.author, b.title)) // by author, then by title
```

<v-click>

<div class="p-3 rounded bg-amber-500/10">

⚠️ `7 / 2 == 3` but `7.toDouble / 2 == 3.5`

</div>

</v-click>

---

# Grouping

```scala {1-4|6-7|9-10}
books.groupBy(_.author)
// Map("Herbert" -> List(dune),
//     "Tolkien" -> List(hobbit),
//     "Asimov"  -> List(foundation, robots))

books.groupMap(_.author)(_.title)
// Map("Herbert" -> List("Dune"), ..., "Asimov" -> List("Foundation", "The Robots"))

books.groupMapReduce(_.author)(_.pages)(_ + _)
// Map("Herbert" -> 412, "Tolkien" -> 310, "Asimov" -> 255)
```

<v-click>

`groupMapReduce(key)(value)(combine)` in three steps:

```
 group by key   →   map each element to a value   →   reduce the values of each group
  _.author             _.pages                          _ + _
```

💡 To **count** per key: map every element to `1` and add.

</v-click>

---

# More aggregation tools

```scala
// partition: split in two according to a predicate
val (thick, thin) = books.partition(_.pages > 300)
// thick = List(dune, hobbit), thin = List(foundation, robots)

// sliding: windows of consecutive elements
List(1, 5, 2, 8).sliding(2).toList     // List(List(1,5), List(5,2), List(2,8))

// scanLeft: like foldLeft, but keeps every intermediate accumulator
List(2, 3, 4).scanLeft(1)(_ * _)       // List(1, 2, 6, 24)

// identity: the function x => x
List("b", "a", "b").groupBy(identity)  // Map("a" -> List("a"), "b" -> List("b","b"))
```

<v-click>

Destructuring a tuple directly in a lambda (Scala 3):

```scala
Map("a" -> 3, "b" -> 1).toList.sortBy((key, count) => -count)   // List(("a",3), ("b",1))
```

</v-click>

---
layout: section
---

# 6 · Recursion & pattern matching

---

# Pattern matching

`match` is a super-powered `switch` — and an **expression**:

```scala {1-5|7-10|12-17}
def describe(n: Int): String = n match
  case 0          => "zero"
  case 1 | 2 | 3  => "small"
  case x if x < 0 => "negative"   // guard
  case _          => "big"        // wildcard: everything else

def era(b: Book): String = b match
  case Book(_, "Tolkien", _, _)       => "Middle-earth"
  case Book(t, _, y, _) if y < 1960   => s"$t is a classic"
  case _                              => "modern"

def firstTwo(xs: List[Int]): String = xs match
  case Nil                  => "empty"
  case a :: Nil             => s"only $a"
  case a :: b :: _          => s"$a then $b"

// binder: name a sub-pattern with @ →  case a :: (rest @ b :: _) => ...
```

---

# A `List` is a recursive structure

```
List(1, 2, 3)  ==  1 :: (2 :: (3 :: Nil))
                   ▲     └─────────┬────────┘
                  head            tail
```

<v-click>

So every function on a list has **two cases**:

```scala {1-4|6-9}
def product(xs: List[Int]): Int = xs match
  case Nil          => 1                       // base case
  case head :: tail => head * product(tail)    // recursive case: trust the recursion!

def countDown(n: Int): List[Int] =
  if n == 0 then Nil
  else n :: countDown(n - 1)
// countDown(3) == List(3, 2, 1)
```

</v-click>

<v-click>

```
product(List(2, 3, 4))
= 2 * product(List(3, 4))
= 2 * (3 * product(List(4)))
= 2 * (3 * (4 * product(Nil)))
= 2 * (3 * (4 * 1))   = 24
```

</v-click>

---

# Tail recursion: don't blow the stack

Each pending `2 * (...)` uses a **stack frame** → `StackOverflowError` on 100 000 elements 💥

<v-click>

**Fix**: carry the partial result in an **accumulator**, so the recursive call is the **last** thing done:

```scala {all|4-7|8}
import scala.annotation.tailrec

def product(xs: List[Int]): Int =
  @tailrec
  def loop(rest: List[Int], acc: Int): Int = rest match
    case Nil          => acc
    case head :: tail => loop(tail, acc * head)   // tail position: nothing left to do after
  loop(xs, 1)
```

</v-click>

<v-click>

- `@tailrec` asks the **compiler to check** it — compile error if the call is not in tail position
- the compiler turns it into a loop: constant stack
- `head * product(tail)` is **not** tail recursive (the `*` happens after the call)
- `cond || recurse(...)` **is** tail recursive ✅

</v-click>

---
layout: section
---

# 7 · Functions are values

---

# Function values and lambdas

```scala {1-2|4-5|7-8|10-12}
val inc: Int => Int = x => x + 1         // type  A => B
inc(41)                                  // 42

val inc2: Int => Int = _ + 1             // placeholder syntax: same thing
val isLong: String => Boolean = _.length > 10

val mul: (Int, Int) => Int = (a, b) => a * b   // two parameters
val mul2: (Int, Int) => Int = _ * _            // each `_` is a NEW parameter

def half(x: Int): Int = x / 2            // a METHOD...
List(10, 20).map(half)                   // ...converted to a function automatically
                                         // (eta-expansion): List(5, 10)
```

<v-click>

<div class="p-3 rounded bg-amber-500/10">

⚠️ `_ * _` means `(a, b) => a * b`, **not** `x => x * x`.

</div>

</v-click>

---

# Functions in data structures

Functions are values: store them in a `List`, a `Map`…

```scala
val cleaners: List[String => String] = List(_.trim, _.toLowerCase, _.reverse)

cleaners.map(f => f("  Hello "))    // List("Hello", "  hello ", " olleH  ")
```

<v-click>

A small "command interpreter" with a `Map`:

```scala
val formats: Map[String, Double => String] = Map(
  "eur" -> (x => s"$x €"),
  "pct" -> (x => s"${x * 100}%"),
)

formats.get("eur").map(f => f(3.5))    // Some("3.5 €")
formats.get("btc").map(f => f(3.5))    // None — unknown key, no crash
```

</v-click>

---

# Higher-order functions (1): taking a function

A **higher-order function** takes and/or returns a function. You already used many: `map`, `filter`, `foldLeft`…

Let's write one ourselves, with recursion only:

```scala {all|3-4|5-6}
extension [A](xs: List[A])
  def myCount(p: A => Boolean): Int = xs match
    case Nil                     => 0
    case head :: tail if p(head) => 1 + tail.myCount(p)
    case _ :: tail               => tail.myCount(p)

List(3, 8, 12, 1).myCount(_ > 5)    // 2
```

<v-click>

```scala
def applyThrice(f: Int => Int, x: Int): Int = f(f(f(x)))

applyThrice(_ * 2, 1)    // 8
```

</v-click>

<!--
Ex06: myMap, myFilter, myFoldLeft, myExists, myFlatMap follow exactly this shape.
-->

---

# Higher-order functions (2): returning a function

<div class="grid grid-cols-2 gap-6">
<div>

### Closures

```scala
def greeter(greeting: String): String => String =
  name => s"$greeting, $name!"

val hi = greeter("Hi")       // a function
hi("Ana")                    // "Hi, Ana!"
greeter("Bonjour")("Léo")    // "Bonjour, Léo!"
```

The returned lambda **captures** `greeting`.

</div>
<div v-click>

### Composition

```scala
val trim:  String => String = _.trim
val upper: String => String = _.toUpperCase

val clean = trim andThen upper  // trim first
clean("  hey ")                 // "HEY"

(upper compose trim)("  hey ")  // same: upper(trim(x))
```

</div>
</div>

<v-click>

### Currying: one argument at a time

```scala
val add:      (Int, Int) => Int = (a, b) => a + b
val addCurry: Int => Int => Int = a => b => a + b

addCurry(3)(4)                   // 7
val add10 = addCurry(10)         // partially applied: Int => Int
List(1, 2).map(add10)            // List(11, 12)
```

</v-click>

---

# Folding over functions

A list of functions can be **folded** into a single function:

```scala
val steps: List[Int => Int] = List(_ + 1, _ * 2, _ - 3)

val pipeline: Int => Int = steps.reduce(_ andThen _)
pipeline(5)        // ((5 + 1) * 2) - 3 = 9
```

<v-click>

<div class="p-3 rounded bg-teal-500/10">

🤔 `reduce` crashes on an **empty** list. Which **neutral element** for `andThen` would let you use `foldLeft` instead?
(Hint: the function that does nothing…) → exercise `composeAll` in Part 6.

</div>

</v-click>

<v-click>

Same idea with predicates:

```scala
val rules: List[String => Boolean] = List(_.nonEmpty, _.length < 20, _.head.isUpper)
val validTitle: String => Boolean = s => rules.forall(rule => rule(s))

validTitle("Dune")     // true
validTitle("dune")     // false
```

</v-click>

---
layout: section
---

# 8 · Putting it all together

---

# A data pipeline, functional style

Raw text → typed values → aggregate → text report

```scala {all|1|3-7|9-10|12-16}
case class Log(level: String, message: String)

def parse(line: String): Option[Log] =
  line.split(";", -1).toList match              // -1 keeps empty trailing fields
    case List(_, level, msg) if level.nonEmpty =>
      Some(Log(level, msg))
    case _ => None                              // invalid line: no exception, just None

val lines = List("10:00;ERROR;disk full", "garbage", "10:05;INFO;ok", "10:07;ERROR;oom")
val logs  = lines.flatMap(parse)                // keeps only the valid ones

val report =
  logs.groupMapReduce(_.level)(_ => 1)(_ + _)   // Map("ERROR" -> 2, "INFO" -> 1)
    .toList
    .sortBy((level, n) => (-n, level))
    .map((level, n) => s"$level: $n")
    .mkString("\n")                             // "ERROR: 2\nINFO: 1"
```

<v-click>

No `var`, no loop, no `null`, no exception: just **small pure functions composed together**.
That is exactly the spirit of the capstone (`Ex07Capstone`).

</v-click>

---

# Your turn: the exercises

```bash
sbt "~testOnly fp.Ex00*"     # run one part's tests, re-run on every save
sbt console                  # REPL: import fp.* then try Data.students
```

| Part | File | Time | Key ideas |
|---|---|---:|---|
| 0 | `Ex00Warmup` | 5' | setup, interpolation |
| 1 | `Ex01Collections` | 15' | `List` `Vector` `Set` `Map` `Option` |
| 2 | `Ex02Transformations` | 20' | `map` `filter` `flatMap` `collect` `for` |
| 3 | `Ex03Aggregations` | 20' | `foldLeft` `groupMapReduce` `sortBy` `sliding` |
| 4 | `Ex04Recursion` | 15' | `Nil` / `head :: tail`, `@tailrec` |
| 5 | `Ex05Functions` | 10' | lambdas, `Map` of functions |
| 6 | `Ex06HigherOrder` | 20' | `myMap`…, `compose`, `curry` |
| 7 | `Ex07Capstone` | 15' | parse → group → report |

<div class="text-sm opacity-80">

📏 Rules: replace each `???`, **no `var`, no loops**. Parts 4 & 6: **no library collection methods** — pattern matching and recursion only. ⭐ = bonus.

</div>

---

# Cheat sheet

<div class="grid grid-cols-2 gap-6 text-sm">
<div>

| I want to… | Use |
|---|---|
| transform each element | `map` |
| keep some elements | `filter` |
| one → many, drop `None`s | `flatMap` · `flatten` |
| filter + map at once | `collect { case … }` |
| test all / any | `forall` · `exists` |
| combine into one value | `foldLeft` · `sum` |
| best element, safely | `maxByOption` |
| sort | `sortBy` (negate for desc.) |

</div>
<div>

| I want to… | Use |
|---|---|
| bucket by key | `groupBy` · `groupMap` |
| count / sum by key | `groupMapReduce` |
| split in two | `partition` |
| consecutive pairs | `sliding(2)` |
| running totals | `scanLeft` |
| maybe a value | `Option` · `getOrElse` |
| nested loops | `for … yield` |
| build a string | `mkString` |

</div>
</div>

---
layout: center
class: text-center
---

# Happy hacking! 🚀

Small **pure** functions · **immutable** data · **compose** them

<div class="pt-6 opacity-70">
Questions?
</div>
