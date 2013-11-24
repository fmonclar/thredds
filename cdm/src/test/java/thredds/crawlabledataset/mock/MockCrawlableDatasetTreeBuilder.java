package thredds.crawlabledataset.mock;

import thredds.crawlabledataset.CrawlableDataset;
import thredds.crawlabledataset.CrawlableDatasetUtils;
import thredds.crawlabledataset.filter.WildcardMatchOnNameFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * _MORE_
 *
 * @author edavis
 */
public class MockCrawlableDatasetTreeBuilder {
  private final MockCrawlableDataset baseCrDs;
  private MockCrawlableDataset cursor;
  private MockCrawlableDataset mostRecentlyAdded;

  public MockCrawlableDatasetTreeBuilder( String basePath, boolean isCollection ) {
    this.baseCrDs = new MockCrawlableDataset( basePath, isCollection );
    this.cursor = baseCrDs;
    this.mostRecentlyAdded = baseCrDs;
  }

  public void setExistsForCurrent( boolean exists) {
    this.cursor.setExists( exists);
  }

  public boolean getExistsForCurrent() {
    return this.cursor.exists();
  }

  public void setLastModifiedForCurrent( Date lastModified ) {
    this.cursor.setLastModified( lastModified);
  }

  public Date getLastModifiedForCurrent() {
    return this.cursor.lastModified();
  }

  public void setLengthForCurrent( long length ) {
    this.cursor.setLength( length );
  }

  public long getLengthForCurrent() {
    return this.cursor.length();
  }

  public void setExistsForMostRecentlyAdded( boolean exists) {
    this.mostRecentlyAdded.setExists( exists);
  }

  public boolean getExistsForMostRecentlyAdded() {
    return this.mostRecentlyAdded.exists();
  }

  public void setLastModifiedForMostRecentlyAdded( Date lastModified ) {
    this.mostRecentlyAdded.setLastModified( lastModified);
  }

  public Date getLastModifiedForMostRecentlyAdded() {
    return this.mostRecentlyAdded.lastModified();
  }

  public void setLengthForMostRecentlyAdded( long length ) {
    this.mostRecentlyAdded.setLength( length );
  }

  public long getLengthForMostRecentlyAdded() {
    return this.mostRecentlyAdded.length();
  }

  public void addChild( String childRelativePath, boolean isCollection ) {
    MockCrawlableDataset child = new MockCrawlableDataset( childRelativePath, isCollection);
    this.cursor.addChild( child);
    child.setParent( this.cursor);
    this.mostRecentlyAdded = child;
  }

  public String moveDown( String childPath ) {
    if ( ! this.cursor.isCollection())
      return null;
    String[] pathSegments = CrawlableDatasetUtils.getPathSegments(childPath);
    if ( ! CrawlableDatasetUtils.isValidRelativePath( pathSegments)
        || pathSegments.length != 1 )
      throw new IllegalArgumentException( String.format("Child path [%s] not single path segment (or not a valid relative path).", childPath));
    WildcardMatchOnNameFilter filter = new WildcardMatchOnNameFilter(childPath);
    List<CrawlableDataset> matchingCrDs = null;
    try {
      matchingCrDs = this.cursor.listDatasets( filter);
    } catch (IOException e) {
      throw new IllegalStateException( "Shouldn't get an IOE on MockCrDs", e);
    }
    if ( matchingCrDs.isEmpty() )
      throw new IllegalArgumentException( "Child path [%s] did not match an existing child.");
    if ( matchingCrDs.size() != 1 )
      throw new IllegalArgumentException( "Child path [%s] matched more than one child.");
    this.cursor = (MockCrawlableDataset) matchingCrDs.get( 0);
    return this.cursor.getPath();
  }

  public String moveUp() {
    MockCrawlableDataset parentDataset = (MockCrawlableDataset) this.cursor.getParentDataset();
    if ( parentDataset != null )
      this.cursor = parentDataset;
    return this.cursor.getPath();
  }

  public MockCrawlableDataset build() {
    return this.baseCrDs;
  }

}
