package tech.ikora.evolution.process;

import java.util.HashSet;
import java.util.Set;

public class Synchronization {
    private final Set<Thread> initializationThreads = new HashSet<>();

    public void register(Synchronizable synchronizable){
        Thread thread = synchronizable.getInitializationThread();
        if(thread != null){
            initializationThreads.add(thread);
        }
    }

    public void waitForInitialization() throws InterruptedException {
        for(Thread thread: initializationThreads){
            thread.join();
        }
    }
}
