/**
 * Test cases for Matrix.scala.
 * 
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

import net.whily.scasci.math.linalg._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
 
class MatrixSpec extends FunSpec with ShouldMatchers {
  val v = Vector(1.0, 2.0, 3.0)

  describe("For vector [1 2 3]'") {
    it("number of rows is 3") {
      v.rows should be (3)
    }

    it("number of columns is 1") {
      v.cols should be (1)
    }

    it("is a vector") {
      v.isVector should be (true)
    }

    it("the 2nd element is 2") {
      v(1) should be (2.0)
    }

    it("after fill") {
      v.fill(1.0)
      v should be (Vector(1.0, 1.0, 1.0))
    }
  }
}
