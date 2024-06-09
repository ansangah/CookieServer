package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SpeedPVPRelay implements Runnable {
    //player1
    Socket player1;
    BufferedReader player1in;
    BufferedWriter player1out;
    //player2
    Socket player2;
    BufferedReader player2in;
    BufferedWriter player2out;
    //userName
    String player1userName;
    String player2userName;
    //constructor
    public SpeedPVPRelay(Socket player1, Socket player2) {
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

        String player1position;
        String player2position;
        while(true) {
            try {
                player1position = player1in.readLine();
                player2position = player2in.readLine();
                if(player1position.equals("gameEnd")){
                    player1out.write("end\nwinner\n");
                    player2out.write("end\nloser\n");
                    player1out.flush();
                    player2out.flush();
                    int player1userRate = 100;
                    int player2userRate = 100;
                    if(Server.SpeedUserRating.containsKey(player1userName)) player1userRate = Server.SpeedUserRating.get(player1userName);
                    if(Server.SpeedUserRating.containsKey(player2userName)) player2userRate = Server.SpeedUserRating.get(player2userName);
                    //
                    double player1WinningRate = 1/(Math.pow(10,(player2userRate-player1userRate)/400.0)+1);
                    double player2WinningRate = 1 - player1WinningRate;
                    int upDown = (int) (20*(1-player1WinningRate));
                    player1userRate+=upDown;
                    player2userRate-=upDown;
                    //
                    receiveSpeedModeLate(player1userName, player1userRate);
                    receiveSpeedModeLate(player2userName, player2userRate);
                    break;
                }
                else if(player2position.equals("gameEnd")){
                    player1out.write("end\nloser\n");
                    player2out.write("end\nwinner\n");
                    player1out.flush();
                    player2out.flush();
                    int player1userRate = 100;
                    int player2userRate = 100;
                    if(Server.SpeedUserRating.containsKey(player1userName)) {
                        player1userRate = Server.SpeedUserRating.get(player1userName);
                    }
                    if(Server.SpeedUserRating.containsKey(player2userName)) {
                        player2userRate = Server.SpeedUserRating.get(player2userName);
                    }
                    //
                    double player1WinningRate = 1/(Math.pow(10,(player2userRate-player1userRate)/400.0)+1);
                    double player2WinningRate = 1 - player1WinningRate;
                    int upDown = (int) (20*(1-player2WinningRate));
                    player1userRate-=upDown;
                    player2userRate+=upDown;
                    //
                    receiveSpeedModeLate(player1userName, player1userRate);
                    receiveSpeedModeLate(player2userName, player2userRate);
                    break;
                }
                else if (player1position.equals("gameOver")) {
                    player1out.write("end\nloser\n");
                    player2out.write("end\nwinner\n");
                    player1out.flush();
                    player2out.flush();
                    int player1userRate = 100;
                    int player2userRate = 100;
                    if(Server.SpeedUserRating.containsKey(player1userName)) player1userRate = Server.SpeedUserRating.get(player1userName);
                    if(Server.SpeedUserRating.containsKey(player2userName)) player2userRate = Server.SpeedUserRating.get(player2userName);
                    //
                    double player1WinningRate = 1/(Math.pow(10,(player2userRate-player1userRate)/400.0)+1);
                    double player2WinningRate = 1 - player1WinningRate;
                    int upDown = (int) (20*(1-player2WinningRate));
                    player1userRate-=upDown;
                    player2userRate+=upDown;
                    //
                    receiveSpeedModeLate(player1userName, player1userRate);
                    receiveSpeedModeLate(player2userName, player2userRate);
                    break;
                }
                else if (player2position.equals("gameOver")) {
                    player1out.write("end\nwinner\n");
                    player2out.write("end\nloser\n");
                    player1out.flush();
                    player2out.flush();
                    int player1userRate = 100;
                    int player2userRate = 100;
                    if(Server.SpeedUserRating.containsKey(player1userName)) player1userRate = Server.SpeedUserRating.get(player1userName);
                    if(Server.SpeedUserRating.containsKey(player2userName)) player2userRate = Server.SpeedUserRating.get(player2userName);
                    //
                    double player1WinningRate = 1/(Math.pow(10,(player2userRate-player1userRate)/400.0)+1);
                    double player2WinningRate = 1 - player1WinningRate;
                    int upDown = (int) (20*(1-player1WinningRate));
                    player1userRate+=upDown;
                    player2userRate-=upDown;
                    //
                    receiveSpeedModeLate(player1userName, player1userRate);
                    receiveSpeedModeLate(player2userName, player2userRate);
                    break;
                }
                else{
                    player1out.write(player2position + "\n");
                    player2out.write(player1position + "\n");
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
    private void receiveSpeedModeLate(String userName, int newUserRate) {
        System.out.println("receiveSpeedModeLate");
        if (Server.SpeedUserRating.containsKey(userName)) {
            int currentUserRate = Server.SpeedUserRating.get(userName);
            List<String> userNames = Server.SpeedUserRank.get(currentUserRate);

            Server.SpeedUserRating.remove(userName);
            Server.SpeedUserRating.put(userName, newUserRate);

            Server.SpeedUserRank.remove(currentUserRate);
            userNames.remove(userName);
            Server.SpeedUserRank.put(currentUserRate, userNames);

            List<String> newUserNames = new ArrayList<>();
            if (Server.SpeedUserRank.containsKey(newUserRate)) {
                newUserNames = Server.SpeedUserRank.get(newUserRate);
                Server.SpeedUserRank.remove(newUserRate);
            }
            newUserNames.add(userName);
            Server.SpeedUserRank.put(newUserRate, newUserNames);
        } else {
            Server.SpeedUserRating.put(userName, newUserRate);
            List<String> newUserNames = new ArrayList<>();
            if (Server.SpeedUserRank.containsKey(newUserRate)) {
                newUserNames = Server.SpeedUserRank.get(newUserRate);
                Server.SpeedUserRank.remove(newUserRate);
            }
            newUserNames.add(userName);
            Server.SpeedUserRank.put(newUserRate, newUserNames);
        }
    }
}
