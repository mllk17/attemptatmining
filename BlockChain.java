import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;

public class BlockChain {

    private static ArrayList<Block> Chain;

    public BlockChain() throws Exception{
        Chain = new <Block>ArrayList();
        BufferedReader reader = new BufferedReader(new FileReader("meledger.txt"));
        String theLine = reader.readLine();

        while(theLine != null){
            String a = "";
            int line = 0;
            String previousHash = "";
            String data = "";
            String nonce = "";
            int b = 0;

            for(int x = 0; x < theLine.length(); x++){
                if(theLine.substring(x, x+1).equals(",")){
                    if(b == 0){
                        line = Integer.parseInt(a);
                        a = "";
                        b++;
                        x++;
                    }
                    else if(b == 1){
                        previousHash = a;
                        a = "";
                        b++;
                        x++;
                    }
                    else{ // b=2
                        data = a;
                        a = "";
                        x = x + 2;
                        nonce = theLine.substring(x);
                        Chain.add(new Block(line, previousHash, data, nonce));
                    }
                }
                else{
                    a = a + theLine.substring(x, x+1);
                }
            }
            theLine = reader.readLine();
        }
        reader.close();
    }

    public static void addBlock() throws Exception {
        int temp = 0;
        int lastLine = Chain.size()-1;
        System.out.println("What data would you like to add?");
        Scanner sc1 = new Scanner(System.in);
        String input = sc1.nextLine();

        String theHashed = "";


        if(Chain.size() > 0) {
            String lastHashed = hashIt(Chain.get(lastLine).getInfo());
            int lastLineNum = Chain.get(lastLine).getl();

            int n = 0;
            String snonce = Integer.toString(n);
            String stuff = (lastLineNum + 1) + lastHashed + input + snonce;
            theHashed = hashIt(stuff);

            while(temp == 0) {
                if (theHashed.substring(0, 1).equals("1")) {
                    Chain.add(new Block(lastLineNum + 1, lastHashed, input, snonce));
                    writeInFile(Integer.toString(lastLineNum+1) + ", " + lastHashed + ", " + input + ", " + snonce);
                    temp = 1;
                } else{
                    n++;
                    temp = 0;
                    snonce = Integer.toString(n);
                    stuff = lastHashed + input + snonce;
                    theHashed = hashIt(stuff);
                }

            }
        }
        else{
            int n2 = 0;
            String snonce2 = Integer.toString(n2);
            String stuff2 = "00" + input + snonce2;
            theHashed = hashIt(stuff2);
            System.out.println("The default difficulty is set to 1.");

            while(temp == 0) {
                if (theHashed.substring(0, 1).equals("1")) {
                    Chain.add(new Block(0, "0", input, snonce2));
                    writeInFile("0, 0, " + input + ", " + snonce2);
                    temp = 1;
                } else {
                    n2++;
                    snonce2 = Integer.toString(n2);
                    stuff2 = "0" + input + snonce2;
                    theHashed = hashIt(stuff2);
                    temp = 0;
                }
            }

        }
    }

    public static Block getBlock(int theBlock){
        return Chain.get(theBlock);
    }

    public static int getSize(){
        return Chain.size();
    }

    public static boolean verifyChain() throws Exception{
        String hashed = "";
        String hashed2 = "";
        String one = "";
        String two = "";
        String hold = "";
        /*while(theLine != null) {

            for(int y = 0; y < theLine.length(); y++){
                if(theLine.substring(y, y+1).equals(",")){
                    hold = hold + one;
                    y++;
                }
                else{
                    one = one + theLine.substring(y, y+1);
                }
            }

        }*/
        for (int x = 0; x < Chain.size() - 1; x++) {
            hashed = hashIt(Chain.get(x).getInfo());
            hashed2 = Chain.get(x + 1).getpH();
            if (!(hashed.equals(hashed2))) {
                return false;
            }
        }
        return true;

    }

    public static int failedAt() throws Exception{
        String hashed = "";
        String hashed2 = "";
        for(int x = 0; x < Chain.size()-1; x++){
            hashed = hashIt(Chain.get(x).getInfo());
            hashed2 = Chain.get(x+1).getpH();
            if(!(hashed.equals(hashed2))){
                return x+1;
            }
        }
        return 0;
    }

    public static void remine(int whichBlock) throws Exception{
        String first = "";
        BufferedReader reader = new BufferedReader(new FileReader("meledger.txt"));
        String theLine = reader.readLine();

        first = hashIt(Chain.get(whichBlock-1).getInfo());
        int a = Chain.get(whichBlock).getl();
        String c = Chain.get(whichBlock).getd();
        int d = Chain.get(whichBlock).getn();
        Chain.set(whichBlock, new Block(a, first, c, Integer.toString(d)));

        int atLine = 0;
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("temp.txt"));
        String correct = "";

        while(theLine != null){
            if(atLine + 1 == whichBlock){
                int one = theLine.indexOf(",");
                int two = theLine.indexOf(",", one+1);
                int three = theLine.indexOf(",", two+1);
                String ph = theLine.substring(one + 2, two);
                String data = theLine.substring(two + 2, three);
                correct = hashIt(atLine + ph + data + theLine.substring(three + 2));
                writer2.write(theLine);
                writer2.newLine();
            }
            else if(atLine != whichBlock){
                writer2.write(theLine);
                writer2.newLine();
            }
            else{
                int one = theLine.indexOf(",");
                int two = theLine.indexOf(",", one + 1);
                String before = theLine.substring(0, one);
                String after = theLine.substring(two);
                writer2.write(before + ", " + correct + after);
                writer2.newLine();
            }
            atLine++;
            theLine = reader.readLine();
        }

        reader.close();
        writer2.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter("meledger.txt", false));
        BufferedReader reader2 = new BufferedReader(new FileReader("temp.txt"));
        String info = reader2.readLine();

        while(info != null){
            writer.write(info);
            writer.newLine();
            info = reader2.readLine();
        }

        writer.close();
        reader2.close();
        BufferedWriter writer2a = new BufferedWriter(new FileWriter("temp.txt", false));
        writer2a.close();
    }

    public static String retrieveInfo(int index){
        Block a = getBlock(index);
        String the = "The line number is " + a.getl() +
                "\nThe previous hash is " + a.getpH() +
                "\nThe data is " + a.getd() +
                "\nThe nonce is " + a.getn();
        return the;
    }

    public static void remineWD(int newDiff) throws Exception{
        String diff = "";
        for(int x = 0; x < newDiff; x ++){
            diff = diff + "1";
        }
        System.out.println(diff);
        String stuff;
        int nonce = 0;
        String hashed;
        int linenum;
        String ph;
        String data;
        BufferedWriter writer = new BufferedWriter(new FileWriter("meledger.txt", false));

        for(int a = 0; a < Chain.size(); a++){
            linenum = Chain.get(a).getl();
            ph = Chain.get(a).getpH();
            data = Chain.get(a).getd();
            int stand = 0;

            while(stand == 0) {
                stuff = Integer.toString(linenum) + ph + data + nonce;
                hashed = hashIt(stuff);

                if (hashed.substring(0, newDiff).equals(diff)) {
                    Chain.set(a, new Block(linenum, ph, data, Integer.toString(nonce)));
                    writer.write(Integer.toString(linenum) + ", " + ph + ", " + data + ", " + Integer.toString(nonce));
                    writer.newLine();
                    stand = 1;
                }

                nonce++;
            }
        }

        writer.close();
    }

    public static void deleteBlock() throws Exception{
        System.out.println("Which block would you like to delete? " +
                "Please input a valid number from 0 to " +
                Integer.toString(Chain.size()-1) + ".");
        Scanner sc5 = new Scanner(System.in);
        int whichBlock = -1;

        while(whichBlock == -1){
            try {
                whichBlock = sc5.nextInt();
            }
            catch(Exception e){
                System.out.println("Please enter a valid input.");
            }
            if(whichBlock > Chain.size()-1){
                System.out.println("Please enter a valid input.");
                whichBlock = -1;
            }
        }
        Chain.remove(whichBlock);

        BufferedReader reader = new BufferedReader(new FileReader("meledger.txt"));
        String theLine = reader.readLine();
        BufferedWriter writer2 = new BufferedWriter(new FileWriter("temp.txt", false));

        while(theLine != null){
            if(!(theLine.substring(0, theLine.indexOf(",")).equals(Integer.toString(whichBlock)))){
                writer2.write(theLine);
                writer2.newLine();
            }
            else{
                break;
            }
            theLine = reader.readLine();
        }

        reader.close();
        writer2.close();
        BufferedWriter writer = new BufferedWriter(new FileWriter("meledger.txt", false));
        BufferedReader reader2 = new BufferedReader(new FileReader("temp.txt"));
        String line = reader2.readLine();

        while(line != null){
            writer.write(line);
            writer.newLine();
            line = reader2.readLine();
        }

        writer.close();
        reader2.close();
        BufferedWriter writer2a = new BufferedWriter(new FileWriter("temp.txt", false));
        writer2a.close();

    }

    public static void writeInFile(String putIn) throws Exception{
        BufferedWriter writer = new BufferedWriter(new FileWriter("meledger.txt", true));
        writer.write(putIn);
        writer.newLine();
        writer.close();
    }

    public static String hashIt(String words) throws Exception{
        return toHexString(getSHA(words));
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
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