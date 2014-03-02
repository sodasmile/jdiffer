package com.sodasmile.differ;

import org.junit.Ignore;
import org.junit.Test;

public class DifferTest {

    @Test
    public void runDiffWithNoArguments() {
        Main.main(new String[0]);
    }

    @Test
    public void name() {
        Main.main(new String[]{"src/test/resources", "."});
    }

    @Test
    @Ignore
    public void name2() {
        Main.main(new String[] {"/Volumes/Duffers/_Usortert/cvss", "."});
    }
}
