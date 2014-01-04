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

/** Trait Field is mainly used for class math.linalg.Matrix. */
trait Field[@specialized(Double) T] {
  def zero: T
  def one: T

  def + (x: T, y: T): T
  def - (x: T, y: T): T
  def * (x: T, y: T): T
  def / (x: T, y: T): T

  def == (x: T, y: T): Boolean
  def != (x: T, y: T): Boolean
  
  /** Approximately equal. */
  def ≈  (x: T, y: T): Boolean 
}

object Field {
  implicit object fieldD extends Field[Double] {
    def zero = 0.0
    def one = 1.0

    def + (x: Double, y: Double) = x + y
    def - (x: Double, y: Double) = x - y
    def * (x: Double, y: Double) = x * y
    def / (x: Double, y: Double) = x / y

    def == (x: Double, y: Double) = x == y
    def != (x: Double, y: Double) = x != y
    def ≈  (x: Double, y: Double): Boolean = {
      val tolerance = 1e-4
      (x - y).abs <= math.max(x.abs, y.abs) * 1e-4
    }
  }
}
