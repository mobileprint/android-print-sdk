package com.hp.impulselib.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Manages a queue of tasks that run in the background and can throw IOExceptions.
 * Similar to Android's AsyncTask but less messy.
 */
public class Tasks implements AutoCloseable {
    private final static String LOG_TAG = "Tasks";

    private Thread mThread;

    private final List<Task> mTasks = new LinkedList<>();
    boolean mRunning;

    /** A task to execute on the background thread */
    public interface Task {
        /** Work on the task (background thread) */
        void run() throws IOException;

        /** Handle an error thrown by the task (main thread) */
        void onError(IOException exception);

        /** Handle successful task completion (main thread) */
        void onDone();
    }

    /** Start execution, listening for exceptions caused by any task */
    public Tasks() {
        Log.d(LOG_TAG, "Tasks() " + this);
        mThread = new Thread() {
            @Override
            public void run() {
                doRun();
            }
        };
        mRunning = true;
        mThread.start();
    }

    /** Queue a task if this object is still live */
    public void queue(Task task) {
        Log.d(LOG_TAG, "queue() " + this + " " + task);
        if (!mRunning) return;
        synchronized (mTasks) {
            mTasks.add(task);
            mTasks.notify();
        }
    }

    /** Closes, silently interrupting or cancelling any queued tasks. */
    @Override
    public void close() {
        Log.d(LOG_TAG, "close() " + this);
        mRunning = false;

        // Stop the thread if waiting on IO
        mThread.interrupt();

        synchronized (mTasks) {
            mTasks.clear();
            // In case we are waiting on tasks
            mTasks.notify();
        }
    }

    /** Return true if still running */
    public boolean isOpen() {
        return !mThread.isInterrupted();
    }

    private void doRun() {
        Log.d(LOG_TAG, "doRun() " + mThread);
        while (mRunning) {
            final Task task = getNextTask();
            if (task == null) return;

            Log.d(LOG_TAG, "Got task " + task);
            try {
                task.run();
                if (mRunning) {
                    runMain(new Runnable() {
                        @Override
                        public void run() {
                            task.onDone();
                        }
                    });
                }
            } catch (final IOException e) {
                Log.d(LOG_TAG, "Task threw exception " + e );
                // Signal error if we weren't intentionally closed
                if (mRunning) {
                    runMain(new Runnable() {
                        @Override
                        public void run() {
                            task.onError(e);
                        }
                    });
                }
                break;
            }
            Log.d(LOG_TAG, "Looping");
        }
        Log.d(LOG_TAG, "Ending thread " + mThread);
    }

    /** Return number of tasks in queue, not including currently running task */
    int getTaskCount() {
        synchronized(mTasks) {
            return mTasks.size();
        }
    }

    private Task getNextTask() {
        synchronized (mTasks) {
            if (mTasks.isEmpty()) {
                try {
                    mTasks.wait();
                } catch (InterruptedException ignored) {
                    mRunning = false;
                }
            }
            if (!mRunning || mTasks.isEmpty()) return null;
            return mTasks.remove(0);
        }
    }

    /** Run something on main thread (or immediately on the current thread if it is the main one) */
    public static void runMain(Runnable toRun) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            toRun.run();
        } else {
            new Handler(Looper.getMainLooper()).post(toRun);
        }
    }

    /** Run something on main thread after delay */
    public static void runMainDelayed(long delay, Runnable runnable) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, delay);
    }

    public static AutoCloseable run(final Task task) {
        final Tasks tasks = new Tasks();
        tasks.queue(new Task() {
            @Override
            public void run() throws IOException {
                task.run();
            }

            @Override
            public void onError(final IOException exception) {
                tasks.close();
                runMain(new Runnable() {
                    @Override
                    public void run() {
                        task.onError(exception);
                    }
                });
            }

            @Override
            public void onDone() {
                tasks.close();
                runMain(new Runnable() {
                    @Override
                    public void run() {
                        task.onDone();
                    }
                });
            }
        });

        return new AutoCloseable() {
            @Override
            public void close() throws Exception {
                Log.d(LOG_TAG, "Closing single-use task");
                tasks.close();
            }
        };
    }
}
