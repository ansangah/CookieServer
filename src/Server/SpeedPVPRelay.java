package Server;

import java.io.*;
import java.net.Socket;

public class SpeedPVPRelay implements Runnable {
    //player1
    Socket player1;
    BufferedReader player1in;
    BufferedWriter player1out;
    int player1DelayTime = 0;
    boolean player1Death = false;
    //player2
    Socket player2;
    BufferedReader player2in;
    BufferedWriter player2out;
    int player2DelayTime = 0;
    boolean player2Death = false;
    //other variable
    boolean isGameEnded = false;
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
        while(!isGameEnded) {

        }
        new Thread(new CommunicationWithClient(player1)).start();
        new Thread(new CommunicationWithClient(player2)).start();
    }
    //other method
}
