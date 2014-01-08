/**
 * Test cases for NBody.scala.
 * 
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

import net.whily.scasci.math.linalg._
import net.whily.scasci.phys._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
 
class NBodySpec extends FunSpec with ShouldMatchers {
  describe("Two body as in section 3.1 and 4.7 of http://www.artcompsci.org/kali/vol/n_body_problem/volume4.pdf") {
    it("Leapfrog: should have small relative energy error") {
      val sim = NBody.twoBodySim
      sim.evolve("leapfrog")
      (math.abs(sim.relativeEnergyError) < 1e-8) should be (true)
    }

    it("RK2: should have small relative energy error") {
      val sim = NBody.twoBodySim
      sim.evolve("rk2")
      (math.abs(sim.relativeEnergyError) < 1e-7) should be (true)
    }
    
    val sim = NBody.twoBodySim
    sim.evolve("rk4")

    it("RK4: correct position and velocity for the 1st body") {
      val pos = sim.bodies(0).pos
      val vel = sim.bodies(0).vel
      (pos ≈ Vec3(1.1992351e-01, -7.2126917e-02, 0.0)) should be (true)
      (vel ≈ Vec3(2.0616138e-01, 4.2779061e-02, 0.0)) should be (true)
    }

    it("RK4: correct position and velocity for the 2nd body") {
      val pos = sim.bodies(1).pos
      val vel = sim.bodies(1).vel
      (pos ≈ Vec3(-4.7969404e-01, 2.8850767e-01, 0.0)) should be (true)
      (vel ≈ Vec3(-8.2464553e-01, -1.7111624e-01, 0.0)) should be (true)
    }

    it("RK4: should have small relative energy error") {
      (math.abs(sim.relativeEnergyError) < 1e-13) should be (true)
    }
  }
}
