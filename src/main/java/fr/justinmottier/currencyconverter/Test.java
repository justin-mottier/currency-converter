package fr.justinmottier.currencyconverter;

import java.util.ArrayList;

public class Test {

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        int middle = list.size() / 2;
        System.out.println(middle);
        System.out.println(list.subList(0, middle));
        System.out.println(list.subList(middle, list.size()));
    }
}
