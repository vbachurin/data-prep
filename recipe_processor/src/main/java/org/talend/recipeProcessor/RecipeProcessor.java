package org.talend.recipeProcessor;

import java.io.File;
import java.io.IOException;

public interface RecipeProcessor {

    public void upperCase(File source, String column) throws IOException;

}
