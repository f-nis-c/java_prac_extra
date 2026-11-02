import java.util.*;
class prnex extends Exception {
    prnex(String m){ super(m); }
    static void chk(String s) throws prnex {
        if(!s.matches("\\d+")) throw new prnex("PRN not digits");
        if(s.length()!=10) throw new prnex("PRN not 10 digits");
        long p=Long.parseLong(s);
        if(p<1000000000L||p>3000000000L) throw new prnex("PRN out of range");
    }
}
class nmex extends Exception {
    nmex(String m){ super(m); }
    static void chk(String[] nm) throws nmex {
        String s=nm[0].trim();
        if(s.isEmpty()) throw new nmex("Name empty");
        if(!s.matches("[a-zA-Z\\s]+")) throw new nmex("Name has symbols");
        String[] p=s.split("\\s+");
        if(p.length<2) throw new nmex("Name needs surname/spaces");
        boolean bad=false;
        for(int i=0;i<p.length;i++){
            if(!Character.isUpperCase(p[i].charAt(0))) bad=true;
            p[i]=Character.toUpperCase(p[i].charAt(0))+(p[i].length()>1?p[i].substring(1):"");
        }
        nm[0]=String.join(" ",p);
        if(bad) throw new nmex("Name not capitalized");
    }
}
class phex extends Exception {
    phex(String m){ super(m); }
    static void chk(String s) throws phex {
        if(s.startsWith("+91")) s=s.substring(3);
        if(!s.matches("\\d+")) throw new phex("Phone not digits");
        if(s.length()!=10) throw new phex("Phone not 10 digits");
        if(s.charAt(0)<'6') throw new phex("Phone must start 6-9");
    }
}
public class E8n9_Xtra{
    public static void main(String[] a){
        Scanner sc=new Scanner(System.in);
        int x=0;
        while (x!=1){
        try{
            System.out.print("Enter PRN: ");
            String prn=sc.nextLine().trim();
            prnex.chk(prn);
            System.out.print("Enter Name: ");
            String[] nm={sc.nextLine()};
            try{ nmex.chk(nm); }
            catch(nmex e){ System.out.println(e.getMessage()); }
            System.out.print("Enter Phone: ");
            String ph=sc.nextLine().trim();
            phex.chk(ph);
            if(ph.startsWith("+91")) ph=ph.substring(3);
            System.out.println("PRN: "+prn);
            System.out.println("Name: "+nm[0]);
            System.out.println("Phone: +91"+ph);
            break;
        }
        catch(prnex e){ System.out.println(e.getMessage()); }
        catch(phex e){ System.out.println(e.getMessage()); }
        catch(Exception e){ System.out.println("Bad input"); }
        System.out.println("Retry?\n1:NO 0:YES");
        x=sc.nextInt();
        sc.nextLine();
        }
    }
}