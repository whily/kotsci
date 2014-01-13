/**
 * N-body simulation.
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
  * instead of specific names like particles.
  *
  * @param mass in kg
  * @param pos position in m
  * @param vel velocity in m / s
  */
class Body(val mass: Double, var pos: Vec3, var vel: Vec3) {
  /** jerk in m / s^3 */
  val jerk = Vec3.zeros

  /** Return the acceleration in m / s^2 */
  def acc(bodies: Array[Body]) = {
    var a = Vec3.zeros
    for (body <- bodies) {
      if (!(body eq this)) {
        val r = body.pos - pos
        val r2 = r ⋅ r
        val r3 = r2 * math.sqrt(r2)
        a += r * (body.mass / r3)
      }
    }
    a
    // We assume G = 1, otherwise, use the following line
    // a * G
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

/** Performs N-body simulation. Based on http://www.artcompsci.org/kali/development.html
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
  * @param Δt time quantum in s
  * @param duration simulation running duration
  */
class NBody(val bodies: Array[Body], val Δt: Double) {
  private val n = bodies.length
  var time = 0.0
  val initialEnergy = totalEnergy()

  def this(config: NBodyConfig, Δt: Double) = this(config.bodies, Δt)

  /** Run N-body simulation until current time >= tEnd. 
    * 
    * @param integrator numerical intergrator
    */
  def evolve(integrator: String, duration: Double) {
    val tEnd = duration - 0.5 * Δt
    while (time < tEnd) {
      step(integrator)
    }
  }

  /** Runs N-body simulation with one step.
    *
    * @param integrator numerical intergrator
    */
  def step(integrator: String) {
    time += Δt
    integrator match {
      case "leapfrog" => leapfrog()
      case "rk2" => rk2()
      case "rk4" => rk4()
    }
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

  /** Leapfrog algorithm, which is 2nd order. Algorithm details in
    * http://www.artcompsci.org/vol_1/v1_web/node34.html
    */
  def leapfrog() {
    for (b <- bodies) b.vel += b.acc(bodies) * (0.5 * Δt)
    for (b <- bodies) b.pos += b.vel * Δt
    for (b <- bodies) b.vel += b.acc(bodies) * (0.5 * Δt)
  }

  /** Second-order Runge-Kutta integrator. */
  def rk2() {
    val oldPos = bodies map (_.pos.copy())
    val halfVel = bodies map (b => b.vel + b.acc(bodies) * (0.5 * Δt))
    for (b <- bodies) b.pos += b.vel * (0.5 * Δt)
    for (b <- bodies) b.vel += b.acc(bodies) * Δt
    for (i <- 0 until n) bodies(i).pos = oldPos(i) + halfVel(i) * Δt
  }

  /** Fourth-order Runge-Kutta integrator. */
  def rk4() {
    val oldPos = bodies map (_.pos.copy())
    val a0 = bodies map (_.acc(bodies))
    for (i <- 0 until n)
      bodies(i).pos = oldPos(i) + bodies(i).vel * (0.5 * Δt) + a0(i) * (0.125 * Δt * Δt)
    val a1 = bodies map (_.acc(bodies))
    for (i <- 0 until n)
      bodies(i).pos = oldPos(i) + bodies(i).vel * Δt + a1(i) * (0.5 * Δt * Δt)
    val a2 = bodies map (_.acc(bodies))
    for (i <- 0 until n)
      bodies(i).pos = oldPos(i) + bodies(i).vel * Δt + (a0(i) + a1(i) * 2) * (1.0 / 6.0 * Δt * Δt)
    for (i <- 0 until n)
      bodies(i).vel += (a0(i) + a1(i) * 4 + a2(i)) * (1.0 / 6.0 * Δt)
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

  def threeBodyConfigs = Array(
    brouckeA1Config, brouckeA2Config, brouckeA3Config, brouckeA4Config, 
    brouckeA5Config, brouckeA6Config, brouckeA7Config, brouckeA8Config, 
    brouckeA9Config, brouckeA10Config,
    figure8Config 
    // TODO: removed for now due to near encouter problem: butterflyIConfig
  )
}
