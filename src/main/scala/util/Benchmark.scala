/**
 * Benchmark utilities.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2014 Yujian Zhang
 */

package net.whily.scasci.util

/** Provides functions for benchmarking.
  */
object Benchmark {
  // Implementation based on discussion in 
  //   http://stackoverflow.com/questions/4753629/how-do-i-make-a-class-generic-for-all-numeric-types

  /** Benchmarks the input function. This is a combination of benchmark() and run(). 
    * Assumes tuneIn = 2.
    * 
    * @param n number of runs for the function
    * @param f the function to run
    */
  def bench[T](n: Int, f: => T): T = benchmark(2, run(n, f))

  /** Benchmarks the input function.
    * 
    * @param tuneIn number of runs before the actual results are shown.
    * @param f the function to be benchmarked.
    */
  def benchmark[T](tuneIn: Int, f: => T): T = {
    for (i <- 0 until tuneIn) {
      @volatile var result = f
    }
    val t0 = System.nanoTime
    val result = f
    printf("Elapsed: %.4f s\n", (System.nanoTime - t0) * 1e-9)
    result
  }

  /** Runs a function multiple times.
    * 
    * @param n number of runs for the function
    * @param f the function to run
    */
  def run[T](n: Int, f: => T): T = {
    if (n > 1) { 
      f
      run(n - 1, f)
    } else f
  }
}
