package io.ran.testclasses;

public class ClassWithStaticMembers {
    private static int somethingStatic = 10;

    public static int getSomethingStatic() {
        return somethingStatic;
    }

    public static void setSomethingStatic(int i) {
        ClassWithStaticMembers.somethingStatic = i;
    }

    public static void doSomethingStatic() {
        System.out.print("do: "+somethingStatic);
    }
}
