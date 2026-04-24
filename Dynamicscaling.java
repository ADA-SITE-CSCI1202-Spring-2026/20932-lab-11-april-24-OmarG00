public class DynamicScaling {

    static class MathTask implements Runnable {
        private final int taskId;
        private final long iterations;

        public MathTask(int taskId, long iterations) {
            this.taskId = taskId;
            this.iterations = iterations;
        }

        @Override
        public void run() {
            long sum = 0;
            for (long i = 0; i < iterations; i++) {
                sum += (i * i * i) + (i * taskId);
            }
        }
    }
    static long runBenchmark(int threadCount) throws InterruptedException {
        long iterationsPerThread = 10_000_000L;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new MathTask(i, iterationsPerThread));
        }

        long startTime = System.currentTimeMillis();

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        return System.currentTimeMillis() - startTime;
    }

    public static void main(String[] args) throws InterruptedException {
        int coreCount = Runtime.getRuntime().availableProcessors();

        System.out.println("Logical processors detected: " + coreCount);

        System.out.println("Running with 1 thread...");
        long singleThreadTime = runBenchmark(1);
        System.out.printf("Time with 1 thread     : %d ms%n", singleThreadTime);

        System.out.println("Running with " + coreCount + " thread(s)...");
        long multiThreadTime = runBenchmark(coreCount);
        System.out.printf("Time with %d thread(s) : %d ms%n", coreCount, multiThreadTime);

        System.out.println("----------------------------------------");
        if (multiThreadTime < singleThreadTime) {
            double speedup = (double) singleThreadTime / multiThreadTime;
            System.out.printf("Speedup: %.2fx faster with %d threads%n", speedup, coreCount);
        } else {
            System.out.println("Multi-thread was not faster (thread overhead exceeded benefit).");
        }
    }
}