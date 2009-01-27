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

import thredds.catalog2.builder.*;
import thredds.catalog2.*;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

/**
 * _more_
 *
 * @author edavis
 * @since 4.0
 */
public class CatalogRefImpl
        extends DatasetNodeImpl
        implements CatalogRef, CatalogRefBuilder
{
  private URI reference;
  private boolean isBuilt = false;

  protected CatalogRefImpl( String name, URI reference, CatalogImpl parentCatalog, DatasetNodeImpl parent )
  {
    super( name, parentCatalog, parent);
    if ( reference == null ) throw new IllegalArgumentException( "CatalogRef reference URI must not be null." );
    this.reference = reference;
  }

  public void setReference( URI reference )
  {
    if ( this.isBuilt ) throw new IllegalStateException( "This CatalogRefBuilder has been built.");
    if ( reference == null ) throw new IllegalArgumentException( "CatalogRef reference URI must not be null." );
    this.reference = reference;
  }

  public URI getReference()
  {
    return this.reference;
  }

  public boolean isBuilt()
  {
    return this.isBuilt;
  }

  @Override
  public boolean isBuildable( List<BuilderIssue> issues )
  {
    if ( this.isBuilt )
      return true;

    List<BuilderIssue> localIssues = new ArrayList<BuilderIssue>();
    super.isBuildable( issues );

    // ToDo Check any invariants.

    if ( localIssues.isEmpty() )
      return true;

    issues.addAll( localIssues );
    return false;
  }

  public CatalogRef build() throws BuilderException
  {
    if ( this.isBuilt )
      return this;

    List<BuilderIssue> issues = new ArrayList<BuilderIssue>();
    if ( !isBuildable( issues ) )
      throw new BuilderException( issues );

    super.build();

    this.isBuilt = true;
    return this;
  }
}
