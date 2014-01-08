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
    bodies foreach { b => b.vel += b.acc * (0.5 * Δt) }
    bodies foreach { b => b.pos += b.vel * Δt }
    bodies foreach { b => b.vel += b.acc * (0.5 * Δt) }
  }

  def evolve() {
    while (time < tEnd) {
      time += Δt
      leapfrog()
    }
  }
}
