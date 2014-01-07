/**
 * Class for special vectors.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

package net.whily.scasci.math.linalg

/** Specialized 3-element vector. */
class Vec3(var x: Double, var y: Double, var z: Double) {
  def copy(): Vec3 = {
    new Vec3(x, y, z)
  }

  // Note that we define quals using mutable fields, which
  // might cause problems when using hash functions/collections.
  // Currently hashCode throws exception direclty; if we really need
  // to support this, we may make Vec3 as immutable (i.e. remove +=, -= methods).

  override def equals(other: Any): Boolean = other match {
    case that: Vec3 => x == that.x && y == that.y && z == that.z
    case _ => false
  }

  override def hashCode = 
    throw new IllegalArgumentException("Vec3: hashCode not supported.")

  /** Set all elements to `v`. */
  def fill(v: Double) = {
    x = v
    y = v
    z = v
  }

  def unary_- : Vec3 = new Vec3(-x, -y, -z)

  def += (that: Vec3) {
    x += that.x
    y += that.y
    z += that.z
  }

  def + (that: Vec3): Vec3 = {
    var u = copy()
    u += that
    u
  }

  def -= (that: Vec3) {
    x -= that.x
    y -= that.y
    z -= that.z
  }

  def - (that: Vec3): Vec3 = {
    var u = copy()
    u -= that
    u
  }

  def *= (a: Double) {
    x *= a
    y *= a
    z *= a
  }

  def * (a: Double): Vec3 = {
    var u = copy()
    u *= a
    u
  }

  def /= (a: Double) {
    val b = 1.0 / a
    x *= b
    y *= b
    z *= b
  }

  def / (a: Double): Vec3 = {
    var u = copy()
    u /= a
    u
  }

  /** Dot product. */
  def ⋅ (that: Vec3): Double = x * that.x + y * that.y + z * that.z

  /** Cross product, as in http://en.wikipedia.org/wiki/Cross_product */
  def × (that: Vec3): Vec3 = {
    new Vec3(y * that.z - z * that.y, z * that.x - x * that.z, x * that.y - y * that.x)
  }

  override def toString = "Vec3(" + x + ", " + y + ", " + z + ")"
}

object Vec3 {
  def apply(x: Double, y: Double, z: Double) = new Vec3(x, y, z)
}
