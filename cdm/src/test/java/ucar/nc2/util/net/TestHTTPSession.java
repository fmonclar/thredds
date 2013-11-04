/*
 * Copyright (c) 1998 - 2012. University Corporation for Atmospheric Research/Unidata
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

package ucar.nc2.util.net;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Test;
import ucar.nc2.util.UnitTestCommon;
import ucar.nc2.util.UnitTestLog;

import static junit.framework.TestCase.*;

public class TestHTTPSession extends UnitTestCommon
{

  //////////////////////////////////////////////////

  // Define the test sets

  int passcount = 0;
  int xfailcount = 0;
  int failcount = 0;
  boolean verbose = true;
  boolean pass = false;

  String datadir = null;
  String threddsroot = null;

  public TestHTTPSession()
  {
    super();
    setTitle("HTTP Session tests");
    HTTPSession.TESTING = true;
  }

  @Test
  public void
  testAgent() throws Exception {
    String globalagent = "TestUserAgent123global";
    String sessionagent = "TestUserAgent123session";
    String url =
            "http://thredds-test.ucar.edu:8081/dts/test.01.dds";

    System.out.println("*** Testing: User Agent");
    System.out.println("*** URL: " + url);
    System.out.println("Test: HTTPSession.setGlobalUserAgent(" + globalagent + ")");
    HTTPSession.setGlobalUserAgent(globalagent);
    HTTPSession session = HTTPFactory.newSession(url);
    HTTPMethod method = HTTPFactory.Get(session);
    method.execute();

    // Use special interface to access the request
    HttpMessage req = method.debugRequest();
    // Look for the user agent header
    Header[] agents = req.getHeaders(HTTPSession.HEADER_USERAGENT);
    assertFalse("User-Agent Header not found",agents.length == 0);
    assertFalse("Multiple User-Agent Headers",agents.length > 1);
    assertTrue(String.format("User-Agent mismatch: expected %s found:%s",
                             globalagent,agents[0].getValue()),
	       globalagent.equals(agents[0].getValue()));

    System.out.println("Test: HTTPSession.setUserAgent(" + sessionagent + ")");
    session.setUserAgent(sessionagent);
    method = HTTPFactory.Get(session);
    method.execute();

    // Use special interface to access the request
    req = method.debugRequest();
    // Look for the user agent header
    agents = req.getHeaders(HTTPSession.HEADER_USERAGENT);
    assertFalse("User-Agent Header not found",agents.length == 0);
    assertFalse("Multiple User-Agent Headers",agents.length > 1);
    assertTrue(String.format("User-Agent mismatch: expected %s found:%s",
                             sessionagent,agents[0].getValue()),
	       sessionagent.equals(agents[0].getValue()));

    // Check other parameters

    System.out.println("Test: HTTPSession: Misc. Parameters");
    session.setSoTimeout(17777);
    session.setConnectionTimeout(37777);
    session.setMaxRedirects(111);
    CredentialsProvider bp = new HTTPBasicProvider("anyuser","password");
    session.setCredentialsProvider(HTTPAuthScheme.BASIC, bp);
    //session.setAuthorizationPreemptive(true); not implemented

    method = HTTPFactory.Get(session);
    method.execute();

    // Use special interface to access the request
    HttpMessage dbgreq = method.debugRequest();
    RequestConfig cfg = ((HttpRequestBase)dbgreq).getConfig();

    System.out.println("Test: Circular Redirects");
    assertTrue("*** Fail: Circular Redirects",
               cfg.isCircularRedirectsAllowed());
    System.out.println("*** Pass: Circular Redirects");

    System.out.println("Test: Max Redirects");
    assertTrue("*** Fail: Max Redirects",
               cfg.getMaxRedirects() == 111);
    System.out.println("*** Pass: Max Redirects");

    System.out.println("Test: SO Timeout");
    assertTrue("*** Fail: SO Timeout",
               cfg.getSocketTimeout() == 17777);
    System.out.println("*** Pass: SO Timeout");

    System.out.println("Test: Connection Timeout");
    assertTrue("*** Fail: Connection Timeout",
               cfg.getConnectTimeout() == 37777);
    System.out.println("*** Pass: SO Timeout");

    System.out.println("Test: Authentication Handled");
    assertTrue("*** Fail: Authentication Handled",
               cfg.isAuthenticationEnabled());
    System.out.println("*** Pass: Authentication Handled");

    System.out.println("Test: Redirects Handled");
    assertTrue("*** Fail: Redirects Handled",
               cfg.isRedirectsEnabled());
    System.out.println("*** Pass: Redirects Handled");

    assertTrue("TestHTTPSession.testAgent", true);
  }
}
