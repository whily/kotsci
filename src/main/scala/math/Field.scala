/**
 * Trait field.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

package net.whily.scasci.math

/** Modelled according to field concept in algebra. Mainly used for
  * class math.linalg.Matrix.
  * 
  * @tparam T data type. Mainly Double, Complex, and GF (latter two 
  *           not implemented yet).
  */
trait Field[@specialized(Double) T] {
  /** Value 0. */
  def zero: T
  
  /** Value 1. */
  def one: T

  /** Binary addition. */
  def plus(x: T, y: T): T

  /** Binary subtraction. */
  def minus(x: T, y: T): T

  /** Binary multiplication. */
  def times(x: T, y: T): T

  /** Binary division. */
  def div(x: T, y: T): T

  /** Approximately equal. */
  def ≈  (x: T, y: T): Boolean 
}

object Field {
  implicit object fieldD extends Field[Double] {
    def zero = 0.0
    def one = 1.0

    def plus(x: Double, y: Double) = x + y
    def minus(x: Double, y: Double) = x - y
    def times(x: Double, y: Double) = x * y
    def div(x: Double, y: Double) = x / y

    def ≈  (x: Double, y: Double): Boolean = {
      val tolerance = 1e-4
      (x - y).abs <= math.max(x.abs, y.abs) * 1e-4
    }
  }
}
