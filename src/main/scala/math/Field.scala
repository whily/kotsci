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

  def +(x: T, y: T): T
  def -(x: T, y: T): T
  def *(x: T, y: T): T
  def /(x: T, y: T): T

  def ==(x: T, y: T): Boolean
  def !=(x: T, y: T): Boolean
}

object Field {
  implicit object fieldD extends Field[Double] {
    def zero = 0.0
    def one = 1.0

    def +(a: Double, b: Double) = a + b
    def -(a: Double, b: Double) = a - b
    def *(a: Double, b: Double) = a * b
    def /(a: Double, b: Double) = a / b

    def ==(a: Double, b: Double) = a == b
    def !=(a: Double, b: Double) = a != b
  }
}
