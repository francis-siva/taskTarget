package com.codestudiocorp;

public class Main {
    static final String JSON_FILE = "./src/main/resources/static/input/data.json";

    public static void main(String[] args) {
        System.out.println("Task scheduler wip [=>]");

        FileAnalyser.readFile(JSON_FILE);

    }
}