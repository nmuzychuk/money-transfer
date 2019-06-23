package com.nmuzychuk;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static spark.Spark.*;

public class App {

    private static Map<String, Account> accounts = new ConcurrentHashMap<>();
    private static Map<String, Transfer> transfers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        final AtomicInteger accountId = new AtomicInteger(0);
        final AtomicInteger transferId = new AtomicInteger(0);

        post("/accounts", (request, response) -> {
            String name = request.queryParams("name");
            int balance = Integer.parseInt(request.queryParams("balance"));

            Account account = new Account(accountId.incrementAndGet(), name, new AtomicInteger(balance));
            accounts.put(String.valueOf(accountId), account);

            response.status(201);
            return account.toString();
        });

        get("/accounts/:id", (request, response) -> {
            Account account = accounts.get(request.params(":id"));
            if (account != null) {
                return account.toString();
            } else {
                response.status(404);
                return "Account not found";
            }
        });

        get("/accounts", (request, response) -> accounts.toString());

        final Jedis transferPublisher = new Jedis();

        post("/transfers", (request, response) -> {
            String sender = request.queryParams("sender");
            String receiver = request.queryParams("receiver");
            int amount = Integer.parseInt(request.queryParams("amount"));

            Transfer transfer = new Transfer(transferId.incrementAndGet(), "New", sender, receiver, amount);
            transfers.put(String.valueOf(transferId), transfer);

            transferPublisher.publish("transfers", "" + transferId);

            response.status(201);
            return transfer.toString();
        });

        get("/transfers", (request, response) -> transfers.toString());

        final Jedis transferSubscriber = new Jedis();

        JedisPubSub subscriber = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                Transfer transfer = transfers.get(message);
                System.out.println("Processing " + transfer);

                Account sender = accounts.get(transfer.getSender());

                if (sender == null) {
                    transfer.setStatus("Invalid sender");
                    return;
                }

                Account receiver = accounts.get(transfer.getReceiver());

                if (receiver == null) {
                    transfer.setStatus("Invalid receiver");
                    return;
                }

                int senderBalance = sender.getBalance().get();

                if (senderBalance < transfer.getAmount()) {
                    transfer.setStatus("Insufficient balance");
                    return;
                }

                boolean isCredited = sender.getBalance().compareAndSet(senderBalance, senderBalance - transfer.getAmount());
                if (isCredited) {
                    receiver.getBalance().addAndGet(transfer.getAmount());
                    transfer.setStatus("Complete");
                } else {
                    // retry transfer;
                }
            }
        };

        new Thread(() -> {
            try {
                transferSubscriber.subscribe(subscriber, "transfers");
                System.out.println("Subscription ended.");
            } catch (Exception e) {
                System.err.println("Subscribing failed. " + e);
            }
        }).start();
    }

    public static class Account {

        private int id;
        private String name;
        private AtomicInteger balance;

        public Account(int id, String name, AtomicInteger balance) {
            this.id = id;
            this.name = name;
            this.balance = balance;
        }

        public int getId() {
            return id;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        public String getName() {
            return name;
        }

        public AtomicInteger getBalance() {
            return balance;
        }
    }

    public static class Transfer {
        private int id;
        private String status;
        private String sender;
        private String receiver;
        private int amount;

        Transfer(int id, String status, String sender, String receiver, int amount) {
            this.id = id;
            this.status = status;
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        public String getSender() {
            return sender;
        }

        public String getReceiver() {
            return receiver;
        }

        public int getAmount() {
            return amount;
        }
    }
}
