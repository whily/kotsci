/**
 * 2D Point.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2015 Yujian Zhang
 */

package net.whily.scasci.geo

/** 2D Point.
  */
case class Point(x: Double, y: Double) {
  /** Return Euclidian distance from this point to `that`. */
  def distTo(that: Point) = {
    val dx = x - that.x
    val dy = y - that.y
    math.sqrt(dx * dx + dy * dy)
  }
}
