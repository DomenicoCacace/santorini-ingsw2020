package it.polimi.ingsw.view.cli.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SafeScanner {
    private final Scanner scanner;


    public SafeScanner(InputStream inputStream) {
        scanner = new Scanner(inputStream);
    }

    public String nextLine() throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        try {
            while (!br.ready() && !Thread.interrupted()) {
                Thread.sleep(200);
            }
            input = br.readLine();
            return input;
        } catch (InputMismatchException e) {
            e.printStackTrace();
            return nextLine();
        }
    }

    public int nextInt() throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int input = -1;
        try {
            while (!br.ready()) {
                Thread.sleep(200);
            }
            input = Integer.parseInt(br.readLine());
            return input;
        } catch (InputMismatchException e) {
            e.printStackTrace();
            return nextInt();
        }
    }
}



