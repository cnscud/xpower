package com.cnscud.xpower.configcenter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zkclient.IZkClient;
import com.github.zkclient.exception.ZkException;

/**
 * Distributed locking via ZooKeeper. Assuming there are N clients that all try to acquire a lock,
 * the algorithm works as follows. Each host creates an ephemeral|sequential node, and requests a
 * list of children for the lock node. Due to the nature of sequential, all the ids are increasing
 * in order, therefore the client with the least ID according to natural ordering will hold the
 * lock. Every other client watches the id immediately preceding its own id and checks for the lock
 * in case of notification. The client holding the lock does the work and finally deletes the node,
 * thereby triggering the next client in line to acquire the lock. Deadlocks are possible but
 * avoided in most cases because if a client drops dead while holding the lock, the ZK session
 * should timeout and since the node is ephemeral, it will be removed in such a case. Deadlocks
 * could occur if the the worker thread on a client hangs but the zk-client thread is still alive.
 * There could be an external monitor client that ensures that alerts are triggered if the least-id
 * ephemeral node is present past a time-out.
 * <p/>
 * Note: Locking attempts will fail in case session expires!
 *
 * @author Florian Leibert
 * @see https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/zookeeper/DistributedLockImpl.java
 */
public class DistributedLockImpl implements IDistributedLock {

  private static final Logger LOG = LoggerFactory.getLogger(DistributedLockImpl.class);

  private final IZkClient zkClient;
  private final String lockPath;

  private final AtomicBoolean aborted = new AtomicBoolean(false);
  private CountDownLatch syncPoint;
  private boolean holdsLock = false;
  private String currentId;
  private String currentNode;
  private String watchedNode;
  private LockWatcher watcher;

  /**
   * Equivalent to {@link #DistributedLockImpl(ZooKeeperClient, String, Iterable)} with a default
   * wide open {@code acl} ({@link ZooDefs.Ids#OPEN_ACL_UNSAFE}).
   */
  public DistributedLockImpl(IZkClient zkClient, String lockPath) {
    this(zkClient, lockPath, ZooDefs.Ids.OPEN_ACL_UNSAFE);
  }

  /**
   * Creates a distributed lock using the given {@code zkClient} to coordinate locking.
   *
   * @param zkClient The ZooKeeper client to use.
   * @param lockPath The path used to manage the lock under.
   * @param acl The acl to apply to newly created lock nodes.
   */
  private DistributedLockImpl(IZkClient zkClient, String lockPath, List<ACL> acl) {
    this.zkClient = zkClient;
    this.lockPath = lockPath;
    this.syncPoint = new CountDownLatch(1);
  }

  private synchronized void prepare()
    throws  InterruptedException, KeeperException {
    zkClient.createPersistent(lockPath, true);

    // Create an EPHEMERAL_SEQUENTIAL node.
    currentNode =
        zkClient.createEphemeralSequential(lockPath + "/member_", new byte[0]);

    // We only care about our actual id since we want to compare ourselves to siblings.
    if (currentNode.contains("/")) {
      currentId = currentNode.substring(currentNode.lastIndexOf("/") + 1);
    }
    LOG.debug("Received ID from zk: {}", currentId);
    this.watcher = new LockWatcher();
  }

  @Override
  public synchronized void lock() throws LockingException {
    if (holdsLock) {
      throw new LockingException("Error, already holding a lock. Call unlock first!");
    }
    try {
      prepare();
      watcher.checkForLock();
      syncPoint.await();
      if (!holdsLock) {
        throw new LockingException("Error, couldn't acquire the lock!");
      }
    } catch (InterruptedException e) {
      cancelAttempt();
      throw new LockingException("InterruptedException while trying to acquire lock!", e);
    } catch (KeeperException e) {
      // No need to clean up since the node wasn't created yet.
      throw new LockingException("KeeperException while trying to acquire lock!", e);
    } catch (ZkException ze) {
      // No need to clean up since the node wasn't created yet.
      throw new LockingException("ZooKeeperConnectionException while trying to acquire lock", ze);
    }
  }

  @Override
  public synchronized boolean tryLock(long timeout, TimeUnit unit) {
    if (holdsLock) {
      throw new LockingException("Error, already holding a lock. Call unlock first!");
    }
    try {
      prepare();
      watcher.checkForLock();
      boolean success = syncPoint.await(timeout, unit);
      if (!success) {
        return false;
      }
      if (!holdsLock) {
        throw new LockingException("Error, couldn't acquire the lock!");
      }
    } catch (InterruptedException e) {
      cancelAttempt();
      return false;
    } catch (KeeperException e) {
      // No need to clean up since the node wasn't created yet.
      throw new LockingException("KeeperException while trying to acquire lock!", e);
    } catch (ZkException e) {
      // No need to clean up since the node wasn't created yet.
      throw new LockingException("ZooKeeperConnectionException while trying to acquire lock", e);
    }
    return true;
  }

  @Override
  public synchronized void unlock() throws LockingException {
    if (currentId == null) {
      throw new LockingException("Error, neither attempting to lock nor holding a lock!");
    }
    // Try aborting!
    if (!holdsLock) {
      aborted.set(true);
      LOG.info("Not holding lock, aborting acquisition attempt! {}", this.lockPath);
    } else {
      LOG.info("Cleaning up this locks ephemeral node. {}", this.lockPath);
      cleanup();
    }
  }

  //TODO(Florian Leibert): Make sure this isn't a runtime exception. Put exceptions into the token?

  private synchronized void cancelAttempt() {
    LOG.info("Cancelling lock attempt! {}", this.lockPath);
    cleanup();
    // Bubble up failure...
    holdsLock = false;
    syncPoint.countDown();
  }

  private void cleanup() {
    LOG.info("Cleaning up! {}", this.lockPath);
    try {
      Stat stat = zkClient.getZooKeeper().exists(currentNode, false);
      if (stat != null) {
        zkClient.delete(currentNode);
      } else {
        LOG.warn("Called cleanup but nothing to cleanup!");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    holdsLock = false;
    aborted.set(false);
    currentId = null;
    currentNode = null;
    watcher = null;
    syncPoint = new CountDownLatch(1);
  }

  class LockWatcher implements Watcher {

    public synchronized void checkForLock() {

      try {
        List<String> candidates = zkClient.getZooKeeper().getChildren(lockPath,false);
        Collections.sort(candidates);
        List<String> sortedMembers = candidates;
        // Unexpected behavior if there are no children!
        if (sortedMembers.isEmpty()) {
          throw new LockingException("Error, member list is empty!");
        }

        int memberIndex = sortedMembers.indexOf(currentId);

        // If we hold the lock
        if (memberIndex == 0) {
          holdsLock = true;
          syncPoint.countDown();
        } else {
          final String nextLowestNode = sortedMembers.get(memberIndex - 1);
          LOG.info("Current LockWatcher with ephemeral node [{}], is " +
              "waiting for [{}] to release lock.", currentId, nextLowestNode);

          watchedNode = String.format("%s/%s", lockPath, nextLowestNode);
          Stat stat = zkClient.getZooKeeper().exists(watchedNode, this);
          if (stat == null) {
            checkForLock();
          }
        }
      } catch (InterruptedException e) {
        LOG.warn(String.format("Current LockWatcher with ephemeral node [%s] " +
            "got interrupted. Trying to cancel lock acquisition.", currentId), e);
        cancelAttempt();
      } catch (KeeperException e) {
        LOG.warn(String.format("Current LockWatcher with ephemeral node [%s] " +
            "got a KeeperException. Trying to cancel lock acquisition.", currentId), e);
        cancelAttempt();
      } catch (ZkException e) {
        LOG.warn( String.format("Current LockWatcher with ephemeral node [%s] " +
            "got a ConnectionException. Trying to cancel lock acquisition.", currentId), e);
        cancelAttempt();
      }
    }

    @Override
    public synchronized void process(WatchedEvent event) {
      // this handles the case where we have aborted a lock and deleted ourselves but still have a
      // watch on the nextLowestNode. This is a workaround since ZK doesn't support unsub.
      if (!event.getPath().equals(watchedNode)) {
        LOG.info("Ignoring call for node:" + watchedNode);
        return;
      }
      //TODO(Florian Leibert): Pull this into the outer class.
      if (event.getType() == Watcher.Event.EventType.None) {
        switch (event.getState()) {
          case SyncConnected:
            // TODO(Florian Leibert): maybe we should just try to "fail-fast" in this case and abort.
            LOG.info("Reconnected...");
            break;
          case Expired:
            LOG.warn(String.format("Current ZK session expired![%s]", currentId));
            cancelAttempt();
            break;
        }
      } else if (event.getType() == Event.EventType.NodeDeleted) {
        checkForLock();
      } else {
        LOG.warn( String.format("Unexpected ZK event: %s", event.getType().name()));
      }
    }
  }
}