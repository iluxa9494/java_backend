package ru.skillbox.socialnetwork.app;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceUniquenessTest {

    @Test
    void masterChangelogShouldBeUniqueOnClasspath() throws IOException {
        List<URL> resources = Collections.list(
                Thread.currentThread().getContextClassLoader()
                        .getResources("db/changelog/db.changelog-master.yaml")
        );
        assertEquals(1, resources.size(),
                "Expected exactly one db/changelog/db.changelog-master.yaml on classpath, got: " + resources);
    }
}
