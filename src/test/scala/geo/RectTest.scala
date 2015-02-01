/**
 * Test cases for Rect.scala.
 * 
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2015 Yujian Zhang
 */

import net.whily.scasci.geo.Rect
import org.scalatest._
 
class RectTest extends FunSpec with Matchers {
  describe("Test class Rect") {
    it("Check intersects()") {
      Rect(0, 0, 2, 2).intersects(Rect(1, 1, 3, 3)) should be (true)
      Rect(0, 0, 2, 2).intersects(Rect(1, 4, 1.5, 5)) should be (false)      
    }
  }
}
