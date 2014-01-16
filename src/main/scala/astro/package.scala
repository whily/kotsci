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

/** Providing constants, functions, classes related to astonomy. */
package object astro {
  // Astronomical constants from http://asa.usno.navy.mil/static/files/2014/Astronomical_Constants_2014.pdf

  /** Astronomical unit, in m */
  val AU = 149597870700.0

  // Following values are from http://nssdc.gsfc.nasa.gov/planetary/factsheet/sunfact.html
  // Retrieved on 16 Jan 2014.

  /** Volumetric mean radius of the Sun, in m */
  val Rsun = 6.96e8

  /** Luminosity, J / s */
  val Lsun = 3.846e26

  /** Effective temperature, K */
  val Tsun = 5778.0

  /** Volumetric mean radius of the earth, in m */
  val Rearth = 6.371e6
}
