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
package thredds.catalog2.xml.parser.jdom;

import thredds.catalog2.xml.util.PropertyElementUtils;
import thredds.catalog2.Property;

import java.net.URI;
import java.io.*;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * _more_
 *
 * @author edavis
 * @since 4.0
 */
public class PropertyParser
{
  private Logger log = LoggerFactory.getLogger( getClass() );

  private SAXBuilder saxBuilder;

  public PropertyParser()
  {
    this.saxBuilder = new SAXBuilder( false );
  }

  public Property readXML( String xmlAsString, URI docBaseUri )
          throws JDOMException
  {
    try
    {
      this.saxBuilder.build( new StringReader( xmlAsString), docBaseUri.toString());
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    return null;
  }

  public Property readXML( URI uri )
          throws IOException, JDOMException
  {
    Document doc = this.saxBuilder.build( uri.toURL() );
    Element propElem = doc.getRootElement();
    if ( propElem.getName() != PropertyElementUtils.ELEMENT_NAME )
    {

    }
    return null;
  }

  public Property readXML( File file, URI docBaseUri )
  {
    return null;
  }

  public Property readXML( Reader reader, URI docBaseUri )
  {
    
    //this.saxBuilder.build( reader, docBaseUri.toString() );
    return null;
  }

  public Property readXML( InputStream is, URI docBaseUri )
  {
    return null;
  }
}
