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
// $Id: ExampleCatalogWriter.java 51 2006-07-12 17:13:13Z caron $
package thredds.examples;

import thredds.catalog.*;

import java.io.IOException;

/**
 * An example of how to write a simple THREDDS InvCatalog document.
 */
public class ExampleCatalogWriter
{
  public static void main( String[] args )
  {
    InvCatalogFactory catFactory = new InvCatalogFactory( "default", true );

    InvCatalogImpl catalog = new InvCatalogImpl( "My Catalog", "1.0", null);

    InvService myService = new InvService( "myServer", ServiceType.DODS.toString(), "http://my.server/cgi-bin/nph-dods", null, null);
    catalog.addService( myService);

    InvDatasetImpl curDs = null;

    for ( int i = 0; i < 5; i++ )
    {
      curDs = new InvDatasetImpl( null, "dataset " + i, null, "myServer", "data/datafile"+i+".nc");
      catalog.addDataset( curDs);
    }

    InvDatasetImpl sDs = new InvDatasetImpl( null, "My Special Datasets");
    catalog.addDataset( sDs);

    for ( int i = 0; i < 3; i++ )
    {
      curDs = new InvDatasetImpl( null, "Special Dataset " + i, null, "myServer", "data/sdatafile" + i + ".nc" );
      sDs.addDataset( curDs );
    }

    // Tie up any loose ends in catalog with finish().
    ((InvCatalogImpl) catalog).finish();

    // Write the catalog to standard output.
    try
    {
      catFactory.writeXML( (InvCatalogImpl) catalog, System.out );
    }
    catch ( IOException e )
    {
      System.err.println( "IOException while writing catalog: " + e.getMessage() );
    }
  }
}

/*
 * $Log: ExampleCatalogWriter.java,v $
 * Revision 1.1  2004/08/23 16:50:34  edavis
 * Update DqcServlet to work with DQC spec v0.3 and InvCatalog v1.0. Folded DqcServlet into the THREDDS server framework/build/distribution. Updated documentation (DqcServlet and THREDDS server).
 *
 */