import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class Project2Original_3{
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
temporary directory, unsharing, make, and execute to see that
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
        ArrayList<String> symbolTable = new ArrayList<>();

        // Calls the lexical analyzer method
        lexical(keywords, relational, delims, errors, symbolTable, numDelims, file);

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

    // A method that reads in possible numbers and checks to see if they are valid
    // if they are it prints them to the console as either a num or a float
    // otherwise they are marked as an error
    public static void checkNum(String num, ArrayList<String> errors){
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
          System.out.println("error: " + error);
          break;
        }
      }
      if(errorNum.isEmpty() == true){
        if((num.indexOf('.')) >= 0 || (num.indexOf('E')) >= 0){
          System.out.println("FLOAT: " + numToPrint);
          numToPrint = "";
        }else{
          System.out.println("NUM: " + numToPrint);
          numToPrint = "";
        }
      }else{
        System.out.println("ERROR: " + errorNum);
      }
    }

    // The method that generates tokens based on the input file and also prints out its result to the console
    public static void lexical(ArrayList<String> keywords, ArrayList<String> relational, ArrayList<String> delims,
        ArrayList<String> errors, ArrayList<String> symbolTable, ArrayList<String> numDelims, File file){

      try{
        Scanner input = new Scanner(file);
        int commentCounter = 0;
        while(input.hasNextLine()){
          String text = input.nextLine();
          String compare = "";
          System.out.println("");
          if(text.isEmpty() == false){
            System.out.println("INPUT: " + text);
          }
          char [] newArray = text.toCharArray();
          for(Project2Original_3.currentIndex = 0; Project2Original_3.currentIndex < newArray.length; Project2Original_3.currentIndex++){
            if ((Project2Original_3.currentIndex + 1)< newArray.length){
               if (String.valueOf(newArray, Project2Original_3.currentIndex, 2).equals("/*")){
                  commentCounter++;
                 Project2Original_3.currentIndex++;
               }else if (String.valueOf(newArray, Project2Original_3.currentIndex, 2).equals("*/")){
                  if(commentCounter > 0){
                    commentCounter--;
                  }else if(commentCounter == 0){
                    compare = compare + newArray[Project2Original_3.currentIndex];
                  }
                  Project2Original_3.currentIndex++;
               }else if (String.valueOf(newArray, Project2Original_3.currentIndex, 2).equals("//")){
                 break;
               }
            }
            if(commentCounter == 0){
              if(!String.valueOf(newArray[Project2Original_3.currentIndex]).equals("\t")){
                compare = compare + newArray[Project2Original_3.currentIndex];
              }
    //          System.out.println("String: " + compare);
              if(keywords.contains(compare)){
                if(Project2Original_3.currentIndex + 1 < newArray.length){
                  if((String.valueOf(newArray[Project2Original_3.currentIndex + 1]).equals(" "))){
                    System.out.println("KEYWORD: " + compare);
                    compare = "";
                  }
                }
                if((Project2Original_3.currentIndex == (newArray.length - 1))){
                  System.out.println("KEYWORD: " + compare);
                  compare = "";
                }
              }else if(relational.contains(compare)){
                if((Project2Original_3.currentIndex+1) < newArray.length){
                  if(relational.contains(String.valueOf(newArray, Project2Original_3.currentIndex, 2))){
                    compare = compare + newArray[Project2Original_3.currentIndex+1];
                    System.out.println("RELATIONAL: " + compare);
                    compare = "";
                  }else{
                    if(!(relational.contains(String.valueOf(newArray[Project2Original_3.currentIndex-1])))){
                      System.out.println("RELATIONAL: " + compare);
                      compare = "";
                    }
                  }
                  compare = "";
                }
              }else if(errors.contains(compare)){
                boolean accidentalError = false;
                String newError = "";
                if(compare.equals("!")){
                  if(Project2Original_3.currentIndex + 1 < newArray.length){
                     if((String.valueOf(newArray[Project2Original_3.currentIndex + 1])).equals("=")){
                        Project2Original_3.currentIndex ++;
                        compare = compare + String.valueOf(newArray[Project2Original_3.currentIndex]);
                        System.out.println("RELATIONAL: " + compare);
                        compare = "";
                        Project2Original_3.currentIndex ++;
                        accidentalError = true;
                     }
                  }else{
                     newError = "!";
                     accidentalError = false;
                     Project2Original_3.currentIndex ++;
                  }
                }else{
                  for(int z = Project2Original_3.currentIndex; z < newArray.length; z++){
                    if(!(String.valueOf(newArray[z])).equals(" ")){
                      newError = newError + newArray[z];
                    }else{
                      Project2Original_3.currentIndex = z;
                      break;
                    }
                  }
                }
                if(accidentalError == false){
                  System.out.println("ERROR: " + newError);
                  newError= "";
                  compare = "";
               }

              }else if((isInteger(compare) == true) || (compare.equals(".")) || (compare.equals("E"))){
    //            System.out.println(compare + "hi");
                String newNum = "";
                Project2Original_3.isNum = true;
                for(int x = Project2Original_3.currentIndex; x < newArray.length; x++){
                  if((newArray[x] != ' ') && (!(numDelims.contains(String.valueOf(newArray[x]))))){
                    newNum = newNum + newArray[x];
   //                 System.out.println("hello" + newNum);
                  }else{
                    Project2Original_3.currentIndex = x-1;
                    checkNum(newNum, errors);
                    compare = "";
                    Project2Original_3.isNum = false;
                    break;
                  }
                  if(x == (newArray.length - 1)){
                    checkNum(newNum, errors);
                  }
                  Project2Original_3.currentIndex = x;
                }
                Project2Original_3.isNum = false;
              }else if(delims.contains(compare)){
                System.out.println("DELIMS: " + compare);
                if(compare.equals("{")){
                  Project2Original_3.depthCount++;
                }else if(compare.equals("}")){
                  Project2Original_3.depthCount--;
                }
                compare = "";
              }else if((String.valueOf(newArray[Project2Original_3.currentIndex]).equals(" ")) || (delims.contains(String.valueOf(newArray[Project2Original_3.currentIndex]))) ||
                    (relational.contains(String.valueOf(newArray[Project2Original_3.currentIndex])))){
                String newID = "";
                String testString = "";
                String beforeTest = "";
                if((!(keywords.contains(compare)))&&(!(relational.contains(compare)))&&(!(delims.contains(compare)))
                      &&(!(errors.contains(compare)))&&(!(compare).equals(" "))&&(Project2Original_3.isNum == false)){
                  for(int k = 0; k < compare.length(); k++){
                    char c = compare.charAt(k);
                    beforeTest = testString;
                    testString = testString + c;
                    if(delims.contains(String.valueOf(c))){
                      System.out.println("DELIMS: " + String.valueOf(c));
                      newID = beforeTest;
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = compare.substring(k, compare.length());
                      System.out.println("ERROR: " + newError);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      System.out.println("RELATIONAL: " + String.valueOf(c));
                    }else{
                      newID = newID + c;
                    }
                  }
                  if(newID.isEmpty() == false){
                    symbolTable.add(newID);
                    System.out.println("ID: " + newID);
                    System.out.println("SCOPE: " + Project2Original_3.depthCount);
                  }
                }
                compare = "";
              }else if(Project2Original_3.currentIndex == (newArray.length - 1)){
          //      System.out.println("hello " + compare);
                String newID = "";
                  for(int p = 0; p < compare.length(); p++){
                    char c = compare.charAt(p);
                    if(delims.contains(String.valueOf(c))){
                      System.out.println("DELIMS: " + String.valueOf(c));
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = "";
                      for(int j = p+1; j < newArray.length; j++){
                        if(!(String.valueOf(newArray[j])).equals(" ")){
                          newError = newError + newArray[j];
                        }else{
                          Project2Original_3.currentIndex = j;
                          break;
                        }
                      }
                      System.out.println("ERROR: " + newError);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      System.out.println("RELATIONAL: " + String.valueOf(c));
                    }else{
                      newID = newID + c;
                    }
                  }
                  if(newID.isEmpty() == false){
                    symbolTable.add(newID);
                    System.out.println("ID: " + newID);
                    System.out.println("SCOPE: " + Project2Original_3.depthCount);
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
}
