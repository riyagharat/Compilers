# Compilers

# Project 1

Project 1 Compiler ---- Due 2/2/17 Thursday at 11:59 PM (nearly midnight)

Your project is to use the grammar definition in the appendix
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
4) multiple line comments are designated by /* followed by */ in 
   a match up fashion for the nesting.
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

Sample input:
/**/          /*/* */   */
/*/*/****This**********/*/    */
/**************/
/*************************
i = 333;        ******************/       */

iiii = 3@33;

int g 4 cd (int u, int v)      {
if(v == >= 0) return/*a comment*/ u;
else ret_urn gcd(vxxxxxxvvvvv, u-u/v*v);
       /* u-u/v*v == u mod v*/
!   
}

return void while       void main()


Sample output:
INPUT: /**/          /*/* */   */
INPUT: /*/*/****This**********/*/    */
INPUT: /**************/
INPUT: /*************************
INPUT: i = 333;        ******************/       */
*  
/  
INPUT: iiii = 3@33;
ID: iiii 
=
NUM: 3
Error: @33
;

INPUT: int g 4 cd (int u, int v)      {
keyword: int
ID: g
NUM: 4
ID: cd
(
keyword: int
ID: u
,
keyword: int
ID: v
)
{

INPUT: if(v == >= 0) return/*a comment*/ u;
keyword: if
(
ID: v
==
>=
NUM: 0
)
keyword: return
ID: u
;

INPUT: else ret_urn gcd(vxxxxxxvvvvv, u-u/v*v);
keyword: else
ID: ret
Error: _urn
ID: gcd
(
ID: vxxxxxxvvvvv
,
ID: u
-
ID: u
/
ID: v
*
 ID: v
)
;
INPUT: /* u-u/v*v == u mod v*/

INPUT: !   
Error: !
INPUT: }
}


INPUT: return void while       void main()
keyword: return
keyword: void
keyword: while
keyword: void
ID: main
(
)


Note: this example does not print the required symbol table, nor does
it demonstrate nesting.

Follow the general guidelines in the Syllabus for project construction 
and grading.




# Project 2

Project 2 Compiler ---- Due 3/2/17 Thursday at 11:59 PM (nearly midnight)

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
simply report such and stop the program.

# Project 3

Project 3 Semantics due 3/30/17 Thursday 11:59 PM (nearly midnight)

Project 3 is the construction of semantic analyzer. You are to include 
in your parser appropriate checking not included in the grammar, but 
defined by the language.

This is going to be the test of the quality of your symbol
table implemented during parsing. You are to determine and
implement appropriate checks as discussed.

Your project should be shar'd containing a makefile, source file,
doc file, and typescript (showing your testing). The makefile file
should be invoked with "make" creating an executable of p3. Your
project will be invoked with p3 fn where fn is the data file to be
analyzed. p3 is the executable resulting from the make command 
or is a script that executes your project. BE SURE TO PROVIDE BOTH A
MAKEFILE AND A P3 EXECUTABLE SCRIPT FOR YOUR PROJECT. Also, be sure
to test the integrity of your shar.

This project must be complete in that the lexical analyzer must be
included to create the tokens required by the parser and
semantic analyzer.

Your project should report on a single line without any additional
characters

ACCEPT

or 

REJECT

upon completion of the analysis.

Note that turnin will report the 2 day late date, if the project
is submitted on this date a penalty will be assessed.

Ensure your shar is properly constructed. 

Use turnin for submission as

turnin fn ree4620_3

where fn is the shar'd file of your complete project.

--------------------------

The following represents a set of tests that might be considered. The
list is not required, nor is it complete, but may be used as a goal
for semantic analysis.

functions declared int or float  must have a return value of the
   correct type.
void functions may or may not have a return, but must not return a
   value.
parameters and arguments agree in number
parameters and arguments agree in type
operand agreement
operand/operator agreement
array index agreement
variable declaration (all variables must be declared ... scope)
variable declaration (all variables declared once ... scope)
void functions cannot have a return value
each program must have one main function
return only simple structures
id's should not be type void
each function should be defined (actually a linker error)



# Project 4

Project 4 Due 4/13/17 Thursday 11:59 PM (nearly midnight)

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

1	func		main		void		0
2  alloc 	 			40			x
3	alloc 	 			4			y
4	disp		x			20			_t0
5  add   	_t0		2			_t1
6  mult     _t1      y        _t2
7	asign		_t2					y
8	end		func		main


# Project 5


Project 5  Due 4/20/17 Thursday 11:59 PM (nearly midnight)

This is a small assignment designed to give you experience
using LEX (flex)  and YACC (bison).

For this exercise you are to generate a LEX (.l) file to
recognize tokens that are to be input to the parser (.y). Use
YACC to recognize (or report) errors for the strings of the
following grammar that generates sql queries. 

-------------------------------------------------------------
The following is a grammar for SQL syntax. 

start 
	::= expression

expression
	::= one-relation-expression | two-relation-expression

one-relation-expression
	::= renaming | restriction | projection

renaming 
	::= term RENAME attribute AS attribute

term 
	::= relation | ( expression )

restriction
	::= term WHERE comparison

projection 
	::= term | term [ attribute-commalist ]

attribute-commalist
	::= attribute | attribute , attribute-commalist

two-relation-expression
	::= projection binary-operation expression

binary-operation
	::= UNION | INTERSECT | MINUS | TIMES | JOIN | DIVIDEBY

comparison
	::= attribute compare number

compare
	::= < | > | <= | >= | = | <>

number
	::= val | val number

val 
	::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

attribute 
	::= CNO | CITY | CNAME | SNO | PNO | TQTY | 
		  SNAME | QUOTA | PNAME | COST | AVQTY |
		  S# | STATUS | P# | COLOR | WEIGHT | QTY

relation 
	::= S | P | SP | PRDCT | CUST | ORDERS

-----------------------------------------------------------

Shar the .l file (for lex), the .y (for yacc) file, test
files, documentation and makefile only (no y.tab.c or lex.yy.c file).
I should type "make" to cause the program to compile
all appropriate portions (lex fn.l and yacc fn.y and cc
fn.c ...) to an executable called p5.

Program output should be one of two messages "ACCEPT"
or "REJECT".

Use turnin fn ree4620_5

Your project will be invoked with p5 < test_file
