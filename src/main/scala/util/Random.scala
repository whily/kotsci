/**
 * Random number generator.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2014 Yujian Zhang
 */

package net.whily.scasci.util

/** Random number generator using mt19937: 
  *   http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/emt.html 
  * The interface follows scala.util.Random.
  * 
  * @tparam seed the seed to create the random number generator.
  */
class Random(seed: Int) {
  // Implementation is based on http://www.math.sci.hiroshima-u.ac.jp/~m-mat/MT/MT2002/CODES/mt19937ar.c

  // Period parameters.
  private val N = 624
  private val M = 397
  private val MatrixA = 0x9908b0dfL  // constant vector a
  private val UpperMask = 0x80000000L // most significant w-r bits
  private val LowerMask = 0x7fffffffL // least significant r bits

  private val mt = new Array[Long](N) // the array for the state vector
  private var mti = 1

  // initializes mt[N] with a seed
  mt(0)= seed
  while (mti < N) {
    mt(mti) = (1812433253L * (mt(mti - 1) ^ (mt(mti-1) >>> 30)) + mti) & 0xffffffffL
    mti += 1
  }

  /** Creates a random number generator. Seed is from the system time. */
  def this() = this(System.currentTimeMillis().toInt)

  /** Returns the next pseudorandom, uniformly distributed boolean value from
    * this random number generator's sequence. */
  def nextBoolean(): Boolean = (nextUnsignedInt32() & 1L) == 0L

  /** Returns the next pseudorandom, uniformly distributed double value [0, 1)
    * from this random number generator's sequence. */
  def nextDouble(): Double =   nextUnsignedInt32() * (1.0 / 4294967296.0)

  // Since Gaussian variables are generated in a pair, we keep the extra
  // one here.
  private var hasNextNextGaussian = false
  private var nextNextGaussian = 0.0

  /** Returns the next pseudorandom, Gaussian ("normally") distributed double 
    * value N(0, 1). */
  def nextGaussian(): Double = {
    // Use polar form of the Box Muller method as in
    // http://www.design.caltech.edu/erik/Misc/Gaussian.html
    // We use StrictMath by following http://docs.oracle.com/javase/7/docs/api/java/util/Random.html#nextGaussian()

    if (hasNextNextGaussian) {
      hasNextNextGaussian = false
      nextNextGaussian
    } else {
      var x1 = 0.0
      var x2 = 0.0
      var w = 0.0
 
      do {
        x1 = 2.0 * nextDouble() - 1.0
        x2 = 2.0 * nextDouble() - 1.0
        w = x1 * x1 + x2 * x2
      } while (w >= 1.0)

      w = StrictMath.sqrt((-2.0 * StrictMath.log(w)) / w)
      hasNextNextGaussian = true
      nextNextGaussian = x2 * w
      x1 * w
    }
  }

  /** Returns a pseudorandom, uniformly distributed int value between 
    * 0 (inclusive) and the specified value (exclusive), drawn from 
    * this random number generator's sequence. */
  def nextInt(n: Int): Int = {
    // Straighforward modulo implementation is not accurate. Rejection method
    // is used
    if (n <= 0)
      throw new IllegalArgumentException("n must be positive");

    val RandMax = 0xffffffffL
    val nn = n.toLong
    val top = ((RandMax + 1L) / nn) * nn - 1L
    var r = 0L
    do {
      r = nextUnsignedInt32()
    } while (r > top)
    (r % n).toInt
  }

  /** Returns the next pseudorandom, uniformly distributed int value from this 
    * random number generator's sequence. */
  def nextInt(): Int = nextUnsignedInt32().toInt

  /** Returns the next pseudorandom, uniformly distributed long value from this 
    * random number generator's sequence. */
  def nextLong(): Long = (nextUnsignedInt32() << 32) + nextUnsignedInt32()

  /** Returns the next pseudorandom, uniformly distributed integer value
    * in [0, 0xffffffff] */
  def nextUnsignedInt32(): Long = {
    var y = 0L

    if (mti >= N) { // generate N words at one time
      val mag01 = Array(0L, MatrixA)
      var kk = 0

      while (kk < N - M) {
        y = (mt(kk) & UpperMask) | (mt(kk + 1) & LowerMask)
        mt(kk) = mt(kk + M) ^ (y >>> 1) ^ mag01(y.toInt & 1)
        kk += 1
      }
      while (kk < N - 1) {
        y = (mt(kk) & UpperMask) | (mt(kk + 1) & LowerMask)
        mt(kk) = mt(kk + (M - N)) ^ (y >>> 1) ^ mag01(y.toInt & 1)
        kk += 1
      }

      y = (mt(N - 1) & UpperMask) | (mt(0) & LowerMask)
      mt(N - 1) = mt(M - 1) ^ (y >>> 1) ^ mag01(y.toInt & 1)

      mti = 0
    }
  
    y = mt(mti)
    mti += 1

    // Tempering
    y ^= (y >>> 11)
    y ^= (y << 7) & 0x9d2c5680L
    y ^= (y << 15) & 0xefc60000L
    y ^= (y >>> 18)

    y
  }
}
