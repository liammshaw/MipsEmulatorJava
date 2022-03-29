Lab2:
	javac *.java

testing:
	java lab3.java test1.asm  test1script.txt > test1.output
	java lab3.java test2.asm  test2script.txt > test2.output
	java lab3.java test3.asm  test3script.txt > test3.output
	diff -w -B test1.out test1.output
	diff -w -B test2.out test2.output
	diff -w -B test3.out test3.output