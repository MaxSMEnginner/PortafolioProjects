import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {
        Main main = new Main();

        System.out.println(main.palindromo("Ana"));

    }

    public ArrayList<Long> fibonacci(int n){
        ArrayList<Long> fib= new ArrayList<>();
        fib.add(0L);
        fib.add(1L);
        if (n<= 0){
            ArrayList<Long> subfib= new ArrayList<>();
            return subfib;
        }
        else if (n == 1){
            ArrayList<Long> subfib= new ArrayList<>();
            subfib.add(fib.get(0));
            return subfib;
        }


        for (int i=2;i < n; i++){
            fib.add(fib.get(i-1)+fib.get(i-2));
        }
        return fib;
    }

    public boolean palindromo_dificil(String oracion){
        oracion = oracion.toLowerCase().replace(" ","");
        char[] oracionarray=oracion.toCharArray();

        StringBuilder alreves=new StringBuilder("");
        for(int i=1;i < oracion.length()+1;i++){
            alreves.append(oracionarray[oracion.length()-i]);
        }

        if (oracion.equals(alreves.toString())){
            return true;
        }else{
            return false;
        }


    }

    public boolean palindromo(String oracion){
        StringBuilder frase= new StringBuilder(oracion.toLowerCase().replace(" ",""));
        if (frase.toString().equals(frase.reverse().toString())){
            return true;
        }else{
            return false;
        }


    }
}