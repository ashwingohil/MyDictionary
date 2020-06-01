
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class MyDictionary{

    public static final String RESET = "\033[0m";  // Text Reset
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    String username;
    private int[] userdictionaryRowNo = new int[26];
    private int[] userderivativesRowNo = new int[26];
    private int[] usernovelsRowNo = new int[26];
    private ArrayList<ArrayList<String>>[] userdictionary = new ArrayList[26];
    private ArrayList<ArrayList<String>>[] userderivatives = new ArrayList[26];
    private ArrayList<ArrayList<String>>[] usernovels = new ArrayList[26];
    private ArrayList<String> WordList = new ArrayList<>();

    private ArrayList<ArrayList<String>> currentWordMeaning = new ArrayList<ArrayList<String>>();
    private ArrayList<String> currentDerivates = new ArrayList<>();
    private ArrayList<ArrayList<String>> currentNovels = new ArrayList<ArrayList<String>>();

    static ArrayList<MyDictionary> objects = new ArrayList<>();
    static ArrayList<String> users = new ArrayList<>();
    static ArrayList<String> userhash = new ArrayList<>();
    static int usersize;
    static boolean loadedUsersState;

    public static void Blue_Bold_Bright(String message) {
        System.out.print(BLUE_BOLD_BRIGHT + message + RESET);
    }

    MyDictionary(String username){
        //Constructor. Initializing Lists and loading Wordinfo from directory
        this.username = username;
        //initializing Lists
        for(int i=0; i < 26; i++){
            userdictionary[i] = new ArrayList<ArrayList<String>>();
            userderivatives[i] = new ArrayList<ArrayList<String>>();
            usernovels[i] = new ArrayList<ArrayList<String>>();
        }
        loadWordList(this.username);
        loadWordsInfoFromDirectory(this.username);
    }

    public static void writeInitFiles(){
        String passwordlist_file = "PasswordList.txt";
        String hintanswer_file = "HintAnswer.txt";
        String userlist_file = "UserList.txt";

        File file1 = new File(passwordlist_file);
        File file2 = new File(hintanswer_file);
        File file3 = new File(userlist_file);

        try {
            if (!file1.exists()) {
                file1.createNewFile();
            }
            if (!file2.exists()) {
                file2.createNewFile();
            }
            if (!file3.exists()) {
                file3.createNewFile();
            }
        }catch (IOException ex){
            System.out.println("Program cannot proceed further. Necessary files cannot be created.");
        }
    }

    private void loadWordsInfoFromDirectory(String username){
        //Load info from file and push to userdictionary and userderivatives
        String wordlistfile = "WordList_"+username;
        ArrayList<String> filelist = new ArrayList<>();
        String[] returnedFiles;
        String directory = System.getProperty("user.dir");
        directory = directory+"/"+username;
        File f = new File(directory);
        returnedFiles = f.list();

        for(String filename: returnedFiles){
            //System.out.println(filename);
            if(!filename.equals(wordlistfile)){
                filelist.add(filename);
            }
        }

        //System.out.println(filelist);
        for(int i=0; i < filelist.size(); i++){
            loadWordMeaningFromFile(setFile(username,filelist.get(i)));
            pushWordToUserDictionary();
            pushDerivativesToUserDerivatives();
            pushNovelsToUserNovels();
        }

    }

    private int searchFromUserDictionary(String word){

        int arrayno;
        arrayno = findArrayNumber(word.charAt(0));

        for(int i=0; i<userdictionary[arrayno].size(); i++){
            if(userdictionary[arrayno].get(i).contains(word)){
                return i;
            }
        }
        return -1;
    }

    private int searchFromUserDerivatives(String word){
        int arrayno;
        arrayno = findArrayNumber(word.charAt(0));
        for(int i=0; i < userderivatives[arrayno].size(); i++){
            if(userderivatives[arrayno].get(i).contains(word)){
                return i;
            }
        }
        return -1;
    }

    private int searchFromUserNovels(String word){
        int arrayno;
        arrayno = findArrayNumber(word.charAt(0));
        for(int i=0; i < usernovels[arrayno].size(); i++){
            if(usernovels[arrayno].get(i).get(0).contains(word)){
                return i;
            }
        }
        return -1;
    }

    private String getRootWordFromUserDerivatives(String word){
        int arrayno;
        arrayno = findArrayNumber(word.charAt(0));

        for(int i=0; i<26; i++){
            for(int j=0; j < userderivativesRowNo[i]; j++){
                if(userderivatives[i].get(j).contains(word)){
                    return userderivatives[i].get(j).get(0);
                }
            }
        }

        return "";
    }

    private void fetchWordMeaning(int rowno, String wordToSearch){
        int arrayno;
        arrayno = findArrayNumber(wordToSearch.charAt(0));
        //System.out.print("Word: ");
        MyDictionary.Blue_Bold_Bright("Word:    ");
        for(int i=0; i<userdictionary[arrayno].get(rowno).size(); i++) {
            if(i == 0) {
                System.out.print(userdictionary[arrayno].get(rowno).get(i));
            }
            else if( i > 0){
                System.out.println();
                System.out.print("  ::");
                System.out.print(userdictionary[arrayno].get(rowno).get(i));
            }
        }
        System.out.println();
    }

    private void fetchWordDerivatives(int rowno, String wordToSearch){
        int arrayno;
        arrayno = findArrayNumber(wordToSearch.charAt(0));
        //System.out.println("Derivatives: ");
        MyDictionary.Blue_Bold_Bright("Derivatives:    ");
        System.out.println();
        for(int i=0; i<userderivatives[arrayno].get(rowno).size(); i++){
            System.out.println(userderivatives[arrayno].get(rowno).get(i));
        }
    }

    private void fetchWordNovels(int rowno, String wordToSearch){

        int arrayno;
        arrayno = findArrayNumber(wordToSearch.charAt(0));
        //System.out.println("Novels: ");
        MyDictionary.Blue_Bold_Bright("Novels:    ");
        System.out.println();
        for(int i=rowno; i < usernovels[arrayno].size(); i++){
            if(usernovels[arrayno].get(i).get(0).contains(wordToSearch)){
                for(int j=1; j<4; j++){
                    System.out.print(usernovels[arrayno].get(i).get(j));
                    System.out.print("  ");
                }
                System.out.println();
            }
            if(!usernovels[arrayno].get(i).get(0).contains(wordToSearch)) break;
        }
    }

    private String getUser(){
        return username;
    }

    public static int checkUser(String username){
        //returns index of the object in the list
        for(int i=0; i<objects.size(); i++){
            if(objects.get(i).username.equals(username)){
                return i;
            }
        }
        return -1;
    }

    public static void loadUsers(){
        try{

            FileReader inFile = new FileReader("UserList.txt");
            BufferedReader inStream = new BufferedReader(inFile);
            String inString;
            while((inString = inStream.readLine()) != null){
                users.add(inString.substring(0,inString.indexOf(':')));
            }
            usersize = users.size();

        }catch(FileNotFoundException ex) {
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void loadHash(){
        try{
            FileReader inFile = new FileReader("UserList.txt");
            BufferedReader inStream = new BufferedReader(inFile);
            String inString;
            while((inString = inStream.readLine()) != null){
                userhash.add(inString.substring(inString.indexOf(":")+2,inString.length()));
            }
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void appendUserToList(String username){
        users.add(username);
        usersize = users.size();
    }

    public static void writeToUserList(String username, String hash){
        try {
            FileWriter outFile = new FileWriter("UserList.txt", true);
            PrintWriter writer = new PrintWriter(outFile);
            writer.print(username);
            writer.print("::");
            writer.print(hash);
            writer.println();
            writer.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static boolean validateUserExists(String username){
        for(int i=0; i < users.size(); i++){
            if(users.get(i).equals(username)){
                return true;
            }
        }
        return false;
    }

    public static void writeHintAnswer(String question, String answer){
        try {
            FileWriter outFile = new FileWriter("HintAnswer.txt", true);
            PrintWriter writer = new PrintWriter(outFile);
            writer.print(question);
            writer.print("::");
            writer.print(answer);
            writer.println();
            writer.close(); //is imp
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void writePassword(String password){
        try{
            FileWriter outFile = new FileWriter("PasswordList.txt", true);
            PrintWriter writer = new PrintWriter(outFile);
            writer.print(password);
            writer.println();
            writer.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static String readPassword(String username){

        int linenumber = 0;
        int linecounter = 1;
        String password = null;

        if(MyDictionary.loadedUsersState == false){
            MyDictionary.loadUsers();
            MyDictionary.loadedUsersState = true;
        }
        for(int i=0; i<users.size(); i++){
            if(users.get(i).equals(username)){
                linenumber = i+1;
                try{
                    FileReader inFile = new FileReader("PasswordList.txt");
                    BufferedReader inStream = new BufferedReader(inFile);
                    while((password=inStream.readLine()) != null) {
                        if (linecounter == linenumber) {
                            return password;
                        }
                        linecounter++;
                    }
                }catch(FileNotFoundException ex){
                    ex.printStackTrace();
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                break;
            }
        }
        return password;
    }

    public static String fetchSecurityLine(String username){

        int linenumber = 0;
        int linecounter = 1;
        String hintline = null;

        if(MyDictionary.loadedUsersState == false){
            MyDictionary.loadUsers();
            MyDictionary.loadedUsersState = true;
        }
        for(int i=0; i<users.size(); i++){
            if(users.get(i).equals(username)){
                linenumber = i+1;
                try{
                    FileReader inFile = new FileReader("HintAnswer.txt");
                    BufferedReader inStream = new BufferedReader(inFile);
                    while((hintline=inStream.readLine()) != null) {
                        if (linecounter == linenumber) {
                            return hintline;
                        }
                        linecounter++;
                    }
                }catch(FileNotFoundException ex){
                    ex.printStackTrace();
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                break;
            }
        }
        return hintline;
    }

    public static void showBanner(){
        System.out.println("\tGreetings");
        System.out.println("a. Register");
        System.out.println("b. Login");
        System.out.println("c. Forgot Password");
        System.out.println("e. Exit");
    }

    public static void showDictionaryBanner(String currentusername){
        System.out.println("Welcome, "+currentusername);
        System.out.println("1. Write Word-Meaning");
        System.out.println("2. Search Word");
        System.out.println("3. Add");
        System.out.println("4. Edit");
        System.out.println("5. Delete");
        System.out.println("0. Log out");
    }

    public static String toHash(String password){
        StringBuilder hashstring = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (byte b : hashInBytes) {
                hashstring.append(String.format("%02x", b));
            }
        }catch(NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
        return hashstring.toString();
    }

    private boolean loadWordMeaningFromUser(String username){
        Scanner input = new Scanner(System.in);
        String inputstr=null;
        String datainput;

        currentWordMeaning.clear();
        currentDerivates.clear();
        currentNovels.clear();

        do{
            System.out.print("Write Word: ");
            datainput = input.nextLine();
            while(datainput.equals("")){
                if (datainput.equals("")) System.out.println("Word cannot be empty");
                System.out.print("Write Word: ");
                datainput = input.nextLine();
            }
            System.out.println("Press e to rewrite or enter to proceed");
            inputstr = input.nextLine();

            while(!inputstr.equals("e") && !inputstr.equals("")){
                System.out.println("Invalid Input");
                inputstr = input.nextLine();
                if(inputstr.equals("e")) break;
                if(inputstr.equals("")) break;
            }

        }while(inputstr.equals("e"));
        currentWordMeaning.add(new ArrayList<String>());
        currentWordMeaning.get(0).add(0,datainput); //word pushed

        int meaningcounter = 1;
        do{
            do{
                System.out.print("Write Meaning"+meaningcounter+" : ");
                datainput = input.nextLine();
                while(datainput.equals("")){
                    if (datainput.equals("")) System.out.println("Meaning cannot be empty");
                    System.out.print("Write Meaning"+meaningcounter+" : ");
                    datainput = input.nextLine();
                }
                System.out.println("Press e to rewrite or enter to proceed");
                inputstr = input.nextLine();

                if(inputstr.equals("")){
                    currentWordMeaning.get(0).add(datainput);
                    break;
                }
            }while(inputstr.equals("e"));

            meaningcounter++;
            System.out.println("Write more meanings? y/n");
            inputstr = input.nextLine();
            while(!inputstr.equals("y") && !inputstr.equals("n")) {
                System.out.println("Invalid input");
                inputstr = input.nextLine();
                if(inputstr.equals("n")) break;
                if(inputstr.equals("y")) break;
            }
        }while(inputstr.equals("y"));



        //DERIVATIVE SECTION
        int derivativecounter=1;
        System.out.print("Want to write Derivates of the word? y/n ");
        inputstr = input.nextLine();
        while(!inputstr.equals("y") && !inputstr.equals("n")) {
            System.out.println("Invalid input");
            inputstr = input.nextLine();
            if (inputstr.equals("n") || inputstr.equals("y")) break;
        }

        if(inputstr.equals("y")){
            System.out.println("Press 0 to skip writing derivates");
            do{
                System.out.print("Write derivate"+derivativecounter+" : ");
                datainput = input.nextLine();
                while(!datainput.equals("0")) { //tricky
                    while (datainput.equals("")) {
                        if (datainput.equals("")) System.out.println("Derivative cannot be empty");
                        System.out.print("Write Derivative" + derivativecounter + " : ");
                        datainput = input.nextLine();
                        if (datainput.equals("0")) break;

                    }
                    if (datainput.equals("0")) break;
                    currentDerivates.add(datainput);

                    derivativecounter++;
                    System.out.println("Write more derivatives? y/n");
                    inputstr = input.nextLine();
                    while (!inputstr.equals("y") && !inputstr.equals("n")) {
                        System.out.println("Invalid input");
                        inputstr = input.nextLine();
                        if (inputstr.equals("n")) break;
                        if (inputstr.equals("y")) break;
                    }
                    if (inputstr.equals("n")) break;
                    if (inputstr.equals("y")) break;
                }
                if(datainput.equals("0")) break;
                if(inputstr.equals("n")) break;
            }while(inputstr.equals("y"));
        }


        //NOVEL SECTION
        int novelcounter=1;
        String novelname;
        String authorname;
        String pageno;
        System.out.print("Want to write Novel information for the word? y/n");
        inputstr =  input.nextLine();
        while(!inputstr.equals("y") && !inputstr.equals("n")) {
            System.out.println("Invalid input");
            inputstr = input.nextLine();
            if (inputstr.equals("n") || inputstr.equals("y")) break;
        }
        if(inputstr.equals("y")){
            System.out.println("Press 0 to skip writing novels");
            do{
                System.out.println("Write Novel"+novelcounter+" : ");
                System.out.print("Novel Name : ");
                datainput = input.nextLine();
                while(!datainput.equals("0")) {
                    while (datainput.equals("")) {
                        if (datainput.equals("")) System.out.println("Novel name cannot be empty");
                        System.out.print("Novel Name: ");
                        datainput = input.nextLine();
                        if (datainput.equals("0")) break;
                    }
                    if (datainput.equals("0")) break;
                    novelname = datainput;

                    System.out.print("Author Name: ");
                    datainput = input.nextLine();
                    while (datainput.equals("")) {
                        if (datainput.equals("")) System.out.println("Author name cannot be empty");
                        System.out.print("Author Name: ");
                        datainput = input.nextLine();
                        if (datainput.equals("0")) break;
                    }
                    if (datainput.equals("0")) break;
                    authorname = datainput;

                    System.out.print("Page number: ");
                    datainput = input.nextLine();
                    while (datainput.equals("")) {
                        if (datainput.equals("")) System.out.println("Page number cannot be empty");
                        System.out.print("Page number: ");
                        datainput = input.nextLine();
                        if (datainput.equals("0")) break;
                    }
                    if (datainput.equals("0")) break;
                    pageno = datainput;

                    if(!novelname.isEmpty() && !authorname.isEmpty() && !pageno.isEmpty()){
                        currentNovels.add(new ArrayList<String>());
                        currentNovels.get(novelcounter-1).add(novelname);
                        currentNovels.get(novelcounter-1).add(authorname);
                        currentNovels.get(novelcounter-1).add(pageno);
                    }

                    novelcounter++;
                    System.out.println("Write more novel information? y/n");
                    inputstr = input.nextLine();
                    while (!inputstr.equals("y") && !inputstr.equals("n")) {
                        System.out.println("Invalid input");
                        inputstr = input.nextLine();
                        if (inputstr.equals("n") || inputstr.equals("y")) break;
                    }
                    if(inputstr.equals("n") || inputstr.equals("y")) break;
                }
                if(datainput.equals("0")) break;

            }while(inputstr.equals("y"));
        }

        if(!currentWordMeaning.get(0).get(0).isEmpty() && !currentWordMeaning.get(0).get(1).isEmpty()) return true;
        else return false;

        //input.close(); //if used it caused exception error closes scanner  in switch cases dictionary menu
    }

    public static void createUserDirectory(String username){
        try {
            String progpath = System.getProperty("user.dir");
            progpath = progpath + "/" + username;
            File myfile = new File(progpath);
            boolean value = myfile.mkdir();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private File setFile(String username, String word){

        String filename = word;
        String progpath = System.getProperty("user.dir");
        String dirname = username;
        progpath = progpath+"/"+dirname;

        File file = new File(progpath,filename);

        return file;
    }

    public static boolean createWordListFile(String username){

        String filename = "WordList_"+username;
        String progpath = System.getProperty("user.dir");
        String dirname = username;
        progpath = progpath+"/"+dirname;

        File file = new File(progpath,filename);

        try{
            FileWriter outFile = new FileWriter(file);
            outFile.close();
            return true;
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return false;
    }

    private boolean updateWordListFile(String username, String word){
        String filename = "WordList_"+username;
        String progpath = System.getProperty("user.dir");
        String dirname = username;
        progpath = progpath+"/"+dirname;

        File file = new File(progpath,filename);

        try{
            FileWriter outFile = new FileWriter(file,true);
            PrintWriter writer = new PrintWriter(outFile);
            writer.print(word);
            writer.println();

            writer.close();
            outFile.close();

            return true;

        }catch(IOException ex){
            ex.printStackTrace();
        }

        return false;
    }

    private boolean wordExistsInWordList(String word){

        if(WordList.isEmpty()) return false;
        else if(!WordList.isEmpty()){
            return WordList.contains(word);
        }
        return false;
    }

    private boolean loadWordList(String username){

        String filename = "WordList_"+username;
        String progpath = System.getProperty("user.dir");
        String dirname = username;
        progpath = progpath+"/"+dirname;

        File file = new File(progpath,filename);

        try{
            FileReader inFile = new FileReader(file);
            BufferedReader inStream = new BufferedReader(inFile);
            String inString;
            while((inString = inStream.readLine()) != null){
                WordList.add(inString);
            }

            inFile.close();
            inStream.close();

            return true;
        }catch(IOException ex){
            ex.printStackTrace();
        }

        return false;
    }

    private void writeWordMeaningToFile(File file){
        try{
            FileWriter outFile = new FileWriter(file);
            PrintWriter writer = new PrintWriter(outFile);
            for(int i=0; i < currentWordMeaning.get(0).size(); i++){
                if(i==0){
                    writer.print(currentWordMeaning.get(0).get(0));
                    writer.println();
                }
                else {
                    writer.print("::");
                    writer.print(currentWordMeaning.get(0).get(i));
                    writer.println();
                }
            }
            for(int i=0; i<currentDerivates.size(); i++){
                writer.print(">");
                writer.print(currentDerivates.get(i));
                writer.println();
            }

            for(int i=0; i<currentNovels.size(); i++){
                for(int j=0; j < currentNovels.get(i).size(); j++) {
                    writer.print("=");
                    writer.print(currentNovels.get(i).get(j));
                }
                writer.println();
            }

            writer.close();
            outFile.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private String getWord(){
        return currentWordMeaning.get(0).get(0);
    }

    private void loadWordMeaningFromFile(File file){
        currentWordMeaning.clear();
        currentDerivates.clear();
        currentNovels.clear();

        int linecounter=1;
        int novelcounter = 0;
        try{
            FileReader inFile = new FileReader(file);
            BufferedReader inStream = new BufferedReader(inFile);
            String inString;
            while((inString = inStream.readLine()) != null){

                if(linecounter==1){
                    currentWordMeaning.add(new ArrayList<String>());
                    currentWordMeaning.get(0).add(inString);
                }
                if(inString.contains("::")){
                   currentWordMeaning.get(0).add(inString.substring(inString.indexOf(":")+2, inString.length()));
                }
                if(inString.contains(">")){
                   currentDerivates.add(inString.substring(inString.indexOf(">")+1, inString.length()));
                }
                if(inString.contains("=")){
                    currentNovels.add(new ArrayList<String>());
                    StringBuilder filler = new StringBuilder();
                    for(int i=0; i < inString.length(); i++){
                        if(inString.charAt(i) == '=') i++;
                        while(inString.charAt(i) != '=' && i < inString.length()-1) {
                            filler = filler.append(inString.charAt(i));
                            i++;
                        }
                        if(i == inString.length()-1) filler = filler.append(inString.charAt(i));
                        currentNovels.get(novelcounter).add(filler.toString());
                        filler.delete(0,filler.length());
                    }
                    novelcounter++;
                }
                linecounter++;
            }

        }catch(IOException ex) {
            ex.printStackTrace();
        }

    }

    private void displayCurrentWordData(){
        if(!currentWordMeaning.isEmpty()){
            System.out.println(currentWordMeaning.get(0).get(0));
            for(int i=1; i<currentWordMeaning.get(0).size(); i++){

                System.out.println(currentWordMeaning.get(0).get(i));
            }
        }
        if(!currentDerivates.isEmpty()){
            System.out.println("Derivatives:");
            for(int i=0; i<currentDerivates.size(); i++){
                System.out.println(currentDerivates.get(i));
            }
        }
        if(!currentNovels.isEmpty()){
            System.out.println("Novels:");
            for(int i=0; i<currentNovels.size(); i++){
                for(int j=0; j<currentNovels.get(i).size(); j++){
                    System.out.print("::");
                    System.out.print(currentNovels.get(i).get(j));
                }
                System.out.println();
            }
        }
    }

    private int findArrayNumber(char letter){
        int x;
        if('a'<= letter && letter <='z'){
            if('a'== 97){
                x = (int)(letter*1-97);
                return x;
            }
        }
        else return -1;
        return -1;
    }

    private boolean pushWordToUserDictionary(){
        String word;
        int arrayno;
        if(!currentWordMeaning.isEmpty()){
            word = currentWordMeaning.get(0).get(0);
            arrayno = findArrayNumber(word.charAt(0));
            userdictionary[arrayno].add(new ArrayList<String>());
            userdictionaryRowNo[arrayno] = userdictionaryRowNo[arrayno] + 1;
            for(int i=0; i < currentWordMeaning.get(0).size(); i++) {
                userdictionary[arrayno].get(userdictionaryRowNo[arrayno] - 1).add(currentWordMeaning.get(0).get(i));
            }
            return true;
        }

        return false;
    }

    private boolean pushDerivativesToUserDerivatives(){
        String word;
        int arrayno;
        if(!currentDerivates.isEmpty()){
            word = currentWordMeaning.get(0).get(0);
            arrayno = findArrayNumber(word.charAt(0));
            userderivatives[arrayno].add(new ArrayList<String>());
            userderivativesRowNo[arrayno] = userderivativesRowNo[arrayno] + 1;
            userderivatives[arrayno].get(userderivativesRowNo[arrayno]-1).add(0,word);
            for(int i=0; i < currentDerivates.size(); i++){
                userderivatives[arrayno].get(userderivativesRowNo[arrayno]-1).add(currentDerivates.get(i));
            }
            return true;
        }
        return false;
    }

    private boolean pushNovelsToUserNovels(){
        String word;
        int arrayno;
        if(!currentNovels.isEmpty()){
            word = currentWordMeaning.get(0).get(0);
            arrayno = findArrayNumber(word.charAt(0));
            for(int i=0; i<currentNovels.size(); i++){
                usernovels[arrayno].add(new ArrayList<String>());
                usernovelsRowNo[arrayno] = usernovelsRowNo[arrayno]+1;
                usernovels[arrayno].get(usernovelsRowNo[arrayno] - 1).add(0,word);
                for(int j=0; j<3; j++) {
                    usernovels[arrayno].get(usernovelsRowNo[arrayno] - 1).add(currentNovels.get(i).get(j));
                }
            }
            return true;
        }
        return false;
    }


    public static void main(String[] args){

        MyDictionary.writeInitFiles();
        String currentusername = null;
        boolean loginsuccess = false;
        Scanner userinput = new Scanner(System.in);
        String inputstr;

        for(;;){

            MyDictionary.showBanner();
            inputstr = userinput.nextLine();
            if(inputstr.equals("a") || inputstr.equals("b") || inputstr.equals("c") || inputstr.equals("e")){
                switch(inputstr){
                    case"a":
                        String username;
                        String password;
                        System.out.println();
                        System.out.println("Welcome to Register");
                        System.out.print("Enter desired username : ");
                        username = userinput.nextLine();
                        if(loadedUsersState == false){
                            MyDictionary.loadUsers();
                            MyDictionary.loadedUsersState = true;
                        }
                        if(MyDictionary.validateUserExists(username) == true){
                            System.out.println("User already exists.");
                        }
                        else{
                            System.out.print("Enter password: ");
                            try {
                                Console cnsl = System.console();
                                if(cnsl != null) {
                                    char pwd[] = cnsl.readPassword();
                                    MyDictionary.writePassword(new String(pwd));
                                    MyDictionary.writeToUserList(username, MyDictionary.toHash(new String(pwd)));
                                }
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        MyDictionary.appendUserToList(username);
                        MyDictionary.loadedUsersState = true;
                        System.out.print("Enter Hint question to retrieve password : ");
                        String hintquestion = userinput.nextLine();
                        System.out.print("Enter hint answer: ");
                        String hintanswer = userinput.nextLine();
                        MyDictionary.writeHintAnswer(hintquestion,hintanswer);
                        System.out.println("Username ["+username+"] registered successfully.");
                        MyDictionary.createUserDirectory(username);
                        MyDictionary.createWordListFile(username);

                        break;

                    case "b":
                        System.out.println("Welcome to Login");
                        System.out.print("Username: ");
                        username = userinput.nextLine(); //username already declared in case a
                        if(loadedUsersState == false){
                            MyDictionary.loadUsers();
                            MyDictionary.loadedUsersState = true;
                        }
                        for(int i=0; i<usersize; i++){
                            if(username.equals(users.get(i))){
                                System.out.print("Password: ");
                                try {
                                    Console cnsl = System.console();
                                    if(cnsl != null) {
                                        char pwd[] = cnsl.readPassword();
                                        password = new String(pwd);
                                        MyDictionary.loadHash();
                                        if((MyDictionary.toHash(password)).equals(userhash.get(i))){
                                            System.out.println("Login successful");
                                            loginsuccess = true;
                                            currentusername = username;
                                        }
                                        else{
                                            System.out.println("Password incorrect");
                                        }
                                    }
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                                break;
                            }
                        }

                        break;

                    case "c":

                        String securityline;
                        String question;
                        String answer;
                        System.out.println("Welcome to Forgot Password");
                        System.out.print("Enter username: ");
                        username = userinput.nextLine();
                        securityline = MyDictionary.fetchSecurityLine(username);
                        question = securityline.substring(0,securityline.indexOf(":"));
                        answer = securityline.substring(securityline.indexOf(":")+2, securityline.length());
                        System.out.println(question);
                        System.out.print("Answer: ");
                        if((userinput.nextLine()).equals(answer)) {

                            System.out.print("Your password is: ");
                            System.out.print(MyDictionary.readPassword(username));
                            System.out.println();
                        }
                        else System.out.println("Incorrect hint answer");

                        break;

                    case "e":
                        break;

                    default:
                        break;
                }
            }
            else{
                System.out.println("Invalid input");
            }
            if(inputstr.equals("e")) break;

            if(inputstr.equals("b") && (loginsuccess == true)){
                int objectindex = -1;
                objectindex = MyDictionary.checkUser(currentusername);
                if(objectindex == -1){
                    objects.add(new MyDictionary(currentusername));
                    objectindex = objectindex + 1;
                    System.out.println("Object created");
                }
                loginsuccess = false;
                for(;;){
                    MyDictionary.showDictionaryBanner(currentusername);
                    inputstr=userinput.nextLine();
                    if(inputstr.equals("0") || inputstr.equals("1") || inputstr.equals("2") || inputstr.equals("3") || inputstr.equals("4") || inputstr.equals("5")){

                        switch (inputstr) {
                            case "1":
                                String wordToWrite;
                                System.out.print("Enter the word to write meaning for: ");
                                wordToWrite = userinput.nextLine();
                                if(objects.get(objectindex).wordExistsInWordList(wordToWrite)){
                                    System.out.println("Word already exists. Edit the word from menu");
                                }
                                else {
                                    System.out.println("You can continue writing details about the word");
                                    //System.out.println(objects.get(objectindex).getUser());
                                    if (objects.get(objectindex).loadWordMeaningFromUser(currentusername)) {
                                        objects.get(objectindex).writeWordMeaningToFile(objects.get(objectindex).setFile(currentusername, objects.get(objectindex).getWord()));
                                        objects.get(objectindex).updateWordListFile(currentusername, objects.get(objectindex).currentWordMeaning.get(0).get(0));
                                        objects.get(objectindex).pushWordToUserDictionary();
                                        objects.get(objectindex).pushDerivativesToUserDerivatives();
                                        objects.get(objectindex).pushNovelsToUserNovels();
                                    }
                                }

                                break;

                            case "2":
                                String wordToSearch;
                                String searchingOption;
                                String rootword;
                                int RowNo_userdictionary = -1;
                                int RowNo_userderivatives = -1;
                                int RowNo_usernovels = -1;
                                //int arrayno;
                                do {
                                    System.out.print("Enter the word to search for the meaning: ");
                                    wordToSearch = userinput.nextLine();
                                    RowNo_userdictionary = objects.get(objectindex).searchFromUserDictionary(wordToSearch);
                                    if (RowNo_userdictionary != -1){
                                        objects.get(objectindex).fetchWordMeaning(RowNo_userdictionary,wordToSearch);
                                        rootword = objects.get(objectindex).getRootWordFromUserDerivatives(wordToSearch);
                                        RowNo_usernovels = objects.get(objectindex).searchFromUserNovels(wordToSearch);
                                        if(!rootword.equals("")){
                                            RowNo_userderivatives = objects.get(objectindex).searchFromUserDerivatives(wordToSearch);
                                            if(RowNo_userderivatives != -1){
                                                objects.get(objectindex).fetchWordDerivatives(RowNo_userderivatives,wordToSearch);
                                            }
                                        }
                                        if(RowNo_usernovels != -1){
                                            objects.get(objectindex).fetchWordNovels(RowNo_usernovels,wordToSearch);
                                        }
                                    }
                                    else if(RowNo_userdictionary == -1) {
                                        System.out.println("Root word: "+objects.get(objectindex).getRootWordFromUserDerivatives(wordToSearch));
                                        rootword = objects.get(objectindex).getRootWordFromUserDerivatives(wordToSearch);
                                        if(!rootword.equals("")){
                                            RowNo_userdictionary = objects.get(objectindex).searchFromUserDictionary(rootword);
                                            RowNo_userderivatives = objects.get(objectindex).searchFromUserDerivatives(rootword);
                                            RowNo_usernovels = objects.get(objectindex).searchFromUserNovels(rootword);
                                            if(RowNo_userdictionary != -1) {
                                                objects.get(objectindex).fetchWordMeaning(RowNo_userdictionary, rootword);
                                                if(RowNo_userderivatives != -1){
                                                    objects.get(objectindex).fetchWordDerivatives(RowNo_userderivatives,rootword);
                                                }
                                                if(RowNo_usernovels != -1){
                                                    objects.get(objectindex).fetchWordNovels(RowNo_usernovels,rootword);
                                                }
                                            }
                                            else System.out.println("Word not in dictionary");
                                        }
                                        else System.out.println("Word not in dictionary");
                                    }

                                    do{
                                        System.out.print("Search for more words? y/n ");
                                        searchingOption = userinput.nextLine();
                                        if(searchingOption.equals("y") || searchingOption.equals("n")) break;
                                        System.out.println("Invalid Input");
                                    }while(!searchingOption.equals("y") || !searchingOption.equals("n"));

                                    if(searchingOption.equals("n")) break;

                                }while(searchingOption.equals("y"));

                                break;

                            case "3":
                                break;

                            case "4":
                                break;
                                
                            case "5":
                                break;

                            case "0":
                                break;

                            default:
                                break;
                        }
                    }
                    else{
                        System.out.println("Invalid Input");
                    }

                    if(inputstr.equals("0")){
                        break;
                    }
                }
            }
        }
        userinput.close();

    }
}

