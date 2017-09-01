package handler.threading;

import handler.fx.uifx.FXWindow;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class TaskHandler {

    protected FXWindow context;
    protected ForkJoinPool executor;
    protected final int parallelism;

    public TaskHandler(FXWindow context) {
        this(context, Runtime.getRuntime().availableProcessors() / 2);
    }

    public TaskHandler(FXWindow context, int parallelism) {
        parallelism = Math.max(1, Math.min(parallelism, Runtime.getRuntime().availableProcessors()));
        this.context = context;
        this.parallelism = parallelism;

        executor = (ForkJoinPool) Executors.newWorkStealingPool(parallelism);
    }

    public void Post(Task task) {
        if(executor.isShutdown()) {
            System.out.println("Attempting to post task on shutdown executor, executing synchronously...");
            task.run();
            return;
        }
        executor.execute(task);
    }

    public void Shutdown() {
        executor.shutdown();
    }


}
