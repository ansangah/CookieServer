package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EnduringPVPRelay implements Runnable {
    //player1
    Socket player1;
    BufferedReader player1in;
    BufferedWriter player1out;
    //player2
    Socket player2;
    BufferedReader player2in;
    BufferedWriter player2out;
    //playerName
    String player1userName;
    String player2userName;
    //constructor
    public EnduringPVPRelay(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
        try {
            player1in = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            player1out = new BufferedWriter(new OutputStreamWriter(player1.getOutputStream()));
            player2in = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            player2out = new BufferedWriter(new OutputStreamWriter(player2.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //run
    @Override
    public void run() {
        try {
            player1out.write("connect\n");
            player2out.write("connect\n");
            player1out.flush();
            player2out.flush();
            player1userName = player1in.readLine();
            player2userName = player2in.readLine();
            System.out.println("Player 1: " + player1userName + "Player 2: " + player2userName);
            player1out.write(player2userName + "\n");
            player2out.write(player1userName + "\n");
            player1out.flush();
            player2out.flush();
            for (int i = 5; i > 0; i--){
                player1out.write(i +"\n");
                player2out.write(i +"\n");
                player1out.flush();
                player2out.flush();
                Thread.sleep(1000);
            }
            player1out.write("start\n");
            player2out.write("start\n");
            player1out.flush();
            player2out.flush();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        String player1life;
        String player2life;
        while(true) {
            try {
                player1life = player1in.readLine();
                player2life = player2in.readLine();
                if(player1life.equals("gameOver")){
                    player1out.write("end\nloser\n");
                    player2out.write("end\nwinner\n");
                    player1out.flush();
                    player2out.flush();
                    int player1userRate = 100;
                    int player2userRate = 100;
                    if(Server.EnduringUserRating.containsKey(player1userName)) player1userRate = Server.EnduringUserRating.get(player1userName);
                    if(Server.EnduringUserRating.containsKey(player2userName)) player2userRate = Server.EnduringUserRating.get(player2userName);
                    //
                    double player1WinningRate = 1/(Math.pow(10,(player2userRate-player1userRate)/400.0)+1);
                    double player2WinningRate = 1 - player1WinningRate;
                    int upDown = (int) (20*(1-player2WinningRate));
                    player1userRate-=upDown;
                    player2userRate+=upDown;
                    //
                    receiveEnduringModeLate(player1userName, player1userRate);
                    receiveEnduringModeLate(player2userName, player2userRate);
                    break;
                }
                else if(player2life.equals("gameOver")){
                    player1out.write("end\nwinner\n");
                    player2out.write("end\nloser\n");
                    player1out.flush();
                    player2out.flush();
                    int player1userRate = 100;
                    int player2userRate = 100;
                    if(Server.EnduringUserRating.containsKey(player1userName)) player1userRate = Server.EnduringUserRating.get(player1userName);
                    if(Server.EnduringUserRating.containsKey(player2userName)) player2userRate = Server.EnduringUserRating.get(player2userName);
                    //
                    double player1WinningRate = 1/(Math.pow(10,(player2userRate-player1userRate)/400.0)+1);
                    double player2WinningRate = 1 - player1WinningRate;
                    int upDown = (int) (20*(1-player1WinningRate));
                    player1userRate+=upDown;
                    player2userRate-=upDown;
                    //
                    receiveEnduringModeLate(player1userName, player1userRate);
                    receiveEnduringModeLate(player2userName, player2userRate);
                    break;
                }
                else{
                    player1out.write(player2life + "\n");
                    player2out.write(player1life + "\n");
                    player1out.flush();
                    player2out.flush();
                }
                Thread.sleep(10);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        new Thread(new CommunicationWithClient(player1)).start();
        new Thread(new CommunicationWithClient(player2)).start();
    }
    //other method
    private void receiveEnduringModeLate(String userName, int newUserRate) {
        System.out.println("receiveEnduringModeLate");
        if (Server.EnduringUserRating.containsKey(userName)) {
            int currentUserRate = Server.EnduringUserRating.get(userName);
            List<String> userNames = Server.EnduringUserRank.get(currentUserRate);

            Server.EnduringUserRating.remove(userName);
            Server.EnduringUserRating.put(userName, newUserRate);

            Server.EnduringUserRank.remove(currentUserRate);
            userNames.remove(userName);
            Server.EnduringUserRank.put(currentUserRate, userNames);

            List<String> newUserNames = new ArrayList<>();
            if (Server.EnduringUserRank.containsKey(newUserRate)) {
                newUserNames = Server.EnduringUserRank.get(newUserRate);
                Server.EnduringUserRank.remove(newUserRate);
            }
            newUserNames.add(userName);
            Server.EnduringUserRank.put(newUserRate, newUserNames);
        } else {
            Server.EnduringUserRating.put(userName, newUserRate);
            List<String> newUserNames = new ArrayList<>();
            if (Server.EnduringUserRank.containsKey(newUserRate)) {
                newUserNames = Server.EnduringUserRank.get(newUserRate);
                Server.EnduringUserRank.remove(newUserRate);
            }
            newUserNames.add(userName);
            Server.EnduringUserRank.put(newUserRate, newUserNames);
        }
    }
}