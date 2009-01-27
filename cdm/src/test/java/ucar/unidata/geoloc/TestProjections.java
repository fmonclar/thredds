/*
 * Copyright 1998-2009 University Corporation for Atmospheric Research/Unidata
 *
 * Portions of this software were developed by the Unidata Program at the
 * University Corporation for Atmospheric Research.
 *
 * Access and use of this software shall impose the following obligations
 * and understandings on the user. The user is granted the right, without
 * any fee or cost, to use, copy, modify, alter, enhance and distribute
 * this software, and any derivative works thereof, and its supporting
 * documentation for any purpose whatsoever, provided that this entire
 * notice appears in all copies of the software, derivative works and
 * supporting documentation.  Further, UCAR requests that the user credit
 * UCAR/Unidata in any publications that result from the use of this
 * software or in any product that includes this software. The names UCAR
 * and/or Unidata, however, may not be used in any advertising or publicity
 * to endorse or promote any products or commercial entity unless specific
 * written permission is obtained from UCAR/Unidata. The user also
 * understands that UCAR/Unidata is not obligated to provide the user with
 * any support, consulting, training or assistance of any kind with regard
 * to the use, operation and performance of this software nor to provide
 * the user with any updates, revisions, new versions or "bug fixes."
 *
 * THIS SOFTWARE IS PROVIDED BY UCAR/UNIDATA "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL UCAR/UNIDATA BE LIABLE FOR ANY SPECIAL,
 * INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING
 * FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION
 * WITH THE ACCESS, USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package ucar.unidata.geoloc;

import junit.framework.*;

import ucar.unidata.geoloc.projection.*;
import ucar.nc2.TestAll;

/**
 * test methods projections have in common
 *
 * @author John Caron
 */

public class TestProjections extends TestCase {
  boolean show = false;
  int NTRIALS = 10000;
  double TOLERENCE = 1.0e-6;
  int count = 10;

  public TestProjections(String name) {
    super(name);
  }

  private void doOne(ProjectionImpl proj, double lat, double lon) {
    LatLonPointImpl startL = new LatLonPointImpl(lat, lon);
    ProjectionPoint p = proj.latLonToProj(startL);
    LatLonPoint endL = proj.projToLatLon(p);

    System.out.println("start  = " + startL);
    System.out.println("end  = " + endL);

  }

  private void testProjection(ProjectionImpl proj) {
    java.util.Random r = new java.util.Random((long) this.hashCode());
    LatLonPointImpl startL = new LatLonPointImpl();

    for (int i = 0; i < NTRIALS; i++) {
      startL.setLatitude(180.0 * (r.nextDouble() - .5)); // random latlon point
      startL.setLongitude(360.0 * (r.nextDouble() - .5));

      ProjectionPoint p = proj.latLonToProj(startL);
      LatLonPoint endL = proj.projToLatLon(p);

      assert (TestAll.closeEnough(startL.getLatitude(), endL.getLatitude(), 1.0e-4)) : proj.getClass().getName() + " failed start= " + startL + " end = " + endL;
      assert (TestAll.closeEnough(startL.getLongitude(), endL.getLongitude(), 1.0e-4)) : proj.getClass().getName() + " failed start= " + startL + " end = " + endL;
    }

    ProjectionPointImpl startP = new ProjectionPointImpl();
    for (int i = 0; i < NTRIALS; i++) {
      startP.setLocation(10000.0 * (r.nextDouble() - .5),  // random proj point
              10000.0 * (r.nextDouble() - .5));

      LatLonPoint ll = proj.projToLatLon(startP);
      ProjectionPoint endP = proj.latLonToProj(ll);

      assert (TestAll.closeEnough(startP.getX(), endP.getX()));
      assert (TestAll.closeEnough(startP.getY(), endP.getY()));
    }

    System.out.println("Tested " + NTRIALS + " pts for projection " + proj.getClassName());
  }

  // must have lon within +/- lonMax
  public void testProjectionLonMax(ProjectionImpl proj, double lonMax, double latMax) {
    java.util.Random r = new java.util.Random((long) this.hashCode());
    LatLonPointImpl startL = new LatLonPointImpl();

    for (int i = 0; i < NTRIALS; i++) {
      startL.setLatitude(latMax * (2*r.nextDouble() - 1)); // random latlon point
      startL.setLongitude(lonMax * (2*r.nextDouble() - 1));

      ProjectionPoint p = proj.latLonToProj(startL);
      LatLonPoint endL = proj.projToLatLon(p);

      if (show) {
        System.out.println("start  = " + startL);
        System.out.println("inter  = " + p);
        System.out.println("end  = " + endL);
      }

      double tolerence = 5.0e-4;
      assert (TestAll.closeEnough(startL.getLatitude(), endL.getLatitude(), tolerence)) : proj.getClass().getName() + " failed start= " + startL + " end = " + endL;
      assert (TestAll.closeEnough(startL.getLongitude(), endL.getLongitude(), tolerence)) : proj.getClass().getName() + " failed start= " + startL + " end = " + endL;
    }

    startL.setLatitude(latMax / 2);
    startL.setLongitude(lonMax / 2);
    ProjectionPointImpl base = new ProjectionPointImpl();
    proj.latLonToProj(startL, base);
    ProjectionPointImpl startP = new ProjectionPointImpl();
    for (int i = 0; i < NTRIALS; i++) {
      startP.setLocation(base.getX() * (2*r.nextDouble() - 1),  // random proj point
              base.getY() * (2*r.nextDouble() - 1));

      LatLonPoint ll = proj.projToLatLon(startP);
      ProjectionPoint endP = proj.latLonToProj(ll);

       if (false) {
        System.out.println("start  = " + startP);
        System.out.println("end  = " + endP);
      }

      assert TestAll.closeEnough(startP.getX(), endP.getX(), 5.0e-4) : " failed start= " + startP.getX() + " end = " + endP.getX();
      assert TestAll.closeEnough(startP.getY(), endP.getY(), 5.0e-4) : " failed start= " + startP.getY() + " end = " + endP.getY();
    }

    System.out.println("Tested " + NTRIALS + " pts for projection " + proj.getClassName());
  }

  public void testLC() {
    testProjection(new LambertConformal());

    LambertConformal lc = new LambertConformal();
    LambertConformal lc2 = (LambertConformal) lc.constructCopy();
    assert lc.equals(lc2);

  }

  public void testTM() {
    testProjection(new TransverseMercator());

    TransverseMercator p = new TransverseMercator();
    TransverseMercator p2 = (TransverseMercator) p.constructCopy();
    assert p.equals(p2);
  }

  public void testStereo() {
    testProjection(new Stereographic());
    Stereographic p = new Stereographic();
    Stereographic p2 = (Stereographic) p.constructCopy();
    assert p.equals(p2);
  }

  public void testLA() {
    testProjection(new LambertAzimuthalEqualArea());
    LambertAzimuthalEqualArea p = new LambertAzimuthalEqualArea();
    LambertAzimuthalEqualArea p2 = (LambertAzimuthalEqualArea) p.constructCopy();
    assert p.equals(p2);
  }

  public void utestOrtho() {
    testProjectionLonMax(new Orthographic(), 180, 80);
    Orthographic p = new Orthographic();
    Orthographic p2 = (Orthographic) p.constructCopy();
    assert p.equals(p2);
  }

  public void testAEA() {
    testProjection(new AlbersEqualArea());
    AlbersEqualArea p = new AlbersEqualArea();
    AlbersEqualArea p2 = (AlbersEqualArea) p.constructCopy();
    assert p.equals(p2);
  }

  public void testFlatEarth() {
    testProjection(new FlatEarth());
    FlatEarth p = new FlatEarth();
    FlatEarth p2 = (FlatEarth) p.constructCopy();
    assert p.equals(p2);
  }

  public void testMercator() {
    testProjection(new Mercator());
    Mercator p = new Mercator();
    Mercator p2 = (Mercator) p.constructCopy();
    assert p.equals(p2);
  }

  public void testRotatedPole() {
    testProjectionLonMax(new RotatedPole(37, 177), 360, 88);
    RotatedPole p = new RotatedPole();
    RotatedPole p2 = (RotatedPole) p.constructCopy();
    assert p.equals(p2);
  }

  // UTM failing - no not use
  public void utestUTM() {
    // 33.75N 15.25E end = 90.0N 143.4W
    //doOne(new UtmProjection(), 33.75, 15.25);

    testProjectionLonMax(new UtmProjection(), 150, 90);
    UtmProjection p = new UtmProjection();
    UtmProjection p2 = (UtmProjection) p.constructCopy();
    assert p.equals(p2);  // */
  }

  public void testVerticalPerspectiveView() {
    testProjectionLonMax(new VerticalPerspectiveView(), 66, 60);
    VerticalPerspectiveView p = new VerticalPerspectiveView();
    VerticalPerspectiveView p2 = (VerticalPerspectiveView) p.constructCopy();
    assert p.equals(p2);
  }


}