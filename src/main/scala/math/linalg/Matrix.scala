/**
 * Matrix class.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

package net.whily.scasci.math.linalg

/** 
 * Dense matrix for linear algebra. Vector is treated as a special matrix, i.e. nx1.
 * Matrix storage is column major, while index starting from 0.
 *
 * @param data underlying array data
 * @param rows number of rows 
 */ 
class Matrix[T](val data: Array[T], val rows: Int) {
  val size: Int = data.length

  val cols: Int = {
    assert(rows > 0 && (size % rows == 0))
    data.length / rows
  }

  override def toString = {
    // TODO: this is a naive implementation without considering big matrix.
    val s = new scala.StringBuilder
    s.append("[ ")
    for (row <- 0 until rows) {
      if (row > 0) s.append("  ")
      for (col <- 0 until cols) {
        s.append(data(col + row * cols))
        s.append(" ")
      }
      if (row < rows - 1) s.append("\n")
    }
    s.append("]")
    s.toString
  }

  val isVector: Boolean = (rows == 1 || cols == 1)

  def apply(index: Int) = {
    assert(isVector)
    if (index < 0 || index >= size)
      throw new IndexOutOfBoundsException(index + " not in [0, " + size + ").")
    data(index)
  }
}
