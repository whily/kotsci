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

/** Specialized 3-element vector. 
  * 
  * It is simpler to use companion object to create Vec3 instances.
  * 
  * @param x the 1st component
  * @param y the 2nd component
  * @param z the 3rd component
  */
class Vec3(var x: Double, var y: Double, var z: Double) {
  /** Returns a copy of current vector. */
  def copy(): Vec3 = {
    new Vec3(x, y, z)
  }

  /** Equals method.
    * 
    * Note that we define quals using mutable fields, which
    * might cause problems when using hash functions/collections.
    * Currently hashCode throws exception direclty; if we really need
    * to support this, we may make Vec3 as immutable (i.e. remove +=, -= methods).
    */
  override def equals(other: Any): Boolean = other match {
    case that: Vec3 => x == that.x && y == that.y && z == that.z
    case _ => false
  }

  /** Hashcode. Disabled to avoid problems with collections. */
  override def hashCode = 
    throw new IllegalArgumentException("Vec3: hashCode not supported.")

  /** Sets all elements to the input scalar. */
  def fill(v: Double) = {
    x = v
    y = v
    z = v
  }

  /** Unary negate. */
  def unary_- : Vec3 = new Vec3(-x, -y, -z)

  /** In-place plus. */
  def += (that: Vec3) {
    x += that.x
    y += that.y
    z += that.z
  }

  /** Binary plus. */
  def + (that: Vec3): Vec3 = {
    var u = copy()
    u += that
    u
  }

  /** In-place minus. */
  def -= (that: Vec3) {
    x -= that.x
    y -= that.y
    z -= that.z
  }

  /** Binary minus. */
  def - (that: Vec3): Vec3 = {
    var u = copy()
    u -= that
    u
  }

  /** In-place times with a scalar. */
  def *= (a: Double) {
    x *= a
    y *= a
    z *= a
  }

  /** Times with a scalar. */
  def * (a: Double): Vec3 = {
    var u = copy()
    u *= a
    u
  }

  /** In-place division with a scalar. */
  def /= (a: Double) {
    val b = 1.0 / a
    x *= b
    y *= b
    z *= b
  }

  /** Division with a scalar. */
  def / (a: Double): Vec3 = {
    var u = copy()
    u /= a
    u
  }

  /** Returns the dot product. */
  def ⋅ (that: Vec3): Double = x * that.x + y * that.y + z * that.z

  /** Returns the cross product, as in http://en.wikipedia.org/wiki/Cross_product */
  def × (that: Vec3): Vec3 = {
    new Vec3(y * that.z - z * that.y, z * that.x - x * that.z, x * that.y - y * that.x)
  }

  /** Returns the string form of the vector. */
  override def toString = "Vec3(" + x + ", " + y + ", " + z + ")"
}

/** Factory for [[net.whily.scasci.math.linalg.Vec3]] instance. */
object Vec3 {
  /** Creates a three element vector with given components. */
  def apply(x: Double, y: Double, z: Double) = new Vec3(x, y, z)
}
