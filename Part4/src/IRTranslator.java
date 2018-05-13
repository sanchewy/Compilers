import java.util.ArrayList;
import java.util.Iterator;
import java.util.EmptyStackException;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

public class IRTranslator {

    int register = 1;
    int count = 0;

    public void IRtoAss(ArrayList<String> code, LinkedHashMap map){
        Iterator it = code.iterator();
        Set <String> vars = new HashSet<String>();
        Set key = map.keySet();
        Iterator scope = key.iterator();
        while(scope.hasNext())
        {

            String currScope = (String)scope.next();

            SymbolTable varis = (SymbolTable)map.get(currScope);
            // Iterator stit = varis.iterator();
            System.out.println(";"+currScope);
            Set vals = varis.getvals();
            Iterator stit = vals.iterator();

            while(stit.hasNext()){
                String curr = (String)stit.next();
                String [] c = curr.split(" ");

                if(c[3].equals("STRING")){
                    vars.add(c[1]);
                    System.out.println("str "+ c[1]+ " "+ c[5]);


                }

                else{
                    vars.add(c[1]);
                    System.out.println( " var "+ c[1]);
                }


            }
        }


        while(it.hasNext()) {

            String curr = (String)it.next();
            String [] c = curr.split(" ");
            System.out.println(curr);
            //System.out.println(c[0]);


            if(c[0].equals(";STOREI")){
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    register=Integer.parseInt(regval[1]);

                    System.out.println("move "+ "r"+register+ " "+ c[2]);

                }
                else if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    register=Integer.parseInt(regval[1]);

                    System.out.println("move "+ c[1]+ " r"+register);

                }

            }
            else if(c[0].equals(";STOREF")){
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    register=Integer.parseInt(regval[1]);

                    System.out.println("move "+ "r"+register+ " "+ c[2]);

                }
                else if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    register=Integer.parseInt(regval[1]);

                    System.out.println("move "+ c[1]+ " r"+register);

                } else if (c[3].charAt(0)=='$') { //TODO fix placing one var in another
                    String [] regval = c[3].split("T");
                    register=Integer.parseInt(regval[1]);

                    System.out.println("move " + c[1] + " r" + register);
                    System.out.println("move r" +register + " " + c[2]);
                }
            }
            else if(c[0].equals(";WRITEI")){
                System.out.println("sys writei "+ c[1]);

            }
            else if(c[0].equals(";WRITES")){
                System.out.println("sys writes "+ c[1]);

            }
            else if(c[0].equals(";WRITEF")){
                System.out.println("sys writer "+ c[1]);

            }
            else if(c[0].equals(";READI")){
                System.out.println("sys readi "+ c[1]);

            }

            else if(c[0].equals(";ADDI")&& c.length>3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("addi "+ b+ " "+d);
                //  System.out.println("move "+d+ " "+a);


            }
            else if(c[0].equals(";ADDF") && c.length>3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("addr "+ b+ " "+d);
                // System.out.println("move "+d+ " "+a);


            }
            else if(c[0].equals(";MULTI") &&c.length>3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("muli "+ b+ " "+d);
                //  System.out.println("move "+d+ " "+a);



            }
            else if(c[0].equals(";MULTF") &&c.length>3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("mulr "+ b+ " "+d);
                //  System.out.println("move "+d+ " "+a);



            }
            else if(c[0].equals(";SUBI") && c.length >3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("subi "+ b+ " "+d);
                //  System.out.println("move "+d+ " "+a);


            }
            else if(c[0].equals(";SUBF") && c.length >3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("subr "+ b+ " "+d);
                //  System.out.println("move "+d+ " "+a);


            }
            else if(c[0].equals(";DIVF") && c.length >3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("divr "+ b+ " "+d);
                //     System.out.println("move "+d+ " "+a);

            }
            else if(c[0].equals(";DIVI") && c.length >3){
                String a= c[1];
                String b= c[2];
                String d= c[3];
                if(c[1].charAt(0)=='$'){
                    String [] regval = c[1].split("T");
                    //System.out.println(regval);
                    a="r"+regval[1];



                }
                if(c[2].charAt(0)=='$'){
                    String [] regval = c[2].split("T");
                    b="r"+regval[1];



                }
                if(c[3].charAt(0)=='$'){
                    String [] regval = c[3].split("T");
                    d ="r"+regval[1];
                }
                System.out.println("move "+ a+" "+d);

                System.out.println("divi "+ b+ " "+d);
                //   System.out.println("move "+d+ " "+a);

            }
            else if(c[0].equals(";RET") && count==0){
                count=1;
                System.out.println("sys halt");


            }
            else if(c[0].contentEquals(";CMPI")){
                String a=c[2];
                if(c[2].charAt(0)=='$'){
                    String[] regval = c[2].split("T");
                    a="r"+regval[1];
                }
                System.out.println("cmpi " + c[1] + " " + a);
            } else if(c[0].contentEquals(";JMP")){
                System.out.println("jmp " + c[1]);
            }
            else if (c[0].contentEquals(";LABEL")){
                System.out.println("label " + c[1]);
            } else if (c[0].contentEquals(";JGE")){
                System.out.println("jge " + c[1]);
            } else if (c[0].contentEquals(";JLE")){
                System.out.println("jle " + c[1]);
            } else if (c[0].contentEquals(";JEQ")){
                System.out.println("jeq " + c[1]);
            } else if (c[0].contentEquals(";JNE")){
                System.out.println("jne " + c[1]);
            } else if (c[0].contentEquals(";JGT")){
                System.out.println("jgt " + c[1]);
            } else if (c[0].contentEquals(";JLT")){
                System.out.println("jlt " + c[1]);
            }
        }
    }

}