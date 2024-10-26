package client;

import partage.Bot;
import partage.Callback;
import partage.Task;
import tachesmono.*;
import tachesmulti.*;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Callback callback = new CallbackImpl();

            Bot bagOfTasks = (Bot) Naming.lookup("BOT");
            System.out.println("Connecté à BagOfTasks.");

            Scanner scanner = new Scanner(System.in);
            boolean validClient = false;
            int account = 0;
            boolean verifApp = false;

            while (!validClient) {
                System.out.print("Entrez votre nom pour l'identification: ");
                String clientName = scanner.nextLine();
                
                Task task = new CheckClient(clientName, callback);
                bagOfTasks.sendTask(task);
                System.out.println("Tâche envoyée au serveur. En attente de validation...");

                Thread.sleep(2000);

                if(((CallbackImpl) callback).isValidClient()) {
                    validClient = true;
                    System.out.println("Identification avec succès !");
                    System.out.print("Veuillez sélectionner le numéro de compte à lequel vous souhaitez acceder : ");
                    account = scanner.nextInt();
                } else {
                    System.out.println("Nom de client non valide, veuillez réésayer.");
                }
            }

            while (!verifApp) {
                System.out.println("--Choisir l'opération que vous souhaitez effectuer pour le compte numéro : " + account);
                System.out.println("1-> depot.");
                System.out.println("2-> retrait");
                System.out.println("3-> afficher solde");
                System.out.println("4-> afficher historique des operations.");
                System.out.println("5-> demander une nouvelle carte bancaire");
                System.out.println("6-> recharger une carte bancaire");
                System.out.println("7-> consulter le solde d'une carte bancaire");
                System.out.println("8-> souscrire à une assurance");
                System.out.println("9-> afficher mes assurances");
                System.out.println("10-> quitter.");
                
                System.out.print("Votre choix : ");
                int operation = scanner.nextInt();
                scanner.nextLine();

                Task task = null;

                switch (operation) {
                    case 1: {
                        System.out.print("Entrez le montant à déposer : ");
                        double montant = scanner.nextDouble();
                        scanner.nextLine();
                        task = new DepositTask(account, montant, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        break;
                    }
                    case 2: {
                        System.out.print("Entrez le montant à retirer : ");
                        double montant = scanner.nextDouble();
                        scanner.nextLine();
                        task = new WithdrawTask(account, montant, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        break;
                    }
                    case 3: {
                        task = new CheckBalanceTask(account, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        break;
                    }
                    case 4: {
                        task = new TransacHistoryTask(account, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        break;
                    }
                    case 5:{
                        System.out.print("1-> Visa | 2-> MatserCard => Veuillez selectionner un type : ");
                        int type = scanner.nextInt();
                        scanner.nextLine();
                        task = new CreateCard(account, type, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        break;
                    }
                    case 6: {
                        ((CallbackImpl) callback).setTechnicalChoice(1);
                        task = new GetCards(account, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        if(((CallbackImpl) callback).isHasCard()) {
                            System.out.print("Veuillez sélectionner l'id de votre carte: ");
                            ((CallbackImpl) callback).setHasCard();
                            int idCard = scanner.nextInt();
                            scanner.nextLine();
                            System.out.print("Veuillez sélectionner le montant: ");
                            double amount = scanner.nextDouble();
                            scanner.nextLine();
                            task = new RechargeCard(account, idCard, amount, callback);
                            bagOfTasks.sendTask(task);
                            Thread.sleep(2000);
                            ((CallbackImpl) callback).setTechnicalChoice(0);
                        } else {
                            System.out.println("ce compte n'est pas associé à aucune carte bancaire!");;
                        }
                        break;
                    }
                    case 7: {
                        ((CallbackImpl) callback).setTechnicalChoice(1);
                        task = new GetCards(account, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        if(((CallbackImpl) callback).isHasCard()) {
                            System.out.print("Veuillez sélectionner l'id de votre carte: ");
                            ((CallbackImpl) callback).setHasCard();
                            int idCard = scanner.nextInt();
                            scanner.nextLine();
                            task = new CardBalance(idCard, callback);
                            bagOfTasks.sendTask(task);
                            Thread.sleep(2000);
                            ((CallbackImpl) callback).setTechnicalChoice(0);
                        } else {
                            System.out.println("ce compte n'est pas associé à aucune carte bancaire!");;
                        }
                        break;
                    }
                    case 8: {
                        ((CallbackImpl) callback).setTechnicalChoice(1);
                        task = new GetInsurance(callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        System.out.print("Veuillez sélectionner une assurance: ");
                        int ins = scanner.nextInt();
                        scanner.nextLine();
                        task = new InsSubscribe(account, ins, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        ((CallbackImpl) callback).setTechnicalChoice(0);
                        break;
                    }
                    case 9: {
                        ((CallbackImpl) callback).setTechnicalChoice(2);
                        task = new ClientInsurances(account, callback);
                        bagOfTasks.sendTask(task);
                        Thread.sleep(2000);
                        ((CallbackImpl) callback).setTechnicalChoice(0);
                        break;
                    }
                    case 10: {
                        System.exit(0);
                    }
                }
            }
            
            scanner.close();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}