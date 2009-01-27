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
package thredds.catalog2.simpleImpl;

import junit.framework.*;
import thredds.catalog2.builder.CatalogRefBuilder;
import thredds.catalog2.builder.DatasetNodeBuilder;
import thredds.catalog2.builder.BuilderIssue;
import thredds.catalog2.builder.BuilderException;
import thredds.catalog2.CatalogRef;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;

/**
 * _more_
 *
 * @author edavis
 * @since 4.0
 */
public class TestCatalogRefImpl extends TestCase
{
  private CatalogImpl parentCatalog;
  private String parentCatName;
  private URI parentCatDocBaseUri;
  private String parentCatVer;

  private DatasetNodeBuilder parentDataset;
  private String parentDsName;

  private CatalogRefBuilder catRefBldr;
  private CatalogRef catRef;

  private String catRefName;
  private URI catRefUri;
  private URI catRefUri2;

  public TestCatalogRefImpl( String name )
  {
    super( name );
  }

  protected void setUp()
  {
    parentCatName = "parent catalog";
    try
    {
      parentCatDocBaseUri = new URI( "http://server/thredds/aCat.xml");
      catRefUri = new URI( "http://server/thredds/cat2.xml" );
      catRefUri2 = new URI( "http://server/thredds/cat3.xml" );
    }
    catch ( URISyntaxException e )
    {
      fail( "Bad URI syntax: " + e.getMessage());
      return;
    }
    parentCatVer = "version";
    parentCatalog = new CatalogImpl( parentCatName, parentCatDocBaseUri, parentCatVer, null, null);

    parentDsName = "parent dataset";
    parentDataset = parentCatalog.addDataset( parentDsName );

    catRefName = "catRef name";
    catRefBldr = parentDataset.addCatalogRef( catRefName, catRefUri );
  }

  public void testGetSet()
  {
    assertFalse( catRefBldr.isBuilt());

    assertTrue( catRefBldr.getName().equals( catRefName ));
    assertTrue( catRefBldr.getReference().equals( catRefUri ));

    catRefBldr.setReference( catRefUri2 );
    assertTrue( catRefBldr.getReference().equals( catRefUri2 ) );
  }

  public void testBuild()
  {
    // Check if buildable
    List<BuilderIssue> issues = new ArrayList<BuilderIssue>();
    if ( !catRefBldr.isBuildable( issues ) )
    {
      StringBuilder stringBuilder = new StringBuilder( "Not isBuildable(): " );
      for ( BuilderIssue bfi : issues )
        stringBuilder.append( "\n    " ).append( bfi.getMessage() ).append( " [" ).append( bfi.getBuilder().getClass().getName() ).append( "]" );
      fail( stringBuilder.toString() );
    }

    // Build
    try
    { catRef = catRefBldr.build(); }
    catch ( BuilderException e )
    { fail( "Build failed: " + e.getMessage() ); }

    assertTrue( catRefBldr.isBuilt() );

    // Test getters of resulting CatalogRef.
    assertTrue( catRef.getName().equals( catRefName ) );
    assertTrue( catRef.getReference().equals( catRefUri ) );

    try
    { catRefBldr.setReference( catRefUri2 ); }
    catch( IllegalStateException ise )
    { return; }
    catch( Exception e )
    { fail( "Unexpected non-IllegalStateException thrown: " + e.getMessage()); }
    fail( "Did not throw expected IllegalStateException.");  
  }
}
