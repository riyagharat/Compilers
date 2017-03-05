import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class Project2Original_8{

/*Project 2 Compiler ---- Due 3/2/17 Thursday at 11:59 PM (nearly midnight)

Your project is to use the grammar definition in the appendix
of your text to guide the construction of a recursive descent parser.
The parser should follow the grammar as described in A.2 page 492.

You should enhance the grammar to include FLOAT as
appropriate throughout all the grammar rules.

Upon execution, your project should report

ACCEPT

or

REJECT

exactly. Failure to print ACCEPT or REJECT appropriately will
result penalty for the test file.

Appropriate documentation as described in the Syllabus should
be included. A shar file, including all files necessary,
(makefile, source files, test files, documentation file
(p2.txt in ascii format), and any other files) should be submitted
by the deadline using turnin as follows:

   turnin fn ree4620_2

By my typing    make    after unsharing your file I should see an
executable called p2 (if you did your project in C) that will
perform the syntax analysis. The analyzer will be invoked with:

   p2 test_fn

where p2 is the executable resulting from the make command
(if done in C or C++) or is a script that executes your project (if
done in anyother language) and test_fn is the test filename upon
which parsing is to be done. You must supply a makefile for any
language. If your project is written in a pure interpreter (python,
ruby, perl, etc.), provide a makefile and indicate such.
(that is,  print "No makefile necessary" from your makefile).

Note that turnin will report the 2 day late date, if the project
is submitted on this date a penalty will be assessed.

Thus, the makefile might be (as needed for python):

-------------------------------------------------
all:
	@echo "no makefile necessary, project in python"
-------------------------------------------------

the p1 script would then be:

-------------------------------------------------
#!/bin/bash
python myprj.py $1
-------------------------------------------------

The shar file can be created as follows:

shar makefile p1 myprj.py p2.txt  > fn

You should not shar a directory, ie when I unshar your project
a new subdirectory should not be created.

You should test the integrity of your shar by copying it to a
temporar y directory, unsharing, make, and execute to see that
all files are present and that the project works
appropriately.

Note: you may have an additional project assigned before this one is
due.

You must enhance your symbol table in preparation for the semantic
analysis project (Project 3). You do not need to print the table.

You do not need to do error recovery, upon detection of the error,
simply report such and stop the program. */

      // global variables
      // stores the current index of the location in the array
      static int currentIndex = 0;
      // a boolean variable that determines that something is a number instead of a id
      static boolean isNum = false;

      static int depthCount = 0;

      static int currentIndexParse = 0;

      public static void main(String[] args) throws FileNotFoundException{
        // Reads file from command line
        File file = new File(args[0]);

        // ArrayList for the designated keywords in C-
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("float");
        keywords.add("int");
        keywords.add("else");
        keywords.add("if");
        keywords.add("return");
        keywords.add("void");
        keywords.add("while");

        // ArrayList for relational operators in C-
        ArrayList<String> relational = new ArrayList<>();
        relational.add("+");
        relational.add("-");
        relational.add("*");
        relational.add("/");
        relational.add("<");
        relational.add("<=");
        relational.add(">");
        relational.add(">=");
        relational.add("==");
        relational.add("!=");
        relational.add("=");

        // ArrayList for the designated deliminators in C-
        ArrayList<String> delims = new ArrayList<>();
        delims.add(";");
        delims.add(",");
        delims.add("(");
        delims.add(")");
        delims.add("[");
        delims.add("]");
        delims.add("{");
        delims.add("}");
        delims.add(".");

        // ArrayList for the designated deliminators for numbers in C-
        // This was created to help with identifying scientific notation
        ArrayList<String> numDelims = new ArrayList<>();
        numDelims.add(";");
        numDelims.add(",");
        numDelims.add("(");
        numDelims.add(")");
        numDelims.add("[");
        numDelims.add("]");
        numDelims.add("{");
        numDelims.add("}");

        // ArrayList for the designated errors in C-
        ArrayList<String> errors = new ArrayList<>();
        errors.add("!");
        errors.add("@");
        errors.add("_");

        // ArrayList for the generated identifiers in C-
        ArrayList<Identifier> labelStorage = new ArrayList<>();

        ArrayList<Token> tokens = new ArrayList<>();

        // Calls the lexical analyzer method
        lexical(keywords, relational, delims, errors, labelStorage, numDelims, file, tokens);

        syntax(tokens);

        // The number of labels
        int n = labelStorage.size();
    //    System.out.println("NUMBER OF LABELS: " + n);
        // The first prime number greater than 2n
        int p = 0;
        for(int i = (2*n + 1); true; i++){
          if(isPrime(i)){
            p = i;
            break;
          }
        }

        // Construct an array of the symbols
        String [] symbolTable = new String [p];
        for(int i = 0; i < symbolTable.length; i++){
          symbolTable[i] = null;
        }

        for (int i = 0; i <= labelStorage.size()-1; i++){
    //        System.out.println("ARRAY LENGTH: " + p);
            String tempLabel = labelStorage.get(i).getLabel();
            int num = insertLinear(symbolTable, tempLabel, p);
            // Sets the hashVal
            labelStorage.get(i).setHashLocation(num);
        }

    } // end main

    // Method checks is the String passed to it is a number
    public static boolean isInteger(String stringNumber){
      try{
        Integer.parseInt(stringNumber);
        return true;
      }catch(NumberFormatException e){
        return false;
      }
    }

    // Checks to see if the number is prime
    public static boolean isPrime(int n){
      for(int j = 2; (j*j <= n); j++){
        if(n % j == 0){
          return false;
        }
      }
      return true;
    }

    // Hashes a word
    public static int hashFunction(String key, int arraySize){
      int hashVal = 0;
      int letter = 0;

      for(int j = 0; j < key.length(); j++){
        if(key.charAt(j) > 64 && key.charAt(j) < 91){
          letter = key.charAt(j) - 64;
        }else if (key.charAt(j) > 96 && key.charAt(j) < 123){
          letter = key.charAt(j) - 96;
        }
           hashVal = (hashVal * 26 + letter) % arraySize;
      }
      return hashVal;
    }

    // Inserts the hashed word to its appropriate location
    static public int insertLinear(String symbolTable[], String label, int arraySize){
        int hashVal = hashFunction(label, arraySize);

        while(symbolTable[hashVal] != null){
           if(symbolTable[hashVal] == label){
           }
           ++hashVal;
           hashVal %= arraySize;
        }
        symbolTable[hashVal] = label;
        return hashVal;
     }

    // A method that reads in possible numbers and checks to see if they are valid
    // if they are it prints them to the console as either a num or a float
    // otherwise they are marked as an error
    public static void checkNum(String num, ArrayList<String> errors, ArrayList<Token> tokens){
      char [] numArray = num.toCharArray();
      String numToPrint = "";
      String errorNum = "";
      String error = "";
      for(int r = 0; r < numArray.length; r++){
        if((numArray[0] == '.')||(numArray[0] == 'E')){
          errorNum = num;
          break;
        }
        if(isInteger(String.valueOf(numArray[r]))){
          numToPrint = numToPrint + numArray[r];
          if(r+1 < numArray.length){
            if(numArray[r+1] == '.'){
              numToPrint = numToPrint + numArray[r+1];
              r = r + 1;
              if(r+1 < numArray.length){
                if(isInteger(String.valueOf(numArray[r+1]))){
                  numToPrint = numToPrint + numArray[r+1];
                  r = r + 1;
                }else{
                  errorNum = num;
                  break;
                }
              }
            }else if(numArray[r+1] == 'E'){
              numToPrint = numToPrint + numArray[r+1];
              r = r + 1;
              if(r+1 < numArray.length){
                if((numArray[r+1] == '+') || (numArray[r+1] == '-')){
                  numToPrint = numToPrint + numArray[r+1];
                  r = r + 1;
                }else if(isInteger(String.valueOf(numArray[r+1]))){
                  numToPrint = numToPrint + numArray[r+1];
                  r = r + 1;
                }else{
                  errorNum = num;
                  break;
                }
              }else{
                errorNum = num;
                break;
              }
            }else if(isInteger(String.valueOf(numArray[r+1]))){
              numToPrint = numToPrint + numArray[r+1];
              r = r + 1;
            }
          }
        }else if(numArray[r] == '.'){
          numToPrint = numToPrint + numArray[r];
          if(r+1 < numArray.length){
            if(isInteger(String.valueOf(numArray[r+1]))){
              numToPrint = numToPrint + numArray[r+1];
              r = r + 1;
            }else{
              errorNum = num;
              break;
            }
          }else{
            errorNum = num;
            break;
          }
        }else if(numArray[r] == 'E'){
          numToPrint = numToPrint + numArray[r];
          if(r+1 < numArray.length){
            if((numArray[r+1] == '+') || (numArray[r+1] == '-')){
              numToPrint = numToPrint + numArray[r+1];
              r = r + 1;
            }else if(isInteger(String.valueOf(numArray[r+1]))){
              numToPrint = numToPrint + numArray[r+1];
              r = r + 1;
            }
          }else{
            errorNum = num;
            break;
          }
        }
        if(errors.contains(String.valueOf(numArray[r]))){
          for(int c = r; c < numArray.length; c++){
            error = error + numArray[c];
          }
  //        System.out.println("error: " + error);
          break;
        }
      }
      if(errorNum.isEmpty() == true){
        if((num.indexOf('.')) >= 0 || (num.indexOf('E')) >= 0){
          Token data = new Token("NUM", numToPrint);
          tokens.add(data);
          numToPrint = "";
        }else{
          Token data = new Token("NUM", numToPrint);
          tokens.add(data);
          numToPrint = "";
        }
      }else{
    //    System.out.println("ERROR: " + errorNum);
      }
    }

    // The method that generates tokens based on the input file and also prints out its result to the console
    public static void lexical(ArrayList<String> keywords, ArrayList<String> relational, ArrayList<String> delims,
        ArrayList<String> errors, ArrayList<Identifier> labelStorage, ArrayList<String> numDelims, File file, ArrayList<Token> tokens){

      try{
        Scanner input = new Scanner(file);
        int commentCounter = 0;
        while(input.hasNextLine()){
          String text = input.nextLine();
          String compare = "";
          System.out.println("");
          if(text.isEmpty() == false){
      //      System.out.println("INPUT: " + text);
          }
          char [] newArray = text.toCharArray();
          for(Project2Original_8.currentIndex = 0; Project2Original_8.currentIndex < newArray.length; Project2Original_8.currentIndex++){
            if ((Project2Original_8.currentIndex + 1)< newArray.length){
               if (String.valueOf(newArray, Project2Original_8.currentIndex, 2).equals("/*")){
                  commentCounter++;
                 Project2Original_8.currentIndex++;
               }else if (String.valueOf(newArray, Project2Original_8.currentIndex, 2).equals("*/")){
                  if(commentCounter > 0){
                    commentCounter--;
                  }else if(commentCounter == 0){
                  //  compare = compare + newArray[Project2Original_8.currentIndex];
                  }
                  Project2Original_8.currentIndex++;
               }else if (String.valueOf(newArray, Project2Original_8.currentIndex, 2).equals("//")){
                 break;
               }
            }
            if(commentCounter == 0){
              if(!String.valueOf(newArray[Project2Original_8.currentIndex]).equals("\t")){
                compare = compare + newArray[Project2Original_8.currentIndex];
              }
  //            System.out.println("String: " + compare);
              if(keywords.contains(compare)){
                if(Project2Original_8.currentIndex + 1 < newArray.length){
                  if((String.valueOf(newArray[Project2Original_8.currentIndex + 1]).equals(" "))){
                    Token data = new Token("KEYWORD", compare);
                    tokens.add(data);
                    compare = "";
                  }
                }
                if((Project2Original_8.currentIndex == (newArray.length - 1))){
                  Token data = new Token("KEYWORD", compare);
                  tokens.add(data);
                  compare = "";
                }
              }else if(relational.contains(compare)){
                if((Project2Original_8.currentIndex+1) < newArray.length){
                  if(relational.contains(String.valueOf(newArray, Project2Original_8.currentIndex, 2))){
                    compare = compare + newArray[Project2Original_8.currentIndex+1];
                    Token data = new Token("RELATIONAL", compare);
                    tokens.add(data);
                    compare = "";
                  }else{
                    if(!(relational.contains(String.valueOf(newArray[Project2Original_8.currentIndex-1])))){
                      Token data = new Token("RELATIONAL", compare);
                      tokens.add(data);
                      compare = "";
                    }
                  }
                  compare = "";
                }
              }else if(errors.contains(compare)){
                boolean accidentalError = false;
                String newError = "";
                if(compare.equals("!")){
                  if(Project2Original_8.currentIndex + 1 < newArray.length){
                     if((String.valueOf(newArray[Project2Original_8.currentIndex + 1])).equals("=")){
                        Project2Original_8.currentIndex ++;
                        compare = compare + String.valueOf(newArray[Project2Original_8.currentIndex]);
                        Token data = new Token("RELATIONAL", compare);
                        tokens.add(data);
                        compare = "";
                        Project2Original_8.currentIndex ++;
                        accidentalError = true;
                     }
                  }else{
                     newError = "!";
                     accidentalError = false;
                     Project2Original_8.currentIndex ++;
                  }
                }else{
                  for(int z = Project2Original_8.currentIndex; z < newArray.length; z++){
                    if(!(String.valueOf(newArray[z])).equals(" ")){
                      newError = newError + newArray[z];
                    }else{
                      Project2Original_8.currentIndex = z;
                      break;
                    }
                  }
                }
                if(accidentalError == false){
              //    System.out.println("ERROR: " + newError);
                  newError= "";
                  compare = "";
               }

              }else if((isInteger(compare) == true) || (compare.equals(".")) || (compare.equals("E"))){
    //            System.out.println(compare + "hi");
                String newNum = "";
                Project2Original_8.isNum = true;
                for(int x = Project2Original_8.currentIndex; x < newArray.length; x++){
                  if((newArray[x] != ' ') && (!(numDelims.contains(String.valueOf(newArray[x]))))){
                    newNum = newNum + newArray[x];
   //                 System.out.println("hello" + newNum);
                  }else{
                    Project2Original_8.currentIndex = x-1;
                    checkNum(newNum, errors, tokens);
                    compare = "";
                    Project2Original_8.isNum = false;
                    break;
                  }
                  if(x == (newArray.length - 1)){
                    checkNum(newNum, errors, tokens);
                  }
                  Project2Original_8.currentIndex = x;
                }
                Project2Original_8.isNum = false;
              }else if(delims.contains(compare)){
                Token data = new Token("DELIMS", compare);
                tokens.add(data);
                if(compare.equals("{")){
                  Project2Original_8.depthCount++;
                }else if(compare.equals("}")){
                  Project2Original_8.depthCount--;
                }
                compare = "";
              }else if((String.valueOf(newArray[Project2Original_8.currentIndex]).equals(" ")) || (delims.contains(String.valueOf(newArray[Project2Original_8.currentIndex]))) ||
                    (relational.contains(String.valueOf(newArray[Project2Original_8.currentIndex])))){
                String newID = "";
                String testString = "";
                String beforeTest = "";
                Token data = new Token("", "");
                Token data4 = new Token("", "");
                Token data5 = new Token("", "");
                boolean check = false, check2 = false, check3 = false;
                if((!(keywords.contains(compare)))&&(!(relational.contains(compare)))&&(!(delims.contains(compare)))
                      &&(!(errors.contains(compare)))&&(!(compare).equals(" "))&&(Project2Original_8.isNum == false)){
                  for(int k = 0; k < compare.length(); k++){
                    char c = compare.charAt(k);
                    beforeTest = testString;
                    testString = testString + c;
                    if(delims.contains(String.valueOf(c))){
                      data = new Token("DELIMS", String.valueOf(c));
                      check = true;
                      compare = "";
                      newID = beforeTest;
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = compare.substring(k, compare.length());
              //        System.out.println("ERROR: " + newError);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      data4 = new Token("RELATIONAL", String.valueOf(c));
                      check2 = true;
                    }else{
                      newID = newID + c;
                    }
                  }
                  if(newID.isEmpty() == false){
                    if(keywords.contains(newID)){
                      data5 = new Token("KEYWORD", newID);
                      check3 = true;
                    }else{
                      Identifier data3 = new Identifier(newID, Project2Original_8.depthCount, 0);
                      labelStorage.add(data3);
                      Token data2 = new Token("ID", newID);
                      tokens.add(data2);
                 //     System.out.println("SCOPE: " + Project2Original_8.depthCount);
                    }
                  }
                  if (check3 == true){
                    tokens.add(data5);
                  }
                  if (check == true){
                    tokens.add(data);
                  }
                  if (check2 == true){
                    tokens.add(data4);
                  }
                }

                compare = "";
              }else if(Project2Original_8.currentIndex == (newArray.length - 1)){
          //      System.out.println("hello " + compare);
                String newID = "";
                Token data = new Token("", "");
                Token data4 = new Token("", "");
                Token data5 = new Token("", "");
                boolean check = false, check2 = false, check3 = false;
                  for(int p = 0; p < compare.length(); p++){
                    char c = compare.charAt(p);
                    if(delims.contains(String.valueOf(c))){
                      data = new Token("DELIMS", String.valueOf(c));
                      check = true;
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = "";
                      for(int j = p+1; j < newArray.length; j++){
                        if(!(String.valueOf(newArray[j])).equals(" ")){
                          newError = newError + newArray[j];
                        }else{
                          Project2Original_8.currentIndex = j;
                          break;
                        }
                      }
                  //    System.out.println("ERROR: " + newError);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      data4 = new Token("RELATIONAL", String.valueOf(c));
                      check2 = true;
                    }else{
                      newID = newID + c;
                    }
                  }
                  if(newID.isEmpty() == false){
                    if(keywords.contains(newID)){
                      data5 = new Token("KEYWORD", newID);
                      check3 = true;
                    }else{
                      Identifier data3 = new Identifier(newID, Project2Original_8.depthCount, 0);
                      labelStorage.add(data3);
                      Token data2 = new Token("ID", newID);
                      tokens.add(data2);
                 //     System.out.println("SCOPE: " + Project2Original_8.depthCount);
                    }
                  }
                  if(check3 == true){
                    tokens.add(data5);
                  }
                  if(check == true){
                    tokens.add(data);
                  }
                  if(check2 == true){
                    tokens.add(data4);
                  }
              }
            }
        }
      }
        input.close();
      }catch (FileNotFoundException e){
        e.printStackTrace();
      }
    }

    public static void syntax(ArrayList<Token> tokens){
      for (int i = 0; i < tokens.size(); i++){
        System.out.println(tokens.get(i));
      }
      Token EOF = new Token("EOF", "EOF");
      tokens.add(EOF);

      program(tokens);
      System.out.println("ACCEPT");
      System.exit(1);
    }

    public static void program(ArrayList<Token> tokens){
      declarationList(tokens);
      return;
    }

    public static void declarationList(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("EOF"))){
        System.out.println("ACCEPT");
        System.exit(1);
      }else{
        typeSpecifier(tokens);
        C(tokens);
        declarationList2(tokens);
        return;
      }
    }

    public static void declarationList2(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("EOF"))){
        System.out.println("ACCEPT");
        System.exit(1);
      }else{
        if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("int")) ||
        ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("float")) ||
        ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("void"))){
          typeSpecifier(tokens);
          C(tokens);
          declarationList2(tokens);
        }else{
          return;
        }
      }
    }

    public static void C(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("EOF"))){
        System.out.println("ACCEPT");
        System.exit(1);
      }else{
        if((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")){
          Project2Original_8.currentIndexParse++;
          X(tokens);
          return;
        }else{
          System.out.println("ERROR: Expecting ID got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }
    }

    public static void X(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken().equals("("))){
        Project2Original_8.currentIndexParse++;
//        System.out.println("ahi: " + (tokens.get(Project2Original_8.currentIndexParse)));
        params(tokens);
  //      if((tokens.get(Project2Original_8.currentIndexParse++).getToken().equals(")"))){
 //         Project2Original_8.currentIndexParse++;
          compoundStmt(tokens);
          return;
      //  }else{
      //    System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
      //    System.out.println("REJECT");
      //    System.exit(1);
      //  }
      }else{
        Y(tokens);
        return;
      }
    }

    public static void Y(ArrayList<Token> tokens){
//      System.out.println("hai: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
      if((tokens.get(Project2Original_8.currentIndexParse).getToken().equals("["))){
        if((tokens.get(Project2Original_8.currentIndexParse + 1).getTokenType().equals("NUM"))){
          Project2Original_8.currentIndexParse++;
          if((tokens.get(Project2Original_8.currentIndexParse + 1).getToken().equals("]"))){
            Project2Original_8.currentIndexParse++;
            if((tokens.get(Project2Original_8.currentIndexParse + 1).getToken().equals(";"))){
              Project2Original_8.currentIndexParse++;
              Project2Original_8.currentIndexParse++;
              return;
            }else{
              System.out.println("ERROR: Expecting ; got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
              System.out.println("REJECT");
              System.exit(1);
            }
          }else{
            System.out.println("ERROR: Expecting ] got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
            System.out.println("REJECT");
            System.exit(1);
          }
        }else{
          System.out.println("ERROR: Expecting NUM got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else if((tokens.get(Project2Original_8.currentIndexParse).getToken().equals(";"))){
        Project2Original_8.currentIndexParse++;
        return;
      }else{
        System.out.println("ERROR: Expecting ; or [ got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void typeSpecifier(ArrayList<Token> tokens){
      if(!(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("int")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("void")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("float")))){
         System.out.println("ERROR: Expecting int or void or float got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
         System.out.println("REJECT");
         System.exit(1);
       }
       Project2Original_8.currentIndexParse++;
       return;
    }

    public static void params(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("void")) &&
        ((tokens.get(Project2Original_8.currentIndexParse + 1).getToken()).equals(")"))){
          Project2Original_8.currentIndexParse = Project2Original_8.currentIndexParse + 2;
        return;
      }else if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("int")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("void")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("float"))){
          param(tokens);
          paramList2(tokens);
          if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(")")){
            Project2Original_8.currentIndexParse++;
            return;
          }else{
            System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
            System.out.println("REJECT");
            System.exit(1);
          }
      }
    }

    public static void paramList2(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(",")){
        Project2Original_8.currentIndexParse++;
        param(tokens);
        return;
      }else{
        return;
      }
    }

    public static void param(ArrayList<Token> tokens){
    //  Project2Original_8.currentIndexParse++;
      typeSpecifier(tokens);
      if((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")){
        Project2Original_8.currentIndexParse++;
        M(tokens);
        return;
      }else{
        System.out.println("ERROR: Expecting ID got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void M(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("[")){
        if((tokens.get(Project2Original_8.currentIndexParse + 1).getToken()).equals("]")){
          Project2Original_8.currentIndexParse++;
          Project2Original_8.currentIndexParse++;
          return;
        }else{
          System.out.println("ERROR: Expecting ] got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        return;
      }
    }

    public static void compoundStmt(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("{")){
        Project2Original_8.currentIndexParse++;
        localDeclarations2(tokens);
        statementList2(tokens);
        if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("EOF"))){
            System.out.println("ACCEPT");
            System.exit(1);
        }   
        if((tokens.get(Project2Original_8.currentIndexParse++).getToken()).equals("}")){
          if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("EOF"))){
            System.out.println("ACCEPT");
            System.exit(1);
          }else{
            Project2Original_8.currentIndexParse++;
          }
          return;
        }else{
          System.out.println("ERROR: Expecting } got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        System.out.println("ERROR: Expecting { got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void localDeclarations2(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("int")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("float")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("void"))){
        typeSpecifier(tokens);
        if((tokens.get(Project2Original_8.currentIndexParse).getTokenType().equals("ID"))){
          Project2Original_8.currentIndexParse++;
          Y(tokens);
          localDeclarations2(tokens);
          return;
        }else{
          System.out.println("ERROR: Expecting ID got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        return;
      }
    }

    public static void statementList2(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("(")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("if")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("NUM")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(";")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("while")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("return")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("{"))){
        statement(tokens);
        statementList2(tokens);
        return;
      }else{
        return;
      }
    }

    public static void statement(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("("))){
        expressionStmt(tokens);
        return;
      }else if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("{")){
        compoundStmt(tokens);
        return;
      }else if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("if")){
        selectionStmt(tokens);
        return;
      }else if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("while")){
        iterationStmt(tokens);
        return;
      }else if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("return")){
        returnStmt(tokens);
        return;
      }
    }

    public static void expressionStmt(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(";")){
        Project2Original_8.currentIndexParse++;
        return;
      }else{
        expression(tokens);
        if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(";")){
          Project2Original_8.currentIndexParse++;
          return;
        }else{
          System.out.println("ERROR: Expecting ; got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }
    }

    public static void selectionStmt(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("if")){
        if((tokens.get(Project2Original_8.currentIndexParse ++).getToken()).equals("(")){
          Project2Original_8.currentIndexParse++;
          expression(tokens);
          if((tokens.get(Project2Original_8.currentIndexParse ++).getToken()).equals(")")){
            Project2Original_8.currentIndexParse++;
            T(tokens);
            return;
          }else{
            System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
            System.out.println("REJECT");
            System.exit(1);
          }
        }else{
          System.out.println("ERROR: Expecting ( got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        System.out.println("ERROR: Expecting if got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void T(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("else")){
        Project2Original_8.currentIndexParse++;
        statement(tokens);
        return;
      }else{
        statement(tokens);
        return;
      }
    }

    public static void iterationStmt(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("while")){
        if((tokens.get(Project2Original_8.currentIndexParse + 1).getToken()).equals("(")){
          Project2Original_8.currentIndexParse++;
          expression(tokens);
    //      if((tokens.get(Project2Original_8.currentIndexParse ++).getToken()).equals(")")){
    //        Project2Original_8.currentIndexParse++;
            statement(tokens);
            return;
    //      }else{
    //        System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
    //        System.out.println("REJECT");
    //        System.exit(1);
    //      }
        }else{
          System.out.println("ERROR: Expecting ( got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        System.out.println("ERROR: Expecting while got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void returnStmt(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("return")){
        Project2Original_8.currentIndexParse++;
        expressionStmt(tokens);
        return;
      }else{
        System.out.println("ERROR: Expecting return got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void expression(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")){
        Project2Original_8.currentIndexParse++;
        F(tokens);
        return;
      }else if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("(")){
        Project2Original_8.currentIndexParse++;
        expression(tokens);
        if((tokens.get(Project2Original_8.currentIndexParse++).getToken()).equals(")")){
        //  Project2Original_8.currentIndexParse++;
          term2(tokens);
          B(tokens);
          S(tokens);
          return;
        }else{
          System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else if((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("NUM")){
          Project2Original_8.currentIndexParse++;
          term2(tokens);
          B(tokens);
          S(tokens);
          return;
      }else{
        System.out.println("ERROR: Expecting ID, (, or NUM got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void F(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("(")){
        Project2Original_8.currentIndexParse++;
        args(tokens);
        if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(")")){
          Project2Original_8.currentIndexParse++;
          term2(tokens);
          B(tokens);
          S(tokens);
          return;
        }else{
          System.out.println("ERROR: Expecting ( got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        P(tokens);
        G(tokens);
        return;
      }
    }

    public static void G(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("=")){
        Project2Original_8.currentIndexParse++;
        expression(tokens);
        return;
      }else{
        term2(tokens);
        B(tokens);
        S(tokens);
        return;
      }
    }

    public static void P(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("[")){
        Project2Original_8.currentIndexParse++;
        expression(tokens);
        if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("]")){
          Project2Original_8.currentIndexParse++;
          return;
        }else{
          System.out.println("ERROR: Expecting ] got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        return;
      }
    }

    public static void S(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("<=")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("<")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(">")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(">=")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("==")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("!="))){
        relop(tokens);
        factor(tokens);
        term2(tokens);
        B(tokens);
        return;
      }else{
        return;
      }
    }

    public static void relop(ArrayList<Token> tokens){
      if(!(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("<=")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("<")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(">")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(">=")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("==")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("!=")))){
         System.out.println("ERROR: Expecting <= or < or > or >= or == or != got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
         System.out.println("REJECT");
         System.exit(1);
       }
       Project2Original_8.currentIndexParse++;
       return;
    }

    public static void B(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("+")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("-"))){
        addop(tokens);
        factor(tokens);
        term2(tokens);
        B(tokens);
        return;
      }else{
        return;
      }
    }

    public static void addop(ArrayList<Token> tokens){
      if(!(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("+")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("-")))){
         System.out.println("ERROR: Expecting + or - got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
         System.out.println("REJECT");
         System.exit(1);
       }
       Project2Original_8.currentIndexParse++;
       return;
    }

    public static void term2(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("*")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("/"))){
        mulop(tokens);
        factor(tokens);
        term2(tokens);
        return;
      }else{
        return;
      }
    }

    public static void mulop(ArrayList<Token> tokens){
      if(!(((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("*")) ||
       ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("/")))){
         System.out.println("ERROR: Expecting * or / got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
         System.out.println("REJECT");
         System.exit(1);
       }
       Project2Original_8.currentIndexParse++;
       return;
    }

    public static void factor(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("(")){
        Project2Original_8.currentIndexParse++;
        expression(tokens);
        System.out.println("hai: " + (tokens.get(Project2Original_8.currentIndexParse)));
        if((tokens.get(Project2Original_8.currentIndexParse + 1).getToken()).equals(")")){
          Project2Original_8.currentIndexParse++;
          return;
        }else{
          System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else if((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")){
        Project2Original_8.currentIndexParse++;
        E(tokens);
        return;
      }else if((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("NUM")){
        Project2Original_8.currentIndexParse++;
        return;
      }else{
        System.out.println("ERROR: Expecting NUM, ID, or ( got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    public static void E(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("(")){
        Project2Original_8.currentIndexParse++;
        args(tokens);
        if((tokens.get(Project2Original_8.currentIndexParse + 1).getToken().equals(")"))){
          Project2Original_8.currentIndexParse = Project2Original_8.currentIndexParse + 2;
          return;
        }else{
          System.out.println("ERROR: Expecting ) got: " + (tokens.get(Project2Original_8.currentIndexParse).getToken()));
          System.out.println("REJECT");
          System.exit(1);
        }
      }else{
        P(tokens);
        return;
      }
    }

    public static void args(ArrayList<Token> tokens){
      if(((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("ID")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals("(")) ||
      ((tokens.get(Project2Original_8.currentIndexParse).getTokenType()).equals("NUM"))){
        expression(tokens);
        argsList2(tokens);
        return;
      }else{
        return;
      }
    }

    public static void argsList2(ArrayList<Token> tokens){
      if((tokens.get(Project2Original_8.currentIndexParse).getToken()).equals(",")){
        Project2Original_8.currentIndexParse++;
        expression(tokens);
        argsList2(tokens);
        return;
      }else{
        return;
      }
    }

    static class Identifier{
       // The values on each line
       private String label;
       private int scope;
       private int hashLocation;

       // Identifier constructors
       public Identifier(){
       }

       public Identifier(String label, int scope, int hashLocation){
         this.label = label;
         this.scope = scope;
         this.hashLocation = hashLocation;
       }

       //Methods for the Identifier Class

       // Get Methods for Identifier Class
       public String getLabel(){
         return label;
       }

       public int getScope(){
         return scope;
       }

       public int getHashLocation(){
         return hashLocation;
       }

       // Set Methods for Identifier Class
       public void setLabel(String newLabel){
         this.label = newLabel;
       }

       public void setScope(int newScope){
         this.scope = newScope;
       }

       public void setHashLocation(int newHashLocation){
         this.hashLocation = newHashLocation;
       }

       public boolean equals(Object obj){
          return true;
       }

       public String toString(){
         return getHashLocation() + " " + getLabel() + " " + getScope();
       }
    }

    static class Token{
      // The values on each line
      private String tokenType;
      private String token;

      // Token constructors
      public Token(){
      }

      public Token(String tokenType, String token){
        this.tokenType = tokenType;
        this.token = token;
      }

      //Methods for the Token Class

      // Get Methods for Token Class
      public String getTokenType(){
        return tokenType;
      }

      public String getToken(){
        return token;
      }

      // Set Methods for Token Class
      public void setTokenType(String newTokenType){
        this.tokenType = newTokenType;
      }

      public void setToken(String newToken){
        this.token = newToken;
      }

      public boolean equals(Object obj){
         return true;
      }

      public String toString(){
        return getTokenType() + ": " + getToken();
      }
    }
}
