/**
 * Package file.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License: 
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

package net.whily.scasci

/** Providing constants, functions, classes related to physics. */
package object phys {
  // Physical constants from http://physics.nist.gov/cuu/Constants/
  // Retrieved on 3 Jan 2014.

  // Universal
  /** Speed of light in vacuum, m / s */
  val C = 299792458.0   

  /** Newtonian constant of gravitation, m^3 / (kg * s^2) */
  val G = 6.67384e-11 

  /** Planck constant, J s */
  val H = 6.62606957e-34 

  // Atomic and nuclear
  
  /** Electron mass, kg */
  val Me = 9.10938291e-31

  /** Muon mass, kg */
  val Mμ = 1.883531475e-28

  /** Tau mass, kg */
  val Mτ = 3.16747e-27

  /** Proton mass, kg */
  val Mp = 1.672621777e-27

  /** Neutron mass, kg */
  val Mn = 1.674927351e-27

  // Physicochemical

  /** Avogadro constant, 1/mol */
  val NA = 6.02214129e23

  /** Atomic mass constant, kg */
  val Mu = 1.660538921e-27

  /** Molar gas constant, J / (mol * K) */
  val R = 8.3144621

  /** Boltzmann constant, J / K */
  val K = 1.3806488e-23

  /** Stefan-Boltzmann constant, W / (m^2 * K^4 */
  val Σ = 5.670373e-8
}
