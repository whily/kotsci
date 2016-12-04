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

import net.whily.scasci.math._
import net.whily.scasci.math.linalg._
import net.whily.scasci.phys._
import org.scalatest.Matchers
import org.scalatest.FunSpec

class NBodySpec extends FunSpec with Matchers {
  describe("Two body as in section 3.1 and 4.7 of http://www.artcompsci.org/kali/vol/n_body_problem/volume4.pdf") {
    val sim = NBody.twoBodyParamSim
    sim.evolve(10.0)

    it("correct position and velocity for the 1st body") {
      val pos = sim.bodies(0).pos
      val vel = sim.bodies(0).vel
      (pos ≈ Vec3(1.1992351e-01, -7.2126917e-02, 0.0)) should be (true)
      (vel ≈ Vec3(2.0616138e-01, 4.2779061e-02, 0.0)) should be (true)
    }

    it("correct position and velocity for the 2nd body") {
      val pos = sim.bodies(1).pos
      val vel = sim.bodies(1).vel
      (pos ≈ Vec3(-4.7969404e-01, 2.8850767e-01, 0.0)) should be (true)
      (vel ≈ Vec3(-8.2464553e-01, -1.7111624e-01, 0.0)) should be (true)
    }

    it("should have small relative energy error") {
      math.abs(sim.relativeEnergyError) should be < 2e-13
    }
  }

  describe("Three body figure 8 as in section 5.1 and 5.2 of http://www.artcompsci.org/kali/vol/n_body_problem/volume4.pdf") {
    val sim = NBody.figure8ParamSim
    sim.evolve(2.1088)

    it("correct position and velocity for the 1st body") {
      val pos = sim.bodies(0).pos
      val vel = sim.bodies(0).vel
      (pos ≈ Vec3(2.5982241e-5, -2.0259655e-5, 0.0)) should be (true)
      (vel ≈ Vec3(-0.93227637, -0.86473501, 0.0)) should be (true)
    }

    it("correct position and velocity for the 2nd body") {
      val pos = sim.bodies(1).pos
      val vel = sim.bodies(1).vel
      (pos ≈ Vec3(0.97011046, -0.24305269, 0.0)) should be (true)
      (vel ≈ Vec3(0.46619301, 0.43238574, 0.0)) should be (true)
    }

    it("correct position and velocity for the 3rd body") {
      val pos = sim.bodies(2).pos
      val vel = sim.bodies(2).vel
      (pos ≈ Vec3(-0.97013644, 0.24307295, 0.0)) should be (true)
      (vel ≈ Vec3(0.46608336, 0.43234927, 0.0)) should be (true)
    }

    it("should have small relative energy error") {
      math.abs(sim.relativeEnergyError) should be < 1e-13
    }
  }

  describe("Test energy of three body configurations from http://suki.ipb.ac.rs/3body/") {
    it("Energy test") {
      for (config <- NBody.threeBodyConfigs) {
        val sim = new NBody(config, 0.0001)
        Field.fieldD.≈(sim.totalEnergy(), config.energy) should be (true)
        Field.fieldD.≈(sim.angularMomentum(), config.angularMomentum) should be (true)
        sim.evolve(1.0)
        math.abs(sim.relativeEnergyError) should be < 2e-11
      }
    }
  }
}
