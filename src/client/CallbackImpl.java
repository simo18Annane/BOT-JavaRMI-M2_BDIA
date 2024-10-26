package client;

import partage.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class CallbackImpl extends UnicastRemoteObject implements Callback {
    private boolean validClient = false;
    private boolean hasCard = false;
    private int technicalChoice = 0; 

    protected CallbackImpl() throws RemoteException {
        super();
    }

    //pour définir quel resultat à afficher au client
    public void setTechnicalChoice(int choice) {
        this.technicalChoice = choice;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResult(Object result) throws RemoteException {
        if (result instanceof List<?>) {
            List<?> resultList = (List<?>) result;

            if(!resultList.isEmpty() && resultList.get(0) instanceof Integer) {
                if (technicalChoice == 0) {
                    List<Integer> accounts = (List<Integer>) resultList;
                    System.out.println("Comptes associés à l'utilisateur :");
                    for (Integer id : accounts) {
                        System.out.println("Compte ID: " + id);
                    }
                    validClient = true;
                } else if(technicalChoice == 1) {
                    List<Integer> creditCards = (List<Integer>) resultList;
                    System.out.println("Cartes bancaires associées à ce compte :");
                    for (Integer id : creditCards) {
                        System.out.println("Card ID: " + id);
                    }
                    hasCard = true;
                }
                
            } else if(!resultList.isEmpty() && resultList.get(0) instanceof String) {
                if (technicalChoice == 0) {
                    List<String> history = (List<String>) resultList;
                    System.out.println("Historique des opérations : ");
                    for (String op : history) {
                        System.out.println(op);
                    }
                } else if (technicalChoice == 1) {
                    List<String> insurance = (List<String>) resultList;
                    System.out.println("Les assurances proposées par la banque : ");
                    for (String ins : insurance) {
                        System.out.println(ins);
                    }
                } else if (technicalChoice == 2) {
                    List<String> insurancecl = (List<String>) resultList;
                    System.out.println("Mes assurances : ");
                    for (String ins : insurancecl) {
                        System.out.println(ins);
                    }
                }
                
            } else if (resultList.isEmpty()) {
                if (technicalChoice == 0) {
                    System.out.println("l'utilisateur n'est pas à un client à la banque");
                    validClient = false;
                } else if (technicalChoice == 2) {
                    System.out.println("Aucune assurance!");
                }
                
            }
            
            
        } else if (result instanceof Boolean) {
            boolean opSuccess = (boolean) result;
            if(opSuccess) {
                System.out.println("L'opération a été avec succès !");
            } else {
                System.out.println("L'opération a échoué.");
            }
        } else if (result instanceof Double) {
            Double balance = (Double) result;
            if(technicalChoice == 0) {
                System.out.println("Votre solde actuel est : " + balance);
            } else if (technicalChoice == 1) {
                System.out.println("le solde de votre carte bancaire est : " + balance);
            }
            
        } else {
            System.out.println("Erreur: résultat inattendu.");
            validClient = false;
        }
    }

    //checker la validation du client
    public boolean isValidClient() {
        return validClient;
    }

    //checker que le client a une carte
    public boolean isHasCard() {
        return hasCard;
    }

    public void setHasCard() {
        this.hasCard = !hasCard;
    }
}