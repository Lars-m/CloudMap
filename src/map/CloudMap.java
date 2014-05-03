package map;

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
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Lars Mortensen
 */
public class CloudMap<K, V> implements Map<K, V> {

  private static final String id = "lam";
  private static final String pw = "lam";
  private static final String DataBaseURL = "jdbc:oracle:thin:@datdb.cphbusiness.dk:1521:dat";
  Connection con = null;

  public CloudMap() {
    try {
      con = DriverManager.getConnection(DataBaseURL, id, pw);
    } catch (SQLException e) {
      System.out.println(e);
    }
  }

  @Override
  public int size() {
    String sql = "SELECT COUNT(*) FROM CLOUDMAP";
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
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
      String sql = "select * from CLOUDMAP where key = ?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, o.toString());
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
    V previousValue =remove(k);
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(v);
      oos.flush();
      oos.close();
      bos.close();
      byte[] data = bos.toByteArray();

      String sql = "insert into CLOUDMAP values (?,?)";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, k.toString());
      ps.setBytes(2, data);
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
      String sql = "delete from CLOUDMAP where KEY = ?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1,o.toString());
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
      String sql = "delete from CLOUDMAP";
      PreparedStatement ps = con.prepareStatement(sql);
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

}
