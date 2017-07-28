import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.*;

/**
 *
 * @author Riya Gharat
 * N00901846
 */

public class Project4{

  /*  Project 4 Due 4/13/17 Thursday 11:59 PM (nearly midnight)

Intermediate Code Generation

You should generate simple quadruples as explained in class and shown
below.  When  you generate simple quadruples you should use the operators
as described in class.

Your project should be shar'd containing a makefile, source file, doc
file, and typescript (showing your testing). The makefile file should
be invoked with "make" creating an executable of p4. Your project will
be invoked with p4 fn1 where fn1 is the program file to be analyzed.
The intermediate code should be written to the screen. Of course, fn1 fn2
will be any name of my chosing.

This project must be complete in that the lexical analyzer and
parser must be included to create the parse tree as required.

Use turnin for submission as

turnin fn ree4620_4

where fn is the shar'd file of your complete project.

----------------------------------------------------
Example test files and corresponding code generation:
----------------------------------------------------
----------------------------------------------------
Example 1

void main(void)
{
  int x;
  int y;
  int z;
  int m;
   while(x + 3 * y > 5)
   {
     x = y + m / z;
     m = x - y + z * m / z;
   }
}

----------------------------------------------------

1         func           main           void           0
2         alloc          4                             x
3         alloc          4                             y
4         alloc          4                             z
5         alloc          4                             m
6         mult           3              y              _t0
7         add            x              _t0            _t1
8         comp           _t1            5              _t2
9         BRLEQ          _t2                           21
10        block
11        div            m              z              _t3
12        add            y              _t3            _t4
13        assign         _t4                           x
14        sub            x              y              _t5
15        times          z              m              _t6
16        div            _t6            z              _t7
17        add            _t5            _t7            _t8
18        assign         _t8                           m
19        end            block
20        BR                                           6
21        end            func           main

----------------------------------------------------
----------------------------------------------------
Example 2

int sub(int x, float y)
{
   return(x+x);
}
void main(void)
{
  int x;
  int y;
  y = sub(x);
}


----------------------------------------------------

1         func           sub            int            2
2         param
3         alloc          4                             x
4         param
5         alloc    		 4 							       y
6         add            x              x              _t0
7         return                                       _t0
8         end            func           sub
9         func           main           void           0
10        alloc          4                             x
11        alloc          4                             y
12        arg                                          x
13        call           sub            1              _t1
14        assign         _t1                           y
15        end            func           main

Example 3

void main(void)
{
   int x[10];
   int y;
   y = (x[5] + 2) * y;
}

----------------------------------------------------

1	  func  main		void		0
2   alloc 	 			 40			x
3	  alloc 	 			 4			y
4	  disp  x			  20			_t0
5   add   _t0		   2			_t1
6   mult  _t1      y        _t2
7	  asign	_t2				y
8	  end		func		main */

      // global variables
      // stores the current index of the location in the array
      static int currentIndex = 0;
      // a boolean variable that determines that something is a number instead of a id
      static boolean isNum = false;
      // a variable to determine the scope of the identifier
      static int depthCount = 0;
      // a variable to track the current parse index in the tokens array
      static int currentIndexParse = 0;
      // incremention counter for codeGen
      static int indexCode = 0;

      static int codeVar = 0;

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

        // ArrayList for storing the tokens that are generated
        ArrayList<Token> tokens = new ArrayList<>();
        // LinkedList for storing the tokens that are generated, used in semantic analysis
        LinkedList<Token> tokenList = new LinkedList<Token>();
        // LinkedList for storing the identifiers that are generated, used in semantic analysis
        LinkedList<Identifier> identifierList = new LinkedList<Identifier>();
        // An array for Code Generation
        String[][]codeGen = new String[100][5];

        // Calls the lexical analyzer method
        lexical(keywords, relational, delims, errors, numDelims, file, tokens, tokenList);

        // Calls the Parser
        syntax(tokens, codeGen);

        semantic(identifierList, tokenList, delims, codeGen);

        for (int i = 0; i < codeGen.length; i++) {
          int j = 0;
          if(codeGen[i][j] != null){
            for (j = 0; j < codeGen[i].length; j++) {
              System.out.printf("%7s", codeGen[i][j]);
            }
            System.out.printf("\n");
          }
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

    // A method that reads in possible numbers and checks to see if they are valid
    // if they are it prints them to the console as either a num or a float
    // otherwise they are marked as an error
    public static void checkNum(String num, ArrayList<String> errors, ArrayList<Token> tokens, LinkedList<Token> tokenList){
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
          System.out.println("REJECT");
          System.exit(1);
          break;
        }
      }
      if(errorNum.isEmpty() == true){
        if((num.indexOf('.')) >= 0 || (num.indexOf('E')) >= 0){
          // added a flag for float to the token object, makes it easier to build the symbolList
          Token data = new Token("NUM", "float", numToPrint, Project4.depthCount);
          tokens.add(data);
          tokenList.addLast(data);
          numToPrint = "";
        }else{
          // added a flag for int to the token object, makes it easier to build the symbolList
          Token data = new Token("NUM", "int", numToPrint, Project4.depthCount);
          tokens.add(data);
          tokenList.addLast(data);
          numToPrint = "";
        }
      }else{
        System.out.println("ERROR: " + errorNum);
        System.out.println("REJECT");
        System.exit(1);
      }
    }

    /**
     * The method that generates tokens based on the input file and also prints out its result to the console
     * @param keywords : the ArrayList keywords to check if the string contains a keyword
     * @param relational : the ArrayList relational to check if the string contains a relational operator
     * @param delims : the ArrayList delims to check if the string contains an delimitor
     * @param errors : the ArrayList errors to check if the string contains an error
     * @param symbolTable : the ArrayList symbolTable that stores the identifiers, this isn't fully implemented yet
     * @param numDelims : the ArrayList numDelims to check if the string contains a delimitor Specific
     * to numbers, ignores '.'
     * @param file : the file that is read in from the command line
     */

    public static void lexical(ArrayList<String> keywords, ArrayList<String> relational, ArrayList<String> delims,
        ArrayList<String> errors, ArrayList<String> numDelims, File file, ArrayList<Token> tokens,
        LinkedList<Token> tokenList){

      try{
        Scanner input = new Scanner(file);
        int commentCounter = 0;
        while(input.hasNextLine()){
          String text = input.nextLine();
          String compare = "";
          if(text.isEmpty() == false){
          }
          char [] newArray = text.toCharArray();
          // Checks for comments
          for(Project4.currentIndex = 0; Project4.currentIndex < newArray.length; Project4.currentIndex++){
            if ((Project4.currentIndex + 1)< newArray.length){
               if (String.valueOf(newArray, Project4.currentIndex, 2).equals("/*")){
                  commentCounter++;
                 Project4.currentIndex++;
               }else if (String.valueOf(newArray, Project4.currentIndex, 2).equals("*/")){
                  if(commentCounter > 0){
                    commentCounter--;
                  }else if(commentCounter == 0){
                  }
                  Project4.currentIndex++;
               }else if (String.valueOf(newArray, Project4.currentIndex, 2).equals("//")){
                 break;
               }
            }
            // If there are no commments, continue reading the line
            if(commentCounter == 0){
              if(!String.valueOf(newArray[Project4.currentIndex]).equals("\t")){
                compare = compare + newArray[Project4.currentIndex];
              }
              // If the string matches a keyword
              if(keywords.contains(compare)){
                if(Project4.currentIndex + 1 < newArray.length){
                  if((String.valueOf(newArray[Project4.currentIndex + 1]).equals(" "))){
                    Token data = new Token("KEYWORD", "", compare, Project4.depthCount);
                    tokens.add(data);
                    tokenList.addLast(data);
                    compare = "";
                  }
                }
                if((Project4.currentIndex == (newArray.length - 1))){
                  Token data = new Token("KEYWORD", "", compare, Project4.depthCount);
                  tokens.add(data);
                  tokenList.addLast(data);
                  compare = "";
                }
                // If there a relational operator that matches the string
              }else if(relational.contains(compare)){
                if((Project4.currentIndex+1) < newArray.length){
                  if(relational.contains(String.valueOf(newArray, Project4.currentIndex, 2))){
                    compare = compare + newArray[Project4.currentIndex+1];
                    Token data = new Token("RELATIONAL", "", compare, Project4.depthCount);
                    tokens.add(data);
                    tokenList.addLast(data);
                    compare = "";
                  }else{
                    if(!(relational.contains(String.valueOf(newArray[Project4.currentIndex-1])))){
                      Token data = new Token("RELATIONAL", "", compare, Project4.depthCount);
                      tokens.add(data);
                      tokenList.addLast(data);
                      compare = "";
                    }
                  }
                  compare = "";
                }
              }else if(errors.contains(compare)){
                boolean accidentalError = false;
                String newError = "";
                if(compare.equals("!")){
                  if(Project4.currentIndex + 1 < newArray.length){
                     if((String.valueOf(newArray[Project4.currentIndex + 1])).equals("=")){
                        Project4.currentIndex ++;
                        compare = compare + String.valueOf(newArray[Project4.currentIndex]);
                        Token data = new Token("RELATIONAL", "", compare, Project4.depthCount);
                        tokens.add(data);
                        tokenList.addLast(data);
                        compare = "";
                        Project4.currentIndex ++;
                        accidentalError = true;
                     }
                  }else{
                     newError = "!";
                     accidentalError = false;
                     Project4.currentIndex ++;
                  }
                }else{
                  for(int z = Project4.currentIndex; z < newArray.length; z++){
                    if(!(String.valueOf(newArray[z])).equals(" ")){
                      newError = newError + newArray[z];
                    }else{
                      Project4.currentIndex = z;
                      break;
                    }
                  }
                }
                // Currently ending the program if there is an error
                if(accidentalError == false){
                  System.out.println("ERROR: " + newError);
                  System.out.println("REJECT");
                  System.exit(1);
                  newError= "";
                  compare = "";
               }
               // if there is an integer that was read
              }else if((isInteger(compare) == true) || (compare.equals(".")) || (compare.equals("E"))){
                String newNum = "";
                Project4.isNum = true;
                for(int x = Project4.currentIndex; x < newArray.length; x++){
                  if((newArray[x] != ' ') && (!(numDelims.contains(String.valueOf(newArray[x]))))){
                    newNum = newNum + newArray[x];
                  }else{
                    Project4.currentIndex = x-1;
                    checkNum(newNum, errors, tokens, tokenList);
                    compare = "";
                    Project4.isNum = false;
                    break;
                  }
                  if(x == (newArray.length - 1)){
                    checkNum(newNum, errors, tokens, tokenList);
                  }
                  Project4.currentIndex = x;
                }
                Project4.isNum = false;
              // if the string is a delimitor
              }else if(delims.contains(compare)){
                if(compare.equals("{")){
                  Project4.depthCount++;
                }else if(compare.equals("}")){
                  Project4.depthCount--;
                }
                Token data = new Token("DELIMS", "", compare, Project4.depthCount);
                tokens.add(data);
                tokenList.addLast(data);

                compare = "";
              // if the string compare that is read is none of the above
              // then it is most likely an identifier
              }else if((String.valueOf(newArray[Project4.currentIndex]).equals(" ")) || (delims.contains(String.valueOf(newArray[Project4.currentIndex]))) ||
                    (relational.contains(String.valueOf(newArray[Project4.currentIndex])))){
                String newID = "";
                String testString = "";
                String beforeTest = "";
                Token data = new Token("", "", "", 0);
                Token data4 = new Token("", "", "", 0);
                Token data5 = new Token("", "", "", 0);
                boolean check = false, check2 = false, check3 = false;
                String type = "", type2 = "", type3 = "", val = "", val2 = "", val3 = "";
                int depth = 0, depth2 = 0, depth3 = 0;
                if((!(keywords.contains(compare)))&&(!(relational.contains(compare)))&&(!(delims.contains(compare)))
                      &&(!(errors.contains(compare)))&&(!(compare).equals(" "))&&(Project4.isNum == false)){
                  for(int k = 0; k < compare.length(); k++){
                    char c = compare.charAt(k);
                    beforeTest = testString;
                    testString = testString + c;
                    if(delims.contains(String.valueOf(c))){
                      data = new Token("DELIMS", "", String.valueOf(c), Project4.depthCount);
                      type = "DELIMS";
                      val = String.valueOf(c);
                      depth = Project4.depthCount;
                      check = true;
                      compare = "";
                      newID = beforeTest;
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = compare.substring(k, compare.length());
                      System.out.println("ERROR: " + newError);
                      System.out.println("REJECT");
                      System.exit(1);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      data4 = new Token("RELATIONAL", "", String.valueOf(c), Project4.depthCount);
                      type2 = "RELATIONAL";
                      val2 = String.valueOf(c);
                      depth2 = Project4.depthCount;
                      check2 = true;
                    }else{
                      newID = newID + c;
                    }
                  }
                  if(newID.isEmpty() == false){
                    if(keywords.contains(newID)){
                      data5 = new Token("KEYWORD", "", newID.trim(), Project4.depthCount);
                      type3 = "KEYWORD";
                      val3 = newID;
                      depth3 = Project4.depthCount;
                      check3 = true;
                    }else{
                      Token data2 = new Token("ID", "", newID.trim(), Project4.depthCount);
                      tokens.add(data2);
                      tokenList.addLast(data2);
                    }
                  }
                  if (check3 == true){
                    tokenList.addLast(data5);
                    tokens.add(data5);
                  }
                  if (check == true){
                    tokenList.addLast(data);
                    tokens.add(data);
                  }
                  if (check2 == true){
                    tokenList.addLast(data4);
                    tokens.add(data4);
                  }
                }

                compare = "";
              // if the string only contains 1 char
              }else if(Project4.currentIndex == (newArray.length - 1)){
                String newID = "";
                Token data = new Token("", "", "", 0);
                Token data4 = new Token("", "", "", 0);
                Token data5 = new Token("", "", "", 0);
                String type = "", type2 = "", type3 = "", val = "", val2 = "", val3 = "";
                int depth = 0, depth2 = 0, depth3 = 0;
                boolean check = false, check2 = false, check3 = false;
                  for(int p = 0; p < compare.length(); p++){
                    char c = compare.charAt(p);
                    if(delims.contains(String.valueOf(c))){
                      data = new Token("DELIMS", "", String.valueOf(c), Project4.depthCount);
                      type = "DELIMS";
                      val = String.valueOf(c);
                      depth = Project4.depthCount;
                      check = true;
                    }else if(errors.contains(String.valueOf(c))){
                      String newError = "";
                      for(int j = p+1; j < newArray.length; j++){
                        if(!(String.valueOf(newArray[j])).equals(" ")){
                          newError = newError + newArray[j];
                        }else{
                          Project4.currentIndex = j;
                          break;
                        }
                      }
                      System.out.println("ERROR: " + newError);
                      System.out.println("REJECT");
                      System.exit(1);
                      compare = "";
                      break;
                    }else if(relational.contains(String.valueOf(c))){
                      data4 = new Token("RELATIONAL", "", String.valueOf(c), Project4.depthCount);
                      type2 = "RELATIONAL";
                      val2 = String.valueOf(c);
                      depth2 = Project4.depthCount;
                      check2 = true;
                    }else{
                      newID = newID + c;
                    }
                  }
                  if(newID.isEmpty() == false){
                    if(keywords.contains(newID)){
                      data5 = new Token("KEYWORD", "", newID, Project4.depthCount);
                      type3 = "KEYWORD";
                      val3 = newID;
                      depth3 = Project4.depthCount;
                      check3 = true;
                    }else{
                      Token data2 = new Token("ID", "", newID.trim(), Project4.depthCount);
                      tokens.add(data2);
                      tokenList.addLast(data2);
                    }
                  }
                  if(check3 == true){
                    tokenList.addLast(data5);
                    tokens.add(data5);
                  }
                  if(check == true){
                    tokenList.addLast(data);
                    tokens.add(data);
                  }
                  if(check2 == true){
                    tokenList.addLast(data4);
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

    // The parser function which has the tokens array passed to it
    public static void syntax(ArrayList<Token> tokens, String[][] codeGen){
      //EOF character that tells when all of the tokens have been parsed
      Token EOF = new Token("EOF", "", "EOF", 0);
      tokens.add(EOF);

      program(tokens, codeGen);
    }

    // program -> declaration-list
    public static void program(ArrayList<Token> tokens, String[][] codeGen){
      declarationList(tokens, codeGen);
      return;
    }

    // declaration-list -> type-specifier C  declaration-list'
    public static void declarationList(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("EOF"))){
      }else{
        typeSpecifier(tokens, codeGen);
        C(tokens, codeGen);
        declarationList2(tokens, codeGen);
        return;
      }
    }

    // declaration-list' -> type-specifier C  declaration-list' | empty
    public static void declarationList2(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("EOF"))){
      }else{
        if(((tokens.get(Project4.currentIndexParse).getToken()).equals("int")) ||
        ((tokens.get(Project4.currentIndexParse).getToken()).equals("float")) ||
        ((tokens.get(Project4.currentIndexParse).getToken()).equals("void"))){
          typeSpecifier(tokens, codeGen);
          C(tokens, codeGen);
          declarationList2(tokens, codeGen);
        }else{
          return;
        }
      }
    }

    // C -> ID X
    public static void C(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("EOF"))){
      }else{
        if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")){
          Project4.currentIndexParse++;
          X(tokens, codeGen);
          return;
        }
      }
    }

    // X -> Y | ( params ) compound-stmt
    public static void X(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken().equals("("))){
        Project4.currentIndexParse++;
        params(tokens, codeGen);
        compoundStmt(tokens, codeGen);
        return;
      }else{
        Y(tokens, codeGen);
        return;
      }
    }

    // Y -> ; | [ NUM ];
    public static void Y(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken().equals("["))){
        if((tokens.get(Project4.currentIndexParse + 1).getTokenType().equals("NUM"))){
          Project4.currentIndexParse++;
          if((tokens.get(Project4.currentIndexParse + 1).getToken().equals("]"))){
            Project4.currentIndexParse++;
            if((tokens.get(Project4.currentIndexParse + 1).getToken().equals(";"))){
              Project4.currentIndexParse++;
              Project4.currentIndexParse++;
              return;
            }
          }
        }
      }else if((tokens.get(Project4.currentIndexParse).getToken().equals(";"))){
        Project4.currentIndexParse++;
        return;
      }
    }

    // type-specifier -> int | void | float
    public static void typeSpecifier(ArrayList<Token> tokens, String[][] codeGen){
      if(!(((tokens.get(Project4.currentIndexParse).getToken()).equals("int")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("void")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("float")))){
  //       System.out.println("ERROR: Expecting int or void or float got: " + (tokens.get(Project4.currentIndexParse).getToken()));
//         System.out.println("REJECT");
//         System.exit(1);
       }
       Project4.currentIndexParse++;
       return;
    }

    // params -> param param-list' | void
    public static void params(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("void")) &&
        ((tokens.get(Project4.currentIndexParse + 1).getToken()).equals(")"))){
          Project4.currentIndexParse = Project4.currentIndexParse + 2;
        return;
      }else if(((tokens.get(Project4.currentIndexParse).getToken()).equals("int")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("void")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("float"))){
          param(tokens, codeGen);
          paramList2(tokens, codeGen);
          if((tokens.get(Project4.currentIndexParse).getToken()).equals(")")){
            Project4.currentIndexParse++;
            return;
          }
      }
    }

    // param-list' -> , param | empty
    public static void paramList2(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals(",")){
        Project4.currentIndexParse++;
        param(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // param -> type-specifier ID M
    public static void param(ArrayList<Token> tokens, String[][] codeGen){
      typeSpecifier(tokens, codeGen);
      if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")){
        Project4.currentIndexParse++;
        M(tokens, codeGen);
        return;
      }
    }

    // M -> empty | []
    public static void M(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("[")){
        if((tokens.get(Project4.currentIndexParse + 1).getToken()).equals("]")){
          Project4.currentIndexParse++;
          Project4.currentIndexParse++;
          return;
        }
      }else{
        return;
      }
    }

    // compound-stmt -> { local-declarations' statement-list' }
    public static void compoundStmt(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("{")){
        Project4.currentIndexParse++;
        localDeclarations2(tokens, codeGen);
        statementList2(tokens, codeGen);
        Project4.currentIndexParse--;
        if((tokens.get(Project4.currentIndexParse + 1).getToken()).equals("}")){
          Project4.currentIndexParse++;
          if(((tokens.get(Project4.currentIndexParse).getToken()).equals("EOF"))){
          }
          return;
        }
      }
    }

    // local-declarations' -> type-specifier ID Y local-declarations' | empty
    public static void localDeclarations2(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("int")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("float")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("void"))){
        typeSpecifier(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getTokenType().equals("ID"))){
          Project4.currentIndexParse++;
          Y(tokens, codeGen);
          localDeclarations2(tokens, codeGen);
          return;
        }
      }else{
        return;
      }
    }

    // statement-list' -> statement statement-list' | empty
    public static void statementList2(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("(")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("if")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("NUM")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals(";")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("while")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("return")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("{"))){
        statement(tokens, codeGen);
        statementList2(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
    public static void statement(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("("))){
        expressionStmt(tokens, codeGen);
        return;
      }else if((tokens.get(Project4.currentIndexParse).getToken()).equals("{")){
        compoundStmt(tokens, codeGen);
        return;
      }else if((tokens.get(Project4.currentIndexParse).getToken()).equals("if")){
        selectionStmt(tokens, codeGen);
        return;
      }else if((tokens.get(Project4.currentIndexParse).getToken()).equals("while")){
        iterationStmt(tokens, codeGen);
        return;
      }else if((tokens.get(Project4.currentIndexParse).getToken()).equals("return")){
        returnStmt(tokens, codeGen);
        return;
      }
    }

    // expression-stmt -> expression ; | ;
    public static void expressionStmt(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals(";")){
        Project4.currentIndexParse++;
        return;
      }else{
        expression(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getToken()).equals(";")){
          Project4.currentIndexParse++;
          return;
        }
      }
    }

    // selection-stmt - > if ( expression ) T
    public static void selectionStmt(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("if")){
        if((tokens.get(Project4.currentIndexParse + 1).getToken()).equals("(")){
          Project4.currentIndexParse++;
          expression(tokens, codeGen);
          if((tokens.get(Project4.currentIndexParse - 1).getToken()).equals(")")){
            T(tokens, codeGen);
            return;
          }
        }
      }
    }

    // T -> statement | else statement
    public static void T(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("else")){
        Project4.currentIndexParse++;
        statement(tokens, codeGen);
        return;
      }else{
        statement(tokens, codeGen);
        return;
      }
    }

    // iteration-stmt -> while ( expression ) statement
    public static void iterationStmt(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("while")){
        if((tokens.get(Project4.currentIndexParse + 1).getToken()).equals("(")){
          Project4.currentIndexParse++;
          expression(tokens, codeGen);
          statement(tokens, codeGen);
          return;
        }
      }
    }

    // return-stmt -> return expression-stmt
    public static void returnStmt(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("return")){
        Project4.currentIndexParse++;
        expressionStmt(tokens, codeGen);
        return;
      }
    }

    // expression -> ID F | ( expression ) term' B S | NUM term' B S
    public static void expression(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")){
        Project4.currentIndexParse++;
        F(tokens, codeGen);
        return;
      }else if((tokens.get(Project4.currentIndexParse).getToken()).equals("(")){
        Project4.currentIndexParse++;
        expression(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getToken()).equals(")")){
          Project4.currentIndexParse++;
          term2(tokens, codeGen);
          B(tokens, codeGen);
          S(tokens, codeGen);
          return;
        }
      }else if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("NUM")){
          Project4.currentIndexParse++;
          term2(tokens, codeGen);
          B(tokens, codeGen);
          S(tokens, codeGen);
          return;
      }
    }

    // F -> P G | ( args ) term' B S
    public static void F(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("(")){
        Project4.currentIndexParse++;
        args(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getToken()).equals(")")){
          Project4.currentIndexParse++;
          term2(tokens, codeGen);
          B(tokens, codeGen);
          S(tokens, codeGen);
          return;
        }
      }else{
        P(tokens, codeGen);
        G(tokens, codeGen);
        return;
      }
    }

    // G -> = expression | term' B S
    public static void G(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("=")){
        Project4.currentIndexParse++;
        expression(tokens, codeGen);
        return;
      }else{
        term2(tokens, codeGen);
        B(tokens, codeGen);
        S(tokens, codeGen);
        return;
      }
    }

    // P -> empty | [ expression ]
    public static void P(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("[")){
        Project4.currentIndexParse++;
        expression(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getToken()).equals("]")){
          Project4.currentIndexParse++;
          return;
        }
      }else{
        return;
      }
    }

    // S -> relop factor term' B | empty
    public static void S(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("<=")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("<")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals(">")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals(">=")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("==")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("!="))){
        relop(tokens, codeGen);
        factor(tokens, codeGen);
        term2(tokens, codeGen);
        B(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // relop -> <= | < | > | >= | == | !=
    public static void relop(ArrayList<Token> tokens, String[][] codeGen){
      if(!(((tokens.get(Project4.currentIndexParse).getToken()).equals("<=")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("<")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals(">")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals(">=")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("==")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("!=")))){
//         System.out.println("ERROR: Expecting <= or < or > or >= or == or != got: " + (tokens.get(Project4.currentIndexParse).getToken()));
//         System.out.println("REJECT");
//         System.exit(1);
       }
       Project4.currentIndexParse++;
       return;
    }

    // B -> addop factor term' B | empty
    public static void B(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("+")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("-"))){
        addop(tokens, codeGen);
        factor(tokens, codeGen);
        term2(tokens, codeGen);
        B(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // addop -> + | -
    public static void addop(ArrayList<Token> tokens, String [][] codeGen){
      if(!(((tokens.get(Project4.currentIndexParse).getToken()).equals("+")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("-")))){
  //       System.out.println("ERROR: Expecting + or - got: " + (tokens.get(Project4.currentIndexParse).getToken()));
  //       System.out.println("REJECT");
//         System.exit(1);
       }
       Project4.currentIndexParse++;
       return;
    }

    // term' -> mulop factor term' | empty
    public static void term2(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getToken()).equals("*")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("/"))){
        mulop(tokens, codeGen);
        factor(tokens, codeGen);
        term2(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // mulop -> * | /
    public static void mulop(ArrayList<Token> tokens, String[][] codeGen){
      if(!(((tokens.get(Project4.currentIndexParse).getToken()).equals("*")) ||
       ((tokens.get(Project4.currentIndexParse).getToken()).equals("/")))){
  //       System.out.println("ERROR: Expecting * or / got: " + (tokens.get(Project4.currentIndexParse).getToken()));
  //       System.out.println("REJECT");
  //       System.exit(1);
       }
       Project4.currentIndexParse++;
       return;
    }

    // factor -> ( expression ) | ID E | NUM
    public static void factor(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("(")){
        Project4.currentIndexParse++;
        expression(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getToken()).equals(")")){
          Project4.currentIndexParse++;
          return;
        }
      }else if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")){
        Project4.currentIndexParse++;
        E(tokens, codeGen);
        return;
      }else if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("NUM")){
        Project4.currentIndexParse++;
        return;
      }
    }

    // E -> P | ( args )
    public static void E(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals("(")){
        Project4.currentIndexParse++;
        args(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getToken().equals(")"))){
          Project4.currentIndexParse++;
          return;
        }
      }else{
        P(tokens, codeGen);
        return;
      }
    }

    // args -> expression args-list' | empty
    public static void args(ArrayList<Token> tokens, String[][] codeGen){
      if(((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")) ||
      ((tokens.get(Project4.currentIndexParse).getToken()).equals("(")) ||
      ((tokens.get(Project4.currentIndexParse).getTokenType()).equals("NUM"))){
        expression(tokens, codeGen);
        if((tokens.get(Project4.currentIndexParse).getTokenType()).equals("ID")){
          codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
          codeGen[Project4.indexCode][1] = "arg";
          codeGen[Project4.indexCode][2] = "";
          codeGen[Project4.indexCode][3] = "";
          codeGen[Project4.indexCode][4] = tokens.get(Project4.currentIndexParse).getTokenType();
          Project4.indexCode++;
        }
        argsList2(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // args-list' -> , expression args-list' | empty
    public static void argsList2(ArrayList<Token> tokens, String[][] codeGen){
      if((tokens.get(Project4.currentIndexParse).getToken()).equals(",")){
        Project4.currentIndexParse++;
        expression(tokens, codeGen);
        argsList2(tokens, codeGen);
        return;
      }else{
        return;
      }
    }

    // This method is the semantic analyzer section. It makes sure that the data that has been
    // successfully parsed, can also be successfully understood. It takes into account certain
    // semantic rules of the TINY C grammar.

    public static void semantic(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList, ArrayList<String> delims,
      String[][] codeGen){

        // num is the used to determine the array length
        int num = 0;
        int funcParam = 0;
        String funcNameTemp = "";
        int funcCount = 0;
        for(int i = 0; i < tokenList.size(); i++){
          // count is used to determine the number of parameters
          int count = 0;
          // increment moves the pointer along by two to record the number of parameters
          int increment = i;
          // x is used to check the token before it
          int x = i-1;
          if((tokenList.get(i).getTokenType()).equals("ID")){
            if((tokenList.get(i+1).getToken()).equals("(")){
              increment = i+2;
              while(!((tokenList.get(increment).getToken()).equals(")"))){
                if((tokenList.get(increment).getToken()).equals(",")){
                  count++;
                }
                increment++;
              }
              // have to increment count after because count is actually recording the number of commas
              count++;
              funcParam = count;
              // checks if the ID is a function declaration
              if(((tokenList.get(x).getToken()).equals("float")) || ((tokenList.get(x).getToken()).equals("int")) || ((tokenList.get(x).getToken()).equals("void"))){
                // decrement for void param
                if((tokenList.get(i+2).getToken()).equals("void")){
                  count--;
                  funcParam--;
                }
                Identifier newIdentifier = new Identifier("FUNCTION", tokenList.get(x).getToken(), tokenList.get(i).getToken(), tokenList.get(i).getDepth(), count, "DEFINITION", "", 0, "", i);
                if((tokenList.get(x).getToken()).equals("int")){
                  tokenList.get(i).setNumType("int");
                }else if((tokenList.get(x).getToken()).equals("float")){
                  tokenList.get(i).setNumType("float");
                }else if((tokenList.get(x).getToken()).equals("void")){
                  tokenList.get(i).setNumType("void");
                }
                identifierList.addLast(newIdentifier);
                if(funcCount > 0){
                  if(!(funcNameTemp.equals(newIdentifier.getName()))){
                    codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                    codeGen[Project4.indexCode][1] = "end";
                    codeGen[Project4.indexCode][2] = "func";
                    codeGen[Project4.indexCode][3] = funcNameTemp;
                    codeGen[Project4.indexCode][4] = "";
                    Project4.indexCode++;
                  }
                }
                funcNameTemp = newIdentifier.getName();

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "func";
                codeGen[Project4.indexCode][2] = newIdentifier.getName();
                codeGen[Project4.indexCode][3] = newIdentifier.getType();
                codeGen[Project4.indexCode][4] = String.valueOf(count);
                Project4.indexCode++;
                funcCount++;
              }else{
                // if its not a function declaration, then its a function call
                Identifier newIdentifier2 = new Identifier("FUNCTION", "", tokenList.get(i).getToken(), tokenList.get(i).getDepth(), count, "CALL", "", 0, "", i);
                identifierList.addLast(newIdentifier2);

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "call";
                codeGen[Project4.indexCode][2] = newIdentifier2.getName();
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(count);
                Project4.indexCode++;
              }
            }else{
              // if the ID is not a function, then it is a variable
              if(((tokenList.get(x).getToken()).equals("float")) || ((tokenList.get(x).getToken()).equals("int"))){
                if((tokenList.get(x).getToken()).equals("int")){
                  tokenList.get(i).setNumType("int");
                }else if((tokenList.get(x).getToken()).equals("float")){
                  tokenList.get(i).setNumType("float");
                }
                // checks if the variable is an array
                if((tokenList.get(i+1).getToken()).equals("[")){
                  if((tokenList.get(i+2).getNumType()).equals("int")){
                    num = Integer.parseInt(tokenList.get(i+2).getToken());
                  }else if((tokenList.get(i+2).getToken()).equals("]")){
                    num = 0;
                  }
                  // creates the identifier object with the array tag and length
                  Identifier newIdentifier3 = new Identifier("VARIABLE", tokenList.get(x).getToken().trim(), tokenList.get(i).getToken().trim(), tokenList.get(i).getDepth(), 0, "DEFINITION", "ARRAY", num, "", i);
                  identifierList.addLast(newIdentifier3);
                  codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                  codeGen[Project4.indexCode][1] = "alloc";
                  codeGen[Project4.indexCode][2] = String.valueOf(4*num);
                  codeGen[Project4.indexCode][3] = "";
                  codeGen[Project4.indexCode][4] = newIdentifier3.getName();;
                  Project4.indexCode++;
                }else{
                  // if its not an array, then the tag is blank and the length is 0
                  Identifier newIdentifier3 = new Identifier("VARIABLE", tokenList.get(x).getToken().trim(), tokenList.get(i).getToken().trim(), tokenList.get(i).getDepth(), 0, "DEFINITION", "", 0, "", i);
                  identifierList.addLast(newIdentifier3);
                  if(funcParam > 0){
                    codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                    codeGen[Project4.indexCode][1] = "param";
                    codeGen[Project4.indexCode][2] = "";
                    codeGen[Project4.indexCode][3] = "";
                    codeGen[Project4.indexCode][4] = "";
                    Project4.indexCode++;
                    funcParam--;
                  }
                  codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                  codeGen[Project4.indexCode][1] = "alloc";
                  codeGen[Project4.indexCode][2] = "4";
                  codeGen[Project4.indexCode][3] = "";
                  codeGen[Project4.indexCode][4] = newIdentifier3.getName();
                  Project4.indexCode++;
                }
              }else{
                // if the variable is define as being void, it is not posisble, so its rejected
                if((tokenList.get(i-1).getToken()).equals("void")){
            //      System.out.println("ERROR: Variables cannot be void");
            //      System.out.println("REJECT");
            //      System.exit(1);
                }else{
                  // this is for a variable that is an array instantiation
                  if((tokenList.get(i+1).getToken()).equals("[")){
                    if((tokenList.get(i+2).getNumType()).equals("int")){
                      num = Integer.parseInt(tokenList.get(i+2).getToken());
                    }else if((tokenList.get(i+2).getToken()).equals("]")){
                      num = 0;
                    }
                    // if it was an array it created as an instantiation array with a length
                    Identifier newIdentifier4 = new Identifier("VARIABLE", "", tokenList.get(i).getToken().trim(), tokenList.get(i).getDepth(), 0, "INSTANTIATION", "ARRAY" , num, "", i);
                    identifierList.addLast(newIdentifier4);

                    codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                    codeGen[Project4.indexCode][1] = "mult";
                    codeGen[Project4.indexCode][2] = "4";
                    codeGen[Project4.indexCode][3] = String.valueOf(num);
                    codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.codeVar);
                    Project4.indexCode++;

                    codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                    codeGen[Project4.indexCode][1] = "disp";
                    codeGen[Project4.indexCode][2] = newIdentifier4.getName();
                    codeGen[Project4.indexCode][3] = "t"+String.valueOf(Project4.codeVar);
                    codeGen[Project4.indexCode][4] = "t"+String.valueOf(++Project4.codeVar);
                    Project4.indexCode++;
                    Project4.codeVar++;

                  }else{
                    // if its not an array then it is an instantiation with a length of 0 and no array tag
                    Identifier newIdentifier5 = new Identifier("VARIABLE", "", tokenList.get(i).getToken().trim(), tokenList.get(i).getDepth(), 0, "INSTANTIATION", "", 0, "", i);
                    identifierList.addLast(newIdentifier5);
                    if(funcParam > 0){
                      codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                      codeGen[Project4.indexCode][1] = "arg";
                      codeGen[Project4.indexCode][2] = "";
                      codeGen[Project4.indexCode][3] = "";
                      codeGen[Project4.indexCode][4] = newIdentifier5.getName();
                      Project4.indexCode++;
                      funcParam--;
                    }
                  }
                }
              }
            }
          }
        }

        // this section for both the variables and functions, it looks through the already created identifierList to
        // detemine the type for each one. If the identifier was created and instantiated later then there is no type
        // associated with it, thus this section looks it up ans assigns it accordingly
        for(int j = 0; j < identifierList.size(); j++){
          String varName = "", funcName = "";
          if((identifierList.get(j).getCategory()).equals("FUNCTION")){
            if((identifierList.get(j).getDefCall()).equals("DEFINITION")){
              funcName = identifierList.get(j).getName();
              for(int k = 0; k < identifierList.size(); k++){
                if((identifierList.get(k).getName()).equals(funcName)){
                  if((identifierList.get(k).getCategory()).equals("FUNCTION")){
                    if((identifierList.get(k).getDefCall()).equals("CALL")){
                      identifierList.get(k).setType(identifierList.get(j).getType());
                      if((identifierList.get(j).getType()).equals("int")){
                        tokenList.get(identifierList.get(k).getTokenIndex()).setNumType("int");
                      }else if((identifierList.get(j).getType()).equals("float")){
                        tokenList.get(identifierList.get(k).getTokenIndex()).setNumType("float");
                      }
                    }
                  }
                }
              }
            }
          }else if((identifierList.get(j).getCategory()).equals("VARIABLE")){
            if((identifierList.get(j).getDefCall()).equals("DEFINITION")){
              varName = identifierList.get(j).getName();
              String isArray = identifierList.get(j).getVarType();
              int arrayLength = identifierList.get(j).getArraySize();
              String type = "";
              for(int k = 0; k < identifierList.size(); k++){
                if((identifierList.get(k).getName()).equals(varName)){
                  if((identifierList.get(k).getCategory()).equals("VARIABLE")){
                    if((identifierList.get(k).getDefCall()).equals("INSTANTIATION")){
                      identifierList.get(k).setType(identifierList.get(j).getType());
                      identifierList.get(k).setVarType(isArray);
                      identifierList.get(k).setArraySize(arrayLength);
                      if((identifierList.get(j).getType()).equals("int")){
                        tokenList.get(identifierList.get(k).getTokenIndex()).setNumType("int");
                      }else if((identifierList.get(j).getType()).equals("float")){
                        tokenList.get(identifierList.get(k).getTokenIndex()).setNumType("float");
                      }
                    }
                  }
                }
              }
            }
          }
        }

        String funcNameIn = "";
        String isGlobal = "";
        int indexCheck = 0;
        for(int j = 0; j < identifierList.size(); j++){
          if((identifierList.get(j).getCategory()).equals("FUNCTION")){
            funcNameIn = identifierList.get(j).getName();
            identifierList.get(j).setFunctionNameCurrent(funcNameIn);
            if((identifierList.get(j).getDefCall()).equals("DEFINITION")){
              for(int k = identifierList.get(j).getTokenIndex(); k < tokenList.size(); k++){
                if((tokenList.get(k).getToken()).equals("}")){
                  indexCheck = k;
                  if(tokenList.get(k).getDepth() == identifierList.get(j).getScope()){
                    isGlobal = "GLOBAL";
                    break;
                  }else{
                    isGlobal = funcNameIn;
                  }
                }
              }
            }
          }else if(((identifierList.get(j).getCategory()).equals("VARIABLE")) && funcNameIn.equals("")){
            identifierList.get(j).setFunctionNameCurrent("GLOBAL");
          }else if(((identifierList.get(j).getCategory()).equals("VARIABLE")) && (identifierList.get(j).getTokenIndex() < indexCheck)){
            identifierList.get(j).setFunctionNameCurrent(funcNameIn);
          }else if(((identifierList.get(j).getCategory()).equals("VARIABLE")) && (identifierList.get(j).getTokenIndex() > indexCheck)){
            identifierList.get(j).setFunctionNameCurrent("GLOBAL");
          }
        }



      boolean checkMain = mainCheck(identifierList);
      boolean checkParamSize = paramSizeCheck(identifierList);
      boolean checkDefCall = defCallCheck(identifierList);
      boolean checkParamType = paramTypeCheck(identifierList, tokenList);
      returnSimpleCheck(identifierList, tokenList, delims);
      voidFuncCheck(identifierList, tokenList, codeGen);
      intFloatFuncCheck(identifierList, tokenList, codeGen);
      operatorOperandCheck(identifierList, tokenList, codeGen);
      operatorOperandAssignmentCheck(identifierList, tokenList, codeGen);
      varDeclaredOnceCheck(identifierList);
      if(checkMain == true && checkParamSize == true && checkDefCall == true && checkParamType == true){ // Add more boolean checks as you write the methods
//        System.out.println("ACCEPT");
//        System.exit(1);
      }else{
//        System.out.println("REJECT");
//        System.exit(1);
      }
    }

    // Checks to make sure that there is one main. If there are more than one, it rejects. If
    // there are none it rejects. If main is not the last function in the file, it rejects.
    public static boolean mainCheck(LinkedList<Identifier> identifierList){
      int mainCount = 0;
      boolean lastFunction = false;

      for(int i = 0; i < identifierList.size(); i++){
        if((identifierList.get(i).getCategory()).equals("FUNCTION")){
          if((identifierList.get(i).getName()).equals("main")){
            mainCount++;
            if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
              lastFunction = true;
            }
          }else{
            if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
              lastFunction = false;
            }
          }
        }
      }
      if(mainCount > 1){
  //      System.out.println("REJECT");
  //      System.exit(1);
      }else if(mainCount == 0){
  //      System.out.println("REJECT");
//        System.exit(1);
      }
      if(lastFunction == false){
  //      System.out.println("REJECT");
  //      System.exit(1);
      }
      return true;
    }

    // Checks to make sure that the parameters of a function call match the length
    // of the parameters of the function definition
    public static boolean paramSizeCheck(LinkedList<Identifier> identifierList){
      for(int i = 0; i < identifierList.size(); i++){
        int paramCheck = 0;
        String funcName = "";
        if((identifierList.get(i).getCategory()).equals("FUNCTION")){
          if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
            paramCheck = identifierList.get(i).getNumOfParams();
            funcName = identifierList.get(i).getName();
            for(int j = 0; j < identifierList.size(); j++){
              if((identifierList.get(j).getCategory()).equals("FUNCTION")){
                if((identifierList.get(j).getName()).equals(funcName)){
                  if((identifierList.get(j).getDefCall()).equals("CALL")){
                    if(paramCheck == identifierList.get(j).getNumOfParams()){
                      return true;
                    }else{
          //            System.out.println("REJECT");
          //            System.exit(1);
                    }
                  }
                }
              }
            }
          }
        }
      }
      return true;
    }

    // Checks to make sure that for every function call there is an associated defined function.
    // Same thing with the variable instantiation. It checks that there is a variable defined first
    // before it is used.
    public static boolean defCallCheck(LinkedList<Identifier> identifierList){

      boolean definedFUNC = false, definedVAR = false, find = true;
      int count = 0;
      int counterVar = 0;
      for(int r = 0; r < identifierList.size(); r++){
        if((identifierList.get(r).getCategory()).equals("FUNCTION")){
          count++;
        }
      }
      if(count == identifierList.size()){
        definedVAR = true;
      }
      for(int i = 0; i < identifierList.size(); i++){
        if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
          if((identifierList.get(i).getCategory()).equals("VARIABLE")){
            definedVAR = true;
          }else if((identifierList.get(i).getCategory()).equals("FUNCTION")){
            definedFUNC = true;
          }
        }else{
          if((identifierList.get(i).getCategory()).equals("VARIABLE")){
            definedVAR = false;
          }else if((identifierList.get(i).getCategory()).equals("FUNCTION")){
            definedFUNC = false;
          }
          for(int j = i; j < identifierList.size(); j++){
            String varName = "", funcName = "";
            if((identifierList.get(j).getCategory()).equals("FUNCTION")){
              if((identifierList.get(j).getDefCall()).equals("CALL")){
                funcName = identifierList.get(j).getName();
                for(int k = 0; k < i; k++){
                  if((identifierList.get(k).getName()).equals(funcName)){
                    if((identifierList.get(k).getCategory()).equals("FUNCTION")){
                      if((identifierList.get(k).getDefCall()).equals("DEFINITION")){
                        definedFUNC = true;
                      }
                    }
                  }
                }
              }
            }else if((identifierList.get(j).getCategory()).equals("VARIABLE")){
              if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                varName = identifierList.get(j).getName();
                for(int k = 0; k < i; k++){
                  if((identifierList.get(k).getName()).equals(varName)){
                    if((identifierList.get(k).getCategory()).equals("VARIABLE")){
                      counterVar++;
                      if((identifierList.get(k).getDefCall()).equals("DEFINITION")){
                        definedVAR = true;
                      }
                    }
                  }
                }
                if(counterVar == 0){
                  find = false;
                }
              }
            }
          }
        }
      }
      if(find == false){
  //      System.out.println("REJECT");
  //      System.exit(1);
        return false;
      }
      if(definedVAR == true && definedFUNC == true){
        return true;
      }else{
  //      System.out.println("REJECT");
  //      System.exit(1);
        return false;
      }
    }

    // Checks to make sure that void functions return nothing, if they have a return statement.
    // If there is no return statement, there is no problem.
    public static void voidFuncCheck(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList,
      String[][] codeGen){
      boolean voidCorrect = false;
      for(int i = 0; i < identifierList.size(); i++){
        String funcName = "";
        int returnCount = 0;
        if((identifierList.get(i).getCategory()).equals("FUNCTION")){
          if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
            if((identifierList.get(i).getType()).equals("void")){
              funcName = identifierList.get(i).getName();
              for(int k = 0; k < tokenList.size(); k++){
                if((tokenList.get(k).getToken()).equals(funcName)){
                  if((tokenList.get(k-1).getToken()).equals("void")){
                    for(int r = k+1; r < tokenList.size(); r++){
                      if((tokenList.get(r).getToken()).equals("{")){
                        for(int p = r+1; p < tokenList.size(); p++){
                          if((tokenList.get(p).getToken()).equals("return")){
                            returnCount++;
                            if((tokenList.get(p+1).getToken()).equals(";")){
                              voidCorrect = true;
                              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                              codeGen[Project4.indexCode][1] = "return";
                              codeGen[Project4.indexCode][2] = "";
                              codeGen[Project4.indexCode][3] = "";
                              codeGen[Project4.indexCode][4] = "";
                              Project4.indexCode++;
                              break;
                            }else{
                              voidCorrect = false;
                //              System.out.println("REJECT");
                //              System.exit(1);
                              break;
                            }
                          }else if(((tokenList.get(p).getToken()).equals("}")) && returnCount == 0){
                            voidCorrect = true;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Checks to make sure that the return statement does not have something complicated in it
    // Like a comma, which indicates returning two things, or an array
    public static void returnSimpleCheck(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList, ArrayList<String> delims){
      boolean simple = true;
      for(int i = 0; i < identifierList.size(); i++){
        String funcName = "";
        int returnCount = 0;
        String functionType = "";
        if((identifierList.get(i).getCategory()).equals("FUNCTION")){
          if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
            funcName = identifierList.get(i).getName();
            functionType = identifierList.get(i).getType();
            for(int k = 0; k < tokenList.size(); k++){
              if((tokenList.get(k).getToken()).equals(funcName)){
                for(int r = k+1; r < tokenList.size(); r++){
                  if((tokenList.get(r).getToken()).equals("{")){
                    for(int p = r+1; p < tokenList.size(); p++){
                      if((tokenList.get(p).getToken()).equals("return")){
                        returnCount++;
                        if((tokenList.get(p+2).getToken()).equals("[")){
                          if(!((tokenList.get(p+3).getNumType()).equals(functionType))){
              //              System.out.println("REJECT");
              //              System.exit(1);
                            break;
                          }else{
                            simple = true;
                            break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Checks to make sure that the parameters of the function call match those of
    // the function definition
    public static boolean paramTypeCheck(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList){
      boolean typesMatch = false;
      int count = 0;
      for(int b = 0; b < identifierList.size(); b++){
        if((identifierList.get(b).getCategory()).equals("FUNCTION")){
          if((identifierList.get(b).getDefCall()).equals("CALL")){
            count++;
          }
        }
      }

      if(count > 0){
        for(int i = 0; i < identifierList.size(); i++){
          String funcName = "";
          ArrayList<Param> paramList = new ArrayList<Param>();
          paramList.clear();
          if((identifierList.get(i).getCategory()).equals("FUNCTION")){
            if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
              funcName = identifierList.get(i).getName();
              int indexCheck = i;
              for(int k = 0; k < identifierList.get(i).getNumOfParams(); k++){
                paramList.add(new Param(funcName, identifierList.get(indexCheck+1).getType(), identifierList.get(indexCheck+1).getVarType()));
                indexCheck++;
              }
              for(int r = 0; r < identifierList.size(); r++){
                if((identifierList.get(r).getCategory()).equals("FUNCTION")){
                  if((identifierList.get(r).getDefCall()).equals("CALL")){
                    if((identifierList.get(r).getName()).equals(funcName)){
                      int numParams = identifierList.get(r).getNumOfParams();
                      int f = r+1;
                      if(((identifierList.get(r).getType()).equals("void")) && ((paramList.get(0).getParamType()).equals("void"))){
                        typesMatch = true;
                      }else{
                        for(int a = 0; a < numParams; a++){
                          if((tokenList.get(identifierList.get(r).getTokenIndex() + 1).getNumType()).equals(paramList.get(a).getParamType())){
                            typesMatch = true;
                          }else{
                            typesMatch = false;
                          }if(f < identifierList.size()){
                            if((identifierList.get(f).getType()).equals(paramList.get(a).getParamType())){
                              if((identifierList.get(f).getVarType()).equals(paramList.get(a).getParamVarType())){
                                typesMatch = true;
                                f++;
                              }else{
                                int index = identifierList.get(f).getTokenIndex() + 1;
                                if((tokenList.get(index).getToken()).equals("[")){
                                  if((tokenList.get(index + 1).getNumType()).equals("int")){
                                    typesMatch = true;
                                    f++;
                                  }else{
                                    typesMatch = false;
                                  }
                                }else{
                                  typesMatch = false;
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }else{
        typesMatch = true;
      }
      if(typesMatch == true){
        return true;
      }else{
  //      System.out.println("REJECT");
        return false;
      }
    }

    // Checks to make sure that an int or float function has a return and that it returns
    // the appropriate type. It even checks if it returns an ID of the appropriate type.
    // If there is no return, then the file is rejected.
    public static void intFloatFuncCheck(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList, String[][] codeGen){
      boolean intCorrect = false;
      boolean floatCorrect = false;
      int countIntFunc = 0;
      int countFloatFunc = 0;
      for(int r = 0; r < identifierList.size(); r++){
        if((identifierList.get(r).getCategory()).equals("FUNCTION")){
          if((identifierList.get(r).getDefCall()).equals("DEFINITION")){
            if(((identifierList.get(r).getType()).equals("int")) || ((identifierList.get(r).getType()).equals("void"))){
              countIntFunc++;
            }else if(((identifierList.get(r).getType()).equals("float")) || ((identifierList.get(r).getType()).equals("void"))){
              countFloatFunc++;
            }
          }
        }
      }
      if(countIntFunc == identifierList.size()){
        floatCorrect = true;
      }else if(countFloatFunc == identifierList.size()){
        intCorrect = true;
      }
      for(int i = 0; i < identifierList.size(); i++){
        String funcName = "";
        String checkToken = "";
        int returnCount = 0;
        if((identifierList.get(i).getCategory()).equals("FUNCTION")){
          if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
            if((identifierList.get(i).getType()).equals("int")){
              funcName = identifierList.get(i).getName();
              for(int k = 0; k < tokenList.size(); k++){
                if((tokenList.get(k).getToken()).equals(funcName)){
                  if((tokenList.get(k-1).getToken()).equals("int")){
                    for(int r = k+1; r < tokenList.size(); r++){
                      if((tokenList.get(r).getToken()).equals("{")){
                        for(int p = r+1; p < tokenList.size(); p++){
                          if((tokenList.get(p).getToken()).equals("return")){
                            returnCount++;
                            if((tokenList.get(p+1).getNumType()).equals("int")){
                              intCorrect = true;
                              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                              codeGen[Project4.indexCode][1] = "return";
                              codeGen[Project4.indexCode][2] = "";
                              codeGen[Project4.indexCode][3] = "";
                              codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.codeVar);
                              Project4.indexCode++;
                              Project4.codeVar++;
                              break;
                            }else if((tokenList.get(p+1).getTokenType()).equals("ID")){
                              checkToken = tokenList.get(p+1).getToken();
                              for(int a = 0; a < identifierList.size(); a++){
                                if((identifierList.get(a).getCategory()).equals("VARIABLE")){
                                  if((identifierList.get(a).getDefCall()).equals("INSTANTIATION")){
                                    if((identifierList.get(a).getName()).equals(checkToken)){
                                      if((identifierList.get(a).getType()).equals("int")){
                                        intCorrect = true;
                                        codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                                        codeGen[Project4.indexCode][1] = "return";
                                        codeGen[Project4.indexCode][2] = "";
                                        codeGen[Project4.indexCode][3] = "";
                                        codeGen[Project4.indexCode][4] = identifierList.get(a).getName();
                                        Project4.indexCode++;
                                        break;
                                      }
                                    }
                                  }
                                }
                              }
                            }else{
                              intCorrect = false;
              //                System.out.println("REJECT");
              //                System.exit(1);
                              break;
                            }
                          }else if(((tokenList.get(p).getToken()).equals("}")) && returnCount == 0){
                            intCorrect = false;
              //              System.out.println("REJECT");
              //              System.exit(1);
                            break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }else if((identifierList.get(i).getType()).equals("float")){
              funcName = identifierList.get(i).getName();
              for(int k = 0; k < tokenList.size(); k++){
                if((tokenList.get(k).getToken()).equals(funcName)){
                  if((tokenList.get(k-1).getToken()).equals("float")){
                    for(int r = k+1; r < tokenList.size(); r++){
                      if((tokenList.get(r).getToken()).equals("{")){
                        for(int p = r+1; p < tokenList.size(); p++){
                          if((tokenList.get(p).getToken()).equals("return")){
                            returnCount++;
                            if((tokenList.get(p+1).getNumType()).equals("float")){
                              floatCorrect = true;
                              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                              codeGen[Project4.indexCode][1] = "return";
                              codeGen[Project4.indexCode][2] = "";
                              codeGen[Project4.indexCode][3] = "";
                              codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.codeVar);
                              Project4.indexCode++;
                              Project4.codeVar++;
                              break;
                            }else if((tokenList.get(p+1).getTokenType()).equals("ID")){
                              checkToken = tokenList.get(p+1).getToken();
                              for(int a = 0; a < identifierList.size(); a++){
                                if((identifierList.get(a).getCategory()).equals("VARIABLE")){
                                  if((identifierList.get(a).getDefCall()).equals("INSTANTIATION")){
                                    if((identifierList.get(a).getName()).equals(checkToken)){
                                      if((identifierList.get(a).getType()).equals("float")){
                                        intCorrect = true;
                                        codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                                        codeGen[Project4.indexCode][1] = "return";
                                        codeGen[Project4.indexCode][2] = "";
                                        codeGen[Project4.indexCode][3] = "";
                                        codeGen[Project4.indexCode][4] = identifierList.get(a).getName();
                                        Project4.indexCode++;
                                        break;
                                      }
                                    }
                                  }
                                }
                              }
                            }else{
                              floatCorrect = false;
              //                System.out.println("REJECT");
              //                System.exit(1);
                              break;
                            }
                          }else if(((tokenList.get(p).getToken()).equals("}")) && returnCount == 0){
                            floatCorrect = false;
              //              System.out.println("REJECT");
              //              System.exit(1);
                            break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Checks that the operators on both sides of an operand match in type
    public static void operatorOperandCheck(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList,
      String[][] codeGen){
      boolean check = false;
      int count = 0;

      for(int i = 0; i < tokenList.size(); i++){
        if(((tokenList.get(i).getToken()).equals("+")) || ((tokenList.get(i).getToken()).equals("-")) || ((tokenList.get(i).getToken()).equals("/")) || ((tokenList.get(i).getToken()).equals("*"))){
          count++;
        }
      }

      if(count == 0){
        check = true;
      }

      for(int i = 0; i < tokenList.size(); i++){
        if(((tokenList.get(i).getToken()).equals("+")) || ((tokenList.get(i).getToken()).equals("-")) || ((tokenList.get(i).getToken()).equals("/")) || ((tokenList.get(i).getToken()).equals("*"))){
          if((tokenList.get(i-1).getToken()).equals("]")){
            if(((tokenList.get(i-4).getNumType()).equals((tokenList.get(i+1).getNumType())))){
              check = true;
              String token1 = tokenList.get(i-4).getToken().trim();
              String token2 = tokenList.get(i+1).getToken().trim();
              String token1Search = "", token1Var = "";
              String token2Search = "", token2Var = "";;
              for(int j = 0; j < identifierList.size(); j++){
                if((identifierList.get(j).getName()).equals(token1)){
                  if((identifierList.get(j).getCategory()).equals("VARIABLE")){
                    if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                      token1Var = identifierList.get(j).getVarType();
                    }
                  }
                }else if((identifierList.get(j).getName()).equals(token2)){
                  if((identifierList.get(j).getCategory()).equals("VARIABLE")){
                    if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                      token2Var = identifierList.get(j).getVarType();
                    }
                  }
                }
              }
              if((tokenList.get(i).getToken()).equals("+")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "add";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.codeVar);
                Project4.indexCode++;
                Project4.codeVar++;
              }else if((tokenList.get(i).getToken()).equals("-")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "sub";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
                Project4.indexCode++;
                Project4.codeVar++;
              }else if((tokenList.get(i).getToken()).equals("/")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "div";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
                Project4.indexCode++;
                Project4.codeVar++;
              }else if((tokenList.get(i).getToken()).equals("*")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "mult";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
                Project4.indexCode++;
                Project4.codeVar++;
              }
              if(token1Var.equals(token2Var)){
                check = true;
              }else{
                check = false;
    //            System.out.println("REJECT");
    //            System.exit(1);
              }
            }else{
              check = false;
    //          System.out.println("REJECT");
    //          System.exit(1);
            }
          }else if((tokenList.get(i-1).getToken()).equals(")")){
            if(((tokenList.get(i-2).getNumType()).equals((tokenList.get(i+1).getNumType())))){
              check = true;
              String token1 = tokenList.get(i-2).getToken().trim();
              String token2 = tokenList.get(i+1).getToken().trim();
              String token1Search = "", token1Var = "";
              String token2Search = "", token2Var = "";;
              for(int j = 0; j < identifierList.size(); j++){
                if((identifierList.get(j).getName()).equals(token1)){
                  if((identifierList.get(j).getCategory()).equals("VARIABLE")){
                    if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                      token1Var = identifierList.get(j).getVarType();
                    }
                  }
                }else if((identifierList.get(j).getName()).equals(token2)){
                  if((identifierList.get(j).getCategory()).equals("VARIABLE")){
                    if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                      token2Var = identifierList.get(j).getVarType();
                    }
                  }
                }
              }
              if((tokenList.get(i).getToken()).equals("+")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "add";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.codeVar);
                Project4.indexCode++;
                Project4.codeVar++;
              }else if((tokenList.get(i).getToken()).equals("-")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "sub";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
                Project4.indexCode++;
                Project4.codeVar++;
              }else if((tokenList.get(i).getToken()).equals("/")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "div";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
                Project4.indexCode++;
                Project4.codeVar++;
              }else if((tokenList.get(i).getToken()).equals("*")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "mult";
                codeGen[Project4.indexCode][2] = token1;
                codeGen[Project4.indexCode][3] = token2;
                codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
                Project4.indexCode++;
                Project4.codeVar++;
              }
              if(token1Var.equals(token2Var)){
                check = true;
              }else{
                check = false;
    //            System.out.println("REJECT");
    //            System.exit(1);
              }
            }else{
              check = false;
    //          System.out.println("REJECT");
    //          System.exit(1);
            }
          }else if(((tokenList.get(i-1).getNumType()).equals((tokenList.get(i+1).getNumType())))){
            check = true;
            String token1 = tokenList.get(i-1).getToken().trim();
            String token2 = tokenList.get(i+1).getToken().trim();
            String token1Search = "", token1Var = "";
            String token2Search = "", token2Var = "";;
            for(int j = 0; j < identifierList.size(); j++){
              if((identifierList.get(j).getName()).equals(token1)){
                if((identifierList.get(j).getCategory()).equals("VARIABLE")){
                  if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                    token1Var = identifierList.get(j).getVarType();
                  }
                }
              }else if((identifierList.get(j).getName()).equals(token2)){
                if((identifierList.get(j).getCategory()).equals("VARIABLE")){
                  if((identifierList.get(j).getDefCall()).equals("INSTANTIATION")){
                    token2Var = identifierList.get(j).getVarType();
                  }
                }
              }
            }
            if((tokenList.get(i).getToken()).equals("+")){
              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
              codeGen[Project4.indexCode][1] = "add";
              codeGen[Project4.indexCode][2] = token1;
              codeGen[Project4.indexCode][3] = token2;
              codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.codeVar);
              Project4.indexCode++;
              Project4.codeVar++;
            }else if((tokenList.get(i).getToken()).equals("-")){
              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
              codeGen[Project4.indexCode][1] = "sub";
              codeGen[Project4.indexCode][2] = token1;
              codeGen[Project4.indexCode][3] = token2;
              codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
              Project4.indexCode++;
              Project4.codeVar++;
            }else if((tokenList.get(i).getToken()).equals("/")){
              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
              codeGen[Project4.indexCode][1] = "div";
              codeGen[Project4.indexCode][2] = token1;
              codeGen[Project4.indexCode][3] = token2;
              codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
              Project4.indexCode++;
              Project4.codeVar++;
            }else if((tokenList.get(i).getToken()).equals("*")){
              codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
              codeGen[Project4.indexCode][1] = "mult";
              codeGen[Project4.indexCode][2] = token1;
              codeGen[Project4.indexCode][3] = token2;
              codeGen[Project4.indexCode][4] = "t"+String.valueOf(Project4.indexCode);
              Project4.indexCode++;
              Project4.codeVar++;
            }
            if(token1Var.equals(token2Var)){
              check = true;
            }else{
              check = false;
  //            System.out.println("REJECT");
  //            System.exit(1);
            }
          }else{
            check = false;
  //          System.out.println("REJECT");
  //          System.exit(1);
          }
        }else if(((tokenList.get(i).getToken()).equals("<=")) || ((tokenList.get(i).getToken()).equals("<")) || ((tokenList.get(i).getToken()).equals(">"))
            || ((tokenList.get(i).getToken()).equals(">="))|| ((tokenList.get(i).getToken()).equals("=="))|| ((tokenList.get(i).getToken()).equals("!="))){
              if((tokenList.get(i).getToken()).equals("<=")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "BRLEQ";
                codeGen[Project4.indexCode][2] = "" /*val here */;
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(Project4.indexCode + 2);
                Project4.indexCode++;

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "block";
                codeGen[Project4.indexCode][2] = "";
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = "";
                Project4.indexCode++;
              }else if((tokenList.get(i).getToken()).equals("<")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "BRL";
                codeGen[Project4.indexCode][2] = "" /*val here */;
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(Project4.indexCode + 2);
                Project4.indexCode++;

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "block";
                codeGen[Project4.indexCode][2] = "";
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = "";
                Project4.indexCode++;
              }else if((tokenList.get(i).getToken()).equals(">")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "BRG";
                codeGen[Project4.indexCode][2] = "" /*val here */;
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(Project4.indexCode + 2);
                Project4.indexCode++;

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "block";
                codeGen[Project4.indexCode][2] = "";
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = "";
                Project4.indexCode++;
              }else if((tokenList.get(i).getToken()).equals(">=")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "BRGEQ";
                codeGen[Project4.indexCode][2] = "" /*val here */;
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(Project4.indexCode + 2);
                Project4.indexCode++;

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "block";
                codeGen[Project4.indexCode][2] = "";
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = "";
                Project4.indexCode++;
              }else if((tokenList.get(i).getToken()).equals("==")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "BREQ";
                codeGen[Project4.indexCode][2] = "" /*val here */;
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(Project4.indexCode + 2);
                Project4.indexCode++;

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "block";
                codeGen[Project4.indexCode][2] = "";
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = "";
                Project4.indexCode++;
              }else if((tokenList.get(i).getToken()).equals("!=")){
                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "BRNEQ";
                codeGen[Project4.indexCode][2] = "" /*val here */;
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = String.valueOf(Project4.indexCode + 2);
                Project4.indexCode++;

                codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                codeGen[Project4.indexCode][1] = "block";
                codeGen[Project4.indexCode][2] = "";
                codeGen[Project4.indexCode][3] = "";
                codeGen[Project4.indexCode][4] = "";
                Project4.indexCode++;
              }
            }
          }
        }


    // Checks that the value on the right matches the type on the left
    public static void operatorOperandAssignmentCheck(LinkedList<Identifier> identifierList, LinkedList<Token> tokenList,
      String[][] codeGen){
      boolean typeValMatch = true;
      int varCount = 0;
      for(int i = 0; i < identifierList.size(); i++){
        String varName = "";
        int scope = 0;
        if((identifierList.get(i).getCategory()).equals("VARIABLE")){
          varName = identifierList.get(i).getName();
          scope = identifierList.get(i).getScope();
          for(int a = 0; a < tokenList.size(); a++){
            if((tokenList.get(a).getToken()).equals(varName)){
              if(tokenList.get(a).getDepth() == scope){
                if((tokenList.get(a+1).getToken()).equals("=")){
                  if((tokenList.get(a+2).getNumType()).equals(identifierList.get(i).getType())){
                    varCount++;
                    typeValMatch = true;
                    a = a+3;
                    codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                    codeGen[Project4.indexCode][1] = "assign";
                    codeGen[Project4.indexCode][2] = "t"+String.valueOf(Project4.codeVar);
                    codeGen[Project4.indexCode][3] = "";
                    codeGen[Project4.indexCode][4] = varName;
                    Project4.indexCode++;
                    Project4.codeVar++;
                    break;
                  }else if((tokenList.get(a+3).getNumType()).equals(identifierList.get(i).getType())){
                    varCount++;
                    typeValMatch = true;
                    a = a+3;
                    codeGen[Project4.indexCode][0] = String.valueOf(Project4.indexCode);
                    codeGen[Project4.indexCode][1] = "assign";
                    codeGen[Project4.indexCode][2] = "t"+String.valueOf(Project4.codeVar);
                    codeGen[Project4.indexCode][3] = "";
                    codeGen[Project4.indexCode][4] = varName;
                    Project4.indexCode++;
                    Project4.codeVar++;
                    break;
                  }else{
                    typeValMatch = false;
    //                System.out.println("REJECT");
    //                System.exit(1);
                  }
                }
              }
            }
          }
        }
        if(varCount > 0){
          break;
        }
      }
    }

    // Checks to make sure that a variable is only declared once
    public static void varDeclaredOnceCheck(LinkedList<Identifier> identifierList){
      boolean check = true;
      for(int i = 0; i < identifierList.size(); i++){
        String varName = "";
        int scope = 0;
        String funcName = "";
        int index = 0;
        String typeVar = "";
        if((identifierList.get(i).getCategory()).equals("VARIABLE")){
          if((identifierList.get(i).getDefCall()).equals("DEFINITION")){
            varName = identifierList.get(i).getName();
            scope = identifierList.get(i).getScope();
            funcName = identifierList.get(i).getFunctionNameCurrent();
            index = identifierList.get(i).getTokenIndex();
            typeVar = identifierList.get(i).getVarType();
            for(int a = 0; a < identifierList.size(); a++){
              if((identifierList.get(a).getCategory()).equals("VARIABLE")){
                if((identifierList.get(a).getDefCall()).equals("DEFINITION")){
                  if((identifierList.get(a).getName()).equals(varName)){
                    if(identifierList.get(a).getScope() == scope){
                      if(!(identifierList.get(a).getTokenIndex() == index)){
                        if((identifierList.get(a).getFunctionNameCurrent()).equals("GLOBAL")){
                          check = false;
    //                      System.out.println("REJECT");
    //                      System.exit(1);
                        }else if(((identifierList.get(a).getFunctionNameCurrent()).equals(funcName)) && (!(funcName.equals("GLOBAL")))){
                          check = false;
    //                      System.out.println("REJECT");
    //                      System.exit(1);
                        }else{
                          check = true;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    public static void createExpression(LinkedList<Token> tokenList){
       for(int i = 0; i < tokenList.size(); i++){
         String expression = "";
         if((tokenList.get(i).getToken()).equals("=")){
           for(int h = 0; h < tokenList.size(); h++){
             if((tokenList.get(h).getToken()).equals(";")){
               System.out.println("hi: " + expression);
            //   createPostfix(expression);
            //   break;
             }else{
               expression = expression + tokenList.get(h).getToken();
             }
           }
         }
       }
     }


    static class Token{
      // The values on each line
      private String tokenType;     // ID, DELIM,...
      private String numType;       // int, float
      private String token;         // the actual token
      private int depth;            // the scope of the token
      private Token next;
      private Token previous;

      // Token constructors
      public Token(){
      }

      public Token(String tokenType, String numType, String token, int depth){
        this.tokenType = tokenType;
        this.numType = numType;
        this.token = token;
        this.depth = depth;
      }

      //Methods for the Token Class

      // Get Methods for Token Class
      public String getTokenType(){
        return tokenType;
      }

      public String getNumType(){
        return numType;
      }

      public String getToken(){
        return token;
      }

      public int getDepth(){
        return depth;
      }

      // Set Methods for Token Class
      public void setTokenType(String newTokenType){
        this.tokenType = newTokenType;
      }

      public void setNumType(String newNumType){
        this.numType = newNumType;
      }

      public void setToken(String newToken){
        this.token = newToken;
      }

      public void setDepth(int newDepth){
        this.depth = newDepth;
      }

      public boolean equals(Object obj){
         return true;
      }

      public void displayTokenAll(){
         System.out.println("Type: " + tokenType + "      Num Type: " + numType + "    Token: " +  token + "    Scope: " + depth);
      }

      public String toString(){
        return "\n" + getTokenType() + " " + getNumType() + "  "+ getToken() + "  " + getDepth() + "\n";
      }
    }

  static class Identifier{
     // The values on each line
     private String category;             // Variable or Function
     private String type;                 // int, float, void
     private String name;                 // The name of the Identifier
     private int scope;                   // The scope of the Identifier
     private int numOfParams;             // Number of params the function has
     private String defCall;              // definition, instantiation, or call
     private String varType;              // if its an array
     private int arraySize;               // length of the array
     private String functionNameCurrent;  // name of function its in
     private int tokenIndex;              // index of id in tokenList
     private Identifier next;
     private Identifier previous;

     // Identifier constructors
     public Identifier(){
     }

     public Identifier(String category, String type, String name, int scope,
      int numOfParams, String defCall, String varType, int arraySize, String functionNameCurrent,
      int tokenIndex){
       this.category = category;
       this.type = type;
       this.name = name;
       this.scope = scope;
       this.numOfParams = numOfParams;
       this.defCall = defCall;
       this.varType = varType;
       this.arraySize = arraySize;
       this.functionNameCurrent = functionNameCurrent;
       this.tokenIndex = tokenIndex;
     }

     //Methods for the Identifier Class

     // Get Methods for Identifier Class
     public String getCategory(){
       return category;
     }

     public String getType(){
       return type;
     }

     public String getName(){
       return name;
     }

     public int getScope(){
       return scope;
     }

     public int getNumOfParams(){
       return numOfParams;
     }

     public String getDefCall(){
       return defCall;
     }

     public String getVarType(){
       return varType;
     }

     public int getArraySize(){
       return arraySize;
     }

     public String getFunctionNameCurrent(){
       return functionNameCurrent;
     }

     public int getTokenIndex(){
       return tokenIndex;
     }

     // Set Methods for Identifier Class
     public void setCategory(String newCategory){
       this.category = newCategory;
     }

     public void setType(String newType){
       this.type = newType;
     }

     public void setName(String newName){
       this.name = newName;
     }

     public void setScope(int newScope){
       this.scope = newScope;
     }

     public void setNumOfParams(int newNumOfParams){
       this.numOfParams = newNumOfParams;
     }

     public void setDefCall(String newDefCall){
       this.defCall = newDefCall;
     }

     public void setVarType(String newVarType){
       this.varType = newVarType;
     }

     public void setArraySize(int newArraySize){
       this.arraySize = newArraySize;
     }

     public void setFunctionNameCurrent(String newFunctionNameCurrent){
       this.functionNameCurrent = newFunctionNameCurrent;
     }

     public void setTokenIndex(int newTokenIndex){
       this.tokenIndex = newTokenIndex;
     }

     public boolean equals(Object obj){
        return true;
     }

     public void displayIdentifierAll(){
        System.out.println("Category: " + category + "    Type: " +  type + "    Name: " + name + "        Scope: " + scope + "     Number of Params: " + numOfParams +
        "       DefCall: " + defCall + "       VarType: " + varType + "       ArraySize: " + arraySize + "       Function Name: " + functionNameCurrent +
        "       Token Index: " + tokenIndex);
     }

     public String toString(){
       return "\n" + getCategory() + " " + getType() + " " + getName() + "  " + getScope() + "   " + getNumOfParams() + "      " + getDefCall() + "         " + getVarType() +
        "   " + getArraySize() + "  " + getFunctionNameCurrent() + "  " + getTokenIndex() + "\n";
     }
  }

  static class Param{
    // The values on each line
    private String functionName;          // name of the function
    private String paramType;             // the type of the param, int, float, void
    private String paramVarType;          // if the param is an array
    private Token next;
    private Token previous;

    // Param constructors
    public Param(){
    }

    public Param(String functionName, String paramType, String paramVarType){
      this.functionName = functionName;
      this.paramType = paramType;
      this.paramVarType = paramVarType;
    }

    //Methods for the Param Class

    // Get Methods for Param Class
    public String getFunctionName(){
      return functionName;
    }

    public String getParamType(){
      return paramType;
    }

    public String getParamVarType(){
      return paramVarType;
    }

    // Set Methods for Param Class
    public void setFunctionName(String newFunctionName){
      this.functionName = newFunctionName;
    }

    public void setParamType(String newParamType){
      this.paramType = newParamType;
    }

    public void setParamVarType(String newParamVarType){
      this.paramVarType = newParamVarType;
    }

    public boolean equals(Object obj){
       return true;
    }

    public void displayTokenAll(){
       System.out.println("Function Name: " + functionName + "      Param Type: " + paramType + "    Param Var Type: " +  paramVarType);
    }

    public String toString(){
      return "\n" + getFunctionName() + " " + getParamType() + "  "+ getParamVarType() + "\n";
    }
  }
}
