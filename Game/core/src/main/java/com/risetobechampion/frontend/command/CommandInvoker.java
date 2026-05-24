package com.risetobechampion.frontend.command;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandInvoker {
    private static final CommandInvoker INSTANCE = new CommandInvoker();
    private final ConcurrentLinkedQueue<Command> queue = new ConcurrentLinkedQueue<>();

    private CommandInvoker() {
    }

    public static CommandInvoker getInstance() {
        return INSTANCE;
    }

    public void execute(Command cmd) {
        if (cmd == null) return;
        try {

            cmd.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enqueue(Command cmd) {
        if (cmd != null) queue.add(cmd);
    }

    public void drain() {
        Command c;
        while ((c = queue.poll()) != null) {
            try {
                c.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
