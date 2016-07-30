import java.util.Random;
import java.util.concurrent.*;

import static java.lang.System.out;

/**
 * Created by chenshuwang on 2016/7/30.
 */
public class Main {
    public static final int jobSleep = 1000;

    public static int doSomething() {
        try {
            Thread.sleep(jobSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 10000/0;
    }

    public static void main(String[] args) {
        /*
        ExecutorService es = Executors.newFixedThreadPool(10);
        Future<Integer> f = es.submit(() -> 100);

        try {
            out.println(f.get());
            f.cancel(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        */

        CompletableFuture<Integer> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> 10000);
        //CompletableFuture<Integer> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> 10000/0);

        supplyAsyncFuture.whenComplete((v, e) -> {
            out.println("(whenComplete) v: " + v);
            out.println("(whenComplete) e: " + e);
        });

        supplyAsyncFuture.whenCompleteAsync((v, e) -> {
            out.println("(whenCompleteAsync) v: " + v);
            out.println("(whenCompleteAsync) e: " + e);
        });

        supplyAsyncFuture.exceptionally(e -> {
            out.println("(exceptionally) ex: " + e);
            return 0;
        }).whenCompleteAsync((v, e) -> {
            out.println("(exceptionally) v: " + v);
            out.println("(exceptionally) e: " + e);
        });

        out.println("Hello, World!");

        try {
            Thread.sleep(jobSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> out.println("hello, runAsync."));
        out.println("hello, runAsync2.");

        supplyAsyncFuture
            .thenApplyAsync(i->i*2)
            .thenApply(i->i+3)
            .whenComplete((v, e) -> out.println("(thenApplyFuture1) v: " + v));

        supplyAsyncFuture
            .thenApplyAsync(i->i*2)
            .thenApply(i->i/0)
            .handleAsync((v, e) -> {
                out.println(v);
                out.println(e);
                return 0;
            }).whenComplete((v, e) -> out.println("(thenApplyFuture2) v: " + v));

        supplyAsyncFuture.thenAccept(i -> out.println("(thenAccept) thenAccept: " + i));
        supplyAsyncFuture.thenAcceptAsync(i -> out.println("(thenAcceptAsync) thenAcceptAsync: " + i));

        supplyAsyncFuture.thenAcceptBoth(CompletableFuture.completedFuture(20), (x, y) -> {
            out.println("(thenAcceptBoth) x: " + x);
            out.println("(thenAcceptBoth) y: " + y);
        });

        supplyAsyncFuture.thenRun(() -> out.println("all finished."));

        supplyAsyncFuture
            .thenCompose(i -> CompletableFuture.supplyAsync(() -> i + 3))
            .whenCompleteAsync((v, e) -> out.println("(thenCompose) v: " + v));

        CompletableFuture<Integer> thenCombineFuture1 = CompletableFuture.supplyAsync(() -> 11);
        CompletableFuture<Integer> thenCombineFuture2 = CompletableFuture.supplyAsync(() -> 22);

        thenCombineFuture1.thenCombine(thenCombineFuture2, (x, y) -> x + y)
            .whenComplete((v, e) -> out.println("(thenCombine) v: " + v));

        Random rand = new Random();
        CompletableFuture<Integer> applyToEitherFuture1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(jobSleep + rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1000;
        });
        CompletableFuture<Integer> applyToEitherFuture2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(jobSleep + rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2000;
        });

        applyToEitherFuture1.applyToEither(applyToEitherFuture2, i -> i)
            .whenComplete((v, e) -> out.println("(applyToEither) v: " + v)) ;

        CompletableFuture.anyOf(applyToEitherFuture1, applyToEitherFuture2)
                .whenComplete((v, e) -> out.println("(anyOf) v: " + v)) ;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
