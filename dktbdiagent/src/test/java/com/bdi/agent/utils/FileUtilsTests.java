package com.bdi.agent.utils;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTests {
    @Test
    public void testGetAllFileNamesWithoutExtension() {
        List<String> names = FileUtils.removeFileExtensions(List.of("test.file", "test2.file"));
        assertThat(names).containsExactlyInAnyOrderElementsOf(List.of("test", "test2"));
    }

    @Test
    public void testGetAllFileNamesWithoutExtensionLeaveOutEmptyString() {
        List<String> names = FileUtils.removeFileExtensions(List.of("test.file", "test2.file", ""));
        assertThat(names).containsExactlyInAnyOrderElementsOf(List.of("test", "test2"));
    }

    @Test
    public void testGetAllFileNamesWithoutExtensionKeepFileWithoutExtension() {
        List<String> names = FileUtils.removeFileExtensions(List.of("test.file", "test2.file", "test3"));
        assertThat(names).containsExactlyInAnyOrderElementsOf(List.of("test", "test2", "test3"));
    }
}
