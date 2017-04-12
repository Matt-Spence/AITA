/**
 * Created by Matth_000 on 3/10/2017.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/*
 * Example usage
 *
 * String normal = "";
 * File test = new File("src/Normalizer.java");
 * normal = Normalizer.normalize(test);
 * System.out.println(normal);
 *
 */
public class Normalizer {
    public static String normalize(String str){
        str = strip(str);
        str = strip(str);
        str = strip(str);//third time's the charm
        str = fill(str);

        return str;
    }

    public static String normalize(File f){
        Scanner scan = null;
        try {
            scan = new Scanner(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        String str = "";
        while(scan.hasNextLine()){
            str += scan.nextLine()+"\n";
        }
        scan.close();
        return normalize(str);

    }

    private static String strip(String str){
        String r = "";
        //str = str.replaceAll("\t", "");
        Scanner scan = new Scanner(str);
        boolean blockComment = false;
        boolean doubleQuotes = false;
        boolean singleQuotes = false;

        while(scan.hasNextLine()){
            String t = scan.nextLine()+"\n";
            if(t.equals("\n")) continue;

            for(int i = 0;i<t.length();i++){
                char c = t.charAt(i);

                if(c == '\n'){
                    if(!(""+t.charAt(i-1)).matches("[\\Q{}[];()?:+=-/*&|!~^<>,\\E]"))
                        r+=" ";

                    continue;
                }
                if(blockComment){
                    if(c=='*' && t.charAt(i+1)=='/'){
                        blockComment = false;
                        i++;
                        r+=" ";
                    }
                    continue;
                }
                if(doubleQuotes){
                    r += c;
                    if(c!='\\' && t.charAt(i+1)=='"'){
                        doubleQuotes = false;
                        r += "\"";
                        i++;
                    }
                    continue;
                }
                if(singleQuotes){
                    r += c;
                    if(c=='\'' && t.charAt(i+1)!='\''){
                        singleQuotes = false;
                        //r += "'";
                        //i++;
                    }
                    continue;
                }

                //probably redundant
                //if(!doubleQuotes && !blockComment && !singleQuotes){
                if(c == '/'){
                    if(t.charAt(i+1)=='/') break;
                    if(t.charAt(i+1)=='*'){
                        blockComment = true;
                        i++;
                        continue;
                    }
                }
                else if(c == '"'){
                    doubleQuotes = true;
                    i--;
                    continue;
                }
                else if(c == '\''){
                    singleQuotes = true;
                    //i--;
                    r+=c;
                    continue;
                }
                if(c == ' ' && ((""+t.charAt(i-1)).matches("[\\Q{}[];()?:+=-/*&|!~^<>,\\E]") || (""+t.charAt(i+1)).matches("[\\Q{}[];()?:+=-/*&|!~^<>,\\E]")))
                    continue;
                if(c == '\t'){
                    r+=" ";
                    continue;
                }

                if( !((c == ' ' && t.charAt(i+1) == ' ') || (c == ';' && t.charAt(i+1) == ';')) )
                    r+=c;
                //}

            }//end for

        }

        //should be unnecessary
        //r = r.replaceAll("\n", " ");
        scan.close();
        return r;
    }

    private static String fill(String str){

        String r = "";

        boolean doubleQuotes = false;
        boolean singleQuotes = false;
        boolean header = false;
        int headerPars = 0;
        int indent = 0;
        boolean lookForBracket = false;
        int tempIndents = 0;

        for(int i = 0;i<str.length();i++){
            char c = str.charAt(i);

            //checks bools
            if(doubleQuotes){
                r += c;
                if(c!='\\' && str.charAt(i+1)=='"'){
                    doubleQuotes = false;
                    r += "\"";
                    i++;
                }
                continue;
            }
            if(singleQuotes){

                r += c;
                if(c=='\'' && str.charAt(i+1)!='\''){
                    //System.out.println("End Single Quotes");
                    singleQuotes = false;
                    //r += "'";
                    //i++;
                }
                continue;
            }
            else if(header){
                //if(c=='(' || c==')')System.out.println("Found "+c);
                if(c=='(')headerPars++;
                if(c==')')headerPars--;
                if(headerPars == 0){
                    header = false;
                    if(str.charAt(i+1)==';'){
                        r+=");\n"+tabs(indent+tempIndents);
                        i++;
                    }
                    else if(str.charAt(i+1)=='{'){
                        r+=")\n"+tabs(indent+tempIndents)+"{\n"+tabs(++indent+tempIndents);
                        i++;
                    }
                    else{
                        r+=")\n"+tabs(++indent+tempIndents);
                        lookForBracket = true;
                    }
                    continue;
                }
                //r+=c;
                //continue;
            }

            if(lookForBracket && (c+"").matches("\\W")){
                lookForBracket = false;
                indent--;
                tempIndents++;
            }

            //test
            //if(c=='(') System.out.println(i);

            //sets bools
            if(c=='{' || c=='}'){
                if(lookForBracket){
                    lookForBracket = false;
                    r = r.substring(0,r.length()-1);
                    indent--;
                }
                if(r.charAt(r.length()-1) != '\n')
                    r+="\n"+tabs(indent+tempIndents);
                indent += c=='{'?1:-1;
                if(c=='}') r = r.substring(0,r.length()-1);
                r+=c+"\n"+tabs(indent+tempIndents);
                continue;
            }

            else if(c == '"'){
                doubleQuotes = true;
                i--;
                continue;
            }
            else if(c == '\''){
                //System.out.println("Start Single Quotes");
                singleQuotes = true;
            }


            else if(c=='(' && !header){
                if(	str.substring(i-3,i).matches("[ ;{}]if") ||
                        str.substring(i-4,i).matches("[ ;{}]for") ||
                        str.substring(i-6,i).matches("[ ;{}]while") ||
                        str.substring(i-6,i).matches("[ ;{}]switch")){
                    header = true;
                    headerPars = 1;
                }
            }
            else if(c=='o'){
                try{
                    if(str.substring(i-2,i+2).matches("[ ;]do[ {]")){
                        if(str.charAt(i+1)=='{'){
                            r+="o\n"+tabs(indent+tempIndents)+"{\n"+tabs(++indent+tempIndents);
                            i++;
                        }
                        else{
                            lookForBracket = true;
                            r+=c+"\n"+tabs(++indent+tempIndents);
                        }
                        continue;
                    }
                }
                catch(IndexOutOfBoundsException e){}
            }

            else if(c=='e'){
                try{
                    if(str.substring(i-3,i+2).equals("else ") && !str.substring(i+2,i+4).matches("if")){
                        r+=c+"\n"+tabs(++indent+tempIndents);
                        i++;
                        lookForBracket = true;
                        continue;
                    }
                }
                catch(IndexOutOfBoundsException e){}
            }



            else if(c==';' && !header){
                if(tempIndents > 0)tempIndents = 0;
                r+=c+"\n"+tabs(indent+tempIndents);
                continue;
            }
            r+=c;
        }

        return r;
    }

    private static String tabs(int in){
        String r = "";
        for(int i = 0;i<in;i++) r+="\t";
        return r;
    }

}

