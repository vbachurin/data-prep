package org.talend.recipeProcessor;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class PureJava implements RecipeProcessor {

    private class MyClass {

        int nbLines = 0;

        StringBuilder lines = new StringBuilder();
    }

    public void upperCase(File source, String column) throws IOException {
        File target = File.createTempFile("java-", "");
        MyClass processedLines = Files.readLines(source, Charsets.UTF_8, new LineProcessor<MyClass>() {

            MyClass toReturn = new MyClass();

            public boolean processLine(String line) throws IOException {
                toReturn.nbLines++;
                toReturn.lines.append(line.toUpperCase() + "\n");
                return true;
            }

            public MyClass getResult() {
                return toReturn;
            }
        });
        Files.write(processedLines.lines.toString(), target, Charsets.UTF_8);
    }

}
