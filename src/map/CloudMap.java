package map;

import exceptions.NoSuchUserException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lars Mortensen
 */
public class CloudMap<K, V> implements Map<K, V>, KeyObserver<K, V> {

  private static final String id = "lam";
  private static final String pw = "lam";
  private static final String DataBaseURL = "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat";
  private String user;
  private KeyObserver observer;
  private Map<K,KeyPoller> pollers = new HashMap();
  
  Connection con = null;

  public CloudMap(String user) {
    try {
      con = DriverManager.getConnection(DataBaseURL, id, pw);
      checkUser(user);
      this.user = user;
      
    } catch (SQLException e) {
      System.out.println(e);
    }
  }
  
  private void checkUser(String user){
    try {
      String sql = "select * from CLOUDMAP_USERS where USERID = ?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, user);
      ResultSet rs = ps.executeQuery();
      if (!rs.next()) {
        throw new NoSuchUserException("This user name does not exist");
      }
    } catch (SQLException ex) {
      Logger.getLogger(CloudMap.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public int size() {
    String sql = "SELECT COUNT(*) FROM CLOUDMAP2 where USERID = ?";
    try {
      PreparedStatement stmt = con.prepareStatement(sql);
      stmt.setString(1,user);
              
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        return rs.getInt("COUNT(*)");
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKey(Object o) {
    return get((K)o) != null;
  }

  @Override
  public boolean containsValue(Object o) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public V get(Object o) {
    try {
      String sql = "select * from CLOUDMAP2 where key = ? and userid = ?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, o.toString());
      ps.setString(2,user);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
          ByteArrayInputStream bais;
          ObjectInputStream ois;
          bais = new ByteArrayInputStream(rs.getBytes("VALUE"));
          ois = new ObjectInputStream(bais);
          V value =(V)ois.readObject();
          return value;
      }

   } catch (IOException | SQLException | ClassNotFoundException ex) {
     String msg = "";
     if(ex instanceof ClassNotFoundException){
       msg="\nWas this value serialized from the same type as the one you are trying to de-serialize into?";
     }
      throw new RuntimeException(ex+msg);
    }
    return null;
  }

  @Override
  public V put(K k, V v) {
    V previousValue = remove(k);
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(v);
      oos.flush();
      oos.close();
      bos.close();
      byte[] data = bos.toByteArray();

      String sql = "insert into CLOUDMAP2(key,value,userid) values (?,?,?)";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, k.toString());
      ps.setBytes(2, data);
      ps.setString(3, user);
      ps.executeUpdate();
    } catch (IOException | SQLException  ex ) {
      throw new RuntimeException(ex);
    }
    return previousValue;
  }

  @Override
  public V remove(Object o) {
    V lastValue = get((K)o);
    try {
      String sql = "delete from CLOUDMAP2 where KEY = ? and userid = ?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1,o.toString());
      ps.setString(2,user);
      ps.executeUpdate();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
    return lastValue;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> map) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void clear() {
    try {
      String sql = "delete from CLOUDMAP2 where userid = ?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, user);
      ps.executeUpdate();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public Set<K> keySet() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Collection<V> values() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  
  
  public void registerAsKeyObserver(KeyObserver o){
    observer = o;
  }
  
  public void unRegisterKeyObserver(){
    observer = null;
  }
  
  public void watchKey(K key){
    KeyPoller<K,V> poller = new KeyPoller(this, key, con);
    poller.start();
    pollers.put(key, poller);
  }
  
  public void stopWatchKey(K key){
    KeyPoller kp = pollers.remove(key);
    if(kp !=null){
      kp.stopKeyPoller();
    }
  }
  
  void notifyListener(K key,V oldVal, V newVal){
    if(observer!=null){
      observer.dataChanged(key,oldVal,newVal);
    }
  }

  @Override
  public void dataChanged(K key,V oldValue, V newValue) {
    System.out.println(key+ " Changed: "+newValue + "   From :"+ oldValue);
  }
  
  public static void main(String[] args) throws InterruptedException {
    System.out.println("AAAAAAAAAA");
    CloudMap<String,String> map = new CloudMap("test-user100");
    System.out.println(map.size());
    map.put("aaaa", "Hello World");
    map.remove("aaaa");
    map.put("aaaa", "Hello World");
    map.registerAsKeyObserver(map);
    map.watchKey("aaaa");
    Thread.sleep(2000);
    map.put("aaaa", "Hello Hello World");
    //map.stopWatchKey("aaaa");
    Thread.sleep(4000);
    map.put("aaaa", "Hello Wonderful World");
    Thread.sleep(4000);
    
  }

}
