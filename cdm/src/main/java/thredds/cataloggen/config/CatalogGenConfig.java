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
// $Id: CatalogGenConfig.java 63 2006-07-12 21:50:51Z edavis $

package thredds.cataloggen.config;

import thredds.catalog.InvDataset;

import java.util.ArrayList;


/**
 * <p>Title: Catalog Generator</p>
 * <p>Description: Tool for generating THREDDS catalogs.</p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: UCAR/Unidata</p>
 * @author Ethan Davis
 * @version 1.0
 */

public class CatalogGenConfig
{
//  private static Log log = LogFactory.getLog( CatalogGenConfig.class );
  static private org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CatalogGenConfig.class);

  // parent dataset for this CatalogGenConfig
  private InvDataset parentDataset = null;
  // type of catalogGenConfig
  private CatalogGenConfig.Type type = null;

  // list of DatasetSources
  private DatasetSource datasetSource = null;

  // validation flag and log
  private boolean isValid = true;
  private StringBuffer msgLog = new StringBuffer();

  public static final String CATALOG_GEN_CONFIG_NAMESPACE_URI_0_5 =
          "http://www.unidata.ucar.edu/namespaces/thredds/CatalogGenConfig/v0.5";

  /** Constructor */
  public CatalogGenConfig( InvDataset parentDataset, String typeName)
  {
    this( parentDataset, CatalogGenConfig.Type.getType( typeName));
  }

  /** Constructor */
  public CatalogGenConfig( InvDataset parentDataset,
                           CatalogGenConfig.Type type)
  {
    log.debug( "CatalogGenConfig(): type " + type.toString() + ".");

    this.parentDataset = parentDataset;
    this.type = type;
  }

  /** Return the parent dataset of this CatalogGenConfig */
  public InvDataset getParentDataset()
  { return( this.parentDataset); }
  /** Set the type of this CatalogGenConfig */
  public void setParentDataset( InvDataset parentDataset)
  { this.parentDataset = parentDataset; }

  /** Return the type of this CatalogGenConfig */
  public CatalogGenConfig.Type getType()
  { return( this.type); }
  /** Set the type of this CatalogGenConfig */
  public void setType( CatalogGenConfig.Type type)
  { this.type = type; }

  /** Return the DatasetSource for this CatalogGenConfig */
  public DatasetSource getDatasetSource()
  { return( this.datasetSource); }
  /** Set the DatasetSource for this CatalogGenConfig */
  public void setDatasetSource( DatasetSource dsSource)
  { this.datasetSource = dsSource; }

  public boolean validate( StringBuilder out)
  {
    log.debug( "validate(): checking if valid");
    this.isValid = true;

    // If log from construction has content, append to validation output msg.
    if (this.msgLog.length() > 0)
    {
      out.append( this.msgLog);
    }

    // Check that type is not null.
    if ( this.getType() == null)
    {
      isValid = false;
      out.append( " ** CatalogGenConfig (3): null value for type is not valid (set with bad string?).");
    }

    // Validate DatasetSource child element.
    this.isValid &= this.getDatasetSource().validate( out);

    log.debug( "validate(): isValid=" + this.isValid + " message is\n" +
            out.toString());

    return( this.isValid);
  }

  public String toString()
  {
    StringBuffer tmp = new StringBuffer();
    tmp.append( "CatalogGenConfig[type:<")
            .append( this.getType() ).append( "> child ")
            .append( this.getDatasetSource().toString() + ")]");

    return( tmp.toString());
  }

  // @todo Convert this main stuff into a test.
//  public static void main(String[] args)
//  {
//    InvDatasetImpl ds = new InvDatasetImpl(
//      null, "my name", "Unknown", null, "myid", null,
//      "http://motherlode.ucar.edu/dods/");
//
//    CatalogGenConfig cgc = new CatalogGenConfig( ds, "Catalog");
//    DatasetNamer dsNamer = new DatasetNamer(
//      ds, "this dsNamer", "false", "RegExp",
//      "match pattern", "sub pattern", null, null);
//
//    cgc.addDatasetNamer( dsNamer);
//
//    System.out.println( "CatalogGenConfig 1:");
//    System.out.println( "  " + cgc.toString());
//
//    StringBuffer myOut = new StringBuffer();
//    if ( cgc.validate( myOut))
//    {
//      System.out.println( "  Valid :" + myOut + ":");
//    } else
//    {
//      System.out.println( "  Invalid :" + myOut + ":");
//    }
//
//    dsNamer.setAttribName( "junk");
//
//    System.out.println( "CatalogGenConfig 1 (modified):");
//    System.out.println( "  " + cgc.toString());
//
//    myOut = new StringBuffer();
//    if ( cgc.validate( myOut))
//    {
//      System.out.println( "  Valid :" + myOut + ":");
//    } else
//    {
//      System.out.println( "  Invalid :" + myOut + ":");
//    }
//
//    dsNamer.setAttribName( null);
//    cgc.setType( "junk");
//    System.out.println( "CatalogGenConfig 1 (modified2):");
//    System.out.println( "  " + cgc.toString());
//    myOut = new StringBuffer();
//    if ( cgc.validate( myOut))
//    {
//      System.out.println( "  Valid :" + myOut + ":");
//    } else
//    {
//      System.out.println( "  Invalid :" + myOut + ":");
//    }
//  }

  /**
   * Type-safe enumeration of the types of CatalogGenConfig.
   *
   * @author Ethan Davis (from John Caron's thredds.catalog.ServiceType)
   */
  public static class Type
  {
    private static java.util.HashMap hash = new java.util.HashMap(20);

    public final static Type CATALOG = new Type( "Catalog");
    public final static Type AGGREGATION = new Type( "Aggregation");

    private String type;
    private Type( String name)
    {
      this.type = name;
      hash.put(name, this);
    }

    /**
     * Find the Type that matches this name.
     * @param name
     * @return Type or null if no match.
     */
    public static Type getType( String name)
    {
      if ( name == null) return null;
      return (Type) hash.get( name);
    }

    /**
     * Return the string name.
     */
    public String toString()
    {
      return type;
    }
  }
}
