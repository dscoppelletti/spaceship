package it.scoppelletti.spaceship.gradle.java;

import org.junit.Assert;
import org.junit.Test;

public class StringExtTest {

    @Test
    public void toCamelCase() {
        Assert.assertEquals("null", StringExt.EMPTY,
                StringExt.toCamelCase(null));
        Assert.assertEquals("empty", StringExt.EMPTY,
                StringExt.toCamelCase(StringExt.EMPTY));
        Assert.assertEquals("common", "spaceshipGradleJava",
                StringExt.toCamelCase("spaceship-gradle-java"));
        Assert.assertEquals("complex", "spaceshipGradleJava",
                StringExt.toCamelCase("-SpAcEsHiP--gRaDlE---jAvA-"));
    }
}
