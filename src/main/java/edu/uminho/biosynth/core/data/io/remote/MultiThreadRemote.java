package edu.uminho.biosynth.core.data.io.remote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.AbstractBiosynthEntity;
import edu.uminho.biosynth.core.components.GenericEnzyme;
import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.data.io.IRemoteSource;
import edu.uminho.biosynth.core.data.io.ISource;

@Deprecated
public class MultiThreadRemote {
	
	/*************************************************************/
	/*
	 * DEBUG
	 * THESE ARE FOR DEBUGGING
	 */
	
	public final static ArrayList<Long> timeRxn = new ArrayList<Long> ();
	public final static ArrayList<Long> timeCpd = new ArrayList<Long> ();
	public final static ArrayList<Long> timeEcn = new ArrayList<Long> ();
	
	/*************************************************************/
	
	private final static Logger LOGGER = Logger.getLogger(MultiThreadRemote.class.getName());

	private final ISource gatherEngine;
	private final int numThreads;
	private final List<Thread> threadArray;
	private final Map<Integer, Map<String, AbstractBiosynthEntity>> threadResults;
	
	public MultiThreadRemote(IRemoteSource engine, int numThreads) {
		this.gatherEngine = engine;
		this.numThreads = numThreads;
		this.threadArray = new ArrayList<Thread> ();
		this.threadResults = new HashMap<Integer, Map<String,AbstractBiosynthEntity>> ();
	}
	
	public MultiThreadRemote(ISource engine, int numThreads) {
		this.gatherEngine = engine;
		this.numThreads = numThreads;
		this.threadArray = new ArrayList<Thread> ();
		this.threadResults = new HashMap<Integer, Map<String,AbstractBiosynthEntity>> ();
	}
	
	private class Worker<T extends AbstractBiosynthEntity> implements Runnable {

		private final ISource engine;
		
		private final int tid;
		private final List<String> work;
		
		private final Class<T> type;
		private final List<AbstractBiosynthEntity> result;
		private final MultiThreadRemote parent;
		
		public Worker(int tid, List<String> work, MultiThreadRemote parent, Class<T> type) {
			this.tid = tid;
			this.work = work;
			this.engine = parent.getGatherEngine();
			this.type = type;
			this.result = new ArrayList<>( work.size());
			this.parent = parent;
			for (int i = 0; i < work.size(); i++) this.result.add(null); 
		}
		
		public Map<String, AbstractBiosynthEntity> getResult() {
			Map<String, AbstractBiosynthEntity> resultMap = new HashMap<String, AbstractBiosynthEntity> ();
			for (int i = 0; i < work.size(); i++) {
				resultMap.put( work.get(i), result.get(i));
			}
			return resultMap;
		}
		
		@Override
		public void run() {
			if (type.equals(GenericMetabolite.class)) {
				for (int i = 0; i < work.size(); i++) {
					GenericMetabolite cpd = null;
					long start = System.currentTimeMillis();
					cpd = this.engine.getMetaboliteInformation( this.work.get(i));
					long end = System.currentTimeMillis();
					MultiThreadRemote.timeCpd.add(end - start);
					result.set(i, cpd);
				}
				this.parent.getThreadResults().put(tid,  this.getResult());
			} else if (type.equals(GenericReaction.class)) {
				for (int i = 0; i < work.size(); i++) {
					GenericReaction rxn = null;
					long start = System.currentTimeMillis();
					rxn = this.engine.getReactionInformation( this.work.get(i));
					long end = System.currentTimeMillis();
					MultiThreadRemote.timeRxn.add(end - start);
					result.set(i, rxn);
				}
				this.parent.getThreadResults().put(tid,  this.getResult());
			} else if (type.equals(GenericEnzyme.class)) {
				for (int i = 0; i < work.size(); i++) {
					GenericEnzyme ecn = null;
					long start = System.currentTimeMillis();
					ecn = this.engine.getEnzymeInformation( this.work.get(i));
					long end = System.currentTimeMillis();
					MultiThreadRemote.timeEcn.add(end - start);
					result.set(i, ecn);
				}
			} else {
				LOGGER.log(Level.SEVERE, "WORKER Unsupported Type - " + this.type);
			}
		}
		
		@Override
		public String toString() {
			return work.toString();
		}
		
	}
	
	private Map<Integer, List<String>> distributeWork(Set<String> workQueue) {
		Map<Integer, List<String>> workPerThread = new HashMap<>();
		int i;
		for (i = 0; i < numThreads; i++) {
			workPerThread.put(i, new ArrayList<String>());
		}
		i = 0;
		for (String cpdId : workQueue) {
			workPerThread.get(i).add(cpdId);
			i = i < (numThreads - 1) ? i + 1 : 0;
		}
		
		return workPerThread;
	}

	public void clear() {
		this.threadArray.clear();
		this.threadResults.clear();
	}
	
	public void startAll() {
		for (int i = 0; i < this.threadArray.size(); i++) {
			this.threadArray.get(i).start();
		}
	}
	
	public void joinAll() {
		for (int i = 0; i < threadArray.size(); i++) {
			try {
				this.threadArray.get(i).join();
			} catch (InterruptedException iEx) {
				LOGGER.log(Level.SEVERE, "INTEX - " + iEx.getMessage());
			}
		}
	}
	
	public synchronized Map<Integer, Map<String, AbstractBiosynthEntity>> getThreadResults() {
		return threadResults;
	}

	public ISource getGatherEngine() {
		return gatherEngine;
	}

	public Map<String, GenericMetabolite> getMetaboliteSetInformation(Set<String> cpdIdSet) {
		this.clear();
		Map<Integer, List<String>> workPerThread = distributeWork( cpdIdSet);
		
		for (int i = 0; i < numThreads; i++) {
			Worker<GenericMetabolite> w = new Worker<>(i, workPerThread.get(i), this, GenericMetabolite.class);
			this.threadResults.put(i, new HashMap<String, AbstractBiosynthEntity>());
			Thread thread = new Thread(w);
			LOGGER.log(Level.INFO, thread + " => " + workPerThread.get(i).size());
			this.threadArray.add(thread);
		}
		
		
		this.startAll();
		
		this.joinAll();
		
		Map<String, GenericMetabolite> retMap = new HashMap<> ();
		for (Integer tid : this.threadResults.keySet()) {
			Map<String,AbstractBiosynthEntity> threadResult = this.threadResults.get(tid);
			for (String key : threadResult.keySet()) {
				if (retMap.put(key, (GenericMetabolite) threadResult.get(key)) != null) {
					LOGGER.log(Level.SEVERE, "Collision detected");
				}
			}
		}
		
		return retMap;
	}
	public Map<String, GenericReaction> getReactionSetInformation(Set<String> rxnIdSet) {
		this.clear();
		Map<Integer, List<String>> workPerThread = distributeWork( rxnIdSet);
		
		for (int i = 0; i < numThreads; i++) {
			Worker<GenericReaction> w = new Worker<>(i, workPerThread.get(i), this, GenericReaction.class);
			Thread thread = new Thread(w);
			LOGGER.log(Level.INFO, thread + " => " + workPerThread.get(i).size());
			this.threadArray.add(thread);
		}
		
		this.startAll();
		
		this.joinAll();
		
		Map<String, GenericReaction> retMap = new HashMap<> ();
		for (Integer tid : this.threadResults.keySet()) {
			Map<String,AbstractBiosynthEntity> threadResult = this.threadResults.get(tid);
			for (String key : threadResult.keySet()) {
				if ( retMap.put(key, (GenericReaction) threadResult.get(key)) != null) {
					LOGGER.log(Level.SEVERE, "Collision detected");
				}
			}
		}
		
		return retMap;
	}
	public Map<String, GenericEnzyme> getEnzymeSetInformation(Set<String> ecnIdSet) {
		this.clear();
		Map<Integer, List<String>> workPerThread = distributeWork( ecnIdSet);
		
		for (int i = 0; i < numThreads; i++) {
			Worker<GenericEnzyme> w = new Worker<>(i, workPerThread.get(i), this, GenericEnzyme.class);
			Thread thread = new Thread(w);
			LOGGER.log(Level.INFO, thread + " => " + workPerThread.get(i).size());
			this.threadArray.add(thread);
		}
		
		this.startAll();
		
		this.joinAll();
		
		Map<String, GenericEnzyme> retMap = new HashMap<> ();
		for (Integer tid : this.threadResults.keySet()) {
			Map<String,AbstractBiosynthEntity> threadResult = this.threadResults.get(tid);
			for (String key : threadResult.keySet()) {
				if ( retMap.put(key, (GenericEnzyme) threadResult.get(key)) != null) {
					LOGGER.log(Level.SEVERE, "Collision detected");
				}
			}
		}
		
		return retMap;
	}
}
