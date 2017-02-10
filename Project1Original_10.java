import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class Project1Original_10{
      /*Your project is to use the grammar definition in the appendix
      of your text to guide the construction of a lexical analyzer.
      The lexical analyzer should return tokens as described. Keep
      in mind these tokens will serve as the input to the parser.
      You must enhance the definitions by adding a keyword "float"
      as a data type to the material on page 493 and beyond.
      Specifically, rule 5 on page 492 should state

          type-specifier -> int | void | float

      and any other modifications necessary must be included.

      Page 491 and 492 should be used to guide the construction of the
      lexical analyzer. A few notable features:
      0) the project's general goal is to construct a list of tokens capable
         of being passed to a parser.
      1) comments should be totally ignored, not passed to the parser and
         not reported.
      2) comments might be nested.
      3) one line comments are designated by //
      4) multiple line comments are designated by /* followed by */
      /*  in a match up fashion for the nesting.
      5) a symbol table* for identifiers should be constructed (as
         per recommendation of your text, I actually recommend
         construction of the symbol table during parsing).
         a) the symbol table should keep track of the identifier
         b) be extensible
         c) keep track of scope
         d) be constructed efficiently
         * this will not be evaluated until project 3
      6) upon reporting of identifiers, their nesting depth/declarations
         should be displayed.

      Appropriate documentation as described in the Syllabus should
      be included. A shar file, including all files necessary,
      (makefile, source files, test files, documentation file
      ("text" in ascii format), and any other files) should be submitted
      by the deadline using turnin as follows:

         turnin fn ree4620_1

      By my typing    make    after unsharing your file, I should see an
      executable called p1, if you wrote your program in C,  that will
      perform the lexical analysis.

      The analyzer will be invoked with:

         p1 test_fn

      where p1 is the executable resulting from the make command and
      test_fn is the test filename upon which lexical analysis is to be
      done. You must supply a makefile for any language you chose to use,
      including scripting languages.

      If you write in other languages, you must supply at p1 file
      that will execute your program.
      For example, such a p1 file might appear as:

      #!/bin/ksh
      ruby your_ruby_script $1

      OR

      #!/bin/ksh
      java your_java_pgm $1

      OR

      #!/bin/ksh
      python your_python_script $1

      Note that turnin will report the 2 day late date, if the project
      is submitted on this date the penalty will be assessed.

      The shar file can be created as follows:

      shar fn1 fn2 fn3 fn4 > fn

      You should NOT shar a directory, i.e. when I unshar your project
      a new subdirectory should not be created.

      You should test the integrity of your shar by: 1)copying it to a
      temporary directory, 2)unsharing, 3)make, and 4)execute to see that
      all files are present and that the project works appropriately.

      Failure to carefully follow these guidelines will result in penalty.
      If you are not sure of some characteristic, ask to verify the
      desired procedure.

      You should echo the input line followed by the output in a
      sequential fashion.

      Note: you may have an additional project assigned before this one is
      due.

      Note: this example does not print the required symbol table, nor does
      it demonstrate nesting.

      Follow the general guidelines in the Syllabus for project construction
      and grading. */

      // global variables
      // stores the current index of the location in the array
      static int currentIndex = 0;
      // a boolean variable that determines that something is a number instead of a id
      static boolean isNum = false;

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
  //    System.out.println("HALLO");
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
          for(Project1Original_10.currentIndex = 0; Project1Original_10.currentIndex < newArray.length; Project1Original_10.currentIndex++){
            if ((Project1Original_10.currentIndex + 1)< newArray.length){
               if (String.valueOf(newArray, Project1Original_10.currentIndex, 2).equals("/*")){
                  commentCounter++;
                 Project1Original_10.currentIndex++;
               }else if (String.valueOf(newArray, Project1Original_10.currentIndex, 2).equals("*/")){
                  if(commentCounter > 0){
                    commentCounter--;
                  }else if(commentCounter == 0){
                    compare = compare + newArray[Project1Original_10.currentIndex];
                  }
                  Project1Original_10.currentIndex++;
               }else if (String.valueOf(newArray, Project1Original_10.currentIndex, 2).equals("//")){
                 break;
               }
            }
            if(commentCounter == 0){
              if(!String.valueOf(newArray[Project1Original_10.currentIndex]).equals("\t")){
                compare = compare + newArray[Project1Original_10.currentIndex];
              }
      //        System.out.println("String: " + compare);
              if(keywords.contains(compare)){
                System.out.println("KEYWORD: " + compare);
                compare = "";
              }else if(relational.contains(compare)){
                if((Project1Original_10.currentIndex+1) < newArray.length){
                  if(relational.contains(String.valueOf(newArray, Project1Original_10.currentIndex, 2))){
                    compare = compare + newArray[Project1Original_10.currentIndex+1];
                    System.out.println("RELATIONAL: " + compare);
                    compare = "";
                  }else{
                    if(!(relational.contains(String.valueOf(newArray[Project1Original_10.currentIndex-1])))){
                      System.out.println("RELATIONAL: " + compare);
                      compare = "";
                    }
                  }
                  compare = "";
                }
              }else if(errors.contains(compare)){
                String newError = "";
                for(int z = Project1Original_10.currentIndex; z < newArray.length; z++){
                  if(!(String.valueOf(newArray[z])).equals(" ")){
                    newError = newError + newArray[z];
                  }else{
                    Project1Original_10.currentIndex = z;
                    break;
                  }
                }
                System.out.println("ERROR: " + newError);
                newError= "";
                compare = "";
              }else if((isInteger(compare) == true) || (compare.equals(".")) || (compare.equals("E"))){
                String newNum = "";
                Project1Original_10.isNum = true;
                for(int x = Project1Original_10.currentIndex; x < newArray.length; x++){
                  if((newArray[x] != ' ') && (!(numDelims.contains(String.valueOf(newArray[x]))))){
                    newNum = newNum + newArray[x];
  //                  System.out.println("hello" + newNum);
                  }else{
                    Project1Original_10.currentIndex = x;
                    checkNum(newNum, errors);
                    compare = "";
                    Project1Original_10.isNum = false;
                    break;
                  }
                  if(x == (newArray.length - 1)){
                    checkNum(newNum, errors);
                  }
                  Project1Original_10.currentIndex = x;
                }
              }else if(delims.contains(compare)){
                System.out.println("DELIMS: " + compare);
                compare = "";
              }else if((String.valueOf(newArray[Project1Original_10.currentIndex]).equals(" ")) || (delims.contains(String.valueOf(newArray[Project1Original_10.currentIndex]))) ||
                    (relational.contains(String.valueOf(newArray[Project1Original_10.currentIndex])))){
                String newID = "";
                String testString = "";
                String beforeTest = "";
                if((!(keywords.contains(compare)))&&(!(relational.contains(compare)))&&(!(delims.contains(compare)))
                      &&(!(errors.contains(compare)))&&(!(compare).equals(" "))&&(isNum == false)){
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
                  }
                }
                compare = "";
              }else if(Project1Original_10.currentIndex == (newArray.length - 1)){
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
                          Project1Original_10.currentIndex = j;
                          break;
                        }
                      }
                      System.out.println("ERROR: " + newError);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      System.out.println("RELATIONAL: " + String.valueOf(c));
                    }
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
