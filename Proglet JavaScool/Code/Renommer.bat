cd C:\ 
TYPE *.jvs >> *.txt

echo import lejos.nxt.*;> exemple.java 

echo public class exemple{ >> exemple.java 
echo public static void main (String[] aArg) throws Exception { >> exemple.java
echo { >> exemple.java

TYPE exemple.jvs >> exemple.java

echo Thread.sleep(10000); >> exemple.java 


echo } >> exemple.java
echo } >> exemple.java


rename exemple.java exemple.bat

Copy exemple.bat FIC.TRA

Find /V /I "import lejos.nxt.Motor" < FIC.TRA > exemple.bat

Del FIC.TRA

Copy exemple.bat FIC.TRA

Find /V /I "void main()" < FIC.TRA > exemple.bat

Del FIC.TRA

Copy exemple.bat FIC.TRA

Find /V /I "upload();" < FIC.TRA > exemple.bat

Del FIC.TRA

rename exemple.bat exemple.java

exit
