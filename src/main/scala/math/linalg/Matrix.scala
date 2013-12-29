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

import scala.reflect.ClassTag

/** 
 * Dense matrix for linear algebra. Vector is treated as a special matrix, i.e. nx1.
 * Matrix storage is column major, while index starting from 0.
 *
 * @param data underlying array data in column major
 * @param rows number of rows 
 */ 
class Matrix[@specialized(Int, Double, Float) T](val data: Array[T], val rows: Int) {
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
        s.append(data(row + col * rows))
        s.append(" ")
      }
      if (row < rows - 1) s.append("\n")
    }
    s.append("]")
    s.toString
  }

  val isVector: Boolean = (cols == 1 || rows == 1)

  def apply(index: Int) = {
    assert(isVector)
    if (index < 0 || index >= size)
      throw new IndexOutOfBoundsException(index + " not in [0, " + size + ").")
    data(index)
  }

  def apply(row: Int, col: Int) = {
    if (row < 0 || row >= rows || col < 0 || col >= cols)
      throw new IndexOutOfBoundsException((row, col) + " not in [0," + rows + ") x [0, " + cols + ")")
    data(row + col * rows)
  }

  def sameShapeAs(that: Matrix[T]): Boolean = {
    rows == that.rows && cols == that.cols
  }

  // Work around to avoid error "cannot find class tag for element type T"
  // According to https://github.com/danielmiladinov/scala-programming-learnings/blob/master/examples/generic_arrays.scala
  // Note that the import of scala.reflect.ClassTag is also due to this work around.
  def + (that: Matrix[T])(implicit m: ClassTag[T]): Matrix[T] = {
    assert(sameShapeAs(that))
    val d = new Array[T](size)
    val e = that.data
    //for (i <- 0 until size)
    //  d(i) = data(i) + e(i)
    new Matrix[T](d, rows)
  }
}
