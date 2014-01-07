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
import net.whily.scasci.math.Field

/** Dense matrix for linear algebra. Vector is treated as a special
  * matrix, i.e. nx1.  Matrix storage is column major, while index
  * starting from 0.
  * 
  * Use factory method in companion object to create a matrix and
  * factory method in object Vector to create a vector.
  *
  * @param data underlying array data in column major
  * @param rows number of rows 
 */ 
class Matrix[T](val data: Array[T], val rows: Int) {
  /** Number of elements in the matrix. */
  val size: Int = data.length

  /** Number of columns. */
  val cols: Int = {
    assert(rows > 0 && (size % rows == 0))
    size / rows
  }

  /** String representation of the matrix. */
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

  /** Returns true if the matrix is actually a vector. */
  val isVector: Boolean = (cols == 1 || rows == 1)

  /** Returns the element with the specified index, for vector only. */
  def apply(index: Int) = {
    assert(isVector)
    if (index < 0 || index >= size)
      throw new IndexOutOfBoundsException(index + " not in [0, " + size + ").")
    data(index)
  }

  /** Updates the element for the specified index, for vector only.
    * 
    * {{{
    * scala> x(3) = 4.0
    * }}}
    */
  def update(index: Int, v: T) {
    assert(isVector)
    if (index < 0 || index >= size)
      throw new IndexOutOfBoundsException(index + " not in [0, " + size + ").")
    data(index) = v
  }

  /** Returns the element at position (row, col), for vector only. */
  def apply(row: Int, col: Int) = {
    if (row < 0 || row >= rows || col < 0 || col >= cols)
      throw new IndexOutOfBoundsException((row, col) + " not in [0," + rows + ") x [0, " + cols + ")")
    data(row + col * rows)
  }

  /** Updates the element at position (row, col). */
  def update(row: Int, col: Int, v: T) = {
    if (row < 0 || row >= rows || col < 0 || col >= cols)
      throw new IndexOutOfBoundsException((row, col) + " not in [0," + rows + ") x [0, " + cols + ")")
    data(row + col * rows) = v
  }

  /** Returns true if that matrix has the same shape as current matrix. */
  def sameShapeAs(that: Matrix[T]): Boolean = {
    rows == that.rows && cols == that.cols
  }

  /** Binary addition.
    * 
    * Work around to avoid error "cannot find class tag for element type T"
    * According to https://github.com/danielmiladinov/scala-programming-learnings/blob/master/examples/generic_arrays.scala
    * Note that the import of scala.reflect.ClassTag is also due to this work around.
    */
  def + (that: Matrix[T])(implicit m: ClassTag[T], n: Field[T]): Matrix[T] = {
    assert(sameShapeAs(that))
    val d = new Array[T](size)
    val e = that.data
    for (i <- 0 until size)
      d(i) = n.plus(data(i), e(i))
    new Matrix[T](d, rows)
  }

  /** Binary subtraction. */
  def - (that: Matrix[T])(implicit m: ClassTag[T], n: Field[T]): Matrix[T] = {
    assert(sameShapeAs(that))
    val d = new Array[T](size)
    val e = that.data
    for (i <- 0 until size)
      d(i) = n.minus(data(i), e(i))
    new Matrix[T](d, rows)
  }
}

/** Factory for [[net.whily.scasci.math.linalg.Matrix]] instance which is a vector. */
object Vector {
  /** Creates a vector
    * 
    * @param xs a sequence of elements for the vector
    * @tparam T type of the elements
    */
  def apply[T](xs: T*)(implicit m: ClassTag[T]) = 
    new Matrix[T](xs.toArray, xs.length)
}

/** Factory for [[net.whily.scasci.math.linalg.Matrix]] instance. */
object Matrix{
}
