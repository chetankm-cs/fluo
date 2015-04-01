/*
 * Copyright 2014 Fluo authors (see AUTHORS)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fluo.api.client;

import io.fluo.api.config.FluoConfiguration;

/**
 * Provides methods for initializing and administering a Fluo application.
 */
public interface FluoAdmin extends AutoCloseable {
  
  /**
   * Specifies Fluo initialization options such as clearing Zookeeper or existing Accumulo table.
   */
  public static class InitOpts {
    private boolean clearZookeeper = false;
    private boolean clearTable = false;
    
    /** 
     * Clears zookeeper root (if exists) specified by {@value FluoConfiguration#CLIENT_ZOOKEEPER_CONNECT_PROP}.  Default is false.
     */
    public InitOpts setClearZookeeper(boolean clearZookeeper) {
      this.clearZookeeper = clearZookeeper;
      return this;
    }
    
    public boolean getClearZookeeper() {
      return clearZookeeper;
    }
    
    /** 
     * Clears accumulo table (if exists) specified by {@value FluoConfiguration#ADMIN_ACCUMULO_TABLE_PROP}.  Default is false.
     */
    public InitOpts setClearTable(boolean clearTable) {
      this.clearTable = clearTable;
      return this;
    }
    
    public boolean getClearTable() {
      return clearTable;
    }
  }

  /**
   * Exception that is thrown if Fluo application was already initialized. An application is already initialized if a directory with same name as application exists
   * at the chroot directory set by the property io.fluo.client.zookeeper.connect. If this directory can be cleared, set {@link InitOpts#setClearTable(boolean)} to true
   */
  public static class AlreadyInitializedException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public AlreadyInitializedException(String msg) {
      super(msg);
    }

    public AlreadyInitializedException() {
      super();
    }
  }

  /**
   * Exception that is thrown if Accumulo table (set by io.fluo.admin.accumulo.table) exists during initialization. If this table can be cleared, set
   * {@link InitOpts#setClearZookeeper(boolean)} to true
   */
  public static class TableExistsException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public TableExistsException(String msg) {
      super(msg);
    }

    public TableExistsException() {
      super();
    }
  }

  /**
   * Initializes Fluo application and stores shared configuration in Zookeeper. Shared configuration consists of properties with
   * {@value io.fluo.api.config.FluoConfiguration#APP_PREFIX}, {@value io.fluo.api.config.FluoConfiguration#OBSERVER_PREFIX} and
   * {@value io.fluo.api.config.FluoConfiguration#TRANSACTION_PREFIX} prefixes. Throws {@link AlreadyInitializedException} if Fluo application was already
   * initialized in Zookeeper. If you want to initialize Zookeeper again, set {@link InitOpts#setClearZookeeper(boolean)} to true. Throws
   * {@link TableExistsException} if Accumulo table exists. If you want to clear table, set {@link InitOpts#setClearTable(boolean)} to true.
   */
  public void initialize(InitOpts opts) throws AlreadyInitializedException, TableExistsException;

  /**
   * Updates shared configuration in Zookeeper. Shared configuration consists of properties with {@value io.fluo.api.config.FluoConfiguration#APP_PREFIX},
   * {@value io.fluo.api.config.FluoConfiguration#OBSERVER_PREFIX} and {@value io.fluo.api.config.FluoConfiguration#TRANSACTION_PREFIX} prefixes. This
   * method is called if a user has previously called {@link #initialize()} but wants changes to shared configuration updated in Zookeeper.
   * 
   * <p>
   * During this method Observers are reinitialized using configuration passed to FluoAdmin and not existing shared configuration stored in zookeeper. So make
   * sure all config needed by observers is present.
   */
  public void updateSharedConfig();

  @Override
  public void close();
}
