/**
 * Test cases for Vec.scala.
 * 
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2014 Yujian Zhang
 */

import net.whily.scasci.util.Random
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
 
class RandomSpec extends FunSpec with ShouldMatchers {
  describe("Test RNG output") {
    val rng = new Random(5489)
    it("For the first 1000 integers, check the first and the last") {
      val a = new Array[Long](1000)
      for (i <- 0 until 1000) {
        a(i) = rng.nextUnsignedInt32()
      }
      a(0) should be (3499211612L)
      a(999) should be (1341017984L)
    }

    it("For the first 1000 doubles, check the first and the last") {
      val b = new Array[Double](1000)
      for (i <- 0 until 1000) {
        b(i) = rng.nextDouble()
      }
      b(0) should be (0.58224916 +- 0.00000001)
      b(999) should be (0.91948258 +- 0.00000001)
    }
  }
}
