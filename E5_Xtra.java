import java.time.LocalDate;
import java.util.*;
class disc {
    String desc;
    disc(String d) { desc = d; }
    double app(double tot) { return tot; }
}
class flat extends disc {
    double off;
    flat(String d, double o) { super(d); off = o; }
    double app(double tot) { return Math.max(0, tot - off); }
}
class perc extends disc {
    double pct;
    perc(String d, double p) { super(d); pct = p; }
    double app(double tot) { return tot * (1 - pct / 100); }
}
class coup extends perc {
    LocalDate exp;
    coup(String d, double p, LocalDate exp) {
        super(d, p);
        this.exp = exp;
    }
    double app(double tot) {
        if (LocalDate.now().isAfter(exp)) { return tot; }
        return super.app(tot);
    }
}
public class E5_Xtra{
    public static void main(String[] args) {
        double tot = 200;
        List<disc> list = List.of(
            new flat("flat", 20),
            new perc("diwali sale", 15),
            new coup("summer", 25, LocalDate.now().plusDays(7)),
            new coup("2023", 50, LocalDate.now().minusDays(10))
        );
        for (disc d : list) {
            double res = d.app(tot);
            System.out.printf("%s \n%.2f to %.2f Rs. (Saved %.2f Rs.)%n", d.desc, tot, res, tot - res);
            if (tot-res==0){System.out.println("Expired"); } 
        }
    }
}