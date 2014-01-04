/**
 * Test cases for Vec.scala.
 * 
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

import net.whily.scasci.math.linalg.Vec3
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
 
class Specs extends FunSpec with ShouldMatchers {
  describe("In class Vec3") {
    it("dot prodcut") {
      (new Vec3(1.0, 3.0, -5.0)) ⋅ (new Vec3(4.0, -2.0, -1.0)) should be (3.0)
    }
  }
}
