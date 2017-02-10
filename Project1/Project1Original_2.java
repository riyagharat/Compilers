import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class Project1Original_2{
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

        ArrayList<String> errors = new ArrayList<>();
        errors.add("!");
        errors.add("@");
        errors.add("_");

        ArrayList<String> symbolTable = new ArrayList<>();

        lexical(keywords, relational, delims, errors, symbolTable, file);

    }

    public static Character[] toCharacterArray( String s ) {
       if ( s == null ) {
         return null;
       }
       int len = s.length();
       Character[] array = new Character[len];
       for (int i = 0; i < len ; i++) {
          array[i] = new Character(s.charAt(i));
       }
       return array;
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
        while(input.hasNextLine()){
          String text = input.nextLine();
          String compare = "";
          System.out.println(text);
          System.out.println("");
          int commentCounter = 0;
          Character [] newArray = toCharacterArray(text);
          for(int i = 0; i < newArray.length; i++){
    //        System.out.println("hello " + newArray[i]);
            compare = compare + newArray[i];
    //        System.out.println("peace " + compare);
            if(keywords.contains(compare)){
              System.out.println("KEYWORD: " + compare);
              compare = "";
            }else if(relational.contains(compare)){
              System.out.println("RELATIONAL: " + compare);
              compare = "";
            }else if(delims.contains(compare)){
              System.out.println("DELIMS: " + compare);
              compare = "";
            }else if(errors.contains(compare)){
      //        System.out.println("ERROR: " + compare);
              String newError = "";
              for(int j = i; j < newArray.length; j++){
                newError = newError + newArray[j];
              }
              System.out.println("ERROR: " + newError);
      //        newError = "";
      //        compare = "";
              break;
            }else if(isInteger(compare) == true){
              System.out.println("NUM: " + compare);
              compare = "";
            }else if(String.valueOf(newArray[i]).equals(" ")){
              if((!(keywords.contains(compare)))&&(!(relational.contains(compare)))&&(!(delims.contains(compare)))
                    &&(!(errors.contains(compare)))&&(!(compare).equals(" "))){
                symbolTable.add(compare);
                System.out.println("ID: " + compare);
              }
              compare = "";
  //          }else if(String.valueOf(newArray[i]).equals("/") && (String.valueOf(newArray[i+1]).equals("*"))){
  //            commentCounter++;
  //            if(String.valueOf(newArray[i]).equals("*") && (String.valueOf(newArray[i+1]).equals("/"))){
  //                commentCounter++;
  //            }
            }else{
          //    compare = "";
  //            String symbol = "", symbol2 = "";
  //            for(int k = i; k < newArray.length; k++){
  //              if(!(newArray[k].equals(" "))){
  //                symbol = symbol + newArray[k];
  //              }else{
  //                symbol2 = symbol;
  //                symbol = "";
  //                symbolTable.add(symbol2);
  //                System.out.println("ID: " + symbol2);
  //                break;
  //              }
  //            }
          //    System.out.println("hi");
            }
  //          System.out.println("compare: " + compare);
  //          compare = "";
          }
          compare = "";
        }
      }catch (FileNotFoundException e){
        e.printStackTrace();
      }
    }
}
