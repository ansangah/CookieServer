package main;

import Server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {
    public static Server server = new Server(8000);

    public static void main(String[] args) {
        //현재 외부 IP 주소 출력
        System.out.println(getMyIp());
        //서버 작동
        server.run();
        //입력을 통한 서버 종료
        int exit = 1;
        Scanner scanner = new Scanner(System.in);
        while(exit != 0){
            exit = scanner.nextInt();
            if(exit == 0){
                System.out.println("진짜 종료?");
                exit = scanner.nextInt();
            }
        }
        server.closeServer();
    }

    private static String getMyIp(){
        String ip = "";
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            ip = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }
}