/**
 * Rectangle.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2015 Yujian Zhang
 */

package net.whily.scasci.geo

/** Rectangle with edges parallel to axes.
  */
class Rect(val x1: Double, val y1: Double, val x2: Double, val y2: Double) {
  assert((x1 <= x2) && (y1 <= y2))
  /** Returns true if the rectangle contains the point `p`. */
  def contains(p: Point) = {
    (x1 <= p.x) && (p.x <= x2) && (y1 <= p.y) && (p.y <= y2)
  }

  /** Returns true if the rectangle intersects `that` rectangel. */
  def intersects(that: Rect) = {
    (Math.max(x1, that.x1) <= Math.min(x2, that.x2)) &&
    (Math.max(y1, that.y1) <= Math.min(y2, that.y2))
  }
}

/** Factory for [[net.whily.scasci.geo.Rect]] instance. */
object Rect {
  /** Creates a Rect. */
  def apply(x1: Double, y1: Double, x2: Double, y2: Double) = new Rect(x1, y1, x2, y2)
}

