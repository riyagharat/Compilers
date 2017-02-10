import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class Project1Original_8{
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

      public static void main(String[] args) throws FileNotFoundException{
        File file = new File(args[0]);

        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("float");
        keywords.add("int");
        keywords.add("else");
        keywords.add("if");
        keywords.add("return");
        keywords.add("void");
        keywords.add("while");

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

        ArrayList<String> errors = new ArrayList<>();
        errors.add("!");
        errors.add("@");
        errors.add("_");

        ArrayList<String> symbolTable = new ArrayList<>();

        lexical(keywords, relational, delims, errors, symbolTable, file);

    }

    public static boolean isInteger(String stringNumber){
      try{
        Integer.parseInt(stringNumber);
        return true;
      }catch(NumberFormatException e){
        return false;
      }
    }

    public static void lexical(ArrayList<String> keywords, ArrayList<String> relational, ArrayList<String> delims,
        ArrayList<String> errors, ArrayList<String> symbolTable, File file){

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
          for(int i = 0; i < newArray.length; i++){
            if ((i + 1)< newArray.length){
               if (String.valueOf(newArray, i, 2).equals("/*")){
                  commentCounter++;
                 i++;
               }else if (String.valueOf(newArray, i, 2).equals("*/")){
                  if(commentCounter > 0){
                    commentCounter--;
                  }else if(commentCounter == 0){
                    compare = compare + newArray[i];
                  }
                  i++;
               }else if (String.valueOf(newArray, i, 2).equals("//")){
                 break;
               }
            }
            if(commentCounter == 0){
              if(String.valueOf(newArray[i]).equals("\t")){
                compare = compare;
              }else{
                compare = compare + newArray[i];
              }
        //      System.out.println("String: " + compare);
              if(keywords.contains(compare)){
                System.out.println("KEYWORD: " + compare);
                compare = "";
              }else if(relational.contains(compare)){
                if((i+1) < newArray.length){
                  if(relational.contains(String.valueOf(newArray, i, 2))){
                    compare = compare + newArray[i+1];
                    System.out.println("RELATIONAL: " + compare);
                    compare = "";
                  }else{
                    if(!(relational.contains(String.valueOf(newArray[i-1])))){
                      System.out.println("RELATIONAL: " + compare);
                      compare = "";
                    }
                  }
                  compare = "";
                }
              }else if(delims.contains(compare)){
                System.out.println("DELIMS: " + compare);
                compare = "";
              }else if(errors.contains(compare)){
                String newError = "";
                for(i = i; i < newArray.length; i++){
                  if(!(String.valueOf(newArray[i])).equals(" ")){
                    newError = newError + newArray[i];
                  }else{
                    break;
                  }
                }
                System.out.println("ERROR: " + newError);
              }else if(isInteger(compare) == true){
                for(int r = i+1; r < newArray.length; r++){
                  if(isInteger(String.valueOf(newArray[r]))){
                    compare = compare + newArray[r];
                  }else if((String.valueOf(newArray[r])).equals(".")){
                    compare = compare + newArray[r];
                  }else if((String.valueOf(newArray[r])).equals("E")){
                    compare = compare + newArray[r];
                  }else if(((String.valueOf(newArray[r])).equals("+")) || ((String.valueOf(newArray[r])).equals("-"))){
                    compare = compare + newArray[r];
                  }else if(errors.contains(String.valueOf(newArray[r]))){
                    break;
                  }
                }
                if((compare.indexOf('.')) >= 0 || (compare.indexOf('E')) >= 0){
                  System.out.println("FLOAT: " + compare);
                  compare = "";
                }else{
                  System.out.println("NUM: " + compare);
                  compare = "";
                }
              }else if((String.valueOf(newArray[i]).equals(" ")) || (delims.contains(String.valueOf(newArray[i]))) ||
                    (relational.contains(String.valueOf(newArray[i])))){
                String newID = "";
                String testString = "";
                String beforeTest = "";
                if((!(keywords.contains(compare)))&&(!(relational.contains(compare)))&&(!(delims.contains(compare)))
                      &&(!(errors.contains(compare)))&&(!(compare).equals(" "))){
                  for(int k = 0; k < compare.length(); k++){
                    char c = compare.charAt(k);
                    beforeTest = testString;
                    testString = testString + c;
                    if(delims.contains(String.valueOf(c))){
                      System.out.println("DELIMS: " + String.valueOf(c));
                      newID = beforeTest;
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = "";
                      for(int j = k+1; j < compare.length(); j++){
                        if(!(String.valueOf(newArray[j])).equals(" ")){
                          newError = newError + newArray[j];
                        }else{
                          break;
                        }
                        System.out.println("ERROR: " + newError);
                      }
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
              }else if(i == (newArray.length - 1)){
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
                          break;
                        }
                      }
                      System.out.println("ERROR: " + newError);
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
