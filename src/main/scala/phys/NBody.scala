/**
  * N-body simulation. 
  * 
  * Reference: http://www.artcompsci.org/kali/vol/indiv_timesteps_1/volume15.pdf
  * 
  * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
  *
  * License:
  *   GNU General Public License v2
  *   http://www.gnu.org/licenses/gpl-2.0.html
  * Copyright (C) 2013 Yujian Zhang
  */

package net.whily.scasci.phys

import net.whily.scasci.math.linalg._

/** Physical particle for N-body simulation.  We use the general term
  * instead of specific names like particles. Individual time step
  * approach is used.
  *
  * @param mass in kg
  * @param pos position in m
  * @param vel velocity in m / s
  */
class Body(val mass: Double, var pos: Vec3, var vel: Vec3) {
  /** acceleration in m / s^2 */
  var acc = Vec3.zeros

  /** jerk in m / s^3 */
  var jerk = Vec3.zeros

  var time = 0.0
  var nextTime = 0.0
  var predPos = Vec3.zeros
  var predVel = Vec3.zeros

  def autonomousStep(bodies: Array[Body] , ΔtParam: Double) {
    takeOneStep(bodies, nextTime, ΔtParam)
  }

  def forcedStep(bodies: Array[Body], t: Double, ΔtParam: Double) {
    takeOneStep(bodies, t, ΔtParam)
  }

  def takeOneStep(bodies: Array[Body], t: Double, ΔtParam: Double) {
    for (body <- bodies) body.predictStep(t)
    correctStep(bodies, t, ΔtParam)
  }

  def predictStep(t: Double) {
    assert(t <= nextTime)
    val Δt = t - time
    predPos = pos + vel * Δt + acc * (Δt * Δt / 2.0) + jerk * (Δt * Δt * Δt / 6.0)
    predVel = vel + acc * Δt + jerk * (Δt * Δt / 2.0)
  }

  def correctStep(bodies: Array[Body], t: Double, ΔtParam: Double) {
    val Δt = t - time
    val (newAcc, newJerk) = getAccAndJerk(bodies)
    val newVel = vel + (acc + newAcc) * (Δt / 2.0) + 
      (jerk - newJerk) * (Δt * Δt / 12.0)
    val newPos = pos + (vel + newVel) * (Δt / 2.0) +
      (acc - newAcc) * (Δt * Δt / 12.0)
    pos.copyFrom(newPos)
    vel.copyFrom(newVel)
    acc.copyFrom(newAcc)
    jerk.copyFrom(newJerk)
    predPos.copyFrom(pos)
    predVel.copyFrom(vel)
    time = t
    nextTime = time + collisionTimeScale(bodies) * ΔtParam
  }

  def collisionTimeScale(bodies: Array[Body]) = {
    var timeScaleSq = 1e300 // A very large number.
    for (body <- bodies) {
      if (!(body eq this)) {
        val r = body.predPos - predPos
        val v = body.predVel - predVel
        val r2 = r ⋅ r
        val v2 = v ⋅ v
        var estimateSq = r2 / v2
        if (timeScaleSq > estimateSq) {
          timeScaleSq = estimateSq
        }
        val a = (mass + body.mass) / r2
        estimateSq = math.sqrt(r2) / a
        if (timeScaleSq > estimateSq) {
          timeScaleSq = estimateSq
        }
      }
    }
    math.sqrt(timeScaleSq)
  }

  def getAccAndJerk(bodies: Array[Body]) = {
    var a = Vec3.zeros
    var j = Vec3.zeros
    for (body <- bodies) {
      if (!(body eq this)) {
        val r = body.predPos - predPos
        val r2 = r ⋅ r
        val r3 = r2 * math.sqrt(r2)
        val v = body.predVel - predVel
        a += r * (body.mass / r3)
        j += (v - r * (3 * (r ⋅ v) / r2)) * (body.mass / r3)
      }
    }
    (a, j)
    // We assume G = 1, otherwise, use the following line
    // (a * G, j * G)
  }

  /** Returns the kinetic energy of the particle. */
  def kineticEnergy() = {
    0.5 * mass * (vel ⋅ vel)
  }

  /** Returns the potential energy of the particle. */
  def potentialEnergy(bodies: Array[Body]) = {
    var p = 0.0
    for (body <- bodies) {
      if (!(body eq this)) {
        p += body.mass / (body.pos - pos).norm
      }
    }
    -mass * p
    // We assume G = 1, otherwise use the following line:
    // -G * mass * p
  }

  /** Return the angular momentum in vector form. */
  def angularMomentum() = {
    (pos * mass) × vel
  }
}

/** Performs N-body simulation with individual time step, Hermite integrator. Based on
  * http://www.artcompsci.org/kali/development.html
  *
  * In N-body simulation, we normally assume G = 1.
  *
  * A typical example for this class is as follows:
  * {{{
  * val sim = NBody.figure8Sim
  * sim.evolve("rk4")
  * }}}
  *
  * @param bodies bodies for simulation. Positions and velocities are already initialized.
  * @param ΔtParam parameter controlling time quantum, in s
  * @param duration simulation running duration
  */
class NBody(val bodies: Array[Body], val ΔtParam: Double) {
  private val n = bodies.length
  var time = 0.0
  val initialEnergy = totalEnergy()
  for (body <- bodies) {
    body.predPos.copyFrom(body.pos)
    body.predVel.copyFrom(body.vel)
  }
  for (body <- bodies) {
    val (a, j) = body.getAccAndJerk(bodies)
    body.acc.copyFrom(a)
    body.jerk.copyFrom(j)
    body.time = time
    body.nextTime = time + body.collisionTimeScale(bodies) * ΔtParam
  }

  def this(config: NBodyConfig, Δt: Double) = this(config.bodies, Δt)

  /** Run N-body simulation until current time >= tEnd. 
    * 
    * @param tEnd the end time
    */
  def evolve(tEnd: Double) {
    while (time < tEnd) {
      val np = findNextParticle()
      time = np.nextTime
      if (time < tEnd) {
        np.autonomousStep(bodies, ΔtParam)
      }
    }
    sync(tEnd)
  }

  def findNextParticle() = {
    var nextTime = 1e300 // A very large number.
    var nextParticle: Body = null
    for (body <- bodies) {
      if (nextTime > body.nextTime) {
        nextTime = body.nextTime
        nextParticle = body
      }
    }
    nextParticle
  }

  def sync(t: Double) {
    for (body <- bodies) {
      body.forcedStep(bodies, t, ΔtParam)
    }
    time = t
  }

  /** Returns the kinetic energy of the system. */
  def kineticEnergy() = (0.0 /: bodies)(_ + _.kineticEnergy())

  /** Returns the potential energy of the system. Note that 0.5 is used
    * since the pairwise potential energy is calcualted twice.
    */
  def potentialEnergy() = (0.5 * (0.0 /: bodies)(_ + _.potentialEnergy(bodies)))

  /** Returns the total energy (kinetic + potential) of the particle. */
  def totalEnergy() = kineticEnergy() + potentialEnergy()

  /** Returns the relative energey error. */
  def relativeEnergyError() = (totalEnergy() - initialEnergy) / initialEnergy

  /** Return the angular momentum in scalar. */
  def angularMomentum() = {
    var sum = Vec3.zeros
    for (body <- bodies)
      sum += body.angularMomentum()
    sum.norm
  }
}

/** Periodic solutions for N-body problem. Such configuration is
  * mainly used for demo, as well as regressions test cases.
  * 
  * Most three body solutions are from http://suki.ipb.ac.rs/3body/
  * 
  * @param name solution name
  * @param discovered the year the solution was discovered
  * @param period solution period
  * @param energy solution energy
  * @param angularMomentum angular momentum (the norm)
  * @param bodies an array of bodies 
  */
case class NBodyConfig(name: String, discovered: Int, period: Double, energy: Double,
  angularMomentum: Double, bodies: Array[Body])

/** Provides example configurations for testing. We use def instead of
  * val so that simulations could be run again and again.
  */
object NBody {
  // Configuration from section 3.1 of http://www.artcompsci.org/kali/vol/n_body_problem/volume4.pdf
  def twoBodyParam = Array(
    new Body(0.8, Vec3(0.2, 0.0, 0.0), Vec3(0.0, 0.1, 0.0)),
    new Body(0.2, Vec3(-0.8, 0.0, 0.0), Vec3(0.0, -0.4, 0.0)))
  def twoBodyParamSim = new NBody(twoBodyParam, 0.0001) // Duration: 10.0

  // Figure-eight three body configuration discovered by Montgomery and Chenciner.
  // From section 5.1 of http://www.artcompsci.org/kali/vol/n_body_problem/volume4.pdf.
  def figure8Param = Array(
    new Body(1.0, Vec3(0.9700436, -0.24308753, 0.0),
      Vec3(0.466203685, 0.43236573, 0.0)),
    new Body(1.0, Vec3(-0.9700436, 0.24308753, 0.0),
      Vec3(0.466203685, 0.43236573, 0.0)),
    new Body(1.0, Vec3(0.0, 0.0, 0.0), 
      Vec3(-0.93240737, -0.86473146, 0.0)))
  def figure8ParamSim = new NBody(figure8Param, 0.0001) // Duration: 2.1088

  // Below configurations are from three body gallery of http://suki.ipb.ac.rs/3body/

  // Broucke A 1 in http://suki.ipb.ac.rs/3body/bsol.php?id=0
  def brouckeA1Config = NBodyConfig(
    "Broucke A 1",
    1975,
    6.283213,
    -0.854131,
    1.098206,
    Array(
      new Body(1.0, Vec3(-0.9892620043, 0.0, 0.0),
      Vec3(0.0, 1.9169244185, 0.0)),
      new Body(1.0, Vec3(2.2096177241, 0.0, 0.0),
        Vec3(0.0, 0.1910268738, 0.0)),
      new Body(1.0, Vec3(-1.2203557197, 0.0, 0.0),
        Vec3(0.0, -2.1079512924, 0.0))))

  // Broucke A 2 in http://suki.ipb.ac.rs/3body/bsol.php?id=1
  def brouckeA2Config = NBodyConfig(
    "Broucke A 2",
    1975,
    7.702408,
    -1.751113,
    1.030573,
    Array(
      new Body(1.0, Vec3(0.3361300950, 0.0, 0.0),
        Vec3(0.0, 1.5324315370, 0.0)),
      new Body(1.0, Vec3(0.7699893804, 0.0, 0.0),
        Vec3(0.0, -0.6287350978, 0.0)),
      new Body(1.0, Vec3(-1.1061194753, 0.0, 0.0),
        Vec3(0.0, -0.9036964391, 0.0))))

  // Broucke A 3 in http://suki.ipb.ac.rs/3body/bsol.php?id=2
  def brouckeA3Config = NBodyConfig(
    "Broucke A 3",
    1975,
    7.910268,
    -1.592078,
    1.014235,
    Array(
      new Body(1.0, Vec3(0.3149337497, 0.0, 0.0),
        Vec3(0.0, 1.4601869417, 0.0)),
      new Body(1.0, Vec3(0.8123820710, 0.0, 0.0),
        Vec3(0.0, -0.5628292375, 0.0)),
      new Body(1.0, Vec3(-1.1273158206, 0.0, 0.0),
        Vec3(0.0, -0.8973577042, 0.0))))

  // Broucke A 4 in http://suki.ipb.ac.rs/3body/bsol.php?id=3
  def brouckeA4Config = NBodyConfig(
    "Broucke A 4",
    1975,
    8.211617,
    -1.419482,
    0.994330,
    Array(
      new Body(1.0, Vec3(0.2843198916, 0.0, 0.0),
        Vec3(0.0, 1.3774179570, 0.0)),
      new Body(1.0, Vec3(0.8736097872, 0.0, 0.0),
        Vec3(0.0, -0.4884226932, 0.0)),
      new Body(1.0, Vec3(-1.1579296788, 0.0, 0.0),
        Vec3(0.0, -0.8889952638, 0.0))))

  // Broucke A 5 in http://suki.ipb.ac.rs/3body/bsol.php?id=4
  def brouckeA5Config = NBodyConfig(
    "Broucke A 5",
    1975,
    8.689105,
    -1.227435,
    0.969590,
    Array(
      new Body(1.0, Vec3(0.2355245585, 0.0, 0.0),
        Vec3(0.0, 1.2795329643, 0.0)),
      new Body(1.0, Vec3(0.9712004534, 0.0, 0.0),
        Vec3(0.0, -0.4021329019, 0.0)),
      new Body(1.0, Vec3(-1.2067250118, 0.0, 0.0),
        Vec3(0.0, -0.8774000623, 0.0))))

  // Broucke A 6 in http://suki.ipb.ac.rs/3body/bsol.php?id=5
  def brouckeA6Config = NBodyConfig(
    "Broucke A 6",
    1975,
    9.593323,
    -1.004011,
    0.939579,
    Array(
      new Body(1.0, Vec3(0.1432778606, 0.0, 0.0),
        Vec3(0.0, 1.1577475241, 0.0)),
      new Body(1.0, Vec3(1.1556938491, 0.0, 0.0),
        Vec3(0.0, -0.2974667752, 0.0)),
      new Body(1.0, Vec3(-1.2989717097, 0.0, 0.0),
        Vec3(0.0, -0.8602807489, 0.0))))

  // Broucke A 7 in http://suki.ipb.ac.rs/3body/bsol.php?id=6
  def brouckeA7Config = NBodyConfig(
    "Broucke A 7",
    1975,
    12.055859,
    -0.717506,
    0.925301,
    Array(
      new Body(1.0, Vec3(-0.1095519101, 0.0, 0.0),
        Vec3(0.0, 0.9913358338, 0.0)),
      new Body(1.0, Vec3(1.6613533905, 0.0, 0.0),
        Vec3(0.0, -0.1569959746, 0.0)),
      new Body(1.0, Vec3(-1.5518014804, 0.0, 0.0),
        Vec3(0.0, -0.8343398592, 0.0))))

  // Broucke A 8 in http://suki.ipb.ac.rs/3body/bsol.php?id=7
  def brouckeA8Config = NBodyConfig(
    "Broucke A 8",
    1975,
    18.118189,
    -1.120841,
    0.955094,
    Array(
      new Body(1.0, Vec3(0.1979259967, 0.0, 0.0),
        Vec3(0.0, 1.2224733132, 0.0)),
      new Body(1.0, Vec3(1.0463975768, 0.0, 0.0),
        Vec3(0.0, -0.3527351133, 0.0)),
      new Body(1.0, Vec3(-1.2443235736, 0.0, 0.0),
        Vec3(0.0, -0.8697381999, 0.0))))

  // Broucke A 9 in http://suki.ipb.ac.rs/3body/bsol.php?id=8
  def brouckeA9Config = NBodyConfig(
    "Broucke A 9",
    1975,
    20.897689,
    -0.872459,
    0.925495,
    Array(
      new Body(1.0, Vec3(0.0557080334, 0.0, 0.0),
        Vec3(0.0, 1.0824099428, 0.0)),
      new Body(1.0, Vec3(1.3308335036, 0.0, 0.0),
        Vec3(0.0, -0.2339059386, 0.0)),
      new Body(1.0, Vec3(-1.3865415370, 0.0, 0.0),
        Vec3(0.0, -0.8485040042, 0.0))))

  // Broucke A 10 in http://suki.ipb.ac.rs/3body/bsol.php?id=9
  def brouckeA10Config = NBodyConfig(
    "Broucke A 10",
    1975,
    32.610953,
    -0.518368,
    1.024216,
    Array(
      new Body(1.0, Vec3(-0.5426216182, 0.0, 0.0),
        Vec3(0.0, 0.8750200467, 0.0)),
      new Body(1.0, Vec3(2.5274928067, 0.0, 0.0),
        Vec3(0.0, -0.0526955841, 0.0)),
      new Body(1.0, Vec3(-1.9848711885, 0.0, 0.0),
        Vec3(0.0, -0.8223244626, 0.0))))

  // Broucke A 11 in http://suki.ipb.ac.rs/3body/bsol.php?id=10
  def brouckeA11Config = NBodyConfig(
    "Broucke A 11",
    1975,
    32.584945,
    -0.824047,
    0.922542,
    Array(
      new Body(1.0, Vec3(0.0132604844, 0.0, 0.0),
        Vec3(0.0, 1.0541519210, 0.0)),
      new Body(1.0, Vec3(1.4157286016, 0.0, 0.0),
        Vec3(0.0, -0.2101466639, 0.0)),
      new Body(1.0, Vec3(-1.4289890859, 0.0, 0.0),
        Vec3(0.0, -0.8440052572, 0.0))))

  // Broucke A 12 in http://suki.ipb.ac.rs/3body/bsol.php?id=11
  def brouckeA12Config = NBodyConfig(
    "Broucke A 12",
    1975,
    42.823219,
    -0.592101,
    0.963712,
    Array(
      new Body(1.0, Vec3(-0.3370767020, 0.0, 0.0),
        Vec3(0.0, 0.9174260238, 0.0)),
      new Body(1.0, Vec3(2.1164029743, 0.0, 0.0),
        Vec3(0.0, -0.0922665014, 0.0)),
      new Body(1.0, Vec3(-1.7793262723, 0.0, 0.0),
        Vec3(0.0, -0.8251595224, 0.0))))

  // Broucke A 13 in http://suki.ipb.ac.rs/3body/bsol.php?id=12
  def brouckeA13Config = NBodyConfig(
    "Broucke A 13",
    1975,
    59.716075,
    -0.432937,
    1.163503,
    Array(
      new Body(1.0, Vec3(-0.8965015243, 0.0, 0.0),
        Vec3(0.0, 0.8285556923, 0.0)),
      new Body(1.0, Vec3(3.2352526189, 0.0, 0.0),
        Vec3(0.0, -0.0056478094, 0.0)),
      new Body(1.0, Vec3(-2.3387510946, 0.0, 0.0),
        Vec3(0.0, -0.8229078829, 0.0))))

  // Broucke A 14 in http://suki.ipb.ac.rs/3body/bsol.php?id=13
  def brouckeA14Config = NBodyConfig(
    "Broucke A 14",
    1975,
    54.230811,
    -0.625794,
    0.947463,
    Array(
      new Body(1.0, Vec3(-0.2637815221, 0.0, 0.0),
        Vec3(0.0, 0.9371630895, 0.0)),
      new Body(1.0, Vec3(1.9698126146, 0.0, 0.0),
        Vec3(0.0, -0.1099503287, 0.0)),
      new Body(1.0, Vec3(-1.7060310924, 0.0, 0.0),
        Vec3(0.0, -0.8272127608, 0.0))))

  // Broucke A 15 in http://suki.ipb.ac.rs/3body/bsol.php?id=14
  def brouckeA15Config = NBodyConfig(
    "Broucke A 15",
    1975,
    92.056119,
    -0.383678,
    1.297157,
    Array(
      new Body(1.0, Vec3(-1.1889693067, 0.0, 0.0),
        Vec3(0.0, 0.8042120498, 0.0)),
      new Body(1.0, Vec3(3.8201881837, 0.0, 0.0),
        Vec3(0.0, 0.0212794833, 0.0)),
      new Body(1.0, Vec3(-2.6312188770, 0.0, 0.0),
        Vec3(0.0, -0.8254915331, 0.0))))

  // Broucke A 16 in http://suki.ipb.ac.rs/3body/bsol.php?id=15
  def brouckeA16Config = NBodyConfig(
    "Broucke A 16",
    1975,
    90.871196,
    -0.468864 ,
    1.093094,
    Array(
      new Body(1.0, Vec3(-0.7283341038, 0.0, 0.0),
        Vec3(0.0, 0.8475982451, 0.0)),
      new Body(1.0, Vec3(2.8989177778, 0.0, 0.0),
        Vec3(0.0, -0.0255162097, 0.0)),
      new Body(1.0, Vec3(-2.1705836741, 0.0, 0.0),
        Vec3(0.0, -0.8220820354, 0.0))))

  // Broucke R1 in http://suki.ipb.ac.rs/3body/bsol.php?id=16
  def brouckeR1Config = NBodyConfig(
    "Broucke R 1",
    1975,
    5.226525,
    -1.464959,
    1.606147,
    Array(
      new Body(1.0, Vec3(0.8083106230, 0.0, 0.0),
        Vec3(0.0, 0.9901979166, 0.0)),
      new Body(1.0, Vec3(-0.4954148566, 0.0, 0.0),
        Vec3(0.0, -2.7171431768, 0.0)),
      new Body(1.0, Vec3(-0.3128957664, 0.0, 0.0),
        Vec3(0.0, 1.7269452602, 0.0))))

  // Broucke R2 in http://suki.ipb.ac.rs/3body/bsol.php?id=17
  def brouckeR2Config = NBodyConfig(
    "Broucke R 2",
    1975,
    5.704198,
    -1.621650,
    1.854913,
    Array(
      new Body(1.0, Vec3(0.9060893715, 0.0, 0.0),
        Vec3(0.0, 0.9658548899, 0.0)),
      new Body(1.0, Vec3(-0.6909723536, 0.0, 0.0),
        Vec3(0.0, -1.6223214842, 0.0)),
      new Body(1.0, Vec3(-0.2151170179, 0.0, 0.0),
        Vec3(0.0, 0.6564665942, 0.0))))

  // Figure 8 in http://suki.ipb.ac.rs/3body/sol.php?id=1
  def figure8Config = {
    val p1 = 0.347111
    val p2 = 0.532728
    NBodyConfig(
      "Figure 8",
      1993,
      6.324449,
      -1.287146,
      0.0,
      Array(
        new Body(1.0, Vec3(-1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(0.0, 0.0, 0.0),
          Vec3(-2.0 * p1, -2.0 * p2, 0.0))))
  }

  // Butterfly I in http://suki.ipb.ac.rs/3body/sol.php?id=2
  def butterflyIConfig = {
    val p1 = 0.306893
    val p2 = 0.125507
    NBodyConfig(
      "Butterfly I",
      2012,
      6.235641,
      -2.170195,
      0.0,
      Array(
        new Body(1.0, Vec3(-1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(0.0, 0.0, 0.0),
          Vec3(-2.0 * p1, -2.0 * p2, 0.0))))
  }

  // Ying-yang 2a in http://suki.ipb.ac.rs/3body/sol.php?id=15
  def yingYang2aConfig = {
    val p1 = 0.416822
    val p2 = 0.330333
    NBodyConfig(
      "Ying-yang 2a",
      2012,
      55.789829,
      -1.651418,
      0.0,
      Array(
        new Body(1.0, Vec3(-1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(0.0, 0.0, 0.0),
          Vec3(-2.0 * p1, -2.0 * p2, 0.0))))
  }

  // Ying-yang 2b in http://suki.ipb.ac.rs/3body/sol.php?id=16
  def yingYang2bConfig = {
    val p1 = 0.417343
    val p2 = 0.313100
    NBodyConfig(
      "Ying-yang 2b",
      2012,
      54.207599,
      -1.683380,
      0.0,
      Array(
        new Body(1.0, Vec3(-1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(1.0, 0.0, 0.0),
          Vec3(p1, p2, 0.0)),
        new Body(1.0, Vec3(0.0, 0.0, 0.0),
          Vec3(-2.0 * p1, -2.0 * p2, 0.0))))
  }

  // 3-chain and 4-chain solutions from http://www.maia.ub.es/dsg/2000/0002simo.ps.gz
  // Also see the movie in http://www.maia.ub.es/dsg/nbody

  // 3-chain
  def threeChainConfig = {
    NBodyConfig(
      "3-chain",
      2000, // Not sure
      2.0 * math.Pi,
      -2.5735495, // This is actually calculated.
      1.02969158, // This is actually calculated. Why not 0?
      Array(
        new Body(1.0, Vec3(1.382857, 0.0, 0.0),
          Vec3(0.0, 0.584873, 0.0)),
        new Body(1.0, Vec3(0.0, 0.157030, 0.0),
          Vec3(1.871935, 0.0, 0.0)),
        new Body(1.0, Vec3(-1.382857, 0.0, 0.0),
          Vec3(0.0, -0.584873, 0.0)),
        new Body(1.0, Vec3(0.0, -0.157030, 0.0),
          Vec3(-1.871935, 0.0, 0.0))))
  }

  // 4-chain
  def fourChainConfig = {
    NBodyConfig(
      "4-chain",
      2000, // Not sure
      2.0 * math.Pi,
      -4.2635737, // This is actually calculated.
      1.17137800E-6, // This is actually calculated. Why not 0?
      Array(
        // 1.657666 and -0.593786 are calculated from center-of-mass condition
        // and zero total momentum, respectively. Not sure whether it is really correct.
        new Body(1.0, Vec3(1.657666, 0.0, 0.0),  
          Vec3(0.0, -0.593786, 0.0)),
        new Body(1.0, Vec3(0.439775, -0.169717, 0.0),
          Vec3(1.822785, 0.128248, 0.0)),
        new Body(1.0, Vec3(-1.268608, -0.267651, 0.0),
          Vec3(1.271564, 0.168645, 0.0)),
        new Body(1.0, Vec3(-1.268608, 0.267651, 0.0),
          Vec3(-1.271564, 0.168645, 0.0)),
        new Body(1.0, Vec3(0.439775, 0.169717, 0.0),
          Vec3(-1.822785, 0.128248, 0.0))))
  }

  // http://www.ams.org/notices/200105/fea-montgomery.pdf

  def threeBodyConfigs = Array(
    brouckeA1Config, brouckeA2Config, brouckeA3Config, brouckeA4Config, 
    brouckeA5Config, brouckeA6Config, brouckeA7Config, brouckeA8Config, 
    brouckeA9Config, brouckeA10Config, brouckeA11Config, brouckeA12Config,
    brouckeA13Config, brouckeA14Config, brouckeA15Config, brouckeA16Config,
    brouckeR1Config, brouckeR2Config,
    figure8Config, 
    butterflyIConfig,
    yingYang2aConfig, yingYang2bConfig,
    threeChainConfig, fourChainConfig
  )
}
