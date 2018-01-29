package com.nio.demo.BIO.bytes.stream;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class PrintStream {

    @Test
    public void test() throws IOException {
        File file = new File(this.getClass().getClassLoader().getResource("target.txt").getPath());
        java.io.PrintStream printStream = new java.io.PrintStream(file);
        printStream.println("你好,java Blocking I/O! print stream");
        printStream.close();
    }
}
