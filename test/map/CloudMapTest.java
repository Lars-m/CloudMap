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
  
  Map<String,Person> cmUser1;
  Map<String,Person> cmUser2;
  
  public CloudMapTest() {
     cmUser1 = new CloudMap("test-user100");
     cmUser2 = new CloudMap("test-user200");
  }
  
  @Before
  public void setUp() {
    //OK, this is cheating since I'm using the control under test to set up the DB ;-)
    cmUser1.clear();
    cmUser2.clear();
  }

  /**
   * Test of size method, of class CloudMap.
   */
  @Test
  public void testSize() {
    cmUser1.put("Lars1", new Person("Lars","Mortensen"));
    cmUser1.put("Lars2", new Person("Lars","Mortensen"));
    cmUser1.put("Lars3", new Person("Lars","Mortensen"));
    assertEquals("Size should be 3", 3, cmUser1.size());
  }

  /**
   * Test of isEmpty method, of class CloudMap.
   */
  @Test
  public void testIsEmpty() {
    assertEquals("Size should be 0", 0, cmUser1.size());
  }

  /**
   * Test of containsKey method, of class CloudMap.
   */
  @Test
  public void testContainsKey() {
    cmUser1.put("Lars", new Person("Lars","Mortensen"));
    assertTrue("'Lars'", cmUser1.containsKey("Lars"));
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
    cmUser1.put("Lars", expected);
    assertEquals("'Lars' instance",expected,cmUser1.get("Lars"));
  }

  /**
   * Test of put method, of class CloudMap.
   */
  @Test
  public void testPut() {
    //Test that previous value is null and value is inserted correctly
    Person expected = new Person("Lars","Mortensen");
    Person previousExpected = cmUser1.put("Lars", expected);
    assertEquals("'Lars' instance",expected,cmUser1.get("Lars"));
    assertNull("Previous value should be null",previousExpected);
    
    //Test the new previous value and value is changed correctly
    Person expectedNew = new Person("xxx","yyy");
    previousExpected = cmUser1.put("Lars", expectedNew);
    assertEquals("'Lars' instance",expectedNew,cmUser1.get("Lars"));
    assertEquals("'Lars' instance",previousExpected,expected);
    
  }

  /**
   * Test of remove method, of class CloudMap.
   */
  @Test
  public void testRemove() {
    Person inserted = new Person("Lars","Mortensen");
    cmUser1.put("Lars",inserted);
    Person p = cmUser1.remove("Lars");
    assertEquals("Size should be 0", 0, cmUser1.size());
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
    cmUser1.put("Lars1", new Person("Lars","Mortensen"));
    cmUser1.put("Lars2", new Person("Lars","Mortensen"));
    cmUser1.put("Lars3", new Person("Lars","Mortensen"));
    cmUser1.clear();
    assertEquals("Size should be 0", 0, cmUser1.size());
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
  
  @Test
   public void testUserFunctionality1(){
     //Verify that same KEY can exist in the database with different values for different users
     Person personForUser1 = new Person("A","B");
     Person personForUser2 = new Person("AA","BB");
     cmUser1.put("KEY", personForUser1);
     cmUser2.put("KEY", personForUser2);
     assertEquals(personForUser1,cmUser1.get("KEY"));
     assertEquals(personForUser2,cmUser2.get("KEY"));
  }
   
  @Test
   public void testUserFunctionality2(){
     //Verify that same KEY value can be deleted for one user without interfering with the same key for another user
     Person personForUser1 = new Person("A","B");
     Person personForUser2 = new Person("AA","BB");
     cmUser1.put("KEY", personForUser1);
     cmUser2.put("KEY", personForUser2);
     cmUser1.remove("KEY");
     assertNull(cmUser1.get("KEY"));
     assertEquals(personForUser2,cmUser2.get("KEY"));
  }
  
}
