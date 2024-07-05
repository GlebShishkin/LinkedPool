package ru.stepup.edu;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

// структура для потоков с объектом синхронизации типа семафор.
class SynTask  implements Runnable {
    private Semaphore sem;
    private Runnable rn;

    public SynTask(Semaphore sem, Runnable rn) {
        this.sem = sem;
        this.rn = rn;
    }

    @Override
    public void run() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            rn.run();
        } finally {

        }
        sem.release();
    }
}

public class Processor {
    private int capacity;   // емкость пула
    private Semaphore semaphore;
    private final Queue<SynTask> tasks = new LinkedList<SynTask>(); // очередь задач с объектом семафора
    private Thread mainThread;

    private volatile Boolean shutdown = false;

    public Processor(int capacity) {
        this.capacity = capacity;
        semaphore = new Semaphore(capacity);
    }

    public void shutdown() {
        shutdown = true;
    }

    public void execute(Runnable runnable) {

        if (shutdown) {
            // ТЗ "метод shutdown(), после выполнения которого новые задачи больше не принимаются пулом
            // (при попытке добавить задачу можно бросать IllegalStateException)"
            throw new IllegalStateException("Прием заданий в пул потоков завершен, но будут выполнены незавершенные задания");
        }

        synchronized(tasks) {
            // добавляем в LinkedList очередную задачу с объектом семофора
            tasks.add(new SynTask(this.semaphore, runnable));
        }
    }

    public void start () {
        // отдельным потоком запускаем потоки пула из списка "tasks"
        Runnable r = ()->{
            while (!shutdown) {
                if (!tasks.isEmpty()) {
                    synchronized (tasks) {
                        SynTask synTask = tasks.peek();
                        new Thread(synTask).start();
                        tasks.remove();
                    }
                }
            }
        };
        mainThread = new Thread(r,"mainThread");
        mainThread.start();
    }

    boolean awaitTermination(long timeout)  throws InterruptedException
    {
        long step = timeout/10;
        long t = System.currentTimeMillis();
        long end = t + timeout;
        // в цикле ждем и проверяем освобождение светофора
        while(System.currentTimeMillis() < end) {
            if (semaphore.availablePermits() == this.capacity) {
                return true;    // светофор освободился
            }
            Thread.sleep( step );
            // System.out.println("semaphore.availablePermits() = " + semaphore.availablePermits());
        }
        return false;   // светофор не освободился
    }
}
