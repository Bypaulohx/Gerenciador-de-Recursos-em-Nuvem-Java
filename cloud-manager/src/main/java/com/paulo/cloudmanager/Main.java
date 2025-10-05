package com.paulo.cloudmanager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String region = System.getenv("AWS_REGION");
        if (region == null || region.isBlank()) {
            System.out.print("Enter AWS region (e.g. us-east-1) [us-east-1]: ");
            String input = scanner.nextLine().trim();
            region = input.isEmpty() ? "us-east-1" : input;
        }

        try (EC2Service ec2 = new EC2Service(region)) {
            loop:
            while (true) {
                System.out.println("\nCommands: create, list, start, stop, terminate, help, exit");
                System.out.print("> ");
                String line = scanner.nextLine().trim();
                switch (line.toLowerCase(Locale.ROOT)) {
                    case "create":
                        System.out.print("AMI id: ");
                        String ami = scanner.nextLine().trim();
                        System.out.print("Instance type (e.g. t2.micro): ");
                        String type = scanner.nextLine().trim();
                        System.out.print("Key pair name (or blank): ");
                        String key = scanner.nextLine().trim();
                        System.out.print("Security group ids (comma separated, or blank): ");
                        String sgs = scanner.nextLine().trim();
                        List<String> sgList = sgs.isBlank() ? Collections.emptyList()
                                : Arrays.stream(sgs.split(",")).map(String::trim).collect(Collectors.toList());
                        System.out.print("Count: ");
                        int count = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Name tag (or blank): ");
                        String name = scanner.nextLine().trim();
                        System.out.print("User data script path (or blank): ");
                        String userDataPath = scanner.nextLine().trim();
                        String userData = null;
                        if (!userDataPath.isBlank()) {
                            userData = Files.readString(Path.of(userDataPath));
                        }

                        List<String> ids = ec2.createInstances(ami, type, key.isBlank() ? null : key, sgList, count, name.isBlank() ? null : name, userData);
                        System.out.println("Created instances: " + String.join(", ", ids));
                        break;

                    case "list":
                        ec2.describeInstances();
                        break;

                    case "start":
                        System.out.print("Instance IDs (comma separated): ");
                        List<String> startIds = Arrays.stream(scanner.nextLine().split(",")).map(String::trim).collect(Collectors.toList());
                        ec2.startInstances(startIds);
                        System.out.println("Start requested.");
                        break;

                    case "stop":
                        System.out.print("Instance IDs (comma separated): ");
                        List<String> stopIds = Arrays.stream(scanner.nextLine().split(",")).map(String::trim).collect(Collectors.toList());
                        ec2.stopInstances(stopIds);
                        System.out.println("Stop requested.");
                        break;

                    case "terminate":
                        System.out.print("Instance IDs (comma separated): ");
                        List<String> termIds = Arrays.stream(scanner.nextLine().split(",")).map(String::trim).collect(Collectors.toList());
                        ec2.terminateInstances(termIds);
                        System.out.println("Terminate requested.");
                        break;

                    case "help":
                        System.out.println("Commands:\n  create — cria instância(s)\n  list — lista instâncias\n  start — inicia instância(s)\n  stop — para instância(s)\n  terminate — finaliza instância(s)\n  exit — sai");
                        break;

                    case "exit":
                        break loop;

                    default:
                        System.out.println("Comando não reconhecido. Digite 'help' para ver opções.");
                }
            }
        }
    }
}