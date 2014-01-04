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

  def + (that: Vec3): Vec3 = {
    var u = copy()
    u += that
    u
  }

  def += (that: Vec3) {
    x += that.x
    y += that.y
    z += that.z
  }

  def - (that: Vec3): Vec3 = {
    var u = copy()
    u -= that
    u
  }

  def -= (that: Vec3) {
    x -= that.x
    y -= that.y
    z -= that.z
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
  def Vec3(x: Double, y: Double, z: Double) = new Vec3(x, y, z)
}
