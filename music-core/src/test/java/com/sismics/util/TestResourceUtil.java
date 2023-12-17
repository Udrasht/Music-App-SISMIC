package com.sismics.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test of the resource utils.
 *
 * @author jtremeaux 
 */
public class TestResourceUtil {

    @Test
    public void listFilesTest() throws Exception {
        List<String> fileList = ResourceUtil.list(Test.class, "/junit/framework");
        Assert.assertTrue(fileList.contains("Test.class"));

        fileList = ResourceUtil.list(Test.class, "/junit/framework/");
        Assert.assertTrue(fileList.contains("Test.class"));

        fileList = ResourceUtil.list(Test.class, "junit/framework/");
        Assert.assertTrue(fileList.contains("Test.class"));

        fileList = ResourceUtil.list(Test.class, "junit/framework/");
        Assert.assertTrue(fileList.contains("Test.class"));
    }
}
