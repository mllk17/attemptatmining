import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.lang.*;

public class mainone {

    //private static int nonce;

    public static void main(String args[]) throws Exception{
        BlockChain ledger = new BlockChain();

        int onceAgain = 0;
        while(onceAgain == 0) {
            menu();
            Scanner sc = new Scanner(System.in);
            int whatToDo = 0;

            while (whatToDo == 0) {
                try {
                    whatToDo = sc.nextInt();
                } catch (Exception e) {
                    sc.next();
                    System.out.println("Please enter a valid input");
                    whatToDo = 0;
                }
                if (whatToDo > 5) {
                    System.out.println("Please enter a valid input2");
                    whatToDo = 0;
                }
            }

            //while loop with incorrect menu
            boolean lop = true; //works

            while (lop) {
                switch (whatToDo) {
                    case 1: //create block and add to chain
                        ledger.addBlock();
                        lop = false;
                        break;

                    case 2: //verify chain
                        int at = 0;
                        if (ledger.verifyChain()) { //is valid
                            System.out.println("All good.");
                            lop = false;
                            break;
                        } else {
                            at = ledger.failedAt();
                            System.out.println("The chain is invalid. " +
                                    "There is an error at Block " + at +
                                    ". \nWould you like to re-mine the blockchain?");
                            String ans = "def";
                            String lower = "";

                            while (ans.equals("def")) {
                                try {
                                    ans = sc.nextLine();
                                } catch (Exception e) {
                                    System.out.println("Please answer yes or no.");
                                }
                                lower = ans.toLowerCase();
                                if (lower.equals("yes")) {
                                    ledger.remine(at);
                                    int holder = 0;
                                    while(holder == 0) {
                                        if (ledger.verifyChain()) {
                                            System.out.println("The blockchain has been remined!");
                                            holder = 1;
                                        } else {
                                            at = ledger.failedAt();
                                            ledger.remine(at);
                                        }
                                    }
                                    lop = false;
                                    ans = "out";
                                    break;
                                }
                                else if(lower.equals("no")){
                                    lop = false;
                                    ans = "out";
                                    break;
                                }
                                else{
                                    ans = "def";
                                }
                            }
                        }
                        break;

                    case 3: //retrieve info from specific block in chain
                        if(ledger.getSize() > 0) {
                            System.out.println("What block would you like to retrieve info from?" +
                                    "\nYou may choose from Block 0 to Block " + Integer.toString(ledger.getSize() - 1) + ".");
                        }
                        else{
                            System.out.println("What block would you like to retrieve info from? You only have Block 0 to choose.");
                        }
                        int theBlock = -1;

                        while (theBlock == -1) {
                            try {
                                theBlock = sc.nextInt();
                            } catch (Exception e) {
                                sc.next();
                                System.out.println("Please enter a valid input.");
                            }
                            if (theBlock > ledger.getSize() - 1) {
                                System.out.println("Please enter a valid input.");
                                theBlock = -1;
                            }
                        }

                        System.out.println(ledger.retrieveInfo(theBlock));
                        lop = false;
                        break;

                    case 4: //adjust difficulty
                        int theDiff = setDiff();
                        System.out.println("Your difficulty is " + theDiff);
                        ledger.remineWD(theDiff);
                        lop = false;
                        break;

                    case 5: //delete block
                        ledger.deleteBlock();
                        if(!ledger.verifyChain()){
                            ledger.remine(ledger.failedAt());
                        }
                        lop = false;
                        break;

                }
            }

            if(ask()){ //true = yes would like to go back to the menu
                onceAgain = 0;
            }
            else{
                onceAgain = 1;
            }
        }
    }

    public static void menu(){
        System.out.println("Welcome peeps. What would you like to do?" +
                "\n1. Create a block and add it to the chain." +
                "\n2. Verify the integrity of the chain." +
                "\n3. Retrieve info from a specific block in the chain." +
                "\n4. Adjust the chain's difficulty" +
                "\n5. Delete a block." +
                "\nPlease type a number.");
    }

    public static boolean ask(){
        System.out.println("\nThe mission has been completed." +
                "\nWould you like to go back to the main menu?" +
                "\nType 'yes' or 'no.'");
        Scanner sc =  new Scanner(System.in);
        String input = "";
        int temp = 0;
        String another = "";

        while(temp == 0){
            try{
                input = sc.nextLine();
            }
            catch (Exception e){
                System.out.println("Please type 'yes' or 'no.'");
            }
            another = input.toLowerCase();

            if(another.equals("yes")){
                return true;
            }
            else if(another.equals("no")){
                return false;
            }
            else{
                temp = 0;
            }
        }

        return false;

    }

    public static int setDiff(){//how to store difficulty?
        System.out.println("What would you like to set the difficulty to?" +
                "\nChoose a number from 1 to 5.");
        Scanner sc = new Scanner(System.in);
        int input = 0;

        while(input == 0){
            try{
                input = sc.nextInt();
            }
            catch(Exception e){
                System.out.println("Please print a valid input.");
            }
            if(input > 5){
                System.out.println("Please print a valid input.");
                input = 0;
            }
        }

        return input;
    }

    public static String hashIt(String words) throws Exception{
        return toHexString(getSHA(words));
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}