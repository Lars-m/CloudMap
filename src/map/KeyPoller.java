package map;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Lars Mortensen
 */
 class KeyPoller<K,V> extends Thread {
   CloudMap<K,V> cloudMap;
   private final int SLEEP_TIME = 3000;
   private boolean running = true;
   K keyToWatch;
   Connection con;

  public void stopKeyPoller() {
    this.running = false;
  }

  public KeyPoller(CloudMap<K,V> cloudMap,K key, Connection con) {
    this.cloudMap = cloudMap;
    this.keyToWatch = key;
    this.con = con;
    this.setDaemon(true);
  }
  
  @Override
  public void run(){
    V originalValue = cloudMap.get(keyToWatch);
    while(running){
      try {
        Thread.sleep(SLEEP_TIME);
        V newValue = cloudMap.get(keyToWatch);
        if(!newValue.equals(originalValue)){
          cloudMap.notifyListener(keyToWatch, originalValue,newValue);
          originalValue = newValue;
        }  
      } catch (InterruptedException ex) {
        Logger.getLogger(KeyPoller.class.getName()).log(Level.SEVERE, null, ex);
      }
    } 
  }
}
