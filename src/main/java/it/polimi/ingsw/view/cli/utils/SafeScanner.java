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

    public String nextLine() {
        while (true) {
            try {
                return scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.next();
            }
        }
    }

    public int nextInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.next();
            }
        }
    }


}
