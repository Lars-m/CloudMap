package map;

import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 * @author Lars Mortensen
 */
public class CloudMapTest {
  
  Map<String,Person> cm;
  
  public CloudMapTest() {
     cm = new CloudMap();
  }
  
  @Before
  public void setUp() {
    //OK, this is cheating since I'm using the control under test to set up the DB ;-)
    cm.clear();
  }

  /**
   * Test of size method, of class CloudMap.
   */
  @Test
  public void testSize() {
    cm.put("Lars1", new Person("Lars","Mortensen"));
    cm.put("Lars2", new Person("Lars","Mortensen"));
    cm.put("Lars3", new Person("Lars","Mortensen"));
    assertEquals("Size should be 3", 3, cm.size());
  }

  /**
   * Test of isEmpty method, of class CloudMap.
   */
  @Test
  public void testIsEmpty() {
    assertEquals("Size should be 0", 0, cm.size());
  }

  /**
   * Test of containsKey method, of class CloudMap.
   */
  @Test
  public void testContainsKey() {
    cm.put("Lars", new Person("Lars","Mortensen"));
    assertTrue("'Lars'", cm.containsKey("Lars"));
  }

  /**
   * Test of containsValue method, of class CloudMap.
   */
  @Test
  @Ignore
  public void testContainsValue() {
  }

  /**
   * Test of get method, of class CloudMap.
   */
  @Test
  public void testGet() {
    Person expected = new Person("Lars","Mortensen");
    cm.put("Lars", expected);
    assertEquals("'Lars' instance",expected,cm.get("Lars"));
  }

  /**
   * Test of put method, of class CloudMap.
   */
  @Test
  public void testPut() {
    //Test that previous value is null and value is inserted correctly
    Person expected = new Person("Lars","Mortensen");
    Person previousExpected = cm.put("Lars", expected);
    assertEquals("'Lars' instance",expected,cm.get("Lars"));
    assertNull("Previous value should be null",previousExpected);
    
    //Test the new previous value and value is changed correctly
    Person expectedNew = new Person("xxx","yyy");
    previousExpected = cm.put("Lars", expectedNew);
    assertEquals("'Lars' instance",expectedNew,cm.get("Lars"));
    assertEquals("'Lars' instance",previousExpected,expected);
    
  }

  /**
   * Test of remove method, of class CloudMap.
   */
  @Test
  public void testRemove() {
    Person inserted = new Person("Lars","Mortensen");
    cm.put("Lars",inserted);
    Person p = cm.remove("Lars");
    assertEquals("Size should be 0", 0, cm.size());
    assertEquals(inserted, p);
  }

  /**
   * Test of putAll method, of class CloudMap.
   */
  @Test
  @Ignore
  public void testPutAll() {
  }

  /**
   * Test of clear method, of class CloudMap.
   */
  @Test
  public void testClear() {
    cm.put("Lars1", new Person("Lars","Mortensen"));
    cm.put("Lars2", new Person("Lars","Mortensen"));
    cm.put("Lars3", new Person("Lars","Mortensen"));
    cm.clear();
    assertEquals("Size should be 0", 0, cm.size());
  }

  /**
   * Test of keySet method, of class CloudMap.
   */
  @Test
  @Ignore
  public void testKeySet() {
  }

  /**
   * Test of values method, of class CloudMap.
   */
  @Test
  @Ignore
  public void testValues() {
  }

  /**
   * Test of entrySet method, of class CloudMap.
   */
  @Test
  @Ignore
  public void testEntrySet() {
  }
  
}
