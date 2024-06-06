package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommunicationWithClient implements Runnable {
    Socket socket;
    BufferedReader in;
    BufferedWriter out;
    boolean isInPVPMode = false;

    public CommunicationWithClient(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String input = "";
        while (!socket.isClosed() && !isInPVPMode) {
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (input) {
                case "communicationExit" -> communicationExit();
                case "sendNormalScoreRank" -> answerSendNormalScoreRank();
                case "receiveNormalScoreModeScore" -> receiveNormalScoreModeScore();
                case "sendHardScoreRank" -> answerSendHardScoreRank();
                case "receiveHardScoreModeScore" -> receiveHardScoreModeScore();
                case "applySpeedPVP" -> applySpeedPVP();
                case "applyEnduringPVP" -> applyEnduringPVP();
            }
        }
    }

    private void communicationExit() {
        try {
            socket.close();
            Server.socketList.remove(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answerSendNormalScoreRank() {
        System.out.println("sendNormalScoreRank");
        Iterator<Integer> userScoreIterator = Server.NormalScoreUserRank.keySet().iterator();
        int i = 0;
        while (userScoreIterator.hasNext() && i < 100) {
            int userScore = userScoreIterator.next();
            List<String> userNames = Server.NormalScoreUserRank.get(userScore);
            i = sendRankingInformation(i, userScore, userNames);
        }
        sendNullRankSpace(i);
    }

    private void receiveNormalScoreModeScore() {
        System.out.println("receiveNormalScoreModeScore");
        String userName;
        int newUserScore;
        try {
            userName = in.readLine();
            newUserScore = Integer.parseInt(in.readLine());
            if (Server.NormalScoreUserScore.containsKey(userName)) {
                int currentUserScore = Server.NormalScoreUserScore.get(userName);
                List<String> userNames = Server.NormalScoreUserRank.get(currentUserScore);

                Server.NormalScoreUserScore.remove(userName);
                Server.NormalScoreUserScore.put(userName, newUserScore);

                Server.NormalScoreUserRank.remove(currentUserScore);
                userNames.remove(userName);
                Server.NormalScoreUserRank.put(currentUserScore, userNames);

                List<String> newUserNames = new ArrayList<>();
                if (Server.NormalScoreUserRank.containsKey(newUserScore)) {
                    newUserNames = Server.NormalScoreUserRank.get(newUserScore);
                    Server.NormalScoreUserRank.remove(newUserScore);
                }
                newUserNames.add(userName);
                Server.NormalScoreUserRank.put(newUserScore, newUserNames);
            } else {
                Server.NormalScoreUserScore.put(userName, newUserScore);
                List<String> newUserNames = new ArrayList<>();
                if (Server.NormalScoreUserRank.containsKey(newUserScore)) {
                    newUserNames = Server.NormalScoreUserRank.get(newUserScore);
                    Server.NormalScoreUserRank.remove(newUserScore);
                }
                newUserNames.add(userName);
                Server.NormalScoreUserRank.put(newUserScore, newUserNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void answerSendHardScoreRank() {
        System.out.println("sendHardScoreRank");
        Iterator<Integer> userScoreIterator = Server.HardScoreUserRank.keySet().iterator();
        int i = 0;
        while (userScoreIterator.hasNext() && i < 100) {
            int userScore = userScoreIterator.next();
            List<String> userNames = Server.HardScoreUserRank.get(userScore);
            i = sendRankingInformation(i, userScore, userNames);
        }
        sendNullRankSpace(i);
    }

    private void receiveHardScoreModeScore() {
        System.out.println("receiveHardScoreModeScore");
        String userName;
        int newUserScore;
        try {
            userName = in.readLine();
            newUserScore = Integer.parseInt(in.readLine());
            if (Server.HardScoreUserScore.containsKey(userName)) {
                int currentUserScore = Server.HardScoreUserScore.get(userName);
                List<String> userNames = Server.HardScoreUserRank.get(currentUserScore);

                Server.HardScoreUserScore.remove(userName);
                Server.HardScoreUserScore.put(userName, newUserScore);

                Server.HardScoreUserRank.remove(currentUserScore);
                userNames.remove(userName);
                Server.HardScoreUserRank.put(currentUserScore, userNames);

                List<String> newUserNames = new ArrayList<>();
                if (Server.HardScoreUserRank.containsKey(newUserScore)) {
                    newUserNames = Server.HardScoreUserRank.get(newUserScore);
                    Server.HardScoreUserRank.remove(newUserScore);
                }
                newUserNames.add(userName);
                Server.HardScoreUserRank.put(newUserScore, newUserNames);
            } else {
                Server.HardScoreUserScore.put(userName, newUserScore);
                List<String> newUserNames = new ArrayList<>();
                if (Server.HardScoreUserRank.containsKey(newUserScore)) {
                    newUserNames = Server.HardScoreUserRank.get(newUserScore);
                    Server.HardScoreUserRank.remove(newUserScore);
                }
                newUserNames.add(userName);
                Server.HardScoreUserRank.put(newUserScore, newUserNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int sendRankingInformation(int i, int userScore, List<String> userNames) {
        for (int j = 0; j < userNames.size() && i < 100; j++) {
            try {
                out.write(userNames.get(j));
                for (int k = 0; k < 10 - userNames.get(j).length(); k++) out.write("  ");
                out.write("        " + userScore + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
        return i;
    }

    private void sendNullRankSpace(int i) {
        if (i < 100) {
            for (int j = 0; j < 100 - i; j++) {
                try {
                    out.write(" \n");
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void applySpeedPVP() {
        Server.SpeedMatchingQueue.add(this.socket);
        isInPVPMode = true;
    }

    private void applyEnduringPVP() {
        Server.EnduringMatchingQueue.add(this.socket);
        isInPVPMode = true;
    }
}
