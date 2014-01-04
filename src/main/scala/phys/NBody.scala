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

import net.whily.scasci.math.linalg.Vec3

/**
 * Entity for N-body simulation.  We use the general term instead of
 * specific names like particles.
 *  
 * position in m
 * velocity in m / s
 * mass     in kg
 */
class Body(var position: Vec3, var velocity: Vec3, val mass: Double) {
  /**
   * Use 2nd approach (i.e. velocity at integer time steps)
   * in http://www.artcompsci.org/vol_1/v1_web/node34.html 
   */
  def leapfrog {
  }
}

/**
 * Δt  time quantum in s
 */
class NBody(val bodies: List[Body], val Δt: Double) {
  /** Update in every time quantum. */
  def update() {
  }
}
