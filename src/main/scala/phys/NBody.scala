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
  var nb: Array[Body] = null

  /** jerk in m / s^3 */
  val jerk = Vec3.zeros

  /** Return the acceleration in m / s^2 */
  def acc = {
    var a = Vec3.zeros
    for (body <- nb) {
      if (!(body eq this)) {
        val r = body.pos - pos
        val r2 = r ⋅ r
        val r3 = r2 * math.sqrt(r2)
        a += r * (body.mass / r3)
      }
    }
    a * G
  }

  /** Returns the kinetic energy of the particle. */
  def kineticEnergy() = {
    0.5 * mass * (vel ⋅ vel)
  }

  /** Returns the potential energy of the particle. */
  def potentialEnergy() = {
    var p = 0.0
    for (body <- nb) {
      if (!(body eq this)) {
        p += body.mass / (body.pos - pos).norm
      }
    }
    -G * mass * p
  }
}

/** Performs N-body simulation. Based on http://www.artcompsci.org/kali/development.html
  * 
  * @param bodies bodies for simulation. Positions and velocities are already initialized.
  * @param Δt time quantum in s
  * @param duration simulation running duration
  */
class NBody(val bodies: Array[Body], val Δt: Double, val duration: Double) {
  // Initialize backward references.
  for (body <- bodies) 
    body.nb = bodies

  private val n = bodies.length
  var time = 0.0
  private val tEnd = duration - 0.5 * Δt
  val initialEnergy = totalEnergy()

  /** Run N-body simulation until current time >= tEnd. */
  def evolve(integrator: String) {
    while (time < tEnd) {
      time += Δt
      integrator match {
        case "leapfrog" => leapfrog()
        case "rk2"      => rk2()
        case "rk4"      => rk4()
      }
    }
  }

  /** Returns the kinetic energy of the system. */
  def kineticEnergy() = (0.0 /: bodies) (_ + _.kineticEnergy())

  /** Returns the potential energy of the system. Note that 0.5 is used
    * since the pairwise potential energy is calcualted twice. */
  def potentialEnergy() = (0.5 * (0.0 /: bodies) (_ + _.potentialEnergy()))

  /** Returns the total energy (kinetic + potential) of the particle. */
  def totalEnergy() = kineticEnergy() + potentialEnergy()

  /** Returns the relative energey error. */
  def relativeEnergyError() = (totalEnergy() - initialEnergy) / initialEnergy

  /** Use 2nd approach (i.e. velocity at integer time steps) in
    * http://www.artcompsci.org/vol_1/v1_web/node34.html
    */
  def leapfrog() {
    for (b <- bodies) b.vel += b.acc * (0.5 * Δt)
    for (b <- bodies) b.pos += b.vel * Δt 
    for (b <- bodies) b.vel += b.acc * (0.5 * Δt) 
  }

  /** Second-order Runge-Kutta integrator. */
  def rk2() {
    val oldPos = bodies map (_.pos.copy())
    val halfVel = bodies map (b => b.vel + b.acc * (0.5 * Δt))
    for (b <- bodies) b.pos += b.vel * (0.5 * Δt)
    for (b <- bodies) b.vel += b.acc * Δt
    for (i <- 0 until n) bodies(i).pos = oldPos(i) + halfVel(i) * Δt
  }

  /** Fourth-order Runge-Kutta integrator. */
  def rk4() {
    val oldPos = bodies map (_.pos.copy())
    val a0 = bodies map (_.acc)
    for (i <- 0 until n) 
      bodies(i).pos = oldPos(i) + bodies(i).vel * (0.5 * Δt) + a0(i) * (0.125 * Δt * Δt)
    val a1 = bodies map (_.acc)
    for (i <- 0 until n)
      bodies(i).pos = oldPos(i) + bodies(i).vel * Δt + a1(i) * (0.5 * Δt * Δt)
    val a2 = bodies map (_.acc)
    for (i <- 0 until n)
      bodies(i).pos = oldPos(i) + bodies(i).vel * Δt + (a0(i) + a1(i) * 2) * (1.0 / 6.0 * Δt * Δt)
    for (i <- 0 until n)
      bodies(i).vel += (a0(i) + a1(i) * 4 + a2(i)) * (1.0 / 6.0 * Δt)
  }
}

/** Provides example configurations for testing. We use def instead of
  * val so that simulations could be run again and again. */
object NBody {
  // Configuration from section 3.1 of http://www.artcompsci.org/kali/vol/n_body_problem/volume4.pdf.
  // The mass is normalized as the example in ACS assumes G=1.
  def twoBodyConfig = Array(
    new Body(0.8 / G, Vec3(0.2, 0.0, 0.0), Vec3(0.0, 0.1, 0.0)),
    new Body(0.2 / G, Vec3(-0.8, 0.0, 0.0), Vec3(0.0, -0.4, 0.0)))
  def twoBodySim = new NBody(twoBodyConfig, 0.0001, 10.0)
}
