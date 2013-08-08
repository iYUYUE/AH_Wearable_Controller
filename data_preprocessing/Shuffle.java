
/*************************************************************************
 *  Compilation:  javac Shuffle.java
 *  Execution:    java Shuffle N < california-gov.txt
 *  Dependencies: StdIn.java
 *  
 *  Reads in N lines of text, shuffles them, and print them in random order.
 *  Uses Knuth's shuffling shuffle.
 *
 *  The file california-gov.txt contains a list of the 135
 *  candidates in the October 7, 2003 California governor's runoff
 *  election. The file cards.txt contains a list of 52 playing cards.
 *
 *
 *  % java Shuffle 5 < california-gov.txt
 *  Iris Adam
 *  Douglas Anderson
 *  Alex-St. James
 *  Angelyne
 *  Brooke Adams
 *
 *  % java Shuffle 5 < cards.txt
 *  Four of Clubs
 *  Six of Clubs
 *  Three of Clubs
 *  Deuce of Clubs
 *  Five of Clubs
 *
 *
 *************************************************************************/

public class Shuffle { 

    // swaps array elements i and j
    public static void exch(String[] a, int i, int j) {
        String swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // take as input an array of strings and rearrange them in random order
    public static void shuffle(String[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + (int) (Math.random() * (N-i));   // between i and N-1
            exch(a, i, r);
        }
    }

    // take as input an array of strings and print them out to standard output
    public static void show(String[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }


    public static void main(String[] args) { 
        int N = Integer.parseInt(args[0]);
        String[] a = new String[N];

        // read in data
        for (int i = 0; i < N; i++) {
            a[i] = StdIn.readLine();
        }

        // shuffle array and print permutation
        shuffle(a);
        show(a);

        System.out.println();

        // do it again
        shuffle(a);
        show(a);

    }
}
