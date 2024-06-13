package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
    ServerSocket serverSocket;
    public static Map<String, Integer> NormalScoreUserScore = new TreeMap<>();
    public static Map< Integer, List<String> > NormalScoreUserRank = new TreeMap<>(Comparator.reverseOrder());
    public static Map<String, Integer> HardScoreUserScore = new TreeMap<>();
    public static Map< Integer, List<String> > HardScoreUserRank = new TreeMap<>(Comparator.reverseOrder());
    public static Map<String, Integer> SpeedUserRating = new TreeMap<>();
    public static Map< Integer, List<String> > SpeedUserRank = new TreeMap<>(Comparator.reverseOrder());
    public static Map<String, Integer> EnduringUserRating = new TreeMap<>();
    public static Map< Integer, List<String> > EnduringUserRank = new TreeMap<>(Comparator.reverseOrder());

    public static Queue<Socket> SpeedMatchingQueue = new LinkedList<>();
    public static Queue<Socket> EnduringMatchingQueue = new LinkedList<>();

    static public List<Socket> socketList = new ArrayList<>();
    public Server(int port){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeServer(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Socket socket : socketList){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
    public void run(){
        new Thread(() -> {
            while(!serverSocket.isClosed()){
                try {
                    Socket socket = serverSocket.accept();
                    socketList.add(socket);
                    new Thread(new CommunicationWithClient(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            while (!serverSocket.isClosed()){
                while (SpeedMatchingQueue.size() < 2){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Socket player1 = SpeedMatchingQueue.poll();
                Socket player2 = SpeedMatchingQueue.poll();
                System.out.println("matchingSuccess");
                new Thread(new SpeedPVPRelay(player1, player2)).start();
            }
        }).start();
        new Thread(() -> {
            while (!serverSocket.isClosed()){
                while (EnduringMatchingQueue.size() < 2){
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Socket player1 = EnduringMatchingQueue.poll();
                Socket player2 = EnduringMatchingQueue.poll();
                if(player1.isConnected() && player2.isConnected()){
                    System.out.println("matchingSuccess");
                    new Thread(new EnduringPVPRelay(player1, player2)).start();
                }
                else{
                    if(player1.isConnected()){
                        EnduringMatchingQueue.add(player1);
                    }
                    if(player2.isConnected()){
                        EnduringMatchingQueue.add(player2);
                    }
                }
            }
        }).start();
    }
}

