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
package ucar.nc2.ft.point;

import ucar.nc2.units.DateRange;
import ucar.nc2.units.DateUnit;
import ucar.nc2.ft.*;
import ucar.ma2.StructureData;
import ucar.ma2.StructureDataIterator;
import ucar.unidata.geoloc.LatLonRect;
import ucar.unidata.geoloc.LatLonPointImpl;

import java.io.IOException;

/**
 * A PointFeatureIterator which uses a StructureDataIterator to iterate over members of a Structure,
 * with optional filtering and calculation of time range and bounding box.
 *
 * Subclass must implement makeFeature() to turn the StructureData into a PointFeature.
 *
 * @author caron
 * @since Feb 29, 2008
 */
public abstract class PointIteratorImpl implements PointFeatureIterator {

  // makeFeature may return null, then skip it and go to next iteration
  protected abstract PointFeature makeFeature(int recnum, StructureData sdata) throws IOException;

  private Filter filter;
  private StructureDataIterator structIter;
  private int count = 0;
  private PointFeature feature = null; // hasNext must cache

  // optionally calculate bounding box, date range
  protected boolean calcBB;
  private LatLonRect bb = null;
  private double minTime = Double.MAX_VALUE;
  private double maxTime = -Double.MAX_VALUE;

  protected PointIteratorImpl(Filter filter, boolean calcBB) throws IOException {
    this.filter = filter;
    this.calcBB = calcBB;
  }

  protected PointIteratorImpl(StructureDataIterator structIter, Filter filter, boolean calcBB) throws IOException {
    this.structIter = structIter;
    this.filter = filter;
    this.calcBB = calcBB;
  }

  public boolean hasNext() throws IOException {
    while (true) {
      StructureData sdata = nextStructureData();
      if (sdata == null) break;
      feature = makeFeature(count, sdata);
      if (feature == null) continue;
      if (feature.getLocation().isMissing()) {
        continue;
      }
      if (filter == null || filter.filter(feature))
        return true;
    }

    // all done
    if (calcBB) finishCalc();
    feature = null;
    return false;
  }

  public PointFeature next() throws IOException {
    if (feature == null) return null;
    if (calcBB) doCalc(feature);
    count++;
    return feature;
  }

  public void setBufferSize(int bytes) {
    structIter.setBufferSize(bytes);
  }

  // so subclasses can override
  protected StructureData nextStructureData() throws IOException {
    return structIter.hasNext() ? structIter.next() : null;
  }

  ////////////////////////////////////////////////////////////////////
  // bb, dateRange calculations
  private void doCalc(PointFeature pf) {
    if (bb == null)
      bb = new LatLonRect(pf.getLocation().getLatLon(), .001, .001);
    else
      bb.extend(pf.getLocation().getLatLon());

    double obsTime = pf.getObservationTime();
    minTime = Math.min(minTime, obsTime);
    maxTime = Math.max(maxTime, obsTime);
  }

  private void finishCalc() {
    if (bb.crossDateline() && bb.getWidth() > 350.0) { // call it global - less confusing
      double lat_min = bb.getLowerLeftPoint().getLatitude();
      double deltaLat = bb.getUpperLeftPoint().getLatitude() - lat_min;
      bb = new LatLonRect(new LatLonPointImpl(lat_min, -180.0), deltaLat, 360.0);
    }
    //calcBB = false;
  }

  /**
   * Get BoundingBox after iteration is finished, if calcBB was set true
   * @return BoundingBox of all returned points
   */
  public LatLonRect getBoundingBox() {
    return bb;
  }

  /**
   * Get DateRange of observation time after iteration is finished, if calcBB was set true
   * @param timeUnit to convert times to dates
   * @return DateRange of all returned points
   */
  public DateRange getDateRange(DateUnit timeUnit) {
    return new DateRange(timeUnit.makeDate(minTime), timeUnit.makeDate(maxTime));
  }

  /**
   * Get number of points returned so far
   * @return number of points returned so far
   */
  public int getCount() {
    return count;
  }

}
