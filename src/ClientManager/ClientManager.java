package ClientManager;

import AccountService.AccountService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;


public class ClientManager {
    /**
     * Starts all the clients in new threads
     *
     * @param args  is an array of 5 integer values:
     *             1. rCount is number of readers. They call getAmount method. rCount >= 0
     *             2. wCount is number of writers. They call addAmount method. wCount >= 0
     *             3. idListStart is a left bound of acceptable id range (including). idListStart >= 0
     *             4. idListEnd is a right bound of acceptable id range (including). idListEnd >= idListStart
     *             5. activity is a number that characterizes how much the client is active. The smaller it is
     *                  the faster the client is. Activity >= 0
     */
    public static void main(String[] args){
        try{
            //check the arguments
            if(args.length != 5)
                throw new IllegalArgumentException("Illegal argument count");
            int rCount= Integer.parseInt(args[0]);
            int wCount= Integer.parseInt(args[1]);
            Integer idListStart= Integer.parseInt(args[2]);
            Integer idListEnd= Integer.parseInt(args[3]);
            int activity= Integer.parseInt(args[4]);
            if(rCount < 0 || wCount < 0 || idListStart < 0 || idListEnd < idListStart || activity < 0)
                throw new IllegalArgumentException("Illegal argument value");

            Registry registry= LocateRegistry.getRegistry("localhost", 1099);
            AccountService accountService= (AccountService)registry.lookup("myAccountServiceObject");

            //run threads
            //run writers
            int clientId= 0;
            for(int i=0; i< wCount; ++i, ++clientId)
                new Thread(new Client(accountService, ClientType.WRITER, idListStart, idListEnd, activity, clientId)).start();
            //run readers
            for(int i=0; i< rCount; ++i, ++clientId)
                new Thread(new Client(accountService, ClientType.READER, idListStart, idListEnd, activity, clientId)).start();

            System.out.println("ClientManager: all clients have been started");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Client stopped because of an exception");
            System.exit(1);
        }
    }
}

class Client implements Runnable{
    int clientId;
    ClientType clientType;
    Integer idListStart;
    Integer idListEnd;
    int activity;
    AccountService accountService;
    Random rand= new Random();

    public Client(AccountService accountService, ClientType clientType, Integer idListStart, Integer idListEnd, int activity, int clientId){
        this.clientType= clientType;
        this.idListStart= idListStart;
        this.idListEnd= idListEnd;
        this.activity=activity;
        this.accountService= accountService;
        this.clientId= clientId;
    }

    @Override
    public void run() {
        try{
            while(true){//infinite calling method
                Integer currId= idListStart + (rand.nextInt(idListEnd - idListStart + 1));
                if(clientType == ClientType.READER){
                    Long value= accountService.getAmount(currId);
                    System.out.println("Client_" + clientId + " getValue(" + currId + ")= " + value);
                }
                else{
                    accountService.addAmount(currId, 1L);//we will always add 1 just for example
                    System.out.println("Client_" + clientId + " addValue(" + currId + ", 1L)");
                }

                //add delay
                try {
                    if(activity > 0)
                        Thread.sleep(rand.nextInt(activity));
                } catch (InterruptedException e) {
                    System.err.println("Client_" + clientId + " has been interrupted");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("Client_" + clientId + " has been turned off because of an exception");
        }
    }
}

enum ClientType {READER, WRITER}
