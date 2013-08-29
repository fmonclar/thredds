package thredds.crawlabledataset;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import thredds.catalog.*;
import thredds.crawlabledataset.filter.MultiSelectorFilter;
import thredds.crawlabledataset.filter.WildcardMatchOnNameFilter;
import ucar.unidata.test.util.TestDir;
import ucar.unidata.test.util.TestFileDirUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests to figure out problem described in JIRA issue
 * <a href="https://bugtracking.unidata.ucar.edu/browse/TDS-466">TDS-466</a>.
 * Basically:
 *
 * <p>
 *   As of 2013-08-23, LDM pqact-s for THREDDS place NEXRAD and TDWR level3
 *   products in the same directory structure. The directory structure looks
 *   like "level3/{Product}/{Stn}/{date}/Level3_{Stn}_{Product}_{date}_{time}.nids".
 * </p>
 * <p>
 *   NEXRAD and TDWR stations are distinct. Each has a distinct set of products
 *   as well as an overlapping set of products.
 * </p>
 * <p>
 *   Planning to separate into level3/nexrad and level3/tdwr directories.
 *   However, the datasetScan filter configuration currently in
 *   tds/src/main/webapp/WEB-INF/altContent/idd/thredds/idd/radars.xml
 *   is no longer working to separate the NEXRAD and TDWR data in the resulting
 *   catalogs.
 * </p>
 *
 * @author edavis
 */
public class ComplexFilterProblemTest_Jira_TDS_466 {
  private File testDir;

  private static List<String> dates = new ArrayList<String>();
  private static List<String> times = new ArrayList<String>();
  private static List<String> stnListTdwr = new ArrayList<String>();
  private static List<String> stnListNexrad = new ArrayList<String>();
  private static List<String> stnListBoth = new ArrayList<String>();
  private static List<String> productListTdwrOnly = new ArrayList<String>();
  private static List<String> productListNexradOnly = new ArrayList<String>();
  private static List<String> productListBoth = new ArrayList<String>();
  private static Map<String,List<String>> mapOfProductsStationsDirectoryStructure = new HashMap<String,List<String>>();

  private static List<String> tdwrIncludePatterns = new ArrayList<String>();
  private static List<String> nexradExcludePatterns = new ArrayList<String>();

  @BeforeClass
  public static void buildListsAndMaps() {
    buildDates();
    buildTimes();
    buildStnListTdwr();
    buildStnListNexrad();
    buildStnListBoth();
    buildProductListTdwrOnly();
    buildProductListNexradOnly();
    buildProductListBoth();
    buildMapOfProductsStationsDirectoryStructure();
    buildTdwrIncludePatterns();
    buildNexradExcludePatterns();
  }

  private static void buildDates() {
    dates.add( "20130822" );
    dates.add( "20130823" );
    dates.add( "20130824" );
    dates.add( "20130825" );
  }

  private static void buildTimes() {
    times.add( "0006");
    times.add( "0715");
    times.add( "1455");
    times.add( "2332");
  }

  private static void buildStnListTdwr() {
    stnListTdwr.add( "StnTdwr01");
    stnListTdwr.add( "StnTdwr02");
    stnListTdwr.add( "StnTdwr03");
  }

  private static void buildStnListNexrad() {
    stnListNexrad.add( "StnNexrad01");
    stnListNexrad.add( "StnNexrad02");
    stnListNexrad.add( "StnNexrad03");
    stnListNexrad.add( "StnNexrad04");
    stnListNexrad.add( "StnNexrad05");
    stnListNexrad.add( "StnNexrad06");
    stnListNexrad.add( "StnNexrad07");
    stnListNexrad.add( "StnNexrad08" );
    stnListNexrad.add( "StnNexrad09" );
  }

  private static void buildStnListBoth() {
    stnListBoth.addAll( stnListTdwr );
    stnListBoth.addAll( stnListNexrad );
  }

  private static void buildProductListTdwrOnly() {
    productListTdwrOnly.add( "TR0");
    productListTdwrOnly.add( "TR1");
    productListTdwrOnly.add( "TR2");
    productListTdwrOnly.add( "TV0" );
    productListTdwrOnly.add( "TZL" );
  }

  private static void buildProductListNexradOnly() {
    productListNexradOnly.add( "DAA");
    productListNexradOnly.add( "DOD");
    productListNexradOnly.add( "DO1");
    productListNexradOnly.add( "DO2");
    productListNexradOnly.add( "DO3");
    productListNexradOnly.add( "DO4");
    productListNexradOnly.add( "DO5");
    productListNexradOnly.add( "DO6");
  }

  private static void buildProductListBoth() {
    productListBoth.add( "DHR");
    productListBoth.add( "DPA");
  }

  private static void buildMapOfProductsStationsDirectoryStructure() {
    for ( String product : productListTdwrOnly) {
      mapOfProductsStationsDirectoryStructure.put( product, stnListTdwr );
    }
    for ( String product : productListNexradOnly) {
      mapOfProductsStationsDirectoryStructure.put( product, stnListNexrad );
    }
    for ( String product : productListBoth) {
      mapOfProductsStationsDirectoryStructure.put( product, stnListBoth );
    }
  }

  private static void buildTdwrIncludePatterns() {
    tdwrIncludePatterns.addAll( productListBoth );
    tdwrIncludePatterns.addAll( productListTdwrOnly );

    tdwrIncludePatterns.addAll( stnListTdwr );
  }

  private static void buildNexradExcludePatterns() {
    nexradExcludePatterns.addAll( productListTdwrOnly );

    nexradExcludePatterns.addAll( stnListTdwr );
  }

  @Before
  public void setupTestDirStructure() {
    File tmpDataDir = new File( TestDir.temporaryLocalDataDir);
    this.testDir = TestFileDirUtils.createTempDirectory( "ComplexFilterProblemTest_Jira_TDS_466", tmpDataDir );
    for ( String product : mapOfProductsStationsDirectoryStructure.keySet()) {
      this.genRadarLevel3DirStructureForProduct( this.testDir, product, mapOfProductsStationsDirectoryStructure.get( product ) );
    }
  }

  private void genRadarLevel3DirStructureForProduct( File testDir, String product, List<String> stationNames ) {
    File productDir = TestFileDirUtils.addDirectory( testDir, product );
    for( String stnName: stationNames) {
      this.genRadarLevel3DirStructureForStation( productDir, stnName );
    }
  }

  private void genRadarLevel3DirStructureForStation(  File productDir, String stationName ) {
    File stnDir = TestFileDirUtils.addDirectory( productDir, stationName );
    String productName = productDir.getName();
    for ( String date : dates ) {
      this.genRadarLevel3DirStructureForDate( stnDir, date );
    }
  }

  private void genRadarLevel3DirStructureForDate(  File stnDir, String dateString ) {
    File dateDir = TestFileDirUtils.addDirectory( stnDir, dateString );
    String stnName = stnDir.getName();
    String productName = stnDir.getParentFile().getName();
    for ( String time : times) {
      String fileName = String.format( "Level3_%s_%s_%s_%s.nids", stnName, productName, dateString, time);
      TestFileDirUtils.addFile( dateDir, fileName );
    }
  }

  private CrawlableDatasetFilter buildTdwrFilter() {
    List<MultiSelectorFilter.Selector> selectorList = new ArrayList<MultiSelectorFilter.Selector>();
    selectorList.add( buildFileInclude() );
    for ( String s : this.tdwrIncludePatterns ) {
      selectorList.add( this.buildSelector( s, true, false, true ));
    }
    selectorList.add( buildDotNameFileExclude());

    return new MultiSelectorFilter( selectorList );
  }

  private CrawlableDatasetFilter buildNexradFilter() {
    List<MultiSelectorFilter.Selector> selectorList = new ArrayList<MultiSelectorFilter.Selector>();
    selectorList.add( buildFileInclude() );
    for ( String s : this.nexradExcludePatterns ) {
      selectorList.add( this.buildSelector( s, false, false, true ));
    }
    selectorList.add( buildDotNameFileExclude());

    return new MultiSelectorFilter( selectorList );

  }

  private MultiSelectorFilter.Selector buildFileInclude() {
    return new MultiSelectorFilter.Selector( new WildcardMatchOnNameFilter( "Level3*.nids" ),
                                             true, true, false);
  }

  private MultiSelectorFilter.Selector buildDotNameFileExclude() {
    return new MultiSelectorFilter.Selector( new WildcardMatchOnNameFilter( ".*" ),
                                             false, false, true);
  }

  private MultiSelectorFilter.Selector buildSelector( String wildcardPattern,
                                           boolean include, boolean applyToAtomic, boolean applyToCollection )
  {
    return new MultiSelectorFilter.Selector( new WildcardMatchOnNameFilter( wildcardPattern ),
                                             include, applyToAtomic, applyToCollection);
  }

  @Test
  public void tryCrDsScanWithTdwrFilter() throws IOException
  {
    CrawlableDatasetFilter crDsFilterForTdwr = this.buildTdwrFilter();
    CrawlableDatasetFilter crDsFilterForNexrad = this.buildNexradFilter();
    CrawlableDatasetFile crDsFile = new CrawlableDatasetFile( this.testDir );

    List<CrawlableDataset> tdwrList = crDsFile.listDatasets( crDsFilterForTdwr );
    List<CrawlableDataset> nexradList = crDsFile.listDatasets( crDsFilterForNexrad );

    List<CrawlableDataset> tdwrList2 = new ArrayList<CrawlableDataset>();
    List<CrawlableDataset> nexradList2 = new ArrayList<CrawlableDataset>();
    List<CrawlableDataset> tdwrList3 = new ArrayList<CrawlableDataset>();
    List<CrawlableDataset> nexradList3 = new ArrayList<CrawlableDataset>();

    for ( CrawlableDataset crDs : tdwrList) {
      tdwrList2.addAll( crDs.listDatasets( crDsFilterForTdwr ));
    }
    for ( CrawlableDataset crDs : nexradList) {
      nexradList2.addAll( crDs.listDatasets( crDsFilterForNexrad ) );
    }

    for ( CrawlableDataset crDs : tdwrList2) {
      tdwrList3.addAll( crDs.listDatasets( crDsFilterForTdwr ));
    }
    for ( CrawlableDataset crDs : nexradList2) {
      nexradList3.addAll( crDs.listDatasets( crDsFilterForNexrad ));
    }

    System.out.println( String.format( "Size of TDWR lists:   %d, %d, %d", tdwrList.size(), tdwrList2.size(), tdwrList3.size()));
    System.out.println( String.format( "Size of NEXRAD lists: %d, %d, %d", nexradList.size(), nexradList2.size(), nexradList3.size() ) );
  }

  @Test
  public void assertNexradConfigDsWithComplexFilterActsAsExpected() throws URISyntaxException
  {
    URI catURI = new URI( "http://jira_tds-466.complexDsScanFilterProb.test/nexrad/cat.xml" );
    CrawlableDatasetFilter crDsFilter = this.buildNexradFilter();

    InvDatasetScan dsScan = buildDatasetScanInParentConfigCat( catURI, crDsFilter );

    // Test scan at product directory (top) level.
    String catRequestUrlFormat = String.format( "testPath/%s/catalog.xml", this.testDir.getName() );
    InvCatalogImpl cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    List<InvDataset> datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    InvDataset topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() == productListNexradOnly.size() + productListBoth.size() );

    for ( InvDataset productDs : datasets ) {
      assertTrue( String.format( "Product dataset name [%s] not in productListNexradOnly %s or productListBoth %s.",
                                 productDs.getName(), productListNexradOnly.toString(), productListBoth.toString()),
                  productListNexradOnly.contains( productDs.getName())
                  || productListBoth.contains( productDs.getName()) );
    }

    // Test scan at station directory (second) level.
    catRequestUrlFormat = String.format( "testPath/%s/%s/catalog.xml",
                                         this.testDir.getName(),
                                         productListNexradOnly.get(0) );
    cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() == stnListNexrad.size() );

    for( InvDataset stnDs : datasets ) {
      assertTrue( String.format( "Product dataset name [%s] not in stnListNexrad %s.",
                                 stnDs.getName(), stnListNexrad.toString() ),
                  stnListNexrad.contains( stnDs.getName()));
    }

    // Test scan at data file directory (third) level.
    catRequestUrlFormat = String.format( "testPath/%s/%s/%s/catalog.xml",
                                         this.testDir.getName(),
                                         productListNexradOnly.get(0),
                                         stnListNexrad.get( 5) );
    cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() == dates.size() );
    for ( InvDataset fileDs : datasets)
    {
      String fileNameRegex = "Level3_.*\\.nids";
      assertTrue( String.format( "File dataset name [%s] doesn't match \"%s\" regExp.", fileDs.getName(), fileNameRegex),
                  fileDs.getName().matches( fileNameRegex ));
    }

    // Test scan at data file directory (third) level.
    catRequestUrlFormat = String.format( "testPath/%s/%s/%s/catalog.xml",
                                         this.testDir.getName(),
                                         productListNexradOnly.get(0),
                                         stnListNexrad.get( 5) );
    cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() ==  times.size() );
    for ( InvDataset fileDs : datasets)
    {
      String fileNameRegex = "Level3_.*\\.nids";
      assertTrue( String.format( "File dataset name [%s] doesn't match \"%s\" regExp.", fileDs.getName(), fileNameRegex),
                  fileDs.getName().matches( fileNameRegex ));
    }

    cat.getName();

  }

  @Test
  public void assertTdwrConfigDsWithComplexFilterActsAsExpected() throws URISyntaxException
  {
    URI catURI = new URI( "http://jira_tds-466.complexDsScanFilterProb.test/tdwr/cat.xml" );
    CrawlableDatasetFilter crDsFilter = this.buildTdwrFilter();

    InvDatasetScan dsScan = buildDatasetScanInParentConfigCat( catURI, crDsFilter );

    // Test scan at product directory (top) level.
    String catRequestUrlFormat = String.format( "testPath/%s/catalog.xml", this.testDir.getName() );
    InvCatalogImpl cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    List<InvDataset> datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    InvDataset topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() == productListTdwrOnly.size() + productListBoth.size() );

    for ( InvDataset productDs : datasets ) {
      assertTrue( String.format( "Product dataset name [%s] not in productListTdwrOnly %s or productListBoth %s.",
                                 productDs.getName(), productListTdwrOnly.toString(), productListBoth.toString()),
                  productListTdwrOnly.contains( productDs.getName())
                  || productListBoth.contains( productDs.getName()) );
    }

    // Test scan at station directory (second) level.
    catRequestUrlFormat = String.format( "testPath/%s/%s/catalog.xml",
                                         this.testDir.getName(),
                                         productListTdwrOnly.get(0) );
    cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() == stnListTdwr.size() );

    for( InvDataset stnDs : datasets ) {
      assertTrue( String.format( "Product dataset name [%s] not in stnListTdwr %s.",
                                 stnDs.getName(), stnListTdwr.toString() ),
                  stnListTdwr.contains( stnDs.getName()));
    }

    // Test scan at data file directory (third) level.
    catRequestUrlFormat = String.format( "testPath/%s/%s/%s/catalog.xml",
                                         this.testDir.getName(),
                                         productListTdwrOnly.get(0),
                                         stnListTdwr.get( 2) );
    cat = dsScan.makeCatalogForDirectory( catRequestUrlFormat, catURI );
    datasets = cat.getDatasets();
    assertTrue( datasets.size() == 1);

    topDs = datasets.get( 0);
    datasets = topDs.getDatasets();
    assertTrue( datasets.size() == dates.size() * times.size() );
    for ( InvDataset fileDs : datasets)
    {
      String fileNameRegex = "Level3_.*\\.nids";
      assertTrue( String.format( "File dataset name [%s] doesn't match \"%s\" regExp.", fileDs.getName(), fileNameRegex),
                  fileDs.getName().matches( fileNameRegex ));
    }

    cat.getName();

  }

  private InvDatasetScan buildDatasetScanInParentConfigCat( URI catURI, CrawlableDatasetFilter crDsFilter )
  {
    InvCatalogImpl configCat = new InvCatalogImpl( "testCat", "1.0.2", catURI );
    configCat.addService( new InvService( "odap", "OPENDAP", "/thredds/dodsC/", null, null ) );
    InvDatasetImpl topConfigDs = new InvDatasetImpl(null, "testDs", null, "odap", null );
    configCat.addDataset( topConfigDs);

    InvDatasetScan dsScan = new InvDatasetScan( null, null, "test", "testPath/" + this.testDir.getName(),
                                                this.testDir.getAbsolutePath(),
                                                crDsFilter, true, null, true, null, null, null);
    dsScan.setServiceName( "odap" );
    topConfigDs.addDataset( dsScan );
    configCat.finish();
    return dsScan;
  }

//  static List<String> stnListForProductsWith45Stns = new ArrayList<String>(45);
//  static {
//     stnListForProductsWith45Stns.add( "ADW");
//     stnListForProductsWith45Stns.add( "ATL");
//     stnListForProductsWith45Stns.add( "BNA");
//     stnListForProductsWith45Stns.add( "BOS");
//     stnListForProductsWith45Stns.add( "BWI");
//     stnListForProductsWith45Stns.add( "CLT");
//     stnListForProductsWith45Stns.add( "CMH");
//     stnListForProductsWith45Stns.add( "CVG");
//     stnListForProductsWith45Stns.add( "DAL");
//     stnListForProductsWith45Stns.add( "DAY");
//     stnListForProductsWith45Stns.add( "DCA");
//     stnListForProductsWith45Stns.add( "DEN");
//     stnListForProductsWith45Stns.add( "DFW");
//     stnListForProductsWith45Stns.add( "DTW");
//     stnListForProductsWith45Stns.add( "EWR");
//     stnListForProductsWith45Stns.add( "FLL");
//     stnListForProductsWith45Stns.add( "HOU");
//     stnListForProductsWith45Stns.add( "IAD");
//     stnListForProductsWith45Stns.add( "IAH");
//     stnListForProductsWith45Stns.add( "ICH");
//     stnListForProductsWith45Stns.add( "IDS");
//     stnListForProductsWith45Stns.add( "JFK");
//     stnListForProductsWith45Stns.add( "LAS");
//     stnListForProductsWith45Stns.add( "LVE");
//     stnListForProductsWith45Stns.add( "MCI");
//     stnListForProductsWith45Stns.add( "MCO");
//     stnListForProductsWith45Stns.add( "MDW");
//     stnListForProductsWith45Stns.add( "MEM");
//     stnListForProductsWith45Stns.add( "MIA");
//     stnListForProductsWith45Stns.add( "MKE");
//     stnListForProductsWith45Stns.add( "MSP");
//     stnListForProductsWith45Stns.add( "MSY");
//     stnListForProductsWith45Stns.add( "OKC");
//     stnListForProductsWith45Stns.add( "ORD");
//     stnListForProductsWith45Stns.add( "PBI");
//     stnListForProductsWith45Stns.add( "PHL");
//     stnListForProductsWith45Stns.add( "PHX");
//     stnListForProductsWith45Stns.add( "PIT");
//     stnListForProductsWith45Stns.add( "RDU");
//     stnListForProductsWith45Stns.add( "SDF");
//     stnListForProductsWith45Stns.add( "SJU");
//     stnListForProductsWith45Stns.add( "SLC");
//     stnListForProductsWith45Stns.add( "STL");
//     stnListForProductsWith45Stns.add( "TPA");
//     stnListForProductsWith45Stns.add( "TUL");
//  }
//  static List<String> stnListForProductsWith153Stns = new ArrayList<String>(153);
//  static {
//    stnListForProductsWith153Stns.add( "ABC");
//    stnListForProductsWith153Stns.add( "ABR");
//    stnListForProductsWith153Stns.add( "ABX");
//    stnListForProductsWith153Stns.add( "AHG");
//    stnListForProductsWith153Stns.add( "AIH");
//    stnListForProductsWith153Stns.add( "AKC");
//    stnListForProductsWith153Stns.add( "AKQ");
//    stnListForProductsWith153Stns.add( "AMA");
//    stnListForProductsWith153Stns.add( "AMX");
//    stnListForProductsWith153Stns.add( "APX");
//    stnListForProductsWith153Stns.add( "ARX");
//    stnListForProductsWith153Stns.add( "ATX");
//    stnListForProductsWith153Stns.add( "BBX");
//    stnListForProductsWith153Stns.add( "BGM");
//    stnListForProductsWith153Stns.add( "BHX");
//    stnListForProductsWith153Stns.add( "BIS");
//    stnListForProductsWith153Stns.add( "BLX");
//    stnListForProductsWith153Stns.add( "BMX");
//    stnListForProductsWith153Stns.add( "BOX");
//    stnListForProductsWith153Stns.add( "BRO");
//    stnListForProductsWith153Stns.add( "BUF");
//    stnListForProductsWith153Stns.add( "BYX");
//    stnListForProductsWith153Stns.add( "CAE");
//    stnListForProductsWith153Stns.add( "CBW");
//    stnListForProductsWith153Stns.add( "CBX");
//    stnListForProductsWith153Stns.add( "CCX");
//    stnListForProductsWith153Stns.add( "CLE");
//    stnListForProductsWith153Stns.add( "CLX");
//    stnListForProductsWith153Stns.add( "CRP");
//    stnListForProductsWith153Stns.add( "CXX");
//    stnListForProductsWith153Stns.add( "CYS");
//    stnListForProductsWith153Stns.add( "DAX");
//    stnListForProductsWith153Stns.add( "DDC");
//    stnListForProductsWith153Stns.add( "DFX");
//    stnListForProductsWith153Stns.add( "DGX");
//    stnListForProductsWith153Stns.add( "DIX");
//    stnListForProductsWith153Stns.add( "DLH");
//    stnListForProductsWith153Stns.add( "DMX");
//    stnListForProductsWith153Stns.add( "DOX");
//    stnListForProductsWith153Stns.add( "DTX");
//    stnListForProductsWith153Stns.add( "DVN");
//    stnListForProductsWith153Stns.add( "DYX");
//    stnListForProductsWith153Stns.add( "EAX");
//    stnListForProductsWith153Stns.add( "EMX");
//    stnListForProductsWith153Stns.add( "ENX");
//    stnListForProductsWith153Stns.add( "EOX");
//    stnListForProductsWith153Stns.add( "EPZ");
//    stnListForProductsWith153Stns.add( "ESX");
//    stnListForProductsWith153Stns.add( "EVX");
//    stnListForProductsWith153Stns.add( "EWX");
//    stnListForProductsWith153Stns.add( "EYX");
//    stnListForProductsWith153Stns.add( "FCX");
//    stnListForProductsWith153Stns.add( "FDR");
//    stnListForProductsWith153Stns.add( "FDX");
//    stnListForProductsWith153Stns.add( "FFC");
//    stnListForProductsWith153Stns.add( "FSD");
//    stnListForProductsWith153Stns.add( "FSX");
//    stnListForProductsWith153Stns.add( "FTG");
//    stnListForProductsWith153Stns.add( "FWS");
//    stnListForProductsWith153Stns.add( "GGW");
//    stnListForProductsWith153Stns.add( "GJX");
//    stnListForProductsWith153Stns.add( "GLD");
//    stnListForProductsWith153Stns.add( "GRB");
//    stnListForProductsWith153Stns.add( "GRK");
//    stnListForProductsWith153Stns.add( "GRR");
//    stnListForProductsWith153Stns.add( "GSP");
//    stnListForProductsWith153Stns.add( "GUA");
//    stnListForProductsWith153Stns.add( "GWX");
//    stnListForProductsWith153Stns.add( "GYX");
//    stnListForProductsWith153Stns.add( "HDX");
//    stnListForProductsWith153Stns.add( "HGX");
//    stnListForProductsWith153Stns.add( "HKI");
//    stnListForProductsWith153Stns.add( "HKM");
//    stnListForProductsWith153Stns.add( "HMO");
//    stnListForProductsWith153Stns.add( "HNX");
//    stnListForProductsWith153Stns.add( "HPX");
//    stnListForProductsWith153Stns.add( "HTX");
//    stnListForProductsWith153Stns.add( "HWA");
//    stnListForProductsWith153Stns.add( "ICT");
//    stnListForProductsWith153Stns.add( "ICX");
//    stnListForProductsWith153Stns.add( "ILN");
//    stnListForProductsWith153Stns.add( "ILX");
//    stnListForProductsWith153Stns.add( "IND");
//    stnListForProductsWith153Stns.add( "INX");
//    stnListForProductsWith153Stns.add( "IWA");
//    stnListForProductsWith153Stns.add( "IWX");
//    stnListForProductsWith153Stns.add( "JAX");
//    stnListForProductsWith153Stns.add( "JGX");
//    stnListForProductsWith153Stns.add( "JKL");
//    stnListForProductsWith153Stns.add( "JUA");
//    stnListForProductsWith153Stns.add( "LBB");
//    stnListForProductsWith153Stns.add( "LCH");
//    stnListForProductsWith153Stns.add( "LGX");
//    stnListForProductsWith153Stns.add( "LIX");
//    stnListForProductsWith153Stns.add( "LNX");
//    stnListForProductsWith153Stns.add( "LOT");
//    stnListForProductsWith153Stns.add( "LRX");
//    stnListForProductsWith153Stns.add( "LSX");
//    stnListForProductsWith153Stns.add( "LTX");
//    stnListForProductsWith153Stns.add( "LVX");
//    stnListForProductsWith153Stns.add( "LWX");
//    stnListForProductsWith153Stns.add( "LZK");
//    stnListForProductsWith153Stns.add( "MAF");
//    stnListForProductsWith153Stns.add( "MAX");
//    stnListForProductsWith153Stns.add( "MBX");
//    stnListForProductsWith153Stns.add( "MHX");
//    stnListForProductsWith153Stns.add( "MKX");
//    stnListForProductsWith153Stns.add( "MLB");
//    stnListForProductsWith153Stns.add( "MOB");
//    stnListForProductsWith153Stns.add( "MPX");
//    stnListForProductsWith153Stns.add( "MQT");
//    stnListForProductsWith153Stns.add( "MRX");
//    stnListForProductsWith153Stns.add( "MSX");
//    stnListForProductsWith153Stns.add( "MTX");
//    stnListForProductsWith153Stns.add( "MUX");
//    stnListForProductsWith153Stns.add( "MVX");
//    stnListForProductsWith153Stns.add( "MXX");
//    stnListForProductsWith153Stns.add( "NKX");
//    stnListForProductsWith153Stns.add( "NQA");
//    stnListForProductsWith153Stns.add( "OAX");
//    stnListForProductsWith153Stns.add( "OHX");
//    stnListForProductsWith153Stns.add( "OKX");
//    stnListForProductsWith153Stns.add( "OTX");
//    stnListForProductsWith153Stns.add( "PAH");
//    stnListForProductsWith153Stns.add( "PBZ");
//    stnListForProductsWith153Stns.add( "PDT");
//    stnListForProductsWith153Stns.add( "POE");
//    stnListForProductsWith153Stns.add( "PUX");
//    stnListForProductsWith153Stns.add( "RAX");
//    stnListForProductsWith153Stns.add( "RGX");
//    stnListForProductsWith153Stns.add( "RIW");
//    stnListForProductsWith153Stns.add( "RLX");
//    stnListForProductsWith153Stns.add( "RTX");
//    stnListForProductsWith153Stns.add( "SFX");
//    stnListForProductsWith153Stns.add( "SGF");
//    stnListForProductsWith153Stns.add( "SHV");
//    stnListForProductsWith153Stns.add( "SJT");
//    stnListForProductsWith153Stns.add( "SOX");
//    stnListForProductsWith153Stns.add( "SRX");
//    stnListForProductsWith153Stns.add( "TBW");
//    stnListForProductsWith153Stns.add( "TFX");
//    stnListForProductsWith153Stns.add( "TLH");
//    stnListForProductsWith153Stns.add( "TLX");
//    stnListForProductsWith153Stns.add( "TWX");
//    stnListForProductsWith153Stns.add( "TYX");
//    stnListForProductsWith153Stns.add( "UDX");
//    stnListForProductsWith153Stns.add( "UEX");
//    stnListForProductsWith153Stns.add( "VAX");
//    stnListForProductsWith153Stns.add( "VBX");
//    stnListForProductsWith153Stns.add( "VNX");
//    stnListForProductsWith153Stns.add( "VTX");
//    stnListForProductsWith153Stns.add( "VWX");
//    stnListForProductsWith153Stns.add( "YUX");
//  }
//  static List<String> stnListForProductsWith201Stns = new ArrayList<String>(153);
//  static {
//    stnListForProductsWith201Stns.add( "ABC");
//    stnListForProductsWith201Stns.add( "ABR");
//    stnListForProductsWith201Stns.add( "ABX");
//    stnListForProductsWith201Stns.add( "ACG");
//    stnListForProductsWith201Stns.add( "ADW");
//    stnListForProductsWith201Stns.add( "AEC");
//    stnListForProductsWith201Stns.add( "AHG");
//    stnListForProductsWith201Stns.add( "AIH");
//    // and on, and on, ...
//  }
//  static Map<String,List<String>> mapProductsToListOfStationsFull = new HashMap<String,List<String>>();
//  static {
//    mapProductsToListOfStationsFull.put( "DAA", stnListForProductsWith153Stns );
//    mapProductsToListOfStationsFull.put( "DHR", stnListForProductsWith201Stns );
//    mapProductsToListOfStationsFull.put( "DOD", stnListForProductsWith153Stns );
//    mapProductsToListOfStationsFull.put( "DPA", stnListForProductsWith201Stns );
//
//    mapProductsToListOfStationsFull.put( "N2C", stnListForProductsWith153Stns );
//    mapProductsToListOfStationsFull.put( "N3C", stnListForProductsWith153Stns );
//    mapProductsToListOfStationsFull.put( "OHA", stnListForProductsWith153Stns );
//    mapProductsToListOfStationsFull.put( "PTA", stnListForProductsWith153Stns );
//    mapProductsToListOfStationsFull.put( "TR0", stnListForProductsWith45Stns );
//    mapProductsToListOfStationsFull.put( "TR1", stnListForProductsWith45Stns );
//    mapProductsToListOfStationsFull.put( "TR2", stnListForProductsWith45Stns );
//    mapProductsToListOfStationsFull.put( "TV0", stnListForProductsWith45Stns );
//    mapProductsToListOfStationsFull.put( "TV1", stnListForProductsWith45Stns );
//    mapProductsToListOfStationsFull.put( "TV2", stnListForProductsWith45Stns );
//    mapProductsToListOfStationsFull.put( "TZL", stnListForProductsWith45Stns );
//  }
}